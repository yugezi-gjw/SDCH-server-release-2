package com.varian.oiscn.appointment.calling;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by bhp9696 on 2018/3/14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CallingServiceVO.class)
public class CallingServiceVOTest {
    private CallingServiceVO callingServiceVO = new CallingServiceVO();
    @Test
    public void testMethod(){
        callingServiceVO.addPatient("pname");
        Assert.assertTrue("pname".equals(callingServiceVO.getPatients().get(0)));
        callingServiceVO.addPatientList(Arrays.asList("pname1","pname2"));
        Assert.assertTrue("pname2".equals(callingServiceVO.getPatients().get(2)));

        callingServiceVO.addText("text");
        callingServiceVO.addVideo("video");
        callingServiceVO.addImage("img");

        Map<String,Object> map = callingServiceVO.toMap();
        Assert.assertNotNull(map);
        Assert.assertFalse(map.isEmpty());
    }
}
