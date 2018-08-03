package com.varian.oiscn.patient.registry;

import com.varian.oiscn.anticorruption.resourceimps.FlagAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.base.extend.ImplementationExtensionService;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfiguration;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.patient.assembler.PatientRegistryAssembler;
import com.varian.oiscn.patient.integration.HISPatientQuery;
import com.varian.oiscn.patient.integration.IPatientQuery;
import com.varian.oiscn.patient.integration.demo.MockHISPatientQuery;
import com.varian.oiscn.patient.registry.scenario.HISAriaQinActiveScenario;
import com.varian.oiscn.patient.registry.scenario.HISAriaQinInactiveScenario;
import com.varian.oiscn.patient.registry.scenario.HISAriaScenario;
import com.varian.oiscn.patient.registry.scenario.HISOnlyScenario;
import com.varian.oiscn.patient.registry.scenario.NoAllScenario;
import com.varian.oiscn.patient.registry.scenario.NoHISAriaOnlyScenario;
import com.varian.oiscn.patient.registry.scenario.NoHISAriaQinActiveScenario;
import com.varian.oiscn.patient.registry.scenario.NoHISAriaQinInactiveScenario;
import com.varian.oiscn.patient.registry.scenario.NoHISQinOnlyScenario;
import com.varian.oiscn.patient.service.PatientServiceImp;
import com.varian.oiscn.patient.util.MockDtoUtil;
import com.varian.oiscn.patient.view.PatientRegistrationVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistryFactory.class, ImplementationExtensionService.class, PatientRegistryAssembler.class,
        HisPatientInfoConfigService.class})
public class RegistryFactoryTest {
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;
    private PatientServiceImp patientServiceImp;
    private IPatientQuery iPatientQuery;
    private Configuration configuration;
    private EncounterServiceImp encounterServiceImp;
    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        patientServiceImp = PowerMockito.mock(PatientServiceImp.class);
        PowerMockito.whenNew(PatientServiceImp.class).withAnyArguments().thenReturn(patientServiceImp);
        iPatientQuery = PowerMockito.mock(MockHISPatientQuery.class);
        configuration = PowerMockito.mock(Configuration.class);
        encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        flagAntiCorruptionServiceImp = PowerMockito.mock(FlagAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(FlagAntiCorruptionServiceImp.class).withNoArguments().thenReturn(flagAntiCorruptionServiceImp);
        PowerMockito.mockStatic(RegistryFactory.class);
        PowerMockito.mockStatic(PatientRegistryAssembler.class);
        PowerMockito.mockStatic(HisPatientInfoConfigService.class);
        PowerMockito.mockStatic(ImplementationExtensionService.class);
        PowerMockito.spy(RegistryFactory.class);
    }

