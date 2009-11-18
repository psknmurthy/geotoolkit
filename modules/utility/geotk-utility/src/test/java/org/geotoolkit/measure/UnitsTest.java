/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.measure;

import javax.measure.unit.Unit;
import javax.measure.quantity.Quantity;
import javax.measure.converter.UnitConverter;
import static javax.measure.unit.Unit.ONE;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.NonSI.CENTIRADIAN;
import static javax.measure.unit.NonSI.DEGREE_ANGLE;
import static javax.measure.unit.NonSI.MINUTE_ANGLE;
import static javax.measure.unit.NonSI.SECOND_ANGLE;
import static javax.measure.unit.NonSI.GRADE;
import static javax.measure.unit.NonSI.DAY;
import static javax.measure.unit.NonSI.SPHERE;
import static javax.measure.unit.NonSI.ATMOSPHERE;
import static javax.measure.unit.NonSI.NAUTICAL_MILE;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.measure.Units.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Test conversions using the units declared in {@link Units}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 */
public class UnitsTest {
    /**
     * Compares two values for equality.
     */
    private static <Q extends Quantity> void checkConversion(
            final double expected, final Unit<Q> unitExpected,
            final double actual,   final Unit<Q> unitActual)
    {
        UnitConverter converter = unitActual.getConverterTo(unitExpected);
        assertEquals(expected, converter.convert(actual), 1E-6);
        converter = converter.inverse();
        assertEquals(actual, converter.convert(expected), 1E-6);
    }

    /**
     * Checks the conversions using {@link Units#SEXAGESIMAL_DMS}.
     */
    @Test
    public void testSexagesimal() {
        checkConversion(10.00, DEGREE_ANGLE, 10.0000, SEXAGESIMAL_DMS);
        checkConversion(10.01, DEGREE_ANGLE, 10.0036, SEXAGESIMAL_DMS);
        checkConversion(10.50, DEGREE_ANGLE, 10.3000, SEXAGESIMAL_DMS);
        checkConversion(10.99, DEGREE_ANGLE, 10.5924, SEXAGESIMAL_DMS);
    }

    /**
     * Tests serialization of units.
     *
     * @todo Disabled for now. Needs JSR-275 fix.
     */
    @Test
    @Ignore
    public void testSerialization() {
        assertEquals(DEGREE_ANGLE,         serialize(DEGREE_ANGLE));
        assertEquals(SEXAGESIMAL_DMS,      serialize(SEXAGESIMAL_DMS));
        assertEquals(DEGREE_MINUTE_SECOND, serialize(DEGREE_MINUTE_SECOND));
        assertEquals(PPM,                  serialize(PPM));
    }

    /**
     * Tests {@link Units#isTemporal}.
     */
    @Test
    public void testIsTemporal() {
        // Standard units
        assertFalse(isTemporal(null));
        assertFalse(isTemporal(ONE));
        assertFalse(isTemporal(METRE));
        assertFalse(isTemporal(RADIAN));
        assertFalse(isTemporal(CENTIRADIAN));
        assertFalse(isTemporal(DEGREE_ANGLE));
        assertFalse(isTemporal(MINUTE_ANGLE));
        assertFalse(isTemporal(SECOND_ANGLE));
        assertFalse(isTemporal(GRADE));
        assertTrue (isTemporal(DAY));
        assertFalse(isTemporal(SPHERE));
        assertFalse(isTemporal(ATMOSPHERE));
        assertFalse(isTemporal(NAUTICAL_MILE));

        // Additional units
        assertFalse(isTemporal(PPM));
        assertTrue (isTemporal(MILLISECOND));
        assertFalse(isTemporal(SEXAGESIMAL_DMS));
        assertFalse(isTemporal(DEGREE_MINUTE_SECOND));
    }

    /**
     * Tests {@link Units#isLinear}.
     */
    @Test
    public void testIsLinear() {
        // Standard units
        assertFalse(isLinear(null));
        assertFalse(isLinear(ONE));
        assertTrue (isLinear(METRE));
        assertFalse(isLinear(RADIAN));
        assertFalse(isLinear(CENTIRADIAN));
        assertFalse(isLinear(DEGREE_ANGLE));
        assertFalse(isLinear(MINUTE_ANGLE));
        assertFalse(isLinear(SECOND_ANGLE));
        assertFalse(isLinear(GRADE));
        assertFalse(isLinear(DAY));
        assertFalse(isLinear(SPHERE));
        assertFalse(isLinear(ATMOSPHERE));
        assertTrue (isLinear(NAUTICAL_MILE));

        // Additional units
        assertFalse(isLinear(PPM));
        assertFalse(isLinear(MILLISECOND));
        assertFalse(isLinear(SEXAGESIMAL_DMS));
        assertFalse(isLinear(DEGREE_MINUTE_SECOND));
    }

    /**
     * Tests {@link Units#isAngular}.
     */
    @Test
    public void testIsAngular() {
        // Standard units
        assertFalse(isAngular(null));
        assertFalse(isAngular(ONE));
        assertFalse(isAngular(METRE));
        assertTrue (isAngular(RADIAN));
        assertTrue (isAngular(CENTIRADIAN));
        assertTrue (isAngular(DEGREE_ANGLE));
        assertTrue (isAngular(MINUTE_ANGLE));
        assertTrue (isAngular(SECOND_ANGLE));
        assertTrue (isAngular(GRADE));
        assertFalse(isAngular(DAY));
        assertFalse(isAngular(SPHERE));
        assertFalse(isAngular(ATMOSPHERE));
        assertFalse(isAngular(NAUTICAL_MILE));

        // Additional units
        assertFalse(isAngular(PPM));
        assertFalse(isAngular(MILLISECOND));
        assertTrue (isAngular(SEXAGESIMAL_DMS));
        assertTrue (isAngular(DEGREE_MINUTE_SECOND));
    }

    /**
     * Tests {@link Units#isScale}.
     */
    @Test
    public void testIsScale() {
        // Standard units
        assertFalse(isScale(null));
        assertTrue (isScale(ONE));
        assertFalse(isScale(METRE));
        assertFalse(isScale(RADIAN));
        assertFalse(isScale(CENTIRADIAN));
        assertFalse(isScale(DEGREE_ANGLE));
        assertFalse(isScale(MINUTE_ANGLE));
        assertFalse(isScale(SECOND_ANGLE));
        assertFalse(isScale(GRADE));
        assertFalse(isScale(DAY));
        assertFalse(isScale(SPHERE));
        assertFalse(isScale(ATMOSPHERE));
        assertFalse(isScale(NAUTICAL_MILE));

        // Additional units
        assertTrue (isScale(PPM));
        assertFalse(isScale(MILLISECOND));
        assertFalse(isScale(SEXAGESIMAL_DMS));
        assertFalse(isScale(DEGREE_MINUTE_SECOND));
    }
}