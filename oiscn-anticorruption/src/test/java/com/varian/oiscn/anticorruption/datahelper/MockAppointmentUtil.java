package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Appointment;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Reference;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-03-24.
 */
public class MockAppointmentUtil {
    private MockAppointmentUtil() {
    }

    public static Appointment givenAnAppointment() {
        Appointment appointment = new Appointment();

        appointment.setId("AppointmentId");
        appointment.setStart(new DateTime(2017, 2, 14, 8, 15).toDate());
        appointment.setEnd(new DateTime(2017, 2, 14, 8, 30).toDate());
        appointment.setDepartment(new Reference("1"));
        appointment.setStatus(Appointment.AppointmentStatus.BOOKED);

        CodeableConcept appointmentReason = new CodeableConcept();
        appointmentReason.getCodingFirstRep().setCode("Consult");
        appointment.setReason(Arrays.asList(appointmentReason));

        appointment.setTasks(Arrays.asList(new Reference("Task/TaskId")));

        appointment.getParticipant().add(new Appointment.AppointmentParticipantComponent().setActor(new Reference("Patient/PatientId").setDisplay("PATIENT")).setRequired(Appointment.ParticipantRequired.REQUIRED));
        appointment.getParticipant().add(new Appointment.AppointmentParticipantComponent().setActor(new Reference("Device/DeviceId").setDisplay("DEVICE")).setRequired(Appointment.ParticipantRequired.REQUIRED));
        appointment.getParticipant().add(new Appointment.AppointmentParticipantComponent().setActor(new Reference("Location/LocationId").setDisplay("LOCATION")).setRequired(Appointment.ParticipantRequired.REQUIRED));
        appointment.getParticipant().add(new Appointment.AppointmentParticipantComponent().setActor(new Reference("Practitioner/PractitionerId").setDisplay("PRACTITIONER")).setRequired(Appointment.ParticipantRequired.REQUIRED));

        appointment.setComment("Comment");
        appointment.setCreated(new Date());

        return appointment;
    }

    public static AppointmentDto givenAnAppointmentDto() {
        AppointmentDto appointmentDto = new AppointmentDto();

        appointmentDto.setAppointmentId("AppointmentId");
        appointmentDto.setOrderId("OrderId");
        appointmentDto.setStartTime(new DateTime(2017, 2, 14, 8, 15).toDate());
        appointmentDto.setEndTime(new DateTime(2017, 2, 14, 8, 30).toDate());

        appointmentDto.setReason("Consult");
        appointmentDto.setStatus(Appointment.AppointmentStatus.FULFILLED.toCode());
        appointmentDto.setComment("Immobilization Appointment Test");
        appointmentDto.setCreatedDT(new Date());

        List<ParticipantDto> lstParticipants = new ArrayList<>();
        lstParticipants.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "PatientId"));
        lstParticipants.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "DeviceId"));
        lstParticipants.add(new ParticipantDto(ParticipantTypeEnum.LOCATION, "LocationId"));
        lstParticipants.add(new ParticipantDto(ParticipantTypeEnum.PRACTITIONER, "PractitionerId"));
        appointmentDto.setParticipants(lstParticipants);

        return appointmentDto;
    }

    public static Bundle givenAnAppointmentBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenAnAppointment());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponents);
        bundle.setTotal(5);
        return bundle;
    }

    public static List<Appointment> givenAnAppointmentList() {
        return Arrays.asList(givenAnAppointment());
    }
}
