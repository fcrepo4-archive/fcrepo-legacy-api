
package org.fcrepo.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.jcr.LoginException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fcrepo.jaxb.responses.access.DescribeRepository;
import org.fcrepo.services.ObjectService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

        mockSession = TestHelpers.getSessionMock();
        testFedoraRepo.setSession(mockSession);

        when(mockRepo.getDescriptorKeys()).thenReturn(new String[0]);
        final NodeTypeIterator mockNT = mock(NodeTypeIterator.class);
        when(mockObjects.getAllNodeTypes(mockSession)).thenReturn(mockNT);
        testFedoraRepo.setRepository(mockRepo);
    }

    @After
    public void tearDown() {

    }

    @Test
    @Ignore
    public void testDescribeModeshape() throws RepositoryException, IOException {
        Workspace mockWorkspace = mock(Workspace.class);
        NamespaceRegistry mockNsReg = mock(NamespaceRegistry.class);
        when(mockNsReg.getPrefixes()).thenReturn(new String[] {});
        when(mockWorkspace.getNamespaceRegistry()).thenReturn(mockNsReg);
        when(mockSession.getWorkspace()).thenReturn(mockWorkspace);
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