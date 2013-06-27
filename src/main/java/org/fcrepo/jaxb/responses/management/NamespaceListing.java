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

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "namespaceRegistry")
public class NamespaceListing {

    @XmlElementRef
    public Set<Namespace> namespaces;

    public NamespaceListing(final Set<Namespace> nses) {
        namespaces = nses;
    }

    public NamespaceListing() {
    }

    @XmlRootElement(name = "namespace")
    public static class Namespace {

        public String prefix;

        @XmlAttribute(name = "URI")
        public URI uri;

        public Namespace(final String prefix, final URI uri) {
            this.prefix = prefix;
            this.uri = uri;
        }

        public Namespace() {
        }
    }

}
