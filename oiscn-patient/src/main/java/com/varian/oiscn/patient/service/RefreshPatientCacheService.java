package com.varian.oiscn.patient.service;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.core.user.UserContext;

import java.util.List;

/**
 * Created by bhp9696 on 2018/2/2.
 */
public class RefreshPatientCacheService implements Runnable {
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
    private PatientServiceImp patientService = new PatientServiceImp(new UserContext());
    @Override
    public void run() {
        List<String> patientSerList = patientService.queryAllActivePatientSer();
        patientAntiCorruptionServiceImp.syncPatientListByPatientIdList(patientSerList);
        List<String> keys = PatientCache.allKeys();
        if(keys!= null && !keys.isEmpty()){
            for(String key:keys){
                if(!patientSerList.contains(key)){
                    PatientCache.remove(key);
                }
            }
        }
    }
}
