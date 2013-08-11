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
import static org.mockito.Mockito.when;

import org.fcrepo.jaxb.responses.management.NextPid;
import org.fcrepo.kernel.identifiers.PidMinter;
import org.fcrepo.legacy.FedoraIdentifiers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Function;

public class FedoraIdentifiersTest {

    @Mock
    private PidMinter mockPidMinter;

    @InjectMocks
    private final FedoraIdentifiers fi = new FedoraIdentifiers();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNextPid() {
        when(mockPidMinter.makePid()).thenReturn(
                new Function<Object, String>() {

                    @Override
                    public String apply(final Object input) {
                        return "asdf:123";
                    }
                });

        final NextPid np = fi.getNextPid(2);

        assertNotNull(np);

        for (final String pid : np.pids) {
            assertEquals("Wrong pid value!", "asdf:123", pid);
        }

    }
}
