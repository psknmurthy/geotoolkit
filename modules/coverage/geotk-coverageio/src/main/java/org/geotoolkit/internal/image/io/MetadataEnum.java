/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.util.List;

import org.opengis.referencing.crs.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.IdentifiedObject;

import org.geotoolkit.util.collection.UnmodifiableArrayList;


/**
 * Enumerations used in {@link org.geotoolkit.image.io.metadata.PredefinedMetadataFormat}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
public final class MetadataEnum {
    /**
     * Do not allow instantiation of this class.
     */
    private MetadataEnum() {
    }

    /**
     * Enumeration of valid coordinate reference system types.
     */
    public static final List<String> CRS_TYPES = UnmodifiableArrayList.wrap(new String[] {
        "geographic", "projected"
    });

    /**
     * The interfaces associated to the {@link #CRS_TYPES} enumeration.
     * Must be in the same order than the above-cited list of type name.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Class<? extends CoordinateReferenceSystem>[] CRS_INTERFACES = new Class[] {
        GeographicCRS.class, ProjectedCRS.class
    };

    /**
     * Enumeration of valid coordinate system types.
     */
    public static final List<String> CS_TYPES = UnmodifiableArrayList.wrap(new String[] {
        "ellipsoidal", "cartesian"
    });

    /**
     * The interfaces associated to the {@link #CS_TYPES} enumeration.
     * Must be in the same order than the above-cited list of type name.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Class<? extends CoordinateSystem>[] CS_INTERFACES = new Class[] {
        EllipsoidalCS.class, CartesianCS.class
    };

    /**
     * Enumeration of valid datum types.
     */
    public static final List<String> DATUM_TYPES = UnmodifiableArrayList.wrap(new String[] {
        "geodetic", "vertical", "temporal", "image", "engineering"
    });

    /**
     * The interfaces associated to the {@link #DATUM_TYPES} enumeration.
     * Must be in the same order than the above-cited list of type name.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Class<? extends Datum>[] DATUM_INTERFACES = new Class[] {
        GeodeticDatum.class, VerticalDatum.class, TemporalDatum.class, ImageDatum.class, EngineeringDatum.class
    };

    /**
     * Returns the name of the type for the given identified object.
     *
     * @param object The object for which the name of the type is wanted.
     * @param types  One of the {@code FOO_INTERFACES} constant.
     * @param names  The {@code FOO_TYPES} constant associated with the above types.
     */
    private static <T extends IdentifiedObject> String getType(final T object,
            final Class<? extends T>[] types, final List<String> names)
    {
        for (int i=0; i<types.length; i++) {
            final Class<? extends IdentifiedObject> type = types[i];
            if (type.isInstance(object)) {
                return names.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the name of the type for the given CRS object, or {@code null} if none.
     *
     * @param  object The object for which the name of the type is wanted, or {@code null}.
     * @return The name of the type, or {@code null} if unknown.
     */
    public static String getType(final CoordinateReferenceSystem object) {
        return getType(object, CRS_INTERFACES, CRS_TYPES);
    }

    /**
     * Returns the name of the type for the given CS object, or {@code null} if none.
     *
     * @param  object The object for which the name of the type is wanted, or {@code null}.
     * @return The name of the type, or {@code null} if unknown.
     */
    public static String getType(final CoordinateSystem object) {
        return getType(object, CS_INTERFACES, CS_TYPES);
    }

    /**
     * Returns the name of the type for the given datum object, or {@code null} if none.
     *
     * @param  object The object for which the name of the type is wanted, or {@code null}.
     * @return The name of the type, or {@code null} if unknown.
     */
    public static String getType(final Datum object) {
        return getType(object, DATUM_INTERFACES, DATUM_TYPES);
    }
}