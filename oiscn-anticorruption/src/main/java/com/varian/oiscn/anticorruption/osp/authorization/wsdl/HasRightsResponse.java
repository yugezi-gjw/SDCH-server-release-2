
package com.varian.oiscn.anticorruption.osp.authorization.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HasRightsResult" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfboolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "hasRightsResult"
})
@XmlRootElement(name = "HasRightsResponse")
public class HasRightsResponse {

    @XmlElementRef(name = "HasRightsResult", namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfboolean> hasRightsResult;

    /**
     * Gets the value of the hasRightsResult property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link ArrayOfboolean }{@code >}
     */
    public JAXBElement<ArrayOfboolean> getHasRightsResult() {
        return hasRightsResult;
    }

    /**
     * Sets the value of the hasRightsResult property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link ArrayOfboolean }{@code >}
     */
    public void setHasRightsResult(JAXBElement<ArrayOfboolean> value) {
        this.hasRightsResult = value;
    }

}
