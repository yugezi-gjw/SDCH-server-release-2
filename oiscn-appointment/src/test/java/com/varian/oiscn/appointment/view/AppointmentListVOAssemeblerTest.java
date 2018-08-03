package com.varian.oiscn.appointment.view;

import com.varian.oiscn.appointment.util.MockDtoUtil;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.PatientDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gbt1220 on 3/2/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({AppointmentListVOAssembler.class})
public class AppointmentListVOAssemeblerTest {

    private AppointmentDto appointmentDto;
    private PatientDto patientDto;
    private AppointmentListVOAssembler assembler;

    @Before
    public void setup() {
        appointmentDto = givenAAppointment();
        patientDto = givenAPatient();
        assembler = new AppointmentListVOAssembler(appointmentDto, patientDto);
    }

    @Test
    public void givenAppointmentAndPatientWhenAssemblerThenReturnPatientView() {
        AppointmentListVO dto = assembler.getViewDto();
        Assert.assertEquals(appointmentDto.getAppointmentId(), dto.getAppointmentId());
        Assert.assertEquals(patientDto.getGender(), dto.getGender());
        Assert.assertEquals(patientDto.getHisId(), dto.getHisId());
        Assert.assertEquals(patientDto.getChineseName(), dto.getPatientName());
        Assert.assertEquals(patientDto.getPhysicianName(), dto.getPrimaryPhysician());
        Assert.assertFalse(dto.getCheckIn());
        Assert.assertFalse(dto.getPaid());
        Assert.assertEquals("", dto.getPrimaryPhysicianComments());
        Assert.assertEquals(DateUtil.formatDate(appointmentDto.getStartTime(),
                DateUtil.SHORT_DATE_TIME_FORMAT) +
                "-" +
                DateUtil.formatDate(appointmentDto.getEndTime(), DateUtil.HOUR_MINUTE_TIME_FORMAT), dto.getScheduleTime());
        Assert.assertEquals(patientDto.getBirthday(), dto.getBirthday());
    }

    private AppointmentDto givenAAppointment() {
        AppointmentDto dto = new AppointmentDto();
        dto.setOrderId("orderId");
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1212"));
        dto.setParticipants(participantDtos);
        dto.setEndTime(new Date());
        dto.setStartTime(new Date());
        dto.setReason("reason");
        dto.setComment("comment");
        dto.setAppointmentId("appointmentId");
        dto.setCreatedDT(new Date());
        dto.setStatus("status");
        return dto;
    }

    private PatientDto givenAPatient() {
        return MockDtoUtil.givenAPatient();
    }
}
