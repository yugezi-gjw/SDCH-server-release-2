package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by gbt1220 on 7/28/2017.
 */
public class ActivityInstanceLastModifiedDtComparatorTest {

    private ActivityInstanceLastModifiedDtComparator comparator;

    @Before
    public void setup() {
        comparator = new ActivityInstanceLastModifiedDtComparator();
    }

    @Test
    public void givenWhenFirstActivityInstanceIsNullThenReturn1() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        List<ActivityInstance> activityInstances = instance.getActivityInstances();
        activityInstances.get(0).setLastModifiedDT(null);
        Assert.assertEquals(1, comparator.compare(activityInstances.get(0), activityInstances.get(1)));
    }

    @Test
    public void givenWhenSecondActivityInstanceIsNullThenReturnNegative1() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        List<ActivityInstance> activityInstances = instance.getActivityInstances();
        activityInstances.get(0).setLastModifiedDT(new Date());
        activityInstances.get(1).setLastModifiedDT(null);
        Assert.assertEquals(-1, comparator.compare(activityInstances.get(0), activityInstances.get(1)));
    }

    @Test
    public void givenTwoActivityInstancesWhenCompareThenReturn() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        List<ActivityInstance> activityInstances = instance.getActivityInstances();
        try {
            activityInstances.get(0).setLastModifiedDT(DateUtil.parse("2017-07-01"));
            activityInstances.get(1).setLastModifiedDT(DateUtil.parse("2017-01-02"));
        } catch (ParseException e) {
            Assert.fail();
        }
        Assert.assertEquals(-1, comparator.compare(activityInstances.get(0), activityInstances.get(1)));
    }
}
