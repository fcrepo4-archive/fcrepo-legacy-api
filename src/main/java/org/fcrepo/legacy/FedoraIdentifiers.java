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

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ContiguousSet.create;
import static com.google.common.collect.DiscreteDomain.integers;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Range.closed;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.fcrepo.AbstractResource;
import org.fcrepo.jaxb.responses.management.NextPid;
import org.fcrepo.session.InjectedSession;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;

/**
 * JAX-RS Resource offering PID creation.
 * 
 * @author ajs6f
 */
@Component("fedoraLegacyIdentifiers")
@Scope("prototype")
@Path("/v3/nextPID")
public class FedoraIdentifiers extends AbstractResource {

    @InjectedSession
    protected Session session;

    /**
     * @param numPids
     * @return HTTP 200 with block of PIDs
     * @throws RepositoryException
     * @throws IOException
     * @throws TemplateException
     */
    @POST
    @Timed
    @Produces({TEXT_XML, APPLICATION_JSON})
    public NextPid getNextPid(@QueryParam("numPids")
    @DefaultValue("1")
    final Integer numPids) {

        return new NextPid(copyOf(transform(create(closed(1, numPids),
                integers()), pidMinter.makePid())));

    }

    public void setSession(final Session session) {
        this.session = session;
    }

}
