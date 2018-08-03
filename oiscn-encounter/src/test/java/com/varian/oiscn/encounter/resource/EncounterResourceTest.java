package com.varian.oiscn.encounter.resource;

import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.base.codesystem.PatientLabelPool;
import com.varian.oiscn.base.coverage.PayorInfoPool;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.FhirServerConfiguration;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.targetvolume.PlanTargetVolumeInfo;
import com.varian.oiscn.core.targetvolume.PlanTargetVolumeVO;
import com.varian.oiscn.core.targetvolume.TargetVolumeGroupVO;
import com.varian.oiscn.core.targetvolume.TargetVolumeVO;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.PatientEncounterEndPlan;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.assign.AssignResourceServiceImp;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPayment;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPaymentServiceImp;
import com.varian.oiscn.encounter.confirmpayment.ConfirmStatus;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstance;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceServiceImp;
import com.varian.oiscn.encounter.history.EncounterTitleItem;
import com.varian.oiscn.encounter.isocenter.ISOCenter;
import com.varian.oiscn.encounter.isocenter.ISOCenterServiceImp;
import com.varian.oiscn.encounter.isocenter.ISOCenterVO;
import com.varian.oiscn.encounter.isocenter.ISOPlanTretment;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoServiceImp;
import com.varian.oiscn.encounter.targetvolume.PlanTargetVolumeServiceImp;
import com.varian.oiscn.encounter.targetvolume.TargetVolumeServiceImp;
import com.varian.oiscn.encounter.treatmentworkload.TreatmentWorkloadServiceImp;
import com.varian.oiscn.encounter.treatmentworkload.TreatmentWorkloadVO;
import com.varian.oiscn.encounter.treatmentworkload.WorkloadPlanVO;
import com.varian.oiscn.encounter.util.MockDtoUtil;
import com.varian.oiscn.encounter.view.DynamicFormItemsAndTemplateInfo;
import com.varian.oiscn.encounter.view.EncounterVO;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by gbt1220 on 6/14/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({EncounterResource.class, StatusIconPool.class, PatientEncounterHelper.class, PayorInfoPool.class,
        SystemConfigPool.class, PatientLabelPool.class})
public class EncounterResourceTest {

    private CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp;

    private EncounterServiceImp encounterServiceImp;

    private EncounterResource encounterResource;

    private Configuration configuration;

    private Environment environment;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp;

    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;

    private CommunicationAntiCorruptionServiceImp communicationAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        Locale.setDefault(Locale.CHINA);
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        diagnosisAntiCorruptionServiceImp = PowerMockito.mock(DiagnosisAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DiagnosisAntiCorruptionServiceImp.class).withNoArguments().thenReturn(diagnosisAntiCorruptionServiceImp);
        encounterResource = new EncounterResource(configuration, environment);
        coverageAntiCorruptionServiceImp = PowerMockito.mock(CoverageAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CoverageAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(coverageAntiCorruptionServiceImp);
        treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);
        communicationAntiCorruptionServiceImp = PowerMockito.mock(CommunicationAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CommunicationAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(communicationAntiCorruptionServiceImp);
    }


