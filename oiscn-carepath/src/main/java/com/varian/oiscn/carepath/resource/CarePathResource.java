package com.varian.oiscn.carepath.resource;

import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.service.QueuingManagementServiceImpl;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.CarePathInstanceSorterHelper;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.tasklocking.TaskLockingDto;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.carepath.assembler.CarePathTemplateAssembler;
import com.varian.oiscn.carepath.service.CarePathConfigService;
import com.varian.oiscn.carepath.vo.ActivityEntryVO;
import com.varian.oiscn.carepath.vo.SequentialActivityVO;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.activity.SequentialActivityStatusEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.carepath.*;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.util.I18nReader;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 4/20/2017.
 */
@Path("/")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarePathResource extends AbstractResource {

    private CarePathAntiCorruptionServiceImp antiCorruptionServiceImp;
    protected ValueSetAntiCorruptionServiceImp valueSetACService;
    private GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp;
    public static final String SCHEDULE_ACTIVITY_PREFIX = "SCHEDULE";

    public CarePathResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        antiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        valueSetACService = new ValueSetAntiCorruptionServiceImp();
        groupAntiCorruptionServiceImp = new GroupAntiCorruptionServiceImp();
    }

    @Path("/carepath/searchActivityEntries")
    @GET
    public Response searchActivityEntries(@Auth UserContext userContext) {
        List<ActivityEntryVO> result = new ArrayList<>();
        List<String> supportCarePathTemplateNameList = new ArrayList<>();
        String defaultTemplateName = configuration.getDefaultCarePathTemplateName();
        supportCarePathTemplateNameList.add(defaultTemplateName);
        List<CarePathConfigItem> carePathConfigItems = this.configuration.getCarePathConfig().getCarePath();
        if(carePathConfigItems != null){
            carePathConfigItems.forEach(carePathConfigItem -> supportCarePathTemplateNameList.add(carePathConfigItem.getTemplateId()));
        }
        if (!supportCarePathTemplateNameList.isEmpty()) {
            CarePathTemplate carePathTemplate;
            for (String templateName : supportCarePathTemplateNameList) {
                carePathTemplate = antiCorruptionServiceImp.queryCarePathByTemplateName(templateName);
                if (carePathTemplate == null) {
                    continue;
                }
                CarePathTemplateHelper.sortActivities(carePathTemplate);
                result.addAll(new CarePathTemplateAssembler(carePathTemplate).getActivityEntries(userContext.getLogin().getResourceSer().toString(), userContext.getLogin().getGroup(),
                        userContext.getLogin().getStaffGroups()));
            }
//          去重
            Set<String> codeSet = new HashSet<>();
            List<ActivityEntryVO> newList = new ArrayList<>();
            Iterator<ActivityEntryVO> it = result.iterator();
            while(it.hasNext()){
                ActivityEntryVO vo = it.next();
                if(codeSet.add(vo.getActivityCode())){
                    newList.add(vo);
                }
            }
            codeSet.clear();
            result = newList;
        }
//        技师特殊处理
        if(userContext.getLogin().getGroup().equals(SystemConfigPool.queryGroupRoleTherapist())) {
            Iterator<ActivityEntryVO> it = result.iterator();
            boolean isFirstAppointment = true;
            while (it.hasNext()) {
                ActivityEntryVO vo = it.next();
                if (vo.getActivityType().equals(ActivityTypeEnum.APPOINTMENT.name())) {
                    if (!isFirstAppointment) {
                        it.remove();
                    } else {
                        isFirstAppointment = false;
                    }
                }
            }
        }
        return Response.ok(result).build();
    }

    @Path("/carepath/searchOrganizations")
    @GET
    public Response searchOrganizations(@Auth UserContext userContext){
        List<GroupDto> groupDtoList = groupAntiCorruptionServiceImp.queryGroupListByResourceID(userContext.getLogin().getResourceSer().toString());
        List<GroupTreeNode> resultList = new ArrayList<>();
        GroupTreeNode root = null;
        String group = userContext.getLogin().getGroup();
        if(SystemConfigPool.queryGroupRoleNurse().equalsIgnoreCase(group)){
            root = GroupPractitionerHelper.copy(GroupPractitionerHelper.getNurseGroupTreeNode());
            root.setName(I18nReader.getLocaleValueByKey("Nurse.Group.TopNodeName"));
        } else if(SystemConfigPool.queryGroupRoleOncologist().equalsIgnoreCase(group)){
            root = GroupPractitionerHelper.copy(GroupPractitionerHelper.getOncologyGroupTreeNode());
            root.setName(I18nReader.getLocaleValueByKey("Oncologist.Group.TopNodeName"));
        } else if(SystemConfigPool.queryGroupRolePhysicist().equals(group)){
            root = GroupPractitionerHelper.copy(GroupPractitionerHelper.getPhysicistGroupTreeNode());
            root.setName(I18nReader.getLocaleValueByKey("Physicist.Group.TopNodeName"));
        } else if(SystemConfigPool.queryGroupRoleTherapist().equals(group)){
            root = GroupPractitionerHelper.copy(GroupPractitionerHelper.getTechGroupTreeNode());
            root.setName(I18nReader.getLocaleValueByKey("Technician.Group.TopNodeName"));
        }
//          根据登录用户所在的组织机构过滤组织机构
        List<String> groupIdList = new ArrayList<>();
        groupDtoList.forEach(groupDto -> groupIdList.add(groupDto.getGroupId()));
        List<String> needDelGroupIdList = new ArrayList<>();
        // root would be NULL when SysAdmin
        if (root != null) {
            GroupPractitionerHelper.findDelGroupIdList(root, groupIdList, needDelGroupIdList);
            GroupPractitionerHelper.cleanNode(root, needDelGroupIdList);
            if(SystemConfigPool.queryGroupRoleTherapist().equals(group)){
                resultList.addAll(root.getSubItems());
            }else{
                resultList.add(root);
            }
        }
        return Response.ok(resultList).build();
    }

    @Path("/carepath/locktask/{activityType}/{id}")
    @POST
    public Response lockTask(@Auth UserContext userContext, @PathParam("activityType") String activityType, @PathParam("id") String instanceId) {
        TaskLockingServiceImpl taskLockingService = new TaskLockingServiceImpl(userContext);
        TaskLockingDto taskLockingDto = new TaskLockingDto();
        boolean lockTaskAction;
        List<KeyValuePair> keyValuePairs = new ArrayList<>();
        String lockResourceName = "";
        try {
            taskLockingService.lockCurrentThread();
            taskLockingDto.setTaskId(instanceId);
            taskLockingDto.setActivityType(activityType);
            TaskLockingDto lockedDto = taskLockingService.findLockTaskUserName(taskLockingDto);
            if (lockedDto == null) {
                taskLockingDto.setLockUserName(userContext.getName());
                taskLockingDto.setResourceSer(userContext.getLogin().getResourceSer());
                taskLockingDto.setResourceName(userContext.getLogin().getResourceName());
                taskLockingDto.setLockTime(new Date());
                lockTaskAction = taskLockingService.lockTask(taskLockingDto);
            } else if (userContext.getName().equals(lockedDto.getLockUserName())) {
                lockTaskAction = true;
                lockResourceName = "";
            } else {
                lockTaskAction = false;
                lockResourceName = lockedDto.getResourceName();
            }
            keyValuePairs.add(new KeyValuePair("lockTask", Boolean.toString(lockTaskAction)));
            keyValuePairs.add(new KeyValuePair("lockResourceName", lockResourceName));
        } finally {
            taskLockingService.unLockCurrentThread();
        }
        return Response.status(Response.Status.OK).entity(keyValuePairs).build();
    }


    @Path("/carepath/searchSequentialActivities")
    @GET
    public Response searchSequentialActivities(@Auth UserContext userContext,
                                               @QueryParam("patientSer") String patientSer,
                                               @QueryParam("id") String instanceId,
                                               @QueryParam("activityCode") String activityCode,
                                               @QueryParam("activityType") String activityType) {
        List<SequentialActivityVO> sequentialActivities = new ArrayList<>();
        boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
        CarePathInstance carePathInstance;
        if(StringUtils.isNumeric(instanceId)) {
            carePathInstance = antiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(patientSer,instanceId,ActivityTypeEnum.valueOf(activityType));
        }else{
            carePathInstance = antiCorruptionServiceImp.queryLastCarePathByPatientIDAndActivityCode(patientSer, activityCode);
        }
        //如果本地存储治疗appointment的开关开启，并且是治疗技师做治疗，则从本地数据库中取出appointment, 再同步到ARIA里
        if (appointmentStoredToLocal) {
            if (StringUtils.equalsIgnoreCase(activityCode,SystemConfigPool.queryTreatmentActivityCode())) {
                //调用service从本地数据库取出appointment
                TreatmentAppointmentService service = new TreatmentAppointmentService(userContext);

                CarePathInstanceHelper carePathInstanceHelper = new CarePathInstanceHelper(carePathInstance);
                ActivityInstance treatmentInstance = carePathInstanceHelper.getActivityByCode(activityCode);
//                if (treatmentInstance == null) {
//                    log.error("The carepath instance doesn't contain activity code[{}].", activityCode);
//                    return Response.ok(sequentialActivities).build();
//                }

                // 判断是否是预约时间最早的appointment，如果是则需要关联carepath，如果不是第一个appointment，直接在aria里创建
                TreatmentAppointmentDTO theFirstTreatmentAppointment = service.queryTheFirstTreatmentAppointmentByPatientSer(Long.parseLong(patientSer), activityCode);
                if (theFirstTreatmentAppointment == null) {
                    log.error("Can't find the treatment appointment by id[{}] in local db.", instanceId);
                    return Response.ok(sequentialActivities).build();
                }
                if (StringUtils.equals(theFirstTreatmentAppointment.getUid(), instanceId)
                        && StringUtils.isEmpty(treatmentInstance.getInstanceID())) {
                    if (isEmpty(theFirstTreatmentAppointment.getAppointmentId())) {
                        AppointmentDto newAppointmentDto = newAppointment(theFirstTreatmentAppointment);
                        String appointmentId = antiCorruptionServiceImp.scheduleNextAppointment(carePathInstance.getId(), treatmentInstance.getId(), newAppointmentDto);
                        if (isNotEmpty(appointmentId)) {
                            treatmentInstance.setInstanceID(appointmentId);
                            treatmentInstance.setIsActiveInWorkflow(true);
                            treatmentInstance.setStatus(CarePathStatusEnum.ACTIVE);
                            //使用aria里新创建的appointmentId作为instanceId
                            instanceId = appointmentId;
                            theFirstTreatmentAppointment.setAppointmentId(appointmentId);
                            service.update(theFirstTreatmentAppointment, theFirstTreatmentAppointment.getId());
//                          需要重新锁定该任务（之前锁定的taskId是本地数据库的主键）
                            new TaskLockingServiceImpl(userContext).lockTask(new TaskLockingDto(instanceId, activityType, userContext.getName(), userContext.getLogin().getResourceSer(), userContext.getLogin().getResourceName(), new Date()));
                            new QueuingManagementServiceImpl().modifyFromUid2AriaId(theFirstTreatmentAppointment.getUid(),appointmentId);
                        }
                    }
                } else {
                    TreatmentAppointmentDTO treatmentAppointmentDTO = service.queryByUidOrAppointmentId(instanceId);
                    if (treatmentAppointmentDTO != null && isEmpty(treatmentAppointmentDTO.getAppointmentId())) {
                        AppointmentDto newAppointmentDto = newAppointment(treatmentAppointmentDTO);
                        AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
                        String appointmentId = appointmentAntiCorruptionServiceImp.createAppointment(newAppointmentDto);
                        instanceId = appointmentId;
                        treatmentAppointmentDTO.setAppointmentId(appointmentId);
                        service.update(treatmentAppointmentDTO, treatmentAppointmentDTO.getId());
                        carePathInstance.addActivityInstance(new ActivityInstance() {{
                            setIsActiveInWorkflow(true);
                            setStatus(CarePathStatusEnum.ACTIVE);
                            setInstanceID(appointmentId);
                            setActivityCode(activityCode);
                            setDefaultGroupID(treatmentInstance.getDefaultGroupID());
                            setActivityType(ActivityTypeEnum.APPOINTMENT);
                        }});
//                      需要重新锁定该任务（之前锁定的taskId是本地数据库的主键）
                        new TaskLockingServiceImpl(userContext).lockTask(new TaskLockingDto(instanceId, activityType, userContext.getName(), userContext.getLogin().getResourceSer(), userContext.getLogin().getResourceName(), new Date()));
                        new QueuingManagementServiceImpl().modifyFromUid2AriaId(treatmentAppointmentDTO.getUid(),appointmentId);
                    }
                }
            }
        }
        SequentialActivitiesHelper helper = new SequentialActivitiesHelper(carePathInstance, userContext.getLogin().getStaffGroups());
        sequentialActivities = helper.querySequentialActivitiesByInstanceId(instanceId, activityType);
        sequentialActivities.forEach(sequentialActivityVO -> {
            sequentialActivityVO.setCarePathInstanceId(carePathInstance.getId());
        });
        return Response.ok(sequentialActivities).build();
    }

    @Path("/carePath/nextSequentialAppointments")
    @GET
    public Response getNextSequentialAppointmentsByPatientSer(@Auth UserContext userContext,
                                                              @QueryParam("hisId") String hisId){
        List<SequentialActivityVO> activeScheduleSequentialActivityVOList = new ArrayList<>();
        HashMap<String, Object> result = new HashMap<>();
        result.put("patientSer", "");
        result.put("schedulingTasks", activeScheduleSequentialActivityVOList);
        if(StringUtils.isEmpty(hisId)){
            log.debug("hisId cannot be empty.");
            return Response.ok(result).build();
        }
        PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        PatientDto patientDto = patientAntiCorruptionServiceImp.queryPatientByHisId(hisId);

        if(patientDto == null){
            log.debug("Cannot find this patient.");
            return Response.ok(result).build();
        }
        String patientSer = patientDto.getPatientSer();
        result.put("patientSer", patientSer);
        PatientEncounterCarePath patientEncounterCarePath = PatientEncounterHelper.getEncounterCarePathByPatientSer(patientSer);
        List<SequentialActivityVO> activeOtherSequentialActivityVOList = new ArrayList<>();

        if (patientEncounterCarePath == null || patientEncounterCarePath.getPlannedCarePath() == null) {
            log.error("No carepath found for this error.");
            return Response.ok(result).build();
        }
        EncounterCarePathList encounterCarePathList = patientEncounterCarePath.getPlannedCarePath();
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        List<CarePathInstance> carePathInstanceList = carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(patientSer);
        List<CarePathInstance> activeCarePathList = new ArrayList<>();
        encounterCarePathList.getEncounterCarePathList().forEach(encounterCarePath -> {
            carePathInstanceList.forEach(carePathInstance -> {
                if (carePathInstance.getId().equals(String.valueOf(encounterCarePath.getCpInstanceId()))) {
                    activeCarePathList.add(carePathInstance);
                }
            });
        });

        for(CarePathInstance currentCarePathInstance : activeCarePathList){
            CarePathInstanceSorterHelper.sortActivities(currentCarePathInstance);
            SequentialActivitiesHelper sequentialActivitiesHelper = new SequentialActivitiesHelper(currentCarePathInstance, userContext.getLogin().getStaffGroups());
            List<ActivityInstance> activeActivityInstances = new ArrayList<>();
            currentCarePathInstance.getActivityInstances().forEach(activityInstance -> {
                if(activityInstance.getIsActiveInWorkflow() && userContext.getLogin().getStaffGroups().contains(activityInstance.getDefaultGroupID())){
                    activeActivityInstances.add(activityInstance);
                }
            });

            //首先看当前的activity是否是预约，可以通过activitycode.yaml中的workSpaceType判断，是否以SCHEDULE开头。
            for(ActivityInstance activityInstance : activeActivityInstances){
                SequentialActivityVO sequentialActivityVO = sequentialActivitiesHelper.assemblerSequentialActivityVO(activityInstance, SequentialActivityStatusEnum.ACTIVE);
                if(sequentialActivityVO.getWorkspaceType().startsWith(SCHEDULE_ACTIVITY_PREFIX)){
                    activeScheduleSequentialActivityVOList.add(sequentialActivityVO);
                } else {
                    activeOtherSequentialActivityVOList.add(sequentialActivityVO);
                }
            }
        }
        //如果当前的任务不是预约任务，则返回空的list。
        if(activeScheduleSequentialActivityVOList.isEmpty()){
            //该用户实际的下一步任务可以在activeOtherSequentialActivityVOList中找到
            return Response.ok(result).build();
        }

        return Response.ok(result).build();

    }

    @GET
    @Path("/carepath/list-primary")
    public Response listCarePathPrimary(@Auth UserContext userContext){
        List<CarePathTemplateVO> carePathList = listCarePath(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
        return Response.ok(carePathList).build();
    }

    @GET
    @Path("/carepath/list-optional")
    public Response listCarePathOptional(@Auth UserContext userContext) {
        List<CarePathTemplateVO> carePathList = listCarePath(EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL);
        return Response.ok(carePathList).build();
    }

    private List<CarePathTemplateVO> listCarePath(EncounterCarePath.EncounterCarePathCategoryEnum category){
        List<CarePathTemplateVO> carePathList = CarePathConfigService.getCarePathTemplateList();
        List<CarePathTemplateVO> newList = new ArrayList<>();
        if(carePathList != null){
            Iterator<CarePathTemplateVO> it = carePathList.iterator();
            while(it.hasNext()){
                CarePathTemplateVO vo = it.next();
                if(vo.getCategory().equals(category)){
                    newList.add(vo);
                }
            }
        }
        return newList;
    }

    private AppointmentDto newAppointment(TreatmentAppointmentDTO treatmentAppointmentDTO) {
        AppointmentDto newAppointmentDto = new AppointmentDto();
        newAppointmentDto.setStartTime(treatmentAppointmentDTO.getStartTime());
        newAppointmentDto.setEndTime(treatmentAppointmentDTO.getEndTime());
        newAppointmentDto.setReason(treatmentAppointmentDTO.getActivityCode());
        newAppointmentDto.setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, String.valueOf(treatmentAppointmentDTO.getPatientSer())));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, treatmentAppointmentDTO.getDeviceId()));
        newAppointmentDto.setParticipants(participantDtos);
        return newAppointmentDto;
    }
}
