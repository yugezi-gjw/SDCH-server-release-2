package com.varian.oiscn.core.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by gbt1220 on 4/5/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diagnosis {
    private String patientID; //todo modify to patientSer
    private String code;
    private String desc;
    private String system;
    private Boolean recurrence;
    private Staging staging;
    private Date diagnosisDate; //todo modify DataBase column to diagnosisDate
    private String bodypartCode;
    private String bodypartDesc;
    private String diagnosisNote;

    @Data
    public static class Staging {
        private String schemeName;
        private String basisCode;
        private String stage;
        private String tcode;
        private String ncode;
        private String mcode;
        private Date date;
    }
}
