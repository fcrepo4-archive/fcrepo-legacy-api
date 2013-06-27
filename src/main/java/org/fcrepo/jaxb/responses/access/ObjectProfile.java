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
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "objectProfile")
public class ObjectProfile {

    @XmlAttribute
    public String pid;

    @XmlElement
    public String objLabel;

    @XmlElement
    public String objOwnerId;

    @XmlElementWrapper(name = "objModels")
    @XmlElement(name = "model")
    public Collection<String> objModels;

    @XmlElement
    public Date objCreateDate;

    @XmlElement
    public Date objLastModDate;

    @XmlElement
    public URI objDissIndexViewURL;

    @XmlElement
    public URI objItemIndexViewURL;

    @XmlElement
    public ObjectStates objState;

    @XmlElement
    public Long objSize;

    public static enum ObjectStates {
        A, D, I
    }

}
