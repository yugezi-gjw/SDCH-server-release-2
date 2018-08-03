package com.varian.oiscn.core.patient;

import org.eclipse.jetty.util.StringUtil;

/**
 * Created by gbt1220 on 3/28/2017.
 * <p>
 * This value set has an inline code system http://hl7.org/fhir/patient-contact-relationship,
 * which defines the following codes:<br /></p>
 * <p><ul><li>emergency Contact for use in case of emergency.</li><br /></p>
 * <p><li>family</li><br /></p>
 * <p><li>guardian</li><br /></p>
 * <p><li>friend</li><br /></p>
 * <p><li>partner</li><br /></p>
 * <p><li>work Contact for matters related to the patients occupation/employment..</li><br /></p>
 * <p><li>caregiver (Non)professional caregiver</li><br /></p>
 * <p><li>agent Contact that acts on behalf of the patient</li><br /></p>
 * <p><li>guarantor Contact for financial matters</li><br /></p>
 * <p><li>owner For animals, the owner of the animal</li><br /></p>
 * <p><li>parent Parent of the patient</li><br /></p>
 * <p></ul><br /></p>
 */
public enum RelationshipEnum {
    EMERGENCY, FAMILY, GUARDIAN, FRIEND, PARTNER, WORK, CAREGIVER, AGENT, GUARANTOR, OWNER, PARENT;

    public static RelationshipEnum fromString(String contactRelationship) {
        if (StringUtil.isBlank(contactRelationship)) {
            return EMERGENCY;
        }
        RelationshipEnum ret;
        switch (contactRelationship.toUpperCase()) {
            case "EMERGENCY":
                ret = EMERGENCY;
                break;
            case "FAMILY":
                ret = FAMILY;
                break;
            case "GUARDIAN":
                ret = GUARDIAN;
                break;
            case "FRIEND":
                ret = FRIEND;
                break;
            case "PARTNER":
                ret = PARTNER;
                break;
            case "WORK":
                ret = WORK;
                break;
            case "CAREGIVER":
                ret = CAREGIVER;
                break;
            case "AGENT":
                ret = AGENT;
                break;
            case "GUARANTOR":
                ret = GUARANTOR;
                break;
            case "OWNER":
                ret = OWNER;
                break;
            case "PARENT":
                ret = PARENT;
                break;
            default:
                ret = EMERGENCY;
        }
        return ret;
    }
}
