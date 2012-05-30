/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.iterator;

import java.awt.Rectangle;
import java.awt.image.*;

/**
 * An Iterator for traversing anyone rendered Image.
 * <p>
 * Iteration transverse each tiles(raster) from rendered image or raster source one by one in order.
 * Rendered Image or raster source must contains Float type data.
 * Iteration to follow tiles(raster) begin by raster bands, next, raster x coordinates,
 * and to finish raster y coordinates.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&gt; tiles x coordinates --&gt; tiles y coordinates --&gt; next rendered image tiles.
 *
 * Moreover iterator traversing a write or read each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Code example :
 * {@code
 *                  final DefaultWritableFloatIterator dWBI = new DefaultWritableFloatIterator(renderedImage);
 *                  while (dWBI.next()) {
 *                      dWBI.setSample(int value);
 *                  }
 * }
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class DefaultWritableDirectFloatIterator extends DefaultDirectFloatIterator {

    /**
     * Raster which is followed by iterator and wherein caller write.
     */
    private WritableRaster currentWritableRaster;

    /**
     * Rendered image which is followed by iterator and wherein caller write.
     */
    private WritableRenderedImage writableRenderedImage;

    /**
     * Current last x tile position in rendered image tile array.
     */
    private int prectX;

    /**
     * Current last y tile position in rendered image tile array.
     */
    private int prectY;

    /**
     * Current writable raster data table.
     */
    private float[][] currentWritableDataArray;

    /**
     * Create an appropriate iterator to read and write in a raster.
     *
     * @param raster         raster which is followed by read-only iterator.
     * @param writableRaster raster which is followed by this write-only iterator and wherein value is writing.
     * @throws IllegalArgumentException if raster and writable raster dimensions are not conform.
     */
    public DefaultWritableDirectFloatIterator(final Raster raster, final WritableRaster writableRaster) {
        super(raster);
        checkRasters(raster, writableRaster);
        this.currentWritableRaster = writableRaster;
    }

    /**
     * Create an appropriate iterator to read and write in a raster sub area.
     *
     * @param raster         raster which is followed by read-only iterator.
     * @param writableRaster raster which is followed by this write-only iterator and wherein value is writing.
     * @param subArea        Rectangle which represent raster sub area, wherein move read and write iterator.
     * @throws IllegalArgumentException if raster and writable raster dimensions are not conform.
     */
    public DefaultWritableDirectFloatIterator(final Raster raster, final WritableRaster writableRaster, final Rectangle subArea) {
        super(raster, subArea);
        checkRasters(raster, writableRaster);
        this.currentWritableRaster = writableRaster;
    }

    /**
     * Create an appropriate iterator to read and write in a rendered image tiles by tiles.
     *
     * @param renderedImage rendered image which is followed by read-only iterator.
     * @param writableRI    writable rendered image which is followed by this write-only iterator and wherein value is writing.
     * @throws IllegalArgumentException if renderedImage and writable renderedImage dimensions are not conform.
     * @throws IllegalArgumentException if renderedImage and writable renderedImage tiles configurations are not conform.
     */
    public DefaultWritableDirectFloatIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRI) {
        super(renderedImage);
        checkRenderedImage(renderedImage, writableRI);
        this.writableRenderedImage = writableRI;
    }

    /**
     * Create an appropriate iterator to read and write in a rendered image sub area, tiles by tiles.
     *
     * @param renderedImage rendered image which is followed by read-only iterator.
     * @param writableRI    writable rendered image which is followed by this write-only iterator and wherein value is writing.
     * @param subArea       Rectangle which represent rendered image sub area, wherein move read and write iterator.
     * @throws IllegalArgumentException if renderedImage and writable renderedImage dimensions are not conform.
     * @throws IllegalArgumentException if renderedImage and writable renderedImage tiles configurations are not conform.
     */
    public DefaultWritableDirectFloatIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRI, final Rectangle subArea) {
        super(renderedImage, subArea);
        checkRenderedImage(renderedImage, writableRI);
        this.writableRenderedImage = writableRI;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void updateCurrentRaster(int tileX, int tileY) {
        super.updateCurrentRaster(tileX, tileY);
        if (currentWritableRaster != null) writableRenderedImage.releaseWritableTile(prectX, prectY);
        this.currentWritableRaster    = writableRenderedImage.getWritableTile(tileX, tileY);
        this.currentWritableRaster    = writableRenderedImage.getWritableTile(tileX, tileY);
        this.currentWritableDataArray = ((DataBufferFloat) currentWritableRaster.getDataBuffer()).getBankData();
        this.prectX = tileX;
        this.prectY = tileY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void close() {
        writableRenderedImage.releaseWritableTile(prectX, prectY);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSample(int value) {
        currentWritableDataArray[band][dataCursor] = (float) value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleDouble(double value) {
        currentWritableDataArray[band][dataCursor] = (float) value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleFloat(float value) {
        currentWritableDataArray[band][dataCursor] = value;
    }
}
