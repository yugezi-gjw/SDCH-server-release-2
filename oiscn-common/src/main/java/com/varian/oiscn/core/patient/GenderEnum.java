package com.varian.oiscn.core.patient;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gbt1220 on 3/28/2017.
 * <p>
 * "M":Male   "F":Female   "UN":Unknown "OT" Other
 */
public enum GenderEnum {
    M,
    F,
    UN,
    OT;

    public static String getDisplay(GenderEnum gender) {
        if (gender == null) {
            return "Unknown";
        }
        switch (gender) {
            case M:
                return "Male";
            case F:
                return "Female";
            case UN:
                return "Unknown";
            case OT:
                return "Other";
            default:
                return "Unknown";
        }
    }

    /**
     * Get the enum value from the activity type string
     *
     * @param gender is the given activity type string
     * @return enum of activity type
     */
    public static GenderEnum fromCode(String gender) {
        if (StringUtils.equalsIgnoreCase(gender, "Male")) {
            return GenderEnum.M;
        }
        if (StringUtils.equalsIgnoreCase(gender, "Female")) {
            return GenderEnum.F;
        }
        if (StringUtils.equalsIgnoreCase(gender, "Unknown")) {
            return GenderEnum.UN;
        }
        if (StringUtils.equalsIgnoreCase(gender, "Other")) {
            return GenderEnum.OT;
        }

        return GenderEnum.UN;
    }
}
