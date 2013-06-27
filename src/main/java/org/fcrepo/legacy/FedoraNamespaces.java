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

import static com.google.common.collect.ImmutableSet.builder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static javax.ws.rs.core.Response.created;

import java.io.IOException;
import java.net.URI;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.fcrepo.AbstractResource;
import org.fcrepo.jaxb.responses.management.NamespaceListing;
import org.fcrepo.jaxb.responses.management.NamespaceListing.Namespace;
import org.fcrepo.session.InjectedSession;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet.Builder;
import com.codahale.metrics.annotation.Timed;

/**
 * The purpose of this class is to allow clients to manipulate the JCR
 * namespaces in play in a repository. This is necessary to allow the use of
 * traditional Fedora namespaced PIDs. Unlike Fedora Classic, a JCR requires
 * that namespaces be registered before use. The catalog of namespaces is very
 * simple, just a set of prefix-URI pairs.
 * 
 * @author ajs6f
 */
@Component("fedoraLegacyNamespaces")
@Scope("prototype")
@Path("/v3/namespaces")
public class FedoraNamespaces extends AbstractResource {

    @InjectedSession
    protected Session session;

    /**
     * Creates a new namespace in the JCR for use in identifing objects.
     * 
     * @param prefix Prefix to use
     * @param uri Uri to use
     * @return 201
     * @throws RepositoryException
     */
    @POST
    @Timed
    @Path("/{prefix}")
    public Response registerObjectNamespace(@PathParam("prefix")
    final String prefix, final String uri) throws RepositoryException {

        try {
            final NamespaceRegistry r =
                    session.getWorkspace().getNamespaceRegistry();
            r.registerNamespace(prefix, uri);
        } finally {
            session.logout();
        }
        return created(uriInfo.getRequestUri()).build();
    }

    /**
     * Register multiple object namespaces.
     * 
     * @param nses A set of namespaces in JAXB-specified format.
     * @return 201
     * @throws RepositoryException
     */
    @POST
    @Timed
    @Consumes({TEXT_XML, APPLICATION_JSON})
    public Response registerObjectNamespaces(final NamespaceListing nses)
        throws RepositoryException {

        try {
            final NamespaceRegistry r =
                    session.getWorkspace().getNamespaceRegistry();
            for (final Namespace ns : nses.namespaces) {
                r.registerNamespace(ns.prefix, ns.uri.toString());
            }
        } finally {
            session.logout();
        }
        return created(uriInfo.getRequestUri()).build();
    }

    /**
     * Retrieve a namespace URI from a prefix.
     * 
     * @param prefix The prefix to search.
     * @return A JAXB-specified format Namespace.
     * @throws RepositoryException
     */
    @GET
    @Path("/{prefix}")
    @Timed
    @Produces(APPLICATION_JSON)
    public Namespace retrieveObjectNamespace(@PathParam("ns")
    final String prefix) throws RepositoryException {

        final NamespaceRegistry r =
                session.getWorkspace().getNamespaceRegistry();

        try {
            final Namespace ns =
                    new Namespace(prefix, URI.create(r.getURI(prefix)));

            return ns;
        } finally {
            session.logout();
        }
    }

    /**
     * @return
     * @throws RepositoryException
     * @throws IOException
     */
    @GET
    @Timed
    @Produces({TEXT_XML, APPLICATION_JSON})
    public NamespaceListing getNamespaces() throws RepositoryException,
        IOException {
        final Builder<Namespace> b = builder();
        try {
            final NamespaceRegistry r =
                    session.getWorkspace().getNamespaceRegistry();

            for (final String prefix : r.getPrefixes()) {
                b.add(new Namespace(prefix, URI.create(r.getURI(prefix))));
            }
        } finally {
            session.logout();
        }
        return new NamespaceListing(b.build());
    }

    public void setSession(final Session session) {
        this.session = session;
    }

}
