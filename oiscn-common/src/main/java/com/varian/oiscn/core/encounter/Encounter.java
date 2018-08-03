package com.varian.oiscn.core.encounter;

import com.varian.oiscn.core.patient.Diagnosis;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by fmk9441 on 2017-03-30.
 */
@Data
public class  Encounter {
    private String id;
    private StatusEnum status;
    //private PatientSourceEnum patientSourceEnum;
    //private String inPatientArea;
    //private String bedNo;
    private String primaryPhysicianGroupID;
    private String primaryPhysicianGroupName;
    private String primaryPhysicianID;
    private String primaryPhysicianName;
    private String physicianBId;
    private String physicianBName;
    private String physicianCId;
    private String physicianCName;
    private String physicianPhone;
    //private String patientID;
    private String patientSer;
    private String age; //todo to int
    //private String organizationID;
    private String alert;
    private boolean urgent;
    private String ecogScore;
    private String ecogDesc;
    private String positiveSign;
    private String insuranceType;
    private String insuranceTypeCode;
    private String patientSource;
    private List<Diagnosis> diagnoses;
    private String physicianComment;
    private String allergyInfo;

    private String cpTemplateId;
    private List<EncounterCarePath> encounterCarePathList;

    public void addDiagnosis(Diagnosis diagnosis) {
        if (diagnoses == null) {
            diagnoses = new ArrayList();
        }
        diagnoses.add(diagnosis);
    }

    public void addEncounterCarePath(String carePathId) {
        if (encounterCarePathList == null) {
            encounterCarePathList = new ArrayList<>();
        }
        encounterCarePathList.add(new EncounterCarePath() {{
            setCpInstanceId(new Long(carePathId));
        }});
    }

    public boolean verifyMandatoryDataAndLength() {
        if (isBlank(this.primaryPhysicianGroupID)
                || isBlank(this.primaryPhysicianID)) {
            return false;
        }
        return true;
    }
}
