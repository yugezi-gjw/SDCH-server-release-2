package com.varian.oiscn.patient.registry;

import com.varian.oiscn.anticorruption.exception.FhirCreatePatientException;
import com.varian.oiscn.anticorruption.resourceimps.FlagAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.patient.service.PatientServiceImp;
import com.varian.oiscn.patient.view.PatientRegistrationVO;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 4/3/2018
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistryFactory.class})
public class PatientRegistryResourceTest {

    private PatientRegistryResource patientRegistryResource;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private PatientServiceImp patientServiceImp;

    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;

    private EncounterServiceImp encounterServiceImp;

    private Configuration configuration;

    private Environment environment;

    @Before
    public void setup() throws Exception{
        PowerMockito.mockStatic(RegistryFactory.class);
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        patientRegistryResource = new PatientRegistryResource(configuration, environment);
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(patientAntiCorruptionServiceImp);
        patientServiceImp = PowerMockito.mock(PatientServiceImp.class);
        PowerMockito.whenNew(PatientServiceImp.class).withAnyArguments().thenReturn(patientServiceImp);
        flagAntiCorruptionServiceImp = PowerMockito.mock(FlagAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(FlagAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(flagAntiCorruptionServiceImp);
        encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
    }

    @Test
    public void testRegisterWithInvalidParam() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());

        vo.setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());

        vo.setPatient(new Patient());
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());

        vo.setEncounter(new Encounter());
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());

        vo.getPatient().setChineseName("name");
        vo.getPatient().setHisId("hisId");
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());
    }

    @Test
    public void testNullRegistry() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        vo.setPatient(givenPatient());
        vo.setEncounter(givenEncounter());
        vo.setScenarioFlag("invalidFlag");
        PowerMockito.when(RegistryFactory.getRegistry(vo)).thenReturn(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());
    }

    @Test
    public void testRegistryDuplicateHISId() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        vo.setPatient(givenPatient());
        vo.setEncounter(givenEncounter());
        vo.setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        IPatientRegistry registry = PowerMockito.mock(IPatientRegistry.class);
        PowerMockito.when(RegistryFactory.getRegistry(vo)).thenReturn(registry);
        PowerMockito.when(registry.verifyRegistry()).thenReturn(RegistryVerifyStatusEnum.DUPLICATE_HIS);
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());
    }

    @Test
    public void testRegistryDuplicateVID() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        vo.setPatient(givenPatient());
        vo.setEncounter(givenEncounter());
        vo.setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        IPatientRegistry registry = PowerMockito.mock(IPatientRegistry.class);
        PowerMockito.when(RegistryFactory.getRegistry(vo)).thenReturn(registry);
        PowerMockito.when(registry.verifyRegistry()).thenReturn(RegistryVerifyStatusEnum.DUPLICATE_VID);
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());
    }

    @Test
    public void testRegistryInvalidDiagnosisDate() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        vo.setPatient(givenPatient());
        vo.setEncounter(givenEncounter());
        vo.setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        IPatientRegistry registry = PowerMockito.mock(IPatientRegistry.class);
        PowerMockito.when(RegistryFactory.getRegistry(vo)).thenReturn(registry);
        PowerMockito.when(registry.verifyRegistry()).thenReturn(RegistryVerifyStatusEnum.INVALID_DIAGNOSIS_DATE);
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());
    }

    @Test
    public void testRegistryDuplicateSSN() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        vo.setPatient(givenPatient());
        vo.setEncounter(givenEncounter());
        vo.setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        UserContext userContext = givenUserContext();
        IPatientRegistry registry = PowerMockito.mock(IPatientRegistry.class);
        PowerMockito.when(RegistryFactory.getRegistry(vo)).thenReturn(registry);
        PowerMockito.when(registry.verifyRegistry()).thenReturn(RegistryVerifyStatusEnum.PASS);
        PowerMockito.when(registry.saveOrUpdate(configuration, userContext)).thenThrow(new FhirCreatePatientException(new Exception()));
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.registry(userContext, vo).getStatusInfo());
    }

    @Test
    public void testRegistryFail() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        vo.setPatient(givenPatient());
        vo.setEncounter(givenEncounter());
        vo.setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        UserContext userContext = givenUserContext();
        IPatientRegistry registry = PowerMockito.mock(IPatientRegistry.class);
        PowerMockito.when(RegistryFactory.getRegistry(vo)).thenReturn(registry);
        PowerMockito.when(registry.verifyRegistry()).thenReturn(RegistryVerifyStatusEnum.PASS);
        PowerMockito.when(registry.saveOrUpdate(configuration, userContext)).thenReturn(null);
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR, patientRegistryResource.registry(userContext, vo).getStatusInfo());
    }

    @Test
    public void testRegistrySuccess() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        vo.setPatient(givenPatient());
        vo.setEncounter(givenEncounter());
        vo.setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        IPatientRegistry registry = PowerMockito.mock(IPatientRegistry.class);
        PowerMockito.when(RegistryFactory.getRegistry(vo)).thenReturn(registry);
        PowerMockito.when(registry.verifyRegistry()).thenReturn(RegistryVerifyStatusEnum.PASS);
        PowerMockito.when(registry.saveOrUpdate(configuration, givenUserContext())).thenReturn(1L);
        Assert.assertEquals(Response.Status.OK, patientRegistryResource.registry(new UserContext(), vo).getStatusInfo());
    }

    @Test
    public void testSearchWithNullHisId() {
        Assert.assertEquals(Response.Status.BAD_REQUEST, patientRegistryResource.search(new UserContext(), "").getStatusInfo());
    }

    @Test
    public void testSearch() {
        String hisId = "hisId";
        AbstractRegistryScenario scenario = PowerMockito.mock(AbstractRegistryScenario.class);
        UserContext userContext = givenUserContext();
        PowerMockito.when(RegistryFactory.getScenario(hisId, configuration, userContext)).thenReturn(scenario);
        PowerMockito.when(scenario.getPatientRegistrationVO()).thenReturn(new PatientRegistrationVO());
        Assert.assertEquals(Response.Status.OK, patientRegistryResource.search(userContext, hisId).getStatusInfo());
    }

    private Patient givenPatient() {
        Patient patient = new Patient();
        patient.setChineseName("chinese");
        patient.setHisId("hisId");
        return patient;
    }

    private Encounter givenEncounter() {
        Encounter encounter = new Encounter();
        encounter.setPrimaryPhysicianID("1");
        encounter.setPrimaryPhysicianGroupID("1");
        return encounter;
    }

    public static UserContext givenUserContext() {
        return new UserContext();
    }

}
