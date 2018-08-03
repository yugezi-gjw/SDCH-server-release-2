package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.carepath.vo.AppointmentFormDataVO;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by BHP9696 on 2017/9/29.
 */
public class TreatmentAppointmentAssembler {
    /**
     * 将AppointmentDto build 成 TreatmentAppointmentDTO
     *
     * @param appointmentDto
     * @param vo
     * @param hisId
     * @return
     */
    public static TreatmentAppointmentDTO appointmentDto2TreatmentAppointmentDTO(AppointmentDto appointmentDto, AppointmentFormDataVO vo, String hisId) {
        TreatmentAppointmentDTO treatmentAppointmentDTO = new TreatmentAppointmentDTO();
        treatmentAppointmentDTO.setHisId(hisId);
        treatmentAppointmentDTO.setPatientSer(vo.getPatientSer());
        treatmentAppointmentDTO.setActivityCode(appointmentDto.getReason());
        treatmentAppointmentDTO.setStatus(appointmentDto.getStatus());
        treatmentAppointmentDTO.setStartTime(appointmentDto.getStartTime());
        treatmentAppointmentDTO.setEndTime(appointmentDto.getEndTime());
        List<ParticipantDto> participantDtoList = appointmentDto.getParticipants();
        for (ParticipantDto dto : participantDtoList) {
            if (ParticipantTypeEnum.DEVICE.equals(dto.getType())) {
                treatmentAppointmentDTO.setDeviceId(dto.getParticipantId());
                break;
            }
        }

        return treatmentAppointmentDTO;
    }

    /**
     * TreatmentAppointmentDTO 转换成AppointmentDto
     *
     * @param treatmentAppointmentDTO
     * @param id2AppointmentId        如果appointmentId为空，是否将TreatmentAppointmentDTO的id值赋予AppointmentDto的AppointmentId
     *                                如果为true，当TreatmentAppointmentDTO的appointmentId为空时，将其id赋予AppointmentDto的AppointmentId
     * @return
     */
    public static AppointmentDto treatmentAppointmentDTO2AppointmentDto(TreatmentAppointmentDTO treatmentAppointmentDTO, boolean id2AppointmentId) {
        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setAppointmentId(treatmentAppointmentDTO.getAppointmentId());
        if (StringUtils.isEmpty(appointmentDto.getAppointmentId()) && id2AppointmentId) {
            appointmentDto.setAppointmentId(treatmentAppointmentDTO.getUid());
        }
        appointmentDto.setStartTime(treatmentAppointmentDTO.getStartTime());
        appointmentDto.setEndTime(treatmentAppointmentDTO.getEndTime());
        appointmentDto.setStatus(treatmentAppointmentDTO.getStatus());
        appointmentDto.setReason(treatmentAppointmentDTO.getActivityCode());
        appointmentDto.setParticipants(Arrays.asList(new ParticipantDto(ParticipantTypeEnum.PATIENT, String.valueOf(treatmentAppointmentDTO.getPatientSer())),
                new ParticipantDto(ParticipantTypeEnum.DEVICE, treatmentAppointmentDTO.getDeviceId())));
        Date nowDate = new Date();
        appointmentDto.setCreatedDT(nowDate);
        appointmentDto.setLastModifiedDT(nowDate);
        return appointmentDto;
    }

}
