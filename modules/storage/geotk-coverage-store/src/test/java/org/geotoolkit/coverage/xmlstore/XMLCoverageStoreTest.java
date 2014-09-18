/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.AbstractPyramidalModelStoreTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.util.FileUtilities;
import org.junit.AfterClass;

/**
 * @author Johann Sorel (Geomatys)
 */
public class XMLCoverageStoreTest extends AbstractPyramidalModelStoreTest {

    private static final List<File> folders = new ArrayList<File>();
    
    @Override
    protected CoverageStore createStore() throws Exception{

        final File tempFolder = File.createTempFile("mosaic", "");
        tempFolder.delete();
        tempFolder.mkdirs();
        folders.add(tempFolder);
        final CoverageStore store = new XMLCoverageStore(tempFolder);

        return store;
    }

    @AfterClass
    public static void cleanup() throws IOException {
        for(File f : folders){
            FileUtilities.deleteDirectory(f.toPath());
        }
        folders.clear();
    }
    
}
