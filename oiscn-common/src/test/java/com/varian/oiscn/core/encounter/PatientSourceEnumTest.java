package com.varian.oiscn.core.encounter;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fmk9441 on 2017-03-30.
 */
public class PatientSourceEnumTest {
    @Test
    public void givenPatientSourceEnumWhenGetDisplayThenReturnPatientSource() {
        Assert.assertEquals("Inpatient", PatientSourceEnum.getDisplay(PatientSourceEnum.I));
        Assert.assertEquals("Outpatient", PatientSourceEnum.getDisplay(PatientSourceEnum.O));
        Assert.assertEquals("Referral", PatientSourceEnum.getDisplay(PatientSourceEnum.R));
        Assert.assertEquals("Emergency", PatientSourceEnum.getDisplay(PatientSourceEnum.E));
    }

    @Test
    public void givenAPatientSourceWhenFromCodeThenReturnPatientSourceEnum() {
        Assert.assertEquals(PatientSourceEnum.I, PatientSourceEnum.fromCode("Inpatient"));
        Assert.assertEquals(PatientSourceEnum.O, PatientSourceEnum.fromCode("Outpatient"));
        Assert.assertEquals(PatientSourceEnum.R, PatientSourceEnum.fromCode("Referral"));
        Assert.assertEquals(PatientSourceEnum.E, PatientSourceEnum.fromCode("Emergency"));
    }
}