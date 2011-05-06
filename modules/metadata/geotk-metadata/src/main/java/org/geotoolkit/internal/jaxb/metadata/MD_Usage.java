/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.identification.Usage;
import org.geotoolkit.metadata.iso.identification.DefaultUsage;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 2.5
 * @module
 */
public final class MD_Usage extends MetadataAdapter<MD_Usage, Usage> {
    /**
     * Empty constructor for JAXB only.
     */
    public MD_Usage() {
    }

    /**
     * Wraps an Usage value with a {@code MD_Usage} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_Usage(final Usage metadata) {
        super(metadata);
    }

    /**
     * Returns the Usage value wrapped by a {@code MD_Usage} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_Usage wrap(final Usage value) {
        return new MD_Usage(value);
    }

    /**
     * Returns the {@link DefaultUsage} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultUsage getElement() {
        if (skip()) return null;
        final Usage metadata = this.metadata;
        return (metadata instanceof DefaultUsage) ?
            (DefaultUsage) metadata : new DefaultUsage(metadata);
    }

    /**
     * Sets the value for the {@link DefaultUsage}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultUsage metadata) {
        this.metadata = metadata;
    }
}
