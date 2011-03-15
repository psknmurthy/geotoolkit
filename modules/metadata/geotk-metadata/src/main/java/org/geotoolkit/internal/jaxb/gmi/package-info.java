/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

/**
 * Extends some classes from the {@link org.geotoolkit.metadata.iso} package in order
 * to give them the {@code "gmi"} namespace. This is required for XML (un)marshalling
 * because GeoAPI merged some classes which were dissociated in the ISO specifications.
 * The GeoAPI merge were done in order to simplify the conceptual model for developers,
 * since the classes were different in ISO specifications for historical reasons - not
 * conceptual reasons.
 * <p>
 * In Geotk implementation, users need to care only about the public classes defined in
 * the {@link org.geotoolkit.metadata.iso} package. When marshalling, the adapters will
 * inspect the properties that are ISO 19115-2 extensions and wrap automatically the
 * {@code "gmd"} metadata into a {@code "gmi"} metadata if any ISO 19115-2 property is
 * non-null or non-empty. This work is performed by a {@code wrap} static method defined
 * in each class of this package.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = Namespaces.GMI)
@XmlAccessorType(XmlAccessType.NONE)
package org.geotoolkit.internal.jaxb.gmi;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.geotoolkit.xml.Namespaces;