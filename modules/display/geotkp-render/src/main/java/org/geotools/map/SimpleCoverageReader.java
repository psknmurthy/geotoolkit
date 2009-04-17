/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.map;

import java.io.IOException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotools.coverage.io.CoverageReadParam;
import org.geotools.coverage.io.CoverageReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Wrapper class around a Gridcoverage2D.
 *
 * @author Johann Sorel (Geomtays)
 */
public class SimpleCoverageReader implements CoverageReader{

    private final GridCoverage2D coverage;

    public SimpleCoverageReader(GridCoverage2D coverage){
        this.coverage = coverage;
    }

    @Override
    public GridCoverage2D read(CoverageReadParam param) throws FactoryException, TransformException, IOException {
        return coverage;
    }

    @Override
    public ReferencedEnvelope getCoverageBounds() {
        return new ReferencedEnvelope(coverage.getEnvelope());
    }

}
