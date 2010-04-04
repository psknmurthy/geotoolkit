/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal;


/**
 * An interface for modules which can perform some initialization and shutdown process.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @see org.geotoolkit.lang.Setup
 *
 * @since 3.10
 * @module
 */
public interface SetupService {
    /**
     * Invoked when the module needs to be initialized.
     *
     * @param reinit {@code false} on first invocation, or {@code true} if this method
     *        is invoked after a shutdown.
     */
    void initialize(boolean reinit);

    /**
     * Invoked when the module needs to be shutdown.
     */
    void shutdown();
}