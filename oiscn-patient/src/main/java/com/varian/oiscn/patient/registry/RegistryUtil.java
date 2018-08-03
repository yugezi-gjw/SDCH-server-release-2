package com.varian.oiscn.patient.registry;

import java.util.Date;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class RegistryUtil {
    private RegistryUtil() {
    }

    public static RegistryVerifyStatusEnum verifyNewPatientRegistry(Patient patient, Encounter encounter) {
        PatientAntiCorruptionServiceImp antiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        PatientDto pdto;
        if (isNotEmpty(patient.getHisId())) {
            pdto = antiCorruptionServiceImp.queryPatientByHisId(patient.getHisId());
            if (pdto != null) {
                return RegistryVerifyStatusEnum.DUPLICATE_HIS;
            }
        }
        if (isNotEmpty(patient.getRadiationId())) {
            pdto = antiCorruptionServiceImp.queryPatientByAriaId(patient.getRadiationId());
            if (pdto != null) {
                return RegistryVerifyStatusEnum.DUPLICATE_VID;
            }
        }
        if (encounter.getDiagnoses() != null
                && !encounter.getDiagnoses().isEmpty()
                && encounter.getDiagnoses().get(0).getDiagnosisDate() != null
                && encounter.getDiagnoses().get(0).getDiagnosisDate().after(new Date())) {
            return RegistryVerifyStatusEnum.INVALID_DIAGNOSIS_DATE;
        }
        return RegistryVerifyStatusEnum.PASS;
    }
}
