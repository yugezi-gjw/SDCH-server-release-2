package com.varian.oiscn.patient.resource;

import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.base.assembler.RegistrationVOAssembler;
import com.varian.oiscn.base.codesystem.PatientLabelPool;
import com.varian.oiscn.base.common.EventHandlerRegistry;
import com.varian.oiscn.base.dynamicform.DynamicFormTemplate;
import com.varian.oiscn.base.dynamicform.DynamicFormTemplateServiceImp;
import com.varian.oiscn.base.extend.ImplementationExtensionService;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.user.PermissionService;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.vid.VIDGeneratorServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.FhirServerConfiguration;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.activity.WorkspaceCodeEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathServiceImpl;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceServiceImp;
import com.varian.oiscn.encounter.dynamicform.DynamicFormRecord;
import com.varian.oiscn.encounter.service.DynamicFormRecordServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.patient.progressstate.PatientProgressStateService;
import com.varian.oiscn.patient.service.PatientServiceImp;
import com.varian.oiscn.patient.util.MockDatabaseConnection;
import com.varian.oiscn.patient.util.MockDtoUtil;
import com.varian.oiscn.patient.view.PatientDynamicFormVO;
import com.varian.oiscn.patient.view.PatientWorkflowProgressVO;
import com.varian.oiscn.patient.view.WorkflowProgressNode;
import com.varian.oiscn.patient.view.WorkflowProgressNodeStatus;
import com.varian.oiscn.patient.workflowprogress.PatientWorkflowProgressHelper;
import com.varian.oiscn.util.I18nReader;
import io.dropwizard.setup.Environment;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by gbt1220 on 12/24/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientResource.class, PatientProgressStateService.class,
        EventHandlerRegistry.class, RegistrationVOAssembler.class,
        StatusIconPool.class, PatientLabelPool.class, ActivityCodesReader.class,
        BasicDataSourceFactory.class, SystemConfigPool.class,
        ConnectionPool.class, PatientEncounterHelper.class, ImplementationExtensionService.class,
        Class.class, EncounterCarePathServiceImpl.class, GroupPractitionerHelper.class,
        PermissionService.class,SystemConfigPool.class})
public class PatientResourceTest {
    private PatientResource resource;

    private Configuration configuration;

    private Environment environment;

    private PatientAntiCorruptionServiceImp patientHapiFhirServiceImp;

    private PractitionerAntiCorruptionServiceImp practitionerAntiCorruptionServiceImp;

    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;

    private DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp;

    private PatientServiceImp patientServiceImp;

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;
    private GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp;
    private DynamicFormTemplateServiceImp dynamicFormTemplateServiceImp;
    DynamicFormRecordServiceImp dynamicFormRecordServiceImp;
    private EncounterServiceImp encounterServiceImp;
    private PatientCacheService patientCacheService;

    private String alert = "Alert";

    private String urgent = "Urgent";

    private String active = "Active";

