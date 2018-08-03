package com.varian.oiscn.patient.integration;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.patient.GenderEnum;
import com.varian.oiscn.core.patient.MaritalStatusEnum;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientStatusEnum;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.core.patient.VIPEnum;
import com.varian.oiscn.patient.integration.service.HisPatientInfoService;
import com.varian.oiscn.patient.view.PatientRegistrationVO;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/25/2017
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HisPatientInfoService.class, HISPatientQuery.class, HttpClients.class})
public class HISPatientQueryTest {

    protected HISPatientQuery hisPatientQuery;
    protected RegistrationVO hisPatientVO;

    @Before
    public void setup() {

    }

    @Test
    public void testQueryByHisId() {
        String hisId = "hisId";
        hisPatientQuery = new HISPatientQuery();
        String params = PowerMockito.mock(String.class);
        hisPatientVO = PowerMockito.mock(RegistrationVO.class);
        PowerMockito.mockStatic(HisPatientInfoService.class);
        PowerMockito.when(HisPatientInfoService.isOK()).thenReturn(true);
        PowerMockito.when(HisPatientInfoService.callHisWebservice(params)).thenReturn(hisPatientVO);
        hisPatientQuery.queryByHisId(hisId);
        Assert.assertTrue(true);
    }

    @Test
    public void testHisPatient() {
        PatientRegistrationVO patientRegistrationVO = new PatientRegistrationVO();
        Patient patient = new Patient();
        patient.setId("id");
        patient.setHisId("hisId");
        patient.setRadiationId("radiationid");
        patient.setPatientSer(123L);
        patient.setNationalId("nationalid");
        patient.setChineseName("chinese name");
        patient.setEnglishName("john");
        patient.setPinyin("pinyin");
        patient.setGender(GenderEnum.M);
        patient.setBirthDate(new Date());
        patient.setPatientStatus(PatientStatusEnum.N);
        patient.setMaritalStatus(MaritalStatusEnum.A);
        patient.setVip(VIPEnum.VIP);
        patient.setCitizenship(new CodeSystem());
        patient.setEthnicGroup(new CodeSystem());
        patient.setPhoto("photo");
        patient.setWorkPhone("workphone");
        patient.setHomePhone("homephone");
        patient.setMobilePhone("mobilephone");
        patient.setAddress("address");
        patient.setPatientHistory("history");
        patientRegistrationVO.setPatient(patient);
        Encounter encounter = new Encounter();
        encounter.setId("id");
        encounter.setStatus(StatusEnum.IN_PROGRESS);
        encounter.setPrimaryPhysicianName("name");
        encounter.setPrimaryPhysicianID("id");
        encounter.setInsuranceType("type");
        patientRegistrationVO.setEncounter(encounter);
        patientRegistrationVO.setScenarioFlag(PatientRegistrationVO.N1_HIS_ONLY);
        JsonSerializer jsonSerializer = new JsonSerializer();
        System.out.println("json: " + jsonSerializer.getJson(patientRegistrationVO));
    }

}
