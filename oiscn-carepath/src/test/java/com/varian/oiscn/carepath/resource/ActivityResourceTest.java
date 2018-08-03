package com.varian.oiscn.carepath.resource;

import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.service.QueuingManagementServiceImpl;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.appointment.vo.QueuingManagementVO;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.util.DeviceUtil;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.carepath.assembler.ActivityInstanceAssembled;
import com.varian.oiscn.carepath.assembler.ActivityInstanceForAppointmentAssembler;
import com.varian.oiscn.carepath.assembler.ActivityInstanceForOrderAssembler;
import com.varian.oiscn.carepath.assembler.ActivityInstanceForPatientAssembler;
import com.varian.oiscn.carepath.service.ActivityServiceImp;
import com.varian.oiscn.carepath.task.EclipseTaskService;
import com.varian.oiscn.carepath.util.MockDatabaseConnection;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.carepath.vo.ActivityInstanceVO;
import com.varian.oiscn.carepath.vo.AppointmentFormDataVO;
import com.varian.oiscn.carepath.vo.AppointmentFormTimeDataVO;
import com.varian.oiscn.config.CarePathConfig;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.RankEnum;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.activity.ActivityCodeConstants;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentRankEnum;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.assign.AssignDeviceVO;
import com.varian.oiscn.core.assign.AssignResourceField;
import com.varian.oiscn.core.assign.AssignResourceVO;
import com.varian.oiscn.core.carepath.*;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderRankEnum;
import com.varian.oiscn.core.order.OrderStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.assign.AssignResourceFieldServiceImp;
import com.varian.oiscn.encounter.assign.AssignResourceServiceImp;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import io.dropwizard.setup.Environment;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;

import static com.varian.oiscn.carepath.util.MockDtoUtil.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;

