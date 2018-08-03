package com.varian.oiscn.rt;

import com.varian.oiscn.anticorruption.resourceimps.DiagnosisAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.TreatmentSummaryAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.ValueSetAntiCorruptionServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.FhirServerConfiguration;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.encounter.PatientEncounterEndPlan;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.rt.util.MockDtoUtil;
import com.varian.oiscn.rt.view.PlanCardVO;
import com.varian.oiscn.rt.view.TreatmentSummaryCardVO;
import io.dropwizard.setup.Environment;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by asharma0 on 12-07-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TreatmentSummaryResource.class, PatientEncounterHelper.class})
public class TreatmentSummaryResourceTest {
    private Configuration configuration;
    private Environment environment;
    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;
    private TreatmentSummaryResource treatmentSummaryResource;
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;
    private DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withNoArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        diagnosisAntiCorruptionServiceImp = PowerMockito.mock(DiagnosisAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DiagnosisAntiCorruptionServiceImp.class).withNoArguments().thenReturn(diagnosisAntiCorruptionServiceImp);
        treatmentSummaryResource = new TreatmentSummaryResource(configuration, environment);
        PowerMockito.mockStatic(PatientEncounterHelper.class);
    }

    @Test
    public void shouldReturnTreatmentSummary() {
        final Long PATIENT_ID = 1212L;
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(String.valueOf(PATIENT_ID))).thenReturn(Optional.of(treatmentSummaryDto));
        Response response = treatmentSummaryResource.get(null, PATIENT_ID,null);
        TreatmentSummaryDto received = (TreatmentSummaryDto) response.getEntity();
        assertThat(treatmentSummaryDto, Matchers.equalTo(received));
    }

    @Test
    public void shouldReturnEmptyResponseForEmptyTreatmentSummary() {
        final Long PATIENT_ID = 1212L;
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(String.valueOf(PATIENT_ID))).thenReturn(Optional.empty());
        Response response = treatmentSummaryResource.get(null, PATIENT_ID,null);
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(response.hasEntity(), is(false));
    }

    @Test
    public void shouldReturnErrorIfPatientIdIsMissing() {
        Response response = treatmentSummaryResource.get(null, null,null);
        int status = response.getStatus();
        assertThat(status, Matchers.equalTo(Response.Status.BAD_REQUEST.getStatusCode()));
        assertThat(response.hasEntity(), is(false));
    }

    @Test
    public void shouldReturnNullIfAPropertyIsMissingForEmptyTreatmentSummaryCard() throws Exception {
        final Long PATIENT_ID = 1212L;
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        treatmentSummaryDto.setLastTreatmentDate(DateTime.parse("2017-08-30").toDate());
        List<PlanSummaryDto> plans = new ArrayList<>();
        treatmentSummaryDto.setPlans(plans);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer(String.valueOf(PATIENT_ID));
        PowerMockito.when(diagnosisAntiCorruptionServiceImp.queryDiagnosisListByPatientID(String.valueOf(PATIENT_ID))).thenReturn(new ArrayList<>());
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(String.valueOf(PATIENT_ID))).thenReturn(Optional.of(treatmentSummaryDto));
        Response response = treatmentSummaryResource.getSummaryCard(MockDtoUtil.givenUserContext(), PATIENT_ID,null);
        TreatmentSummaryCardVO treatmentSummaryCardVO = (TreatmentSummaryCardVO) response.getEntity();
        assertNull(treatmentSummaryCardVO.getPlanList());
        assertEquals(treatmentSummaryCardVO.getLastTreatmentDate(), "2017-08-30");
    }

    @Test
    public void shouldReturnNullIfAPropertyIsMissingForEmptyTreatmentSummaryCardForEncounter() throws Exception {
        final Long PATIENT_ID = 121212L;
        final Long ENCOUNTER_ID = 1212L;
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        treatmentSummaryDto.setLastTreatmentDate(DateTime.parse("2017-08-30").toDate());
        List<PlanSummaryDto> plans = new ArrayList<>();
        treatmentSummaryDto.setPlans(plans);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer(String.valueOf(PATIENT_ID));
        PatientEncounterEndPlan patientEncounterEndPlan = new PatientEncounterEndPlan(){{
            setCompletedPlan(new ArrayList<>());
            setPatientSer(String.valueOf(PATIENT_ID));
        }};
        PowerMockito.when(PatientEncounterHelper.getEncounterEndPlanByPatientSer(String.valueOf(PATIENT_ID))).thenReturn(patientEncounterEndPlan);
        PowerMockito.when(diagnosisAntiCorruptionServiceImp.queryDiagnosisListByPatientID(String.valueOf(PATIENT_ID))).thenReturn(new ArrayList<>());
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(String.valueOf(PATIENT_ID),String.valueOf(ENCOUNTER_ID))).thenReturn(Optional.of(treatmentSummaryDto));
        Response response = treatmentSummaryResource.getSummaryCard(MockDtoUtil.givenUserContext(), PATIENT_ID,ENCOUNTER_ID);
        TreatmentSummaryCardVO treatmentSummaryCardVO = (TreatmentSummaryCardVO) response.getEntity();
        assertNull(treatmentSummaryCardVO.getPlanList());
        assertEquals(treatmentSummaryCardVO.getLastTreatmentDate(), "2017-08-30");
    }



    @Test
    public void testShouldReturnSummaryCardForEncounter() throws Exception {
        final Long PATIENT_ID = 121212L;
        final Long ENCOUNTER_ID = 1212L;
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        treatmentSummaryDto.setLastTreatmentDate(DateTime.parse("2017-08-30").toDate());
        List<PlanSummaryDto> plans = Arrays.asList(new PlanSummaryDto(){{
            setPlanSetupId("plan1");
            setCreatedDt(DateTime.parse("2017-08-30").toDate());
        }});
        treatmentSummaryDto.setPlans(plans);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer(String.valueOf(PATIENT_ID));
        PatientEncounterEndPlan patientEncounterEndPlan = new PatientEncounterEndPlan(){{
            setCompletedPlan(Arrays.asList(new EncounterEndPlan(){{
                setEncounterId(new Long(ENCOUNTER_ID));
                setPlanSetupId("plan1");
                setPlanCreatedDt(DateTime.parse("2017-08-30").toDate());
            }}));
            setPatientSer(String.valueOf(PATIENT_ID));
        }};
        PowerMockito.when(PatientEncounterHelper.getEncounterEndPlanByPatientSer(String.valueOf(PATIENT_ID))).thenReturn(patientEncounterEndPlan);
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(String.valueOf(PATIENT_ID),String.valueOf(ENCOUNTER_ID))).thenReturn(Optional.of(treatmentSummaryDto));
        Response response = treatmentSummaryResource.getSummaryCard(MockDtoUtil.givenUserContext(), PATIENT_ID,ENCOUNTER_ID);
        TreatmentSummaryCardVO treatmentSummaryCardVO = (TreatmentSummaryCardVO) response.getEntity();
        assertNotNull(treatmentSummaryCardVO.getPlanList());
        assertEquals(treatmentSummaryCardVO.getLastTreatmentDate(), "2017-08-30");
    }

    @Test
    public void shouldReturnErrorIfHisIdIsMissingForTreatmentSummaryCard() {
        Response response = treatmentSummaryResource.getSummaryCard(null, null,null);
        int status = response.getStatus();
        assertThat(status, Matchers.equalTo(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(response.hasEntity(), is(false));
    }

    @Test
    public void testGetSummaryCardWithSpecifiedLanguageBodyPart() throws Exception {
        String hisId = "5678";
        Long patientId = 12121L;
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();

        treatmentSummaryDto.setLastTreatmentDate(DateTime.parse("2017-08-30").toDate());
        PlanSummaryDto planSummaryDto = new PlanSummaryDto();
        planSummaryDto.setDeliveredDose(3.2);
        planSummaryDto.setDeliveredFractions(20);
        planSummaryDto.setDoseSummary(new ArrayList<>());
        planSummaryDto.setFields(new ArrayList<>());
        planSummaryDto.setPlannedDose(3.3);
        planSummaryDto.setPlannedFractions(30);
        planSummaryDto.setPlanSetupId("planId");
        planSummaryDto.setPlanSetupName("planSetupName");
        List<PlanSummaryDto> planSummaryDtoList = new ArrayList<>();
        planSummaryDtoList.add(planSummaryDto);
        treatmentSummaryDto.setPlans(planSummaryDtoList);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer(String.valueOf(patientId));

        PatientDto patientFromFhir = mock(PatientDto.class);
        String patientSer = mock(String.class);
        when(patientFromFhir.getPatientSer()).thenReturn(patientSer);
        when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(patientFromFhir);

        Diagnosis diagnosis = mock(Diagnosis.class);
        List<Diagnosis> diagnosisList = new ArrayList<>();
        diagnosisList.add(diagnosis);
        when(diagnosisAntiCorruptionServiceImp.queryDiagnosisListByPatientID(patientSer)).thenReturn(diagnosisList);

        String fhirBodypartCode = "1234";
        when(diagnosis.getBodypartCode()).thenReturn(fhirBodypartCode);

        FhirServerConfiguration fhirServerConfig = mock(FhirServerConfiguration.class);
        String lan = mock(String.class);
        when(configuration.getFhirServerConfiguration()).thenReturn(fhirServerConfig);
        when(fhirServerConfig.getFhirLanguage()).thenReturn(lan);

        ValueSetAntiCorruptionServiceImp vsService = mock(ValueSetAntiCorruptionServiceImp.class);
        CodeSystem codeSystem = mock(CodeSystem.class);

        whenNew(ValueSetAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(vsService);
        when(vsService.queryAllPrimarySites(lan)).thenReturn(codeSystem);

        List<CodeValue> codeValues = new ArrayList<>();
        CodeValue codeValue = mock(CodeValue.class);
        String code = "1234";
        String desc = mock(String.class);
        when(codeValue.getCode()).thenReturn(code);
        when(codeValue.getDesc()).thenReturn(desc);
        codeValues.add(codeValue);
        when(codeSystem.getCodeValues()).thenReturn(codeValues);

        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(String.valueOf(patientId))).thenReturn(Optional.of(treatmentSummaryDto));

        Response response = treatmentSummaryResource.getSummaryCard(MockDtoUtil.givenUserContext(), patientId,null);

        Object entity = response.getEntity();
        if (entity instanceof TreatmentSummaryCardVO) {
            TreatmentSummaryCardVO treatmentSummaryCardVO = (TreatmentSummaryCardVO) entity;
            assertNotNull(treatmentSummaryCardVO.getPlanList());
            assertEquals(treatmentSummaryCardVO.getLastTreatmentDate(), "2017-08-30");
            for (PlanCardVO plan: treatmentSummaryCardVO.getPlanList()) {
            	assertNotNull(plan.getPlanId());
            	assertNotNull(plan.getPlanName());
            	assertNotNull(plan.getPlannedFractions());
            	assertNotNull(plan.getDeliveredFractions());
            }
        } else {
            Assert.fail("Response Entity is not a TreatmentSummaryCardVO!!!");
        }
    }
}