package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.assembler.PractitionerAssembler;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRPractitionerInterface;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceType;
import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;

/**
 * Created by fmk9441 on 2017-02-07.
 */
public class PractitionerAntiCorruptionServiceImp {
    private FHIRPractitionerInterface fhirPractitionerInterface;

    /**
     * Default Constructor.<br>
     */
    public PractitionerAntiCorruptionServiceImp() {
        fhirPractitionerInterface = new FHIRPractitionerInterface();
    }

    /**
     * Return Practitioner DTO.<br>
     *
     * @param practitionerId Practitioner Id
     * @return Practitioner DTO
     */
    public PractitionerDto queryPractitionerById(String practitionerId) {
        PractitionerDto practitionerDto = null;
        Practitioner practitioner = fhirPractitionerInterface.queryById(practitionerId,Practitioner.class);
        if (practitioner != null) {
            practitionerDto = PractitionerAssembler.getPractitionerDto(practitioner);
        }

        return practitionerDto;
    }

    /**
     * Return Practitioner DTO.<br>
     * @param loginId Login Id
     * @return Practitioner DTO
     */
    public PractitionerDto queryPractitionerByLoginId(String loginId) {
        PractitionerDto practitionerDto = null;
        Practitioner practitioner = fhirPractitionerInterface.queryPractitionerByLoginId(loginId);
        if (practitioner != null) {
            practitionerDto = PractitionerAssembler.getPractitionerDto(practitioner);
        }
        return practitionerDto;
    }

    /**
     * Return Practitioner DTO List.<br>
     * @param groupId Group Id
     * @return Practitioner DTO List
     */
    public List<PractitionerDto> queryPractitionerDtoListByGroupId(String groupId) {
        List<PractitionerDto> lstPractitionerDto = new ArrayList<>();
        List<Reference> lstMemberRef = fhirPractitionerInterface.queryPractitionerListByGroupId(groupId);
        if (!lstMemberRef.isEmpty()) {
            lstPractitionerDto.addAll(lstMemberRef.stream().map(r -> new PractitionerDto(getReferenceValue(r), r.getDisplay(), ParticipantTypeEnum.fromCode(getReferenceType(r)))).collect(Collectors.toList()));
        }
        return lstPractitionerDto;
    }
}