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

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "objectDatastreams")
public class ObjectDatastreams {

    @XmlElement(name = "datastream")
    public Set<DatastreamElement> datastreams;

    @XmlType(name = "datastream")
    public static class DatastreamElement {

        @XmlAttribute
        public String dsid;

        @XmlAttribute
        public String label;

        @XmlAttribute
        public String mimeType;

        public DatastreamElement(final String dsid, final String label,
                final String mimeType) {
            this.dsid = dsid;
            this.label = label;
            this.mimeType = mimeType;
        }

        public DatastreamElement() {
        }
    }
}