/**
 * Created by gbt1220 on 4/26/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ActivityResource.class, StatusIconPool.class,
        SystemConfigPool.class, ActivityCodesReader.class, ConnectionPool.class,
        DeviceUtil.class, EclipseTaskService.class})
public class ActivityResourceTest {
    private Configuration configuration;

    private Environment environment;

    private OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private TaskLockingServiceImpl taskLockingService;

    private ActivityResource activityResource;

    private String code = "activityCode";

    private String active = "Active";

    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;

    private AppointmentFormDataVO vo = givenAppointmentFormData();

    private TreatmentAppointmentService treatmentAppointmentService;

    private ActivityServiceImp activityServiceImp;

    private DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp;

    private AssignResourceServiceImp assignResourceServiceImp;

    private EncounterServiceImp encounterServiceImp;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn("standardTPL"); PowerMockito.when(configuration.getCarePathConfig()).thenReturn(new CarePathConfig(){{
            setCarePath(Arrays.asList(new CarePathConfigItem(){{
                setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
                setTemplateId("BCRITemplate");
                setDescription("BCRIDemo");
                setTemplateName("BCRIDemo");
            }}));
        }});
        environment = PowerMockito.mock(Environment.class);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryGroupRoleNurse()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupRoleOncologist()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupRolePhysicist()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");

        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");


        orderAntiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(orderAntiCorruptionServiceImp);
        appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withNoArguments().thenReturn(appointmentAntiCorruptionServiceImp);
        carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        activityServiceImp = PowerMockito.mock(ActivityServiceImp.class);
        PowerMockito.whenNew(ActivityServiceImp.class).withAnyArguments().thenReturn(activityServiceImp);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(Matchers.anyLong())).thenReturn(new Encounter(){{
            setId("12345");
        }});
        dynamicFormInstanceServiceImp = PowerMockito.mock(DynamicFormInstanceServiceImp.class);
        PowerMockito.whenNew(DynamicFormInstanceServiceImp.class).withAnyArguments().thenReturn(dynamicFormInstanceServiceImp);

        activityResource = new ActivityResource(configuration, environment);
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(MockDtoUtil.givenAPractitionerGroupMap()));
        flagAntiCorruptionServiceImp = PowerMockito.mock(FlagAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(FlagAntiCorruptionServiceImp.class).withNoArguments().thenReturn(flagAntiCorruptionServiceImp);
        taskLockingService = PowerMockito.mock(TaskLockingServiceImpl.class);
        PowerMockito.whenNew(TaskLockingServiceImpl.class).withAnyArguments().thenReturn(taskLockingService);
        PowerMockito.mockStatic(StatusIconPool.class);
        PowerMockito.when(configuration.getActiveStatusIconDesc()).thenReturn(active);
        PowerMockito.mockStatic(ActivityCodesReader.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        Connection conn = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(conn);
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(MockDtoUtil.givenAPractitionerGroupMap()));
        assignResourceServiceImp = PowerMockito.mock(AssignResourceServiceImp.class);
        PowerMockito.whenNew(AssignResourceServiceImp.class).withAnyArguments().thenReturn(assignResourceServiceImp);
//        encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
//        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
    }

    @Test
    public void givenActivityCodeWhenQueryWithPatientTypeAndPatientListIsNullThenReturnEmptyList() throws Exception {
        UserContext userContext = givenAnUserContext();
        userContext.getLogin().setGroup("Other");
        Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.PATIENT.name(), null, null, "", "", "", "", "asc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(new ArrayList<ActivityInstanceVO>()));
    }

    @Test
    public void givenActivityCodeWhenQueryWithPatientTypeAndUserIsOncologistThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        List<PatientDto> patientDtoList = givenPatientDtoList();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientDtoListByPractitionerIdList(Arrays.asList(userContext.getLogin().getResourceSer().toString()))).thenReturn(patientDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForPatientAssembler assembler = PowerMockito.mock(ActivityInstanceForPatientAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForPatientAssembler.class).withArguments(patientDtoList, userContext.getLogin().getStaffGroups(), configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.PATIENT.name(), null, null, "", "", "", "", "asc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));
    }

    @Test
    public void givenActivityCodeWhenQueryWithPatientTypeAndUserIsNurseThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        userContext.getLogin().setGroup(SystemConfigPool.queryGroupRoleNurse());
        List<PatientDto> patientDtoList = givenPatientDtoList();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryAllActivePatients()).
                thenReturn(patientDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForPatientAssembler assembler = PowerMockito.mock(ActivityInstanceForPatientAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForPatientAssembler.class).withArguments(patientDtoList, userContext.getLogin().getStaffGroups(), configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.PATIENT.name(), null, null, "", "", "", "", "asc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));
    }

    @Test
    public void givenMyPatientGroupWhenQueryPaginationWithPatientTypeThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        List<PatientDto> patientDtoList = givenPatientDtoList();
        Pagination<PatientDto> patientDtoPagination = new Pagination<>();
        patientDtoPagination.setLstObject(patientDtoList);
        patientDtoPagination.setTotalCount(100);

        String activeCode = "activeCode";
        PowerMockito.when(StatusIconPool.get(active)).thenReturn(activeCode);

        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientDtoPaginationByPractitionerIdList(Arrays.asList(userContext.getLogin().getResourceSer().toString()), 10, 1,1, activeCode, null)).thenReturn(patientDtoPagination);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        Pagination<ActivityInstanceVO> result = new Pagination<>();
        result.setLstObject(instanceVOList);
        result.setTotalCount(100);
        ActivityInstanceForPatientAssembler assembler = PowerMockito.mock(ActivityInstanceForPatientAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForPatientAssembler.class).withArguments(patientDtoList, userContext.getLogin().getStaffGroups(), configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);

        Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.PATIENT.name(), null, null, "", "", "10", "1", "asc", false, ActivityCodeConstants.MY_PATIENTS);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(result));
    }

    @Test
    public void givenMyPatientGroupWhenQueryWithPatientTypeThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        List<PatientDto> patientDtoList = givenPatientDtoList();
        Pagination<PatientDto> patientDtoPagination = new Pagination<>();
        patientDtoPagination.setLstObject(patientDtoList);
        patientDtoPagination.setTotalCount(100);

        String activeCode = "activeCode";
        PowerMockito.when(StatusIconPool.get(active)).thenReturn(activeCode);

        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientDtoListByPractitionerIdList(Arrays.asList(userContext.getLogin().getResourceSer().toString()))).thenReturn(patientDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        Pagination<ActivityInstanceVO> result = new Pagination<>();
        result.setLstObject(instanceVOList);
        result.setTotalCount(100);
        ActivityInstanceForPatientAssembler assembler = PowerMockito.mock(ActivityInstanceForPatientAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForPatientAssembler.class).withArguments(patientDtoList, userContext.getLogin().getStaffGroups(), configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);

        Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.PATIENT.name(), null, null, "", "", "", "", "asc", false, ActivityCodeConstants.MY_PATIENTS);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void givenAllPatientGroupWhenQueryPaginationWithPatientTypeThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        List<PatientDto> patientDtoList = givenPatientDtoList();
        Pagination<PatientDto> patientDtoPagination = new Pagination<>();
        patientDtoPagination.setLstObject(patientDtoList);
        patientDtoPagination.setTotalCount(100);
        String activeCode = "activeCode";
        PowerMockito.when(StatusIconPool.get(active)).thenReturn(activeCode);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryAllPatientsWithPaging(10, 1, 1, activeCode, null)).
                thenReturn(patientDtoPagination);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        Pagination<ActivityInstanceVO> result = new Pagination<>();
        result.setLstObject(instanceVOList);
        result.setTotalCount(100);
        ActivityInstanceForPatientAssembler assembler = PowerMockito.mock(ActivityInstanceForPatientAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForPatientAssembler.class).withArguments(patientDtoList, userContext.getLogin().getStaffGroups(), configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.PATIENT.name(), null, null, "", "", "10", "1", "asc", false, ActivityCodeConstants.ALL_PATIENTS);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(result));
    }

    @Test
    public void givenAllPatientGroupWhenQueryWithPatientTypeThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        userContext.getLogin().setGroup(SystemConfigPool.queryGroupRoleNurse());
        List<PatientDto> patientDtoList = givenPatientDtoList();
        Pagination<PatientDto> patientDtoPagination = new Pagination<>();
        patientDtoPagination.setLstObject(patientDtoList);
        patientDtoPagination.setTotalCount(100);
        String activeCode = "activeCode";
        PowerMockito.when(StatusIconPool.get(active)).thenReturn(activeCode);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryAllActivePatients()).
                thenReturn(patientDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForPatientAssembler assembler = PowerMockito.mock(ActivityInstanceForPatientAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForPatientAssembler.class).withArguments(patientDtoList, userContext.getLogin().getStaffGroups(), configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.PATIENT.name(), null, null, "", "", "", "", "asc", false, ActivityCodeConstants.ALL_PATIENTS);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void givenActivityCodeWhenQueryAppointmentListByDeviceIdAndActivityNameAndDateRangeWithPaginationThenReturnResponseOK() {
        UserContext userContext = givenAnUserContext();
        List<AppointmentDto> appointmentDtoList = Arrays.asList(new AppointmentDto());
        Pagination<AppointmentDto> appointmentDtoPagination = new Pagination<>();
        appointmentDtoPagination.setLstObject(appointmentDtoList);
        appointmentDtoPagination.setTotalCount(100);
        String startDate = "2017-08-25";
        String endDate = "2017-08-25";
        String sort = "asc";
        String countPerPage = "10";
        String pageNumber = "1";
        String defaultTemplateName = "BCRIStandard";
        ActivityCodeConfig taskActivityCodeConfig = new ActivityCodeConfig();
        taskActivityCodeConfig.setName("testTaskCode");
        List<String> deviceIdList = Arrays.asList("23EX", "21EX 1900");
        try {
            PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
            CarePathTemplate template = new CarePathTemplate();
            template.setActivities(Arrays.asList(new PlannedActivity() {{
                setActivityCode(taskActivityCodeConfig.getName());
                setDeviceIDs(deviceIdList);
            }}));
            PowerMockito.when(this.carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
            PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode(code)).thenReturn(taskActivityCodeConfig);
            PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRangeWithPagination(
                    deviceIdList, code, startDate, endDate,
                    Arrays.asList(new ImmutablePair<>(AppointmentRankEnum.START_TIME, RankEnum.fromCode(sort))),
                    Integer.parseInt(countPerPage), Integer.parseInt(pageNumber), Integer.parseInt(pageNumber)))
                    .thenReturn(appointmentDtoPagination);

            List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
            Pagination<ActivityInstanceVO> result = new Pagination<>();
            result.setLstObject(instanceVOList);
            result.setTotalCount(0);
            ActivityInstanceAssembled assembler = PowerMockito.mock(ActivityInstanceForAppointmentAssembler.class);

            PowerMockito.whenNew(ActivityInstanceForAppointmentAssembler.class).withArguments(appointmentDtoList, configuration, userContext).thenReturn((ActivityInstanceForAppointmentAssembler) assembler);
            PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);

            QueuingManagementServiceImpl queuingManagementService = PowerMockito.mock(QueuingManagementServiceImpl.class);
            PowerMockito.whenNew(QueuingManagementServiceImpl.class).withAnyArguments().thenReturn(queuingManagementService);
            PowerMockito.when(queuingManagementService.queryCheckInList(Matchers.any())).thenReturn(new ArrayList<QueuingManagementVO>());

            Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.APPOINTMENT.name(), startDate, endDate, "", "", countPerPage, pageNumber, sort, false, "");
            Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
            assertThat(response.getEntity(), equalTo(result));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenActivityCodeWhenQueryWithTaskTypeAndHisIdThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        userContext.getLogin().setResourceSer(11L);
        List<OrderDto> orderDtoList = givenAnOrderList();
        String hisId = "hisId";
        String activityCode = "deviceCode";
        String defaultTemplateName = "defaultName";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(patientDto);
        Pagination<OrderDto> pagination = new Pagination<>();
        pagination.setLstObject(orderDtoList);
        pagination.setTotalCount(orderDtoList.size());
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPatientIdAndActivityCodeAndGroupIdsAndPractitionerIdsWithPaging(patientDto.getPatientSer(), activityCode, null,null,Integer.MAX_VALUE, 1,Integer.MAX_VALUE, null)).thenReturn(pagination);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForOrderAssembler assembler = PowerMockito.mock(ActivityInstanceForOrderAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForOrderAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1, Integer.MAX_VALUE)).thenReturn(new Pagination<>());
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientPinyinAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1, Integer.MAX_VALUE)).thenReturn(new Pagination<>());
        PowerMockito.when(SystemConfigPool.queryViewAllPatientsForPhysicistFromConfig()).thenReturn(false);
        Response response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.TASK.name(), null, null, hisId, "", "", "", null, false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));
    }

    @Test
    public void givenActivityCodeWhenQueryWithTaskTypeAndHisIdAndFhirNotFoundPatientThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        userContext.getLogin().setResourceSer(11L);
        String hisId = "hisId";
        String activityCode = "deviceCode";
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(null);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForOrderAssembler assembler = PowerMockito.mock(ActivityInstanceForOrderAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForOrderAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Pagination<PatientDto> pagination = new Pagination<>();
        pagination.setLstObject(new ArrayList<>());
        pagination.setTotalCount(0);
        String defaultTemplateName = "defaultName";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1, Integer.MAX_VALUE)).thenReturn(pagination);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientPinyinAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1, Integer.MAX_VALUE)).thenReturn(pagination);
        PowerMockito.when(SystemConfigPool.queryViewAllPatientsForPhysicistFromConfig()).thenReturn(false);
        Response response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.TASK.name(), null, null, hisId, "", "", "", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void givenActivityCodeWhenQueryPaginationWithTaskTypeAndHisIdThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        userContext.getLogin().setResourceSer(11L);
        List<OrderDto> orderDtoList = new ArrayList<>();
        String hisId = "hisId";
        String activityCode = "noDeviceCode";
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        Pagination<OrderDto> orderDtoPagination = new Pagination<>();
        orderDtoPagination.setLstObject(orderDtoList);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(patientDto);
        String defaultTemplateName = "defaultName";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(hisId, null, Arrays.asList("11"), Integer.MAX_VALUE, 1,Integer.MAX_VALUE)).thenReturn(new Pagination<>());
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientPinyinAndPractitionerIds(hisId, null, Arrays.asList("11"), Integer.MAX_VALUE, 1,Integer.MAX_VALUE)).thenReturn(new Pagination<>());
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1,Integer.MAX_VALUE)).thenReturn(new Pagination<>());
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientPinyinAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1,Integer.MAX_VALUE)).thenReturn(new Pagination<>());
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPatientIdAndActivityCodeAndGroupIdsAndPractitionerIdsWithPaging(
                patientDto.getPatientSer(), code,null,null, Integer.MAX_VALUE, 1, Integer.MAX_VALUE,null)).
                thenReturn(orderDtoPagination);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        Pagination<ActivityInstanceVO> result = new Pagination<>();
        result.setLstObject(instanceVOList);
        ActivityInstanceForOrderAssembler assembler = PowerMockito.mock(ActivityInstanceForOrderAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForOrderAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        PowerMockito.when(SystemConfigPool.queryViewAllPatientsForPhysicistFromConfig()).thenReturn(false);
        Response response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.TASK.name(), null, null, hisId, "", "10", "1", null, false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(result.getLstObject()));
    }

    @Test
    public void givenActivityCodeWhenQueryWithTaskTypeThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        List<OrderDto> orderDtoList = givenAnOrderList();
        List<ImmutablePair<OrderRankEnum, RankEnum>> lstRank = new ArrayList<>();
        lstRank.add(new ImmutablePair<>(OrderRankEnum.TASK_CREATION_DATE, RankEnum.DESC));
        String defaultTemplateName = "defaultName";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByPractitionerIdAndActivityName(new ArrayList<>(), code, lstRank, null)).thenReturn(orderDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForOrderAssembler assembler = PowerMockito.mock(ActivityInstanceForOrderAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForOrderAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        PowerMockito.when(SystemConfigPool.queryViewAllPatientsForPhysicistFromConfig()).thenReturn(false);
        Response response = activityResource.searchActiveActivities(userContext, code, ActivityTypeEnum.TASK.name(), null, null, "", "", "", "", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));
    }

    @Test
    public void givenActivityCodeWhenQueryPaginationWithTaskTypeThenReturnResponseOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        userContext.getLogin().setResourceSer(11L);
        List<OrderDto> orderDtoList = givenAnOrderList();
        Pagination<OrderDto> orderDtoPagination = new Pagination<>();
        orderDtoPagination.setLstObject(orderDtoList);
        List<ImmutablePair<OrderRankEnum, RankEnum>> lstRank = new ArrayList<>();
        lstRank.add(new ImmutablePair<>(OrderRankEnum.TASK_CREATION_DATE, RankEnum.DESC));
        String defaultTemplateName = "defaultName";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        String activityCode = "deviceCode";
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderListByGroupIDAndActivityNameWithPaging(
                null,
                activityCode,
                lstRank,
                10,
                1,
                1,
                null)).thenReturn(orderDtoPagination);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        Pagination<ActivityInstanceVO> result = new Pagination<>();
        result.setLstObject(instanceVOList);
        ActivityInstanceForOrderAssembler assembler = PowerMockito.mock(ActivityInstanceForOrderAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForOrderAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        PowerMockito.when(SystemConfigPool.queryViewAllPatientsForPhysicistFromConfig()).thenReturn(false);

        AssignResourceFieldServiceImp assignResourceFieldServiceImp = PowerMockito.mock(AssignResourceFieldServiceImp.class);
        PowerMockito.whenNew(AssignResourceFieldServiceImp.class).withAnyArguments().thenReturn(assignResourceFieldServiceImp);
        PowerMockito.when(assignResourceFieldServiceImp.queryAssignResourceFieldByCategory(Matchers.anyString())).thenReturn(Arrays.asList(new AssignResourceField(){{
            setName("field1");
            setValue("field1");
        }},new AssignResourceField(){{
            setName("field2");
            setValue("field2");
        }},new AssignResourceField(){{
            setName("field3");
            setValue("field3");
        }}));
        PowerMockito.when(assignResourceFieldServiceImp.queryAssignResourceFieldValue("DynamicFormFieldValue")).thenReturn(Arrays.asList(new AssignResourceField(){{
            setName("field1");
            setValue("v1");
        }},new AssignResourceField(){{
            setName("v");
            setValue("v2");
        }},new AssignResourceField(){{
            setName("field3");
            setValue("v3");
        }}));

        Map<String,Map<String,String>> map = new HashMap<>();
        map.put("1231",new HashMap<String,String>(){{
            put("field1","true");
            put("field2","true");
            put("field3","");
        }});
        PowerMockito.when(dynamicFormInstanceServiceImp.queryFieldsValueByPatientSerListAndFieldNames(Matchers.anyList(),Matchers.anyList())).thenReturn(map);

        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        PowerMockito.when(ActivityCodesReader.getActivityCode(activityCode)).thenReturn(activityCodeConfig);

        Response response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.TASK.name(), null, null, "", "", "10", "1", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(result));
    }

    @Test
    public void givenActivityCodeWhenQueryWithAppointmentTypeAndNoDevicesThenReturnNotFound() throws Exception {
        UserContext userContext = givenAnUserContext();
        String defaultTemplateName = "defaultName";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode("noDeviceCode")).thenReturn(activityCodeConfig);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        Response response = activityResource.searchActiveActivities(userContext, "noDeviceCode", ActivityTypeEnum.APPOINTMENT.name(), null, null, "", "", "", "", null, false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(new ArrayList<ActivityInstanceVO>()));
    }

    @Test
    public void givenActivityCodeWhenQueryWithAppointmentTypeAndHisIdAndNotFoundPatientThenReturnNotFound() throws Exception {
        UserContext userContext = givenAnUserContext();
        String hisId = "hisId";
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(null);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1,Integer.MAX_VALUE)).thenReturn(new Pagination<PatientDto>());
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientPinyinAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1,Integer.MAX_VALUE)).thenReturn(new Pagination<PatientDto>());
        Response response = activityResource.searchActiveActivities(userContext, "code", ActivityTypeEnum.APPOINTMENT.name(), null, null, hisId, "", "", "", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(new ArrayList<ActivityInstanceVO>()));
    }

    @Test
    public void givenActivityCodeWhenQueryWithAppointmentTypeAndHisIdThenReturnOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        String hisId = "hisId";
        PatientDto patientDto = givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(patientDto);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1,Integer.MAX_VALUE)).thenReturn(new Pagination<PatientDto>());
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientPinyinAndPractitionerIds(hisId, null, null, Integer.MAX_VALUE, 1,Integer.MAX_VALUE)).thenReturn(new Pagination<PatientDto>());
        List<AppointmentDto> appointmentDtoList = givenAnAppointmentList();
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCode(patientDto.getPatientSer(), "code")).thenReturn(appointmentDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForAppointmentAssembler assembler = PowerMockito.mock(ActivityInstanceForAppointmentAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForAppointmentAssembler.class).withArguments(appointmentDtoList, configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Response response = activityResource.searchActiveActivities(userContext, "code", ActivityTypeEnum.APPOINTMENT.name(), null, null, hisId, "", "", "", null, false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));
    }

    @Test
    public void givenActivityCodeWhenQueryWithAppointmentTypeAndStartDateAndEndDateAreNullThenReturnOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        List<AppointmentDto> appointmentDtoList = givenAnAppointmentList();
        String defaultTemplateName = "defaultName";
        String activityCode = "deviceCode";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        ActivityCodeConfig taskActivityCodeConfig = new ActivityCodeConfig();
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode(activityCode)).thenReturn(taskActivityCodeConfig);
        String curDate = DateUtil.getCurrentDate();
        List<ImmutablePair<AppointmentRankEnum, RankEnum>> lstRank = new ArrayList<>();
        lstRank.add(new ImmutablePair<>(AppointmentRankEnum.START_TIME, RankEnum.DESC));
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(Arrays.asList("deviceId1"), activityCode, curDate, curDate, lstRank)).thenReturn(appointmentDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForAppointmentAssembler assembler = PowerMockito.mock(ActivityInstanceForAppointmentAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForAppointmentAssembler.class).withArguments(appointmentDtoList, configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Response response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.APPOINTMENT.name(), null, null, "", "", "", "", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));

        response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.APPOINTMENT.name(), curDate, curDate, "", "", "", "", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));
    }

    @Test
    public void givenActivityCodeWhenQueryWithAppointmentTypeAndStartDateAndEndDateAreNotNullThenReturnOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        List<AppointmentDto> appointmentDtoList = givenAnAppointmentList();
        String defaultTemplateName = "defaultName";
        String activityCode = "deviceCode";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        ActivityCodeConfig taskActivityCodeConfig = new ActivityCodeConfig();
        taskActivityCodeConfig.setName(activityCode);
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode(activityCode)).thenReturn(taskActivityCodeConfig);
        List<String> deviceIds = Arrays.asList("deviceId1");
        PowerMockito.mockStatic(DeviceUtil.class);
        PowerMockito.when(DeviceUtil.getDevicesByActivityCode(anyString(), anyString())).thenReturn(deviceIds);
        String curDate = DateUtil.getCurrentDate();
        List<ImmutablePair<AppointmentRankEnum, RankEnum>> lstRank = new ArrayList<>();
        lstRank.add(new ImmutablePair<>(AppointmentRankEnum.START_TIME, RankEnum.DESC));
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(deviceIds, activityCode, curDate, curDate, lstRank)).thenReturn(appointmentDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForAppointmentAssembler assembler = PowerMockito.mock(ActivityInstanceForAppointmentAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForAppointmentAssembler.class).withArguments(appointmentDtoList, configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Response response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.APPOINTMENT.name(), curDate, curDate, "", "", "", "", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));

        response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.APPOINTMENT.name(), curDate, curDate, "", "", "", "", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));
    }

    @Test
    public void testActivityCodeWhenQueryWithAppointmentTypeAndStartDateAndEndDateAndDeviceIdThenReturnOk() throws Exception {
        UserContext userContext = givenAnUserContext();
        String deviceId = "1101";
        List<AppointmentDto> appointmentDtoList = givenAnAppointmentList();
        String defaultTemplateName = "defaultName";
        String secondTemplateId = "secondTemplateId";
        String activityCode = "DoRepositioning";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        CarePathTemplate template = givenATemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).thenReturn(template);
        CarePathConfig carePathConfig = new CarePathConfig(){{
            setCarePath(Arrays.asList(new CarePathConfigItem(){{
                setTemplateId("secondTemplateId");
            }}));
        }};
        PowerMockito.when(configuration.getCarePathConfig()).thenReturn(carePathConfig);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(secondTemplateId)).thenReturn(new CarePathTemplate(){{
            setActivities(Arrays.asList(new PlannedActivity(){{
                setActivityType(ActivityTypeEnum.TASK);
                setDeviceIDs(Arrays.asList(deviceId));
                setActivityCode("ScheduleRepositioning");
            }}));
        }});
        PowerMockito.when(ActivityCodesReader.getActivityCode("ScheduleRepositioning")).thenReturn(new ActivityCodeConfig(){{
            setRelativeCode("DoRepositioning");
        }});


        ActivityCodeConfig taskActivityCodeConfig = new ActivityCodeConfig();
        taskActivityCodeConfig.setName("ScheduleRepositioning");
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode(activityCode)).thenReturn(taskActivityCodeConfig);
        List<String> deviceIds = Arrays.asList(deviceId);
        PowerMockito.mockStatic(DeviceUtil.class);
        PowerMockito.when(DeviceUtil.getDevicesByActivityCode(anyString(), anyString())).thenReturn(deviceIds);
        String curDate = DateUtil.getCurrentDate();
        List<ImmutablePair<AppointmentRankEnum, RankEnum>> lstRank = new ArrayList<>();
        lstRank.add(new ImmutablePair<>(AppointmentRankEnum.START_TIME, RankEnum.DESC));
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(deviceIds, activityCode, curDate, curDate, lstRank)).thenReturn(appointmentDtoList);
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        ActivityInstanceForAppointmentAssembler assembler = PowerMockito.mock(ActivityInstanceForAppointmentAssembler.class);
        PowerMockito.whenNew(ActivityInstanceForAppointmentAssembler.class).withArguments(appointmentDtoList, configuration, userContext).thenReturn(assembler);
        PowerMockito.when(assembler.getActivityInstances()).thenReturn(instanceVOList);
        Response response = activityResource.searchActiveActivities(userContext, activityCode, ActivityTypeEnum.APPOINTMENT.name(), curDate, curDate, "", deviceId, "", "", "desc", false, "");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        assertThat(response.getEntity(), equalTo(instanceVOList));

    }
    @Test
    public void givenWhenDoneActivityAndInvalidActivityTypeThenReturnBadRequest() {
        CarePathInstance carePathInstance = givenACarePathInstance();
        vo.setActivityType("1");
        vo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(vo.getPatientSer().toString(),carePathInstance.getId(),ActivityTypeEnum.APPOINTMENT)).thenReturn(carePathInstance);
        CarePathInstanceHelper helper = PowerMockito.mock(CarePathInstanceHelper.class);
        try {
            PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(helper);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        ActivityInstance theDoneActivity = new ActivityInstance() {{
            setActivityCode("DoCT");
            setActivityType(ActivityTypeEnum.APPOINTMENT);
            setIsActiveInWorkflow(true);
        }};
        PowerMockito.when(helper.getActivityByInstanceIdAndActivityType(carePathInstance.getId(), vo.getActivityType())).thenReturn(theDoneActivity);

        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        PowerMockito.when(ActivityCodesReader.getActivityCode(theDoneActivity.getActivityCode())).thenReturn(activityCodeConfig);

        List<String> treatmentActivityCodeList = Arrays.asList("DoFirstTreatment");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE)).thenReturn(treatmentActivityCodeList);
        List<String> appointmentStoredToLocalList = Arrays.asList("false");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL)).thenReturn(appointmentStoredToLocalList);
        Response response = activityResource.setActivityDone(givenUserContext(), "1", vo);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void testDonePendingActivity() {
        CarePathInstance carePathInstance = givenACarePathInstance();
        vo.setActivityType("1");
        vo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(vo.getPatientSer().toString(), carePathInstance.getId(), ActivityTypeEnum.APPOINTMENT)).thenReturn(carePathInstance);
        CarePathInstanceHelper helper = PowerMockito.mock(CarePathInstanceHelper.class);
        try {
            PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(helper);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        ActivityInstance theDoneActivity = new ActivityInstance() {{
            setActivityCode("DoCT");
            setActivityType(ActivityTypeEnum.APPOINTMENT);
            setIsActiveInWorkflow(false);
        }};
        PowerMockito.when(helper.getActivityByInstanceIdAndActivityType(carePathInstance.getId(), vo.getActivityType())).thenReturn(theDoneActivity);

        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        PowerMockito.when(ActivityCodesReader.getActivityCode(anyString())).thenReturn(activityCodeConfig);

        Response response = activityResource.setActivityDone(givenUserContext(), "1", vo);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void givenWhenDoneActivityAndNotAppointmentTypeThenReturnInternalServerError() {
        CarePathInstance carePathInstance = givenACarePathInstance();
        vo.setActivityType("1");
        vo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(vo.getPatientSer().toString(),carePathInstance.getId(),ActivityTypeEnum.APPOINTMENT)).thenReturn(carePathInstance);
        CarePathInstanceHelper helper = PowerMockito.mock(CarePathInstanceHelper.class);
        try {
            PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(helper);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        ActivityInstance theDoneActivity = new ActivityInstance() {{
            setActivityCode("DoCT");
        }};
        PowerMockito.when(helper.getActivityByInstanceIdAndActivityType(carePathInstance.getId(), vo.getActivityType())).thenReturn(theDoneActivity);
        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        PowerMockito.when(ActivityCodesReader.getActivityCode(theDoneActivity.getActivityCode())).thenReturn(activityCodeConfig);
        activityCodeConfig.setRelativeCode(ActivityTypeEnum.TASK.name());
        ActivityInstance relativeActivityInstance = new ActivityInstance();
        relativeActivityInstance.setActivityType(ActivityTypeEnum.TASK);
        PowerMockito.when(helper.getActivityByCode(Matchers.any())).thenReturn(relativeActivityInstance);
        Response response = activityResource.setActivityDone(new UserContext(), "1", vo);
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
    }

    @Test
    public void testCheckHasPreActiveActivity() {
        CarePathInstance carePathInstance = givenACarePathInstance();
        vo.setActivityType("1");
        vo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(vo.getPatientSer().toString(), carePathInstance.getId(), ActivityTypeEnum.APPOINTMENT)).thenReturn(carePathInstance);
        CarePathInstanceHelper helper = PowerMockito.mock(CarePathInstanceHelper.class);
        try {
            PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(helper);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        ActivityInstance theDoneActivity = new ActivityInstance() {{
            setActivityCode("DoCT");
            setIsActiveInWorkflow(false);
        }};
        ActivityInstance preActiveActivity = new ActivityInstance() {{
            setActivityCode("DoImmobilization");
        }};
        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        PowerMockito.when(ActivityCodesReader.getActivityCode(anyString())).thenReturn(activityCodeConfig);
        PowerMockito.when(helper.getActivityByInstanceIdAndActivityType(carePathInstance.getId(), vo.getActivityType())).thenReturn(theDoneActivity);
        PowerMockito.when(helper.getPreActiveInWorkflowActivity(theDoneActivity)).thenReturn(preActiveActivity);

        Response response = activityResource.preCheck(new UserContext(), "1", vo);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void givenWhenDoneActivityAndActivityTypeIsAppointmentThenReturnOK() {
        CarePathInstance carePathInstance = givenACarePathInstance();
        vo.setActivityType("1");
        vo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(vo.getPatientSer().toString(),carePathInstance.getId(),ActivityTypeEnum.APPOINTMENT)).thenReturn(carePathInstance);
        CarePathInstanceHelper helper = PowerMockito.mock(CarePathInstanceHelper.class);
        try {
            PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(helper);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        ActivityInstance theDoneActivity = new ActivityInstance() {{
            setActivityCode("DoCT");
            setActivityType(ActivityTypeEnum.APPOINTMENT);
            setIsActiveInWorkflow(true);
        }};
        PowerMockito.when(helper.getActivityByInstanceIdAndActivityType(carePathInstance.getId(), vo.getActivityType())).thenReturn(theDoneActivity);
        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        PowerMockito.when(ActivityCodesReader.getActivityCode(theDoneActivity.getActivityCode())).thenReturn(activityCodeConfig);
        activityCodeConfig.setRelativeCode(ActivityTypeEnum.APPOINTMENT.name());
        ActivityInstance relativeActivityInstance = new ActivityInstance();
        relativeActivityInstance.setActivityType(ActivityTypeEnum.APPOINTMENT);
        PowerMockito.when(helper.getActivityByCode(Matchers.any())).thenReturn(relativeActivityInstance);
        relativeActivityInstance.setActivityCode("ActivityCode1");
        PowerMockito.when(SystemConfigPool.queryTreatmentActivityCode()).thenReturn("ActivityCode1");
        List<AppointmentFormTimeDataVO> appointmentFormTimeDataVOList = new ArrayList<>();
        AppointmentFormTimeDataVO vo1 = new AppointmentFormTimeDataVO();
        appointmentFormTimeDataVOList.add(vo1);
        PowerMockito.when(activityServiceImp.checkMultiAppointmentsConflict(Matchers.any())).thenReturn(appointmentFormTimeDataVOList);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        PowerMockito.when(userContext.getName()).thenReturn("sysAdmin");
        Response response = activityResource.setActivityDone(userContext, "1", vo);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

//    @Test
//    public void givenWhenDoneActivityActivityTypeIsTaskThenReturnNothing() {
//        CarePathInstance carePathInstance = givenACarePathInstance();
//        vo.setActivityType(ActivityTypeEnum.TASK.name());
//        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(vo.getPatientSer())).thenReturn(carePathInstance);
//        CarePathInstanceHelper helper = PowerMockito.mock(CarePathInstanceHelper.class);
//        try {
//            PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(helper);
//        } catch (Exception e) {
//            Assert.fail(e.getMessage());
//        }
//        ActivityInstance theDoneActivity = new ActivityInstance() {{
//            setActivityCode("DoCT");
//            setStatus(CarePathStatusEnum.ACTIVE);
//        }};
//        PowerMockito.when(helper.getActivityByInstanceIdAndActivityType(carePathInstance.getId(), vo.getActivityType())).thenReturn(theDoneActivity);
//        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
//        PowerMockito.when(ActivityCodesReader.getActivityCode(theDoneActivity.getActivityCode())).thenReturn(activityCodeConfig);
//
////        activityCodeConfig.setRelativeCode(ActivityTypeEnum.TASK.name());
////        ActivityInstance relativeActivityInstance = new ActivityInstance();
////        relativeActivityInstance.setActivityType(ActivityTypeEnum.TASK);
////        relativeActivityInstance.setActivityCode("ActivityCode1");
////        PowerMockito.when(helper.getActivityByCode(Matchers.any())).thenReturn(relativeActivityInstance);
////        vo.setActivityType(ActivityTypeEnum.TASK.name());
////        theDoneActivity.setStatus(CarePathStatusEnum.CANCELLED);
//        Response response = activityResource.setActivityDone(new UserContext(), "1", vo);
//        Assert.assertTrue(true);
//    }

    @Test
    public void givenWhenDoneActivityAndAppointmentTypeThenReturnOk() {
        CarePathInstance carePathInstance = givenACarePathInstance();
        carePathInstance.getActivityInstances().get(0).setActivityType(ActivityTypeEnum.APPOINTMENT);
        carePathInstance.getActivityInstances().get(1).setActivityType(ActivityTypeEnum.APPOINTMENT);
        vo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(vo.getPatientSer().toString(),carePathInstance.getId(),ActivityTypeEnum.APPOINTMENT)).thenReturn(carePathInstance);
        List<String> treatmentActivityCodeList = Arrays.asList("DoFirstTreatment");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE)).thenReturn(treatmentActivityCodeList);
        List<String> appointmentStoredToLocalList = Arrays.asList("false");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL)).thenReturn(appointmentStoredToLocalList);
        CarePathInstanceHelper helper = PowerMockito.mock(CarePathInstanceHelper.class);
        try {
            PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(helper);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        ActivityInstance theDoneActivity = new ActivityInstance() {{
            setActivityCode("DoCT");
            setActivityType(ActivityTypeEnum.TASK);
            setIsActiveInWorkflow(true);
        }};
        PowerMockito.when(helper.getActivityByInstanceIdAndActivityType(carePathInstance.getId(), vo.getActivityType())).thenReturn(theDoneActivity);

        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        activityCodeConfig.setReleaseResourceForActivity("actCode01,actCode02");
        PowerMockito.when(ActivityCodesReader.getActivityCode(theDoneActivity.getActivityCode())).thenReturn(activityCodeConfig);

        PowerMockito.when(assignResourceServiceImp.deleteAssignedResource(anyLong(), anyString())).thenReturn(true);

        PowerMockito.when(taskLockingService.unLockTask(Matchers.anyObject())).thenReturn(true);
        Response response = activityResource.setActivityDone(givenAnUserContext(), "1", vo);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void givenWhenNurseAppointmentThenSave2LocalDbThenReturnOk() {
        String hisId = "hisId";
        CarePathInstance carePathInstance = givenACarePathInstance();
        carePathInstance.getActivityInstances().get(0).setActivityType(ActivityTypeEnum.APPOINTMENT);
        carePathInstance.getActivityInstances().get(1).setActivityType(ActivityTypeEnum.APPOINTMENT);
        vo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(vo.getPatientSer().toString(),carePathInstance.getId(),ActivityTypeEnum.APPOINTMENT)).thenReturn(carePathInstance);
        List<String> treatmentActivityCodeList = Arrays.asList("DoFirstTreatment");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE)).thenReturn(treatmentActivityCodeList);
        List<String> appointmentStoredToLocalList = Arrays.asList("true");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL)).thenReturn(appointmentStoredToLocalList);
        TreatmentAppointmentService treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
        try {
            PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(treatmentAppointmentService);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        PatientDto patientDto = new PatientDto();
        patientDto.setHisId(hisId);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(anyString())).thenReturn(patientDto);
        PowerMockito.when(treatmentAppointmentService.create(Matchers.any(TreatmentAppointmentDTO.class))).thenReturn("1191");
        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig() {{
            setRelativeCode("DoFirstTreatment");
            setReleaseResourceForActivity("activity01, activity02");
        }};
        PowerMockito.when(ActivityCodesReader.getActivityCode("ScheduleFirstTreatment")).thenReturn(activityCodeConfig);

        try {
            CarePathInstanceHelper helper = PowerMockito.mock(CarePathInstanceHelper.class);
            PowerMockito.whenNew(CarePathInstanceHelper.class).withAnyArguments().thenReturn(helper);
            ActivityInstance theDoneActivity = new ActivityInstance() {{
                setActivityCode("ScheduleFirstTreatment");
                setActivityType(ActivityTypeEnum.APPOINTMENT);
            }};
            PowerMockito.when(helper.getActivityByInstanceIdAndActivityType(carePathInstance.getId(), vo.getActivityType())).thenReturn(theDoneActivity);

            ActivityInstance relativeActivityInstance = new ActivityInstance() {{
                setActivityType(ActivityTypeEnum.APPOINTMENT);
                setActivityCode("DoFirstTreatment");
            }};
            PowerMockito.when(helper.getActivityByCode(activityCodeConfig.getRelativeCode())).thenReturn(relativeActivityInstance);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        PowerMockito.when(assignResourceServiceImp.deleteAssignedResource(anyLong(), anyString())).thenReturn(true);

        PowerMockito.when(taskLockingService.unLockTask(Matchers.anyObject())).thenReturn(true);
        Response response = activityResource.setActivityDone(givenAnUserContext(), "1", vo);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

    }

    @Test
    public void givenWhenSearchActivityStatusByIdAndActivityTypeThenReturnActivityStatus() {
        String id = "123";
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderStatus(OrderStatusEnum.COMPLETED.name());
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderById(id)).thenReturn(orderDto);
        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setStatus(AppointmentStatusEnum.FULFILLED.name());
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentById(id)).thenReturn(appointmentDto);
        Response response = activityResource.searchActivityStatus(givenAnUserContext(), id, "APPOINTMENT");
        Assert.assertTrue(((Map<String, String>) response.getEntity()).get("activityStatus").equals("done"));
    }

    @Test
    public void givenAddAppointmentWhenModifyMultipleTreatmentAppointmentThenReturnBoolean() {
        AppointmentFormDataVO vo = givenAppointmentFormData();
        CarePathInstance carePathInstance = givenACarePathInstance();
        PowerMockito.when(activityServiceImp.checkMultiAppointmentsConflict(vo)).thenReturn(new ArrayList<>());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(vo.getPatientSer().toString())).thenReturn(carePathInstance);
        PowerMockito.when(appointmentAntiCorruptionServiceImp.createAppointment(new AppointmentDto())).thenReturn("");
        List<String> treatmentActivityCodeList = Arrays.asList("DoFirstTreatment");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE)).thenReturn(treatmentActivityCodeList);
        List<String> appointmentStoredToLocalList = Arrays.asList("false");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL)).thenReturn(appointmentStoredToLocalList);
        Response response = activityResource.modifyMultipleTreatmentAppointment(new UserContext(), "activityCode1", vo);
        List<AppointmentFormTimeDataVO> responseList = (List) response.getEntity();
        Assert.assertTrue(responseList.isEmpty());
    }

    @Test
    public void givenDeleteAppointmentWhenModifyMultipleTreatmentAppointmentThenReturnBoolean() {
        AppointmentFormDataVO vo = givenAppointmentFormData();
        vo.getAppointTimeList().forEach(appointmentFormTimeDataVO -> {
            appointmentFormTimeDataVO.setAppointmentId(Math.random() + "");
            appointmentFormTimeDataVO.setAction(1);
        });
        CarePathInstance carePathInstance = givenACarePathInstance();
        PowerMockito.when(activityServiceImp.checkMultiAppointmentsConflict(vo)).thenReturn(new ArrayList<>());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(vo.getPatientSer().toString())).thenReturn(carePathInstance);
        Response response = activityResource.modifyMultipleTreatmentAppointment(new UserContext(), "activityCode1", vo);
        List<AppointmentFormTimeDataVO> responseList = (List) response.getEntity();
        Assert.assertTrue(responseList.isEmpty());
    }

    @Test
    public void givenDeleteFirstAppointmentWhenModifyMultipleTreatmentAppointmentThenReturnBoolean() {
        AppointmentFormDataVO vo = givenAppointmentFormData();
        vo.getAppointTimeList().get(0).setAppointmentId("1");
        vo.getAppointTimeList().get(0).setAction(1);
        CarePathInstance carePathInstance = givenACarePathInstance();
        PowerMockito.when(activityServiceImp.checkMultiAppointmentsConflict(vo)).thenReturn(new ArrayList<>());
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(vo.getPatientSer().toString())).thenReturn(carePathInstance);
        PowerMockito.when(appointmentAntiCorruptionServiceImp.createAppointment(new AppointmentDto())).thenReturn("");
        Response response = activityResource.modifyMultipleTreatmentAppointment(new UserContext(), "activityCode1", vo);
        List<AppointmentFormTimeDataVO> responseList = (List) response.getEntity();
        Assert.assertTrue(responseList.isEmpty());
    }

    @Test
    public void givenHisIdWhenGetTreatmentAppointNumThenReturnObject(){
        String patientId = "12121";
        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(false);
        CarePathInstance carePathInstance = PowerMockito.mock(CarePathInstance.class);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(patientId)).thenReturn(carePathInstance);
        List<ActivityInstance> list = Arrays.asList(new ActivityInstance(){{
            setActivityType(ActivityTypeEnum.APPOINTMENT);
            setDueDateOrScheduledStartDate(new Date());
        }});
        PowerMockito.when(carePathInstance.getActivityInstances()).thenReturn(list);
        Pagination<AppointmentDto> pagination = new Pagination<AppointmentDto>(){{
            setLstObject(Arrays.asList(new AppointmentDto(){{
                setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED));
            }},new AppointmentDto(){{
                setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
            }}));
            setTotalCount(2);
        }};
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeWithPaging(Matchers.anyString(),Matchers.anyString(),
                Matchers.anyInt(),Matchers.anyInt(),Matchers.anyInt())).thenReturn(pagination);

        Response response = activityResource.getTreatmentAppointNum(new UserContext(),12122L);
        Assert.assertNotNull(response);
        List<KeyValuePair> rlist = (List<KeyValuePair>) response.getEntity();
        Assert.assertNotNull(rlist);
        Assert.assertTrue(rlist.size() == 2);

        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
        treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
        Map<String,Integer> map = new HashMap<String,Integer>(){{
            put("totalNum",12);
            put("completedNum",1);
        }};
        PowerMockito.when(treatmentAppointmentService.queryTotalAndCompletedTreatment(Matchers.anyLong(),Matchers.any(),Matchers.any())).thenReturn(map);
        rlist = (List<KeyValuePair>) response.getEntity();
        Assert.assertNotNull(rlist);
        Assert.assertTrue(rlist.size() == 2);
    }

    @Test
    public void givenHisIdAndAppointmentNotStoredToLocalWhenGetTreatmentAppointNumThenReturnObject() throws Exception {
        String patientId = "12121";
        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
        Map<String, Integer> map = new HashMap<>();
        map.put("totalNum", 10);
        map.put("completedNum", 5);
        treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
        PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(treatmentAppointmentService);
        PowerMockito.when(treatmentAppointmentService.queryTotalAndCompletedTreatment(Matchers.anyLong(),Matchers.any(),Matchers.any())).thenReturn(map);
        Response response = activityResource.getTreatmentAppointNum(new UserContext(), 123456L);
        Assert.assertNotNull(response);
        List<KeyValuePair> rlist = (List<KeyValuePair>) response.getEntity();
        Assert.assertNotNull(rlist);
    }

    @Test
    public void testPendingTask() throws Exception {
        UserContext userContext = givenAnUserContext();
        userContext.getLogin().setGroup("Other");
        EclipseTaskService service = PowerMockito.mock(EclipseTaskService.class);
        PowerMockito.whenNew(EclipseTaskService.class).withArguments(Mockito.any(UserContext.class)).thenReturn(service);
        PowerMockito.when(service.createPendingTask(Mockito.anyObject(), Mockito.anyObject())).thenReturn("newId");

        Response response = activityResource.pendingTask(userContext, "importExport", "3456", 123456L);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void testScheduleTreatmentAppointment2Local() throws Exception {
        String hisId = "hisId";
        Class[] parameterTypes = {AppointmentFormDataVO.class, ActivityInstance.class, UserContext.class};
        AppointmentFormDataVO vo = givenAppointmentFormData();
        ActivityInstance instance = new ActivityInstance();
        UserContext userContext = new UserContext();
        Object[] arguments = {vo, instance, userContext};
        Method method = activityResource.getClass().getDeclaredMethod("scheduleTreatmentAppointment2Local", parameterTypes);
        method.setAccessible(true);
        TreatmentAppointmentService treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
        PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(treatmentAppointmentService);
        PatientDto patientDto = new PatientDto();
        patientDto.setHisId(hisId);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(anyString())).thenReturn(patientDto);
        method.invoke(activityResource, arguments);
        method.setAccessible(false);
        Assert.assertTrue(true);
    }

    @Test
    public void testScheduleTreatmentAppointment() throws Exception {
        Class[] parameterTypes = {AppointmentFormDataVO.class, ActivityInstance.class, CarePathAntiCorruptionServiceImp.class, CarePathInstance.class};
        AppointmentFormDataVO vo = givenAppointmentFormData();
        ActivityInstance instance = new ActivityInstance();
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        CarePathInstance carePathInstance = new CarePathInstance();
        Object[] arguments = {vo, instance, carePathAntiCorruptionServiceImp, carePathInstance};
        Method method = activityResource.getClass().getDeclaredMethod("scheduleTreatmentAppointment", parameterTypes);
        method.setAccessible(true);
        method.invoke(activityResource, arguments);
        method.setAccessible(false);
        Assert.assertTrue(true);
    }

    @Test
    public void testTreatmentAppointmentDTOListContainDto() throws Exception {
        Class[] parameterTypes = {List.class, TreatmentAppointmentDTO.class};
        List<TreatmentAppointmentDTO> treatmentAppointmentDTOS = new ArrayList<>();
        TreatmentAppointmentDTO treatmentAppointmentDTO = new TreatmentAppointmentDTO();
        treatmentAppointmentDTO.setStartTime(new Date());
        treatmentAppointmentDTOS.add(treatmentAppointmentDTO);
        TreatmentAppointmentDTO treatmentAppointmentDTO1 = new TreatmentAppointmentDTO();
        treatmentAppointmentDTO1.setStartTime(new Date());
        Object[] arguments = {treatmentAppointmentDTOS, treatmentAppointmentDTO1};
        Method method = activityResource.getClass().getDeclaredMethod("treatmentAppointmentDTOListContainDto", parameterTypes);
        method.setAccessible(true);
        method.invoke(activityResource, arguments);
        method.setAccessible(false);
        Assert.assertTrue(true);
    }

    @Test
    public void testCalculateDueDate() throws Exception {
        Class[] parameterTypes = {Date.class, ActivityInstance.class};
        Date theDueDateOfActivity = new Date();
        ActivityInstance activityInstance = new ActivityInstance();
        activityInstance.setActivityType(ActivityTypeEnum.TASK);
        OrderDto orderDto = new OrderDto();
        orderDto.setDueDate(DateUtil.addDay(new Date(), 1));
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderById(Matchers.anyString())).thenReturn(orderDto);
        Object[] arguments = {theDueDateOfActivity, activityInstance};
        Method method = activityResource.getClass().getDeclaredMethod("calculateDueDate", parameterTypes);
        method.setAccessible(true);
        method.invoke(activityResource, arguments);
        method.setAccessible(false);
        Assert.assertTrue(true);
    }

    @Test
    public void givenAppointmentFormDataVOThenReturnFailToScheduleList() {
        AppointmentFormDataVO appointmentFormDataVO = givenAppointmentFormData();
        List<AppointmentFormTimeDataVO> failToScheduleList = givenAppointmentFormTimeDateList();
        PowerMockito.when(activityServiceImp.checkMultiAppointmentsConflict(appointmentFormDataVO)).thenReturn(failToScheduleList);
        Response response = activityResource.checkRecurringAppointments(new UserContext(), appointmentFormDataVO);
        Assert.assertNotNull(failToScheduleList);
        Assert.assertEquals(failToScheduleList, response.getEntity());
    }

    @Test
    public void givenEmptyAppointmentFormDataVOThenReturnFalse() {
        AppointmentFormDataVO appointmentFormDataVO = givenAppointmentFormData();
        appointmentFormDataVO.setDeviceId("");
        Response response = activityResource.checkRecurringAppointments(new UserContext(), appointmentFormDataVO);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    public void givenEmptyAssignResourceVOListThenReturnFalse() {
        Response response = activityResource.assignActivityResource(new UserContext(), "", new ArrayList<>());
        Assert.assertEquals(false, response.getEntity());
    }

    @Test
    public void givenNotEmptyAssignResourceVOListAndNoFailWhenDoneTaskThenReturnOK() throws Exception {
        List<AssignResourceVO> assignResourceVOS = assembleAssignResouceVOList();
        PowerMockito.when(activityServiceImp.setTaskDone(Matchers.anyString(), Matchers.anyString(), Matchers.any())).thenReturn(true);
        UserContext userContext = givenAnUserContext();
        EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(Matchers.anyLong())).thenReturn(new Encounter(){{
            setId("12345");
        }});
        PowerMockito.when(assignResourceServiceImp.assignPatient2Resource(Matchers.any())).thenReturn(true);
        Response response = activityResource.assignActivityResource(userContext,"activityCode", assignResourceVOS);
        Assert.assertEquals(true, response.getEntity());
    }

    @Test
    public void givenNotEmptyAssignResourceVOListAndHasFailedWhenDoneTaskThenReturnOK() throws Exception {
        List<AssignResourceVO> assignResourceVOS = assembleAssignResouceVOList();
        PowerMockito.when(activityServiceImp.setTaskDone(Matchers.anyString(), Matchers.anyString(), Matchers.any())).thenReturn(false);
        UserContext userContext = givenAnUserContext();
        EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(Matchers.anyLong())).thenReturn(new Encounter(){{
            setId("12345");
        }});
        PowerMockito.when(assignResourceServiceImp.assignPatient2Resource(Matchers.any())).thenReturn(true);
        Response response = activityResource.assignActivityResource(userContext, "activityCode", assignResourceVOS);
        Assert.assertEquals(true, response.getEntity());
    }

    @Test
    public void givenNotEmptyAssignTPSAndNoFailWhenDoneTaskThenReturnOK() throws Exception {
        List<AssignResourceVO> assignResourceVOS = assembleAssignResouceVOList();
        PowerMockito.when(activityServiceImp.setTaskDone(Matchers.anyString(), Matchers.anyString(), Matchers.any())).thenReturn(true);
        UserContext userContext = givenAnUserContext();
        EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(Matchers.anyLong())).thenReturn(new Encounter(){{
            setId("12345");
        }});
        PowerMockito.when(assignResourceServiceImp.assignPatient2Resource(Matchers.any())).thenReturn(true);
        Response response = activityResource.assignActivityResource(userContext,"assignTPSSDCH", assignResourceVOS);
        Assert.assertEquals(true, response.getEntity());
    }

    @Test
    public void givenNotEmptyAssignTPSAndHasFailedWhenDoneTaskThenReturnOK() throws Exception {
        List<AssignResourceVO> assignResourceVOS = assembleAssignResouceVOList();
        PowerMockito.when(activityServiceImp.setTaskDone(Matchers.anyString(), Matchers.anyString(), Matchers.any())).thenReturn(false);
        UserContext userContext = givenAnUserContext();
        EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(Matchers.anyLong())).thenReturn(new Encounter(){{
            setId("12345");
        }});
        PowerMockito.when(assignResourceServiceImp.assignPatient2Resource(Matchers.any())).thenReturn(true);
        Response response = activityResource.assignActivityResource(userContext, "assignTPS", assignResourceVOS);
        Assert.assertEquals(true, response.getEntity());
    }

//    @Test
//    public void givenAssignMachinesThenReturnList() throws Exception {
//        AssignResourceServiceImp assignResourceServiceImp = PowerMockito.mock(AssignResourceServiceImp.class);
//        PowerMockito.whenNew(AssignResourceServiceImp.class).withAnyArguments().thenReturn(assignResourceServiceImp);
//        List<AssignResourceVO> assignResourceVOS = givenAssignResourceVOS();
//        PowerMockito.when(assignResourceServiceImp.searchAssignResourceSummary(anyObject())).thenReturn(assignResourceVOS);
//        Response response = activityResource.searchDeviceSummary(new UserContext());
//        Assert.assertNotNull(assignResourceVOS);
//        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
//    }

    @Test
    public void givenAssignResourceThenReturnList() throws Exception {
        AssignResourceServiceImp assignResourceServiceImp = PowerMockito.mock(AssignResourceServiceImp.class);
        PowerMockito.whenNew(AssignResourceServiceImp.class).withAnyArguments().thenReturn(assignResourceServiceImp);
        List<AssignResourceVO> assignResourceVOS = givenAssignResourceVOS();
        PowerMockito.when(assignResourceServiceImp.listAssignResourceSummary(anyString())).thenReturn(assignResourceVOS);
        Response response = activityResource.listAssignResourceSummary(new UserContext(), "actCode");
        Assert.assertNotNull(assignResourceVOS);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    private List<AssignResourceVO> givenAssignResourceVOS() {
        List<AssignResourceVO> assignResourceVOS = new ArrayList<>();
        AssignResourceVO assignResourceVO = new AssignDeviceVO();
        assignResourceVO.setId("id1");
        assignResourceVO.setCode("code1");
        assignResourceVO.setName("machine1");
        assignResourceVO.setAmount(1);
        assignResourceVO.setColor("color");
        assignResourceVO.setPatientSerInstanceIdPairList(assemblePatientSerInstanceIdPairList());
        assignResourceVOS.add(assignResourceVO);
        return assignResourceVOS;
    }

    private CarePathTemplate givenATemplate() {
        CarePathTemplate template = givenCarePathTemplate();
        PlannedActivity activity = new PlannedActivity();
        activity.setId("4");
        activity.setDepartmentID("1");
        activity.setDefaultGroupID("10013");
        activity.setActivityType(ActivityTypeEnum.APPOINTMENT);
        activity.setActivityCode("deviceCode");
        activity.setDeviceIDs(Arrays.asList("deviceId1"));
        activity.setAutoAssignPrimaryOncologist(true);
        template.getActivities().add(activity);

        PlannedActivity activity2 = new PlannedActivity();
        activity2.setId("5");
        activity2.setDepartmentID("1");
        activity2.setDefaultGroupID("10013");
        activity2.setActivityType(ActivityTypeEnum.APPOINTMENT);
        activity2.setActivityCode("noDeviceCode");
        activity2.setAutoAssignPrimaryOncologist(false);
        template.getActivities().add(activity2);
        return template;
    }

    private AppointmentFormDataVO givenAppointmentFormData() {
        AppointmentFormDataVO rvo = new AppointmentFormDataVO();
        rvo.setActivityType(ActivityTypeEnum.TASK.name());
        rvo.setDeviceId("1");
        rvo.setAppointTimeList(Arrays.asList(new AppointmentFormTimeDataVO("", "2017-05-11 11:00:00", "2017-05-11 11:20:00", 0),
                new AppointmentFormTimeDataVO("", "2017-05-12 11:00:00", "2017-05-12 11:20:00", 0),
                new AppointmentFormTimeDataVO("", "2017-05-13 11:00:00", "2017-05-13 11:20:00", 0)));
        rvo.setPatientSer(12345L);
        return rvo;
    }

    private List<AppointmentFormTimeDataVO> givenAppointmentFormTimeDateList() {
        List<AppointmentFormTimeDataVO> vos = new ArrayList<>();
        AppointmentFormTimeDataVO vo1 = new AppointmentFormTimeDataVO("", "2017-05-12 11:00:00", "2017-05-12 11:20:00",0);
        AppointmentFormTimeDataVO vo2 = new AppointmentFormTimeDataVO("", "2017-05-13 11:00:00", "2017-05-13 11:20:00",0);
        vos.add(vo1);
        vos.add(vo2);
        return vos;
    }

    private List<PatientDto> givenPatientDtoList() {
        List<PatientDto> result = new ArrayList<>();
        PatientDto dto = new PatientDto();
        dto.setAriaId("1");
        dto.setHisId("1");
        dto.setNationalId("1");
        dto.setChineseName("name");
        dto.setEnglishName("name");
        dto.setGender("M");
        dto.setBirthday(new Date());
        dto.setContactPerson("person");
        dto.setContactPhone("phone");
        dto.setPatientSer("1");
        dto.setPhysicianGroupId("1");
        dto.setPhysicianId("1");
        dto.setPhysicianName("name");
        result.add(dto);
        return result;
    }

    private UserContext givenAnUserContext() {
        Login login = MockDtoUtil.givenALogin();
        login.setGroup(SystemConfigPool.queryGroupRoleOncologist());
        UserContext userContext = new UserContext(login, MockDtoUtil.givenAnOspLogin());
        return userContext;
    }

    private List<OrderDto> givenAnOrderList() {
        List<OrderDto> orderDtoList = new ArrayList<>();
        orderDtoList.add(new OrderDto());
        return orderDtoList;
    }

    private List<ActivityInstanceVO> givenAnActivityInstanceVOList() {
        List<ActivityInstanceVO> instanceVOList = new ArrayList<>();
        return instanceVOList;
    }

    private List<AppointmentDto> givenAnAppointmentList() {
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        appointmentDtoList.add(new AppointmentDto());
        return appointmentDtoList;
    }

    private Pagination<ActivityInstanceVO> givenActivityInstancePagination() {
        List<ActivityInstanceVO> instanceVOList = givenAnActivityInstanceVOList();
        Pagination<ActivityInstanceVO> result = new Pagination<>();
        result.setLstObject(instanceVOList);
        return result;
    }

    private Pagination<OrderDto> givenOrderDtoPagination() {
        List<OrderDto> orderDtos = new ArrayList<>();
        Pagination<OrderDto> result = new Pagination<>();
        result.setLstObject(orderDtos);
        return result;
    }

    private Pagination<PatientDto> givenPatientDtoPagination() {
        List<PatientDto> patientDtos = new ArrayList<>();
        Pagination<PatientDto> result = new Pagination<>();
        result.setLstObject(patientDtos);
        return result;
    }

    private List<AssignResourceVO> assembleAssignResouceVOList() {
        List<AssignResourceVO> assignResourceVOS = new ArrayList<>();
        AssignResourceVO assignResourceVO = new AssignDeviceVO();
        assignResourceVO.setId("id1");
        assignResourceVO.setCode("code1");
        assignResourceVO.setName("machine1");
        assignResourceVO.setAmount(1);
        assignResourceVO.setColor("color");
        assignResourceVO.setPatientSerInstanceIdPairList(assemblePatientSerInstanceIdPairList());
        assignResourceVOS.add(assignResourceVO);
        return assignResourceVOS;
    }

    private List<Map<String, String>> assemblePatientSerInstanceIdPairList() {
        List<Map<String, String>> pair = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("PatientSer", "1");
        map.put("orderId", "instanceId1");
        Map<String, String> map1 = new HashMap<>();
        map1.put("PatientSer", "2");
        map1.put("orderId", "instanceId2");
        pair.add(map);
        pair.add(map1);
        return pair;
    }
}
