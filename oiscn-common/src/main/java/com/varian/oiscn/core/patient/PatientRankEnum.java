package com.varian.oiscn.core.patient;

/**
 * Created by fmk9441 on 2017-07-04.
 */
public enum PatientRankEnum {
    CREATION_DATE;

    public static String getDisplay(PatientRankEnum patientRankEnum) {
        if (CREATION_DATE.equals(patientRankEnum)) {
            return "CreationDate";
        } else {
            return "entered-in-error";
        }
    }
}
