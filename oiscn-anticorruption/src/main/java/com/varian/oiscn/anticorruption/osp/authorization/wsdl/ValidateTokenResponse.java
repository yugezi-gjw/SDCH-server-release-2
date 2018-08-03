
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
 *         &lt;element name="ValidateTokenResult" type="{http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/}UserInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "validateTokenResult"
})
@XmlRootElement(name = "ValidateTokenResponse")
public class ValidateTokenResponse {

    @XmlElementRef(name = "ValidateTokenResult", namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<UserInfo> validateTokenResult;

    /**
     * Gets the value of the validateTokenResult property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link UserInfo }{@code >}
     */
    public JAXBElement<UserInfo> getValidateTokenResult() {
        return validateTokenResult;
    }

    /**
     * Sets the value of the validateTokenResult property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link UserInfo }{@code >}
     */
    public void setValidateTokenResult(JAXBElement<UserInfo> value) {
        this.validateTokenResult = value;
    }

}
