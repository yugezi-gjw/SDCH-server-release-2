
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
 *         &lt;element name="secToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "secToken"
})
@XmlRootElement(name = "ValidateToken")
public class ValidateToken {

    @XmlElementRef(name = "secToken", namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> secToken;

    /**
     * Gets the value of the secToken property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getSecToken() {
        return secToken;
    }

    /**
     * Sets the value of the secToken property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setSecToken(JAXBElement<String> value) {
        this.secToken = value;
    }

}
