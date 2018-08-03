
package com.varian.oiscn.anticorruption.osp.authorization.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfboolean complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="ArrayOfboolean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="boolean" type="{http://www.w3.org/2001/XMLSchema}boolean" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfboolean", namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", propOrder = {
        "_boolean"
})
public class ArrayOfboolean {

    @XmlElement(name = "boolean", type = Boolean.class)
    protected List<Boolean> _boolean;

    /**
     * Gets the value of the boolean property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boolean property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoolean().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Boolean }
     */
    public List<Boolean> getBoolean() {
        if (_boolean == null) {
            _boolean = new ArrayList<Boolean>();
        }
        return this._boolean;
    }

}
