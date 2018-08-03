package com.varian.oiscn.patient.assembler;

import com.varian.oiscn.core.identifier.Identifier;
import com.varian.oiscn.core.identifier.IdentifierStatusEnum;
import com.varian.oiscn.core.identifier.IdentifierTypeEnum;
import com.varian.oiscn.core.identifier.IdentifierUseEnum;
import com.varian.oiscn.core.patient.*;

/**
 * Created by gbt1220 on 3/28/2017.
 */
public class PatientAssembler {

    private PatientAssembler() {
    }

    public static Patient getPatient(RegistrationVO registrationVO) {
        Patient patient = new Patient();
        patient.setHisId(registrationVO.getHisId());
        patient.setPatientSer(Long.parseLong(registrationVO.getPatientSer()));
        patient.setRadiationId(registrationVO.getAriaId());
        patient.setNationalId(registrationVO.getNationalId());
        patient.setChineseName(registrationVO.getChineseName());
        patient.setEnglishName(registrationVO.getEnglishName());
        patient.setPinyin(registrationVO.getPinyin());
        patient.setGender(GenderEnum.fromCode(registrationVO.getGender()));
        patient.setBirthDate(registrationVO.getBirthday());
        patient.setPatientStatus(PatientStatusEnum.N);
        patient.setVip(VIPEnum.N);
        patient.addIdentifier(new Identifier("", patient.getHisId(),
                IdentifierTypeEnum.HC, IdentifierStatusEnum.A, IdentifierUseEnum.USUAL));
        patient.addIdentifier(new Identifier("", patient.getRadiationId(),
                IdentifierTypeEnum.JHN, IdentifierStatusEnum.A, IdentifierUseEnum.USUAL));
        patient.addIdentifier(new Identifier("", patient.getNationalId(),
                IdentifierTypeEnum.BRN, IdentifierStatusEnum.A, IdentifierUseEnum.OFFICIAL));
        patient.addHumanName(new HumanName("", "", "", registrationVO.getChineseName(), "", "", "", NameTypeEnum.IDE));
        patient.addHumanName(new HumanName("", "", "", registrationVO.getEnglishName(), "", "", "", NameTypeEnum.ABC));
        patient.setAddress(registrationVO.getAddress());
        patient.setMobilePhone(registrationVO.getTelephone());;


        Contact contact = new Contact();
        contact.setRelationship(RelationshipEnum.FAMILY);
        contact.setName(registrationVO.getContactPerson());
        contact.setMobilePhone(registrationVO.getContactPhone());
        patient.addContact(contact);
        
        patient.setPhoto(registrationVO.getPhoto());
        patient.setPatientHistory(registrationVO.getPatientHistory() == null ? "" : registrationVO.getPatientHistory());
        return patient;
    }
}
