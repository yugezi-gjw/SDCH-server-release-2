
package com.varian.oiscn.anticorruption.osp.authorization.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for UserInfo complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="UserInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserCUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LanguageID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserGroupID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SecToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SecTokenExpPeriod" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserInfo", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", propOrder = {
        "userID",
        "userName",
        "userCUID",
        "languageID",
        "userGroupID",
        "secToken",
        "secTokenExpPeriod"
})
public class UserInfo {

    @XmlElementRef(name = "UserID", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> userID;
    @XmlElementRef(name = "UserName", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> userName;
    @XmlElementRef(name = "UserCUID", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> userCUID;
    @XmlElementRef(name = "LanguageID", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> languageID;
    @XmlElementRef(name = "UserGroupID", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> userGroupID;
    @XmlElementRef(name = "SecToken", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> secToken;
    @XmlElement(name = "SecTokenExpPeriod")
    protected Long secTokenExpPeriod;

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
     * Gets the value of the userName property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setUserName(JAXBElement<String> value) {
        this.userName = value;
    }

    /**
     * Gets the value of the userCUID property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getUserCUID() {
        return userCUID;
    }

    /**
     * Sets the value of the userCUID property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setUserCUID(JAXBElement<String> value) {
        this.userCUID = value;
    }

    /**
     * Gets the value of the languageID property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getLanguageID() {
        return languageID;
    }

    /**
     * Sets the value of the languageID property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setLanguageID(JAXBElement<String> value) {
        this.languageID = value;
    }

    /**
     * Gets the value of the userGroupID property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getUserGroupID() {
        return userGroupID;
    }

    /**
     * Sets the value of the userGroupID property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setUserGroupID(JAXBElement<String> value) {
        this.userGroupID = value;
    }

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
     * Gets the value of the secTokenExpPeriod property.
     *
     * @return possible object is
     * {@link Long }
     */
    public Long getSecTokenExpPeriod() {
        return secTokenExpPeriod;
    }

    /**
     * Sets the value of the secTokenExpPeriod property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setSecTokenExpPeriod(Long value) {
        this.secTokenExpPeriod = value;
    }

}
