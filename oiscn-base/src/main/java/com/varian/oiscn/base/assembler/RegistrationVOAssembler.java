package com.varian.oiscn.base.assembler;

import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.practitioner.PractitionerTreeNode;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 6/13/2017.
 */
public class RegistrationVOAssembler {

    private RegistrationVOAssembler() {
    }

    public static RegistrationVO getRegistrationVO(PatientDto patientDto) {
        RegistrationVO registrationVO = new RegistrationVO();
        registrationVO.setAriaId(patientDto.getAriaId());
        registrationVO.setHisId(patientDto.getHisId());
        registrationVO.setNationalId(patientDto.getNationalId());
        registrationVO.setChineseName(patientDto.getChineseName());
        registrationVO.setEnglishName(patientDto.getEnglishName());
        registrationVO.setPinyin(patientDto.getPinyin());
        registrationVO.setGender(patientDto.getGender());
        registrationVO.setBirthday(patientDto.getBirthday());
        registrationVO.setContactPerson(patientDto.getContactPerson());
        registrationVO.setContactPhone(patientDto.getContactPhone());
        registrationVO.setPatientSer(patientDto.getPatientSer());
        registrationVO.setPhysicianGroupId(patientDto.getPhysicianGroupId());
        registrationVO.setPhysicianId(patientDto.getPhysicianId());
        registrationVO.setPhysicianName(patientDto.getPhysicianName());
        registrationVO.setPhysicianPhone(patientDto.getPhysicianPhone());
        registrationVO.setTelephone(patientDto.getTelephone());
        registrationVO.setAddress(patientDto.getAddress());
        registrationVO.setCpTemplateId(patientDto.getCpTemplateId());
        registrationVO.setCpTemplateName(patientDto.getCpTemplateName());
        registrationVO.setInsuranceType(patientDto.getInsuranceType());
        registrationVO.setPatientSource(patientDto.getPatientSource());
        // ECOG, Positive Sign will keep in Local DB.
        // registrationVO.setEcogScore(patientDto.getEcogScore());
        // registrationVO.setEcogDesc(patientDto.getEcogDesc());
        // registrationVO.setPositiveSign(patientDto.getPositiveSign());
        registrationVO.setPatientHistory(patientDto.getPatientHistory());
        // From Aria to registrationVO
        registrationVO.setPhotoByte(patientDto.getPhoto());
        return registrationVO;
    }

    public static PatientDto getPatientDto(RegistrationVO registrationVO) {
        PatientDto patientDto = new PatientDto();
        patientDto.setAriaId(registrationVO.getAriaId());
        patientDto.setHisId(registrationVO.getHisId());
        patientDto.setNationalId(registrationVO.getNationalId());
        patientDto.setChineseName(registrationVO.getChineseName());
        patientDto.setEnglishName(registrationVO.getEnglishName());
        patientDto.setPinyin(registrationVO.getPinyin());
        patientDto.setGender(registrationVO.getGender());
        patientDto.setBirthday(registrationVO.getBirthday());
        patientDto.setContactPerson(registrationVO.getContactPerson());
        patientDto.setContactPhone(registrationVO.getContactPhone());
        patientDto.setPatientSer(registrationVO.getPatientSer());
        patientDto.setPhysicianGroupId(registrationVO.getPhysicianGroupId());
        patientDto.setPhysicianId(registrationVO.getPhysicianId());
        patientDto.setPhysicianName(registrationVO.getPhysicianName());
        patientDto.setTelephone(registrationVO.getTelephone());
        patientDto.setAddress(registrationVO.getAddress());
        patientDto.setPhoto(registrationVO.getPhotoByte());
        patientDto.setCpTemplateId(registrationVO.getCpTemplateId());
        patientDto.setCpTemplateName(registrationVO.getCpTemplateName());
        patientDto.setInsuranceType(registrationVO.getInsuranceType());
        patientDto.setPatientSource(registrationVO.getPatientSource());
        // ECOG, Positive Sign will keep in Local DB.
        patientDto.setEcogScore(registrationVO.getEcogScore());
        patientDto.setEcogDesc(registrationVO.getEcogDesc());
        patientDto.setPositiveSign(registrationVO.getPositiveSign());
        patientDto.setPatientHistory(registrationVO.getPatientHistory());
        return patientDto;
    }

