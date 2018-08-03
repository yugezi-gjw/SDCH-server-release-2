package com.varian.oiscn.patient.registry;

import java.util.Date;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.patient.util.MockDtoUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistryUtil.class})
public class RegistryUtilTest {
    private PatientAntiCorruptionServiceImp antiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        antiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(antiCorruptionServiceImp);
    }

    @Test
    public void testVerifyHisIdNotNull() {
        Patient p = givenAPatient();
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(antiCorruptionServiceImp.queryPatientByHisId(anyString())).thenReturn(patientDto);
        RegistryVerifyStatusEnum result = RegistryUtil.verifyNewPatientRegistry(p, new Encounter());
        Assert.assertEquals(result, RegistryVerifyStatusEnum.DUPLICATE_HIS);
    }

    @Test
    public void testVerifyAriaIdNotNull() {
        Patient p = givenAPatient();
        p.setHisId(null);
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(antiCorruptionServiceImp.queryPatientByAriaId(anyString())).thenReturn(patientDto);
        RegistryVerifyStatusEnum result = RegistryUtil.verifyNewPatientRegistry(p, new Encounter());
        Assert.assertEquals(result, RegistryVerifyStatusEnum.DUPLICATE_VID);
    }

    @Test
    public void testVerifyInvalidDiagnosisDate() {
        Encounter encounter = MockDtoUtil.givenAnEncounter();
        encounter.getDiagnoses().get(0).setDiagnosisDate(DateUtil.addMillSecond(new Date(), 3 * 60 * 1000));
        RegistryVerifyStatusEnum result = RegistryUtil.verifyNewPatientRegistry(new Patient(), encounter);
        Assert.assertEquals(result, RegistryVerifyStatusEnum.INVALID_DIAGNOSIS_DATE);
    }

    @Test
    public void testVerifyPass() {
        Encounter encounter = MockDtoUtil.givenAnEncounter();
        RegistryVerifyStatusEnum result = RegistryUtil.verifyNewPatientRegistry(new Patient(), encounter);
        Assert.assertEquals(result, RegistryVerifyStatusEnum.PASS);
    }

    private Patient givenAPatient() {
        Patient p = new Patient();
        p.setHisId("hisId");
        p.setRadiationId("ariaId");
        return p;
    }

}
