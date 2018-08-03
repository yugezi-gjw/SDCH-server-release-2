package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.order.OrderDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by gbt1220 on 7/20/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(ActivityInstanceDueDateComparator.class)
public class ActivityInstanceDueDateComparatorTest {

    private OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    private ActivityInstanceDueDateComparator comparator;

    @Before
    public void setup() throws Exception {
        orderAntiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(orderAntiCorruptionServiceImp);
        appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withNoArguments().thenReturn(appointmentAntiCorruptionServiceImp);
        comparator = new ActivityInstanceDueDateComparator();
    }

    @Test
    public void givenActivityOfNonInstanceIdWhenCompareThenReturnNegative() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        ActivityInstance nonInstanceId = instance.getActivityInstances().get(1);
        ActivityInstance existInstanceId = instance.getActivityInstances().get(0);
        Assert.assertEquals(-1, comparator.compare(nonInstanceId, existInstanceId));
    }

    @Test
    public void givenActivityOfNonInstanceIdWhenCompareThenReturnPositive() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        ActivityInstance nonInstanceId = instance.getActivityInstances().get(1);
        ActivityInstance existInstanceId = instance.getActivityInstances().get(0);
        Assert.assertEquals(1, comparator.compare(existInstanceId, nonInstanceId));
    }

    @Test
    public void givenTaskAndAppointmentActivityWhenCompareThenReturn() throws ParseException {
        ActivityInstance taskInstance = givenTaskInstance();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ActivityInstance appointmentInstance = givenAppointmentInstance();
        OrderDto orderDto = MockDtoUtil.givenOrderList().get(0);
        Date orderDate = DateUtil.parse("2017-07-19 10:10");
        orderDto.setDueDate(orderDate);
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderById(taskInstance.getInstanceID())).thenReturn(orderDto);
        AppointmentDto appointmentDto = MockDtoUtil.givenAppointmentListDto().get(0);
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentById(appointmentInstance.getInstanceID())).thenReturn(appointmentDto);
        Assert.assertEquals(-1, comparator.compare(taskInstance, appointmentInstance));
    }

    private ActivityInstance givenTaskInstance() {
        ActivityInstance taskInstance = new ActivityInstance();
        taskInstance.setActivityType(ActivityTypeEnum.TASK);
        taskInstance.setInstanceID("1");
        taskInstance.setDueDateOrScheduledStartDate(new Date());
        return taskInstance;
    }

    private ActivityInstance givenAppointmentInstance() {
        ActivityInstance appointmentInstance = new ActivityInstance();
        appointmentInstance.setActivityType(ActivityTypeEnum.APPOINTMENT);
        appointmentInstance.setInstanceID("2");
        appointmentInstance.setDueDateOrScheduledStartDate(new Date());
        return appointmentInstance;
    }

}
