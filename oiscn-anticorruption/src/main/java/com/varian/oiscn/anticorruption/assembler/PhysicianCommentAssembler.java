package com.varian.oiscn.anticorruption.assembler;

import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import org.hl7.fhir.dstu3.model.Communication;
import org.hl7.fhir.dstu3.model.StringType;

/**
 * Created by gbt1220 on 12/21/2017.
 */
public class PhysicianCommentAssembler {

    private PhysicianCommentAssembler() {}

    public static void assemblerCommunication(Communication communication, PhysicianCommentDto dto) {
        communication.getPayloadFirstRep().setContent(new StringType(dto.getComments()));
        communication.setSent(dto.getLastUpdateTime());
    }
}
