package com.varian.oiscn.appointment.calling;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by bhp9696 on 2018/3/14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CallingServiceException.class)
public class CallingServiceExceptionTest {
    private CallingServiceException exception;

    @Test
    public void testMethod(){
        exception = new CallingServiceException(ServerStatusEnum.BAD_REQUEST);
        Assert.assertTrue(exception.getStatus().equals(ServerStatusEnum.BAD_REQUEST));
        exception.addBadItem("baditem");
        Assert.assertTrue("baditem".equals(exception.getBadItemList().get(0)));
        String msg = exception.getMessage();
        Assert.assertNotNull(msg);
    }
}
