
package org.fcrepo.legacy;

import static com.hp.hpl.jena.sparql.util.FmtUtils.stringEsc;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;
import static org.fcrepo.jaxb.responses.access.ObjectProfile.ObjectStates.A;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.fcrepo.AbstractResource;
import org.fcrepo.FedoraObject;
import org.fcrepo.jaxb.responses.access.ObjectProfile;
import org.fcrepo.rdf.GraphSubjects;
import org.fcrepo.rdf.impl.DefaultGraphSubjects;
import org.fcrepo.session.InjectedSession;
import org.fcrepo.utils.FedoraJcrTypes;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;

@Component("fedoraLegacyObjects")
@Scope("prototype")
@Path("/v3/objects")
public class FedoraObjects extends AbstractResource {

    private static final Logger logger = getLogger(FedoraObjects.class);

    private static final GraphSubjects graphSubjects = new DefaultGraphSubjects();

    @InjectedSession
    protected Session session;
    /**
     * 
     * Provides a serialized list of JCR names for all objects in the repo.
     * 
     * @return 200
     * @throws RepositoryException
     */
    @GET
	@Timed
    public Response getObjects() throws RepositoryException {


		try {
        	return ok(nodeService.getObjectNames(session, LegacyPathHelpers.OBJECT_PATH).toString()).build();
		} finally {
			session.logout();
		}

    }

    /**
     * Creates a new object with a repo-chosen PID
     * 
     * @return 201
     * @throws RepositoryException
     */
    @POST
    @Path("/new")
	@Timed
    public Response ingestAndMint() throws RepositoryException {
        return ingest(pidMinter.mintPid(), "");
    }

    /**
     * Does nothing yet-- must be improved to handle the FCREPO3 PUT to /objects/{pid}
     * 
     * @param pid
     * @return 201
     * @throws RepositoryException
     */
    @PUT
    @Path("/{pid}")
	@Timed
    public Response modify(@PathParam("pid")
    final String pid) throws RepositoryException {
        try {
            // TODO do something with awful mess of fcrepo3 query params
            session.save();
            return created(uriInfo.getRequestUri()).build();
        } finally {
            session.logout();
        }
    }

    /**
     * Creates a new object.
     * 
     * @param pid
     * @return 201
     * @throws RepositoryException
     */
    @POST
    @Path("/{pid}")
	@Timed
    public Response ingest(@PathParam("pid")
    final String pid, @QueryParam("label")
    @DefaultValue("")
    final String label) throws RepositoryException {

        logger.debug("Attempting to ingest with pid: {}", pid);

        try {
            final FedoraObject result =
                    objectService.createObject(session, LegacyPathHelpers.getObjectPath(pid));

			if (label != null && !"".equals(label)) {

                result.updatePropertiesDataset("INSERT { <" + graphSubjects.getGraphSubject(result.getNode()) + "> <http://purl.org/dc/terms/title> \"" + stringEsc(label) + "\"} WHERE { }");
		    }

            session.save();
            logger.debug("Finished ingest with pid: {}", pid);
            return created(uriInfo.getRequestUri()).entity(pid).build();

        } finally {
            session.logout();
        }
    }

    /**
     * Returns an object profile.
     * 
     * @param pid
     * @return 200
     * @throws RepositoryException
     * @throws IOException
     */
    @GET
    @Path("/{pid}")
	@Timed
    @Produces({TEXT_XML, APPLICATION_JSON, TEXT_HTML})
    public ObjectProfile getObject(@PathParam("pid")
    final String pid) throws RepositoryException, IOException {


		try {
			final ObjectProfile objectProfile = new ObjectProfile();
			final FedoraObject obj = objectService.getObject(session, LegacyPathHelpers.getObjectPath(pid));
			objectProfile.pid = pid;

			if(obj.getNode().hasProperty("dc:title")) {
				objectProfile.objLabel = obj.getNode().getProperty("dc:title").getValues()[0].getString();
			}

			objectProfile.objOwnerId = obj.getNode().getProperty(FedoraJcrTypes.JCR_CREATEDBY).getString();
			objectProfile.objCreateDate = obj.getCreatedDate();
			objectProfile.objLastModDate = obj.getLastModifiedDate();
			objectProfile.objSize = obj.getSize();
			objectProfile.objItemIndexViewURL =
					uriInfo.getAbsolutePathBuilder().path("datastreams").build();
			objectProfile.objState = A;
			objectProfile.objModels = obj.getModels();
			return objectProfile;
		} finally {
			session.logout();
		}

    }

    /**
     * Deletes an object.
     * 
     * @param pid
     * @return
     * @throws RepositoryException
     */
    @DELETE
    @Path("/{pid}")
	@Timed
    public Response deleteObject(@PathParam("pid")
    final String pid) throws RepositoryException {
		try {
        	nodeService.deleteObject(session, LegacyPathHelpers.getObjectPath(pid));
		} finally {
        	session.save();
		}
        return noContent().build();
    }


    public void setSession(final Session session) {
        this.session = session;
    }

}
