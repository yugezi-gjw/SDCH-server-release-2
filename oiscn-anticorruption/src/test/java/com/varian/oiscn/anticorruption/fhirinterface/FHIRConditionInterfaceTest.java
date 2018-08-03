package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import com.varian.fhir.resources.Condition;
import com.varian.oiscn.anticorruption.converter.EnumConditionQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.datahelper.MockDiagnosisUtil;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IIdType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.util.TextUtils.isEmpty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-05-16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRConditionInterface.class, FHIRContextFactory.class})
public class FHIRConditionInterfaceTest {
    private static final String DIAGNOSIS_ID = "DiagnosisID";
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRConditionInterface fhirConditionInterface;

    private static Parameters givenAnOutParams() {
        Parameters outParams = new Parameters();
        List<Parameters.ParametersParameterComponent> lstParameterComponent = new ArrayList<>();
        Parameters.ParametersParameterComponent parameterComponent = new Parameters.ParametersParameterComponent();
        parameterComponent.setValue(new StringType("StageSummary"));
        lstParameterComponent.add(parameterComponent);
        outParams.setParameter(lstParameterComponent);
        return outParams;
    }

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirConditionInterface = new FHIRConditionInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenADiagnosisWhenCreateThenReturnID() {
        Condition condition = MockDiagnosisUtil.givenACondtion();
        ICreate iCreate = PowerMockito.mock(ICreate.class);
        PowerMockito.when(client.create()).thenReturn(iCreate);
        ICreateTyped iCreateTyped = PowerMockito.mock(ICreateTyped.class);
        PowerMockito.when(iCreate.resource(condition)).thenReturn(iCreateTyped);
        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iCreateTyped.execute()).thenReturn(methodOutcome);
        IIdType iIdType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(iIdType);
        PowerMockito.when(iIdType.getIdPart()).thenReturn(DIAGNOSIS_ID);
        String createdDiagnosisID = fhirConditionInterface.create(condition);
        Assert.assertEquals(createdDiagnosisID, DIAGNOSIS_ID);
    }

    @Test
    public void givenADiagnosisWhenUpdateThenReturnID() {
        Condition condition = MockDiagnosisUtil.givenACondtion();
        IUpdate iUpdate = PowerMockito.mock(IUpdate.class);
        PowerMockito.when(client.update()).thenReturn(iUpdate);
        IUpdateTyped iUpdateTyped = PowerMockito.mock(IUpdateTyped.class);
        PowerMockito.when(iUpdate.resource(condition)).thenReturn(iUpdateTyped);
        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iUpdateTyped.execute()).thenReturn(methodOutcome);
        IIdType iIdType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(iIdType);
        PowerMockito.when(iIdType.getIdPart()).thenReturn(DIAGNOSIS_ID);
        String updatedDiagnosisID = fhirConditionInterface.update(condition);
        Assert.assertEquals(updatedDiagnosisID, DIAGNOSIS_ID);
    }

    @Test
    public void givenADiagnosisWhenCreateThenThrowException() {
        Condition condition = MockDiagnosisUtil.givenACondtion();
        PowerMockito.when(client.create()).thenThrow(Exception.class);
        String createdDiagnosisID = fhirConditionInterface.create(condition);
        Assert.assertTrue(isEmpty(createdDiagnosisID));
    }

    @Test
    public void givenADxCodeDxSchemeStagingSchemeTNMCodeWhenCalculateThenReturnStageSummary() throws Exception {
        final String dxCode = "DxCode";
        final String dxScheme = "DxScheme";
        final String stageScheme = "StagingScheme";
        final String stageTCode = "T1";
        final String stageNCode = "N1";
        final String stageMCode = "M0";

        Parameters outParams = givenAnOutParams();

        Parameters inParams = PowerMockito.mock(Parameters.class);
        PowerMockito.whenNew(Parameters.class).withNoArguments().thenReturn(inParams);

        Parameters.ParametersParameterComponent parametersParameterComponent1 = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inParams.addParameter()).thenReturn(parametersParameterComponent1);
        PowerMockito.when(parametersParameterComponent1.setName(anyString())).thenReturn(parametersParameterComponent1);
        StringType stDxCode = PowerMockito.mock(StringType.class);
        PowerMockito.whenNew(StringType.class).withArguments(dxCode).thenReturn(stDxCode);
        PowerMockito.when(parametersParameterComponent1.setValue(stDxCode)).thenReturn(parametersParameterComponent1);

        Parameters.ParametersParameterComponent parametersParameterComponent2 = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inParams.addParameter()).thenReturn(parametersParameterComponent2);
        PowerMockito.when(parametersParameterComponent2.setName(anyString())).thenReturn(parametersParameterComponent2);
        StringType stDxScheme = PowerMockito.mock(StringType.class);
        PowerMockito.whenNew(StringType.class).withArguments(dxScheme).thenReturn(stDxScheme);
        PowerMockito.when(parametersParameterComponent2.setValue(stDxScheme)).thenReturn(parametersParameterComponent2);

        Parameters.ParametersParameterComponent parametersParameterComponent3 = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inParams.addParameter()).thenReturn(parametersParameterComponent3);
        PowerMockito.when(parametersParameterComponent3.setName(anyString())).thenReturn(parametersParameterComponent3);
        StringType stStageScheme = PowerMockito.mock(StringType.class);
        PowerMockito.whenNew(StringType.class).withArguments(stageScheme).thenReturn(stStageScheme);
        PowerMockito.when(parametersParameterComponent3.setValue(stStageScheme)).thenReturn(parametersParameterComponent3);

        Parameters.ParametersParameterComponent parametersParameterComponent4 = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inParams.addParameter()).thenReturn(parametersParameterComponent4);
        PowerMockito.when(parametersParameterComponent4.setName(anyString())).thenReturn(parametersParameterComponent4);

        CodeableConcept codeableConcept = PowerMockito.mock(CodeableConcept.class);
        PowerMockito.whenNew(CodeableConcept.class).withNoArguments().thenReturn(codeableConcept);
        Coding coding = PowerMockito.mock(Coding.class);
        PowerMockito.when(codeableConcept.addCoding()).thenReturn(coding);
        PowerMockito.when(coding.setCode(stageTCode)).thenReturn(coding);
        PowerMockito.when(coding.setSystem("N")).thenReturn(coding);
        PowerMockito.when(codeableConcept.addCoding()).thenReturn(coding);
        PowerMockito.when(coding.setCode(stageNCode)).thenReturn(coding);
        PowerMockito.when(coding.setSystem("N")).thenReturn(coding);
        PowerMockito.when(codeableConcept.addCoding()).thenReturn(coding);
        PowerMockito.when(coding.setCode(stageMCode)).thenReturn(coding);
        PowerMockito.when(coding.setSystem("M")).thenReturn(coding);
        PowerMockito.when(parametersParameterComponent4.setValue(codeableConcept)).thenReturn(parametersParameterComponent4);

        IOperation iOperation = PowerMockito.mock(IOperation.class);
        PowerMockito.when(client.operation()).thenReturn(iOperation);
        IOperationUnnamed iOperationUnnamed = PowerMockito.mock(IOperationUnnamed.class);
        PowerMockito.when(iOperation.onType(Condition.class)).thenReturn(iOperationUnnamed);
        IOperationUntyped iOperationUntyped = PowerMockito.mock(IOperationUntyped.class);
        PowerMockito.when(iOperationUnnamed.named(anyString())).thenReturn(iOperationUntyped);
        IOperationUntypedWithInput iOperationUntypedWithInput = PowerMockito.mock(IOperationUntypedWithInput.class);
        PowerMockito.when(iOperationUntyped.withParameters(inParams)).thenReturn(iOperationUntypedWithInput);

        PowerMockito.when(iOperationUntypedWithInput.execute()).thenReturn(outParams);

        String stageSummary = fhirConditionInterface.calculateStageSummary(dxCode, dxScheme, stageScheme, stageTCode, stageNCode, stageMCode);
        Assert.assertEquals(stageSummary, "StageSummary");
    }

    @Test
    public void givenANullMapWhenQueryThenThrowEmptyConditionList() {
        Map<EnumConditionQuery, ImmutablePair<EnumMatchQuery, Object>> conditionQueryImmutablePairLinkedHashMap = null;
        List<Condition> lstCondition = fhirConditionInterface.queryConditionList(conditionQueryImmutablePairLinkedHashMap);
        Assert.assertTrue(lstCondition.isEmpty());
    }

    @Test
    public void givenAnEmptyMapWhenQueryThenThrowEmptyConditionList() {
        Map<EnumConditionQuery, ImmutablePair<EnumMatchQuery, Object>> conditionQueryImmutablePairMap = new LinkedHashMap<>();
        List<Condition> lstCondition = fhirConditionInterface.queryConditionList(conditionQueryImmutablePairMap);
        Assert.assertTrue(lstCondition.isEmpty());
    }

    @Test
    public void givenAMapWhenQueryThenThrowException() {
        Map<EnumConditionQuery, ImmutablePair<EnumMatchQuery, Object>> conditionQueryImmutablePairMap = new LinkedHashMap<>();
        conditionQueryImmutablePairMap.put(EnumConditionQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "PatientID"));
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        List<Condition> lstCondition = fhirConditionInterface.queryConditionList(conditionQueryImmutablePairMap);
        Assert.assertTrue(lstCondition.isEmpty());
    }

    @Test
    public void givenAMapWithCategoryAndPatientIDWhenQueryThenReturnConditionList() throws Exception {
        Map<EnumConditionQuery, ImmutablePair<EnumMatchQuery, Object>> conditionQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        conditionQueryImmutablePairLinkedHashMap.put(EnumConditionQuery.CATEGORY, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "CategoryID"));
        conditionQueryImmutablePairLinkedHashMap.put(EnumConditionQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "PatientID"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Condition.class)).thenReturn(iQuery);

        StringClientParam stringClientParamCategory = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Condition.SP_CATEGORY).thenReturn(stringClientParamCategory);
        StringClientParam.IStringMatch iStringMatchCategory = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamCategory.matchesExactly()).thenReturn(iStringMatchCategory);
        ICriterion iCriterionCategory = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchCategory.value(anyString())).thenReturn(iCriterionCategory);
        PowerMockito.when(iQuery.where(iCriterionCategory)).thenReturn(iQuery);

        StringClientParam stringClientParamPatientID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Condition.SP_PATIENT).thenReturn(stringClientParamPatientID);
        StringClientParam.IStringMatch iStringMatchPatientID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamPatientID.matchesExactly()).thenReturn(iStringMatchPatientID);
        ICriterion iCriterionPatientID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPatientID.value(anyString())).thenReturn(iCriterionPatientID);
        PowerMockito.when(iQuery.and(iCriterionPatientID)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockDiagnosisUtil.givenAnDiagnosisBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Condition> lstCondition = fhirConditionInterface.queryConditionList(conditionQueryImmutablePairLinkedHashMap);
        Assert.assertThat(1, is(lstCondition.size()));
    }
}
