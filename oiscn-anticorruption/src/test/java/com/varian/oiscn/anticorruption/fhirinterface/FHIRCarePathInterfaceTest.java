package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import com.varian.fhir.resources.ActivityDefinition;
import com.varian.fhir.resources.Appointment;
import com.varian.fhir.resources.CarePath;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.datahelper.MockAppointmentUtil;
import com.varian.oiscn.anticorruption.datahelper.MockCarePathUtil;
import com.varian.oiscn.anticorruption.datahelper.MockTaskUtil;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.*;

/**
 * Created by fmk9441 on 2017-04-20.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRCarePathInterface.class, FHIRContextFactory.class, PatientEncounterHelper.class})
public class FHIRCarePathInterfaceTest {
    private static final String TEMPLATE_NAME = "TemplateName";
    private static final String PATIENT_ID = "PatientID";
    private IParser parser;
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRCarePathInterface fhirCarePathInterface;

    @Before
    public void setup() {
        parser = PowerMockito.mock(IParser.class);
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirCarePathInterface = new FHIRCarePathInterface();
        PowerMockito.mockStatic(PatientEncounterHelper.class);

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
        PowerMockito.when(factory.getXmlParser()).thenReturn(parser);
    }

    @Test
    public void givenATemplateNameWhenQueryThenReturnCarePath() throws Exception {
        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(CarePath.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(CarePath.SP_TEMPLATE).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);

        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(CarePath.class, ActivityDefinition.class))).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockCarePathUtil.givenACarePathBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        CarePath carePath = fhirCarePathInterface.queryCarePathByTemplateName(TEMPLATE_NAME);
        Assert.assertEquals(carePath.getTemplateName().getValue(), "TemplateName");
    }

    @Test
    public void givenATemplateNameWhenQueryThenThrowException() throws Exception {
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        CarePath carePath = fhirCarePathInterface.queryCarePathByTemplateName(TEMPLATE_NAME);
        Assert.assertNull(carePath);
    }

    @Test
    public void givenAPatientIDWhenQueryThenReturnCarePath() throws Exception {
        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(CarePath.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(CarePath.SP_PATIENT_LIST).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.values(Matchers.anyList())).thenReturn(iCriterion);
        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(CarePath.class, ActivityDefinition.class))).thenReturn(iQuery);

        PowerMockito.when(iQuery.count(Matchers.anyInt())).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockCarePathUtil.givenACarePathBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath());
        CarePath carePath = fhirCarePathInterface.queryLastCarePathByPatientID(PATIENT_ID);
        Assert.assertEquals(carePath.getPatient().getReference(), PATIENT_ID);
    }

    @Test
    public void givenAPatientIDWhenQueryThenThrowException() throws Exception {
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        CarePath carePath = fhirCarePathInterface.queryLastCarePathByPatientID(PATIENT_ID);
        Assert.assertNull(carePath);
    }

    @Test
    public void givenAPatientIDListWhenQueryThenReturnCarePathList() throws Exception {
        final List<String> patientIdList = Arrays.asList(PATIENT_ID);
        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(CarePath.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(CarePath.SP_PATIENT_LIST).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.values(anyList())).thenReturn(iCriterion);

        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(CarePath.class, ActivityDefinition.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.count(anyInt())).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockCarePathUtil.givenACarePathBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<CarePath> lstCarePath = fhirCarePathInterface.queryCarePathListByPatientIDList(patientIdList);
        Assert.assertThat(1, is(lstCarePath.size()));
    }

    @Test
    public void givenAPatientIDListWhenQueryThenThrowException() throws Exception {
        final List<String> patientIdList = Arrays.asList(PATIENT_ID);
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        List<CarePath> lstCarePath = fhirCarePathInterface.queryCarePathListByPatientIDList(patientIdList);
        Assert.assertThat(0, is(lstCarePath.size()));
    }

    @Test
    public void givenACarePathIDAndActivityIDAndTaskWhenScheduleThenReturnSuccess() throws Exception {
        final String carePathID = "CarePathID";
        final String activityInstanceID = "ActivityInstanceID";
        final Task task = MockTaskUtil.givenATask();

        Parameters inParams = PowerMockito.mock(Parameters.class);
        PowerMockito.whenNew(Parameters.class).withNoArguments().thenReturn(inParams);

        Parameters.ParametersParameterComponent parametersParameterComponent1 = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inParams.addParameter()).thenReturn(parametersParameterComponent1);
        PowerMockito.when(parametersParameterComponent1.setName(anyString())).thenReturn(parametersParameterComponent1);
        IdType idType = new IdType();
        PowerMockito.whenNew(IdType.class).withAnyArguments().thenReturn(idType);
        PowerMockito.when(parametersParameterComponent1.setValue(idType)).thenReturn(parametersParameterComponent1);

        Parameters.ParametersParameterComponent parametersParameterComponent2 = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inParams.addParameter()).thenReturn(parametersParameterComponent2);
        PowerMockito.when(parametersParameterComponent2.setName(anyString())).thenReturn(parametersParameterComponent2);
        StringType stringType = PowerMockito.mock(StringType.class);
        PowerMockito.whenNew(StringType.class).withArguments(parser.encodeResourceToString(task)).thenReturn(stringType);
        PowerMockito.when(parametersParameterComponent2.setValue(stringType)).thenReturn(parametersParameterComponent2);

        IOperation iOperation = PowerMockito.mock(IOperation.class);
        PowerMockito.when(client.operation()).thenReturn(iOperation);
        IOperationUnnamed iOperationUnnamed = PowerMockito.mock(IOperationUnnamed.class);
        IdDt idDt = PowerMockito.mock(IdDt.class);
        PowerMockito.whenNew(IdDt.class).withArguments(anyString(), anyString()).thenReturn(idDt);
        PowerMockito.when(iOperation.onInstance(idDt)).thenReturn(iOperationUnnamed);
        IOperationUntyped iOperationUntyped = PowerMockito.mock(IOperationUntyped.class);
        PowerMockito.when(iOperationUnnamed.named(anyString())).thenReturn(iOperationUntyped);
        IOperationUntypedWithInput iOperationUntypedWithInput = PowerMockito.mock(IOperationUntypedWithInput.class);
        PowerMockito.when(iOperationUntyped.withParameters(inParams)).thenReturn(iOperationUntypedWithInput);

        Parameters outParams = PowerMockito.mock(Parameters.class);
        List<ParametersParameterComponent> paraList = new ArrayList<>();
        ParametersParameterComponent para1 = PowerMockito.mock(ParametersParameterComponent.class);
        PowerMockito.when(para1.getName()).thenReturn("abcd");
        Type wrong = PowerMockito.mock(Type.class);
        PowerMockito.when(wrong.toString()).thenReturn("wrong");
        PowerMockito.when(para1.getValue()).thenReturn(wrong);
        paraList.add(para1);
        ParametersParameterComponent para2 = PowerMockito.mock(ParametersParameterComponent.class);
        PowerMockito.when(para2.getName()).thenReturn("id");
        Type correct = PowerMockito.mock(Type.class);
        PowerMockito.when(correct.toString()).thenReturn("correct");
        PowerMockito.when(para2.getValue()).thenReturn(correct);
        paraList.add(para2);
        PowerMockito.when(outParams.getParameter()).thenReturn(paraList);
        PowerMockito.when(iOperationUntypedWithInput.execute()).thenReturn(outParams);

        String id = fhirCarePathInterface.scheduleNextTask(carePathID, activityInstanceID, task);
        Assert.assertNotNull(id);
        Assert.assertEquals("correct", id);
    }

    @Test
    public void givenACarePathIDAndActivityIDAndAppointmentWhenScheduleThenReturnSuccess() throws Exception {
        final String carePathID = "CarePathID";
        final String activityInstanceID = "ActivityInstanceID";
        final Appointment appointment = MockAppointmentUtil.givenAnAppointment();

        Parameters inParams = PowerMockito.mock(Parameters.class);
        PowerMockito.whenNew(Parameters.class).withNoArguments().thenReturn(inParams);

        Parameters.ParametersParameterComponent parametersParameterComponent1 = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inParams.addParameter()).thenReturn(parametersParameterComponent1);
        PowerMockito.when(parametersParameterComponent1.setName(anyString())).thenReturn(parametersParameterComponent1);
        IdType idType = new IdType();
        PowerMockito.whenNew(IdType.class).withAnyArguments().thenReturn(idType);
        PowerMockito.when(parametersParameterComponent1.setValue(idType)).thenReturn(parametersParameterComponent1);

        Parameters.ParametersParameterComponent parametersParameterComponent2 = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inParams.addParameter()).thenReturn(parametersParameterComponent2);
        PowerMockito.when(parametersParameterComponent2.setName(anyString())).thenReturn(parametersParameterComponent2);
        StringType stringType = PowerMockito.mock(StringType.class);
        PowerMockito.whenNew(StringType.class).withArguments(parser.encodeResourceToString(appointment)).thenReturn(stringType);
        PowerMockito.when(parametersParameterComponent2.setValue(stringType)).thenReturn(parametersParameterComponent2);

        IOperation iOperation = PowerMockito.mock(IOperation.class);
        PowerMockito.when(client.operation()).thenReturn(iOperation);
        IOperationUnnamed iOperationUnnamed = PowerMockito.mock(IOperationUnnamed.class);
        IdDt idDt = PowerMockito.mock(IdDt.class);
        PowerMockito.whenNew(IdDt.class).withArguments(anyString(), anyString()).thenReturn(idDt);
        PowerMockito.when(iOperation.onInstance(idDt)).thenReturn(iOperationUnnamed);
        IOperationUntyped iOperationUntyped = PowerMockito.mock(IOperationUntyped.class);
        PowerMockito.when(iOperationUnnamed.named(anyString())).thenReturn(iOperationUntyped);
        IOperationUntypedWithInput iOperationUntypedWithInput = PowerMockito.mock(IOperationUntypedWithInput.class);
        PowerMockito.when(iOperationUntyped.withParameters(inParams)).thenReturn(iOperationUntypedWithInput);

        Parameters outParams = PowerMockito.mock(Parameters.class);
        PowerMockito.when(iOperationUntypedWithInput.execute()).thenReturn(outParams);

        String flag = fhirCarePathInterface.scheduleNextAppointment(carePathID, activityInstanceID, appointment);
        Assert.assertNotNull(flag);
    }

    @Test
    public void givenNewPatientWhenAssignCarePathThenNotFail() throws Exception {
        Parameters inputParams = PowerMockito.mock(Parameters.class);
        PowerMockito.whenNew(Parameters.class).withNoArguments().thenReturn(inputParams);
        Parameters.ParametersParameterComponent parameterComponent = PowerMockito.mock(Parameters.ParametersParameterComponent.class);
        PowerMockito.when(inputParams.addParameter()).thenReturn(parameterComponent);
        PowerMockito.when(parameterComponent.setName(anyString())).thenReturn(parameterComponent);
        PowerMockito.when(parameterComponent.setValue(any())).thenReturn(parameterComponent);
        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
        IOperation iOperation = PowerMockito.mock(IOperation.class);
        PowerMockito.when(client.operation()).thenReturn(iOperation);
        IOperationUnnamed iOperationUnnamed = PowerMockito.mock(IOperationUnnamed.class);
        PowerMockito.when(iOperation.onInstance(any())).thenReturn(iOperationUnnamed);
        IOperationUntyped iOperationUntyped = PowerMockito.mock(IOperationUntyped.class);
        PowerMockito.when(iOperationUnnamed.named(anyString())).thenReturn(iOperationUntyped);
        IOperationUntypedWithInput iOperationUntypedWithInput = PowerMockito.mock(IOperationUntypedWithInput.class);
        PowerMockito.when(iOperationUntyped.withParameters(inputParams)).thenReturn(iOperationUntypedWithInput);
        Parameters outParams = PowerMockito.mock(Parameters.class);
        PowerMockito.when(iOperationUntypedWithInput.execute()).thenReturn(outParams);

        try {
            fhirCarePathInterface.assignCarePath("1", "1", "1");
        } catch (Exception e) {
            Assert.fail();
        }
    }
}