
package org.fcrepo.legacy;

import static org.fcrepo.legacy.LegacyPathHelpers.getDatastreamsPath;
import static org.fcrepo.legacy.LegacyPathHelpers.getObjectPath;
import static org.fcrepo.legacy.TestHelpers.getUriInfoImpl;
import static org.fcrepo.test.util.TestHelpers.mockDatastream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.tika.io.IOUtils;
import org.fcrepo.Datastream;
import org.fcrepo.FedoraResource;
import org.fcrepo.exception.InvalidChecksumException;
import org.fcrepo.jaxb.responses.access.ObjectDatastreams;
import org.fcrepo.jaxb.responses.management.DatastreamHistory;
import org.fcrepo.jaxb.responses.management.DatastreamProfile;
import org.fcrepo.services.DatastreamService;
import org.fcrepo.services.LowLevelStorageService;
import org.fcrepo.services.NodeService;
import org.fcrepo.session.SessionFactory;
import org.fcrepo.utils.FedoraJcrTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.jcr.api.Repository;

import com.sun.jersey.multipart.MultiPart;

public class FedoraDatastreamsTest {

    FedoraDatastreams testObj;

    DatastreamService mockDatastreams;

    NodeService mockNodes;

    LowLevelStorageService mockLow;

    Repository mockRepo;

    Session mockSession;

    SecurityContext mockSecurityContext;

    HttpServletRequest mockServletRequest;

    Principal mockPrincipal;

    String mockUser = "testuser";

