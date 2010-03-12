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
package org.geotoolkit.referencing.factory.wkt;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.VerticalCRS;

import org.geotoolkit.test.Depend;
import org.geotoolkit.io.wkt.WKTFormatTest;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.*;
import org.postgresql.ds.PGSimpleDataSource;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Tests {@link PostgisAuthorityFactory}. This test case requires the test configuration
 * described in the {@code geotk-coverage-sql} module, otherwise the test will be skipped.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
@Depend(WKTFormatTest.class)
public class PostgisAuthorityFactoryTest {
    /**
     * Gets the connection parameters to the coverage database.
     */
    private static DataSource getCoverageDataSource() throws IOException {
        final File pf = new File(Installation.TESTS.directory(true), "coverage-sql.properties");
        assumeTrue(pf.isFile()); // The test will be skipped if the above resource is not found.
        final Properties properties = new Properties();
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(pf));
        properties.load(in);
        in.close();
        final PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName  (properties.getProperty("server"));
        ds.setDatabaseName(properties.getProperty("database"));
        ds.setUser        (properties.getProperty("user"));
        ds.setPassword    (properties.getProperty("password"));
        return ds;
    }

    /**
     * Tests a few CRS using the test database of the {@code geotk-coverage-sql} module,
     * if this database is found.
     *
     * @throws FactoryException Should not happen.
     * @throws IOException If an error occured while reading the properties file.
     * @throws SQLException If an error occured while reading the PostGIS tables.
     */
    @Test
    public void testUsingCoverageSQL() throws FactoryException, IOException, SQLException {
        final Connection connection = getCoverageDataSource().getConnection();
        final PostgisAuthorityFactory factory = new PostgisAuthorityFactory(null, connection);
        try {
            /*
             * Test general information.
             */
            assertEquals("EPSG", Citations.getIdentifier(factory.getAuthority()));
            assertTrue(factory.getBackingStoreDescription().contains("PostgreSQL"));
            /*
             * Test fetching a few CRS.
             */
            assertEquals("WGS 84", String.valueOf(factory.getDescriptionText("EPSG:4326")));

            final GeographicCRS geoCRS = factory.createGeographicCRS("EPSG:4326");
            assertEquals("EPSG:4326",    AbstractIdentifiedObject.getIdentifier(geoCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:4326", AbstractIdentifiedObject.getIdentifier(geoCRS, Citations.POSTGIS).toString());
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, geoCRS));

            final ProjectedCRS projCRS = factory.createProjectedCRS("EPSG:3395");
            assertEquals("EPSG:3395",    AbstractIdentifiedObject.getIdentifier(projCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:3395", AbstractIdentifiedObject.getIdentifier(projCRS, Citations.POSTGIS).toString());
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, projCRS.getBaseCRS()));

            final VerticalCRS vertCRS = factory.createVerticalCRS("EPSG:57150");
            assertEquals("EPSG:57150",   AbstractIdentifiedObject.getIdentifier(vertCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:6000", AbstractIdentifiedObject.getIdentifier(vertCRS, Citations.POSTGIS).toString());
            /*
             * Test the list of authority codes.
             */
            final Set<String> all        = factory.getAuthorityCodes(null);
            final Set<String> geographic = factory.getAuthorityCodes(GeographicCRS.class);
            final Set<String> projected  = factory.getAuthorityCodes(ProjectedCRS.class);
            final Set<String> vertical   = factory.getAuthorityCodes(VerticalCRS .class);
            assertTrue (all       .contains("4326"));
            assertTrue (all       .contains("3395"));
            assertTrue (geographic.contains("4326"));
            assertTrue (projected .contains("3395"));
            assertFalse(projected .contains("4326"));
            assertFalse(geographic.contains("3395"));
            assertTrue(all.containsAll(geographic));
            assertTrue(all.containsAll(projected));
            assertTrue(all.containsAll(vertical));
            assertTrue(Collections.disjoint(geographic, projected));
            assertTrue(Collections.disjoint(geographic, vertical));
            assertTrue(Collections.disjoint(projected,  vertical));
        } finally {
            factory.dispose(false);
        }
        assertTrue("Connection should be closed.", connection.isClosed());
    }

    /**
     * Tests the wrapping of {@link PostgisAuthorityFactory} in {@link PostgisCachingFactory}.
     *
     * @throws FactoryException Should not happen.
     * @throws IOException If an error occured while reading the properties file.
     */
    @Test
    public void testCaching() throws FactoryException, IOException {
        final PostgisCachingFactory factory = new PostgisCachingFactory(null, getCoverageDataSource());
        try {
            /*
             * Test general information.
             */
            assertEquals("EPSG", Citations.getIdentifier(factory.getAuthority()));
            assertTrue(factory.getBackingStoreDescription().contains("PostgreSQL"));
            /*
             * Test fetching a few CRS.
             */
            assertEquals("WGS 84", String.valueOf(factory.getDescriptionText("EPSG:4326")));

            final GeographicCRS geoCRS = factory.createGeographicCRS("EPSG:4326");
            assertEquals("EPSG:4326",    AbstractIdentifiedObject.getIdentifier(geoCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:4326", AbstractIdentifiedObject.getIdentifier(geoCRS, Citations.POSTGIS).toString());
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, geoCRS));

            final ProjectedCRS projCRS = factory.createProjectedCRS("EPSG:3395");
            assertEquals("EPSG:3395",    AbstractIdentifiedObject.getIdentifier(projCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:3395", AbstractIdentifiedObject.getIdentifier(projCRS, Citations.POSTGIS).toString());
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, projCRS.getBaseCRS()));

            final VerticalCRS vertCRS = factory.createVerticalCRS("EPSG:57150");
            assertEquals("EPSG:57150",   AbstractIdentifiedObject.getIdentifier(vertCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:6000", AbstractIdentifiedObject.getIdentifier(vertCRS, Citations.POSTGIS).toString());
            /*
             * Test the cache.
             */
            assertSame(geoCRS,  factory.createGeographicCRS("EPSG:4326"));
            assertSame(projCRS, factory.createProjectedCRS ("EPSG:3395"));
            assertSame(vertCRS, factory.createVerticalCRS  ("EPSG:57150"));
        } finally {
            factory.dispose();
        }
    }
}