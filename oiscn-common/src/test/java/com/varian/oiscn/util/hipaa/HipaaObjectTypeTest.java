package com.varian.oiscn.util.hipaa;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class HipaaObjectTypeTest {
    @Test
    public void givenValueThenReturnEnumType(){
        String value = "Patient";
        HipaaObjectType hipaaObjectType = HipaaObjectType.forValue(value);
        Assert.assertEquals(HipaaObjectType.Patient, hipaaObjectType);
    }

    @Test
    public void givenWrongValueThenReturnOtherEnumType(){
        String value = "Wrong";
        HipaaObjectType hipaaObjectType = HipaaObjectType.forValue(value);
        Assert.assertEquals(HipaaObjectType.Other, hipaaObjectType);
    }

    @Test
    public void convertedToStringType(){
        String value = "Patient";
        HipaaObjectType hipaaObjectType = HipaaObjectType.Patient;
        Assert.assertEquals(value, hipaaObjectType.toValue());
    }
}
