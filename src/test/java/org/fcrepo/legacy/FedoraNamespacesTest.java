
package org.fcrepo.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fcrepo.identifiers.UUIDPidMinter;
import org.fcrepo.jaxb.responses.management.NamespaceListing;
import org.fcrepo.jaxb.responses.management.NamespaceListing.Namespace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FedoraNamespacesTest {

    FedoraNamespaces testObj;

    Namespace mockNs;
    private Session mockSession;

    @Before
    public void setUp() throws LoginException, RepositoryException,
            URISyntaxException {
        mockNs = new Namespace();
        mockNs.prefix = TestHelpers.MOCK_PREFIX;
        mockNs.uri = new URI(TestHelpers.MOCK_URI_STRING);

        testObj = new FedoraNamespaces();
        testObj.setUriInfo(TestHelpers.getUriInfoImpl());


        mockSession = TestHelpers.getSessionMock();
        testObj.setSession(mockSession);

        testObj.setPidMinter(new UUIDPidMinter());
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testRegisterObjectNamespace() throws RepositoryException {
        final Response actual =
                testObj.registerObjectNamespace(TestHelpers.MOCK_PREFIX, TestHelpers.MOCK_URI_STRING);
        assertNotNull(actual);
        assertEquals(Status.CREATED.getStatusCode(), actual.getStatus());
    }

    @Test
    public void testRegisterObjectNamespaces() throws RepositoryException {
        final Set<Namespace> mockNses = new HashSet<Namespace>();
        mockNses.add(mockNs);
        final NamespaceListing nses = new NamespaceListing();
        nses.namespaces = mockNses;
        final Response actual = testObj.registerObjectNamespaces(nses);
        assertNotNull(actual);
        assertEquals(Status.CREATED.getStatusCode(), actual.getStatus());
    }

    @Test
    public void testRetrieveObjectNamespace() throws RepositoryException {
        testObj.registerObjectNamespace(TestHelpers.MOCK_PREFIX, TestHelpers.MOCK_URI_STRING);
        final Namespace actual = testObj.retrieveObjectNamespace(TestHelpers.MOCK_PREFIX);
        assertNotNull(actual);
        assertEquals(actual.uri, mockNs.uri);
    }

    @Test
    public void testGetNamespaces() throws RepositoryException, IOException {
        final NamespaceListing actual = testObj.getNamespaces();
        assertNotNull(actual);
    }
}
