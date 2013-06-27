/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.legacy;

import static org.fcrepo.legacy.LegacyPathHelpers.OBJECT_PATH;
import static org.fcrepo.legacy.LegacyPathHelpers.getObjectPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fcrepo.FedoraObject;
import org.fcrepo.identifiers.UUIDPidMinter;
import org.fcrepo.jaxb.responses.access.ObjectProfile;
import org.fcrepo.services.NodeService;
import org.fcrepo.services.ObjectService;
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
    public void setUp() throws LoginException, RepositoryException {
        mockObjects = mock(ObjectService.class);

        mockNodes = mock(NodeService.class);
        testObj = new FedoraObjects();
        testObj.setObjectService(mockObjects);
        testObj.setNodeService(mockNodes);
        mockRepo = mock(Repository.class);

        mockSession = TestHelpers.getSessionMock();
        testObj.setSession(mockSession);
        testObj.setUriInfo(TestHelpers.getUriInfoImpl());
        testObj.setPidMinter(new UUIDPidMinter());
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
        // this verify will fail when modify is actually implemented, thus
        // encouraging the unit test to be updated appropriately.
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
        when(mockObjects.getObject(mockSession, getObjectPath(pid)))
                .thenReturn(mockObj);
        when(mockObj.getNode()).thenReturn(mockNode);
        when(mockNode.getProperty(FedoraJcrTypes.JCR_CREATEDBY)).thenReturn(
                mock(Property.class));
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
