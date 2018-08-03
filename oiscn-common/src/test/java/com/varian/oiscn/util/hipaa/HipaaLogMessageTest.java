package com.varian.oiscn.util.hipaa;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class HipaaLogMessageTest {

    @Test
    public void testToString() {
        HipaaLogMessage msg = new HipaaLogMessage();
        msg.setApplicationId("applicationId");
        msg.setComment("comment");
        msg.setEvent(HipaaEvent.Other);
        msg.setObjectId("objectId");
        msg.setObjectType(HipaaObjectType.Other);
        msg.setPatientId("patientId");
        msg.setPatientName("patientName");
        msg.setTime(new Date());
        msg.setUserId("userId");
        
        Assert.assertNotNull(msg.toString());
        Assert.assertTrue(msg.toString().length() > 0);
    }

}
