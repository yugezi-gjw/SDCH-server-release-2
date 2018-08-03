package com.varian.oiscn.patient.progressstate;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.core.activity.ActivityCodeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.order.OrderDto;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by gbt1220 on 3/9/2017.
 */
public class PatientProgressStateService {
    private String patientId;

    private String userGroup;

    private OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    public PatientProgressStateService(String patientId, String userGroup) {
        this.patientId = patientId;
        this.userGroup = userGroup;
        this.initialize();
    }

    private void initialize() {
        this.orderAntiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
        this.appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
    }

    public PatientProgressState getCurrentProgressState() {
        PatientProgressState curState;
        List<OrderDto> orderDtoList = orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientId);
        if (orderDtoList.isEmpty()) {
            if (StringUtils.equalsIgnoreCase(userGroup, SystemConfigPool.queryGroupRoleOncologist())) {
                return new PatientProgressState(ProgressStateEnum.REGISTERED, NextActionEnum.SCHEDULE_IMMOBILIZATION);
            }
            return new PatientProgressState(ProgressStateEnum.REGISTERED, NextActionEnum.NONE);
        } else {
            List<AppointmentDto> appointmentDtoList;
            boolean isImmobilizationScheduled = false;
            boolean isCTSimScheduled = false;
            for (OrderDto orderDto : orderDtoList) {
                appointmentDtoList = appointmentAntiCorruptionServiceImp.queryAppointmentByOrderId(orderDto.getOrderId());
                if (ActivityCodeEnum.IMMOBILIZATION_ORDER.equals(ActivityCodeEnum.fromCode(orderDto.getOrderType()))) {
                    isImmobilizationScheduled = !appointmentDtoList.isEmpty();
                } else if (ActivityCodeEnum.CT_SIMULATION_ORDER.equals(ActivityCodeEnum.fromCode(orderDto.getOrderType()))) {
                    isCTSimScheduled = !appointmentDtoList.isEmpty();
                }
            }
            if (isCTSimScheduled) {
                curState = new PatientProgressState(ProgressStateEnum.CT_SIM_SCHEDULED, NextActionEnum.NONE);
            } else {
                if (isImmobilizationScheduled) {
                    curState = new PatientProgressState(ProgressStateEnum.CT_SIM_APPLIED, NextActionEnum.SCHEDULE_CT_SIM);
                } else {
                    curState = new PatientProgressState(ProgressStateEnum.CT_SIM_APPLIED, NextActionEnum.SCHEDULE_IMMOBILIZATION);
                }
            }
        }
        return curState;
    }
}
