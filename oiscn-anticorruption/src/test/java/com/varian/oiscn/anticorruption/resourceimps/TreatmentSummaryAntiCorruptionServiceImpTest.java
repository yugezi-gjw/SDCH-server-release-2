package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.TreatmentSummary;
import com.varian.oiscn.anticorruption.assembler.TreatmentSummaryAssembler;
import com.varian.oiscn.anticorruption.datahelper.MockTreatmentSummaryUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRTreatmentSummaryInterface;
import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterEndPlan;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;

/**
 * Created by asharma0 on 12-07-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TreatmentSummaryAntiCorruptionServiceImp.class, FHIRTreatmentSummaryInterface.class, PatientEncounterHelper.class})
public class TreatmentSummaryAntiCorruptionServiceImpTest {
    private FHIRTreatmentSummaryInterface fhirTreatmentSummaryInterface;
    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;

    @Before
    public void Setup() throws Exception {
        PowerMockito.mockStatic(PatientEncounterHelper.class);
        fhirTreatmentSummaryInterface = PowerMockito.mock(FHIRTreatmentSummaryInterface.class);
        PowerMockito.whenNew(FHIRTreatmentSummaryInterface.class).withNoArguments().thenReturn(fhirTreatmentSummaryInterface);
        treatmentSummaryAntiCorruptionServiceImp = new TreatmentSummaryAntiCorruptionServiceImp();
    }

    @Test
    public void shouldGetTreatmentSummary() throws Exception {
        final String PATIENT_ID = "patientId";
        Mockito.when(fhirTreatmentSummaryInterface.getTreatmentSummary(PATIENT_ID)).thenReturn(MockTreatmentSummaryUtil.givenATreatmentSummary());
        TreatmentSummaryDto tsDto = treatmentSummaryAntiCorruptionServiceImp.getTxSummaryByPatientId(PATIENT_ID).get();
        assertThat(tsDto.getDoseUnit(), is("Gy"));
        assertThat(new DateTime(tsDto.getLastTreatmentDate(), DateTimeZone.UTC).toString(), is("2017-05-25T23:32:45.000Z"));
        PlanSummaryDto p1 = tsDto.getPlans().get(0);
        PlanSummaryDto p2 = tsDto.getPlans().get(1);
        assertThat(p1.getDeliveredDose(), is(20.0));
        assertThat(p1.getDeliveredFractions(), is(1));
        assertThat(p1.getPlannedDose(), is(200.0));
        assertThat(p1.getPlannedFractions(), is(10));
        assertThat(p1.getPlanSetupId(), is("Plan 1"));
        assertThat(p1.getPlanSetupName(), is("First Plan"));
        assertThat(p1.getDoseSummary().size(), is(1));
        assertThat(p1.getFields().size(), is(2));

        assertThat(p2.getDeliveredDose(), is(24.0));
        assertThat(p2.getDeliveredFractions(), is(1));
        assertThat(p2.getPlannedDose(), is(240.0));
        assertThat(p2.getPlannedFractions(), is(10));
        assertThat(p2.getPlanSetupId(), is("Plan 2"));
        assertThat(p2.getPlanSetupName(), is("Second Plan"));
        assertThat(p2.getDoseSummary().size(), is(1));
        assertThat(p1.getFields().size(), is(2));
    }

    @Test
    public void shouldGetNothingIfTxDataNotExist() throws Exception {
        final String PATIENT_ID = "patientId";
        PowerMockito.when(fhirTreatmentSummaryInterface.getTreatmentSummary(PATIENT_ID)).thenReturn(null);
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = treatmentSummaryAntiCorruptionServiceImp.getTxSummaryByPatientId(PATIENT_ID);
        Assert.assertTrue(treatmentSummaryDtoOptional.equals(Optional.empty()));
    }

    @Test
    public void testGetApproveTxSummaryByPatientId() throws Exception {
        TreatmentSummary treatmentSummary = MockTreatmentSummaryUtil.givenATreatmentSummary();
        TreatmentSummaryDto treatmentSummaryDto = TreatmentSummaryAssembler.getTreatmentSummaryDto(treatmentSummary);
        Optional<TreatmentSummaryDto> optional = Optional.of(treatmentSummaryDto);
        treatmentSummaryAntiCorruptionServiceImp = PowerMockito.spy(new TreatmentSummaryAntiCorruptionServiceImp());
        PowerMockito.doReturn(optional).when(treatmentSummaryAntiCorruptionServiceImp, "getTxSummaryByPatientId", "patientId");
        Assert.assertTrue(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientId("patientId").isPresent());
    }

    @Test
    public void testGetApproveTxSummaryByPatientIdAndEncounterId() throws Exception {
        TreatmentSummary treatmentSummary = MockTreatmentSummaryUtil.givenATreatmentSummary();
        TreatmentSummaryDto treatmentSummaryDto = TreatmentSummaryAssembler.getTreatmentSummaryDto(treatmentSummary);
        Optional<TreatmentSummaryDto> optional = Optional.of(treatmentSummaryDto);
        treatmentSummaryAntiCorruptionServiceImp = PowerMockito.spy(new TreatmentSummaryAntiCorruptionServiceImp());
        PowerMockito.doReturn(optional).when(treatmentSummaryAntiCorruptionServiceImp, "getApproveTxSummaryByPatientId", "1");
        PatientEncounterEndPlan patientEncounterEndPlan = new PatientEncounterEndPlan() {{
            setCompletedPlan(Arrays.asList(new EncounterEndPlan() {{
                setEncounterId(1L);
                setPlanSetupId("plan1");
                setPlanCreatedDt(DateTime.parse("2017-08-30").toDate());
            }}));
            setPatientSer("1");
        }};
        PowerMockito.when(PatientEncounterHelper.getEncounterEndPlanByPatientSer(anyString())).thenReturn(patientEncounterEndPlan);
        PatientEncounterCarePath patientEncounterCarePath = PowerMockito.mock(PatientEncounterCarePath.class);
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(anyString())).thenReturn(patientEncounterCarePath);
        EncounterCarePathList encounterCarePathList = PowerMockito.mock(EncounterCarePathList.class);
        PowerMockito.when(patientEncounterCarePath.getPlannedCarePath()).thenReturn(encounterCarePathList);
        PowerMockito.when(encounterCarePathList.getEncounterId()).thenReturn(21L);

        Assert.assertTrue(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId("1", "1").isPresent());
    }

    @Test
    public void testGetActivityEncounterTxSummaryByPatientSer() throws Exception {
        TreatmentSummary treatmentSummary = MockTreatmentSummaryUtil.givenATreatmentSummary();
        TreatmentSummaryDto treatmentSummaryDto = TreatmentSummaryAssembler.getTreatmentSummaryDto(treatmentSummary);
        Optional<TreatmentSummaryDto> optional = Optional.of(treatmentSummaryDto);
        treatmentSummaryAntiCorruptionServiceImp = PowerMockito.spy(new TreatmentSummaryAntiCorruptionServiceImp());
        PowerMockito.doReturn(optional).when(treatmentSummaryAntiCorruptionServiceImp, "getApproveTxSummaryByPatientId", "1");
        PatientEncounterEndPlan patientEncounterEndPlan = new PatientEncounterEndPlan() {{
            setCompletedPlan(Arrays.asList(new EncounterEndPlan() {{
                setEncounterId(1L);
                setPlanSetupId("plan1");
                setPlanCreatedDt(DateTime.parse("2017-08-31").toDate());
            }}));
            setPatientSer("1");
        }};
        PowerMockito.when(PatientEncounterHelper.getEncounterEndPlanByPatientSer(anyString())).thenReturn(patientEncounterEndPlan);
        Assert.assertTrue(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer("1").isPresent());
    }
}