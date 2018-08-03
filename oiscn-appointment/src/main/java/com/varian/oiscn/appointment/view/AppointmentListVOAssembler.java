package com.varian.oiscn.appointment.view;

import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.patient.PatientDto;

/**
 * Created by gbt1220 on 2/27/2017.
 */
public class AppointmentListVOAssembler {

    private AppointmentDto appointmentDto;

    private PatientDto patientDto;

    public AppointmentListVOAssembler(AppointmentDto appointmentDto, PatientDto patientDto) {
        this.appointmentDto = appointmentDto;
        this.patientDto = patientDto;
    }

    public AppointmentListVO getViewDto() {
        AppointmentListVO dto = new AppointmentListVO();
        dto.setAppointmentId(this.appointmentDto.getAppointmentId());
        dto.setOrderId(this.appointmentDto.getOrderId());
        dto.setPatientName(this.patientDto.getChineseName());
        dto.setHisId(this.patientDto.getHisId());
        dto.setPatientSer(new Long(this.patientDto.getPatientSer()));
        dto.setGender(this.patientDto.getGender());
        dto.setBirthday(this.patientDto.getBirthday());
        dto.setPrimaryPhysician(patientDto.getPhysicianName());
        dto.setAriaId(patientDto.getAriaId());
        dto.setPrimaryPhysicianComments("");
        dto.setPaid(false);
        dto.setCheckIn(false);
        dto.setScheduleTime(DateUtil.formatDate(appointmentDto.getStartTime(), DateUtil.SHORT_DATE_TIME_FORMAT) +
                "-" +
                DateUtil.formatDate(appointmentDto.getEndTime(), DateUtil.HOUR_MINUTE_TIME_FORMAT));
        dto.setReason(this.appointmentDto.getReason());
        dto.setReasonContent(ActivityCodesReader.getActivityCode(dto.getReason()).getContent());
        return dto;
    }
}
