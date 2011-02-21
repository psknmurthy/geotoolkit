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
import org.opengis.metadata.extent.VerticalExtent;
import org.geotoolkit.metadata.iso.extent.DefaultVerticalExtent;


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
public final class EX_VerticalExtent extends MetadataAdapter<EX_VerticalExtent, VerticalExtent> {
    /**
     * Empty constructor for JAXB only.
     */
    public EX_VerticalExtent() {
    }

    /**
     * Wraps an VerticalExtent value with a {@code EX_VerticalExtent} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private EX_VerticalExtent(final VerticalExtent metadata) {
        super(metadata);
    }

    /**
     * Returns the VerticalExtent value wrapped by a {@code EX_VerticalExtent} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected EX_VerticalExtent wrap(final VerticalExtent value) {
        return new EX_VerticalExtent(value);
    }

    /**
     * Returns the {@link DefaultVerticalExtent} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultVerticalExtent getElement() {
        final VerticalExtent metadata = this.metadata;
        return (metadata instanceof DefaultVerticalExtent) ?
            (DefaultVerticalExtent) metadata : new DefaultVerticalExtent(metadata);
    }

    /**
     * Sets the value for the {@link DefaultVerticalExtent}. This
     * method is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultVerticalExtent metadata) {
        this.metadata = metadata;
    }
}