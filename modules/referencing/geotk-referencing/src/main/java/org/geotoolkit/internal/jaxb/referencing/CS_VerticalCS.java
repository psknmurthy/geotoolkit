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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.referencing.cs.VerticalCS;
import org.geotoolkit.referencing.cs.DefaultVerticalCS;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;


/**
 * JAXB adapter for {@link VerticalCS}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.05
 *
 * @since 3.00
 * @module
 */
public final class CS_VerticalCS extends MetadataAdapter<CS_VerticalCS, VerticalCS> {
    /**
     * Empty constructor for JAXB only.
     */
    public CS_VerticalCS() {
    }

    /**
     * Wraps a {@link VerticalCS} value with a {@code gml:verticalCS} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CS_VerticalCS(final VerticalCS metadata) {
        super(metadata);
    }

    /**
     * Returns the {@link VerticalCS} value wrapped by a {@code gml:verticalCS} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CS_VerticalCS wrap(final VerticalCS value) {
        return new CS_VerticalCS(value);
    }

    /**
     * Returns the {@link DefaultVerticalCS} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "VerticalCS")
    public DefaultVerticalCS getElement() {
        final VerticalCS metadata = this.metadata;
        return (metadata instanceof DefaultVerticalCS) ?
            (DefaultVerticalCS) metadata : new DefaultVerticalCS(metadata);
    }

    /**
     * Sets the value for the {@link DefaultVerticalCS}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultVerticalCS metadata) {
        this.metadata = metadata;
    }
}