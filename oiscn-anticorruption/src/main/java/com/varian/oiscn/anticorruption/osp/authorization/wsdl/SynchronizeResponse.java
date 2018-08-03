
package com.varian.oiscn.anticorruption.osp.authorization.wsdl;

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
 *         &lt;element name="SynchronizeResult" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "synchronizeResult"
})
@XmlRootElement(name = "SynchronizeResponse")
public class SynchronizeResponse {

    @XmlElement(name = "SynchronizeResult")
    protected Boolean synchronizeResult;

    /**
     * Gets the value of the synchronizeResult property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isSynchronizeResult() {
        return synchronizeResult;
    }

    /**
     * Sets the value of the synchronizeResult property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setSynchronizeResult(Boolean value) {
        this.synchronizeResult = value;
    }

}