    @Before
    public void setUp() throws LoginException, RepositoryException {
        mockSecurityContext = mock(SecurityContext.class);
        mockServletRequest = mock(HttpServletRequest.class);
        mockPrincipal = mock(Principal.class);
        //Function<HttpServletRequest, Session> mockFunction = mock(Function.class);
        mockDatastreams = mock(DatastreamService.class);
        mockLow = mock(LowLevelStorageService.class);
        mockNodes = mock(NodeService.class);

        testObj = new FedoraDatastreams();
        testObj.setDatastreamService(mockDatastreams);
        testObj.setNodeService(mockNodes);
        testObj.setSecurityContext(mockSecurityContext);
        testObj.setHttpServletRequest(mockServletRequest);
        testObj.setLlStoreService(mockLow);
        //mockRepo = mock(Repository.class);
        final SessionFactory mockSessions = mock(SessionFactory.class);
        testObj.setSessionFactory(mockSessions);
        mockSession = mock(Session.class);
        when(
                mockSessions.getSession(any(SecurityContext.class),
                        any(HttpServletRequest.class))).thenReturn(mockSession);
        when(mockSession.getUserID()).thenReturn(mockUser);
        when(mockSecurityContext.getUserPrincipal()).thenReturn(mockPrincipal);
        when(mockPrincipal.getName()).thenReturn(mockUser);

        testObj.setUriInfo(getUriInfoImpl());
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetDatastreams() throws RepositoryException, IOException {
        final String pid = "FedoraDatastreamsTest1";
        final FedoraResource mockObject = mock(FedoraResource.class);
        final Node mockNode = mock(Node.class);
        when(mockObject.getNode()).thenReturn(mockNode);
        final Node mockDatastreamNode = mock(Node.class);
        final NodeType mockNodeType = mock(NodeType.class);
        when(mockNodeType.getName()).thenReturn(FedoraJcrTypes.FEDORA_DATASTREAM);
        when(mockDatastreamNode.getMixinNodeTypes()).thenReturn(new NodeType[] { mockNodeType });

        final NodeIterator mockIterator = mock(NodeIterator.class);
        when(mockIterator.hasNext()).thenReturn(true, false);
        when(mockIterator.next()).thenReturn(mockDatastreamNode);
        when(mockNode.getNodes()).thenReturn(mockIterator);
        when(mockNodes.getObject(mockSession, getObjectPath(pid))).thenReturn(mockObject);
        final ObjectDatastreams actual = testObj.getDatastreams(pid);
        verify(mockSession, never()).save();
        assertEquals(1, actual.datastreams.size());
    }

    @Test
    public void testModifyDatastreams() throws RepositoryException,
            IOException, InvalidChecksumException {
        final String pid = "FedoraDatastreamsTest1";
        final String dsId1 = "testDs1";
        final String dsId2 = "testDs2";
        final HashMap<String, String> atts = new HashMap<String, String>(2);
        atts.put(dsId1, "asdf");
        atts.put(dsId2, "sdfg");
        final MultiPart multipart = TestHelpers.getStringsAsMultipart(atts);
        final Response actual =
                testObj.modifyDatastreams(pid, Arrays.asList(new String[] {
                        dsId1, dsId2}), multipart);
        assertEquals(Status.CREATED.getStatusCode(), actual.getStatus());
        verify(mockDatastreams).createDatastreamNode(any(Session.class),
                eq(getDatastreamsPath(pid, dsId1)), anyString(),
                any(InputStream.class));
        verify(mockDatastreams).createDatastreamNode(any(Session.class),
                eq(getDatastreamsPath(pid, dsId2)), anyString(),
                any(InputStream.class));
        verify(mockSession).save();
    }

    @Test
    public void testDeleteDatastreams() throws RepositoryException, IOException {
        final String pid = "FedoraDatastreamsTest1";
        final List<String> dsidList =
                Arrays.asList(new String[] {"ds1", "ds2"});
        final Response actual = testObj.deleteDatastreams(pid, dsidList);
        assertEquals(Status.NO_CONTENT.getStatusCode(), actual.getStatus());
        verify(mockNodes).deleteObject(mockSession, getDatastreamsPath(pid, "ds1"));
        verify(mockNodes).deleteObject(mockSession, getDatastreamsPath(pid, "ds2"));
        verify(mockSession).save();
    }

    @Test
    public void testGetDatastreamsContents() throws RepositoryException,
            IOException {
        final String pid = "FedoraDatastreamsTest1";
        final String dsId = "testDS";
        final String dsContent = "asdf";
        final Datastream mockDs = mockDatastream(pid, dsId, dsContent);
        when(mockDatastreams.getDatastream(mockSession, getDatastreamsPath(pid, dsId))).thenReturn(mockDs);

        final Response resp =
                testObj.getDatastreamsContents(pid, Arrays
                        .asList(new String[] {dsId}));
        final MultiPart multipart = (MultiPart) resp.getEntity();

        verify(mockDatastreams).getDatastream(mockSession, getDatastreamsPath(pid, dsId));
        verify(mockDs).getContent();
        verify(mockSession, never()).save();
        assertEquals(1, multipart.getBodyParts().size());
        final InputStream actualContent =
                (InputStream) multipart.getBodyParts().get(0).getEntity();
        assertEquals("asdf", IOUtils.toString(actualContent, "UTF-8"));
    }

    @Test
    public void testAddDatastream() throws RepositoryException, IOException,
            InvalidChecksumException {
        final String pid = "FedoraDatastreamsTest1";
        final String dsId = "testDS";
        final String dsContent = "asdf";
        final String dsPath = getDatastreamsPath(pid, dsId);
        final InputStream dsContentStream = IOUtils.toInputStream(dsContent);
        final Response actual =
                testObj.addDatastream(pid, null, null, dsId, null,
                        dsContentStream);
        assertEquals(Status.CREATED.getStatusCode(), actual.getStatus());
        verify(mockDatastreams).createDatastreamNode(any(Session.class),
                eq(dsPath), anyString(), any(InputStream.class), anyString(),
                anyString());
        verify(mockSession).save();
    }

    @Test
    public void testModifyDatastream() throws RepositoryException, IOException,
            InvalidChecksumException {
        final String pid = "FedoraDatastreamsTest1";
        final String dsId = "testDS";
        final String dsContent = "asdf";
        final String dsPath = getDatastreamsPath(pid, dsId);
        final InputStream dsContentStream = IOUtils.toInputStream(dsContent);
        final Response actual =
                testObj.modifyDatastream(pid, dsId, null, dsContentStream);
        assertEquals(Status.CREATED.getStatusCode(), actual.getStatus());
        verify(mockDatastreams).createDatastreamNode(any(Session.class),
                eq(dsPath), anyString(), any(InputStream.class));
        verify(mockSession).save();
    }

    @Test
    public void testGetDatastream() throws RepositoryException, IOException {
        final String pid = "FedoraDatastreamsTest1";
        final String dsId = "testDS";
        final Datastream mockDs = mockDatastream(pid, dsId, null);
        when(mockDatastreams.getDatastream(mockSession, getDatastreamsPath(pid, dsId))).thenReturn(mockDs);
        final DatastreamProfile actual = testObj.getDatastream(pid, dsId);
        assertNotNull(actual);
        verify(mockDatastreams).getDatastream(mockSession, getDatastreamsPath(pid, dsId));
        verify(mockSession, never()).save();
    }

    @Test
    public void testGetDatastreamContent() throws RepositoryException,
            IOException {
        final String pid = "FedoraDatastreamsTest1";
        final String dsId = "testDS";
        final String dsContent = "asdf";
        final Datastream mockDs = mockDatastream(pid, dsId, dsContent);
        when(mockDatastreams.getDatastream(mockSession, getDatastreamsPath(pid, dsId))).thenReturn(mockDs);
        final Request mockRequest = mock(Request.class);
        final Response actual =
                testObj.getDatastreamContent(pid, dsId, mockRequest);
        verify(mockDatastreams).getDatastream(mockSession, getDatastreamsPath(pid, dsId));
        verify(mockDs).getContent();
        verify(mockSession, never()).save();
        final String actualContent =
                IOUtils.toString((InputStream) actual.getEntity());
        assertEquals("asdf", actualContent);
    }

    @Test
    public void testGetDatastreamHistory() throws RepositoryException,
            IOException {
        final String pid = "FedoraDatastreamsTest1";
        final String dsId = "testDS";
        final Datastream mockDs = mockDatastream(pid, dsId, null);
        when(mockDatastreams.getDatastream(mockSession, getDatastreamsPath(pid, dsId))).thenReturn(mockDs);
        final DatastreamHistory actual =
                testObj.getDatastreamHistory(pid, dsId);
        assertNotNull(actual);
        verify(mockDatastreams).getDatastream(mockSession, getDatastreamsPath(pid, dsId));
        verify(mockSession, never()).save();
    }

    @Test
    public void testDeleteDatastream() throws RepositoryException, IOException {
        final String pid = "FedoraDatastreamsTest1";
        final String dsId = "testDS";
        final Response actual = testObj.deleteDatastream(pid, dsId);
        assertNotNull(actual);
        assertEquals(Status.NO_CONTENT.getStatusCode(), actual.getStatus());
        verify(mockNodes).deleteObject(mockSession, getDatastreamsPath(pid, dsId));
        verify(mockSession).save();
    }
}