package com.varian.oiscn.anticorruption.converter;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Reference;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by fmk9441 on 2017-05-11.
 */
public class DataHelper {
    private DataHelper() {

    }

    /**
     * Return Reference String from Fhir Reference.<br>
     *
     * @param reference Fhir Reference
     * @return Reference String
     */
    public static String getReferenceType(Reference reference) {
        String value = StringUtils.EMPTY;
        if (reference != null && reference.hasReference()) {
            if (reference.getReference().indexOf("/") > 0) {
                value = reference.getReference().substring(0, reference.getReference().indexOf("/")).replace("#", "").replace("/", "");
            }
        }
        return value;
    }

    /**
     * Return Reference Value String from Fhir Reference.<br>
     * @param reference Fhir Reference
     * @return Reference Value String
     */
    public static String getReferenceValue(Reference reference) {
        String value = StringUtils.EMPTY;
        if (null != reference && reference.hasReference()) {
            value = reference.getReference();
            if (isNotBlank(value)) {
                if (value.contains("/")) {
                    value = value.substring(value.indexOf("/"), value.length());
                }
                value = value.replace("#", "").replace("/", "");
            }
        }

        return value;
    }

    /**
     * Return Reference Value String by value String.<br>
     * @param value Reference Value
     * @return result
     */
    public static String getReferenceValue(String value) {
        String result = value;
        if (isNotBlank(value)) {
            if (value.contains("/")) {
                result = value.substring(value.indexOf("/"), value.length());
            }
            result = result.replace("#", "").replace("/", "");
        }

        return result;
    }

    /**
     * Return Fhir Reference by Id, Display, Resource Name, and contained.<br>
     * @param id Id
     * @param display Display
     * @param resourceName Resource Name
     * @param contained Contained
     * @return Fhir Reference
     */
    public static Reference getReference(String id, String display, String resourceName, boolean contained) {
        String t = contained ? (StringUtils.startsWith(id, "#") ? id : ("#" + resourceName + "/" + id)) : resourceName + "/" + id;
        Reference reference = new Reference().setReference(t).setDisplay(display);
        reference.setId(t);
        return reference;
    }
}