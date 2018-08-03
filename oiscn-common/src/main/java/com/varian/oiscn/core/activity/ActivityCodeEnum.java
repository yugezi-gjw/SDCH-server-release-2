package com.varian.oiscn.core.activity;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by fmk9441 on 2017-03-09.
 */
public enum ActivityCodeEnum {
    IMMOBILIZATION_ORDER,
    IMMOBILIZATION_APPOINTMENT,
    CT_SIMULATION_ORDER,
    CT_SIMULATION_APPOINTMENT,

    IMPORT_CT_IMAGE,
    TARGET_CONTOURING,
    CONTOURING_APPROVAL;

    /**
     * Get the display name from the enum of activity type
     *
     * @param activityType is the given enum of activity type
     * @return real display name of activity type
     */
    public static String getDisplay(ActivityCodeEnum activityType) {
        switch (activityType) {
            case IMMOBILIZATION_ORDER:
                return "Create Physician Order Task";
            case IMMOBILIZATION_APPOINTMENT:
                return "Consult";
            case CT_SIMULATION_ORDER:
                return "Obtain Imaging Studies";
            case CT_SIMULATION_APPOINTMENT:
                return "Re-Simulation";

            case IMPORT_CT_IMAGE:
                return "ImportCTImage";
            case TARGET_CONTOURING:
                return "TargetContouring";
            case CONTOURING_APPROVAL:
                return "ContouringApproval";
            default:
                return "Create Physician Order Task";
        }
    }

    /**
     * Get the enum value from the activity type string
     *
     * @param activityType is the given activity type string
     * @return enum of activity type
     */
    public static ActivityCodeEnum fromCode(String activityType) {
        if (StringUtils.equalsIgnoreCase(activityType, "Create Physician Order Task")) {
            return ActivityCodeEnum.IMMOBILIZATION_ORDER;
        }
        if (StringUtils.equalsIgnoreCase(activityType, "Consult")) {
            return ActivityCodeEnum.IMMOBILIZATION_APPOINTMENT;
        }
        if (StringUtils.equalsIgnoreCase(activityType, "Obtain Imaging Studies")) {
            return ActivityCodeEnum.CT_SIMULATION_ORDER;
        }
        if (StringUtils.equalsIgnoreCase(activityType, "Re-Simulation")) {
            return ActivityCodeEnum.CT_SIMULATION_APPOINTMENT;
        }

        if (StringUtils.equalsIgnoreCase(activityType, "ImportCTImage")) {
            return ActivityCodeEnum.IMPORT_CT_IMAGE;
        }
        if (StringUtils.equalsIgnoreCase(activityType, "TargetContouring")) {
            return ActivityCodeEnum.TARGET_CONTOURING;
        }
        if (StringUtils.equalsIgnoreCase(activityType, "ContouringApproval")) {
            return ActivityCodeEnum.CONTOURING_APPROVAL;
        }

        return ActivityCodeEnum.IMMOBILIZATION_ORDER;
    }
}