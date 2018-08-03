package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.core.patient.PatientDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientCacheService {

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    public PatientCacheService() {
        this.patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
    }

    /**
     * Return Patient DTO.<br>
     *
     * @param patientId Patient Id
     * @return Patient DTO
     */
    public PatientDto queryPatientByPatientId(String patientId) {
        PatientDto patientDto = PatientCache.get(patientId);
        if (patientDto == null) {
            patientDto = patientAntiCorruptionServiceImp.queryPatientByPatientId(patientId);
        }
        return patientDto;
    }

    public PatientDto queryPatientByPatientIdWithPhoto(String patientId) {
        PatientDto patientDto = PatientCache.get(patientId);
        if (patientDto == null) {
            patientDto = patientAntiCorruptionServiceImp.queryPatientByPatientIdWithPhoto(patientId);
        }
        return patientDto;
    }

    /**
     * Return Patient Map with Patient Id.<br>
     *
     * @param lstPatientId Patient Id List
     * @return Patient DTO Map
     */
    public Map<String, PatientDto> queryPatientListByPatientIdList(List<String> lstPatientId) {
        if (lstPatientId == null || lstPatientId.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, PatientDto> patientDtoMap = new HashMap<>();
        for (String patientId : lstPatientId) {
            PatientDto p = PatientCache.get(patientId);
            if (p != null) {
                patientDtoMap.put(patientId, p);
            }
        }
        if (patientDtoMap.isEmpty()) {
            patientDtoMap = patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(lstPatientId);
        }
        return patientDtoMap;
    }
}
