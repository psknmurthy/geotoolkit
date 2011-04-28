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
package org.geotoolkit.internal.jaxb.gco;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gmd.PT_FreeText;


/**
 * JAXB wrapper for string value in a {@code <gco:CharacterString>} or {@code <gmx:Anchor>} element,
 * for ISO-19139 compliance.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.13
 *
 * @see org.geotoolkit.internal.jaxb.gmd.PT_FreeText
 *
 * @since 2.5
 * @module
 */
@XmlSeeAlso(PT_FreeText.class)
public class GO_CharacterString {
    /**
     * The text or anchor value, or {@code null} if none. May be an instance
     * of {@link AnchorType}, which needs to be handled in a special way.
     */
    CharSequence text;

    /**
     * Empty constructor for JAXB only.
     */
    public GO_CharacterString() {
    }

    /**
     * Builds an adapter for the given text.
     *
     * @param text The string to marshall, or {@code null} if none.
     */
    public GO_CharacterString(final CharSequence text) {
        this.text = text;
    }

    /**
     * Returns the text. This method is called by JAXB at marshalling time.
     *
     * @return The text, or {@code null}.
     */
    @XmlElement(name = "CharacterString")
    public final String getCharacterString() {
        final CharSequence text = this.text;
        return (text == null || text instanceof AnchorType) ? null : text.toString();
    }

    /**
     * Sets the value to the given string. This method is called by JAXB at unmarshalling time.
     *
     * @param text The new text.
     */
    public final void setCharacterString(String text) {
        if (text != null) {
            text = text.trim();
            if (text.isEmpty()) {
                text = null;
            }
        }
        this.text = text;
    }

    /**
     * Returns the text associated with a reference.
     * This method is called by JAXB at marshalling time.
     *
     * @return The anchor, or {@code null}.
     */
    @XmlElement(name = "Anchor", namespace = Namespaces.GMX)
    public final AnchorType getAnchor() {
        final CharSequence text = this.text;
        return (text instanceof AnchorType) ? (AnchorType) text : null;
    }

    /**
     * Sets the value for the metadata string.
     * This method is called by JAXB at unmarshalling time.
     *
     * @param anchor The new anchor.
     */
    public final void setAnchor(final AnchorType anchor) {
        this.text = anchor;
    }

    /**
     * Returns the text as a string, or {@code null} if none.
     * The null value is expected by various {@code unmarshal} methods.
     *
     * {@note Returning <code>null</code> is unusual and not a recommended practice.
     * But precedents exist (for example Swing <code>DefaultMutableTreeNode</code>)
     * and this class is not for public usage.}
     */
    @Override
    public final String toString() {
        final CharSequence text = this.text;
        return (text != null) ? text.toString() : null; // NOSONAR: Really want to return null.
    }
}
