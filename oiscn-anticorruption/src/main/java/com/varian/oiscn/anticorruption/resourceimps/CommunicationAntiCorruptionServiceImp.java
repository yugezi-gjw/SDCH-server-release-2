package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.oiscn.anticorruption.assembler.PhysicianCommentAssembler;
import com.varian.oiscn.anticorruption.converter.DataHelper;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRCommunicationInterface;
import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Communication;

/**
 * Created by gbt1220 on 12/21/2017.
 */
public class CommunicationAntiCorruptionServiceImp {

    private FHIRCommunicationInterface communicationInterface;

    /**
     * Default Constructor.<br>
     */
    public CommunicationAntiCorruptionServiceImp() {
        communicationInterface = new FHIRCommunicationInterface();
    }

    /**
     * Update physician comment
     * @param dto physician comment dto
     * @return updated id
     */
    public String updatePhysicianComment(PhysicianCommentDto dto) {
        if(StringUtils.isEmpty(StringUtils.trimToEmpty(dto.getComments()))){
            return errorPhysicianComment(dto);
        }
        Communication communication = communicationInterface.queryByPatientId(dto.getPatientSer());
        if (communication != null) {
            PhysicianCommentAssembler.assemblerCommunication(communication, dto);
            return communicationInterface.update(communication);
        }
        return StringUtils.EMPTY;
    }

    public String errorPhysicianComment(PhysicianCommentDto dto) {
        Communication communication = communicationInterface.queryByPatientId(dto.getPatientSer());
        if (communication != null) {
            communication.setStatus(Communication.CommunicationStatus.ENTEREDINERROR);
            return communicationInterface.update(communication);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Create physician comment
     * @param physicianCommentDto physician comment dto
     * @return created id
     */
    public String createPhysicianComment(PhysicianCommentDto physicianCommentDto) {
        Communication communication = communicationInterface.getCommunicationObject(physicianCommentDto);
        return communicationInterface.create(communication);
    }

    /**
     * Query physician comment of patient
     * @param patientId patient id
     * @return physician comment dto
     */
    public PhysicianCommentDto queryPhysicianCommentByPatientId(String patientId) {
        PhysicianCommentDto physicianCommentDto = null;
        Communication communication = communicationInterface.queryByPatientId(patientId);
        if (communication != null) {
            physicianCommentDto = new PhysicianCommentDto();
            physicianCommentDto.setComments(communication.getPayloadFirstRep().getContent().toString());
            physicianCommentDto.setPatientSer(patientId);
            physicianCommentDto.setLastUpdateTime(communication.getSent());
            physicianCommentDto.setPractitionerId(DataHelper.getReferenceValue(communication.getSender().getReference()));
        }
        return physicianCommentDto;
    }

}
