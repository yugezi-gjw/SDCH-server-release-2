package com.varian.oiscn.core.participant;

import com.varian.oiscn.core.activity.ActivityCodeEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by fmk9441 on 2017-03-09.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ActivityCodeEnum.class})
public class ActivityCodeEnumTest {
    @Test
    public void givenAnActivityTypeEnumWhenGetDisplayThenReturnActivityTypeString() {
        Assert.assertEquals("Create Physician Order Task", ActivityCodeEnum.getDisplay(ActivityCodeEnum.IMMOBILIZATION_ORDER));
        Assert.assertEquals("Consult", ActivityCodeEnum.getDisplay(ActivityCodeEnum.IMMOBILIZATION_APPOINTMENT));
        Assert.assertEquals("Obtain Imaging Studies", ActivityCodeEnum.getDisplay(ActivityCodeEnum.CT_SIMULATION_ORDER));
        Assert.assertEquals("Re-Simulation", ActivityCodeEnum.getDisplay(ActivityCodeEnum.CT_SIMULATION_APPOINTMENT));

        Assert.assertEquals("ImportCTImage", ActivityCodeEnum.getDisplay(ActivityCodeEnum.IMPORT_CT_IMAGE));
        Assert.assertEquals("TargetContouring", ActivityCodeEnum.getDisplay(ActivityCodeEnum.TARGET_CONTOURING));
        Assert.assertEquals("ContouringApproval", ActivityCodeEnum.getDisplay(ActivityCodeEnum.CONTOURING_APPROVAL));
    }

    @Test
    public void givenAnActivityTypeStringWhenFromCodeThenReturnActivityTypeEnum() {
        Assert.assertEquals(ActivityCodeEnum.IMMOBILIZATION_ORDER, ActivityCodeEnum.fromCode("Create Physician Order Task"));
        Assert.assertEquals(ActivityCodeEnum.IMMOBILIZATION_APPOINTMENT, ActivityCodeEnum.fromCode("Consult"));
        Assert.assertEquals(ActivityCodeEnum.CT_SIMULATION_ORDER, ActivityCodeEnum.fromCode("Obtain Imaging Studies"));
        Assert.assertEquals(ActivityCodeEnum.CT_SIMULATION_APPOINTMENT, ActivityCodeEnum.fromCode("Re-Simulation"));

        Assert.assertEquals(ActivityCodeEnum.IMPORT_CT_IMAGE, ActivityCodeEnum.fromCode("ImportCTImage"));
        Assert.assertEquals(ActivityCodeEnum.TARGET_CONTOURING, ActivityCodeEnum.fromCode("TargetContouring"));
        Assert.assertEquals(ActivityCodeEnum.CONTOURING_APPROVAL, ActivityCodeEnum.fromCode("ContouringApproval"));
    }
}
