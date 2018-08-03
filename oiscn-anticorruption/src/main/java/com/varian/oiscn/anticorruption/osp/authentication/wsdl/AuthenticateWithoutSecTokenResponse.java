
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
 *         &lt;element name="AuthenticateWithoutSecTokenResult" type="{http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/}UserInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "authenticateWithoutSecTokenResult"
})
@XmlRootElement(name = "AuthenticateWithoutSecTokenResponse")
public class AuthenticateWithoutSecTokenResponse {

    @XmlElementRef(name = "AuthenticateWithoutSecTokenResult", namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<UserInfo> authenticateWithoutSecTokenResult;

    /**
     * Gets the value of the authenticateWithoutSecTokenResult property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link UserInfo }{@code >}
     */
    public JAXBElement<UserInfo> getAuthenticateWithoutSecTokenResult() {
        return authenticateWithoutSecTokenResult;
    }

    /**
     * Sets the value of the authenticateWithoutSecTokenResult property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link UserInfo }{@code >}
     */
    public void setAuthenticateWithoutSecTokenResult(JAXBElement<UserInfo> value) {
        this.authenticateWithoutSecTokenResult = value;
    }

}
