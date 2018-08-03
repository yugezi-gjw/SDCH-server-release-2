package com.varian.oiscn.core.encounter;

import com.varian.oiscn.core.patient.Diagnosis;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fmk9441 on 2017-03-30.
 */
@Data
public class EncounterV2 {
    private String id;
    private StatusEnum status;
    private PatientSourceEnum patientSourceEnum;
    private String inPatientArea;
    private String bedNo;
    private String primaryPhysicianGroupID;
    private String primaryPhysicianGroupName;
    private String primaryPhysicianID;
    private String physicianBId;
    private String physicianBName;
    private String physicianCId;
    private String physicianCName;
    private String patientId;
    private String patientSer;
    private String age;
    private String organizationID;
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

    private List<EncounterCarePath> encounterCarePathList;

    public void addDiagnosis(Diagnosis diagnosis) {
        if (diagnoses == null) {
            diagnoses = new ArrayList();
        }
        diagnoses.add(diagnosis);
    }
}