    public static Diagnosis getDiagnosis(RegistrationVO registrationVO) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode(registrationVO.getDiagnosisCode());
        diagnosis.setDesc(registrationVO.getDiagnosisDesc());
        diagnosis.setRecurrence(isNotEmpty(registrationVO.getRecurrent()) ? Boolean.parseBoolean(registrationVO.getRecurrent()) : null);
        if (isNotEmpty(registrationVO.getTcode()) &&
                isNotEmpty(registrationVO.getNcode()) &&
                isNotEmpty(registrationVO.getMcode())) {
            Diagnosis.Staging diagnosisStaging = new Diagnosis.Staging();
            diagnosisStaging.setSchemeName(registrationVO.getStagingScheme());
            diagnosisStaging.setStage(registrationVO.getStaging());
            diagnosisStaging.setTcode(registrationVO.getTcode());
            diagnosisStaging.setNcode(registrationVO.getNcode());
            diagnosisStaging.setMcode(registrationVO.getMcode());
            diagnosisStaging.setDate(registrationVO.getDiagnosisDate());
            diagnosis.setStaging(diagnosisStaging);
        }
        diagnosis.setDiagnosisDate(registrationVO.getDiagnosisDate());
        diagnosis.setBodypartCode(registrationVO.getBodypart());
        diagnosis.setBodypartDesc(registrationVO.getBodypartDesc());
        diagnosis.setDiagnosisNote(registrationVO.getDiagnosisNote());
        return diagnosis;
    }

    public static void assemblerDiagnosisData2RegistrationVO(RegistrationVO registrationVO, Diagnosis diagnosis) {
        registrationVO.setDiagnosisCode(diagnosis.getCode());
        registrationVO.setDiagnosisDesc(diagnosis.getDesc());
        registrationVO.setRecurrent(diagnosis.getRecurrence() == null ? null : diagnosis.getRecurrence().toString());
        if (diagnosis.getStaging() != null) {
            registrationVO.setStaging(diagnosis.getStaging().getStage());
            registrationVO.setTcode(diagnosis.getStaging().getTcode());
            registrationVO.setNcode(diagnosis.getStaging().getNcode());
            registrationVO.setMcode(diagnosis.getStaging().getMcode());
        }
        registrationVO.setDiagnosisDate(diagnosis.getDiagnosisDate());
        registrationVO.setBodypart(diagnosis.getBodypartCode());
        registrationVO.setBodypartDesc(diagnosis.getBodypartDesc());
        registrationVO.setDiagnosisNote(diagnosis.getDiagnosisNote());
    }

    public static Patient getPatient(RegistrationVO registrationVO) {
        Patient patient = new Patient();
        if(StringUtils.isNotBlank(registrationVO.getPatientSer())) {
            patient.setPatientSer(Long.parseLong(registrationVO.getPatientSer()));
        }
        patient.setChineseName(registrationVO.getChineseName());
        patient.setPhoto(registrationVO.getPhoto());
        patient.setRadiationId(registrationVO.getAriaId());
        patient.setNationalId(registrationVO.getNationalId());
        String gender = registrationVO.getGender();
        patient.setGender(GenderEnum.fromCode(gender));
        patient.setBirthDate(registrationVO.getBirthday());
        patient.setHisId(registrationVO.getHisId());
        patient.setAddress(registrationVO.getAddress());
        patient.setMobilePhone(registrationVO.getTelephone());
        patient.setPatientHistory(registrationVO.getPatientHistory());
        Contact contact = new Contact();
        contact.setName(registrationVO.getContactPerson());
        contact.setMobilePhone(registrationVO.getContactPhone());
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        patient.setContacts(contacts);

        return patient;
    }

    public static Encounter getEncounter(Encounter encounter, RegistrationVO registrationVO) {
        List<Diagnosis> diagnosisList = new ArrayList<>();
        Diagnosis diagnosis = new Diagnosis();
        encounter.setAge(registrationVO.getAge());
        encounter.setAlert(registrationVO.getWarningText());
        encounter.setUrgent(registrationVO.isUrgent());
        encounter.setInsuranceTypeCode(registrationVO.getInsuranceType());
        encounter.setAllergyInfo(registrationVO.getAllergyInfo());
        encounter.setPhysicianComment(registrationVO.getPhysicianComment());

        encounter.setPatientSource(registrationVO.getPatientSource());
        diagnosis.setDesc(registrationVO.getDiagnosisDesc());
        diagnosis.setBodypartCode(registrationVO.getBodypart());
        diagnosis.setBodypartDesc(registrationVO.getBodypartDesc());
        diagnosis.setCode(registrationVO.getDiagnosisCode());
        diagnosis.setDiagnosisNote(registrationVO.getDiagnosisNote());
        diagnosis.setDiagnosisDate(registrationVO.getDiagnosisDate());
        if(!StringUtils.isEmpty(registrationVO.getTcode()) || !StringUtils.isEmpty(registrationVO.getNcode()) ||
                !StringUtils.isEmpty(registrationVO.getNcode()) || !StringUtils.isEmpty(registrationVO.getStaging())){
            Diagnosis.Staging staging = new Diagnosis.Staging   ();
            staging.setStage(registrationVO.getStaging());
            staging.setTcode(registrationVO.getTcode());
            staging.setNcode(registrationVO.getNcode());
            staging.setMcode(registrationVO.getMcode());
            diagnosis.setStaging(staging);
        }
        if(registrationVO.getRecurrent() != null){
            Boolean recurrence = Boolean.valueOf(registrationVO.getRecurrent());
            diagnosis.setRecurrence(recurrence);
        }
        diagnosisList.add(diagnosis);
        encounter.setDiagnoses(diagnosisList);

        encounter.setPositiveSign(registrationVO.getPositiveSign());
        encounter.setEcogDesc(registrationVO.getEcogDesc());
        encounter.setEcogScore(registrationVO.getEcogScore());

        PractitionerTreeNode practitionerTreeNode = GroupPractitionerHelper.getPractitionerTreeNodeByName(GroupPractitionerHelper.getOncologyGroupTreeNode(),registrationVO.getPhysicianName());
        if(practitionerTreeNode != null){
            String primaryPhysicianID = practitionerTreeNode.getId();
            String primaryPhysicianGroupID = GroupPractitionerHelper.getRegisterGroupByPractitionerId(GroupPractitionerHelper.getOncologyGroupTreeNode(),String.valueOf(primaryPhysicianID)).getId();
            encounter.setPrimaryPhysicianID(primaryPhysicianID);
            encounter.setPrimaryPhysicianName(practitionerTreeNode.getName());
            encounter.setPrimaryPhysicianGroupID(primaryPhysicianGroupID);
            PractitionerTreeNode practitionerBTreeNode = GroupPractitionerHelper.getPractitionerTreeNodeByName(GroupPractitionerHelper.getOncologyGroupTreeNode(),registrationVO.getPhysicianBName());
            if(practitionerBTreeNode != null){
                encounter.setPhysicianBId(practitionerBTreeNode.getId());
                encounter.setPhysicianBName(practitionerBTreeNode.getName());
                PractitionerTreeNode practitionerCTreeNode = GroupPractitionerHelper.getPractitionerTreeNodeByName(GroupPractitionerHelper.getOncologyGroupTreeNode(),registrationVO.getPhysicianCName());
                if(practitionerCTreeNode != null){
                    encounter.setPhysicianCId(practitionerCTreeNode.getId());
                    encounter.setPhysicianCName(practitionerCTreeNode.getName());
                }
            }
        }
        return encounter;
    }
}