    @Before
    public void setup() throws Exception {
        Locale.setDefault(Locale.CHINA);
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        patientHapiFhirServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        practitionerAntiCorruptionServiceImp = PowerMockito.mock(PractitionerAntiCorruptionServiceImp.class);
        carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientHapiFhirServiceImp);
        PowerMockito.whenNew(PractitionerAntiCorruptionServiceImp.class).withNoArguments().thenReturn(practitionerAntiCorruptionServiceImp);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withNoArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);
        dynamicFormTemplateServiceImp = PowerMockito.mock(DynamicFormTemplateServiceImp.class);
        PowerMockito.whenNew(DynamicFormTemplateServiceImp.class).withNoArguments().thenReturn(dynamicFormTemplateServiceImp);
        patientCacheService = PowerMockito.mock(PatientCacheService.class);
        PowerMockito.whenNew(PatientCacheService.class).withAnyArguments().thenReturn(patientCacheService);
        resource = new PatientResource(configuration, environment);
        patientServiceImp = PowerMockito.mock(PatientServiceImp.class);
        PowerMockito.whenNew(PatientServiceImp.class).withAnyArguments().thenReturn(patientServiceImp);
        flagAntiCorruptionServiceImp = PowerMockito.mock(FlagAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(FlagAntiCorruptionServiceImp.class).withNoArguments().thenReturn(flagAntiCorruptionServiceImp);
        PowerMockito.mockStatic(StatusIconPool.class);
        PowerMockito.mockStatic(RegistrationVOAssembler.class);
        PowerMockito.mockStatic(PatientLabelPool.class);
        PowerMockito.when(configuration.getAlertPatientLabelDesc()).thenReturn(alert);
        PowerMockito.when(configuration.getUrgentStatusIconDesc()).thenReturn(urgent);
        PowerMockito.when(configuration.getActiveStatusIconDesc()).thenReturn(active);
        diagnosisAntiCorruptionServiceImp = PowerMockito.mock(DiagnosisAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DiagnosisAntiCorruptionServiceImp.class).withNoArguments().thenReturn(diagnosisAntiCorruptionServiceImp);
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(PowerMockito.mock(MockDatabaseConnection.class));
        PowerMockito.mockStatic(PatientEncounterHelper.class);
        dynamicFormRecordServiceImp = PowerMockito.mock(DynamicFormRecordServiceImp.class);
        PowerMockito.whenNew(DynamicFormRecordServiceImp.class).withAnyArguments().thenReturn(dynamicFormRecordServiceImp);
        groupAntiCorruptionServiceImp = PowerMockito.mock(GroupAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(GroupAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(groupAntiCorruptionServiceImp);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryGroupRoleNurse()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupRoleOncologist()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupRolePhysicist()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");

        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");
    }

    @Test
    public void testGetPhotoWithoutHisId() {
        Response response = resource.getPhoto(new UserContext(), "");
        Assert.assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
    }

    @Test
    public void testGetPhotoNormal() {
        List<Long> hisIdList = Arrays.asList(new Long[] {1L, 2L});
        byte[] mockPhotoBytes1 = new byte[]{33, 22, 33, 44};
        byte[] mockPhotoBytes2 = new byte[]{22, 33, 44, 44};
        Map<Long, byte[]> mockPhotoBytesMap = new HashMap<>();
        mockPhotoBytesMap.put(Long.valueOf(hisIdList.get(0)), mockPhotoBytes1);
        mockPhotoBytesMap.put(Long.valueOf(hisIdList.get(1)), mockPhotoBytes2);
        PowerMockito.when(patientServiceImp.getPhotoListByPatientSerList(hisIdList)).thenReturn(mockPhotoBytesMap);
        Response response = resource.getPhoto(new UserContext(), "1, 2");
        Object data = response.getEntity();
        if (data instanceof Map) {
            Map<String, String> actualData = (Map<String, String>) data;
            for (String key : actualData.keySet()) {
                String actualString = actualData.get(key);
                String expectedString = Base64.encodeBase64URLSafeString(mockPhotoBytesMap.get(key));
                Assert.assertEquals(expectedString, actualString);
            }
            Assert.assertEquals(mockPhotoBytesMap.size(), actualData.size());
        }
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void testGetPhotoWithHisIdButNoData() {
        List<Long> hisIdList = Arrays.asList(111L, 222L);
        Map<Long, byte[]> mockPhotoBytesMap = new HashMap<>();
        PowerMockito.when(patientServiceImp.getPhotoListByPatientSerList(hisIdList)).thenReturn(mockPhotoBytesMap);
        Response response = resource.getPhoto(new UserContext(), "111,222");
        Object data = response.getEntity();
        if (data instanceof Map) {
            Map<String, String> actualData = (Map<String, String>) data;
            Assert.assertEquals(0, actualData.size());
        }
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void testUpdatePhotoWithoutHisId() {
        RegistrationVO vo = PowerMockito.mock(RegistrationVO.class);
        Response response = resource.updatePhoto(new UserContext(), null, vo);
        Assert.assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
    }

    @Test
    public void testUpdatePhotoWithBadPhotoData() {
        RegistrationVO vo = PowerMockito.mock(RegistrationVO.class);
        PowerMockito.when(vo.getPhoto()).thenReturn("~~~");
        Long patientSer = 123545L;
        Response response = resource.updatePhoto(new UserContext(), patientSer, vo);
        Assert.assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
    }

    @Test
    public void testUpdatePhotoNormal() throws Exception {
        Long patientSer = 92342L;
        RegistrationVO vo = PowerMockito.mock(RegistrationVO.class);
        PowerMockito.when(vo.getPhoto()).thenReturn("abcdefg");
        byte[] photoByte = Base64.decodeBase64("abcdefg");
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer("123456");
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByPatientId(patientSer.toString())).thenReturn(patientDto);
        PowerMockito.when(patientHapiFhirServiceImp.update(patientSer, patientDto)).thenReturn(patientSer.toString());
        PowerMockito.when(patientServiceImp.updatePhotoByPatientSer(patientSer, photoByte)).thenReturn(1);
        PowerMockito.when(patientCacheService.queryPatientByPatientId(Matchers.anyString())).thenReturn(patientDto);
        Response response = resource.updatePhoto(new UserContext(), patientSer, vo);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.ACCEPTED));
    }

    @Test
    public void givenAPatientSerWhenQueryPatientAndHisIdIsBlankThenReturnResponseStatusIsBadRequest() {
        Response response = resource.searchByPatientSer(new UserContext(), null);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenAPatientSerWhenQueryPatientAndFhirServerNotFoundThenReturnResponseStatusIsNotFound() {
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByPatientIdWithPhoto(anyString())).thenReturn(null);
        Response response = resource.searchByPatientSer(MockDtoUtil.givenUserContext(), 12121L);
        assertThat(response.getEntity(), equalTo(new PatientDto()));
    }

    @Test
    public void givenAHisIdWhenQueryPatientAndHisIdIsBlankThenReturnResponseStatusIsBadRequest() {
        Response response = resource.searchByHIsId(new UserContext(), "");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenAHisIdWhenQueryPatientAndFhirServerNotFoundThenReturnResponseStatusIsNotFound() {
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(null);
        Response response = resource.searchByHIsId(MockDtoUtil.givenUserContext(), "notExistHisId");
        assertThat(response.getEntity(), equalTo(new PatientDto()));
    }
    @Test
    public void testQueryPatientWhenEncounterIsNullThenReturnResponseStatusIsOk() throws Exception {
        PatientDto dto = givenPatientDtoContainsLabels();
        Patient patient = new Patient();
        patient.setHisId(dto.getHisId());
        UserContext userContext = MockDtoUtil.givenUserContext();
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(dto);
        RegistrationVO registrationVO = MockDtoUtil.givenARegistrationVO();
        PowerMockito.when(RegistrationVOAssembler.getRegistrationVO(dto)).thenReturn(registrationVO);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(Long.parseLong(dto.getPatientSer()))).thenReturn(null);
        Response response = resource.searchByHIsId(userContext, dto.getHisId());
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenAPatientSerWhenQueryPatientAndFhirServerFoundThenReturnResponseStatusIsOk() throws Exception {
        PatientDto dto = givenPatientDtoContainsLabels();
        Patient patient = new Patient();
        patient.setPatientSer(Long.parseLong(dto.getPatientSer()));
        UserContext userContext = MockDtoUtil.givenUserContext();
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientWithPhotoByHisId(anyString())).thenReturn(dto);
        RegistrationVO registrationVO = MockDtoUtil.givenARegistrationVO();
        PowerMockito.when(RegistrationVOAssembler.getRegistrationVO(dto)).thenReturn(registrationVO);
        PowerMockito.when(patientServiceImp.queryPatientByPatientSer(dto.getPatientSer())).thenReturn(patient);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(Long.parseLong(dto.getPatientSer()))).thenReturn(MockDtoUtil.givenAnEncounter());
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

        List<Diagnosis> diagnosisList = new ArrayList<>();
        diagnosisList.add(MockDtoUtil.givenADiagnosis());
        PowerMockito.when(diagnosisAntiCorruptionServiceImp.queryDiagnosisListByPatientID(dto.getPatientSer())).thenReturn(diagnosisList);

        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = PowerMockito.mock(CoverageAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CoverageAntiCorruptionServiceImp.class).withNoArguments().thenReturn(coverageAntiCorruptionServiceImp);
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryByPatientId(anyString())).thenReturn(new CoverageDto());

        String urgentIconCode = "1";
        PowerMockito.when(StatusIconPool.get(urgent)).thenReturn(urgentIconCode);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(dto.getPatientSer(), urgentIconCode)).thenReturn(true);
        Response response = resource.searchByHIsId(userContext, dto.getHisId());
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenAPatientDtoWhenUpdatePatientAndChineseNameIsBlankThenReturnResponseStatusIsBadRequest() {
        RegistrationVO dto = MockDtoUtil.givenARegistrationVO();
        dto.setChineseName("");
        Response response = resource.update(new UserContext(), dto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        assertThat(response.getEntity(), equalTo(dto));
    }

    @Test
    public void givenAPatientDtoWhenUpdatePatientAndHisIdIsBlankThenReturnResponseStatusIsBadRequest() {
        RegistrationVO vo = MockDtoUtil.givenARegistrationVO();
        vo.setChineseName("newChinese");
        vo.setHisId("");
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByPatientId(vo.getPatientSer())).thenReturn(new PatientDto());
        Response response = resource.update(new UserContext(), vo);
        assertThat(response.getEntity(), equalTo(vo));
    }

    @Test
    public void givenAPatientDtoWhenUpdatePatientAndFhirServerFailToUpdateThenReturnResponseIsNotModified() throws Exception {
        PatientDto dto = MockDtoUtil.givenAPatient();
        RegistrationVO vo = MockDtoUtil.givenARegistrationVO();
        PowerMockito.when(RegistrationVOAssembler.getPatientDto(vo)).thenReturn(dto);
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByPatientId(vo.getPatientSer())).thenReturn(dto);
        PowerMockito.when(PatientLabelPool.get(alert)).thenReturn("1");
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByHisId(dto.getHisId())).thenReturn(dto);
        PowerMockito.when(patientHapiFhirServiceImp.updatePatient(dto)).thenReturn("");
        PowerMockito.when(patientServiceImp.queryPatientByPatientSer(dto.getHisId())).thenReturn(new Patient() {{
            setHisId(dto.getHisId());
        }});
        PowerMockito.when(patientCacheService.queryPatientByPatientId(Matchers.anyString())).thenReturn(dto);
        Response response = resource.update(new UserContext(), vo);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.NOT_MODIFIED));
        assertThat(response.getEntity(), equalTo(vo));
    }

    @Test
    public void givenAPatientDtoWhenUpdateActivePatientThenReturnResponseIsAccepted() throws Exception {
        PatientDto dto = MockDtoUtil.givenAPatient();
        RegistrationVO vo = MockDtoUtil.givenARegistrationVO();
        PowerMockito.when(RegistrationVOAssembler.getPatientDto(vo)).thenReturn(dto);
        PowerMockito.when(PatientLabelPool.get(alert)).thenReturn("1");
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByHisId(dto.getHisId())).thenReturn(dto);
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByPatientId(vo.getPatientSer())).thenReturn(dto);
        PowerMockito.when(patientHapiFhirServiceImp.updatePatient(dto)).thenReturn(vo.getPatientSer());
        Diagnosis diagnosis = MockDtoUtil.givenADiagnosis();
        PowerMockito.when(RegistrationVOAssembler.getDiagnosis(vo)).thenReturn(diagnosis);
        PowerMockito.when(diagnosisAntiCorruptionServiceImp.updateDiagnosis(diagnosis)).thenReturn("1");
        String urgentCode = "code";
        PowerMockito.when(StatusIconPool.get(urgent)).thenReturn(urgentCode);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(vo.getPatientSer(), urgentCode)).thenReturn(false);
        String activeCode = "active";
        PowerMockito.when(StatusIconPool.get(active)).thenReturn(activeCode);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(vo.getPatientSer(), activeCode)).thenReturn(true);
        PowerMockito.when(patientServiceImp.queryPatientByPatientSer(dto.getHisId())).thenReturn(new Patient() {{
            setHisId(dto.getHisId());
        }});
        PowerMockito.when(patientServiceImp.update(vo)).thenReturn(true);
        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = PowerMockito.mock(CoverageAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CoverageAntiCorruptionServiceImp.class).withNoArguments().thenReturn(coverageAntiCorruptionServiceImp);
        PowerMockito.when(coverageAntiCorruptionServiceImp.updateCoverage(anyObject())).thenReturn("1");

        Response response = resource.update(new UserContext(), vo);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.ACCEPTED));
        assertThat(response.getEntity(), equalTo(vo));
    }

    @Test
    public void givenAPatientDtoWhenUpdateDisActivePatientThenReturnResponseIsAccepted() throws Exception {
        PatientDto dto = MockDtoUtil.givenAPatient();
        RegistrationVO vo = MockDtoUtil.givenARegistrationVO();
        PowerMockito.when(RegistrationVOAssembler.getPatientDto(vo)).thenReturn(dto);
        PowerMockito.when(PatientLabelPool.get(alert)).thenReturn("1");
        PowerMockito.when(patientHapiFhirServiceImp.updatePatient(dto)).thenReturn(vo.getPatientSer());
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByHisId(dto.getHisId())).thenReturn(dto);
        PowerMockito.when(patientHapiFhirServiceImp.queryPatientByPatientId(vo.getPatientSer())).thenReturn(dto);
        Diagnosis diagnosis = MockDtoUtil.givenADiagnosis();
        PowerMockito.when(RegistrationVOAssembler.getDiagnosis(vo)).thenReturn(diagnosis);
        PowerMockito.when(diagnosisAntiCorruptionServiceImp.updateDiagnosis(diagnosis)).thenReturn("1");
        String urgentCode = "code";
        PowerMockito.when(StatusIconPool.get(urgent)).thenReturn(urgentCode);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(vo.getPatientSer(), urgentCode)).thenReturn(false);
        String activeCode = "active";
        PowerMockito.when(StatusIconPool.get(active)).thenReturn(activeCode);
        PowerMockito.when(flagAntiCorruptionServiceImp.checkPatientStatusIcon(vo.getPatientSer(), activeCode)).thenReturn(false);
        PowerMockito.when(flagAntiCorruptionServiceImp.markPatientStatusIcon(vo.getPatientSer(), activeCode)).thenReturn(true);
        PowerMockito.when(patientServiceImp.updateWithNewEncounter(vo)).thenReturn(true);
        PowerMockito.when(patientServiceImp.queryPatientByPatientSer(dto.getHisId())).thenReturn(new Patient() {{
            setHisId(dto.getHisId());
        }});
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryDefaultDepartment()).thenReturn("1");
        CarePathTemplate defaultTemplate = MockDtoUtil.givenCarePathTemplate();

        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(anyString())).thenReturn(defaultTemplate);
        PowerMockito.when(carePathAntiCorruptionServiceImp.assignCarePath(anyString(), anyString(), anyString())).thenReturn(true);

        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = PowerMockito.mock(CoverageAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CoverageAntiCorruptionServiceImp.class).withNoArguments().thenReturn(coverageAntiCorruptionServiceImp);
        PowerMockito.when(coverageAntiCorruptionServiceImp.updateCoverage(anyObject())).thenReturn("1");

        Response response = resource.update(new UserContext(), vo);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.ACCEPTED));
        assertThat(response.getEntity(), equalTo(vo));
    }

    private PatientDto givenPatientDtoContainsLabels() {
        PatientDto dto = MockDtoUtil.givenAPatient();
        PatientDto.PatientLabel label = new PatientDto.PatientLabel();
        label.setLabelId("1");
        label.setLabelTag("Alert");
        label.setLabelText("WarningText");
        dto.addPatientLabel(label);
        return dto;
    }

    @Test
    public void givenPatientSerWhenPatientWorkflowProgressThenReturnObject() {
        try {
            Long patientSer = 357L;
            PatientDto patientDto = new PatientDto();
            patientDto.setPatientSer(String.valueOf(patientSer));
            CarePathInstance carePathInstance = new CarePathInstance();
            PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(String.valueOf(patientSer))).thenReturn(carePathInstance);
            PatientWorkflowProgressHelper helper = PowerMockito.mock(PatientWorkflowProgressHelper.class);
            PowerMockito.whenNew(PatientWorkflowProgressHelper.class).withArguments(carePathInstance, null, null).thenReturn(helper);
            EncounterCarePathServiceImpl ecpService = PowerMockito.mock(EncounterCarePathServiceImpl.class);
            PowerMockito.whenNew(EncounterCarePathServiceImpl.class).withAnyArguments().thenReturn(ecpService);
            PowerMockito.when(ecpService.countCarePathByPatientSer(patientSer)).thenReturn(1);

            List<WorkflowProgressNode> list = new ArrayList<WorkflowProgressNode>() {{
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAppointment"), false, WorkflowProgressNodeStatus.COMPLETED));//制模定位申请
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.doImmobilization"), true, WorkflowProgressNodeStatus.IN_PROGRESS));//做制模
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.scheduleCTSim"), false, WorkflowProgressNodeStatus.NOT_STARTED));//预约CT定位
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.doCTSim"), true, WorkflowProgressNodeStatus.NOT_STARTED));//做CT
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.importCTImage"), false, WorkflowProgressNodeStatus.NOT_STARTED));//导入CT图像
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.criticalOrganContouring"), true, WorkflowProgressNodeStatus.NOT_STARTED));//勾画危及器官
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.targetContouring"), true, WorkflowProgressNodeStatus.NOT_STARTED));//勾画靶区
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.contouringApproval"), false, WorkflowProgressNodeStatus.NOT_STARTED));//靶区确认
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.placePlanningOrder"), false, WorkflowProgressNodeStatus.NOT_STARTED));//计划申请
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.treatmentPlanning"), false, WorkflowProgressNodeStatus.NOT_STARTED));//创建计划
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.txPlanningConfirmation"), true, WorkflowProgressNodeStatus.NOT_STARTED));//审核计划
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.txPlanningApproval"), false, WorkflowProgressNodeStatus.NOT_STARTED));//确认计划
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.scheduleRepositioning"), false, WorkflowProgressNodeStatus.NOT_STARTED));//预约复位
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.treatmentApproval"), true, WorkflowProgressNodeStatus.NOT_STARTED));//审核复位
                add(new WorkflowProgressNode(I18nReader.getLocaleValueByKey("PatientResourceTests.doTreatment"), true, WorkflowProgressNodeStatus.NOT_STARTED));//治疗
            }};
            PowerMockito.when(helper.getKeyActivityWorkflowOfPatient()).thenReturn(list);

            Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = Optional.of(new TreatmentSummaryDto() {{
                setPlans(Arrays.asList(new PlanSummaryDto() {{
                    setPlannedFractions(30);
                    setDeliveredFractions(10);
                }}));
            }});
            PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(String.valueOf(patientSer))).thenReturn(treatmentSummaryDtoOptional);
            Response response = resource.patientWorkflowProgress(MockDtoUtil.givenUserContext(), patientSer);
            Assert.assertNotNull(((PatientWorkflowProgressVO) response.getEntity()).getActivityNodes());
            PatientWorkflowProgressVO patientWorkflowProgressVO = (PatientWorkflowProgressVO) response.getEntity();
            Assert.assertTrue(patientWorkflowProgressVO.getActivityNodes().size() == 15);
            Assert.assertNotNull(patientWorkflowProgressVO.getTreatmentActivityNodes());
            Assert.assertTrue(patientWorkflowProgressVO.getTreatmentActivityNodes().size() == 1);
            Assert.assertNotNull(patientWorkflowProgressVO.getTreatmentActivityNodes().get(0).getActivityName());
            Assert.assertNotNull(patientWorkflowProgressVO.getTreatmentActivityNodes().get(0).getIsKeyActivity());
            Assert.assertNotNull(patientWorkflowProgressVO.getTreatmentActivityNodes().get(0).getTotalTreatmentCount());
            Assert.assertNotNull(patientWorkflowProgressVO.getTreatmentActivityNodes().get(0).getTreatedCount());
            Assert.assertNotNull(patientWorkflowProgressVO.getTreatmentActivityNodes().get(0).getStatus());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenPatientDynamicFormsThenReturnObject() {
        try {
            Long patientSer = 357L;
            String activityCode = "PlaceImmobilizationAndCTOrder";
            String activityCodeb = "PlacePlanningOrder";
            String activityCodec = "DoCTSim";
            PatientDto patientDto = new PatientDto();
            patientDto.setPatientSer(String.valueOf(patientSer));
            CarePathInstance carePathInstance = new CarePathInstance();
            carePathInstance.setActivityInstances(Arrays.asList(new ActivityInstance() {{
                                                                    setActivityCode(activityCode);
                                                                    setStatus(CarePathStatusEnum.COMPLETED);
                                                                    setLastModifiedDT(DateUtil.parse("2018-08-21 10:20:01"));
                                                                }}, new ActivityInstance() {{
                                                                    setActivityCode(activityCodeb);
                                                                    setStatus(CarePathStatusEnum.COMPLETED);
                                                                    setLastModifiedDT(DateUtil.parse("2018-08-21 13:30:01"));
                                                                }}, new ActivityInstance() {{
                                                                    setActivityCode(activityCodec);
                                                                    setStatus(CarePathStatusEnum.COMPLETED);
                                                                    setLastModifiedDT(DateUtil.parse("2018-08-21 10:40:01"));
                                                                }}
            ));
            PowerMockito.when(carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(String.valueOf(patientSer))).thenReturn(Arrays.asList(carePathInstance));

            PowerMockito.mockStatic(ActivityCodesReader.class);
            PowerMockito.when(ActivityCodesReader.getActivityCode(activityCode)).thenReturn(new ActivityCodeConfig() {
                {
                    setWorkspaceType(WorkspaceCodeEnum.DYNAMIC_FORM.name());
                    setName(activityCode);
                    addDynamicFormTemplateId("PlaceImmobilizationAndCTOrder");
                    addDynamicFormTemplateId("CTAndImmobilizationOrderTemplateSwitch");
                }
            });
            PowerMockito.when(ActivityCodesReader.getActivityCode(activityCodec)).thenReturn(new ActivityCodeConfig() {
                {
                    setName(activityCodec);
                    setWorkspaceType(WorkspaceCodeEnum.DYNAMIC_FORM.name());
                    addDynamicFormTemplateId("DoImmobilizationAndCT");
                }
            });
            PowerMockito.when(ActivityCodesReader.getActivityCode(activityCodeb)).thenReturn(new ActivityCodeConfig() {
                {
                    setName(activityCodeb);
                    setWorkspaceType(WorkspaceCodeEnum.DYNAMIC_FORM.name());
                    addDynamicFormTemplateId("PlacePlanningOrder");
                }
            });


            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(Arrays.asList("PlaceImmobilizationAndCTOrder"))).thenReturn(Arrays.asList(new DynamicFormTemplate() {{
                setTemplateName(I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAndCTOrder")); //制模CT定位申请单
            }}));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(Arrays.asList("CTAndImmobilizationOrderTemplateSwitch"))).thenReturn(Arrays.asList(new DynamicFormTemplate() {{
                setTemplateName(I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAndCTOrder2")); //制模CT定位申请单2
            }}));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(Arrays.asList("DoImmobilizationAndCT"))).thenReturn(Arrays.asList(new DynamicFormTemplate() {{
                setTemplateName(I18nReader.getLocaleValueByKey("PatientResourceTests.doImmobilizationAndCT")); //制模CT定位记录单
            }}));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(Arrays.asList("PlacePlanningOrder"))).thenReturn(Arrays.asList(new DynamicFormTemplate() {{
                setTemplateName(I18nReader.getLocaleValueByKey("PatientResourceTests.placePlanningOrders")); //放射治疗申请单
            }}));
            Long encounterId = 1212L;
            Encounter encounter = new Encounter();
            encounter.setId(String.valueOf(encounterId));
            PowerMockito.when(encounterServiceImp.queryByPatientSer(patientSer)).thenReturn(encounter);
            List<DynamicFormRecord> dynamicFormRecordList = Arrays.asList(new DynamicFormRecord(){{
                setTemplateId("PlaceImmobilizationAndCTOrder");
                setEncounterId(encounterId);
                setHisId("hisId");
                setCarePathInstanceId("111");
                setCreateDate(new Date());
                setId("111111");
                setCreatedUser("sysadmin");
                setDynamicFormRecordInfo(Arrays.asList(new KeyValuePair("field1","value1")));
            }});
            PowerMockito.when(dynamicFormRecordServiceImp.queryDynamicFormRecordInfoByEncounterId(patientSer, encounterId)).thenReturn(dynamicFormRecordList);
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList("PlaceImmobilizationAndCTOrder"))).thenReturn(Arrays.asList(new KeyValuePair("PlaceImmobilizationAndCTOrder", I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAndCTOrder"))));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList("CTAndImmobilizationOrderTemplateSwitch"))).thenReturn(Arrays.asList(new KeyValuePair("CTAndImmobilizationOrderTemplateSwitch", I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAndCTOrder2"))));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList("DoImmobilizationAndCT"))).thenReturn(Arrays.asList(new KeyValuePair("DoImmobilizationAndCT", I18nReader.getLocaleValueByKey("PatientResourceTests.doImmobilizationAndCT"))));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList("PlacePlanningOrder"))).thenReturn(Arrays.asList(new KeyValuePair("PlacePlanningOrder", I18nReader.getLocaleValueByKey("PatientResourceTests.placePlanningOrders"))));
            UserContext userContext = MockDtoUtil.givenUserContext();
            DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = PowerMockito.mock(DynamicFormInstanceServiceImp.class);
            PowerMockito.whenNew(DynamicFormInstanceServiceImp.class).withArguments(userContext).thenReturn(dynamicFormInstanceServiceImp);
            PowerMockito.when(dynamicFormInstanceServiceImp.queryFieldValueByPatientSerListAndFieldName(String.valueOf(patientSer), activityCode + ".templateId")).thenReturn("CTAndImmobilizationOrderTemplateSwitch");
            Response response = resource.queryPatientDynamicForms(userContext, patientSer);
            Assert.assertNotNull(response.getEntity());
            List<PatientDynamicFormVO> patientDynamicFormVOS = (List<PatientDynamicFormVO>) response.getEntity();
            Collections.sort(patientDynamicFormVOS);
            PatientDynamicFormVO patientDynamicFormVO = patientDynamicFormVOS.get(0);
            Assert.assertNotNull(patientDynamicFormVO.getCompletedDt());
            Assert.assertNotNull(patientDynamicFormVO.getId());
            Assert.assertNotNull(patientDynamicFormVO.getSelectedTemplateHeader());
            Assert.assertNotNull(patientDynamicFormVO.getSelectedTemplateId());
            new PatientDynamicFormVO("id","templateId","header","carePathInstanceId", "2017-12-12");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testListDynamicFormHistory() {
        try {
            Long patientSer = 357L;
            String activityCode = "PlaceImmobilizationAndCTOrder";
            String activityCodeb = "PlacePlanningOrder";
            String activityCodec = "DoCTSim";
            PatientDto patientDto = new PatientDto();
            patientDto.setPatientSer(String.valueOf(patientSer));
            CarePathInstance carePathInstance = new CarePathInstance();
            carePathInstance.setActivityInstances(Arrays.asList(new ActivityInstance() {{
                                                                    setActivityCode(activityCode);
                                                                    setStatus(CarePathStatusEnum.COMPLETED);
                                                                    setLastModifiedDT(DateUtil.parse("2018-08-21 10:20:01"));
                                                                }}, new ActivityInstance() {{
                                                                    setActivityCode(activityCodeb);
                                                                    setStatus(CarePathStatusEnum.COMPLETED);
                                                                    setLastModifiedDT(DateUtil.parse("2018-08-21 13:30:01"));
                                                                }}, new ActivityInstance() {{
                                                                    setActivityCode(activityCodec);
                                                                    setStatus(CarePathStatusEnum.COMPLETED);
                                                                    setLastModifiedDT(DateUtil.parse("2018-08-21 10:40:01"));
                                                                }}
            ));
            PowerMockito.when(carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(String.valueOf(patientSer))).thenReturn(Arrays.asList(carePathInstance));

            PowerMockito.mockStatic(ActivityCodesReader.class);
            PowerMockito.when(ActivityCodesReader.getActivityCode(activityCode)).thenReturn(new ActivityCodeConfig() {
                {
                    setWorkspaceType(WorkspaceCodeEnum.DYNAMIC_FORM.name());
                    setName(activityCode);
                    addDynamicFormTemplateId("PlaceImmobilizationAndCTOrder");
                    addDynamicFormTemplateId("CTAndImmobilizationOrderTemplateSwitch");
                }
            });
            PowerMockito.when(ActivityCodesReader.getActivityCode(activityCodec)).thenReturn(new ActivityCodeConfig() {
                {
                    setName(activityCodec);
                    setWorkspaceType(WorkspaceCodeEnum.DYNAMIC_FORM.name());
                    addDynamicFormTemplateId("DoImmobilizationAndCT");
                }
            });
            PowerMockito.when(ActivityCodesReader.getActivityCode(activityCodeb)).thenReturn(new ActivityCodeConfig() {
                {
                    setName(activityCodeb);
                    setWorkspaceType(WorkspaceCodeEnum.DYNAMIC_FORM.name());
                    addDynamicFormTemplateId("PlacePlanningOrder");
                }
            });


            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(Arrays.asList("PlaceImmobilizationAndCTOrder"))).thenReturn(Arrays.asList(new DynamicFormTemplate() {{
                setTemplateName(I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAndCTOrder")); //制模CT定位申请单
            }}));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(Arrays.asList("CTAndImmobilizationOrderTemplateSwitch"))).thenReturn(Arrays.asList(new DynamicFormTemplate() {{
                setTemplateName(I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAndCTOrder2")); //制模CT定位申请单2
            }}));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(Arrays.asList("DoImmobilizationAndCT"))).thenReturn(Arrays.asList(new DynamicFormTemplate() {{
                setTemplateName(I18nReader.getLocaleValueByKey("PatientResourceTests.doImmobilizationAndCT")); //制模CT定位记录单
            }}));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(Arrays.asList("PlacePlanningOrder"))).thenReturn(Arrays.asList(new DynamicFormTemplate() {{
                setTemplateName(I18nReader.getLocaleValueByKey("PatientResourceTests.placePlanningOrders")); //放射治疗申请单
            }}));
            Long encounterId = 12121L;
            Encounter encounter = new Encounter();
            encounter.setId(String.valueOf(encounterId));
            PowerMockito.when(encounterServiceImp.queryByPatientSer(patientSer)).thenReturn(encounter);
            List<DynamicFormRecord> dynamicFormRecordList = Arrays.asList(new DynamicFormRecord(){{
                setTemplateId("PlaceImmobilizationAndCTOrder");
                setEncounterId(encounterId);
                setHisId("hisId");
                setCarePathInstanceId("111");
                setCreateDate(new Date());
                setId("111111");
                setCreatedUser("sysadmin");
                setDynamicFormRecordInfo(Arrays.asList(new KeyValuePair("field1","value1")));
            }});
            PowerMockito.when(dynamicFormRecordServiceImp.queryDynamicFormRecordInfoByEncounterId(patientSer, encounterId)).thenReturn(dynamicFormRecordList);
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList("PlaceImmobilizationAndCTOrder"))).thenReturn(Arrays.asList(new KeyValuePair("PlaceImmobilizationAndCTOrder", I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAndCTOrder"))));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList("CTAndImmobilizationOrderTemplateSwitch"))).thenReturn(Arrays.asList(new KeyValuePair("CTAndImmobilizationOrderTemplateSwitch", I18nReader.getLocaleValueByKey("PatientResourceTests.placeImmobilizationAndCTOrder2"))));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList("DoImmobilizationAndCT"))).thenReturn(Arrays.asList(new KeyValuePair("DoImmobilizationAndCT", I18nReader.getLocaleValueByKey("PatientResourceTests.doImmobilizationAndCT"))));
            PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList("PlacePlanningOrder"))).thenReturn(Arrays.asList(new KeyValuePair("PlacePlanningOrder", I18nReader.getLocaleValueByKey("PatientResourceTests.placePlanningOrders"))));
            UserContext userContext = MockDtoUtil.givenUserContext();
            DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = PowerMockito.mock(DynamicFormInstanceServiceImp.class);
            PowerMockito.whenNew(DynamicFormInstanceServiceImp.class).withArguments(userContext).thenReturn(dynamicFormInstanceServiceImp);
            PowerMockito.when(dynamicFormInstanceServiceImp.queryFieldValueByPatientSerListAndFieldName(String.valueOf(patientSer), activityCode + ".templateId")).thenReturn("CTAndImmobilizationOrderTemplateSwitch");
            Response response = resource.listDynamicFormHistory(userContext, patientSer, 12121L);
            Assert.assertNotNull(response.getEntity());
            List<PatientDynamicFormVO> patientDynamicFormVOS = (List<PatientDynamicFormVO>) response.getEntity();
            Collections.sort(patientDynamicFormVOS);
            PatientDynamicFormVO patientDynamicFormVO = patientDynamicFormVOS.get(0);
            Assert.assertNotNull(patientDynamicFormVO.getCompletedDt());
            Assert.assertNotNull(patientDynamicFormVO.getId());
            Assert.assertNotNull(patientDynamicFormVO.getSelectedTemplateHeader());
            Assert.assertNotNull(patientDynamicFormVO.getSelectedTemplateId());
            new PatientDynamicFormVO("id","templateId","header", "carePathInstanceId", "2017-12-12");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void givenHisIdWhenSearchDeviceFromDynamicFormThenReturnObject() {
        Long patientSer = 1222L;
        String deviceCode = "21EX";
        DeviceDto deviceDto = new DeviceDto() {{
            setCode(deviceCode);
            setId("1101");
            setName(deviceCode);
            setSchedulable(true);
        }};
        DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp = PowerMockito.mock(DeviceAntiCorruptionServiceImp.class);
        DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = PowerMockito.mock(DynamicFormInstanceServiceImp.class);

        try {
            PowerMockito.whenNew(DeviceAntiCorruptionServiceImp.class).withNoArguments().thenReturn(deviceAntiCorruptionServiceImp);
            PowerMockito.whenNew(DynamicFormInstanceServiceImp.class).withAnyArguments().thenReturn(dynamicFormInstanceServiceImp);
            PowerMockito.when(dynamicFormInstanceServiceImp.queryFieldValueByPatientSerListAndFieldName(String.valueOf(patientSer), "jiasuqi")).thenReturn(deviceCode);
            PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByCode(deviceCode)).thenReturn(deviceDto);
            Response response = resource.searchDeviceFromDynamicForm(new UserContext(), patientSer);
            DeviceDto obj = (DeviceDto) response.getEntity();
            Assert.assertNotNull(obj);
            Assert.assertTrue(deviceCode.equals(obj.getCode()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenHisIdAndPatientHistoryWhenModifyPatientHistoryThenReturnResponseStatusOk() {
        try {
            Long patientSer = 1212L;
            RegistrationVO registrationVO = new RegistrationVO() {{
                setPatientSer(String.valueOf(patientSer));
                setPatientHistory("Patient history");
            }};
            PatientServiceImp patientServiceImp = PowerMockito.mock(PatientServiceImp.class);
            PowerMockito.whenNew(PatientServiceImp.class).withAnyArguments().thenReturn(patientServiceImp);
            PowerMockito.when(patientServiceImp.updatePatientHistory(patientSer, registrationVO.getPatientHistory())).thenReturn(true);
            Response response = resource.modifyPatientHistory(new UserContext(), patientSer, registrationVO);
            Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenHisIdListReturnPhysicianCommentsInBatch() {
        List<String> hisIdList = new ArrayList<>();
        hisIdList.add("hisIdDoesNotExist");
        hisIdList.add("hisIdDoesNotExist");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("key", "value");
        PowerMockito.when(encounterServiceImp.getPhysicianCommentsInBatch(hisIdList)).thenReturn(hashMap);

        List<KeyValuePair> keyValuePairList = new ArrayList<>();
        keyValuePairList.add(new KeyValuePair("", "hisIdDoesNotExist"));
        keyValuePairList.add(new KeyValuePair("", "hisIdDoesNotExist"));
        Assert.assertEquals(hashMap, resource.getPhysicianCommentsInBatch(new UserContext(), keyValuePairList).getEntity());
    }

    @Test
    public void givenWhenGetVIDThenReturnVID() throws Exception {
        VIDGeneratorServiceImp serviceImp = PowerMockito.mock(VIDGeneratorServiceImp.class);
        PowerMockito.whenNew(VIDGeneratorServiceImp.class).withNoArguments().thenReturn(serviceImp);
        String vid = "testVId";
        PowerMockito.when(serviceImp.generateVID()).thenReturn(vid);
        Response response = resource.searchVID(new UserContext());
        Assert.assertEquals(vid, response.getEntity());
    }

    // The method queryDynamicFormRecordInfoById is no longer needed.
//    @Test
//    public void givenIdThenReturnDynamicForm() {
//        String id = "id";
//        PowerMockito.when(dynamicFormRecordServiceImp.queryDynamicFormRecordInfoById(id)).thenReturn("value");
//        Response response = resource.queryExistingDynamicForm(new UserContext(), "1212", id);
//        Assert.assertEquals("value", response.getEntity().toString());
//    }

    @Test
    public void testCheckVidExists(){
        String vid = "exitsVid";
        Long patientSer = 12345L;
        List<PatientDto> dtoList = new ArrayList<>();
        dtoList.add(new PatientDto(){{
            setPatientSer("12345");
        }});
        dtoList.add(new PatientDto(){{
            setPatientSer("56789");
        }});

        PowerMockito.when(patientHapiFhirServiceImp.queryByAriaId(vid)).thenReturn(dtoList);
        Response response = resource.checkVidExists(new UserContext(), vid, patientSer);
        KeyValuePair keyValuePair = (KeyValuePair) response.getEntity();
        Assert.assertEquals("true",keyValuePair.getValue());
        vid = "notExistVid";
        PowerMockito.when(patientHapiFhirServiceImp.queryByAriaId(vid)).thenReturn(null);
        response = resource.checkVidExists(new UserContext(),vid, patientSer);
        keyValuePair = (KeyValuePair) response.getEntity();
        Assert.assertEquals("false",keyValuePair.getValue());

        dtoList = new ArrayList<>();
        dtoList.add(new PatientDto(){{
            setPatientSer("");
        }});
        PowerMockito.when(patientHapiFhirServiceImp.queryByAriaId(vid)).thenReturn(dtoList);
        response = resource.checkVidExists(new UserContext(), vid,patientSer);
        keyValuePair = (KeyValuePair) response.getEntity();
        Assert.assertEquals("true", keyValuePair.getValue());

        dtoList.clear();
        dtoList.add(new PatientDto(){{
            setPatientSer("123");
        }});
        PowerMockito.when(patientHapiFhirServiceImp.queryByAriaId(vid)).thenReturn(dtoList);
        response = resource.checkVidExists(new UserContext(), vid, 123L);
        keyValuePair = (KeyValuePair) response.getEntity();
        Assert.assertEquals("false", keyValuePair.getValue());
    }

    @Test
    public void testGetPatientAuthorityTable() throws Exception {
        PowerMockito.mockStatic(GroupPractitionerHelper.class);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        String oncologist = SystemConfigPool.queryGroupRoleOncologist();
        PowerMockito.when(userContext.getLogin()).thenReturn(new Login(){{
            setGroup(oncologist);
        }});

        GroupTreeNode root = buildGroupThreeNode();
        PowerMockito.when(GroupPractitionerHelper.getOncologyGroupTreeNode()).thenReturn(root);
        List<GroupDto> groupDtoList = Arrays.asList(new GroupDto(){{
            setGroupId("2");
            setGroupName("Oncologist_头组");
        }});
        PowerMockito.when(GroupPractitionerHelper.copy(root)).thenReturn(root);
        PowerMockito.when(groupAntiCorruptionServiceImp.queryGroupListByResourceID(Matchers.anyString())).thenReturn(groupDtoList);
        PowerMockito.mockStatic(PermissionService.class);

        PowerMockito.when(GroupPractitionerHelper.searchGroupById(groupDtoList.get(0).getGroupId())).thenReturn(root);
        PowerMockito.when(PermissionService.getResourceGroupsOfViewPatientList(Matchers.anyString())).thenReturn(Arrays.asList("Oncologist_头组") );

        PowerMockito.when(GroupPractitionerHelper.parallelTreeNode(Matchers.any())).thenReturn(Arrays.asList(root,root.getSubItems().get(0)));

        Response response = resource.getPatientAuthorityTable(userContext);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());

        PowerMockito.when(PermissionService.getResourceGroupsOfViewPatientList(Matchers.anyString())).thenReturn(new ArrayList<String>() );
        response = resource.getPatientAuthorityTable(userContext);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }

    private  GroupTreeNode buildGroupThreeNode(){
        GroupTreeNode test = new GroupTreeNode("1","Oncologist","Oncologist");
        GroupTreeNode sub1 = new GroupTreeNode("2","Oncologist_头组","Oncologist_头组");
        GroupTreeNode sub2 = new GroupTreeNode("3","Oncologist_胸组","Oncologist_胸组");
        GroupTreeNode sub11 = new GroupTreeNode("21","Oncologist_头组_头A组","Oncologist_头组_头A组");
        GroupTreeNode sub12 = new GroupTreeNode("22","Oncologist_头组_头B组","Oncologist_头组_头B组");
        GroupTreeNode sub21 = new GroupTreeNode("31","Oncologist_胸组_胸A组","Oncologist_胸组_胸A组");
        GroupTreeNode sub22 = new GroupTreeNode("32","Oncologist_胸组_胸B组","Oncologist_胸组_胸B组");

        GroupTreeNode sub3 = new GroupTreeNode("4","Oncologist_腹组","Oncologist_腹组");
        GroupTreeNode sub31 = new GroupTreeNode("41","Oncologist_腹组_腹A组","Oncologist_腹组_腹A组");
        GroupTreeNode sub32 = new GroupTreeNode("42","Oncologist_腹组_腹B组","Oncologist_腹组_腹B组");

        test.addAChildGroup(sub1);
        test.addAChildGroup(sub2);
        test.addAChildGroup(sub3);
        sub1.addAChildGroup(sub11);
        sub1.addAChildGroup(sub12);
        sub2.addAChildGroup(sub21);
        sub2.addAChildGroup(sub22);
        sub3.addAChildGroup(sub31);
        sub3.addAChildGroup(sub32);

         return test;
    }
}