    @Test
    public void givenHisIdWhenSearchThenReturnResponseOk() throws Exception {
        Long patientSer = 121212L;
        Encounter encounter = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(encounterServiceImp.queryByPatientSer(patientSer)).thenReturn(encounter);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer(String.valueOf(patientSer));
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(String.valueOf(patientSer))).thenReturn(patientDto);
        PowerMockito.when(diagnosisAntiCorruptionServiceImp.queryDiagnosisListByPatientID(String.valueOf(patientSer))).thenReturn(new ArrayList<>());
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryByPatientId(anyString())).thenReturn(new CoverageDto());
        PatientCacheService patientCacheService = PowerMockito.mock(PatientCacheService.class);
        PowerMockito.whenNew(PatientCacheService.class).withAnyArguments().thenReturn(patientCacheService);
        PowerMockito.when(patientCacheService.queryPatientByPatientId(Matchers.anyString())).thenReturn(new PatientDto());
        Response response = encounterResource.search(MockDtoUtil.givenUserContext(), patientSer);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void testSearchWithHisIdNotInFhirPatient() throws Exception {
        Long patientSer = 121212L;
        when(encounterServiceImp.queryByPatientSer(new Long(patientSer))).thenReturn(null);
        when(patientAntiCorruptionServiceImp.queryPatientByPatientId(String.valueOf(patientSer))).thenReturn(null);
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryByPatientId(anyString())).thenReturn(new CoverageDto());
        PatientCacheService patientCacheService = PowerMockito.mock(PatientCacheService.class);
        PowerMockito.whenNew(PatientCacheService.class).withAnyArguments().thenReturn(patientCacheService);
        PowerMockito.when(patientCacheService.queryPatientByPatientId(Matchers.anyString())).thenReturn(null);
        Response response = encounterResource.search(MockDtoUtil.givenUserContext(), patientSer);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.NO_CONTENT));
        Assert.assertNull(response.getEntity());
        response = encounterResource.search(MockDtoUtil.givenUserContext(),null);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testSearchWithPatientSerAndTranslateBodyPartCode() throws Exception {
        Long patientSer = 121212L;
        when(encounterServiceImp.queryByPatientSer(new Long(patientSer))).thenReturn(null);
        PatientDto patientFromFhir = mock(PatientDto.class);
        when(patientFromFhir.getPatientSer()).thenReturn(String.valueOf(patientSer));
        when(patientAntiCorruptionServiceImp.queryPatientByPatientId(String.valueOf(patientSer))).thenReturn(patientFromFhir);

        Diagnosis diagnosis = mock(Diagnosis.class);
        List<Diagnosis> diagnosisList = new ArrayList<>();
        diagnosisList.add(diagnosis);
        when(diagnosisAntiCorruptionServiceImp.queryDiagnosisListByPatientID(String.valueOf(patientSer))).thenReturn(diagnosisList);

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

//        when(fhirBodypartCode.equals(anyString())).thenReturn(true);
        PatientCacheService patientCacheService = PowerMockito.mock(PatientCacheService.class);
        PowerMockito.whenNew(PatientCacheService.class).withAnyArguments().thenReturn(patientCacheService);
        PowerMockito.when(patientCacheService.queryPatientByPatientId(Matchers.anyString())).thenReturn(new PatientDto());

        Response response = encounterResource.search(MockDtoUtil.givenUserContext(), patientSer);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        Object entity = response.getEntity();
        if (entity instanceof EncounterVO) {
            EncounterVO encounterVO = (EncounterVO) entity;
            Assert.assertSame(null, encounterVO.getBodypartDesc());
            Assert.assertNull(encounterVO.getAge());
            Assert.assertNull(encounterVO.getAllergyInfo());
            Assert.assertNull(encounterVO.getBodypart());
            Assert.assertNull(encounterVO.getDiagnosis());
            Assert.assertNull(encounterVO.getEcogDesc());
            Assert.assertNull(encounterVO.getEcogScore());
            Assert.assertNull(encounterVO.getInsuranceType());
            Assert.assertNull(encounterVO.getInsuranceTypeCode());
            Assert.assertNull(encounterVO.getTcode());
            Assert.assertNull(encounterVO.getNcode());
            Assert.assertNull(encounterVO.getMcode());
            Assert.assertNull(encounterVO.getPatientID());
            Assert.assertNull(encounterVO.getPatientSource());
            Assert.assertNull(encounterVO.getPhysicianComment());
            Assert.assertNull(encounterVO.getPositiveSign());
            Assert.assertNull(encounterVO.getStaging());
            Assert.assertNull(encounterVO.getWarningText());
            Assert.assertFalse(encounterVO.isUrgent());
        } else {
            Assert.fail("Response Entity is not a EncounterVO!!!");
        }
    }

    @Test
    public void givenPatientIdAndItemListWhenSaveSuccessfullyThenReturnTrue() throws Exception {
        UserContext userContext = MockDtoUtil.givenUserContext();
        DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = PowerMockito.mock(DynamicFormInstanceServiceImp.class);
        PowerMockito.whenNew(DynamicFormInstanceServiceImp.class).withArguments(userContext).thenReturn(dynamicFormInstanceServiceImp);
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        Long patientSer = 121212L;
         CarePathInstance carePathInstance = new CarePathInstance();
        String carePathInstanceId = "carePathInstanceId";
        carePathInstance.setId(carePathInstanceId);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(patientSer.toString())).thenReturn(Arrays.asList(carePathInstance));
        DynamicFormItemsAndTemplateInfo dynamicFormItemsAndTemplateInfo = new DynamicFormItemsAndTemplateInfo();
        List<KeyValuePair> itemKeyList = givenADynamicFormItemKeyList();
        dynamicFormItemsAndTemplateInfo.setRecordInfo(itemKeyList);
        dynamicFormItemsAndTemplateInfo.setTemplateInfo("templateInfo");
        String activityCode = "activityCode";
        PowerMockito.when(dynamicFormInstanceServiceImp.saveOrUpdate(patientSer, dynamicFormItemsAndTemplateInfo, activityCode, carePathInstanceId, "")).thenReturn("1");
        Response response = encounterResource.saveDynamicFormItems(userContext, patientSer, activityCode, "carePathInstanceId", "", dynamicFormItemsAndTemplateInfo);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenPatientIdAndItemKeyListWhenSearchItemValuesAndNotFoundInDBThenReturnEmptyValuesList() throws Exception {
        UserContext userContext = MockDtoUtil.givenUserContext();
        DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = PowerMockito.mock(DynamicFormInstanceServiceImp.class);
        PowerMockito.whenNew(DynamicFormInstanceServiceImp.class).withArguments(userContext).thenReturn(dynamicFormInstanceServiceImp);
        Long patientSer = 12314L;
        List<KeyValuePair> itemKeyList = givenADynamicFormItemKeyList();
        PowerMockito.when(dynamicFormInstanceServiceImp.queryByPatientSer(patientSer)).thenReturn(null);
        Response response = encounterResource.searchDynamicFormItems(userContext, patientSer, itemKeyList);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenPatientIdAndItemKeyListWhenSearchItemValuesThenReturnValuesList() throws Exception {
        UserContext userContext = MockDtoUtil.givenUserContext();
        DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = PowerMockito.mock(DynamicFormInstanceServiceImp.class);
        PowerMockito.whenNew(DynamicFormInstanceServiceImp.class).withArguments(userContext).thenReturn(dynamicFormInstanceServiceImp);
        Long patientId = 123456L;
        List<KeyValuePair> searchKeyList = givenADynamicFormItemKeyList();
        searchKeyList.add(new KeyValuePair("3", ""));
        DynamicFormInstance instance = givenADynamicFormInstance();
        PowerMockito.when(dynamicFormInstanceServiceImp.queryByPatientSer(patientId)).thenReturn(instance);
        Response response = encounterResource.searchDynamicFormItems(userContext, patientId, searchKeyList);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenPatientIdWhenSearchTargetVolumeThenReturnList() {
        try {
            UserContext userContext = MockDtoUtil.givenUserContext();
            TargetVolumeServiceImp targetVolumeServiceImp = PowerMockito.mock(TargetVolumeServiceImp.class);
            PowerMockito.whenNew(TargetVolumeServiceImp.class).withArguments(userContext).thenReturn(targetVolumeServiceImp);
            Long patientSer = 12122L;
            Long encounterId = 1212L;
            PowerMockito.when(encounterServiceImp.queryByPatientSer(patientSer)).thenReturn(new Encounter(){{
                setId(encounterId.toString());
            }});
            TargetVolumeGroupVO targetVolumeGroup = new TargetVolumeGroupVO();
            PowerMockito.when(targetVolumeServiceImp.queryTargetVolumeGroupByPatientSer(patientSer,encounterId)).thenReturn(targetVolumeGroup);
            Response response = encounterResource.searchTargetVolume(userContext, patientSer,null);
            assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));

            response = encounterResource.searchTargetVolume(userContext,null,null);
            Assert.assertEquals(response.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
//
//    @Test
//    public void givenPatientIdAndTargetVolumeGroupListThenSuccessSaveOrUpdateThenReturnTrue() {
//        try {
//            UserContext userContext = MockDtoUtil.givenUserContext();
//            TargetVolumeServiceImp targetVolumeServiceImp = PowerMockito.mock(TargetVolumeServiceImp.class);
//            PowerMockito.whenNew(TargetVolumeServiceImp.class).withArguments(userContext).thenReturn(targetVolumeServiceImp);
//            String patientId = "patientId";
//            String saveOrUpdateId = "100010";
//            List<TargetVolumeGroup> glist = Arrays.asList(new TargetVolumeGroup());
//            TargetVolume targetVolume = new TargetVolume();
//            targetVolume.setHisId(patientId);
//            targetVolume.setTargetVolumeGroupList(glist);
//            PowerMockito.when(targetVolumeServiceImp.saveTargetVolume(targetVolume)).thenReturn(saveOrUpdateId);
//            Response response = encounterResource.saveTargetVolume(userContext, patientId, glist);
//            assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
//        } catch (Exception e) {
//            Assert.fail(e.getMessage());
//        }
//    }

    @Test
    public void givenPatientIdWhenSearchISOCenterThenReturnList() {
        try {
            Long patientId = 1212L;
            Long encounterId = 121221L;
            UserContext userContext = MockDtoUtil.givenUserContext();
            ISOCenterServiceImp isoCenterServiceImp = PowerMockito.mock(ISOCenterServiceImp.class);
            PowerMockito.whenNew(ISOCenterServiceImp.class).withArguments(userContext).thenReturn(isoCenterServiceImp);
            List<ISOPlanTretment> list = Arrays.asList(new ISOPlanTretment() {{
                setPlanId("Lung RA");
                setSiteList(Arrays.asList(new ISOCenterVO() {{
                    setIsoName("ISO1");
                    setVrt(23.1);
                    setLng(44.2);
                    setLat(55.0);
                }}));
            }});
            PowerMockito.when(isoCenterServiceImp.queryPlanTreatmentByPatientSer(new Long(patientId),new Long(encounterId))).thenReturn(list);
            Response response = encounterResource.searchISOCenter(userContext, patientId,encounterId);
            Assert.assertEquals(response.getEntity(), list);
            response = encounterResource.searchISOCenter(userContext,null,encounterId);
            Assert.assertEquals(response.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientIdAndPlanTreatmentListThenSuccessSaveOrUpdateThenReturnTrue() {
        try {
            UserContext userContext = MockDtoUtil.givenUserContext();
            ISOCenterServiceImp isoCenterServiceImp = PowerMockito.mock(ISOCenterServiceImp.class);
            PowerMockito.whenNew(ISOCenterServiceImp.class).withArguments(userContext).thenReturn(isoCenterServiceImp);
            Long patientId = 1212L;
            Long encounterId = 111L;
            String saveOrUpdateId = "100010";
            ISOCenter isoCenter = new ISOCenter();
            isoCenter.setPatientSer(patientId);
            isoCenter.setEncounterId(String.valueOf(encounterId));
            isoCenter.setPlanList(Arrays.asList(new ISOPlanTretment() {{
                setPlanId("Node RA");
                setSiteList(Arrays.asList(new ISOCenterVO() {{
                    setIsoName("ISO1");
                    setVrt(24.1);
                    setLng(64.2);
                    setLat(55.9);
                }}));
            }}));
            PowerMockito.when(encounterServiceImp.queryByPatientSer(patientId)).thenReturn(new Encounter(){{
                setId(String.valueOf(encounterId));
            }});
            PowerMockito.when(isoCenterServiceImp.saveOrUpdateISOCenter(isoCenter)).thenReturn(saveOrUpdateId);
            Response response = encounterResource.saveISOCenter(userContext, patientId, isoCenter.getPlanList());
            Assert.assertThat(response.getEntity(), equalTo(true));

            response = encounterResource.saveISOCenter(userContext, null, isoCenter.getPlanList());
            Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenSearchConfirmPaymentThenReturnConfirmPaymentList() {
        try {
            PowerMockito.mockStatic(SystemConfigPool.class);
            PowerMockito.when(SystemConfigPool.queryConfigValueByName(Matchers.anyString())).thenReturn(Arrays.asList("DoTreatment"));
            ConfirmPaymentServiceImp confirmPaymentServiceImp = PowerMockito.mock(ConfirmPaymentServiceImp.class);
            PowerMockito.whenNew(ConfirmPaymentServiceImp.class).withAnyArguments().thenReturn(confirmPaymentServiceImp);
            Long patientSer =  12121L;
            Long patientSer2 = 121L;
            ConfirmPayment confirmPayment = new ConfirmPayment();
            confirmPayment.setPatientSer(patientSer);
            List<ConfirmPayment> confirmPaymentList = Arrays.asList(confirmPayment,new ConfirmPayment(){{
                setPatientSer(patientSer2);
            }});

            List<KeyValuePair> keyValuePairs = Arrays.asList(new KeyValuePair(patientSer.toString(),""),new KeyValuePair(patientSer2.toString(),""));
            List<String> patientSerList = Arrays.asList(patientSer.toString(),patientSer2.toString());

            Set<String> set = new HashSet<>();
            patientSerList.forEach(ps->set.add(ps));
            List<String> patientSearchList = new ArrayList<>();
            patientSearchList.addAll(set);
            PowerMockito.when(confirmPaymentServiceImp.queryConfirmPaymentListByPatientSerList(patientSearchList)).thenReturn(confirmPaymentList);
            UserContext userContext = MockDtoUtil.givenUserContext();
            Response response = encounterResource.searchConfirmPayment(userContext, keyValuePairs, null);
            Assert.assertEquals(response.getEntity(), confirmPaymentList);
            List<ConfirmStatus> confirmStatusList = new ArrayList<>();
            String activityCode = "ActivityCode";
            ConfirmStatus confirmStatus = new ConfirmStatus(activityCode, "testing", 1,0L);
            confirmStatusList.add(confirmStatus);
            confirmPayment.setConfirmStatusList(confirmStatusList);
            confirmPaymentList = Arrays.asList(confirmPayment);
            PowerMockito.when(confirmPaymentServiceImp.queryConfirmPaymentListByPatientSerList(patientSerList)).thenReturn(confirmPaymentList);
            PowerMockito.when(confirmPaymentServiceImp.containConfirmPayment(confirmPayment, activityCode)).thenReturn(true);
            response = encounterResource.searchConfirmPayment(userContext, keyValuePairs, activityCode);
            Assert.assertEquals("true", ((List<KeyValuePair>)response.getEntity()).get(0).getValue());

            response = encounterResource.searchConfirmPayment(userContext,null,activityCode);
            Assert.assertEquals(response.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenConfirmPaymentWhenSaveConfirmPaymentThenReturnTrue() {
        try {
            UserContext userContext = MockDtoUtil.givenUserContext();
            ConfirmPaymentServiceImp confirmPaymentServiceImp = PowerMockito.mock(ConfirmPaymentServiceImp.class);
            PowerMockito.whenNew(ConfirmPaymentServiceImp.class).withAnyArguments().thenReturn(confirmPaymentServiceImp);
            ConfirmPayment confirmPayment = new ConfirmPayment();
            PowerMockito.when(confirmPaymentServiceImp.saveOrUpdateConfirmPayment(confirmPayment)).thenReturn("3");
            Response response = encounterResource.saveConfirmPayment(userContext, 201707140001L, confirmPayment);
            Assert.assertTrue((Boolean) response.getEntity());

            response = encounterResource.saveConfirmPayment(userContext,null,confirmPayment);
            Assert.assertEquals(response.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenSearchTreatmentWorkloadThenReturnTreatmentWorkload() {
        try {
            TreatmentWorkloadVO treatmentWorkloadVO = new TreatmentWorkloadVO();
            Long patientSer = 1212L;
            Long encounterId = 111L;
            treatmentWorkloadVO.setPatientSer(patientSer);
            treatmentWorkloadVO.setEncounterId(String.valueOf(encounterId));
            treatmentWorkloadVO.setPlanList(new ArrayList<>());
            treatmentWorkloadVO.setWorker(new ArrayList<>());
            treatmentWorkloadVO.setSign(new ArrayList<>());
            TreatmentWorkloadServiceImp treatmentWorkloadServiceImp = PowerMockito.mock(TreatmentWorkloadServiceImp.class);
            PowerMockito.whenNew(TreatmentWorkloadServiceImp.class).withAnyArguments().thenReturn(treatmentWorkloadServiceImp);
            PowerMockito.when(treatmentWorkloadServiceImp.queryTreatmentWorkloadByPatientSer(patientSer,encounterId)).thenReturn(treatmentWorkloadVO);
            Response response = this.encounterResource.searchTreatmentWorkload(MockDtoUtil.givenUserContext(), patientSer,encounterId);
            Assert.assertNotNull(response.getEntity());

            response = encounterResource.searchTreatmentWorkload(MockDtoUtil.givenUserContext(),null,encounterId);
            Assert.assertEquals(response.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenTreatmentWorkloadWhenSaveTreatmentWorkloadThenReturnTrueOrFalse() {
        try {
            TreatmentWorkloadServiceImp treatmentWorkloadServiceImp = PowerMockito.mock(TreatmentWorkloadServiceImp.class);
            TreatmentWorkloadVO treatmentWorkloadVO = new TreatmentWorkloadVO();
            Response response = this.encounterResource.saveTreatmentWorkload(MockDtoUtil.givenUserContext(), treatmentWorkloadVO);
            Assert.assertTrue((Boolean) response.getEntity());

            treatmentWorkloadVO.setPlanList(Arrays.asList(new WorkloadPlanVO()));
            PowerMockito.whenNew(TreatmentWorkloadServiceImp.class).withAnyArguments().thenReturn(treatmentWorkloadServiceImp);
            PowerMockito.when(treatmentWorkloadServiceImp.createTreatmentWorkLoad(treatmentWorkloadVO)).thenReturn(true);
            response = this.encounterResource.saveTreatmentWorkload(MockDtoUtil.givenUserContext(), treatmentWorkloadVO);
            Assert.assertTrue((Boolean) response.getEntity());

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientWhenCompleteEncounterThenReturnOkResponse() throws Exception {
        EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        Long patientSer = 1212L;
        Encounter encounter = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(encounterServiceImp.queryByPatientSer(patientSer)).thenReturn(encounter);
        PowerMockito.when(encounterServiceImp.updateByPatientSer(encounter,patientSer)).thenReturn(true);

        PatientDto patientDto = MockDtoUtil.givenAPatient();
        patientDto.setPatientSer(patientSer.toString());
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(patientSer.toString())).thenReturn(patientDto);
        String activeStatusIconDesc = "statusIcon";
        String activeStatusCode = "statusCode";
        PowerMockito.when(configuration.getActiveStatusIconDesc()).thenReturn(activeStatusIconDesc);
        PowerMockito.mockStatic(StatusIconPool.class);
        PowerMockito.when(StatusIconPool.get(activeStatusIconDesc)).thenReturn(activeStatusCode);
        FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = PowerMockito.mock(FlagAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(FlagAntiCorruptionServiceImp.class).withNoArguments().thenReturn(flagAntiCorruptionServiceImp);
        PowerMockito.when(flagAntiCorruptionServiceImp.unmarkPatientStatusIcon(patientDto.getPatientSer(), activeStatusCode)).thenReturn(true);

        List<OrderDto> orderDtos = MockDtoUtil.givenOrderList();
        OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(orderAntiCorruptionServiceImp);
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientDto.getPatientSer())).thenReturn(orderDtos);

        List<AppointmentDto> appointmentDtos = MockDtoUtil.givenAppointmentListDto();
        AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withNoArguments().thenReturn(appointmentAntiCorruptionServiceImp);
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientId(patientDto.getPatientSer())).thenReturn(appointmentDtos);

        SetupPhotoServiceImp setupPhotoServiceImp = PowerMockito.mock(SetupPhotoServiceImp.class);
        PowerMockito.whenNew(SetupPhotoServiceImp.class).withAnyArguments().thenReturn(setupPhotoServiceImp);
        PowerMockito.when(setupPhotoServiceImp.clearSetupPhotos(anyLong())).thenReturn(true);

        PowerMockito.mockStatic(PatientEncounterHelper.class);
        PowerMockito.doNothing().when(PatientEncounterHelper.class, "syncEncounterCarePathByPatientSer", Matchers.anyString());

        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
        Date plandCtDate = new Date();
        String planId = "Plan1";
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = Optional.of(new TreatmentSummaryDto(){{
            setPlans(Arrays.asList(new PlanSummaryDto(){{
                setPlanSetupId(planId);
                setCreatedDt(plandCtDate);
            }},new PlanSummaryDto(){{
                setPlanSetupId("Plan2");
                setCreatedDt(new Date());
            }}));
        }});
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp
                .getActivityEncounterTxSummaryByPatientSer(Matchers.anyString()))
                .thenReturn(treatmentSummaryDtoOptional);
        PatientEncounterEndPlan encounterEndPlan = new PatientEncounterEndPlan(){{
            setCompletedPlan(Arrays.asList(new EncounterEndPlan(){{
                setPlanSetupId(planId);
                setPlanCreatedDt(plandCtDate);
            }}));
        }};
        PowerMockito.when(encounterServiceImp.createEncounterEndPlan(Matchers.anyList())).thenReturn(true);
        PowerMockito.doNothing().when(PatientEncounterHelper.class,"syncEncounterEndPlanByPatientSer",patientSer.toString());
        PowerMockito.when(PatientEncounterHelper.getEncounterEndPlanByPatientSer(String.valueOf(patientSer))).thenReturn(encounterEndPlan);

        AssignResourceServiceImp assignResourceServiceImp = PowerMockito.mock(AssignResourceServiceImp.class);
        PowerMockito.whenNew(AssignResourceServiceImp.class).withAnyArguments().thenReturn(assignResourceServiceImp);
        PowerMockito.when(assignResourceServiceImp.deleteAssignedResource(anyLong(), anyString())).thenReturn(true);


        PowerMockito.mockStatic(PatientLabelPool.class);
        String alertLabel = "Alert";
        String alertLabelCode = "1001";
        PowerMockito.when(configuration.getAlertPatientLabelDesc()).thenReturn(alertLabel);
        PowerMockito.when(PatientLabelPool.get(alertLabel)).thenReturn(alertLabelCode);

        String urgent = "Urgent";
        String urgentCode = "UrgentCode";
        PowerMockito.when(configuration.getUrgentStatusIconDesc()).thenReturn(urgent);
        PowerMockito.when(StatusIconPool.get(urgent)).thenReturn(urgentCode);
        PowerMockito.when(flagAntiCorruptionServiceImp.unmarkPatientStatusIcon(patientDto.getPatientSer(), urgentCode)).thenReturn(true);

        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientIdWithPhoto(patientSer.toString())).thenReturn(patientDto);
        PowerMockito.when(patientAntiCorruptionServiceImp.updatePatient(Matchers.any())).thenReturn(patientSer.toString());

        PowerMockito.when(communicationAntiCorruptionServiceImp.errorPhysicianComment(Matchers.any())).thenReturn("111");

        Patient patient = new Patient();
        patient.setPatientHistory("history");
        PowerMockito.when(encounterServiceImp.queryPatientByPatientSer(anyLong())).thenReturn(patient);

        Response response = encounterResource.completeEncounter(new UserContext(), patientSer);
        Assert.assertEquals(patientSer, response.getEntity());
        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(false);
        response = encounterResource.completeEncounter(new UserContext(), patientSer);
        Assert.assertEquals(patientSer, response.getEntity());

        response = encounterResource.completeEncounter(new UserContext(), null);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void givenPatientSerAndCommentWhenModifyPhysicianCommentThenReturnTrue() {
        try {
            EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
            PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
            Long patientSer = 1212L;
            String physicianComment = "comments";

            UserContext userContext = MockDtoUtil.givenUserContext();
            PowerMockito.when(encounterServiceImp.modifyPhysicianComment(Matchers.anyLong(), anyObject())).thenReturn(true);
            Response response = encounterResource.modifyPhysicianComment(userContext, patientSer, new EncounterVO() {{
                setPhysicianComment(physicianComment);
            }});
            Assert.assertTrue((Boolean) response.getEntity());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenQueryPhysicianCommentThenReturnComments() {
        try {
            EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
            PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
            Long patientSer = 1212L;
            String physicianComment = "comments";
            PowerMockito.when(encounterServiceImp.queryByPatientSer(patientSer)).thenReturn(new Encounter() {{
                setPhysicianComment(physicianComment);
            }});
            Response response = this.encounterResource.searchPhysicianComment(new UserContext(), patientSer);
            KeyValuePair keyValuePair = (KeyValuePair) response.getEntity();
            Assert.assertTrue(physicianComment.equals(keyValuePair.getValue()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenSearchTreatmentWorkloadHistoryThenReturnList() {
        Long patientSer = 1212L;
        Long encounterId = 111L;
        String treatmentDate = "2017-10-11 12:22:33";
        List<TreatmentWorkloadVO> treatmentWorkloadVOList = Arrays.asList(new TreatmentWorkloadVO() {{
            setPatientSer(patientSer);
            setTreatmentDate(treatmentDate);
        }});
        try {
            TreatmentWorkloadServiceImp treatmentWorkloadServiceImp = PowerMockito.mock(TreatmentWorkloadServiceImp.class);
            PowerMockito.whenNew(TreatmentWorkloadServiceImp.class).withAnyArguments().thenReturn(treatmentWorkloadServiceImp);
            PowerMockito.when(treatmentWorkloadServiceImp.queryTreatmentWorkloadListByPatientSer(patientSer,encounterId)).thenReturn(treatmentWorkloadVOList);
            Response response = encounterResource.searchTreatmentWorkloadHistory(new UserContext(), patientSer,encounterId);
            Assert.assertNotNull(response);
            Assert.assertTrue(treatmentWorkloadVOList.equals(response.getEntity()));

            response = encounterResource.searchTreatmentWorkloadHistory(MockDtoUtil.givenUserContext(),null,encounterId);
            Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerAndAllergyInfoWhenModifyingAllergyInfoThenReturnTrue() {
        try {
            EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
            PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
            Long patientSer = 1212L;
            String allergyInfo = "allergyInfo";
            PowerMockito.when(encounterServiceImp.modifyAllergyInfo(patientSer, allergyInfo)).thenReturn(true);
            Response response = encounterResource.modifyAllergyInfo(new UserContext(), patientSer, new EncounterVO() {{
                setAllergyInfo(allergyInfo);
            }});
            Assert.assertTrue((Boolean) response.getEntity());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testLinkNewCarePath() {
        try {
            EncounterResource spy = PowerMockito.spy(new EncounterResource(configuration, environment));
            UserContext userContext = new UserContext();
            Long patientSer = 1212L;
            String carepathTemplateId = "carepathTemplateId";
            String newCarePathInstanceId = "newCarePathInstanceId";
            PowerMockito.mockStatic(PatientEncounterHelper.class);
            PowerMockito.doNothing().when(PatientEncounterHelper.class, "syncEncounterCarePathByPatientSer", Matchers.anyString());

            PowerMockito.doReturn(true).when(spy, "cancelUncompletedTasks", patientSer);
            PowerMockito.doReturn(true).when(spy, "cancelUncompletedAppointments", patientSer);
            PowerMockito.doReturn(newCarePathInstanceId).when(spy, "linkCarepath", patientSer, carepathTemplateId);
            PowerMockito.doReturn(true).when(spy, "updateEncounterWithNewCarePath", userContext, patientSer, newCarePathInstanceId, EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);

            SetupPhotoServiceImp setupPhotoServiceImp = PowerMockito.mock(SetupPhotoServiceImp.class);
            PowerMockito.whenNew(SetupPhotoServiceImp.class).withAnyArguments().thenReturn(setupPhotoServiceImp);
            PowerMockito.when(setupPhotoServiceImp.clearSetupPhotos(anyLong())).thenReturn(true);

            Response response = spy.linkNewCarePath(userContext, patientSer, carepathTemplateId);
            Assert.assertEquals(Response.Status.ACCEPTED, response.getStatusInfo());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testLinkNewOptionalCarePath() {
        try {
            EncounterResource spy = PowerMockito.spy(new EncounterResource(configuration, environment));
            UserContext userContext = new UserContext();
            Long patientSer = 1212L;
            String carepathTemplateId = "carepathTemplateId";
            String newCarePathInstanceId = "newCarePathInstanceId";
            PowerMockito.mockStatic(PatientEncounterHelper.class);
            PowerMockito.doNothing().when(PatientEncounterHelper.class, "syncEncounterCarePathByPatientSer", Matchers.anyString());

            PowerMockito.doReturn(newCarePathInstanceId).when(spy, "linkCarepath", patientSer, carepathTemplateId);
            PowerMockito.doReturn(true).when(spy, "updateEncounterWithNewCarePath", userContext, patientSer, newCarePathInstanceId, EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL);
            SetupPhotoServiceImp setupPhotoServiceImp = PowerMockito.mock(SetupPhotoServiceImp.class);
            PowerMockito.whenNew(SetupPhotoServiceImp.class).withAnyArguments().thenReturn(setupPhotoServiceImp);
            PowerMockito.when(setupPhotoServiceImp.clearSetupPhotos(anyLong())).thenReturn(true);

            Response response = spy.linkNewOptionalCarePath(userContext, patientSer, carepathTemplateId);
            Assert.assertEquals(Response.Status.ACCEPTED, response.getStatusInfo());
        } catch (Exception e) {
            Assert.fail();
        }
    }
    @Test
    public void testSearchAllTargetVolumeName(){
        try {
            UserContext userContext = MockDtoUtil.givenUserContext();
            TargetVolumeServiceImp targetVolumeServiceImp = PowerMockito.mock(TargetVolumeServiceImp.class);
            PowerMockito.whenNew(TargetVolumeServiceImp.class).withArguments(userContext).thenReturn(targetVolumeServiceImp);
            Long patientSer = 1212L;
            Long encounterId = 1212L;
            PowerMockito.when(encounterServiceImp.queryByPatientSer(patientSer)).thenReturn(new Encounter(){{
                setId(encounterId.toString());
            }});
            TargetVolumeGroupVO targetVolumeGroup = new TargetVolumeGroupVO();
            targetVolumeGroup.setTargetVolumeList(Arrays.asList(new TargetVolumeVO(){{
                setName("TargetVolumeName");
            }}));
            PowerMockito.when(targetVolumeServiceImp.queryTargetVolumeGroupOnlyTargetVolumeExceptItemByPatientSer(patientSer,encounterId)).thenReturn(targetVolumeGroup);
            Response response = encounterResource.searchAllTargetVolumeName(userContext, patientSer,encounterId);
            assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
            List<KeyValuePair> list = (List)response.getEntity();
            Assert.assertNotNull(list);
            Assert.assertTrue(list.size() == 1);
            response = encounterResource.searchAllTargetVolumeName(userContext,null,encounterId);
            Assert.assertEquals(response.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testHistoryList() {
        Long patientSer = 121212L;
        List<EncounterTitleItem> result = PowerMockito.mock(ArrayList.class);
        PowerMockito.when(encounterServiceImp.listHistory(Mockito.anyLong())).thenReturn(result);
        Response response = encounterResource.historyList(MockDtoUtil.givenUserContext(), patientSer);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        Assert.assertSame(result, response.getEntity());
    }

    @Test
    public void testHistoryListBadPatientSer() {
        Long patientSer = null;
        List<EncounterTitleItem> result = PowerMockito.mock(ArrayList.class);
        PowerMockito.when(encounterServiceImp.listHistory(Mockito.anyLong())).thenReturn(result);
        Response response = encounterResource.historyList(MockDtoUtil.givenUserContext(), patientSer);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenEncounterIdAndPatientSerThenReturnEncounter() throws Exception {
        Long patientSer = 123L;
        Long encounterId = 123L;
        Encounter encounter = PowerMockito.mock(Encounter.class);
        PowerMockito.when(encounterServiceImp.queryEncounterByIdAndPatientSer(Matchers.anyLong(), Matchers.anyLong())).thenReturn(encounter);
        Response response = encounterResource.queryEncounterById(MockDtoUtil.givenUserContext(), patientSer, encounterId);
        Assert.assertSame(encounter, response.getEntity());
        response = encounterResource.queryEncounterById(MockDtoUtil.givenUserContext(), null, encounterId);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        response = encounterResource.queryEncounterById(MockDtoUtil.givenUserContext(), patientSer, null);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testSaveTargetVolume() throws Exception {
        TargetVolumeGroupVO targetVolumeGroupVO = new TargetVolumeGroupVO(){{
            setPatientSer(12121L);
            setEncounterId("121212");
        }};
        TargetVolumeServiceImp targetVolumeServiceImp = PowerMockito.mock(TargetVolumeServiceImp.class);
        PowerMockito.whenNew(TargetVolumeServiceImp.class).withAnyArguments().thenReturn(targetVolumeServiceImp);
        EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(targetVolumeGroupVO.getPatientSer()))
                .thenReturn(new Encounter(){{
                    setId("121212");
                }});
        PowerMockito.when(targetVolumeServiceImp.saveTargetVolume(targetVolumeGroupVO)).thenReturn(true);
        Response response = encounterResource.saveTargetVolume(new UserContext(),targetVolumeGroupVO);
        Assert.assertEquals(true,response.getEntity());
    }

    @Test
    public void testSavePlanTargetVolume() throws Exception {
        PlanTargetVolumeServiceImp planTargetVolumeServiceImp = PowerMockito.mock(PlanTargetVolumeServiceImp.class);
        PowerMockito.whenNew(PlanTargetVolumeServiceImp.class).withAnyArguments().thenReturn(planTargetVolumeServiceImp);
        PlanTargetVolumeVO planTargetVolumeVO = new PlanTargetVolumeVO();
        Response response = encounterResource.savePlanTargetVolume(new UserContext(),planTargetVolumeVO);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        planTargetVolumeVO.setPlanTargetVolumeList(Arrays.asList(new PlanTargetVolumeInfo()));
        planTargetVolumeVO.setPatientSer("1212");
        PowerMockito.when(planTargetVolumeServiceImp.savePlanTargetVolumeName(planTargetVolumeVO)).thenReturn(true);
        response = encounterResource.savePlanTargetVolume(new UserContext(),planTargetVolumeVO);
        Assert.assertEquals(true,response.getEntity());
    }

    @Test
    public void testSearchPlanTargetVolume() throws Exception {
        PlanTargetVolumeServiceImp planTargetVolumeServiceImp = PowerMockito.mock(PlanTargetVolumeServiceImp.class);
        PowerMockito.whenNew(PlanTargetVolumeServiceImp.class).withAnyArguments().thenReturn(planTargetVolumeServiceImp);
        Long patientSer = null;
        Long encounterId = 121L;
        Response response = encounterResource.searchPlanTargetVolume(MockDtoUtil.givenUserContext(),patientSer,encounterId);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        patientSer = 121212L;
        PowerMockito.when(encounterServiceImp.queryByPatientSer(patientSer)).thenReturn(new Encounter(){{
            setId(encounterId.toString());
        }});
        PlanTargetVolumeVO vo = new PlanTargetVolumeVO();
        PowerMockito.when(planTargetVolumeServiceImp.queryPlanTargetVolumeMappingByPatientSer(patientSer,encounterId))
                .thenReturn(vo);
        response = encounterResource.searchPlanTargetVolume(MockDtoUtil.givenUserContext(),patientSer,encounterId);
        assertThat(vo,equalTo(response.getEntity()));
    }


    private DynamicFormInstance givenADynamicFormInstance() {
        DynamicFormInstance instance = new DynamicFormInstance();
        instance.setDynamicFormItems(givenADynamicFormItemKeyList());
        return instance;
    }

    private List<KeyValuePair> givenADynamicFormItemKeyList() {
        List<KeyValuePair> result = new ArrayList<>();
        result.add(new KeyValuePair("1", ""));
        result.add(new KeyValuePair("2", ""));
        return result;
    }

//    private List<Map<String, String>> assemblePatientSerInstanceIdPairList() {
//        List<Map<String, String>> pair = new ArrayList<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("PatientSer", "1");
//        map.put("orderId", "instanceId1");
//        Map<String, String> map1 = new HashMap<>();
//        map1.put("PatientSer", "2");
//        map1.put("orderId", "instanceId2");
//        pair.add(map);
//        pair.add(map1);
//        return pair;
//    }

}
