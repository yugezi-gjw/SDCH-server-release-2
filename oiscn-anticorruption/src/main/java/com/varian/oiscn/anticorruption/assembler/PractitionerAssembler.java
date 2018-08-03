package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.core.practitioner.PractitionerDto;

/**
 * Created by fmk9441 on 2017-02-09.
 */
public class PractitionerAssembler {
    private PractitionerAssembler() {

    }

    /**
     * Return DTO  from Fhir Practitioner
     *
     * @param practitioner Fhir Practitioner
     * @return DTO
     */
    public static PractitionerDto getPractitionerDto(Practitioner practitioner) {
        PractitionerDto practitionerDto = new PractitionerDto();
        if (practitioner != null) {
            practitionerDto.setId(practitioner.hasIdElement() ? practitioner.getIdElement().getIdPart() : null);
            practitionerDto.setName(null != practitioner.getDisplayName() ? practitioner.getDisplayName().getValue() : null);
        }
        return practitionerDto;
    }
}