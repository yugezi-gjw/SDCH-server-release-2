package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Task;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Reference;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-03-24.
 */
public final class MockTaskUtil {
    private MockTaskUtil() {
    }

    public static Task givenATask() {
        Task task = new Task();
        task.setId("TaskId");
        task.setMeta(new Meta().setLastUpdated(new Date()));
        CodeableConcept ccReason = new CodeableConcept();
        ccReason.getCodingFirstRep().setCode("Create Physician Order Task");
        task.setReason(ccReason);
        task.setStatus(Task.TaskStatus.READY);
        DateTime current = new DateTime();
        task.setRestriction(new Task.TaskRestrictionComponent());
        task.getRestriction().getPeriod().setEnd(new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth(), current.getHourOfDay(), current.getMinuteOfHour()).toDate());
        task.setOwner(new Reference("OwnerId").setDisplay("Owner"));
        task.setGroup(new Reference("GroupId").setDisplay("Group"));
        task.setAuthoredOn(new Date());
        task.setPartOf(Arrays.asList(new Reference("AppointmentId").setDisplay("Appointment")));
        task.setFor(new Reference("PatientId").setDisplay("Patient"));
        task.getRestriction().addRecipient(new Reference("DeviceId").setDisplay("Device"));
        task.getRestriction().addRecipient(new Reference("PractitionerId").setDisplay("Practitioner"));

        return task;
    }

    public static OrderDto givenAnOrderDto() {
        OrderDto orderDto = new OrderDto();

        orderDto.setOrderId("OrderId");
        orderDto.setOrderGroup("GroupId");
        orderDto.setOrderStatus("ready");
        orderDto.setOrderType("Create Physician Order Task");
        orderDto.setOwnerId("OwnerId");
        orderDto.setDueDate(new Date());
        orderDto.setLastModifiedDT(new Date());
        List<ParticipantDto> lstParticipant = new ArrayList<>();
        lstParticipant.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "PatientId"));
        lstParticipant.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "DeviceId"));
        lstParticipant.add(new ParticipantDto(ParticipantTypeEnum.PRACTITIONER, "PractitionerId"));
        orderDto.setParticipants(lstParticipant);

        return orderDto;
    }

    public static Bundle givenATaskBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenATask());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponents);
        bundle.setTotal(5);
        return bundle;
    }

    public static List<Task> givenATaskList() {
        return Arrays.asList(givenATask());
    }
}