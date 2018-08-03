package com.varian.oiscn.anticorruption.converter;

import com.varian.oiscn.core.patient.GenderEnum;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Enumerations;

/**
 * Created by fmk9441 on 2017-01-18.
 */
public class EnumerationHelper {
    private EnumerationHelper() {

    }

    public static Enumerations.AdministrativeGender getGender(String gender) {
        Enumerations.AdministrativeGender result;
        if (StringUtils.equalsIgnoreCase(gender, Enumerations.AdministrativeGender.MALE.name())) {
            result = Enumerations.AdministrativeGender.MALE;
        } else if (StringUtils.equalsIgnoreCase(gender, Enumerations.AdministrativeGender.FEMALE.name())) {
            result = Enumerations.AdministrativeGender.FEMALE;
        } else if (StringUtils.equalsIgnoreCase(gender, Enumerations.AdministrativeGender.OTHER.name())) {
            result = Enumerations.AdministrativeGender.OTHER;
        } else {
            result = Enumerations.AdministrativeGender.UNKNOWN;
        }
        return result;
    }

    /**
     * Translate local gender to fhir gender
     *
     * @param gender local gender
     * @return fhir gender
     */
    public static Enumerations.AdministrativeGender getGender(GenderEnum gender) {
        Enumerations.AdministrativeGender result;
        String genderStr = GenderEnum.getDisplay(gender);
        if (StringUtils.equalsIgnoreCase(genderStr, Enumerations.AdministrativeGender.MALE.name())) {
            result = Enumerations.AdministrativeGender.MALE;
        } else if (StringUtils.equalsIgnoreCase(genderStr, Enumerations.AdministrativeGender.FEMALE.name())) {
            result = Enumerations.AdministrativeGender.FEMALE;
        } else if (StringUtils.equalsIgnoreCase(genderStr, Enumerations.AdministrativeGender.OTHER.name())) {
            result = Enumerations.AdministrativeGender.OTHER;
        } else {
            result = Enumerations.AdministrativeGender.UNKNOWN;
        }
        return result;
    }
}
