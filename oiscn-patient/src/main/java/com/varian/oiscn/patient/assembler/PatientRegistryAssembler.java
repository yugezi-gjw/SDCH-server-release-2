package com.varian.oiscn.patient.assembler;

import com.varian.oiscn.anticorruption.resourceimps.CoverageAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.DiagnosisAntiCorruptionServiceImp;
import com.varian.oiscn.base.codesystem.PatientLabelPool;
import com.varian.oiscn.base.util.PhotoUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Contact;
import com.varian.oiscn.core.patient.GenderEnum;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public class PatientRegistryAssembler {

    private PatientRegistryAssembler() {}

    public static PatientDto getPatientDto(Patient patient, Encounter encounter, Configuration configuration) {
        PatientDto patientDto = new PatientDto();
        //assemble patient data
        patientDto.setAriaId(patient.getRadiationId());
        patientDto.setHisId(patient.getHisId());
        patientDto.setNationalId(patient.getNationalId());
        patientDto.setChineseName(patient.getChineseName());
        patientDto.setEnglishName(patient.getEnglishName());
        patientDto.setPinyin(patient.getPinyin());
        patientDto.setGender(GenderEnum.getDisplay(patient.getGender()));
        patientDto.setBirthday(patient.getBirthDate());
        if (patient.getContacts() != null && !patient.getContacts().isEmpty()) {
            patientDto.setContactPerson(patient.getContacts().get(0).getName());
            patientDto.setContactPhone(patient.getContacts().get(0).getMobilePhone());
        }
        patientDto.setPatientSer(patient.getPatientSer() == null ? "" : patient.getPatientSer().toString());
        patientDto.setTelephone(patient.getMobilePhone());
        patientDto.setAddress(patient.getAddress());
        patientDto.setPhoto(PhotoUtil.decode(patient.getPhoto()));
        patientDto.setPatientHistory(patient.getPatientHistory());

        //assemble encounter data
        patientDto.setPhysicianGroupId(encounter.getPrimaryPhysicianGroupID());
        patientDto.setPhysicianId(encounter.getPrimaryPhysicianID());
        patientDto.setPhysicianName(encounter.getPrimaryPhysicianName());
        patientDto.setInsuranceType(encounter.getInsuranceType());
        patientDto.setPatientSource(encounter.getPatientSource());
        patientDto.setPositiveSign(encounter.getPositiveSign());

        patientDto = assembleAlert(patientDto, StringUtils.trimToEmpty(encounter.getAlert()), configuration);
        return patientDto;
    }

    private static PatientDto assembleAlert(PatientDto patientDto, String alert, Configuration configuration) {
        String alertCode = PatientLabelPool.get(configuration.getAlertPatientLabelDesc());
        if (StringUtils.isEmpty(alertCode)) {
            log.error("Can't get the alert code, please check the alert config.");
            return patientDto;
        }
        PatientDto.PatientLabel alertLabel = new PatientDto.PatientLabel();
        alertLabel.setLabelId(alertCode);
        alertLabel.setLabelTag(configuration.getAlertPatientLabelDesc());
        alertLabel.setLabelText(alert);
        patientDto.addPatientLabel(alertLabel);
        return patientDto;
    }

    public static Patient getPatientFromARIA(PatientDto patientInARIA) {
        Patient patient = new Patient();
        if(StringUtils.isNotBlank(patientInARIA.getPatientSer())) {
            patient.setPatientSer(Long.parseLong(patientInARIA.getPatientSer()));
        }
        patient.setHisId(patientInARIA.getHisId());
        patient.setRadiationId(patientInARIA.getAriaId()); //VID
        patient.setNationalId(patientInARIA.getNationalId());
        patient.setChineseName(patientInARIA.getChineseName());
        patient.setEnglishName(patientInARIA.getEnglishName());
        patient.setPinyin(patientInARIA.getPinyin());
        patient.setGender(GenderEnum.fromCode(patientInARIA.getGender()));
        patient.setBirthDate(patientInARIA.getBirthday());
        Contact contact = new Contact();
        contact.setName(patientInARIA.getContactPerson());
        contact.setMobilePhone(patientInARIA.getContactPhone());
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        patient.setContacts(contacts);
        patient.setMobilePhone(patientInARIA.getTelephone());
        patient.setAddress(patientInARIA.getAddress());
        patient.setPhoto(Base64.encodeBase64String(patientInARIA.getPhoto()));
        patient.setPatientHistory(patientInARIA.getPatientHistory());

        return patient;
    }

    public static Encounter getEncounterFromARIA(Encounter encounter, PatientDto patientInARIA) {
        if (isNotEmpty(patientInARIA.getPhysicianId())) {
            encounter.setPrimaryPhysicianID(patientInARIA.getPhysicianId());
            encounter.setPrimaryPhysicianName(patientInARIA.getPhysicianName());
            encounter.setPhysicianPhone(patientInARIA.getPhysicianPhone());
        }
        if (isNotEmpty(patientInARIA.getPhysicianGroupId())) {
            encounter.setPrimaryPhysicianGroupID(patientInARIA.getPhysicianGroupId());
        }
        encounter.setEcogScore(patientInARIA.getEcogScore());
        encounter.setEcogDesc(patientInARIA.getEcogDesc());
        encounter.setPositiveSign(patientInARIA.getPositiveSign());
        encounter.setPatientSource(patientInARIA.getPatientSource());

        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = new CoverageAntiCorruptionServiceImp();
        CoverageDto coverageDto = coverageAntiCorruptionServiceImp.queryByPatientId(patientInARIA.getPatientSer());
        if (coverageDto != null) {
            encounter.setInsuranceTypeCode(coverageDto.getInsuranceTypeCode());
            encounter.setInsuranceType(coverageDto.getInsuranceTypeDesc());
        }
        DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp = new DiagnosisAntiCorruptionServiceImp();
        encounter.setDiagnoses(diagnosisAntiCorruptionServiceImp.queryDiagnosisListByPatientID(patientInARIA.getPatientSer()));

        return encounter;
    }
}
