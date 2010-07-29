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
package org.geotoolkit.test.stress;

import java.util.Arrays;
import java.util.Random;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.math.XMath;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.referencing.operation.transform.LinearTransform;


/**
 * Generates random grid geometries in order to create random requests.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 */
public class RequestGenerator {
    /**
     * The random number generator.
     */
    final Random random;

    /**
     * Contains the maximal extent of the random envelopes to be generated by
     * {@link #getRandomGrid()}. This grid geometry must have a valid CRS.
     */
    protected final GeneralGridGeometry domain;

    /**
     * The minimal grid size to be requested. The default value is 1 for every dimensions.
     * Subclass constructors can modify those values in-place if they need different ones.
     */
    protected final int[] minimalGridSize;

    /**
     * The maximal grid size to be requested. The default value is the grid span for every
     * dimensions. Subclass constructors can modify those values in-place if they need
     * different ones.
     */
    protected final int[] maximalGridSize;

    /**
     * Creates a new request generator for the given domain.
     *
     * @param domain Contains the maximal extent of the random envelopes to be generated.
     */
    public RequestGenerator(final GeneralGridGeometry domain) {
        this.random = new Random();
        this.domain = domain;
        final GridEnvelope gridRange = domain.getGridRange();
        final int dimension = gridRange.getDimension();
        minimalGridSize = new int[dimension];
        maximalGridSize = new int[dimension];
        Arrays.fill(minimalGridSize, 1);
        for (int i=0; i<dimension; i++) {
            maximalGridSize[i] = gridRange.getSpan(i);
        }
    }

    /**
     * Returns a random grid geometry inside the {@linkplain #domain}. The grid envelope
     * size will range from {@link #minimalGridSize} to {@link #maximalGridSize} inclusive.
     *
     * @return A new random grid geoemtry inside the domain.
     */
    public GeneralGridGeometry getRandomGrid() {
        final GridEnvelope gridRange = domain.getGridRange();
        final int dimension = gridRange.getDimension();
        final int[] lower = new int[dimension];
        final int[] upper = new int[dimension];
        for (int i=0; i<dimension; i++) {
            int min = minimalGridSize[i];
            final int span = random.nextInt(maximalGridSize[i] - min + 1) + min;
            min = gridRange.getLow(i);
            min += random.nextInt(gridRange.getHigh(i) - min - span + 2);
            lower[i] = min;
            upper[i] = min + span;
        }
        return new GeneralGridGeometry(new GeneralGridEnvelope(lower, upper, false),
                domain.getGridToCRS(), domain.getCoordinateReferenceSystem());
    }

    /**
     * Returns the resolution of the given grid geometry, in units of the envelope.
     *
     * @param  request The grid geometry from which to extract the resolution.
     * @return The resolution, in units of the envelope.
     */
    public static double[] getResolution(final GeneralGridGeometry request) {
        final Matrix gridToCRS = ((LinearTransform) request.getGridToCRS()).getMatrix();
        final double[] row = new double[gridToCRS.getNumCol() - 1];
        final double[] res = new double[gridToCRS.getNumRow() - 1];
        for (int j=0; j<res.length; j++) {
            for (int i=0; i<row.length; i++) {
                row[i] = gridToCRS.getElement(j, i);
            }
            res[j] = XMath.magnitude(row);
        }
        return res;
    }
}