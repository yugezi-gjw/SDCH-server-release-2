package com.varian.oiscn.patient.service;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.core.patient.PatientDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bhp9696 on 2018/3/22.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RefreshPatientCacheService.class,PatientCache.class})
public class RefreshPatientCacheServiceTest {
    private RefreshPatientCacheService refreshPatientCacheService;
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;
    private PatientServiceImp patientService;

    @Before
    public void setup(){
        try {
            patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(patientAntiCorruptionServiceImp);

            patientService = PowerMockito.mock(PatientServiceImp.class);
            PowerMockito.whenNew(PatientServiceImp.class).withAnyArguments().thenReturn(patientService);
            PowerMockito.mockStatic(PatientCache.class);
            refreshPatientCacheService = new RefreshPatientCacheService();
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRun() throws Exception {
        List<String> patientSerList = Arrays.asList("1121","1222");
        PowerMockito.when(patientService.queryAllActivePatientSer()).thenReturn(patientSerList);

        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(patientSerList)).thenReturn(new HashMap<String, PatientDto>());

        PowerMockito.when(PatientCache.allKeys()).thenReturn(Arrays.asList("1121","1222","1223"));
        PowerMockito.doNothing().when(PatientCache.class,"remove",new Object[]{"1223"});
        refreshPatientCacheService.run();

    }
}
