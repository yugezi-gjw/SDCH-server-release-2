package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Task;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReference;
import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceType;
import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by fmk9441 on 2017-02-13.
 */
@Slf4j
public class TaskAssembler {
    private TaskAssembler() {

    }

    /**
     * Return Fhir Task from DTO.<br>
     *
     * @param orderDto DTO
     * @return Fhir Task
     */
    public static Task getTask(OrderDto orderDto) {
        Task task = new Task();

        if (orderDto == null) {
            return task;
        }

        if (isNotBlank(orderDto.getOrderId())) {
            task.setId(orderDto.getOrderId());
        }
        if (isNotBlank(orderDto.getOrderType())) {
            CodeableConcept ccReason = new CodeableConcept();
            ccReason.getCodingFirstRep().setCode(orderDto.getOrderType());
            task.setReason(ccReason);
        }
        if (isNotBlank(orderDto.getOrderStatus())) {
            try {
                task.setStatus(Task.TaskStatus.fromCode(orderDto.getOrderStatus()));
            } catch (FHIRException e) {
                log.error("FHIRException: {}", e.getMessage());
            }
        } else {
            task.setStatus(org.hl7.fhir.dstu3.model.Task.TaskStatus.READY);
        }

        task.setRestriction(new Task.TaskRestrictionComponent());

        if(null != orderDto.getDueDate()) {
            task.getRestriction().getPeriod().setEnd(orderDto.getDueDate());
        }else{
            task.getRestriction().getPeriod().setEnd(new Date());
        }
        if (isNotBlank(orderDto.getOrderGroup())) {
            task.setGroup(new Reference(orderDto.getOrderGroup()).setDisplay("Group"));
        }
        if (isNotBlank(orderDto.getOwnerId())) {
            task.setOwner(getReference(orderDto.getOrderGroup(), "Owner", "Group", false));
        }
        if (null != orderDto.getParticipants() && !orderDto.getParticipants().isEmpty()) {
            for (ParticipantDto participantDto : orderDto.getParticipants()) {
                if (ParticipantTypeEnum.PATIENT.equals(participantDto.getType())) {
                    task.setFor(getReference(participantDto.getParticipantId(), ParticipantTypeEnum.PATIENT.name(), ParticipantTypeEnum.PATIENT.name(), false));
                } else if (ParticipantTypeEnum.PRACTITIONER.equals(participantDto.getType())) {
                    task.getRestriction().addRecipient(new Reference(participantDto.getParticipantId()).setDisplay(ParticipantTypeEnum.PRACTITIONER.name()));
                }
            }
        }

        return task;
    }

    /**
     * Return Order DTO from Fhir Task.<br>
     * @param task Fhir Task
     * @return Order DTO
     */
    public static OrderDto getOrderDto(Task task) {
        OrderDto orderDto = new OrderDto();

        if (task == null) {
            return orderDto;
        }

        orderDto.setOrderId(task.getIdElement().getIdPart());
        if (task.hasReason()) {
            orderDto.setOrderType(task.getReason().getCodingFirstRep().getCode());
        }
        if (task.hasStatus()) {
            orderDto.setOrderStatus(task.getStatus().toCode());
        }
        if (null != task.getGroup()) {
            orderDto.setOrderGroup(getReferenceValue(task.getGroup().getReference()));
        }
        if (task.hasAuthoredOn()) {
            orderDto.setCreatedDT(task.getAuthoredOn());
        }
        if (task.hasMeta()) {
            orderDto.setLastModifiedDT(task.getMeta().getLastUpdated());
        }
        if (task.hasOwner()) {
            orderDto.setOwnerId(getReferenceValue(task.getOwner()));
        }

        List<ParticipantDto> lstParticipantDto = new ArrayList<>();
        if (task.hasFor()) {
            lstParticipantDto.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, getReferenceValue(task.getFor().getReference())));
        }
        if (task.hasRestriction()) {
            if(task.getRestriction().hasPeriod()) {
                orderDto.setDueDate(task.getRestriction().getPeriod().getEnd());
            }
            if (task.getRestriction().hasRecipient()) {
                task.getRestriction().getRecipient().forEach(reference -> lstParticipantDto.add(new ParticipantDto(ParticipantTypeEnum.fromCode(reference.getDisplay()), getReferenceValue(reference))));
            }
        }
        orderDto.setParticipants(lstParticipantDto);

        return orderDto;
    }

    /**
     * Update Fhir Task from Order DTO.<br>
     * @param task Fhir Task
     * @param orderDto Order DTO
     */
    public static void updateTask(Task task, OrderDto orderDto) {
        if (task == null || orderDto == null) {
            return;
        }

        if (isNotBlank(orderDto.getOrderGroup())) {
            task.setGroup(getReference(orderDto.getOrderGroup(), "Group", "Group", false));
        }
        if (isNotBlank(orderDto.getOrderStatus())) {
            try {
                task.setStatus(Task.TaskStatus.fromCode(orderDto.getOrderStatus()));
            } catch (FHIRException e) {
                log.error("FHIRException: {}", e.getMessage());
            }
        }
        if (null != orderDto.getDueDate()) {
            task.getRestriction().getPeriod().setEnd(orderDto.getDueDate());
        }
        //TODO: 由于FHIR里link一个新的carepath后，产生的头结点里owner的reference格式是"null/1004"，所以如果update的task是这样格式的就在
        //这里主动重新设置成正确格式"PRACTITIONER/1004"
        if (task.getOwner() != null
                && task.getOwner().getReference() != null
                && StringUtils.equalsIgnoreCase("null", getReferenceType(task.getOwner()))) {
            task.setOwner(getReference(getReferenceValue(task.getOwner()), ParticipantTypeEnum.PRACTITIONER.name(), ParticipantTypeEnum.PRACTITIONER.name(), false));
        }
        if (isNotEmpty(orderDto.getOwnerId())) {
            task.setOwner(getReference(orderDto.getOwnerId(), ParticipantTypeEnum.PRACTITIONER.name(), ParticipantTypeEnum.PRACTITIONER.name(), false));
        }
        if (null != orderDto.getParticipants() && !orderDto.getParticipants().isEmpty()) {
            for (ParticipantDto participantDto : orderDto.getParticipants()) {
                updateTaskRestriction(task, participantDto);
            }
        }
    }

    private static void updateTaskRestriction(Task task, ParticipantDto participantDto) {
        boolean existed = false;
        if (task.hasRestriction()) {
            for (Reference reference : task.getRestriction().getRecipient()) {
                if (getReferenceType(reference).equalsIgnoreCase(participantDto.getType().name())) {
                    reference.setReference(participantDto.getParticipantId());
                    existed = true;
                    break;
                }
            }
            if (!existed) {
                task.getRestriction().addRecipient(getReference(participantDto.getParticipantId(), participantDto.getType().name(), participantDto.getType().name(), false));
            }
        }
    }
}