package com.varian.oiscn.base.common;

import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static com.varian.oiscn.base.util.MockDtoUtil.givenACarePathInstance;

/**
 * Created by gbt1220 on 5/12/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CarePathInstanceHelper.class)
public class CarePathInstanceHelperTest {
    @Test
    public void givenACarePathInstanceWhenNotSuchElementInCarePathThenReturnNull() {
        CarePathInstance instance = givenACarePathInstance();
        Assert.assertNotNull(new CarePathInstanceHelper(instance).getPrevActivitiesByInstanceId("not in carepath id", ActivityTypeEnum.TASK.name()));
        Assert.assertNotNull(new CarePathInstanceHelper(instance).getNextActivitiesByInstanceId("not in carepath id", ActivityTypeEnum.TASK.name()));
        Assert.assertNotNull(new CarePathInstanceHelper(instance).getPrevActivities("not in carepath id"));
        Assert.assertNotNull(new CarePathInstanceHelper(instance).getNextActivities("not in carepath id"));
    }

    @Test
    public void givenACarePathInstanceWhenGetByInstanceIdThenReturnTheInstance() {
        CarePathInstance instance = givenACarePathInstance();
        Assert.assertEquals(instance.getActivityInstances().get(0), new CarePathInstanceHelper(instance).getPrevActivities("2").get(0));
        Assert.assertEquals(instance.getActivityInstances().get(1), new CarePathInstanceHelper(instance).getNextActivities("1").get(0));

        Assert.assertNotNull(new CarePathInstanceHelper(instance).getPrevActivitiesByInstanceId("1", ActivityTypeEnum.TASK.name()));
        Assert.assertEquals(instance.getActivityInstances().get(1), new CarePathInstanceHelper(instance).getNextActivitiesByInstanceId("1", ActivityTypeEnum.TASK.name()).get(0));
    }

    @Test
    public void givenACarePathWhenDoneFirstActivityThenReturnTrue() {
        CarePathInstance instance = givenACarePathInstance();
        CarePathInstanceHelper helper = new CarePathInstanceHelper(instance);
        Assert.assertTrue(helper.isAllPreActivitiesDoneOfEachNextActivity("1", instance.getActivityInstances().get(1)));
    }

    @Test
    public void givenACarePathWhenPreActivityHasNoInstanceIdThenReturnFalse() {
        CarePathInstance instance = givenSpecialCarePathInstance();
        instance.getActivityInstances().get(0).setInstanceID("");
        CarePathInstanceHelper helper = new CarePathInstanceHelper(instance);
        Assert.assertFalse(helper.isAllPreActivitiesDoneOfEachNextActivity("2", instance.getActivityInstances().get(3)));
    }

    @Test
    public void givenACarePathWhenPreTaskIsNotDoneThenReturnFalse() throws Exception {
        CarePathInstance instance = givenSpecialCarePathInstance();
        CarePathInstanceHelper helper = new CarePathInstanceHelper(instance);
        OrderAntiCorruptionServiceImp antiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(antiCorruptionServiceImp);
        PowerMockito.when(antiCorruptionServiceImp.queryOrderById("2")).thenReturn(MockDtoUtil.givenAnOrderDto());
        Assert.assertFalse(helper.isAllPreActivitiesDoneOfEachNextActivity("3", instance.getActivityInstances().get(3)));
    }

    @Test
    public void givenACarePathWhenPreAppointmentIsNotDoneThenReturnFalse() throws Exception {
        CarePathInstance instance = givenSpecialCarePathInstance();
        CarePathInstanceHelper helper = new CarePathInstanceHelper(instance);
        Assert.assertFalse(helper.isAllPreActivitiesDoneOfEachNextActivity("2", instance.getActivityInstances().get(3)));
    }

    @Test
    public void givenInstanceIdAndActivityTypeThenReturnActivityInstance(){
        CarePathInstance carePathInstance = givenSpecialCarePathInstance();
        CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
        ActivityInstance activityInstance = helper.getActivityByInstanceIdAndActivityType("1", "TASK");
        Assert.assertEquals("activityCode1", activityInstance.getActivityCode());
    }

    @Test
    public void givenActivityCodeThenReturnActivityInstance(){
        CarePathInstance carePathInstance = givenSpecialCarePathInstance();
        CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
        ActivityInstance activityInstance = helper.getActivityByCode("activityCode1");
        Assert.assertEquals("1", activityInstance.getId());
    }

    @Test
    public void testGetFirstActivity() {
        CarePathInstance carePathInstance = givenSpecialCarePathInstance();
        CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
        Assert.assertNotNull(helper.getFirstActivity());
    }

    @Test
    public void testGetPreActiveInWorkflowActivity() {
        CarePathInstance carePathInstance = givenSpecialCarePathInstance();
        CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
        Assert.assertNotNull(helper.getPreActiveInWorkflowActivity(carePathInstance.getActivityInstances().get(3)));
    }

    private CarePathInstance givenSpecialCarePathInstance() {
        CarePathInstance instance = new CarePathInstance();
        instance.setId("1");
        instance.setPatientID("patientID");

        ActivityInstance one = new ActivityInstance();
        one.setId("1");
        one.setActivityCode("activityCode1");
        one.setActivityType(ActivityTypeEnum.TASK);
        one.setDefaultGroupID("1");
        one.setDepartmentID("1");
        one.setInstanceID("1");
        one.setIsActiveInWorkflow(false);
        one.setPrevActivities(null);
        one.setNextActivities(Arrays.asList("2"));
        one.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(one);

        ActivityInstance two = new ActivityInstance();
        two.setId("2");
        two.setActivityCode("activityCode2");
        two.setActivityType(ActivityTypeEnum.TASK);
        two.setDefaultGroupID("1");
        two.setDepartmentID("1");
        two.setInstanceID("2");
        two.setIsActiveInWorkflow(false);
        two.setPrevActivities(Arrays.asList("1"));
        two.setNextActivities(Arrays.asList("4"));
        two.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(two);

        ActivityInstance three = new ActivityInstance();
        three.setId("3");
        three.setActivityCode("activityCode3");
        three.setActivityType(ActivityTypeEnum.APPOINTMENT);
        three.setDefaultGroupID("1");
        three.setDepartmentID("1");
        three.setInstanceID("");
        three.setIsActiveInWorkflow(false);
        three.setPrevActivities(null);
        three.setNextActivities(Arrays.asList("4"));
        three.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(three);

        ActivityInstance four = new ActivityInstance();
        four.setId("4");
        four.setActivityCode("activityCode4");
        four.setActivityType(ActivityTypeEnum.TASK);
        four.setDefaultGroupID("1");
        four.setDepartmentID("1");
        four.setInstanceID("");
        four.setIsActiveInWorkflow(false);
        four.setPrevActivities(Arrays.asList("2", "3"));
        four.setNextActivities(null);
        four.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(four);

        return instance;
    }
}
