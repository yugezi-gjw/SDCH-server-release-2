package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Appointment;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceType;
import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by fmk9441 on 2017-02-13.
 */
@Slf4j
public class AppointmentAssembler {
    private AppointmentAssembler() {}

    /**
     * Return Fhir Appointment By DTO.<br>
     *
     * @param appointmentDto Appointment DTO
     * @return Fhir Appointment
     */
    public static Appointment getAppointment(AppointmentDto appointmentDto) {
        Appointment appointment = new Appointment();
        if (appointmentDto != null) {
            if (isNotBlank(appointmentDto.getAppointmentId())) {
                appointment.setId(appointmentDto.getAppointmentId());
            }
            if (null != appointmentDto.getStartTime()) {
                appointment.setStart(appointmentDto.getStartTime());
            }
            if (null != appointmentDto.getEndTime()) {
                appointment.setEnd(appointmentDto.getEndTime());
            }
            if (isNotBlank(appointmentDto.getStatus())) {
                try {
                    appointment.setStatus(Appointment.AppointmentStatus.fromCode(appointmentDto.getStatus()));
                } catch (FHIRException e) {
                    log.error("FHIRException: {}", e.getMessage());
                }
            } else {
                appointment.setStatus(Appointment.AppointmentStatus.BOOKED);
            }
            if (isNotBlank(appointmentDto.getReason())) {
                CodeableConcept appointmentReason = new CodeableConcept();
                appointmentReason.getCodingFirstRep().setCode(appointmentDto.getReason());
                appointment.setReason(Arrays.asList(appointmentReason));
            }
            if (null != appointmentDto.getParticipants() && !appointmentDto.getParticipants().isEmpty()) {
                for (ParticipantDto participantDto : appointmentDto.getParticipants()) {
                    Appointment.AppointmentParticipantComponent participant = appointment.addParticipant();
                    participant.setActor(new Reference(String.format("%1$s/%2$s", participantDto.getType().name(), participantDto.getParticipantId())).setDisplay(participantDto.getType().name())).setRequired(Appointment.ParticipantRequired.REQUIRED);
                }
            }
            if (isNotBlank(appointmentDto.getComment())) {
                appointment.setComment(appointmentDto.getComment());
            }
        }
        return appointment;
    }

    /**
     * Update Fhir Appointment by DTO.<br>
     * @param appointment Fhir Appointment 
     * @param appointmentDto DTO
     */
    public static void updateAppointment(Appointment appointment, AppointmentDto appointmentDto) {
        if (appointmentDto != null && appointment != null) {
            if (null != appointmentDto.getStartTime()) {
                appointment.setStart(appointmentDto.getStartTime());
            }
            if (null != appointmentDto.getEndTime()) {
                appointment.setEnd(appointmentDto.getEndTime());
            }
            if (isNotBlank(appointmentDto.getStatus())) {
                try {
                    appointment.setStatus(Appointment.AppointmentStatus.fromCode(appointmentDto.getStatus()));
                } catch (FHIRException e) {
                    log.error("FHIRException: {}", e.getMessage());
                }
            }
            if (isNotBlank(appointmentDto.getComment())) {
                appointment.setComment(appointmentDto.getComment());
            }
            if (null != appointmentDto.getParticipants() && !appointmentDto.getParticipants().isEmpty()) {
                for (ParticipantDto participantDto : appointmentDto.getParticipants()) {
                    updateAppointmentParticipant(appointment, participantDto);
                }
            }
        }
    }

