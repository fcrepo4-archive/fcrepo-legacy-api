
package org.fcrepo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.fcrepo.jaxb.responses.access.DescribeRepository;
import org.fcrepo.services.ObjectService;
import org.fcrepo.session.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.jcr.api.Repository;

public class FedoraRepositoryTest {

    FedoraRepository testFedoraRepo;

    Repository mockRepo;

    Session mockSession;

    ObjectService mockObjects;

    @Before
    public void setUp() throws LoginException, RepositoryException {
        testFedoraRepo = new FedoraRepository();
        testFedoraRepo.setUriInfo(TestHelpers.getUriInfoImpl());
        mockObjects = mock(ObjectService.class);
        testFedoraRepo.setObjectService(mockObjects);
        mockRepo = mock(Repository.class);
        mockSession = mock(Session.class);
        final SessionFactory mockSessions = mock(SessionFactory.class);
        when(mockSessions.getSession()).thenReturn(mockSession);
        when(
                mockSessions.getSession(any(SecurityContext.class),
                        any(HttpServletRequest.class))).thenReturn(mockSession);
        testFedoraRepo.setSessionFactory(mockSessions);
        when(mockRepo.getDescriptorKeys()).thenReturn(new String[0]);
        when(mockObjects.getRepositoryNamespaces(mockSession)).thenReturn(
                new HashMap<String, String>(0));
        final NodeTypeIterator mockNT = mock(NodeTypeIterator.class);
        when(mockObjects.getAllNodeTypes(mockSession)).thenReturn(mockNT);
        testFedoraRepo.setRepository(mockRepo);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testDescribeModeshape() throws RepositoryException, IOException {
        final Response actual = testFedoraRepo.describeModeshape();
        assertNotNull(actual);
        assertEquals(Status.OK.getStatusCode(), actual.getStatus());
    }

    @Test
    public void testDescribe() throws LoginException, RepositoryException {
        final DescribeRepository actual = testFedoraRepo.describe();
        assertNotNull(actual);
        assertEquals("4.0-modeshape-candidate", actual.getRepositoryVersion());
    }

    @Test
    public void testDescribeHtml() throws LoginException, RepositoryException {
        final String actual = testFedoraRepo.describeHtml();
        assertNotNull(actual);
        assertEquals(true, actual.contains("4.0-modeshape-candidate"));
    }
}