    @Test
    public void testNoAllScenario() throws Exception {
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(anyString())).thenReturn(null);
        PowerMockito.when(patientServiceImp.queryPatientByHisId(anyString())).thenReturn(null);
        PowerMockito.doReturn(iPatientQuery).when(RegistryFactory.class, "newPatientExtendQuery");
        PowerMockito.when(iPatientQuery.queryByHisId(anyString())).thenReturn(null);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof NoAllScenario);
    }

    @Test
    public void testNoHISAriaOnlyScenario() throws Exception {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(patientDto);
        PowerMockito.when(patientServiceImp.queryPatientByHisId(anyString())).thenReturn(null);
        PowerMockito.doReturn(iPatientQuery).when(RegistryFactory.class, "newPatientExtendQuery");
        PowerMockito.when(iPatientQuery.queryByHisId(anyString())).thenReturn(null);
        Encounter encounter = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(PatientRegistryAssembler.getEncounterFromARIA(anyObject(), anyObject())).thenReturn(encounter);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof NoHISAriaOnlyScenario);
    }

    @Test
    public void testHISAriaScenario() throws Exception {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(patientDto);
        PowerMockito.when(patientServiceImp.queryPatientByHisId(anyString())).thenReturn(null);
        PowerMockito.doReturn(iPatientQuery).when(RegistryFactory.class, "newPatientExtendQuery");
        RegistrationVO hisPatient = MockDtoUtil.givenARegistrationVO();
        PowerMockito.when(iPatientQuery.queryByHisId(anyString())).thenReturn(hisPatient);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof HISAriaScenario);
    }

    @Test
    public void testHISOnlyScenario() throws Exception {
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(null);
        PowerMockito.when(patientServiceImp.queryPatientByHisId(anyString())).thenReturn(null);
        PowerMockito.doReturn(iPatientQuery).when(RegistryFactory.class, "newPatientExtendQuery");
        RegistrationVO hisPatient = MockDtoUtil.givenARegistrationVO();
        PowerMockito.when(iPatientQuery.queryByHisId(anyString())).thenReturn(hisPatient);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof HISOnlyScenario);
    }

    @Test
    public void testNoHISQinOnlyScenario() throws Exception {
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(null);
        Patient patient = new Patient();
        Encounter encounter = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(patientServiceImp.queryPatientByHisId(anyString())).thenReturn(patient);
        PowerMockito.doReturn(iPatientQuery).when(RegistryFactory.class, "newPatientExtendQuery");
        PowerMockito.when(iPatientQuery.queryByHisId(anyString())).thenReturn(null);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(anyLong())).thenReturn(encounter);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof NoHISQinOnlyScenario);
    }

    @Test
    public void testNoHISAriaQinActiveScenario() throws Exception {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(patientDto);
        Patient patient = new Patient();
        PowerMockito.when(patientServiceImp.queryPatientByPatientSer(anyString())).thenReturn(patient);
        PowerMockito.doReturn(iPatientQuery).when(RegistryFactory.class, "newPatientExtendQuery");
        PowerMockito.when(iPatientQuery.queryByHisId(anyString())).thenReturn(null);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(anyString(), anyString())).thenReturn(true);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof NoHISAriaQinActiveScenario);
    }

    @Test
    public void testNoHISAriaQinInactiveScenario() throws Exception {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(patientDto);
        Patient patient = new Patient();
        PowerMockito.when(patientServiceImp.queryPatientByPatientSer(anyString())).thenReturn(patient);
        PowerMockito.doReturn(iPatientQuery).when(RegistryFactory.class, "newPatientExtendQuery");
        PowerMockito.when(iPatientQuery.queryByHisId(anyString())).thenReturn(null);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(anyString(), anyString())).thenReturn(false);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof NoHISAriaQinInactiveScenario);
    }

    @Test
    public void testHISAriaQinActiveScenario() throws Exception {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(patientDto);
        Patient patient = new Patient();
        PowerMockito.when(patientServiceImp.queryPatientByPatientSer(anyString())).thenReturn(patient);
        RegistrationVO registrationVO = MockDtoUtil.givenARegistrationVO();
        HisPatientInfoConfiguration hisPatientInfoConfiguration = PowerMockito.mock(HisPatientInfoConfiguration.class);
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(hisPatientInfoConfiguration);
        PowerMockito.when(hisPatientInfoConfiguration.isHisPatientQueryEnable()).thenReturn(true);

        PowerMockito.when(ImplementationExtensionService.getImplementationClassOf(IPatientQuery.class.getName())).thenReturn(null);
        HISPatientQuery hisPatientQuery = PowerMockito.mock(HISPatientQuery.class);
        PowerMockito.whenNew(HISPatientQuery.class).withNoArguments().thenReturn(hisPatientQuery);
        PowerMockito.when(hisPatientQuery.queryByHisId(anyString())).thenReturn(registrationVO);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(anyString(), anyString())).thenReturn(true);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof HISAriaQinActiveScenario);
    }

    @Test
    public void testHISAriaQinInactiveScenario() throws Exception {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(patientDto);
        Patient patient = new Patient();
        PowerMockito.when(patientServiceImp.queryPatientByPatientSer(anyString())).thenReturn(patient);
        RegistrationVO registrationVO = MockDtoUtil.givenARegistrationVO();
        PowerMockito.doReturn(iPatientQuery).when(RegistryFactory.class, "newPatientExtendQuery");
        PowerMockito.when(iPatientQuery.queryByHisId(anyString())).thenReturn(registrationVO);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(anyString(), anyString())).thenReturn(false);

        AbstractRegistryScenario scenario = RegistryFactory.getScenario("hisId", configuration, MockDtoUtil.givenUserContext());
        Assert.assertTrue(scenario instanceof HISAriaQinInactiveScenario);
    }

    @Test
    public void testGetScenarios() {
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        }}) instanceof HISOnlyScenario);
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N2_HIS_ARIA);
        }}) instanceof HISAriaScenario);
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N3_HIS_ARIA_QIN_ACTIVE);
        }}) instanceof HISAriaQinActiveScenario);
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N4_HIS_ARIA_QIN_INACTIVE);
        }}) instanceof HISAriaQinInactiveScenario);
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N5_NOHIS_ARIA_QIN_ACTIVE);
        }}) instanceof NoHISAriaQinActiveScenario);
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N6_NOHIS_ARIA_QIN_INACTIVE);
        }}) instanceof NoHISAriaQinInactiveScenario);
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N7_NOHIS_ARIA_ONLY);
        }}) instanceof NoHISAriaOnlyScenario);
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N8_NOHIS_QIN_ONLY);
        }}) instanceof NoHISQinOnlyScenario);
        Assert.assertTrue(RegistryFactory.getRegistry(new PatientRegistrationVO() {{
            setScenarioFlag(PatientRegistrationVO.N9_NOALL);
        }}) instanceof NoAllScenario);
    }
}
