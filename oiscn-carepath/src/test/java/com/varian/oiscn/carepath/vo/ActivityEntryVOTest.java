package com.varian.oiscn.carepath.vo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ActivityEntryVOTest {

    @Test
    public void test(){
        String activityCode = "activityCode";
        String activityType = "activityType";
        String activityName = "activityName";
        ActivityEntryVO activityEntryVO = new ActivityEntryVO(activityCode, activityType, activityName);
        activityEntryVO.setActivityCode(activityCode);
        activityEntryVO.setActivityType(activityType);
        activityEntryVO.setActivityName(activityName);
        Assert.assertEquals(activityCode, activityEntryVO.getActivityCode());
        Assert.assertEquals(activityType, activityEntryVO.getActivityType());
        Assert.assertEquals(activityName, activityEntryVO.getActivityName());
    }
}
