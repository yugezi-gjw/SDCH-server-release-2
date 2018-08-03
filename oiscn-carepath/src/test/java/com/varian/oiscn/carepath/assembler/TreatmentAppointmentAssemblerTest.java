package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.carepath.vo.AppointmentFormDataVO;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

@RunWith(PowerMockRunner.class)
public class TreatmentAppointmentAssemblerTest {

    @Test
    public void givenAppointmentDtoAndVOThenReturnTreatmentDto(){
        AppointmentDto appointmentDto = new AppointmentDto();
        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setType(ParticipantTypeEnum.DEVICE);
        String participantId = "participantId";
        participantDto.setParticipantId(participantId);
        appointmentDto.setParticipants(Arrays.asList(participantDto));
        AppointmentFormDataVO appointmentFormDataVO = new AppointmentFormDataVO();
        Long patientSer = 121212L;
        appointmentFormDataVO.setPatientSer(patientSer);
        TreatmentAppointmentDTO treatmentAppointmentDTO = TreatmentAppointmentAssembler.appointmentDto2TreatmentAppointmentDTO(appointmentDto, appointmentFormDataVO, "hisId");
        Assert.assertEquals(participantId, treatmentAppointmentDTO.getDeviceId());
        Assert.assertEquals(patientSer, treatmentAppointmentDTO.getPatientSer());
        Assert.assertNull(treatmentAppointmentDTO.getEndTime());
    }

    @Test
    public void givenTreatmentAppointmentDTOThenReturnAppointmentDto(){
        TreatmentAppointmentDTO treatmentAppointmentDTO = new TreatmentAppointmentDTO();
        String activityCode = "activityCode";
        treatmentAppointmentDTO.setActivityCode(activityCode);
        AppointmentDto appointmentDto = TreatmentAppointmentAssembler.treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO, true);
        Assert.assertNull(appointmentDto.getStartTime());
        Assert.assertEquals(activityCode, appointmentDto.getReason());
    }
}
