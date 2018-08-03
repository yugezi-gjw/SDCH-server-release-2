package com.varian.oiscn.patient.progrssstate;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.core.activity.ActivityCodeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.patient.progressstate.NextActionEnum;
import com.varian.oiscn.patient.progressstate.PatientProgressState;
import com.varian.oiscn.patient.progressstate.PatientProgressStateService;
import com.varian.oiscn.patient.progressstate.ProgressStateEnum;
import com.varian.oiscn.patient.util.MockDtoUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 3/9/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientProgressStateService.class,SystemConfigPool.class})
public class PatientProgressStateServiceTest {

    private String patientId;

    private OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    private PatientProgressStateService stateService;

    @Before
    public void setup() throws Exception {
        patientId = "patientId";
        orderAntiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(orderAntiCorruptionServiceImp);
        appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withNoArguments().thenReturn(appointmentAntiCorruptionServiceImp);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryGroupRoleNurse()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupRoleOncologist()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupRolePhysicist()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");

        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");
    }

    @Test
    public void givenWhenOrderIsEmptyAndUserGroupIsOncologistThenReturnRegisteredProgress() {
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientId)).thenReturn(new ArrayList<>());
        PatientProgressState givenState = givenARegisteredStateAndScheduleImmobProgressState();
        stateService = new PatientProgressStateService(patientId, SystemConfigPool.queryGroupRoleOncologist());
        PatientProgressState curState = stateService.getCurrentProgressState();
        Assert.assertEquals(givenState.getCurrentProgress(), curState.getCurrentProgress());
        Assert.assertEquals(givenState.getNextAction(), curState.getNextAction());
    }

    @Test
    public void givenWhenOrderIsEmptyAndUserGroupIsNurseThenReturnRegisteredProgress() {
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientId)).thenReturn(new ArrayList<>());
        PatientProgressState givenState = givenARegisteredProgressState();
        stateService = new PatientProgressStateService(patientId, SystemConfigPool.queryGroupRoleNurse());
        PatientProgressState curState = stateService.getCurrentProgressState();
        Assert.assertEquals(givenState.getCurrentProgress(), curState.getCurrentProgress());
        Assert.assertEquals(givenState.getNextAction(), curState.getNextAction());
    }

    @Test
    public void givenAnOrderWhenCTSimScheduledThenReturnCTSimScheduledProgress() {
        List<OrderDto> orderDtoList = givenAnOrderList();
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientId)).thenReturn(orderDtoList);
        List<AppointmentDto> appointmentDtoList = givenAnAppointmentList();
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentByOrderId("orderIdTwo")).thenReturn(appointmentDtoList);
        PatientProgressState givenState = givenACTSimScheduledProgressState();
        stateService = new PatientProgressStateService(patientId, SystemConfigPool.queryGroupRoleNurse());
        PatientProgressState curState = stateService.getCurrentProgressState();
        Assert.assertEquals(givenState.getCurrentProgress(), curState.getCurrentProgress());
        Assert.assertEquals(givenState.getNextAction(), curState.getNextAction());
    }

    @Test
    public void givenAnOrderWhenCTSimAppliedAndImmobilizationScheduledThenReturnCTSimAppliedProgress() {
        List<OrderDto> orderDtoList = givenAnOrderList();
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientId)).thenReturn(orderDtoList);
        List<AppointmentDto> appointmentDtoList = givenAnAppointmentList();
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentByOrderId("orderIdOne")).thenReturn(appointmentDtoList);
        PatientProgressState givenState = givenACTSimAppliedAndScheduleCTSimActionProgressState();
        stateService = new PatientProgressStateService(patientId, SystemConfigPool.queryGroupRoleNurse());
        PatientProgressState curState = stateService.getCurrentProgressState();
        Assert.assertEquals(givenState.getCurrentProgress(), curState.getCurrentProgress());
        Assert.assertEquals(givenState.getNextAction(), curState.getNextAction());
    }

    @Test
    public void givenAnOrderWhenCTSimAppliedAndImmobilizationNotScheduledThenReturnCTSimAppliedAndScheduleImmobilizationProgress() {
        List<OrderDto> orderDtoList = givenAnOrderList();
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientId)).thenReturn(orderDtoList);
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentByOrderId("orderIdOne")).thenReturn(new ArrayList<>());
        PatientProgressState givenState = givenACTSimAppliedAndScheduleImmobilizationActionProgressState();
        stateService = new PatientProgressStateService(patientId,  SystemConfigPool.queryGroupRoleNurse());
        PatientProgressState curState = stateService.getCurrentProgressState();
        Assert.assertEquals(givenState.getCurrentProgress(), curState.getCurrentProgress());
        Assert.assertEquals(givenState.getNextAction(), curState.getNextAction());
    }

    private PatientProgressState givenACTSimAppliedAndScheduleImmobilizationActionProgressState() {
        return new PatientProgressState(ProgressStateEnum.CT_SIM_APPLIED, NextActionEnum.SCHEDULE_IMMOBILIZATION);
    }

    private PatientProgressState givenACTSimAppliedAndScheduleCTSimActionProgressState() {
        return new PatientProgressState(ProgressStateEnum.CT_SIM_APPLIED, NextActionEnum.SCHEDULE_CT_SIM);
    }

    private PatientProgressState givenACTSimScheduledProgressState() {
        return new PatientProgressState(ProgressStateEnum.CT_SIM_SCHEDULED, NextActionEnum.NONE);
    }

    private PatientProgressState givenARegisteredProgressState() {
        return new PatientProgressState(ProgressStateEnum.REGISTERED, NextActionEnum.NONE);
    }

    private PatientProgressState givenARegisteredStateAndScheduleImmobProgressState() {
        return new PatientProgressState(ProgressStateEnum.REGISTERED, NextActionEnum.SCHEDULE_IMMOBILIZATION);
    }

    private List<OrderDto> givenAnOrderList() {
        List<OrderDto> orderDtoList = new ArrayList<>();
        OrderDto immobOrder = MockDtoUtil.givenAnOrderDto();
        immobOrder.setOrderId("orderIdOne");
        immobOrder.setOrderType(ActivityCodeEnum.getDisplay(ActivityCodeEnum.IMMOBILIZATION_ORDER));
        immobOrder.setOrderStatus(ActivityCodeEnum.getDisplay(ActivityCodeEnum.IMMOBILIZATION_ORDER));
        OrderDto ctSimOrder = MockDtoUtil.givenAnOrderDto();
        ctSimOrder.setOrderId("orderIdTwo");
        ctSimOrder.setOrderType(ActivityCodeEnum.getDisplay(ActivityCodeEnum.CT_SIMULATION_ORDER));
        ctSimOrder.setOrderStatus(ActivityCodeEnum.getDisplay(ActivityCodeEnum.CT_SIMULATION_ORDER));
        orderDtoList.add(immobOrder);
        orderDtoList.add(ctSimOrder);
        return orderDtoList;
    }

    private List<AppointmentDto> givenAnAppointmentList() {
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        appointmentDtoList.add(new AppointmentDto("", "", null, null, "", "", "", null, null, null));
        return appointmentDtoList;
    }
}
