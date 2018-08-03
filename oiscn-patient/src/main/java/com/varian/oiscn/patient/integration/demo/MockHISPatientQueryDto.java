package com.varian.oiscn.patient.integration.demo;

import com.varian.oiscn.core.common.KeyValuePair;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by gbt1220 on 1/2/2018.
 */
@Data
public class MockHISPatientQueryDto {
    private String hisId;
    private String nationalId;
    private String fullName;
    private String gender;
    private Date birthDate;
    private String age;
    private String telephone;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String medicalHistory;

    private boolean urgent;
    private String positiveSign;
//    private String insuranceTypeCode;
    private String insuranceType;
    private String patientSource;

    private String physicianName;
    private String physicianBName;
    private String physicianCName;
    private String physicianComment;
    private String allergyInfo;
    private String bodypart;
    private Date diagnosisDate;
    private String diagnosisNote;
    private String ecogScore;
    private String ecogDesc;
    private String alert;
    private String recurrent;
    private String physicianGroupId;
    private String physicianId;
    private String diagnosisCode;
    private String diagnosisDesc;
    private String tcode;
    private String ncode;
    private String mcode;
    private String staging;

    private List<KeyValuePair> dynamicFormItems;
}
