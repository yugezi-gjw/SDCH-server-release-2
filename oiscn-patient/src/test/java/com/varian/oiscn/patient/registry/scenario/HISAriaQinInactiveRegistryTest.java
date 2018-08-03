package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.patient.assembler.CoverageAssembler;
import com.varian.oiscn.patient.assembler.PatientRegistryAssembler;
import com.varian.oiscn.patient.registry.RegistryService;
import com.varian.oiscn.patient.util.MockDtoUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HISAriaQinInactiveScenario.class, PatientRegistryAssembler.class,
        StatusIconPool.class, CoverageAssembler.class, SystemConfigPool.class,
        PatientCache.class, RegistryService.class, PatientEncounterHelper.class})
public class HISAriaQinInactiveRegistryTest {
    protected Configuration configuration;

    private PatientAntiCorruptionServiceImp antiCorruptionServiceImp;
    private DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp;
    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;
    private CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp;
    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    private HISAriaQinInactiveScenario registry;
    private RegistryService registryService;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        antiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(antiCorruptionServiceImp);
        diagnosisAntiCorruptionServiceImp = PowerMockito.mock(DiagnosisAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DiagnosisAntiCorruptionServiceImp.class).withNoArguments().thenReturn(diagnosisAntiCorruptionServiceImp);
        flagAntiCorruptionServiceImp = PowerMockito.mock(FlagAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(FlagAntiCorruptionServiceImp.class).withNoArguments().thenReturn(flagAntiCorruptionServiceImp);
        coverageAntiCorruptionServiceImp = PowerMockito.mock(CoverageAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CoverageAntiCorruptionServiceImp.class).withNoArguments().thenReturn(coverageAntiCorruptionServiceImp);
        carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        PowerMockito.mockStatic(PatientRegistryAssembler.class);
        PowerMockito.mockStatic(StatusIconPool.class);
        PowerMockito.mockStatic(CoverageAssembler.class);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.mockStatic(PatientCache.class);
        PowerMockito.mockStatic(PatientEncounterHelper.class);

        PowerMockito.when(SystemConfigPool.queryDefaultDepartment()).thenReturn("1");
        PowerMockito.when(configuration.getUrgentStatusIconDesc()).thenReturn("urgent");
        PowerMockito.when(StatusIconPool.get(anyString())).thenReturn("urgentCode");
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn("template");

        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(anyString(), anyString())).thenReturn(true);
        PowerMockito.when(flagAntiCorruptionServiceImp.markPatientStatusIcon(anyString(), anyString())).thenReturn(true);
        PowerMockito.when(flagAntiCorruptionServiceImp.unmarkPatientStatusIcon(anyString(), anyString())).thenReturn(true);

        PowerMockito.when(coverageAntiCorruptionServiceImp.updateCoverage(anyObject())).thenReturn("1");

        registryService = PowerMockito.mock(RegistryService.class);
        PowerMockito.whenNew(RegistryService.class).withAnyArguments().thenReturn(registryService);
    }

    @Test
    public void testPatientSerIsNull() {
        registry = new HISAriaQinInactiveScenario(new Patient(), new Encounter());
        Assert.assertNull(registry.saveOrUpdate(configuration, new UserContext()));
    }

    @Test
    public void testSaveOrUpdate() {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        Patient patient = giveAPatient();
        Encounter encounter = new Encounter();
        registry = new HISAriaQinInactiveScenario(patient, encounter);

        PowerMockito.when(PatientRegistryAssembler.getPatientDto(patient, encounter, configuration)).thenReturn(patientDto);
        PowerMockito.when(antiCorruptionServiceImp.update(patient.getPatientSer(), patientDto)).thenReturn("1");

        PowerMockito.when(CoverageAssembler.getCoverageDto(patient.getPatientSer().toString(), encounter.getInsuranceTypeCode())).thenReturn(new CoverageDto());
        PowerMockito.when(carePathAntiCorruptionServiceImp.linkCarePath(anyString(), anyString(), anyString())).thenReturn("1");
        PowerMockito.when(registryService.updateWithNewEncounter(patient, encounter, patient.getPatientSer())).thenReturn(true);

        Assert.assertEquals(patient.getPatientSer(), registry.saveOrUpdate(configuration, new UserContext()));
    }

    private Patient giveAPatient() {
        Patient patient = new Patient();
        patient.setPatientSer(1L);
        return patient;
    }
}
