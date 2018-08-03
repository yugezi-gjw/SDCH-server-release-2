package com.varian.oiscn.anticorruption.base;

/**
 * Created by gbt1220 on 2/2/2018.
 */
public class PatientIdMapper {

    public static final String IDENTIFIER_MAPPER_TO_HIS_ID = "hisId";
    public static final String IDENTIFIER_MAPPER_TO_ARIA_ID = "ariaId";

    private static String patientId1Mapper;

    private static String patientId2Mapper;

    public static void init(String patientId1, String patientId2) {
        patientId1Mapper = patientId1;
        patientId2Mapper = patientId2;
    }

    public static String getPatientId1Mapper() {
        return patientId1Mapper;
    }

    public static String getPatientId2Mapper() {
        return patientId2Mapper;
    }
}
