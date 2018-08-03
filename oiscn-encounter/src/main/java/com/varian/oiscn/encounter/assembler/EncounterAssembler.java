package com.varian.oiscn.encounter.assembler;

import com.varian.oiscn.base.assembler.RegistrationVOAssembler;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.encounter.view.EncounterVO;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by gbt1220 on 3/30/2017.
 */
public class EncounterAssembler {
    private EncounterAssembler() {
    }

    public static Encounter getEncounter(RegistrationVO registrationVO) {
        Encounter encounter = new Encounter();
//        encounter.setPatientID(registrationVO.getId());
        encounter.setPatientSer(registrationVO.getPatientSer());
        encounter.setPrimaryPhysicianGroupID(registrationVO.getPhysicianGroupId());
        encounter.setPrimaryPhysicianGroupName(registrationVO.getPhysicianGroupName());
        encounter.setPrimaryPhysicianID(registrationVO.getPhysicianId());
        encounter.setPrimaryPhysicianName(registrationVO.getPhysicianName());
        encounter.setPhysicianBId(registrationVO.getPhysicianBId());
        encounter.setPhysicianBName(registrationVO.getPhysicianBName());
        encounter.setPhysicianCId(registrationVO.getPhysicianCId());
        encounter.setPhysicianCName(registrationVO.getPhysicianCName());
        encounter.setStatus(StatusEnum.IN_PROGRESS);//active patient
        encounter.setUrgent(registrationVO.isUrgent());
        encounter.setAlert(registrationVO.getWarningText());
        Diagnosis diagnosis = RegistrationVOAssembler.getDiagnosis(registrationVO);
        diagnosis.setPatientID(registrationVO.getId());
        encounter.addDiagnosis(diagnosis);
        encounter.setEcogScore(registrationVO.getEcogScore());
        encounter.setEcogDesc(registrationVO.getEcogDesc());
        encounter.setPositiveSign(registrationVO.getPositiveSign());
        encounter.setInsuranceTypeCode(registrationVO.getInsuranceTypeCode());
        encounter.setInsuranceType(registrationVO.getInsuranceType());
        encounter.setPatientSource(registrationVO.getPatientSource());
        encounter.setAge(registrationVO.getAge());
        encounter.setAllergyInfo(registrationVO.getAllergyInfo());
        encounter.setPhysicianComment(registrationVO.getPhysicianComment());
        if(StringUtils.isNotEmpty(registrationVO.getCarePathInstanceId())) {
            encounter.setEncounterCarePathList(Arrays.asList(new EncounterCarePath() {{
                setCpInstanceId(new Long(registrationVO.getCarePathInstanceId()));
            }}));
        }
        return encounter;
    }

    public static EncounterVO getEncounterVO(Encounter encounter) {
        EncounterVO encounterVO = new EncounterVO();
//        encounterVO.setPatientID(encounter.getPatientID());
        Diagnosis diagnosis = new Diagnosis();
        if (encounter.getDiagnoses() != null && !encounter.getDiagnoses().isEmpty()) {
            diagnosis = encounter.getDiagnoses().get(0);
        }
        encounterVO.setDiagnosis(diagnosis.getDesc());
        encounterVO.setBodypartDesc(diagnosis.getBodypartDesc());
        encounterVO.setBodypart(diagnosis.getBodypartCode());
        if (null != diagnosis.getStaging()) {
            encounterVO.setStaging(diagnosis.getStaging().getStage());
            encounterVO.setTcode(diagnosis.getStaging().getTcode());
            encounterVO.setNcode(diagnosis.getStaging().getNcode());
            encounterVO.setMcode(diagnosis.getStaging().getMcode());
        }
        encounterVO.setUrgent(encounter.isUrgent());
        encounterVO.setWarningText(encounter.getAlert());
        encounterVO.setEcogScore(encounter.getEcogScore());
        encounterVO.setEcogDesc(encounter.getEcogDesc());
        encounterVO.setPositiveSign(encounter.getPositiveSign());
        encounterVO.setInsuranceType(encounter.getInsuranceType());
        encounterVO.setPatientSource(encounter.getPatientSource());
        encounterVO.setAge(encounter.getAge());
        encounterVO.setAllergyInfo(encounter.getAllergyInfo());
        return encounterVO;
    }
}
