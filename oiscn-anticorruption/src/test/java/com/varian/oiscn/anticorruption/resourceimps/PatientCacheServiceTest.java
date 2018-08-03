package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.oiscn.anticorruption.datahelper.MockPatientUtil;
import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.core.patient.PatientDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientCacheService.class, PatientCache.class})
public class PatientCacheServiceTest {

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private PatientCacheService patientCacheService;

    @Before
    public void setup() throws Exception {
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);

        patientCacheService = new PatientCacheService();
    }

    @Test
    public void testQueryPatientByPatientId() {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(anyString())).thenReturn(patientDto);
        Assert.assertEquals(patientDto, patientCacheService.queryPatientByPatientId("patientId"));
    }

    @Test
    public void testQueryPatientByPatientIdWithPhoto() {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientIdWithPhoto(anyString())).thenReturn(patientDto);
        Assert.assertEquals(patientDto, patientCacheService.queryPatientByPatientIdWithPhoto("patientId"));
    }

    @Test
    public void testQueryPatientListAndIdListIsEmpty() {
        Assert.assertEquals(0, patientCacheService.queryPatientListByPatientIdList(new ArrayList<>()).size());
    }

    @Test
    public void testQueryPatientListAndCacheIsNotEmpty() {
        PowerMockito.mockStatic(PatientCache.class);
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        PowerMockito.when(PatientCache.get(anyString())).thenReturn(patientDto);
        Map<String, PatientDto> result = patientCacheService.queryPatientListByPatientIdList(Arrays.asList("patientId"));
        Assert.assertEquals(patientDto, result.get("patientId"));
    }
}
