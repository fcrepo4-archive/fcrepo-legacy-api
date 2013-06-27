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

package org.fcrepo.integration.api;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class FedoraIdentifiersIT extends AbstractResourceIT {

    @Test
    public void testGetNextPidResponds() throws Exception {
        final HttpPost method = new HttpPost(serverAddress + "nextPID");
        method.addHeader("Accepts", "text/xml");
        logger.debug("Executed testGetNextPidResponds()");
        assertEquals(HttpServletResponse.SC_OK, getStatus(method));
    }

    @Test
    public void testGetNextHasAPid() throws IOException {
        final HttpPost method =
                new HttpPost(serverAddress + "nextPID?numPids=1");
        method.addHeader("Accepts", "text/xml");
        final HttpResponse response = client.execute(method);
        logger.debug("Executed testGetNextHasAPid()");
        final String content = EntityUtils.toString(response.getEntity());
        logger.debug("Only to find:\n" + content);
        assertTrue("Didn't find a single dang PID!", compile("<pid>.*?</pid>",
                DOTALL).matcher(content).find());
    }

    @Test
    public void testGetNextHasTwoPids() throws IOException {
        final HttpPost method =
                new HttpPost(serverAddress + "nextPID?numPids=2");
        method.addHeader("Accepts", "text/xml");
        final HttpResponse response = client.execute(method);
        logger.debug("Executed testGetNextHasTwoPids()");
        final String content = EntityUtils.toString(response.getEntity());
        logger.debug("Only to find:\n" + response);
        assertTrue("Didn't find a two dang PIDs!", compile(
                "<pid>.*?</pid>.*?<pid>.*?</pid>", DOTALL).matcher(content)
                .find());
    }
}
