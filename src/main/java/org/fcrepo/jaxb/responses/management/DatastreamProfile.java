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

package org.fcrepo.jaxb.responses.management;

import org.fcrepo.kernel.Datastream;
import org.fcrepo.kernel.utils.LowLevelCacheEntry;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "datastreamProfile",
        namespace = "http://www.fedora.info/definitions/1/0/management/")
public class DatastreamProfile {

    @XmlAttribute
    public String pid;

    @XmlAttribute
    public String dsID;

    @XmlElement
    public String dsLabel;

    @XmlElement
    public String dsOwnerId;

    @XmlElement
    public String dsVersionID;

    @XmlElement
    public Date dsLastModifiedDate;

    @XmlElement
    public Date dsCreateDate;

    @XmlElement
    public DatastreamStates dsState;

    @XmlElement
    public String dsMIME;

    @XmlElement
    public URI dsFormatURI;

    @XmlElement
    public DatastreamControlGroup dsControlGroup;

    @XmlElement
    public long dsSize;

    @XmlElement
    public String dsVersionable;

    @XmlElement
    public String dsInfoType;

    @XmlElement
    public String dsLocation;

    @XmlElement
    public String dsLocationType;

    @XmlElement
    public String dsChecksumType;

    @XmlElement
    public URI dsChecksum;

    @XmlElement
    public DSStores dsStores;

    public static enum DatastreamControlGroup {
        M, E, R
    }

    public static enum DatastreamStates {
        A, D, I
    }

    /**
     * adds the datastream store information to the datastream profile output.
     * datastream profile output in fcrepo4 no longer matches output from
     * fcrepo3.x
     */
    public static class DSStores {

        @XmlElement(name = "dsStore")
        public List<String> storeIdentifiers;

        public DSStores() {
            this.storeIdentifiers = new ArrayList<String>();
        }

        public DSStores(final Datastream datastream, final Set<?> cacheEntries) {
            this.storeIdentifiers = new ArrayList<String>();
            for (final Object name : cacheEntries) {
                final LowLevelCacheEntry cacheEntry = (LowLevelCacheEntry) name;
                storeIdentifiers.add(cacheEntry.getExternalIdentifier());
            }
        }
    }

}
