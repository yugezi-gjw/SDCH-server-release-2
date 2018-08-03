package com.varian.oiscn.core.encounter;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by fmk9441 on 2017-03-30.
 */
public enum PatientSourceEnum {
    I,
    O,
    R,
    E;

    /**
     * Get the display name of patient source enumeration
     *
     * @param patientSource enumeration value
     * @return display name of the given patient source enumeration
     */
    public static String getDisplay(PatientSourceEnum patientSource) {
        switch (patientSource) {
            case I:
                return "Inpatient";
            case O:
                return "Outpatient";
            case R:
                return "Referral";
            default:
                return "Emergency";
        }
    }

    /**
     * Get the enumeration from the given string
     *
     * @param patientSource given string
     * @return enumeration value of patient source
     */
    public static PatientSourceEnum fromCode(String patientSource) {
        if (StringUtils.equalsIgnoreCase(patientSource, "Inpatient")) {
            return PatientSourceEnum.I;
        }
        if (StringUtils.equalsIgnoreCase(patientSource, "Outpatient")) {
            return PatientSourceEnum.O;
        }
        if (StringUtils.equalsIgnoreCase(patientSource, "Referral")) {
            return PatientSourceEnum.R;
        }
        if (StringUtils.equalsIgnoreCase(patientSource, "Emergency")) {
            return PatientSourceEnum.E;
        }

        return PatientSourceEnum.E;
    }
}