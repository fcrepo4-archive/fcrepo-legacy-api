
package org.fcrepo.legacy;

import static org.fcrepo.legacy.LegacyPathHelpers.OBJECT_PATH;
import static org.fcrepo.legacy.LegacyPathHelpers.getObjectPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Principal;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.fcrepo.FedoraObject;
import org.fcrepo.identifiers.UUIDPidMinter;
import org.fcrepo.jaxb.responses.access.ObjectProfile;
import org.fcrepo.services.NodeService;
import org.fcrepo.services.ObjectService;
import org.fcrepo.session.SessionFactory;
import org.fcrepo.utils.FedoraJcrTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.jcr.api.Repository;

public class FedoraObjectsTest {

    FedoraObjects testObj;

    ObjectService mockObjects;

    Repository mockRepo;

    Session mockSession;

    private NodeService mockNodes;

    @Before
    public void setUp() throws LoginException, RepositoryException, NoSuchFieldException {
        mockObjects = mock(ObjectService.class);

        mockNodes = mock(NodeService.class);
        testObj = new FedoraObjects();
        mockSession = TestHelpers.getSessionMock();
        TestHelpers.setField(testObj, "objectService", mockObjects);
        TestHelpers.setField(testObj, "nodeService", mockNodes);
        TestHelpers.setField(testObj, "uriInfo", TestHelpers.getUriInfoImpl());
        TestHelpers.setField(testObj, "pidMinter", new UUIDPidMinter());
        TestHelpers.setField(testObj, "session", mockSession);
        mockRepo = mock(Repository.class);

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetObjects() throws RepositoryException {
        final Response actual = testObj.getObjects();
        assertNotNull(actual);
        assertEquals(Status.OK.getStatusCode(), actual.getStatus());
        verify(mockNodes).getObjectNames(mockSession, OBJECT_PATH);
        verify(mockSession, never()).save();
    }

    @Test
    public void testIngestAndMint() throws RepositoryException {
        final Response actual = testObj.ingestAndMint();
        assertNotNull(actual);
        assertEquals(Status.CREATED.getStatusCode(), actual.getStatus());
        verify(mockSession).save();
    }

    @Test
    public void testModify() throws RepositoryException {
        final String pid = "testObject";
        final Response actual = testObj.modify(pid);
        assertNotNull(actual);
        assertEquals(Status.CREATED.getStatusCode(), actual.getStatus());
        // this verify will fail when modify is actually implemented, thus encouraging the unit test to be updated appropriately.
        verifyNoMoreInteractions(mockObjects);
        verify(mockSession).save();
    }

    @Test
    public void testIngest() throws RepositoryException {
        final String pid = "testObject";
        final Response actual = testObj.ingest(pid, null);
        assertNotNull(actual);
        assertEquals(Status.CREATED.getStatusCode(), actual.getStatus());
        assertTrue(actual.getEntity().toString().endsWith(pid));
        verify(mockObjects).createObject(mockSession, getObjectPath(pid));
        verify(mockSession).save();
    }

    @Test
    public void testGetObject() throws RepositoryException, IOException {
        final String pid = "testObject";
        final FedoraObject mockObj = mock(FedoraObject.class);
        Node mockNode = mock(Node.class);
        when(mockObjects.getObject(mockSession, getObjectPath(pid))).thenReturn(mockObj);
        when(mockObj.getNode()).thenReturn(mockNode);
        when(mockNode.getProperty(FedoraJcrTypes.JCR_CREATEDBY)).thenReturn(mock(Property.class));
        final ObjectProfile actual = testObj.getObject(pid);
        assertNotNull(actual);
        assertEquals(pid, actual.pid);
        verify(mockObjects).getObject(mockSession, getObjectPath(pid));
        verify(mockSession, never()).save();
    }

    @Test
    public void testDeleteObject() throws RepositoryException {
        final String pid = "testObject";
        final Response actual = testObj.deleteObject(pid);
        assertNotNull(actual);
        assertEquals(Status.NO_CONTENT.getStatusCode(), actual.getStatus());
        verify(mockNodes).deleteObject(mockSession, getObjectPath(pid));
        verify(mockSession).save();
    }
}
