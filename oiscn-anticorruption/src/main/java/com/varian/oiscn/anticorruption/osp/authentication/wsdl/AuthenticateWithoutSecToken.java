
package com.varian.oiscn.anticorruption.osp.authentication.wsdl;

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
 *         &lt;element name="userID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "userID",
        "password"
})
@XmlRootElement(name = "AuthenticateWithoutSecToken")
public class AuthenticateWithoutSecToken {

    @XmlElementRef(name = "userID", namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> userID;
    @XmlElementRef(name = "password", namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> password;

    /**
     * Gets the value of the userID property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getUserID() {
        return userID;
    }

    /**
     * Sets the value of the userID property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setUserID(JAXBElement<String> value) {
        this.userID = value;
    }

    /**
     * Gets the value of the password property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setPassword(JAXBElement<String> value) {
        this.password = value;
    }

}
