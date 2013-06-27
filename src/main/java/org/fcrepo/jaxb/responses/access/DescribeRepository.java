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

package org.fcrepo.jaxb.responses.access;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fedoraRepository")
@XmlAccessorType(XmlAccessType.FIELD)
public class DescribeRepository {

    public static final String FEDORA_VERSION = "4.0-modeshape-candidate";

    @XmlElement
    public URI repositoryBaseURL;

    @XmlElement
    protected String repositoryVersion = FEDORA_VERSION;

    @XmlElement
    public Long numberOfObjects;

    @XmlElement
    public Long repositorySize;

    @XmlElement(name = "sampleOAI-URL")
    public URI sampleOAIURL;

    @XmlElement(name = "clusterConfiguration", nillable = true)
    public DescribeCluster clusterConfiguration = null;

    public URI getRepositoryBaseURL() {
        return repositoryBaseURL;
    }

    public void setRepositoryBaseURL(final URI repositoryBaseURL) {
        this.repositoryBaseURL = repositoryBaseURL;
    }

    public String getRepositoryVersion() {
        return repositoryVersion;
    }

    public void setRepositoryVersion(final String repositoryVersion) {
        this.repositoryVersion = repositoryVersion;
    }

    public Long getNumberOfObjects() {
        return numberOfObjects;
    }

    public void setNumberOfObjects(final Long numberOfObjects) {
        this.numberOfObjects = numberOfObjects;
    }

    public Long getRepositorySize() {
        return repositorySize;
    }

    public void setRepositorySize(final Long repositorySize) {
        this.repositorySize = repositorySize;
    }

    public URI getSampleOAIURL() {
        return sampleOAIURL;
    }

    public void setSampleOAIURL(final URI sampleOAIURL) {
        this.sampleOAIURL = sampleOAIURL;
    }

    public DescribeCluster getClusterConfiguration() {
        return clusterConfiguration;
    }

    public void setClusterConfiguration(DescribeCluster describeCluster) {
        if (describeCluster != null)
            this.clusterConfiguration = describeCluster;
    }

}
