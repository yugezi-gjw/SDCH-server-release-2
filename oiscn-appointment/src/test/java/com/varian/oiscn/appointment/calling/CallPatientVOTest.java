package com.varian.oiscn.appointment.calling;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bhp9696 on 2018/3/14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CallPatientVO.class)
public class CallPatientVOTest {

    @Test
    public void testGetAppointmentId(){
        CallPatientVO vo = new CallPatientVO(){{
            setAppointmentId("1212");
            setPatientName(Arrays.asList("a","b"));
        }};
        String id = vo.getAppointmentId();
        List<String> names = vo.getPatientName();
        Assert.assertTrue(id.equals("1212"));
        Assert.assertTrue(names.get(0).equals("a"));
    }
}
