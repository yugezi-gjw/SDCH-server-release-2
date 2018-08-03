package com.varian.oiscn.carepath.resource;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.GroupAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.ValueSetAntiCorruptionServiceImp;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.practitioner.PractitionerTreeNode;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.tasklocking.TaskLockingDto;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.carepath.assembler.CarePathTemplateAssembler;
import com.varian.oiscn.carepath.service.CarePathConfigService;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.carepath.vo.ActivityEntryVO;
import com.varian.oiscn.carepath.vo.SequentialActivityVO;
import com.varian.oiscn.config.CarePathConfig;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.activity.SequentialActivityStatusEnum;
import com.varian.oiscn.core.carepath.*;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import io.dropwizard.setup.Environment;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by gbt1220 on 4/20/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CarePathResource.class, ValueSetAntiCorruptionServiceImp.class,
        SystemConfigPool.class, CarePathConfigService.class, GroupPractitionerHelper.class})
public class CarePathResourceTest {

    private Configuration configuration;

    private Environment environment;

    private CarePathTemplateAssembler assembler;

    private CarePathAntiCorruptionServiceImp antiCorruptionServiceImp;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private CarePathResource resource;

    private GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        PowerMockito.when(configuration.getCarePathConfig()).thenReturn(new CarePathConfig(){{
            setCarePath(Arrays.asList(new CarePathConfigItem(){{
                setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
                setTemplateId("BCRITemplate");
                setDescription("BCRIDemo");
                setTemplateName("BCRIDemo");
            }},new CarePathConfigItem(){{
                setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL);
                setTemplateId("BCRITemplate2");
                setDescription("BCRIDemo2");
                setTemplateName("BCRIDemo2");
            }}));
        }});
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryGroupRoleNurse()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupRoleOncologist()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupRolePhysicist()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");

        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn("DefaultTemplate");
        environment = PowerMockito.mock(Environment.class);
        assembler = PowerMockito.mock(CarePathTemplateAssembler.class);
        antiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(antiCorruptionServiceImp);
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(patientAntiCorruptionServiceImp);
        groupAntiCorruptionServiceImp = PowerMockito.mock(GroupAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(GroupAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(groupAntiCorruptionServiceImp);
        resource = new CarePathResource(configuration, environment);
    }

    @Test
    public void givenWhenSearchThenReturnResponseOK() throws Exception {
        String defaultTemplateName = "QinDemo";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        UserContext userContext = givenUserContext();
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");
        userContext.getLogin().setGroup("Therapist");
        PowerMockito.when(antiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        PowerMockito.whenNew(CarePathTemplateAssembler.class).withArguments(template).thenReturn(assembler);
        List<ActivityEntryVO> activityEntryList = givenActivityEntryList();
        PowerMockito.when(assembler.getActivityEntries(userContext.getLogin().getResourceSer().toString(), userContext.getLogin().getGroup(), userContext.getLogin().getStaffGroups())).thenReturn(activityEntryList);

        Response response = resource.searchActivityEntries(userContext);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void givenCarePathInstanceWhenQuerySequentialActivitiesThenReturnOk() throws Exception {
        String patientId = "testPatientId";
        String instanceId = "testInstanceId";
        String activityType = ActivityTypeEnum.TASK.name();
        String activityCode = "activityCode";
        CarePathInstance instance = new CarePathInstance();
        List<SequentialActivityVO> sequentialActivityVOList = givenSequentialActivities();
        PowerMockito.when(antiCorruptionServiceImp.queryLastCarePathByPatientIDAndActivityCode(patientId,activityCode)).thenReturn(instance);
        SequentialActivitiesHelper helper = PowerMockito.mock(SequentialActivitiesHelper.class);
        UserContext userContext = givenUserContext();
        PowerMockito.whenNew(SequentialActivitiesHelper.class).withArguments(instance, userContext.getLogin().getStaffGroups()).thenReturn(helper);
        PowerMockito.when(helper.querySequentialActivitiesByInstanceId(instanceId, activityType)).thenReturn(sequentialActivityVOList);
        Response response = resource.searchSequentialActivities(userContext, patientId, instanceId, activityCode, activityType);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenCarePathInstanceWhenQueryTreatmentAppointmentSequentialActivitiesThenReturnOk() throws Exception {
        String patientId = "1234567";
        String instanceId = "testInstanceId";
        String activityType = ActivityTypeEnum.TASK.name();
        String activityCode = "DoFirstTreatment";
        CarePathInstance instance = new CarePathInstance();
        PowerMockito.when(antiCorruptionServiceImp.queryLastCarePathByPatientIDAndActivityCode(patientId,activityCode)).thenReturn(instance);

        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL)).thenReturn(Arrays.asList("true"));
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE)).thenReturn(Arrays.asList("DoFirstTreatment"));
        TreatmentAppointmentService service = PowerMockito.mock(TreatmentAppointmentService.class);
        PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(service);
        CarePathInstanceHelper carePathInstanceHelper = PowerMockito.mock(CarePathInstanceHelper.class);
        PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(carePathInstanceHelper);
        ActivityInstance treatmentInstance = givenAnActivityInstance();
        PowerMockito.when(carePathInstanceHelper.getActivityByCode(activityCode)).thenReturn(treatmentInstance);
        TreatmentAppointmentDTO theFirstTreatmentAppointment = givenATreatmentAppointmentDTO();
        PowerMockito.when(service.queryTheFirstTreatmentAppointmentByPatientSer(Long.parseLong(patientId), activityCode)).thenReturn(theFirstTreatmentAppointment);

        List<SequentialActivityVO> sequentialActivityVOList = givenSequentialActivities();
        SequentialActivitiesHelper helper = PowerMockito.mock(SequentialActivitiesHelper.class);
        UserContext userContext = givenUserContext();
        PowerMockito.whenNew(SequentialActivitiesHelper.class).withArguments(instance, userContext.getLogin().getStaffGroups()).thenReturn(helper);
        PowerMockito.when(helper.querySequentialActivitiesByInstanceId(instanceId, activityType)).thenReturn(sequentialActivityVOList);
        Response response = resource.searchSequentialActivities(userContext, patientId, instanceId, activityCode, activityType);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenCarePathInstanceAndAppointmentStoredToLocalTrueWhenQuerySequentialActivitiesThenReturnOk() throws Exception {
        String patientId = "testPatientId";
        String instanceId = "testInstanceId";
        String activityType = ActivityTypeEnum.TASK.name();
        String activityCode = "activityCode";
        CarePathInstance instance = new CarePathInstance();
        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(false);
        List<SequentialActivityVO> sequentialActivityVOList = givenSequentialActivities();
        PowerMockito.when(antiCorruptionServiceImp.queryLastCarePathByPatientIDAndActivityCode(patientId,activityCode)).thenReturn(instance);
        SequentialActivitiesHelper helper = PowerMockito.mock(SequentialActivitiesHelper.class);
        UserContext userContext = givenUserContext();
        PowerMockito.whenNew(SequentialActivitiesHelper.class).withArguments(instance, userContext.getLogin().getStaffGroups()).thenReturn(helper);
        PowerMockito.when(helper.querySequentialActivitiesByInstanceId(instanceId, activityType)).thenReturn(sequentialActivityVOList);
        Response response = resource.searchSequentialActivities(userContext, patientId, instanceId, activityCode, activityType);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testListCarePathPrimary() throws Exception {
        CarePathResource resource = new CarePathResource(null, null);
        PowerMockito.mockStatic(CarePathConfigService.class);

        List<CarePathTemplateVO> carePathList = Arrays.asList(new CarePathTemplateVO(){{
            setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
            setDescription("TemplateDescription");
            setName("TemplateName");
            setId("TemplateId");
        }},new CarePathTemplateVO(){{
            setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL);
            setDescription("TemplateDescription2");
            setName("TemplateName2");
            setId("TemplateId2");
        }});
        PowerMockito.when(CarePathConfigService.getCarePathTemplateList()).thenReturn(carePathList);
        Response response = resource.listCarePathPrimary(null);
        Object actual = response.getEntity();
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertNotNull(actual);
    }

    @Test
    public void testListCarePathOptional() throws Exception {
        CarePathResource resource = new CarePathResource(null, null);
        PowerMockito.mockStatic(CarePathConfigService.class);

        List<CarePathTemplateVO> carePathList = Arrays.asList(new CarePathTemplateVO(){{
            setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
            setDescription("TemplateDescription");
            setName("TemplateName");
            setId("TemplateId");
        }},new CarePathTemplateVO(){{
            setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL);
            setDescription("TemplateDescription2");
            setName("TemplateName2");
            setId("TemplateId2");
        }});
        PowerMockito.when(CarePathConfigService.getCarePathTemplateList()).thenReturn(carePathList);
        Response response = resource.listCarePathOptional(null);
        Object actual = response.getEntity();
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertNotNull(actual);
    }
    @Test
    public void givenHisIdThenReturnNextSequentialAppointments() throws Exception{
        String patientSer = "112121";
        String hisId = "hisId";

        CarePathInstance carePathInstance = MockDtoUtil.givenACarePathInstance();
        PowerMockito.when(antiCorruptionServiceImp.queryAllCarePathByPatientID(patientSer)).thenReturn(Arrays.asList(carePathInstance));
        SequentialActivitiesHelper helper = PowerMockito.mock(SequentialActivitiesHelper.class);
        UserContext userContext = givenUserContext();
        userContext.getLogin().setStaffGroups(Arrays.asList("1"));
        PowerMockito.whenNew(SequentialActivitiesHelper.class).withArguments(carePathInstance, userContext.getLogin().getStaffGroups()).thenReturn(helper);
        SequentialActivityVO sequentialActivityVO = new SequentialActivityVO();
        sequentialActivityVO.setWorkspaceType("SCHEDULE_DAY");
        sequentialActivityVO.setStatus(SequentialActivityStatusEnum.ACTIVE);
        SequentialActivityVO sequentialActivityVOOther = new SequentialActivityVO();
        sequentialActivityVOOther.setWorkspaceType("ECLIPSE");
        PowerMockito.when(helper.assemblerSequentialActivityVO(Matchers.any(), Matchers.any())).thenReturn(sequentialActivityVO).thenReturn(sequentialActivityVOOther);

        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(new PatientDto(){{
            setHisId(hisId);
            setPatientSer(patientSer);
        }});

        HashMap<String, Object> result = new HashMap<>();
        result.put("patientSer", patientSer);
        List<SequentialActivityVO> activeScheduleSequentialActivityVOList = new ArrayList<>();
        activeScheduleSequentialActivityVOList.add(sequentialActivityVO);
        result.put("schedulingTasks", activeScheduleSequentialActivityVOList);
        Assert.assertNotNull(resource.getNextSequentialAppointmentsByPatientSer(userContext, patientSer).getEntity());
    }

    @Test
    public void testLockTaskWithLockingDtoIsNull() throws Exception {
        String activityType = "TASK";
        String instanceId = "instanceId";
        UserContext userContext = givenUserContext();
        TaskLockingServiceImpl taskLockingService = PowerMockito.mock(TaskLockingServiceImpl.class);
        PowerMockito.whenNew(TaskLockingServiceImpl.class).withArguments(userContext).thenReturn(taskLockingService);
//        TaskLockingDto taskLockingDto = givenTaskLockingDto("userone");
        TaskLockingDto queryDto = assembleTaskLockingQueryDto(activityType, instanceId);
        PowerMockito.when(taskLockingService.findLockTaskUserName(queryDto)).thenReturn(null);
        queryDto = assembleTaskLockingParams(queryDto, userContext);
        PowerMockito.when(taskLockingService.lockTask(Matchers.any())).thenReturn(true);
//        PowerMockito.when(taskLockingService.lockTask(queryDto)).thenReturn(true);
        Response response = resource.lockTask(userContext, activityType, instanceId);
        List<KeyValuePair> keyValuePairs = assembleKeyValuePairs(true, "");
        assertThat(response.getEntity(), is(keyValuePairs));
    }

    @Test
    public void testLockTaskWithLockingDtoIsNotNullAndUserNameEqualsLockUserName() throws Exception {
        String activityType = "TASK";
        String instanceId = "instanceId";
        UserContext userContext = givenUserContext();
        TaskLockingServiceImpl taskLockingService = PowerMockito.mock(TaskLockingServiceImpl.class);
        PowerMockito.whenNew(TaskLockingServiceImpl.class).withArguments(userContext).thenReturn(taskLockingService);
        TaskLockingDto taskLockingDto = givenTaskLockingDto("name");
        TaskLockingDto queryDto = assembleTaskLockingQueryDto(activityType, instanceId);
        PowerMockito.when(taskLockingService.findLockTaskUserName(queryDto)).thenReturn(taskLockingDto);
        queryDto = assembleTaskLockingParams(queryDto, userContext);
//        PowerMockito.when(taskLockingService.lockTask(queryDto)).thenReturn(true);
        PowerMockito.when(taskLockingService.lockTask(Matchers.any())).thenReturn(true);
        Response response = resource.lockTask(userContext, activityType, instanceId);
        List<KeyValuePair> keyValuePairs = assembleKeyValuePairs(true, "");
        assertThat(response.getEntity(), is(keyValuePairs));
    }

    @Test
    public void testLockTaskWithLockingDtoIsNotNullAndUserNameNotEqualsLockUserName() throws Exception {
        String activityType = "TASK";
        String instanceId = "instanceId";
        UserContext userContext = givenUserContext();
        TaskLockingServiceImpl taskLockingService = PowerMockito.mock(TaskLockingServiceImpl.class);
        PowerMockito.whenNew(TaskLockingServiceImpl.class).withArguments(userContext).thenReturn(taskLockingService);
        TaskLockingDto taskLockingDto = givenTaskLockingDto("notequalsname");
        TaskLockingDto queryDto = assembleTaskLockingQueryDto(activityType, instanceId);
        PowerMockito.when(taskLockingService.findLockTaskUserName(queryDto)).thenReturn(taskLockingDto);
        queryDto = assembleTaskLockingParams(queryDto, userContext);
        PowerMockito.when(taskLockingService.lockTask(Matchers.any())).thenReturn(false);
        Response response = resource.lockTask(userContext, activityType, instanceId);
        List<KeyValuePair> keyValuePairs = assembleKeyValuePairs(false, "");
        assertThat(response.getEntity(), is(keyValuePairs));
    }

    @Test
    public void testSearchOrganizations(){
        PowerMockito.when(groupAntiCorruptionServiceImp.queryGroupListByResourceID(Matchers.anyString())).thenReturn(Arrays.asList(new GroupDto(){{
            setGroupName("Nurse");
            setGroupId("121");
        }}));
        PowerMockito.mockStatic(GroupPractitionerHelper.class);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        Login login  = PowerMockito.mock(Login.class);
        PowerMockito.when(userContext.getLogin()).thenReturn(login);
        PowerMockito.when(login.getGroup()).thenReturn("Nurse");
        GroupTreeNode root = new GroupTreeNode("121","Oncology","Oncology");
        root.addAChildGroup(new GroupTreeNode("122","Oncology_Header","Oncology_Header"));
        root.addAPractitioner(new PractitionerTreeNode("1111","zhaoxin", ParticipantTypeEnum.PRACTITIONER));
        PowerMockito.when(GroupPractitionerHelper.getOncologyGroupTreeNode()).thenReturn(root);
        PowerMockito.when(GroupPractitionerHelper.getNurseGroupTreeNode()).thenReturn(root);
        PowerMockito.when(GroupPractitionerHelper.getPhysicistGroupTreeNode()).thenReturn(root);
        PowerMockito.when(GroupPractitionerHelper.getTechGroupTreeNode()).thenReturn(root);
        PowerMockito.when(GroupPractitionerHelper.copy(root)).thenReturn(root);
        Response response = resource.searchOrganizations(userContext);
        Assert.assertNotNull(response);
        PowerMockito.when(login.getGroup()).thenReturn("Oncologist");
        response = resource.searchOrganizations(userContext);
        Assert.assertNotNull(response);
        PowerMockito.when(login.getGroup()).thenReturn("Physicist");
        response = resource.searchOrganizations(userContext);
        Assert.assertNotNull(response);
        PowerMockito.when(login.getGroup()).thenReturn("Technician");
        response = resource.searchOrganizations(userContext);
        Assert.assertNotNull(response);

    }

    private TreatmentAppointmentDTO givenATreatmentAppointmentDTO() {
        TreatmentAppointmentDTO dto = new TreatmentAppointmentDTO();
        dto.setId("testInstanceId");
        return dto;
    }

    private List<ActivityEntryVO> givenActivityEntryList() {
        List<ActivityEntryVO> result = new ArrayList<>();
        result.add(new ActivityEntryVO("1", ActivityTypeEnum.APPOINTMENT.name(), "name1"));
        result.add(new ActivityEntryVO("2", ActivityTypeEnum.APPOINTMENT.name(), "name2"));
        return result;
    }

    private UserContext givenUserContext() {
        return new UserContext(MockDtoUtil.givenALogin(), MockDtoUtil.givenAnOspLogin());
    }

    private CarePathTemplate givenATemplate() {
        return MockDtoUtil.givenCarePathTemplate();
    }

    private List<SequentialActivityVO> givenSequentialActivities() {
        List<SequentialActivityVO> sequentialActivities = new ArrayList<>();
        SequentialActivityVO vo = new SequentialActivityVO();
        vo.setWorkspaceType("DYNAMIC_FORM");
        List<String> dynamicFormTemplateIds = new ArrayList<>();
        dynamicFormTemplateIds.add("DoImmob");
        vo.setDynamicFormTemplateIds(dynamicFormTemplateIds);
        vo.setType(ActivityTypeEnum.TASK);
        vo.setStatus(SequentialActivityStatusEnum.ACTIVE);
        vo.setInstanceId("testInstanceId");
        vo.setActivityCode("code");
        vo.setActivityId("1");
        vo.setDisplayName("name");
        sequentialActivities.add(vo);
        return sequentialActivities;
    }

    private ActivityInstance givenAnActivityInstance() {
        ActivityInstance instance = new ActivityInstance();
        return instance;
    }

    private TaskLockingDto givenTaskLockingDto(String lockUserName) {
        TaskLockingDto taskLockingDto = new TaskLockingDto("taskId", "TASK", lockUserName, 122l, "resourceone", new Date());
        return taskLockingDto;
    }

    private TaskLockingDto assembleTaskLockingQueryDto(String activityType, String instanceId) {
        TaskLockingDto taskLockingDto = new TaskLockingDto();
        taskLockingDto.setTaskId(instanceId);
        taskLockingDto.setActivityType(activityType);
        return taskLockingDto;
    }

    private TaskLockingDto assembleTaskLockingParams(TaskLockingDto taskLockingDto, UserContext userContext) {
        taskLockingDto.setLockUserName(userContext.getName());
        taskLockingDto.setResourceName(userContext.getLogin().getResourceName());
        taskLockingDto.setResourceSer(userContext.getLogin().getResourceSer());
        taskLockingDto.setLockTime(new Date());
        return taskLockingDto;
    }

    private List<KeyValuePair> assembleKeyValuePairs(boolean lockTaskAction, String lockResourceName) {
        List<KeyValuePair> keyValuePairs = new ArrayList<>();
        keyValuePairs.add(new KeyValuePair("lockTask", Boolean.toString(lockTaskAction)));
        keyValuePairs.add(new KeyValuePair("lockResourceName", lockResourceName));
        return keyValuePairs;
    }

}
