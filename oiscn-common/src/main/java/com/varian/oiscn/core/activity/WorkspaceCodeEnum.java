package com.varian.oiscn.core.activity;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gbt1220 on 5/17/2017.
 */
public enum WorkspaceCodeEnum {
    DYNAMIC_FORM,
    SCHEDULE_SINGLE,
    SCHEDULE_MULTIPLE,
    ECLIPSE_CREATE_TREATMENT_PLAN,
    ECLIPSE_CRITICAL_ORGAN_CONTOURING,
    ECLIPSE_TARGET_CONTOURING,
    ECLIPSE_CONTOURING_APPROVAL,
    ECLIPSE_TREATMENT_PLAN_REVIEW,
    ECLIPSE_TREATMENT_PLAN_APPROVE,
    ECLIPSE_IMPORT_CT_IMAGE;

    public static WorkspaceCodeEnum fromCode(String activityCode) {
        if (StringUtils.equalsIgnoreCase(activityCode, "DYNAMIC_FORM")) {
            return DYNAMIC_FORM;
        }
        if (StringUtils.equalsIgnoreCase(activityCode, "SCHEDULE_SINGLE")) {
            return SCHEDULE_SINGLE;
        }
        if (StringUtils.equalsIgnoreCase(activityCode, "SCHEDULE_MULTIPLE")) {
            return SCHEDULE_MULTIPLE;
        }
        if (StringUtils.equalsIgnoreCase(activityCode, "ECLIPSE_CREATE_TREATMENT_PLAN")) {
            return ECLIPSE_CREATE_TREATMENT_PLAN;
        }
        if (StringUtils.equalsIgnoreCase(activityCode, "ECLIPSE_CRITICAL_ORGAN_CONTOURING")) {
            return ECLIPSE_CRITICAL_ORGAN_CONTOURING;
        }
        if (StringUtils.equalsIgnoreCase(activityCode, "ECLIPSE_TARGET_CONTOURING")) {
            return ECLIPSE_TARGET_CONTOURING;
        }
        if (StringUtils.equalsIgnoreCase(activityCode, "ECLIPSE_CONTOURING_APPROVAL")) {
            return ECLIPSE_CONTOURING_APPROVAL;
        }
        if (StringUtils.equalsIgnoreCase(activityCode, "ECLIPSE_TREATMENT_PLAN_REVIEW")) {
            return ECLIPSE_TREATMENT_PLAN_REVIEW;
        }
        if (StringUtils.equalsIgnoreCase(activityCode, "ECLIPSE_TREATMENT_PLAN_APPROVE")) {
            return ECLIPSE_TREATMENT_PLAN_APPROVE;
        }
        return ECLIPSE_IMPORT_CT_IMAGE;
    }
}
