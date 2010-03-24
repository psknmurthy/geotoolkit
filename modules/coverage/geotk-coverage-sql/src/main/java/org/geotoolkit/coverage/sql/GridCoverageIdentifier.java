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
package org.geotoolkit.coverage.sql;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.internal.sql.table.MultiColumnIdentifier;


/**
 * The identifier of a {@link GridCoverageEntry}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @see GridCoverageTable#createIdentifier
 *
 * @since 3.10
 * @module
 */
@Immutable
final class GridCoverageIdentifier extends MultiColumnIdentifier<GridCoverageIdentifier> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    static final long serialVersionUID = -6775081539771641953L;

    /**
     * The series in which the {@link GridCoverageEntry} is defined.
     */
    final SeriesEntry series;

    /**
     * The grid coverage filename, not including the extension.
     */
    final String filename;

    /**
     * The 1-based index of the image to read.
     */
    final short imageIndex;

    /**
     * The 1-based index of the altitude in the {@link GridGeometryEntry#verticalOrdinates} array,
     * or 0 if none. We store the index of altitude instead than the altitude value in order to
     * ensure that two requests with slightly different altitudes will be resolved to the same
     * entry if the different altitudes are resolved to the same index.
     */
    final short zIndex;

    /**
     * The spatial and vertical extents of the grid coverage, together with the <cite>grid to
     * CRS</cite> transform. This information is not part of the identifier, so it shall
     * <strong>not</strong> be taken in consideration in implementation of {@link #hashCode()},
     * {@link #equals(Object)} and {@link #compareTo(GridCoverageIdentifier)} methods. This entry
     * is stored there because it was necessary for the computation of {@link #zIndex}, so putting
     * it there avoid the need to fetch it twice.
     * <p>
     * This field shall be considered as final. It is not declared so only in order to allow
     * {@link GridCoverageTable#createEntry} to complete this {@code GridCoverageIdentifier}
     * when this {@code geometry} field is still {@code null}. This happen always before the
     * instance is published.
     */
    GridGeometryEntry geometry;

    /**
     * Creates a new identifier.
     */
    GridCoverageIdentifier(final SeriesEntry series, final String filename, final short imageIndex,
            final short zIndex, final GridGeometryEntry geometry)
    {
        this.series     = series;
        this.filename   = filename;
        this.imageIndex = imageIndex;
        this.zIndex     = zIndex;
        this.geometry   = geometry;
    }

    /**
     * Returns the image file. The returned file should be
     * {@linkplain File#isAbsolute absolute}. If it is not, then there is probably no
     * {@linkplain org.constellation.catalog.ConfigurationKey#ROOT_DIRECTORY root directory}
     * set and consequently the file is probably not accessible locally.
     * In such case, consider using {@link #uri()} instead.
     */
    public File file() {
        return series.file(filename);
    }

    /**
     * Returns the image URI.
     *
     * @throws URISyntaxException if the URI can not be created from the informations
     *         provided in the database.
     */
    public URI uri() throws URISyntaxException {
        return series.uri(filename);
    }

    /**
     * Returns the identifiers. This method intentionally exclude the {@link #zIndex} value from
     * the identifiers, because it doesn't appear explicitly as a column in the database.
     */
    @Override
    public Comparable<?>[] getIdentifiers() {
        return new Comparable<?>[] {
            series.identifier,
            filename,
            imageIndex
            // Do NOT include 'zIndex' and 'geometry'.
        };
    }

    /**
     * Returns a hash code value for this identifier.
     */
    @Override
    public int hashCode() {
        return super.hashCode() + 31*zIndex; // Do NOT include 'geometry'.
    }

    /**
     * Returns {@code true} if this object is equals to the given object.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (super.equals(other)) {
            return zIndex == ((GridCoverageIdentifier) other).zIndex;
            // Do NOT compare 'geometry'.
        }
        return false;
    }

    /**
     * Compares this identifier with the given one for order.
     */
    @Override
    public int compareTo(final GridCoverageIdentifier that) {
        int d = super.compareTo(that);
        if (d == 0) {
            d = (int) zIndex - (int) that.zIndex;
            // Do NOT compare 'geometry'.
        }
        return d;
    }
}