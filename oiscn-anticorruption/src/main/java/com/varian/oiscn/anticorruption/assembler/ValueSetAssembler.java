package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.ValueSet;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;

/**
 * Created by fmk9441 on 2017-06-16.
 */
public class ValueSetAssembler {
    private ValueSetAssembler() {

    }

    /**
     * Return Code System from Fhir ValueSet.<br>
     *
     * @param valueSet Fhir ValueSet
     * @return Code System
     */
    public static CodeSystem getCodeSystem(ValueSet valueSet){
        CodeSystem codeSystem = new CodeSystem();
        if (valueSet == null) {
            return codeSystem;
        }
        if(valueSet.hasId()){
            codeSystem.setSystem(valueSet.getId());
        }
        if(valueSet.hasName()){
            codeSystem.setCode(valueSet.getName());
        }
        if(valueSet.hasCompose()){
            ValueSet.ValueSetComposeComponent valueSetComposeComponent = valueSet.getCompose();
            if(valueSetComposeComponent.hasInclude()){
                valueSetComposeComponent.getInclude().get(0).getConcept().forEach(conceptReferenceComponent -> {
                    CodeValue codeValue = new CodeValue();
                    codeValue.setCode(conceptReferenceComponent.getCode());
                    codeValue.setDesc(conceptReferenceComponent.getDisplay());
                    codeSystem.addCodeValue(codeValue);
                });
            }
        }
        return codeSystem;
    }
}