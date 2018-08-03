package com.varian.oiscn.encounter.view;

import lombok.Data;

/**
 * Created by gbt1220 on 6/14/2017.
 */
@Data
public class EncounterVO {
    private String patientID;
    private String diagnosis;
    private String bodypart;
    private String bodypartDesc;
    private String staging;
    private String tcode;
    private String ncode;
    private String mcode;
    private String warningText;
    private boolean urgent;
    private String insuranceTypeCode;
    private String insuranceType;
    private String patientSource;
    private String positiveSign;
    /**
     * ECOG Score: 0,1,2,3,4 or free text,32 English chars
     */
    private String ecogScore;
    /** ECOG Description */
    private String ecogDesc;
    private String physicianComment;
    private String age;
    private String allergyInfo;
}
