package com.varian.oiscn.base.applicationlaunch;

import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientCacheService;
import com.varian.oiscn.base.applicationlanuch.ApplicationLaunchContentBuilder;
import com.varian.oiscn.base.applicationlanuch.ApplicationLaunchParam;
import com.varian.oiscn.core.patient.PatientDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by gbt1220 on 7/27/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationLaunchContentBuilder.class,PatientIdMapper.class, PatientAntiCorruptionServiceImp.class})
public class ApplicationLaunchContentBuilderTest {

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception{
        PowerMockito.mockStatic(PatientIdMapper.class);
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(patientAntiCorruptionServiceImp);
    }

    @Test
    public void getLaunchContentTestID1HisId() throws Exception {
        ApplicationLaunchParam param = givenParam();
        PatientIdMapper.init(PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID, PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID);

        PatientDto dto = PowerMockito.mock(PatientDto.class);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(Matchers.anyString())).thenReturn(dto);
        PowerMockito.when(PatientIdMapper.getPatientId1Mapper()).thenReturn(PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID);
        PatientCacheService patientCacheService = PowerMockito.mock(PatientCacheService.class);
        PowerMockito.whenNew(PatientCacheService.class).withAnyArguments().thenReturn(patientCacheService);
        PowerMockito.when(patientCacheService.queryPatientByPatientId(Matchers.anyString())).thenReturn(dto);
        Assert.assertNotNull(ApplicationLaunchContentBuilder.getLaunchContent(param));
    }

    @Test
    public void getLaunchContentTestID1AriaId() throws Exception {
        ApplicationLaunchParam param = givenParam();
        PatientIdMapper.init(PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID, PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID);

        PatientDto dto = PowerMockito.mock(PatientDto.class);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(Matchers.anyString())).thenReturn(dto);

        PowerMockito.mockStatic(PatientIdMapper.class);
        PowerMockito.when(PatientIdMapper.getPatientId1Mapper()).thenReturn(PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(Matchers.anyString())).thenReturn(new PatientDto());
        Assert.assertNotNull(ApplicationLaunchContentBuilder.getLaunchContent(param));
    }

    private ApplicationLaunchParam givenParam() {
        ApplicationLaunchParam param = new ApplicationLaunchParam();
        param.setModuleId("moduleId");
        param.setOspCUID("cuid");
        param.setResourceName("resourceName");
        param.setPatientName("patientName");
        param.setPatientSer(123456L);
        param.setOspToken("token");
        param.setTaskId("taskId");
        param.setTaskName("taskName");
        return param;
    }
}
