package com.varian.oiscn.core.patient;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gbt1220 on 4/5/2017.
 */
public enum StagingEnum {
    STAGE_I,
    STAGE_II,
    STAGE_III,
    STAGE_IV,
    ERROR_CODE;

    public static StagingEnum fromCode(String staging) {
        if (StringUtils.equalsIgnoreCase(staging, "I")) {
            return STAGE_I;
        } else if (StringUtils.equalsIgnoreCase(staging, "II")) {
            return STAGE_II;
        } else if (StringUtils.equalsIgnoreCase(staging, "III")) {
            return STAGE_III;
        } else if (StringUtils.equalsIgnoreCase(staging, "IV")) {
            return STAGE_IV;
        } else {
            return ERROR_CODE;
        }
    }

    public static String getDisplay(StagingEnum staging) {
        if (staging == null) {
            return StringUtils.EMPTY;
        }
        switch (staging) {
            case STAGE_I:
                return "I";
            case STAGE_II:
                return "II";
            case STAGE_III:
                return "III";
            default:
                return "IV";
        }
    }
}
