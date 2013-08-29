/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.2-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.08 at 11:22:32 AM EET 
//

package org.exoplatform.gwtframework.commons.wadl;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://research.sun.com/wadl/2006/10}doc" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://research.sun.com/wadl/2006/10}grammars" minOccurs="0"/>
 *         &lt;element ref="{http://research.sun.com/wadl/2006/10}resources" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://research.sun.com/wadl/2006/10}resource_type"/>
 *           &lt;element ref="{http://research.sun.com/wadl/2006/10}method"/>
 *           &lt;element ref="{http://research.sun.com/wadl/2006/10}representation"/>
 *           &lt;element ref="{http://research.sun.com/wadl/2006/10}fault"/>
 *         &lt;/choice>
 *         &lt;any/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */

public class WadlApplication {

    protected List<Doc> doc;

    protected Grammars grammars;

    protected Resources resources;

    protected List<Object> resourceTypeOrMethodOrRepresentation;

    protected List<Object> any;

    /**
     * Gets the value of the doc property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the doc property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDoc().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Doc }
     */
    public List<Doc> getDoc() {
        if (doc == null) {
            doc = new ArrayList<Doc>();
        }
        return this.doc;
    }

    /**
     * Gets the value of the grammars property.
     *
     * @return possible object is
     *         {@link Grammars }
     */
    public Grammars getGrammars() {
        return grammars;
    }

    /**
     * Sets the value of the grammars property.
     *
     * @param value
     *         allowed object is
     *         {@link Grammars }
     */
    public void setGrammars(Grammars value) {
        this.grammars = value;
    }

    /**
     * Gets the value of the resources property.
     *
     * @return possible object is
     *         {@link Resources }
     */
    public Resources getResources() {
        return resources;
    }

    /**
     * Sets the value of the resources property.
     *
     * @param value
     *         allowed object is
     *         {@link Resources }
     */
    public void setResources(Resources value) {
        this.resources = value;
    }

    /**
     * Gets the value of the resourceTypeOrMethodOrRepresentation property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceTypeOrMethodOrRepresentation property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceTypeOrMethodOrRepresentation().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceType }
     * {@link Method }
     * {@link JAXBElement }{@code <}{@link RepresentationType }{@code >}
     * {@link JAXBElement }{@code <}{@link RepresentationType }{@code >}
     */
    public List<Object> getResourceTypeOrMethodOrRepresentation() {
        if (resourceTypeOrMethodOrRepresentation == null) {
            resourceTypeOrMethodOrRepresentation = new ArrayList<Object>();
        }
        return this.resourceTypeOrMethodOrRepresentation;
    }

    /**
     * Gets the value of the any property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}