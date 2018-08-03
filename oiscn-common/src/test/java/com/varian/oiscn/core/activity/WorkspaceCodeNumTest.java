package com.varian.oiscn.core.activity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gbt1220 on 5/18/2017.
 */
public class WorkspaceCodeNumTest {
    @Test
    public void givenStringWhenFromCodeThenReturnTheEnum() {
        Assert.assertEquals(WorkspaceCodeEnum.DYNAMIC_FORM, WorkspaceCodeEnum.fromCode("DYNAMIC_FORM"));
        Assert.assertEquals(WorkspaceCodeEnum.ECLIPSE_CONTOURING_APPROVAL, WorkspaceCodeEnum.fromCode("ECLIPSE_CONTOURING_APPROVAL"));
        Assert.assertEquals(WorkspaceCodeEnum.ECLIPSE_CREATE_TREATMENT_PLAN, WorkspaceCodeEnum.fromCode("ECLIPSE_CREATE_TREATMENT_PLAN"));
        Assert.assertEquals(WorkspaceCodeEnum.ECLIPSE_CRITICAL_ORGAN_CONTOURING, WorkspaceCodeEnum.fromCode("ECLIPSE_CRITICAL_ORGAN_CONTOURING"));
        Assert.assertEquals(WorkspaceCodeEnum.ECLIPSE_IMPORT_CT_IMAGE, WorkspaceCodeEnum.fromCode("ECLIPSE_IMPORT_CT_IMAGE"));
        Assert.assertEquals(WorkspaceCodeEnum.ECLIPSE_TARGET_CONTOURING, WorkspaceCodeEnum.fromCode("ECLIPSE_TARGET_CONTOURING"));
        Assert.assertEquals(WorkspaceCodeEnum.ECLIPSE_TREATMENT_PLAN_APPROVE, WorkspaceCodeEnum.fromCode("ECLIPSE_TREATMENT_PLAN_APPROVE"));
        Assert.assertEquals(WorkspaceCodeEnum.ECLIPSE_TREATMENT_PLAN_REVIEW, WorkspaceCodeEnum.fromCode("ECLIPSE_TREATMENT_PLAN_REVIEW"));
        Assert.assertEquals(WorkspaceCodeEnum.SCHEDULE_MULTIPLE, WorkspaceCodeEnum.fromCode("SCHEDULE_MULTIPLE"));
        Assert.assertEquals(WorkspaceCodeEnum.SCHEDULE_SINGLE, WorkspaceCodeEnum.fromCode("SCHEDULE_SINGLE"));
    }
}
