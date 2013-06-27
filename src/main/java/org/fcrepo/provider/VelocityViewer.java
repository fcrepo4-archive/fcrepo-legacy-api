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

package org.fcrepo.provider;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.fcrepo.jaxb.responses.access.DescribeRepository;
import org.fcrepo.jaxb.search.FieldSearchResult;
import org.slf4j.Logger;

/**
 * Resolves the view to be used
 * 
 * @author Vincent Nguyen
 */
public class VelocityViewer {

    private VelocityEngine velocityEngine;

    private static final Logger logger = getLogger(VelocityViewer.class);

    public VelocityViewer() {
        try {
            // Load the velocity properties from the class path
            final Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream(
                    "velocity.properties"));

            // Create and initialize the template engine
            velocityEngine = new VelocityEngine(properties);
        } catch (final Exception e) {
            logger.warn("Exception rendering Velocity template: {}", e);
        }
    }

    public String getRepoInfo(final DescribeRepository repoinfo) {
        try {
            // Build a context to hold the model
            final VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("repo", repoinfo);

            // Execute the template
            final StringWriter writer = new StringWriter();
            velocityEngine.mergeTemplate("views/repo-info.vm", "utf-8",
                    velocityContext, writer);

            // Return the result
            return writer.toString();
        } catch (final Exception e) {
            logger.warn("Exception rendering Velocity template: {}", e);
        }
        return null;
    }

    public String getFieldSearch(final FieldSearchResult results) {
        try {
            // Build a context to hold the model
            final VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("results", results);

            // Execute the template
            final StringWriter writer = new StringWriter();
            velocityEngine.mergeTemplate("views/search-results-form.vm",
                    "utf-8", velocityContext, writer);

            // Return the result
            return writer.toString();
        } catch (final Exception e) {
            logger.warn("Exception rendering Velocity template: {}", e);
        }
        return null;
    }

}
