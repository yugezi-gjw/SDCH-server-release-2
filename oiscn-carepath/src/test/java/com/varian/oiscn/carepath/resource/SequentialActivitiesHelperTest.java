package com.varian.oiscn.carepath.resource;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderStatusEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

/**
 * Created by gbt1220 on 5/17/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SequentialActivitiesHelper.class, SystemConfigPool.class})
public class SequentialActivitiesHelperTest {

    @Test
    public void givenCarePathInstanceWhenQueryThenReturnSequentialActivities() throws Exception {
        CarePathInstance instance = givenSpecialCarePathInstance();
        Assert.assertEquals(4,
                new SequentialActivitiesHelper(instance, Arrays.asList("1")).
                        getAllSequentialActivities(instance.getActivityInstances().get(2)).size());
    }

    @Test
    public void givenCarePathInstanceWhenNotContainedGroupIdThenReturnEmpty() {
        CarePathInstance instance = givenSpecialCarePathInstance();
        Assert.assertEquals(0,
                new SequentialActivitiesHelper(instance, Arrays.asList("2")).
                        getAllSequentialActivities(instance.getActivityInstances().get(2)).size());
    }

    @Test
    public void givenCarePathInstanceWhenQueryByInstanceIdThenReturnSequentialActivityVOList() throws Exception {
        CarePathInstance instance = givenSpecialCarePathInstance();
        SequentialActivitiesHelper helper = new SequentialActivitiesHelper(instance, Arrays.asList("1"));
        OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(orderAntiCorruptionServiceImp);
        AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withNoArguments().thenReturn(appointmentAntiCorruptionServiceImp);
        OrderDto orderDto = MockDtoUtil.givenAnOrderDto();
        orderDto.setOrderStatus(OrderStatusEnum.COMPLETED.name());
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderById("1")).thenReturn(orderDto);
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentById("3")).thenReturn(new AppointmentDto());
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
        Assert.assertEquals(4, helper.querySequentialActivitiesByInstanceId("2", "TASK").size());
    }

    private CarePathInstance givenSpecialCarePathInstance() {
        CarePathInstance instance = new CarePathInstance();
        instance.setId("1");
        instance.setPatientID("1");

        ActivityInstance one = new ActivityInstance();
        one.setId("1");
        one.setActivityCode("activityCode1");
        one.setActivityType(ActivityTypeEnum.TASK);
        one.setDefaultGroupID("1");
        one.setDepartmentID("1");
        one.setInstanceID("1");
        one.setIsActiveInWorkflow(false);
        one.setPrevActivities(null);
        one.setNextActivities(Arrays.asList("2", "3"));
        one.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(one);

        ActivityInstance two = new ActivityInstance();
        two.setId("2");
        two.setActivityCode("activityCode2");
        two.setActivityType(ActivityTypeEnum.TASK);
        two.setDefaultGroupID("1");
        two.setDepartmentID("1");
        two.setInstanceID("2");
        two.setIsActiveInWorkflow(true);
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
        three.setInstanceID("3");
        three.setIsActiveInWorkflow(false);
        three.setPrevActivities(Arrays.asList("1"));
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
