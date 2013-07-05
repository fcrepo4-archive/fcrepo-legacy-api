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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
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

import org.fcrepo.identifiers.UUIDPidMinter;
import org.fcrepo.jaxb.responses.access.DescribeRepository;
import org.fcrepo.services.ObjectService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.modeshape.jcr.api.Repository;

public class FedoraRepositoryTest {

    FedoraRepository testObj;

    Repository mockRepo;

    Session mockSession;

    ObjectService mockObjects;

    @Before
    public void setUp() throws LoginException, RepositoryException, NoSuchFieldException {
        testObj = new FedoraRepository();

        TestHelpers.setField(testObj, "uriInfo", TestHelpers.getUriInfoImpl());
        TestHelpers.setField(testObj, "pidMinter", new UUIDPidMinter());

        mockObjects = mock(ObjectService.class);
        TestHelpers.setField(testObj, "objectService", mockObjects);
        mockRepo = mock(Repository.class);

        mockSession = TestHelpers.getSessionMock();
        TestHelpers.setField(testObj, "session", mockSession);

        when(mockRepo.getDescriptorKeys()).thenReturn(new String[0]);
        final NodeTypeIterator mockNT = mock(NodeTypeIterator.class);
        when(mockObjects.getAllNodeTypes(mockSession)).thenReturn(mockNT);
        TestHelpers.setField(testObj, "repo", mockRepo);
    }

    @After
    public void tearDown() {

    }

    @Test
    @Ignore
    public void testDescribeModeshape() throws RepositoryException, IOException {
        Workspace mockWorkspace = mock(Workspace.class);
        NamespaceRegistry mockNsReg = mock(NamespaceRegistry.class);
        when(mockNsReg.getPrefixes()).thenReturn(new String[] { });
        when(mockWorkspace.getNamespaceRegistry()).thenReturn(mockNsReg);
        when(mockSession.getWorkspace()).thenReturn(mockWorkspace);
        final Response actual = testObj.describeModeshape();
        assertNotNull(actual);
        assertEquals(Status.OK.getStatusCode(), actual.getStatus());
    }

    @Test
    public void testDescribe() throws LoginException, RepositoryException {
        final DescribeRepository actual = testObj.describe();
        assertNotNull(actual);
        assertEquals("4.0-modeshape-candidate", actual.getRepositoryVersion());
    }

    @Test
    public void testDescribeHtml() throws LoginException, RepositoryException {
        final String actual = testObj.describeHtml();
        assertNotNull(actual);
        assertEquals(true, actual.contains("4.0-modeshape-candidate"));
    }
}