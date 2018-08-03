package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Flag;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Reference;

/**
 * Created by fmk9441 on 2017-06-23.
 */
public class FlagAssembler {
    private FlagAssembler() {}

    /**
     * Return Fhir Flag from flag code.<br>
     *
     * @param patientID Patient Id
     * @param flagCode  Flag Code
     * @return Fhir Flag
     */
    public static Flag getFlag(String patientID, String flagCode) {
        Flag flag = new Flag();
        flag.setSubject(new Reference().setReference(patientID));
        flag.setCode(new CodeableConcept().addCoding(new Coding().setCode(flagCode)));
        flag.setStatus(org.hl7.fhir.dstu3.model.Flag.FlagStatus.ACTIVE);
        return flag;
    }
}
