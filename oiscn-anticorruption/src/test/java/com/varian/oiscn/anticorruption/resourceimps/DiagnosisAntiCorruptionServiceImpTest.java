package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Condition;
import com.varian.oiscn.anticorruption.assembler.ConditionAssembler;
import com.varian.oiscn.anticorruption.converter.EnumConditionQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.datahelper.MockDiagnosisUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRConditionInterface;
import com.varian.oiscn.core.patient.Diagnosis;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.codesystems.ConditionCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;

/**
 * Created by fmk9441 on 2017-05-16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DiagnosisAntiCorruptionServiceImp.class, ConditionAssembler.class})
public class DiagnosisAntiCorruptionServiceImpTest {
    private FHIRConditionInterface fhirConditionInterface;
    private DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirConditionInterface = PowerMockito.mock(FHIRConditionInterface.class);
        PowerMockito.whenNew(FHIRConditionInterface.class).withNoArguments().thenReturn(fhirConditionInterface);
        diagnosisAntiCorruptionServiceImp = new DiagnosisAntiCorruptionServiceImp();
    }

    @Test
    public void givenADiagnosisWhenCreateThenReturnDiagnosisID() {
        final String diagnosisID = "DiagnosisID";
        Diagnosis diagnosis = PowerMockito.mock(Diagnosis.class);
        Condition condition = PowerMockito.mock(Condition.class);
        PowerMockito.mockStatic(ConditionAssembler.class);
        PowerMockito.when(ConditionAssembler.getCondition(diagnosis)).thenReturn(condition);
        PowerMockito.when(fhirConditionInterface.create(condition)).thenReturn(diagnosisID);
        String createdDiagnosisID = diagnosisAntiCorruptionServiceImp.createDiagnosis(diagnosis);
        Assert.assertEquals(createdDiagnosisID, diagnosisID);
    }

    @Test
    public void givenADiagnosisWhenUpdateThenReturnDiagnosisID() {
        final String diagnosisID = "DiagnosisID";
        Diagnosis diagnosis = MockDiagnosisUtil.givenADiagnosis();
        Map<EnumConditionQuery, ImmutablePair<EnumMatchQuery, Object>> conditionQueryImmutablePairMap = new LinkedHashMap<>();
        conditionQueryImmutablePairMap.put(EnumConditionQuery.CATEGORY, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ConditionCategory.ENCOUNTERDIAGNOSIS.toCode()));
        conditionQueryImmutablePairMap.put(EnumConditionQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, diagnosis.getPatientID()));
        List<Condition> lstCondition = MockDiagnosisUtil.givenAConditionList();
        PowerMockito.when(fhirConditionInterface.queryConditionList(conditionQueryImmutablePairMap)).thenReturn(lstCondition);
        PowerMockito.when(fhirConditionInterface.update(lstCondition.get(0))).thenReturn(diagnosisID);
        String updatedDiagnosisID = diagnosisAntiCorruptionServiceImp.updateDiagnosis(diagnosis);
        Assert.assertEquals(updatedDiagnosisID, diagnosisID);
    }

    @Test
    public void givenADxCodeDxSchemeStagingSchemeTNMCodeWhenCalculateThenReturnStageSummary() {
        final String dxCode = "DxCode";
        final String dxScheme = "DxScheme";
        final String stageScheme = "StagingScheme";
        final String stageTCode = "T";
        final String stageNCode = "N";
        final String stageMCode = "M";
        PowerMockito.when(fhirConditionInterface.calculateStageSummary(dxCode, dxScheme, stageScheme, stageTCode, stageNCode, stageMCode)).thenReturn("StageSummary");
        String stageSummary = diagnosisAntiCorruptionServiceImp.calculateStageSummary(dxCode, dxScheme, stageScheme, stageTCode, stageNCode, stageMCode);
        Assert.assertEquals(stageSummary, "StageSummary");
    }

    @Test
    public void givenAPatientIDWhenQueryThenReturnDiagnosisList() {
        final String patientID = "PatientID";
        LinkedHashMap<EnumConditionQuery, ImmutablePair<EnumMatchQuery, Object>> conditionQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        conditionQueryImmutablePairLinkedHashMap.put(EnumConditionQuery.CATEGORY, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ConditionCategory.ENCOUNTERDIAGNOSIS.toCode()));
        conditionQueryImmutablePairLinkedHashMap.put(EnumConditionQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientID));
        List<Condition> lstCondition = Arrays.asList(MockDiagnosisUtil.givenACondtion());
        PowerMockito.when(fhirConditionInterface.queryConditionList(conditionQueryImmutablePairLinkedHashMap)).thenReturn(lstCondition);
        List<Diagnosis> lstDiagnosis = diagnosisAntiCorruptionServiceImp.queryDiagnosisListByPatientID(patientID);
        Assert.assertThat(1, is(lstDiagnosis.size()));
    }
}