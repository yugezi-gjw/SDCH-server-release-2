
package com.varian.oiscn.anticorruption.osp.authorization.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for ServiceFault complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="ServiceFault">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Reason" type="{http://schemas.varian.com/Foundation/Platform/Shared/ReasonCode/2011/4/}ReasonCode" minOccurs="0"/>
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceFault", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", propOrder = {
        "header",
        "reason",
        "message"
})
public class ServiceFault {

    @XmlElementRef(name = "Header", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> header;
    @XmlElement(name = "Reason")
    @XmlSchemaType(name = "string")
    protected ReasonCode reason;
    @XmlElementRef(name = "Message", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> message;

    /**
     * Gets the value of the header property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setHeader(JAXBElement<String> value) {
        this.header = value;
    }

    /**
     * Gets the value of the reason property.
     *
     * @return possible object is
     * {@link ReasonCode }
     */
    public ReasonCode getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     *
     * @param value allowed object is
     *              {@link ReasonCode }
     */
    public void setReason(ReasonCode value) {
        this.reason = value;
    }

    /**
     * Gets the value of the message property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public JAXBElement<String> getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     *
     * @param value allowed object is
     *              {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    public void setMessage(JAXBElement<String> value) {
        this.message = value;
    }

}
