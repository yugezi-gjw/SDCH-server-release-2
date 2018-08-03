
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
 *         &lt;element name="strPrivilegeid" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "secToken",
        "strPrivilegeid"
})
@XmlRootElement(name = "HasRights")
public class HasRights {

    @XmlElementRef(name = "secToken", namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> secToken;
    @XmlElementRef(name = "strPrivilegeid", namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> strPrivilegeid;

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

    /**
     * Gets the value of the strPrivilegeid property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     */
    public JAXBElement<ArrayOfstring> getStrPrivilegeid() {
        return strPrivilegeid;
    }

    /**
     * Sets the value of the strPrivilegeid property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     */
    public void setStrPrivilegeid(JAXBElement<ArrayOfstring> value) {
        this.strPrivilegeid = value;
    }

}
