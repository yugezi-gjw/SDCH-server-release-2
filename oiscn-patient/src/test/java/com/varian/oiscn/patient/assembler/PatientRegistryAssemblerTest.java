package com.varian.oiscn.patient.assembler;

import java.util.Arrays;

import com.varian.oiscn.anticorruption.resourceimps.CoverageAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.DiagnosisAntiCorruptionServiceImp;
import com.varian.oiscn.base.codesystem.PatientLabelPool;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Contact;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.patient.util.MockDtoUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientRegistryAssembler.class, PatientLabelPool.class, Configuration.class})
public class PatientRegistryAssemblerTest {
    private Configuration configuration;

    @Before
    public void setup() {
        configuration = PowerMockito.mock(Configuration.class);
        PowerMockito.mockStatic(PatientLabelPool.class);
        PowerMockito.when(PatientLabelPool.get(anyString())).thenReturn("label");
        PowerMockito.when(configuration.getAlertPatientLabelDesc()).thenReturn("desc");
    }

    @Test
    public void testPatientRegistryAssembler() {
        String alertCode = "alertCode";
        PowerMockito.when(PatientLabelPool.get(Matchers.anyString())).thenReturn(alertCode);
        Encounter encounter = givenAEncounter();
        encounter.setAlert(alertCode);
        PatientDto patientDto = PatientRegistryAssembler.getPatientDto(givenAPatient(), encounter, configuration);
        Assert.assertNotNull(patientDto);
    }

    @Test
    public void testGetPatientFromARIA(){
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        Patient patient = PatientRegistryAssembler.getPatientFromARIA(patientDto);
        Assert.assertEquals("hisId", patient.getHisId());
    }

    @Test
    public void testGetEncounterFromARIA() throws Exception{
        Encounter encounter = givenAEncounter();
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = PowerMockito.mock(CoverageAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CoverageAntiCorruptionServiceImp.class).withNoArguments().thenReturn(coverageAntiCorruptionServiceImp);
        CoverageDto coverageDto = PowerMockito.mock(CoverageDto.class);
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryByPatientId(patientDto.getPatientSer())).thenReturn(coverageDto);
        DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp = PowerMockito.mock(DiagnosisAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DiagnosisAntiCorruptionServiceImp.class).withNoArguments().thenReturn(diagnosisAntiCorruptionServiceImp);
        Encounter returnValue = PatientRegistryAssembler.getEncounterFromARIA(encounter, patientDto);
        Assert.assertEquals("physicianId", returnValue.getPrimaryPhysicianID());
    }

    private Patient givenAPatient() {
        Patient patient = new Patient();
        patient.setPatientSer(1L);
        patient.setHisId("hisId");
        patient.setChineseName("name");
        patient.setContacts(Arrays.asList(new Contact(){
            {setMobilePhone("12342353");}
        }));
        patient.setRadiationId("vid");
        return patient;
    }

    private Encounter givenAEncounter() {
        return MockDtoUtil.givenAnEncounter();
    }
}
