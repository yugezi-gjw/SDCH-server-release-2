package com.varian.oiscn.util.hipaa;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class HipaaEventTest {

    @Test
    public void givenValueThenReturnEnumType(){
        String value = "FailedLogin";
        HipaaEvent hipaaEvent = HipaaEvent.forValue(value);
        Assert.assertEquals(HipaaEvent.FailedLogin, hipaaEvent);
    }

    @Test
    public void givenWrongValueThenReturnOtherEnumType(){
        String value = "Wrong";
        HipaaEvent hipaaEvent = HipaaEvent.forValue(value);
        Assert.assertEquals(HipaaEvent.Other, hipaaEvent);
    }

    @Test
    public void convertedToStringType(){
        String value = "FailedLogin";
        HipaaEvent hipaaEvent = HipaaEvent.FailedLogin;
        Assert.assertEquals(value, hipaaEvent.toValue());
    }
}