    private static void updateAppointmentParticipant(Appointment appointment, ParticipantDto participantDto) {
        boolean participantNotExisted = true;
        for (Appointment.AppointmentParticipantComponent appointmentParticipantComponent : appointment.getParticipant()) {
            if (appointmentParticipantComponent.hasActor()
                    && appointmentParticipantComponent.getActor().hasDisplay()
                    && getReferenceType(appointmentParticipantComponent.getActor()).equalsIgnoreCase(participantDto.getType().name())
                    && !getReferenceValue(appointmentParticipantComponent.getActor()).equals(participantDto.getParticipantId())) {
                appointmentParticipantComponent.setActor(new Reference(String.format("%1$s/%2$s", participantDto.getType().name(), participantDto.getParticipantId())).setDisplay(participantDto.getType().name())).setRequired(Appointment.ParticipantRequired.REQUIRED);
                participantNotExisted = false;
                break;
            }
        }

        if (participantNotExisted) {
            Appointment.AppointmentParticipantComponent participant = appointment.addParticipant();
            participant.setActor(new Reference(participantDto.getParticipantId()).setDisplay(participantDto.getType().name())).setRequired(Appointment.ParticipantRequired.REQUIRED);
        }
    }

    /**
     * Return DTO by Fhir Appointment.<br>
     * @param appointment Fhir Appointment
     * @return DTO
     */
    public static AppointmentDto getAppointmentDto(Appointment appointment) {
        AppointmentDto appointmentDto = new AppointmentDto();
        if (appointment != null) {
            appointmentDto.setAppointmentId(appointment.hasIdElement() ? appointment.getIdElement().getIdPart() : null);
            appointmentDto.setStartTime(appointment.hasStart() ? appointment.getStart() : null);
            appointmentDto.setEndTime(appointment.hasEnd() ? appointment.getEnd() : null);
            appointmentDto.setStatus(appointment.hasStatus() ? appointment.getStatus().getDisplay() : null);
            getParticipant(appointment, appointmentDto);
            getReason(appointment, appointmentDto);
            getOrderId(appointment, appointmentDto);
            appointmentDto.setCreatedDT(appointment.hasCreated() ? appointment.getCreated() : null);
            appointmentDto.setLastModifiedDT(appointment.hasMeta() ? appointment.getMeta().getLastUpdated() : null);
            appointmentDto.setComment(appointment.hasComment() ? appointment.getComment() : null);
        }
        return appointmentDto;
    }

    private static void getReason(Appointment appointment, AppointmentDto appointmentDto) {
        if (appointment.hasReason()) {
            CodeableConcept appointmentReason = appointment.getReason().get(0);
            appointmentDto.setReason(appointmentReason.getCodingFirstRep().getCode());
        }
    }

    private static void getOrderId(Appointment appointment, AppointmentDto appointmentDto) {
        if (null != appointment.getTasks() && !appointment.getTasks().isEmpty()) {
            appointmentDto.setOrderId(getReferenceValue(appointment.getTasks().get(0)));
        }
    }

    private static void getParticipant(Appointment appointment, AppointmentDto appointmentDto) {
        if (!appointment.hasParticipant())
            return;
        List<ParticipantDto> lstParticipantDtos = new ArrayList<>();
        for (Appointment.AppointmentParticipantComponent component : appointment.getParticipant()) {
            if (component.hasActor() && component.getActor().hasReference()) {
                ParticipantDto participantDto = new ParticipantDto();
                switch (ParticipantTypeEnum.fromCode(getReferenceType(component.getActor()))) {
                    case PATIENT:
                        participantDto.setType(ParticipantTypeEnum.PATIENT);
                        participantDto.setParticipantId(getReferenceValue(component.getActor()));
                        break;
                    case LOCATION:
                        participantDto.setType(ParticipantTypeEnum.LOCATION);
                        participantDto.setParticipantId(getReferenceValue(component.getActor()));
                        break;
                    case DEVICE:
                        participantDto.setType(ParticipantTypeEnum.DEVICE);
                        participantDto.setParticipantId(getReferenceValue(component.getActor()));
                        break;
                    default:
                        participantDto.setType(ParticipantTypeEnum.PRACTITIONER);
                        participantDto.setParticipantId(getReferenceValue(component.getActor()));
                        break;
                }
                lstParticipantDtos.add(participantDto);
            }
        }
        appointmentDto.setParticipants(lstParticipantDtos);
    }
}