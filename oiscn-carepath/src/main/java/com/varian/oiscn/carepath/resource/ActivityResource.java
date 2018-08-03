package com.varian.oiscn.carepath.resource;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.appointment.dto.CheckInStatusEnum;
import com.varian.oiscn.appointment.dto.QueuingManagementDTO;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.service.QueuingManagementServiceImpl;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.appointment.vo.AppointmentDataTimeSlotVO;
import com.varian.oiscn.appointment.vo.AppointmentDataVO;
import com.varian.oiscn.appointment.vo.QueuingManagementVO;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.tasklocking.TaskLockingDto;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.util.DeviceUtil;
import com.varian.oiscn.carepath.assembler.ActivityInstanceForAppointmentAssembler;
import com.varian.oiscn.carepath.assembler.ActivityInstanceForOrderAssembler;
import com.varian.oiscn.carepath.assembler.ActivityInstanceForPatientAssembler;
import com.varian.oiscn.carepath.assembler.TreatmentAppointmentAssembler;
import com.varian.oiscn.carepath.service.ActivityServiceImp;
import com.varian.oiscn.carepath.service.LinkCPInDynamicFormConfigService;
import com.varian.oiscn.carepath.task.EclipseTask;
import com.varian.oiscn.carepath.task.EclipseTaskService;
import com.varian.oiscn.carepath.vo.ActivityInstanceVO;
import com.varian.oiscn.carepath.vo.AppointmentActionEnum;
import com.varian.oiscn.carepath.vo.AppointmentFormDataVO;
import com.varian.oiscn.carepath.vo.AppointmentFormTimeDataVO;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.RankEnum;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.activity.ActivityCodeConstants;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentRankEnum;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.assign.AssignResource;
import com.varian.oiscn.core.assign.AssignResourceField;
import com.varian.oiscn.core.assign.AssignResourceVO;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathConfigItem;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import com.varian.oiscn.core.carepath.PlannedActivity;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderRankEnum;
import com.varian.oiscn.core.order.OrderStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.assign.AssignResourceFieldServiceImp;
import com.varian.oiscn.encounter.assign.AssignResourceServiceImp;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.resource.BaseResponse;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Class description
 *
 * @author gbt1220
 */
@Path("/")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActivityResource extends AbstractResource {
    private static int COUNT_PER_PAGE = Integer.MAX_VALUE;
    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;
    private OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp;
    private EncounterServiceImp encounterServiceImp;

    /**
     * Constructs ...
     *
     * @param configuration
     * @param environment
     */
    public ActivityResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
        patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        orderAntiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
        encounterServiceImp = new EncounterServiceImp(new UserContext());
    }

    private Date calculateDueDate(Date theDueDateOfActivity, ActivityInstance activityInstance) {
        Date tmpTheDueDateOfActivity = theDueDateOfActivity;
        if (activityInstance.getActivityType().equals(ActivityTypeEnum.TASK)) {
            OrderDto thePrevOrder = orderAntiCorruptionServiceImp.queryOrderById(activityInstance.getInstanceID());
            if (thePrevOrder.getDueDate().compareTo(theDueDateOfActivity) > 0) {
                tmpTheDueDateOfActivity = thePrevOrder.getDueDate();
            }
        } else {
            AppointmentDto appointmentDto =
                    appointmentAntiCorruptionServiceImp.queryAppointmentById(activityInstance.getInstanceID());
            if (appointmentDto.getEndTime().compareTo(theDueDateOfActivity) > 0) {
                tmpTheDueDateOfActivity = appointmentDto.getEndTime();
            }
        }
        return tmpTheDueDateOfActivity;
    }

    private AppointmentDto createAppointmentDto(AppointmentFormDataVO vo, AppointmentFormTimeDataVO timeDataVo, String activityCode, String status) {
        AppointmentDto newAppointmentDto = new AppointmentDto();
        try {
            newAppointmentDto.setStartTime(DateUtil.parse(timeDataVo.getStartTime()));
            newAppointmentDto.setEndTime(DateUtil.parse(timeDataVo.getEndTime()));
        } catch (ParseException e) {
            log.error("ParseException: {}", e.getMessage());
        }
        newAppointmentDto.setReason(activityCode);
        newAppointmentDto.setStatus(status);
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, String.valueOf(vo.getPatientSer())));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, vo.getDeviceId()));
        newAppointmentDto.setParticipants(participantDtos);

        return newAppointmentDto;
    }

    /**
     * Search patient list, task list or appointment list
     *
     * @param userContext  user context
     * @param activityCode activity code
     * @param activityType activity type
     * @param startDate    start date
     * @param endDate      end date
     * @param hisId        his id
     * @param countPerPage count per page
     * @param pageNumber   page number
     * @param sort         sort
     * @return
     */
    @Path("/activities/search")
    @GET
    public Response searchActiveActivities(@Auth UserContext userContext, @QueryParam("code") String activityCode,
                                           @QueryParam("type") String activityType,
                                           @QueryParam("startDate") String startDate,
                                           @QueryParam("endDate") String endDate,
                                           @QueryParam("hisId") String hisId,
                                           @QueryParam("deviceId") String deviceId,
                                           @QueryParam("countPerPage") String countPerPage,
                                           @QueryParam("pageNumber") String pageNumber,
                                           @QueryParam("sort") String sort,
                                           @QueryParam("urgent") boolean urgent,
                                           @QueryParam("group") String group) {
        log.info("Search active activities by code[{}],type[{}],startDate[{}],endDate[{}],sort[{}],userId[{}]",
                activityCode, activityType, startDate, endDate, sort, userContext.getName());

        List<ActivityInstanceVO> instanceVOList = new ArrayList<>();
        Pagination<ActivityInstanceVO> pagination = new Pagination<>();

        if (log.isDebugEnabled()) {
            log.debug("activityType: [{}]", activityType);
        }
        if (StringUtils.equalsIgnoreCase(ActivityTypeEnum.PATIENT.name(), activityType)) {
            Pagination<PatientDto> patientDtoPagination = getPatientDtoList(
                    userContext,
                    urgent,
                    hisId,
                    group,
                    countPerPage,
                    pageNumber);
            if ((patientDtoPagination.getLstObject() == null) || patientDtoPagination.getLstObject().isEmpty()) {
                log.warn("Not found patient");
                return Response.ok(instanceVOList).build();
            }

            ActivityInstanceForPatientAssembler patientAssembler =
                    new ActivityInstanceForPatientAssembler(patientDtoPagination.getLstObject(),
                            userContext.getLogin().getStaffGroups(),
                            configuration,
                            userContext);
            instanceVOList = patientAssembler.getActivityInstances();
            pagination.setLstObject(instanceVOList);
            pagination.setTotalCount(patientDtoPagination.getTotalCount());
        } else if (StringUtils.equalsIgnoreCase(ActivityTypeEnum.TASK.name(), activityType)) {
            //用于存储如果是Physicist的话，是否需要区分TOMO，Eclipse等，存储内容为TOMO，Eclipse等
            List<String> tpsList = new ArrayList<>();
            Pagination<OrderDto> orderDtoPagination;
            if (StringUtils.equals(userContext.getLogin().getGroup(), SystemConfigPool.queryGroupRolePhysicist())) {
                List<String> staffGroups = userContext.getLogin().getStaffGroups();
                if (StringUtils.isNotBlank(group)) {
                    GroupTreeNode groupTreeNode = GroupPractitionerHelper.searchGroupById(group);
                    if (groupTreeNode.getSubItems().isEmpty()) {
                        String[] tpsNameString = groupTreeNode.getOriginalName().split("_");
                        tpsList.add(StringUtils.trimToEmpty(tpsNameString[1]).toLowerCase());
                    } else {
                        if (!staffGroups.contains(groupTreeNode.getId())) {
                            groupTreeNode.getSubItems().forEach(groupTreeNode1 -> {
                                if (staffGroups.contains(groupTreeNode1.getId())) {
                                    String[] tpsNameString = groupTreeNode1.getOriginalName().split("_");
                                    tpsList.add(StringUtils.trimToEmpty(tpsNameString[1]).toLowerCase());
                                }
                            });
                        }
                    }
                }
            }
            orderDtoPagination = getOrderDtos(userContext, hisId, activityCode, group, countPerPage, pageNumber, sort, urgent);

            if ((orderDtoPagination.getLstObject() == null) || orderDtoPagination.getLstObject().isEmpty()) {
                log.debug("Not found order by activity code[{}]", activityCode);

                return Response.ok(instanceVOList).build();
            }
            if (log.isDebugEnabled()) {
                log.debug("orderDtoPagination TotalCount: [{}]", orderDtoPagination.getTotalCount());
                int debugIndex = 0;
                for (OrderDto object : orderDtoPagination.getLstObject()) {
                    log.debug("OrderDto[{}]: {}", debugIndex++, object.toString());
                }
            }

            ActivityInstanceForOrderAssembler orderAssembler = new ActivityInstanceForOrderAssembler(
                    orderDtoPagination.getLstObject(),
                    configuration,
                    userContext);

            instanceVOList = orderAssembler.getActivityInstances();

            if (!tpsList.isEmpty() && !instanceVOList.isEmpty()) {
                List<Long> patientSerList = new ArrayList<>();
                instanceVOList.forEach(activityInstanceVO -> patientSerList.add(Long.parseLong(activityInstanceVO.getPatientSer())));

                AssignResourceServiceImp assignResourceServiceImp = new AssignResourceServiceImp(userContext);
                Map<Long, String> patientSerAndAssignTPSMap = assignResourceServiceImp.getPatientSerResourceMapByPatientSerList(patientSerList, SystemConfigPool.queryPhysicistGroupingActivityCode());
                for (Iterator<ActivityInstanceVO> iterator = instanceVOList.iterator(); iterator.hasNext(); ) {
                    Long patientSerForIterator = Long.parseLong(iterator.next().getPatientSer());
                    if (!patientSerAndAssignTPSMap.containsKey(patientSerForIterator) || !tpsList.contains(StringUtils.trimToEmpty(patientSerAndAssignTPSMap.get(patientSerForIterator)).toLowerCase())) {
                        iterator.remove();
                    }
                }
                if (StringUtils.isNotEmpty(countPerPage) && StringUtils.isNotEmpty(pageNumber)) {
                    pagination.setTotalCount(instanceVOList.size());
                    int countPerPageInt = Integer.parseInt(countPerPage);
                    int pageNumberInt = Integer.parseInt(pageNumber);
                    int fromIndex = countPerPageInt * (pageNumberInt - 1);
                    int toIndex = countPerPageInt * pageNumberInt;
                    if (instanceVOList.size() >= toIndex) {
                        instanceVOList = instanceVOList.subList(fromIndex, toIndex);
                    } else if (instanceVOList.size() - 1 >= fromIndex && instanceVOList.size() <= toIndex) {
                        instanceVOList = instanceVOList.subList(fromIndex, instanceVOList.size());
                    } else {
                        //如果instanceVOList.size() - 1 < fromIndex
                        instanceVOList.clear();
                    }
                }
            } else {
                pagination.setTotalCount(orderDtoPagination.getTotalCount());
            }
            pagination.setLstObject(instanceVOList);
            AssignResourceFieldServiceImp assignResourceFieldServiceImp = new AssignResourceFieldServiceImp(userContext);
            List<AssignResourceField> assignResourceFieldList = assignResourceFieldServiceImp.queryAssignResourceFieldByCategory(activityCode);

            if (!assignResourceFieldList.isEmpty()) {
                List<AssignResourceField> assignResourceFieldValueList = assignResourceFieldServiceImp.queryAssignResourceFieldValue("DynamicFormFieldValue");
                List<String> fieldNames = assignResourceFieldList.stream().map(assignResourceField -> assignResourceField.getValue()).collect(Collectors.toList());
                List<String> patientSerList = new ArrayList<>();
                List<Long> patientSerLongList = new ArrayList<>();
                instanceVOList.forEach(activityInstanceVO -> {
                    patientSerList.add(activityInstanceVO.getPatientSer());
                    patientSerLongList.add(Long.parseLong(activityInstanceVO.getPatientSer()));
                });

                DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = new DynamicFormInstanceServiceImp(userContext);
                Map<String, Map<String, String>> patientSerAndFieldNamePairs = sortFieldMap(assignResourceFieldList, dynamicFormInstanceServiceImp.queryFieldsValueByPatientSerListAndFieldNames(patientSerList, fieldNames));
                // Field value -> 中文
                if (patientSerAndFieldNamePairs != null && !patientSerAndFieldNamePairs.isEmpty()) {
                    Collection<Map<String, String>> values = patientSerAndFieldNamePairs.values();
                    for (Map<String, String> valueMap : values) {
                        if (valueMap != null) {
                            Iterator<Map.Entry<String, String>> it = valueMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry<String, String> entry = it.next();
                                if ("true".equalsIgnoreCase(entry.getValue())) {
                                    Optional<AssignResourceField> assignResourceFieldOptional = assignResourceFieldValueList.stream().filter(assignResourceField -> entry.getKey().equals(assignResourceField.getName())).findFirst();
                                    if (assignResourceFieldOptional.isPresent()) {
                                        entry.setValue(assignResourceFieldOptional.get().getValue());
                                    }
                                } else if ("false".equalsIgnoreCase(entry.getValue())) {
                                    entry.setValue("");
                                }
                            }
                        }
                    }

                    // <activityCode, fieldName> - Current Activity need which
                    // resource, and set to which target field.
                    String sourceActivityCode = ActivityCodesReader.getActivityCode(activityCode).getSourceActivityCode();
                    String targetFieldName = ActivityCodesReader.getActivityCode(activityCode).getTargetFieldName();

                    AssignResourceServiceImp service = new AssignResourceServiceImp(userContext);

                    // get assignedResource By activityCode
                    final Map<Long, String> assignResourceMapPatientResourceId = service.getPatientSerResourceMapByPatientSerList(patientSerLongList, sourceActivityCode);

                    DeviceAntiCorruptionServiceImp deviceFhirservice = new DeviceAntiCorruptionServiceImp();
                    instanceVOList.forEach(instanceVO -> {
                        String patientSer = instanceVO.getPatientSer();
                        Long patientSerLong = Long.parseLong(patientSer);
                        instanceVO.setDynamicField(patientSerAndFieldNamePairs.get(patientSer));
                        String resourceId = "";
                        if (assignResourceMapPatientResourceId != null) {
                            resourceId = assignResourceMapPatientResourceId.get(patientSerLong);
                        }

                        if (StringUtils.isNotBlank(resourceId)) {
                            DeviceDto fhirDevice = deviceFhirservice.queryDeviceByID(resourceId);
                            if (fhirDevice != null) {
                                instanceVO.addDynamicField(targetFieldName, fhirDevice.getName());
                            }
                        }

                        // Assign Resource (Device & TPS) - 设置医师的大分组名称
                        instanceVO.setPhysicianGroupName(GroupPractitionerHelper.getTopGroupName(GroupPractitionerHelper.getOncologyGroupTreeNode(), instanceVO.getPhysicianId()));
                    });
                }
            }

        } else if (StringUtils.equalsIgnoreCase(ActivityTypeEnum.APPOINTMENT.name(), activityType)) {
            List<String> supportCarePathTemplateNameList = new ArrayList<>();
            String defaultTemplateName = configuration.getDefaultCarePathTemplateName();
            supportCarePathTemplateNameList.add(defaultTemplateName);
            List<CarePathConfigItem> carePathConfigItems = this.configuration.getCarePathConfig().getCarePath();
            if(carePathConfigItems != null){
                carePathConfigItems.forEach(carePathConfigItem -> supportCarePathTemplateNameList.add(carePathConfigItem.getTemplateId()));
            }
            String relateActivityCode = null;
            CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
            for(String templateId : supportCarePathTemplateNameList){
                CarePathTemplate carePathTemplate = carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(templateId);
                if(carePathTemplate != null){
                    Optional<PlannedActivity> plannedActivityOptional = carePathTemplate.getActivities().stream().filter(plannedActivity -> {
                        List<String> deviceIdList = plannedActivity.getDeviceIDs();
                        if(deviceIdList != null){
                            return deviceIdList.contains(deviceId);
                        }
                        return false;
                    }).findFirst();
                    if(plannedActivityOptional.isPresent()){
                        relateActivityCode = plannedActivityOptional.get().getActivityCode();
                        break;
                    }
                }
            }
            if(relateActivityCode != null){
                activityCode = ActivityCodesReader.getActivityCode(relateActivityCode).getRelativeCode();
            }
            String treatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
            boolean appointmentStoredToLocal = false;
            if (activityCode.equals(treatmentActivityCode)) {
                appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
            }
            Pagination<AppointmentDto> appointmentDtoPagination;
            if (!appointmentStoredToLocal) {
                appointmentDtoPagination = getAppointmentDtos(hisId, activityCode, deviceId, startDate, endDate, sort, countPerPage, pageNumber);
            } else {
                appointmentDtoPagination = getAppointmentDtosFromLocal(hisId, deviceId, startDate, endDate, sort, countPerPage, pageNumber, userContext);
            }
            if (appointmentDtoPagination == null || appointmentDtoPagination.getLstObject() == null || appointmentDtoPagination.getLstObject().isEmpty()) {
                log.debug("Not found appointment by activity code[{}]", activityCode);

                return Response.ok(instanceVOList).build();
            }

            if (log.isDebugEnabled()) {
                log.debug("appointmentDtoPagination TotalCount: [{}]", appointmentDtoPagination.getTotalCount());
                int debugIndex = 0;
                for (AppointmentDto object : appointmentDtoPagination.getLstObject()) {
                    log.debug("AppointmentDto[{}]: {}", debugIndex++, object.toString());
                }
            }

//          去除在waiting和calling list中的预约信息
            QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl();
            QueuingManagementDTO param = new QueuingManagementDTO();
            param.setActivityCode(activityCode);
            try {
                String tmpStartDate = startDate;
                String tmpEndDate = endDate;
                if (!(isNotEmpty(tmpStartDate) && isNotEmpty(tmpEndDate))) {
                    String curDate = DateUtil.getCurrentDate();
                    tmpStartDate = curDate;
                    tmpEndDate = curDate;
                }
                param.setCheckInStartTime(DateUtil.parse(tmpStartDate));
                param.setCheckInEndTime(DateUtil.parse(tmpEndDate));
                param.setCheckInStatusList(Arrays.asList(CheckInStatusEnum.WAITING, CheckInStatusEnum.CALLING, CheckInStatusEnum.CALLED));
            } catch (ParseException e) {
                log.error("ParseException: {}", e.getMessage());
            }
            List<QueuingManagementVO> list = queuingManagementService.queryCheckInList(param);
            if (!list.isEmpty()) {
                list.forEach(queuingManagementDTO -> {
                    Iterator<AppointmentDto> it = appointmentDtoPagination.getLstObject().iterator();
                    while (it.hasNext()) {
                        if (it.next().getAppointmentId().equals(queuingManagementDTO.getAppointmentId())) {
                            it.remove();
                            break;
                        }
                    }
                });
            }
            ActivityInstanceForAppointmentAssembler appointmentAssembler =
                    new ActivityInstanceForAppointmentAssembler(appointmentDtoPagination.getLstObject(),
                            configuration,
                            userContext);

            instanceVOList = appointmentAssembler.getActivityInstances();
            pagination.setLstObject(instanceVOList);
            pagination.setTotalCount(appointmentDtoPagination.getTotalCount());
        }
        if (StringUtils.isEmpty(countPerPage) || StringUtils.isEmpty(pageNumber)) {
            return Response.ok(instanceVOList).build();
        } else {
            return Response.ok(pagination).build();
        }
    }

    /**
     * 对patientSerAndFieldNamePairs中按照fieldList排序
     *
     * @param fieldList
     * @param patientSerAndFieldNamePairs
     * @return
     */
    private Map<String, Map<String, String>> sortFieldMap(List<AssignResourceField> fieldList, Map<String, Map<String, String>> patientSerAndFieldNamePairs) {
        Map<String, Map<String, String>> sortMap = new HashMap<>();
        if (patientSerAndFieldNamePairs != null && !patientSerAndFieldNamePairs.isEmpty()) {
            patientSerAndFieldNamePairs.entrySet().forEach(stringMapEntry -> {
                Map<String, String> sMap = new LinkedHashMap<>();
                Map<String, String> val = stringMapEntry.getValue();
                fieldList.forEach(field -> {
                    if (val.containsKey(field.getValue())) {
                        sMap.put(field.getName(), val.get(field.getValue()));
                    }
                });
                sortMap.put(stringMapEntry.getKey(), sMap);
            });
        }
        return sortMap;
    }

    /**
     * Search activity status by task id or appointment id
     *
     * @param userContext  user context
     * @param instanceId   task id or appointment id
     * @param activityType activity type, TASK or APPOINTMENT
     * @return
     */
    @Path("/activity/status/{id}")
    @GET
    public Response searchActivityStatus(@Auth UserContext userContext, @PathParam("id") String instanceId,
                                         @QueryParam("activityType") String activityType) {
        Map<String, String> activityStatusMap = new HashMap<>();
        String activityStatus = "inProcess";

        if (activityType.equalsIgnoreCase(ActivityTypeEnum.TASK.name())) {
            OrderDto orderDto = orderAntiCorruptionServiceImp.queryOrderById(instanceId);

            if (OrderStatusEnum.COMPLETED.equals(OrderStatusEnum.fromCode(orderDto.getOrderStatus()))) {
                activityStatus = "done";
            }
        } else if (activityType.equalsIgnoreCase(ActivityTypeEnum.APPOINTMENT.name())) {
            AppointmentDto appointmentDto = appointmentAntiCorruptionServiceImp.queryAppointmentById(instanceId);

            if (AppointmentStatusEnum.FULFILLED.equals(AppointmentStatusEnum.fromCode(appointmentDto.getStatus()))) {
                activityStatus = "done";
            }
        }

        activityStatusMap.put("activityStatus", activityStatus);

        return Response.ok(activityStatusMap).build();
    }

    @GET
    @Path("/activity/check-done-task")
    public Response pendingTask(@Auth UserContext userContext,
                                @QueryParam("moduleId") String moduleId,
                                @QueryParam("taskId") String taskId,
                                @QueryParam("patientSer") Long patientSer) {

        EclipseTaskService taskService = new EclipseTaskService(new UserContext());
        EclipseTask task = new EclipseTask();
        task.setModuleId(moduleId);
        task.setOrderId(taskId);
        task.setPatientSer(String.valueOf(patientSer));
        taskService.createPendingTask(task, userContext.getName());
        return Response.ok(task).build();
    }

    @Path("/activity/pre-check/{id}")
    @PUT
    public Response preCheck(@Auth UserContext userContext, @PathParam("id") String instanceId,
                             AppointmentFormDataVO vo) {
        BaseResponse res = new BaseResponse();
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        //根据当前的instanceId，获取当前task所在的CarePathInstance
        CarePathInstance carePathInstance =
                carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(String.valueOf(vo.getPatientSer()), instanceId, ActivityTypeEnum.valueOf(vo.getActivityType()));
        CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
        ActivityInstance needDoneActivity = helper.getActivityByInstanceIdAndActivityType(instanceId, vo.getActivityType());
        //当前提交的activity如果是Pending状态，說明有前序activity沒有done，返回前序activity的error code
        if (!needDoneActivity.getOriginalActiveInWorkflowFlag()) {
            ActivityInstance preActiveInWorkflowActivity = helper.getPreActiveInWorkflowActivity(needDoneActivity);
            if (preActiveInWorkflowActivity != null) {
                res.addError("error-501", ActivityCodesReader.getActivityCode(preActiveInWorkflowActivity.getActivityCode()).getContent());
            }
        }
        return Response.ok(res).build();
    }

    /**
     * Complete task or appointment
     *
     * @param userContext user context
     * @param instanceId  task id or appointment id
     * @param vo          scheduling task or appointment params
     * @return
     */
    @Path("/activity/{id}")
    @PUT
    public synchronized Response setActivityDone(@Auth UserContext userContext, @PathParam("id") String instanceId,
                                                 AppointmentFormDataVO vo) {
        boolean result = false;
        BaseResponse res = new BaseResponse();
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        //根据当前的instanceId，获取当前task所在的CarePathInstance
        CarePathInstance carePathInstance =
                carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(String.valueOf(vo.getPatientSer()), instanceId, ActivityTypeEnum.valueOf(vo.getActivityType()));
        CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
        List<ActivityInstance> nextActivities;
        ActivityInstance needDoneActivity = helper.getActivityByInstanceIdAndActivityType(instanceId, vo.getActivityType());

        ActivityCodeConfig activityCodeConfig = ActivityCodesReader.getActivityCode(needDoneActivity.getActivityCode());

        /*
        如果配置了relative code，说明此task节点有对应的appointment节点，此时，在done这个task的时候，需要schedule对应的appointment
         */
        final String relativeCode = activityCodeConfig.getRelativeCode();
        if (isNotEmpty(relativeCode)) {
            ActivityInstance relativeActivityInstance = helper.getActivityByCode(relativeCode);

            if (relativeActivityInstance == null || !ActivityTypeEnum.APPOINTMENT.equals(relativeActivityInstance.getActivityType())) {
                log.error("Bad Activity Code Config. {}>>{}", needDoneActivity.getActivityCode(), relativeCode);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
            } else {
                if (relativeActivityInstance.getActivityCode().equals(SystemConfigPool.queryTreatmentActivityCode())) {
                    ActivityServiceImp activityServiceImp = new ActivityServiceImp(userContext);
                    //如果是治疗的预约，则需要先看是否有冲突
                    List<AppointmentFormTimeDataVO> appointmentFormTimeDataVOList = activityServiceImp.checkMultiAppointmentsConflict(vo);
                    if (!appointmentFormTimeDataVOList.isEmpty()) {
//                     去除冲突的，获取需要创建的预约
//                      获取需要添加的
                        removeConflictAppointment(vo, appointmentFormTimeDataVOList);
                        res.addError("error-042", appointmentFormTimeDataVOList);
                    }
                    scheduleAppointment(vo, relativeActivityInstance, carePathAntiCorruptionServiceImp, carePathInstance, userContext);
                } else {
                    //如果不是治疗预约则直接预约
                    AppointmentDataVO appointmentDataVO = createAppointmentDataVO(vo);
                    appointmentDataVO.setActivityCode(needDoneActivity.getActivityCode());
                    appointmentDataVO.setInstanceId(instanceId);
                    res = checkAppointmentTime(appointmentDataVO, carePathInstance);
                    if (!res.getErrors().isEmpty()) {
                        //预约如果时间上早于前一个activity的结束时间则返回错误
                        return Response.ok(res).build();
                    } else {
                        scheduleAppointment(vo, relativeActivityInstance, carePathAntiCorruptionServiceImp, carePathInstance, userContext);
                    }
                }
            }
        }

        if (StringUtils.equalsIgnoreCase(ActivityTypeEnum.TASK.name(), vo.getActivityType())) {
            log.info("Done task : {}; status : {}", instanceId, needDoneActivity.getStatus());

            //如果不是completed状态，则设置成completed状态。
            if (!CarePathStatusEnum.COMPLETED.equals(needDoneActivity.getStatus())
                    && needDoneActivity.getDueDateOrScheduledStartDate() == null) {
                OrderDto theDoneOrder = new OrderDto();
                theDoneOrder.setOrderId(instanceId);
                Date dueDate = DateUtil.addMillSecond(getDueDateOfTheActivity(needDoneActivity.getId(), helper), 60 * 1000);
                log.debug("Done task[{}], due date [{}].", instanceId, dueDate);
                theDoneOrder.setDueDate(dueDate);
                orderAntiCorruptionServiceImp.updateOrder(theDoneOrder);
            }

            nextActivities = helper.getNextActivitiesByInstanceId(instanceId, ActivityTypeEnum.TASK.name());
        } else if (StringUtils.equalsIgnoreCase(ActivityTypeEnum.APPOINTMENT.name(), vo.getActivityType())) {
            log.info("Done appointment : {} status: {}", instanceId, needDoneActivity.getStatus());

            if (!CarePathStatusEnum.COMPLETED.equals(needDoneActivity.getStatus())) {
                AppointmentDto appointmentDto = new AppointmentDto();
                appointmentDto.setAppointmentId(instanceId);
                Date startTime = DateUtil.addMillSecond(getDueDateOfTheActivity(needDoneActivity.getId(), helper), 60 * 1000);
                Date endTime = DateUtil.addMillSecond(startTime, 60 * 1000);
                log.debug("Done appointment[{}], startTime is {}, endTime is {} ", instanceId, startTime, endTime);
                appointmentDto.setStartTime(startTime);
                appointmentDto.setEndTime(endTime);
                appointmentAntiCorruptionServiceImp.updateAppointment(appointmentDto);

//                如果是治疗预约的话，并且治疗预约存本地开关打开，则本地数据库中的预约信息需要更新
                String treatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
                if (needDoneActivity.getActivityCode().equals(treatmentActivityCode)) {
                    boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
                    if (appointmentStoredToLocal) {
                        TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(userContext);
                        treatmentAppointmentService.updateStatusByAppointmentId(needDoneActivity.getInstanceID(), AppointmentStatusEnum.fromCode(appointmentDto.getStatus()));
                    }
                }
            }

            nextActivities = helper.getNextActivitiesByInstanceId(instanceId, ActivityTypeEnum.APPOINTMENT.name());
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        //如果relative code是空的情况下
        if (StringUtils.equalsIgnoreCase(ActivityTypeEnum.TASK.name(), vo.getActivityType())
                && !CarePathStatusEnum.COMPLETED.equals(needDoneActivity.getStatus())) {
            OrderDto theDoneOrder = new OrderDto();
            theDoneOrder.setOrderId(instanceId);
            theDoneOrder.setOwnerId(String.valueOf(userContext.getLogin().getResourceSer()));
            theDoneOrder.setOrderStatus(OrderStatusEnum.getDisplay(OrderStatusEnum.COMPLETED));
            theDoneOrder.setParticipants(Arrays.asList(new ParticipantDto() {{
                setType(ParticipantTypeEnum.PRACTITIONER);
                setParticipantId(String.valueOf(userContext.getLogin().getResourceSer()));
            }}));
            orderAntiCorruptionServiceImp.updateOrder(theDoneOrder);
        } else if (StringUtils.equalsIgnoreCase(ActivityTypeEnum.APPOINTMENT.name(), vo.getActivityType())
                && !CarePathStatusEnum.COMPLETED.equals(needDoneActivity.getStatus())) {
            AppointmentDto appointmentDto = new AppointmentDto();
            appointmentDto.setAppointmentId(instanceId);
            appointmentDto.setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED));
            appointmentAntiCorruptionServiceImp.updateAppointment(appointmentDto);
        }
        result = true;
        if ((nextActivities != null) && !nextActivities.isEmpty()) {
            //对所有的next activities进行schedule

            CarePathTemplate carePathTemplate = carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(carePathInstance.getCarePathTemplateId());
            for (ActivityInstance eachNextActivity : nextActivities) {
                // check if all pre activities of each next activity are done.
                if (!helper.isAllPreActivitiesDoneOfEachNextActivity(instanceId, eachNextActivity)) {
                    break;
                }
                if (ActivityTypeEnum.TASK.equals(eachNextActivity.getActivityType())
                        && StringUtils.isEmpty(eachNextActivity.getInstanceID())) {
                    OrderDto newOrderDto = new OrderDto();
                    newOrderDto.setOrderType(eachNextActivity.getActivityCode());
                    newOrderDto.setOrderStatus(OrderStatusEnum.getDisplay(OrderStatusEnum.READY));
                    newOrderDto.setOrderGroup(eachNextActivity.getDefaultGroupID());
                    //如果是主治的task，勾选后产生的task在ARIA里会自动设置一个owner为主治医生，所以就不需要在这里再设置一遍，否则在update这个task时fhir会抛异常
                    if (!isAutoAssignPrimaryPhysician(eachNextActivity.getActivityCode(), carePathTemplate)) {
                        newOrderDto.setOwnerId(eachNextActivity.getDefaultGroupID());
                    }
                    newOrderDto.setDueDate(DateUtil.addMillSecond(getDueDateOfTheActivity(eachNextActivity.getId(), helper), 1 * 1000));

                    List<ParticipantDto> participantList = new ArrayList<>();
                    ParticipantDto patient = new ParticipantDto(ParticipantTypeEnum.PATIENT,
                            String.valueOf(vo.getPatientSer()));
                    participantList.add(patient);
                    newOrderDto.setParticipants(participantList);
                    log.debug("Schedule next order of id[{}]: activityCode[{}], status[{}], group[{}], dueDate[{}], patient[{}]",
                            eachNextActivity.getId(), newOrderDto.getOrderType(),
                            newOrderDto.getOrderStatus(), newOrderDto.getOrderGroup(),
                            newOrderDto.getDueDate(), String.valueOf(vo.getPatientSer()));
                    String id = carePathAntiCorruptionServiceImp.scheduleNextTask(carePathInstance.getId(),
                            eachNextActivity.getId(),
                            newOrderDto);
                    result = isNotEmpty(id);
                }
            }
        }

        //Link new optional carepath like MRI
        //TODO: Future client will give the context of link carepath.
        LinkCPInDynamicFormConfigService.linkOptionalCP(userContext, needDoneActivity.getActivityCode(), vo.getPatientSer());

        // Release specified resource by activity code. Blank would release all resource only for finishing treatment.
        String releaseResourceForActivity = activityCodeConfig.getReleaseResourceForActivity();
        if (StringUtils.isNotBlank(releaseResourceForActivity)) {
            releaseAssignedResource(releaseResourceForActivity, vo.getPatientSer(), userContext);
        }

        //删除之前加的task锁
        TaskLockingServiceImpl taskLockingService = new TaskLockingServiceImpl(userContext);
        TaskLockingDto taskLockingDto = new TaskLockingDto(instanceId, needDoneActivity.getActivityType().name(), userContext.getName(), null, null, null);
        taskLockingService.unLockTask(taskLockingDto);
        if (!result) {
            res.addError("error-041", result);
            return Response.ok(result).build();
        }

        return Response.ok(res).build();
    }

    private boolean isAutoAssignPrimaryPhysician(String activityCode, CarePathTemplate carePathTemplate) {
        for (PlannedActivity plannedActivity : carePathTemplate.getActivities()) {
            if (StringUtils.equalsIgnoreCase(activityCode, plannedActivity.getActivityCode())) {
                return plannedActivity.getAutoAssignPrimaryOncologist();
            }
        }
        return false;
    }

    private AppointmentDataVO createAppointmentDataVO(AppointmentFormDataVO appointmentFormDataVO) {
        AppointmentDataVO appointmentDataVO = new AppointmentDataVO() {{
            setPatientSer(String.valueOf(appointmentFormDataVO.getPatientSer()));
            setActivityType(appointmentFormDataVO.getActivityType());
            setDeviceId(appointmentFormDataVO.getDeviceId());
            setAppointTimeList(new ArrayList<>());
        }};
        appointmentFormDataVO.getAppointTimeList().forEach(slot -> {
            appointmentDataVO.getAppointTimeList().add(new AppointmentDataTimeSlotVO() {{
                setEndTime(slot.getEndTime());
                setStartTime(slot.getStartTime());
                setAppointmentId(slot.getAppointmentId());
                setAction(slot.getAction());
            }});
        });
        return appointmentDataVO;
    }


    private BaseResponse checkAppointmentTime(AppointmentDataVO vo, CarePathInstance carePathInstance) {
        BaseResponse res = new BaseResponse();
        List<ActivityInstance> activityInstanceList = carePathInstance.getOriginalActivityInstances();
//      get appointment from activityInstanceList
        List<ActivityInstance> appointmentAi = new ArrayList<>();
        activityInstanceList.forEach(activityInstance -> {
            if (activityInstance.getActivityType().equals(ActivityTypeEnum.APPOINTMENT)) {
                appointmentAi.add(activityInstance);
            }
        });

        Pagination<AppointmentDto> pagination;
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();

        int countPerPage = Integer.MAX_VALUE;
        AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
        pagination = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(String.valueOf(vo.getPatientSer()), null, null, Arrays.asList(
                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED)),
                countPerPage, 1, Integer.MAX_VALUE);
        if (pagination != null && pagination.getLstObject().size() > 0) {
            pagination.getLstObject().forEach(appointmentDto -> {
                appointmentAi.forEach(activityInstance -> {
                    if (appointmentDto.getAppointmentId().equals(activityInstance.getInstanceID())) {
                        appointmentDtoList.add(appointmentDto);
                    }
                });
            });
        }

        Iterator<AppointmentDataTimeSlotVO> it = vo.getAppointTimeList().iterator();
        while (it.hasNext()) {
            if (StringUtils.isNotEmpty(it.next().getAppointmentId())) {
                it.remove();
            }
        }
        Collections.sort(vo.getAppointTimeList());
        // 获取预约时间最近的一个预约
        AppointmentDataTimeSlotVO firstTimeSlotVO = vo.getAppointTimeList().get(0);
        // 如果firstTimeSlotVO 的开始时间小于等于latestAppointmentDto的结束时间，则返回预约时间错误的信息
        Date starTime = new Date();
        String startTimeStr = firstTimeSlotVO.getStartTime();
        try {
            starTime = DateUtil.parse(startTimeStr);
        } catch (ParseException e) {
            log.error("Date ParseException: {}, Item: {}", e.getMessage(), startTimeStr);
            res.addError("error-099", startTimeStr);

        }

        if (!appointmentDtoList.isEmpty()) {
            appointmentDtoList.sort(Comparator.comparing(AppointmentDto::getEndTime));
            AppointmentDto latestAppointmentDto = appointmentDtoList.get(appointmentDtoList.size() - 1);

            if (starTime.compareTo(latestAppointmentDto.getEndTime()) <= 0) {
                firstTimeSlotVO.setActName(ActivityCodesReader.getActivityCode(vo.getActivityCode()).getEntryContent());
                firstTimeSlotVO.setConflictActName(ActivityCodesReader.getSourceActivityCodeByRelativeCode(latestAppointmentDto.getReason()).getEntryContent());
                // 预约时间早于最近一个预约
                res.addError("error-030", firstTimeSlotVO);
            }
        }
        if (starTime.before(new Date())) {
            // 预约时间早于当前时间，不可预约
            res.addError("error-031", firstTimeSlotVO);
        }
        return res;
    }

    /**
     * 去除不合法的预约项目
     *
     * @param vo
     * @param appointmentFormTimeDataVOList
     */
    private void removeConflictAppointment(AppointmentFormDataVO vo, List<AppointmentFormTimeDataVO> appointmentFormTimeDataVOList) {
        List<AppointmentFormTimeDataVO> appointmentFormTimeDataVOS = vo.getAppointTimeList();
        if (appointmentFormTimeDataVOS != null) {
            Iterator<AppointmentFormTimeDataVO> it = appointmentFormTimeDataVOS.iterator();
            while (it.hasNext()) {
                AppointmentFormTimeDataVO timeDataVO = it.next();
                if (appointmentFormTimeDataVOList.contains(timeDataVO)) {
                    it.remove();
                }
            }
        }
    }

    private void releaseAssignedResource(String activityCode, Long patientSer, UserContext userContext) {
        AssignResourceServiceImp service = new AssignResourceServiceImp(userContext);
        service.deleteAssignedResource(patientSer, activityCode);
    }

    /**
     * Get assign tps summary
     *
     * @param userContext user context
     * @return tps summary
     */
    @Path("/activity/assign-resource/summary")
    @GET
    public Response listAssignResourceSummary(@Auth UserContext userContext, @QueryParam("activityCode") String activityCode) {
        AssignResourceServiceImp service = new AssignResourceServiceImp(userContext);
        List<AssignResourceVO> assignResourceList = service.listAssignResourceSummary(activityCode);
        return Response.ok(assignResourceList).build();
    }

    @Path("/activity/assign-resource/assign")
    @PUT
    public Response assignActivityResource(@Auth UserContext userContext, @QueryParam("activityCode") String activityCode,
                                           List<AssignResourceVO> assignResourceVOList) {
        AssignResourceServiceImp assignResourceServiceImp = new AssignResourceServiceImp(userContext);
        ActivityServiceImp activityServiceImp = new ActivityServiceImp(userContext);

        boolean result = false;
        if (!assignResourceVOList.isEmpty()) {
            List<AssignResource> assignResources = new ArrayList<>();
            List<AssignResource> failAssignResources = new ArrayList<>();

            assignResourceVOList.forEach(assignResourceVO -> {
                List<Map<String, String>> patientSerInstanceIdPairList = assignResourceVO.getPatientSerInstanceIdPairList();


                if (patientSerInstanceIdPairList != null && !patientSerInstanceIdPairList.isEmpty()) {
                    patientSerInstanceIdPairList.forEach(pair -> {
                        String patientSer = pair.get(AssignResourceServiceImp.PATIENT_SER_KEY);
                        String orderId = pair.get(AssignResourceServiceImp.ORDER_ID_KEY);

                        AssignResource assignResource = new AssignResource();
                        assignResource.setResourceId(assignResourceVO.getId());
                        assignResource.setActivityCode(activityCode);
                        assignResource.setPatientSer(Long.parseLong(patientSer));

                        if (StringUtils.isNotBlank(patientSer) && StringUtils.isNotBlank(orderId)) {

                            if (activityServiceImp.setTaskDone(patientSer, orderId, userContext)) {
                                Encounter encounter = encounterServiceImp.queryByPatientSer(Long.parseLong(patientSer));
                                if (encounter != null) {
                                    assignResource.setEncounterId(Long.parseLong(encounter.getId()));
                                }
                                assignResources.add(assignResource);
                            } else {
                                failAssignResources.add(assignResource);
                            }
                        }
                    });
                }

            });
            result = assignResourceServiceImp.assignPatient2Resource(assignResources);

            if (!failAssignResources.isEmpty()) {
                failAssignResources.forEach(assignResource -> {
                    log.error("Save assign resource has been failed, PatientSer {}, resourceId {} ", assignResource.getPatientSer(), assignResource.getResourceId());
                });
            }

        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    private Date getDueDateOfTheActivity(String activityId, CarePathInstanceHelper helper) {
        Date theDueDateOfTheTask = new Date();
        List<ActivityInstance> prevActivities = helper.getPrevActivities(activityId);

        if ((prevActivities != null) && !prevActivities.isEmpty()) {
            for (ActivityInstance activityInstance : prevActivities) {
                theDueDateOfTheTask = calculateDueDate(theDueDateOfTheTask,
                        activityInstance);
            }
        }

        return theDueDateOfTheTask;
    }

    /**
     * 预约
     *
     * @param vo
     * @param relativeActivityInstance
     * @param carePathAntiCorruptionServiceImp
     * @param carePathInstance
     * @param userContext
     */
    private void scheduleAppointment(AppointmentFormDataVO vo, ActivityInstance relativeActivityInstance,
                                     CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp, CarePathInstance carePathInstance,
                                     UserContext userContext) {
//      非技师预约，需要将之前的预约过滤掉
        List<AppointmentFormTimeDataVO> timeDataVOList = vo.getAppointTimeList();
        if (timeDataVOList != null && !timeDataVOList.isEmpty()) {
            Iterator<AppointmentFormTimeDataVO> it = timeDataVOList.iterator();
            while (it.hasNext()) {
                AppointmentFormTimeDataVO dvo = it.next();
                if (StringUtils.isNotEmpty(dvo.getAppointmentId())) {
                    it.remove();
                }
            }
        }
        Collections.sort(vo.getAppointTimeList());
        /**
         * 特殊节点：治疗
         */
        String treatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
        if (relativeActivityInstance.getActivityCode().equals(treatmentActivityCode)) {
            boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
            if (!appointmentStoredToLocal) {//预约信息保存到fhir
                scheduleTreatmentAppointment(vo, relativeActivityInstance, carePathAntiCorruptionServiceImp, carePathInstance);
            } else {//预约信息保存到本地
                scheduleTreatmentAppointment2Local(vo, relativeActivityInstance, userContext);
            }
        } else {
            //非特殊节点预约保存到fhir
            scheduleNormalAppointment(vo, relativeActivityInstance, carePathAntiCorruptionServiceImp, carePathInstance);
        }
    }

    /**
     * 非特殊节点预约
     *
     * @param vo
     * @param relativeActivityInstance
     * @param carePathAntiCorruptionServiceImp
     * @param carePathInstance
     */
    private void scheduleNormalAppointment(AppointmentFormDataVO vo, ActivityInstance relativeActivityInstance, CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp, CarePathInstance carePathInstance) {
        //前端有可能把已完成和已预约的记录一块提交过来，所以对于非治疗的节点需要过滤掉已完成的预约
        AppointmentFormTimeDataVO newSchedule = null;
        for (AppointmentFormTimeDataVO data : vo.getAppointTimeList()) {
            if (isEmpty(data.getAppointmentId())) {
                newSchedule = data;
                break;
            }
        }
        if (newSchedule != null) {
            AppointmentDto newAppointmentDto = createAppointmentDto(vo,
                    newSchedule,
                    relativeActivityInstance.getActivityCode(),
                    AppointmentStatusEnum.getDisplay(
                            AppointmentStatusEnum.BOOKED));
            carePathAntiCorruptionServiceImp.scheduleNextAppointment(carePathInstance.getId(),
                    relativeActivityInstance.getId(),
                    newAppointmentDto);
        } else {
            log.error("The appointment data is error. ");
        }
    }

    /**
     * 护士在特殊节点预约到本地数据库
     *
     * @param vo
     * @param relativeActivityInstance
     * @param userContext
     */
    private void scheduleTreatmentAppointment2Local(AppointmentFormDataVO vo, ActivityInstance relativeActivityInstance, UserContext userContext) {
        for (int i = 0; i < vo.getAppointTimeList().size(); i++) {
            AppointmentDto newAppointmentDto = createAppointmentDto(vo,
                    vo.getAppointTimeList().get(i),
                    relativeActivityInstance.getActivityCode(),
                    AppointmentStatusEnum.getDisplay(
                            AppointmentStatusEnum.BOOKED));
            TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(userContext);
            TreatmentAppointmentDTO treatmentAppointmentDTO = TreatmentAppointmentAssembler.appointmentDto2TreatmentAppointmentDTO(newAppointmentDto, vo, null);
            treatmentAppointmentDTO.setEncounterId(encounterServiceImp.queryByPatientSer(vo.getPatientSer()).getId());
            String id = treatmentAppointmentService.create(treatmentAppointmentDTO);
            if (StringUtils.isEmpty(id)) {
                log.error("Local createAppointment: fail!");
            }
        }
    }

    /**
     * 护士在特殊节点预约到fhir
     *
     * @param vo
     * @param relativeActivityInstance
     * @param carePathAntiCorruptionServiceImp
     * @param carePathInstance
     */
    private void scheduleTreatmentAppointment(AppointmentFormDataVO vo, ActivityInstance relativeActivityInstance, CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp, CarePathInstance carePathInstance) {
        AppointmentDto newAppointmentDto = createAppointmentDto(vo,
                vo.getAppointTimeList().get(0),
                relativeActivityInstance.getActivityCode(),
                AppointmentStatusEnum.getDisplay(
                        AppointmentStatusEnum.BOOKED));
        carePathAntiCorruptionServiceImp.scheduleNextAppointment(carePathInstance.getId(),
                relativeActivityInstance.getId(),
                newAppointmentDto);
//                  是治疗节点      第二个及以后的预约，只是在Activity上创建Appointment，和CarePathInstance中的ActivityInstance没有关系
        for (int i = 1; i < vo.getAppointTimeList().size(); i++) {
            newAppointmentDto = createAppointmentDto(vo,
                    vo.getAppointTimeList().get(i),
                    relativeActivityInstance.getActivityCode(),
                    AppointmentStatusEnum.getDisplay(
                            AppointmentStatusEnum.BOOKED));
            appointmentAntiCorruptionServiceImp.createAppointment(newAppointmentDto);
        }
    }


    /**
     * @param userContext  user context
     * @param activityCode
     * @param vo
     * @return
     */
    @Path("/activity/multiappoint")
    @POST
    public synchronized Response modifyMultipleTreatmentAppointment(@Auth UserContext userContext,
                                                                    @QueryParam("activityCode") String activityCode,
                                                                    AppointmentFormDataVO vo) {
        Collections.sort(vo.getAppointTimeList());
        ActivityServiceImp activityServiceImp = new ActivityServiceImp(userContext);
        List<AppointmentFormTimeDataVO> appointmentFormTimeDataVOList = activityServiceImp.checkMultiAppointmentsConflict(vo);
        if (!appointmentFormTimeDataVOList.isEmpty()) {
//          去除冲突的，获取需要创建的预约
//          获取需要添加的
            removeConflictAppointment(vo, appointmentFormTimeDataVOList);
        }
        String treatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
        if (activityCode.equals(treatmentActivityCode)) {
//      将取消和非取消的分成两个List
            List<AppointmentFormTimeDataVO> cancelList = new ArrayList<>();
            List<AppointmentFormTimeDataVO> notCancelList = new ArrayList<>();
            vo.getAppointTimeList().forEach(appointmentFormTimeDataVO -> {
                if (AppointmentActionEnum.DELETED.ordinal() == appointmentFormTimeDataVO.getAction()) {//删除
                    cancelList.add(appointmentFormTimeDataVO);
                } else {
                    notCancelList.add(appointmentFormTimeDataVO);
                }
            });
            boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
            if (!appointmentStoredToLocal) {
                CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
                CarePathInstance carePathInstance =
                        carePathAntiCorruptionServiceImp.queryLastCarePathByPatientIDAndActivityCode(String.valueOf(vo.getPatientSer()), activityCode);
                CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
                ActivityInstance activityInstance = helper.getActivityByCode(activityCode);
//      The appointment id which relation to the carePath instance
                String appointmentId = activityInstance.getInstanceID();
                if (StringUtils.isNotEmpty(appointmentId)) {
                    multipleTreatmentAppointment(activityCode, vo, cancelList, notCancelList, appointmentId);
                } else {
//           not appointment any treatment
                    return Response.ok(false).build();
                }
            } else {
                multipleTreatmentAppointment2Local(userContext, activityCode, vo, cancelList, notCancelList);
            }
        }
        return Response.ok(appointmentFormTimeDataVOList).build();
    }

    /**
     * @param userContext
     * @param vo
     * @return
     */
    @Path("/activity/checkRecurringAppointments")
    @POST
    public Response checkRecurringAppointments(@Auth UserContext userContext,
                                               AppointmentFormDataVO vo) {
        if (vo.getAppointTimeList().isEmpty() || StringUtils.isEmpty(vo.getDeviceId())) {
            log.error("deviceId or appointment time list can not be blank");
            return Response.ok(false).build();
        }

        if (log.isDebugEnabled()) {
            log.debug("deviceId: [{}]", vo.getDeviceId());
        }

        ActivityServiceImp activityServiceImp = new ActivityServiceImp(userContext);

        List<AppointmentFormTimeDataVO> failToScheduleList = activityServiceImp.checkMultiAppointmentsConflict(vo);

        return Response.ok(failToScheduleList).build();
    }

    @Path("/activity/treatmentNum/{patientSer}")
    @GET
    public Response getTreatmentAppointNum(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer) {
        boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
        int completedSize = 0;
        int totalNum = 0;
        if (!appointmentStoredToLocal) {
            List<AppointmentDto> appointmentDtoList = new ArrayList<>();
            Pagination<AppointmentDto> pagination = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeWithPaging(String.valueOf(patientSer), SystemConfigPool.queryTreatmentActivityCode(), Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
            if (pagination.getLstObject() != null && !pagination.getLstObject().isEmpty()) {
                appointmentDtoList.addAll(pagination.getLstObject());
            }
            totalNum = appointmentDtoList.size();
            final int[] completed = {0};
            appointmentDtoList.forEach(appointmentDto -> {
                if (AppointmentStatusEnum.fromCode(appointmentDto.getStatus()).equals(AppointmentStatusEnum.FULFILLED)) {
                    completed[0]++;
                }
            });
            completedSize = completed[0];
        } else {
            TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(userContext);
            Map<String, Integer> map = treatmentAppointmentService.queryTotalAndCompletedTreatment(patientSer, null, null);
            if (map != null && !map.isEmpty()) {
                totalNum = map.get("totalNum");
                completedSize = map.get("completedNum");
            }

        }
        List<KeyValuePair> rlist = Arrays.asList(new KeyValuePair("totalNum", totalNum + ""), new KeyValuePair("completedNum", completedSize + ""));
        return Response.ok(rlist).build();
    }


    /**
     * 治疗技师多次预约保存到本地
     *
     * @param userContext
     * @param activityCode
     * @param vo
     * @param cancelList
     * @param notCancelList
     */
    private void multipleTreatmentAppointment2Local(@Auth UserContext userContext, @QueryParam("activityCode") String activityCode, AppointmentFormDataVO vo, List<AppointmentFormTimeDataVO> cancelList, List<AppointmentFormTimeDataVO> notCancelList) {
        //存储到本地的数据库
//      将取消和非取消的分成两个List
        TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(userContext);
        TreatmentAppointmentDTO firstTreatmentAppointDTO = null;
        List<TreatmentAppointmentDTO> list = treatmentAppointmentService.queryByPatientSerAndStatus(vo.getPatientSer(), Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED));
        if (!list.isEmpty()) {
            firstTreatmentAppointDTO = list.get(0);
        }
        if (firstTreatmentAppointDTO == null) {
            firstTreatmentAppointDTO = new TreatmentAppointmentDTO();
        }
        AppointmentDto firstAppointmentDto = TreatmentAppointmentAssembler.treatmentAppointmentDTO2AppointmentDto(firstTreatmentAppointDTO, false);

//            如果取消的预约中包含第一个预约，则此预约不能取消，只能更新为最近的一次预约，并将最近的一次预约取消
        AppointmentFormTimeDataVO firstAppointment = null;
        try {
            for (AppointmentFormTimeDataVO formTimeDataVo : cancelList) {
                if (firstAppointmentDto.getStartTime().equals(DateUtil.parse(formTimeDataVo.getStartTime())) &&
                        firstAppointmentDto.getEndTime().equals(DateUtil.parse(formTimeDataVo.getEndTime()))) {
                    firstAppointment = formTimeDataVo;
                    cancelList.remove(formTimeDataVo);
                    break;
                }
            }
        } catch (ParseException e) {
            log.error("ParseException: {}", e.getMessage());
        }
        if (firstAppointment != null) {
//                从 未取消 的预约中获取第一个预约
            AppointmentFormTimeDataVO appointmentFormTimeDataVO;
            if (!notCancelList.isEmpty()) {
//                    如果存在未取消的预约
                appointmentFormTimeDataVO = notCancelList.remove(0);
                AppointmentDto updateAppointmentDto = createAppointmentDto(vo,
                        appointmentFormTimeDataVO, activityCode,
                        AppointmentStatusEnum.getDisplay(
                                AppointmentStatusEnum.BOOKED));
                updateAppointmentDto.setAppointmentId(firstAppointment.getAppointmentId());
                if (StringUtils.isNotEmpty(firstAppointment.getAppointmentId())) {
                    appointmentAntiCorruptionServiceImp.updateAppointment(updateAppointmentDto);
                }
//                     此预约已经更新到第一个预约上，所以需要将其取消
                cancelList.add(appointmentFormTimeDataVO);
            } else {
//                    如果只有一个预约，并且被取消了，那么此节点会被取消
                cancelList.add(firstAppointment);
            }
        }
        cancelList.forEach((AppointmentFormTimeDataVO appointmentFormTimeDataVO) -> {
//                取消预约
            AppointmentDto newAppointmentDto = createAppointmentDto(vo,
                    appointmentFormTimeDataVO,
                    activityCode,
                    AppointmentStatusEnum.getDisplay(
                            AppointmentStatusEnum.CANCELLED));
            newAppointmentDto.setAppointmentId(appointmentFormTimeDataVO.getAppointmentId());
            if (StringUtils.isNotEmpty(newAppointmentDto.getAppointmentId())) {
                appointmentAntiCorruptionServiceImp.updateAppointment(newAppointmentDto);
            }
            treatmentAppointmentService.updateStatusByPatientSerAndActivityAndStartTimeAndEndTime(vo.getPatientSer(), activityCode, newAppointmentDto.getStartTime(), newAppointmentDto.getEndTime(), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.CANCELLED));
        });
//                如果非删除的预约中，如果保存过得，不需要重新保存。
        List<TreatmentAppointmentDTO> treatmentAppointmentDTOList = treatmentAppointmentService.queryByPatientSerAndStatus(vo.getPatientSer(), Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED));
        final String encounterId = encounterServiceImp.queryByPatientSer(vo.getPatientSer()).getId();
        notCancelList.forEach(appointmentFormTimeDataVO -> {
            if (StringUtils.isEmpty(appointmentFormTimeDataVO.getAppointmentId())) {
                AppointmentDto newAppointmentDto = createAppointmentDto(vo,
                        appointmentFormTimeDataVO,
                        activityCode,
                        AppointmentStatusEnum.getDisplay(
                                AppointmentStatusEnum.BOOKED));
                TreatmentAppointmentDTO tmp = TreatmentAppointmentAssembler.appointmentDto2TreatmentAppointmentDTO(newAppointmentDto, vo, null);
                tmp.setEncounterId(encounterId);
                if (!treatmentAppointmentDTOListContainDto(treatmentAppointmentDTOList, tmp)) {
                    treatmentAppointmentService.create(tmp);
                }
            }
        });
    }

    /**
     * 治疗技师多次预约保存到fhir
     *
     * @param activityCode
     * @param vo
     * @param cancelList
     * @param notCancelList
     * @param appointmentId
     */
    private void multipleTreatmentAppointment(@QueryParam("activityCode") String activityCode, AppointmentFormDataVO vo, List<AppointmentFormTimeDataVO> cancelList, List<AppointmentFormTimeDataVO> notCancelList, String appointmentId) {
        //            如果取消的预约中包含第一个预约，则此预约不能取消，只能更新为最近的一次预约，并将最近的一次预约取消
        AppointmentFormTimeDataVO firstAppointment = null;
        for (AppointmentFormTimeDataVO formTimeDataVo : cancelList) {
            if (appointmentId.equals(formTimeDataVo.getAppointmentId())) {
                firstAppointment = formTimeDataVo;
                cancelList.remove(formTimeDataVo);
                break;
            }
        }

        if (firstAppointment != null) {
//                从 未取消 的预约中获取第一个预约
            AppointmentFormTimeDataVO appointmentFormTimeDataVO;
            if (!notCancelList.isEmpty()) {
//                    如果存在未取消的预约
                appointmentFormTimeDataVO = notCancelList.remove(0);
                AppointmentDto updateAppointmentDto = createAppointmentDto(vo,
                        appointmentFormTimeDataVO, activityCode,
                        AppointmentStatusEnum.getDisplay(
                                AppointmentStatusEnum.BOOKED));
                updateAppointmentDto.setAppointmentId(firstAppointment.getAppointmentId());
                appointmentAntiCorruptionServiceImp.updateAppointment(updateAppointmentDto);
                if (StringUtils.isNotEmpty(appointmentFormTimeDataVO.getAppointmentId())) {
//                     此预约已经更新到第一个预约上，所以需要将其取消
                    cancelList.add(appointmentFormTimeDataVO);
                }
            } else {
//                    如果只有一个预约，并且被取消了，那么此节点会被取消
                cancelList.add(firstAppointment);
            }
        }
        cancelList.forEach((AppointmentFormTimeDataVO appointmentFormTimeDataVO) -> {
//                取消预约
            AppointmentDto newAppointmentDto = createAppointmentDto(vo,
                    appointmentFormTimeDataVO,
                    activityCode,
                    AppointmentStatusEnum.getDisplay(
                            AppointmentStatusEnum.CANCELLED));
            newAppointmentDto.setAppointmentId(appointmentFormTimeDataVO.getAppointmentId());
            appointmentAntiCorruptionServiceImp.updateAppointment(newAppointmentDto);

        });
//                如果非删除的预约中，属性appointmentId不为空，说明是保存过得，不需要重新保存。
        notCancelList.forEach(appointmentFormTimeDataVO -> {
            if (StringUtils.isEmpty(appointmentFormTimeDataVO.getAppointmentId())) {
                AppointmentDto newAppointmentDto = createAppointmentDto(vo,
                        appointmentFormTimeDataVO,
                        activityCode,
                        AppointmentStatusEnum.getDisplay(
                                AppointmentStatusEnum.BOOKED));
                appointmentAntiCorruptionServiceImp.createAppointment(newAppointmentDto);
            }
        });
    }

    private boolean treatmentAppointmentDTOListContainDto(List<TreatmentAppointmentDTO> treatmentAppointmentDTOList, TreatmentAppointmentDTO dto) {
        boolean exists = false;
        for (TreatmentAppointmentDTO treatmentAppointmentDTO : treatmentAppointmentDTOList) {
            if (dto.getStartTime().equals(treatmentAppointmentDTO.getStartTime())) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    /**
     * 从本地查询治疗预约情况
     *
     * @param hisId
     * @param startDate
     * @param endDate
     * @param sort
     * @param countPerPage
     * @param pageNumber
     * @param userContext
     * @return
     */
    private Pagination<AppointmentDto> getAppointmentDtosFromLocal(String hisId, String deviceId, String startDate,
                                                                   String endDate, String sort, String countPerPage, String pageNumber,
                                                                   UserContext userContext) {
        final Pagination<AppointmentDto> pagination = new Pagination<>();

        String tmpSort = sort;
        if (isEmpty(tmpSort)) {
            tmpSort = "asc";
        }
        // String tmpStartDate = startDate;
        // String tmpEndDate = endDate;
        Date dateStart;
        Date dateEnd;
        try {
            if (isBlank(startDate)) {
                // If startDate is null, means today 00:00:00.
                dateStart = DateUtil.getToday();
            } else {
                dateStart = DateUtil.parse(startDate);
            }

            if (isBlank(endDate)) {
                dateEnd = dateStart;
            } else {
                dateEnd = DateUtil.parse(endDate);
            }
        } catch (ParseException e) {
            log.error("ParseException: {}", e.getMessage());
            return pagination;
        }

        // // TODO
        // if (!(isNotEmpty(tmpStartDate) && isNotEmpty(tmpEndDate))) {
        // String curDate = DateUtil.getCurrentDate();
        // tmpStartDate = curDate;
        // tmpEndDate = curDate;
        // }

        pagination.setLstObject(new ArrayList<>());
        TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(userContext);

        if (isNotEmpty(hisId)) {
            List<PatientDto> rList = queryPatientByHisIdOrAriaIdOrName(hisId, null, null, null, patientAntiCorruptionServiceImp);
            if (!rList.isEmpty()) {
                List<Long> patientSerList = new ArrayList<>();
                rList.forEach(patientDto -> {
                    patientSerList.add(Long.parseLong(patientDto.getHisId()));
                });
                Pagination<TreatmentAppointmentDTO> treatmentAppointmentDTOPagination;
                if (StringUtils.isEmpty(deviceId)) {
                    treatmentAppointmentDTOPagination = treatmentAppointmentService.queryByPatientSerListAndDatePagination(patientSerList, dateStart, dateEnd, tmpSort, countPerPage, pageNumber);
                } else {
                    treatmentAppointmentDTOPagination = treatmentAppointmentService.queryByPatientSerListAndDeviceIdAndDatePagination(patientSerList, deviceId, dateStart, dateEnd, tmpSort, countPerPage, pageNumber);
                }
                if (treatmentAppointmentDTOPagination.getLstObject() != null) {
                    treatmentAppointmentDTOPagination.getLstObject().forEach(treatmentAppointmentDTO -> {
                        pagination.getLstObject().add(TreatmentAppointmentAssembler.treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO, true));
                    });
                    pagination.setTotalCount(pagination.getLstObject().size());
                }
            }
        } else {
            Pagination<TreatmentAppointmentDTO> treatmentAppointmentDTOPagination;
            if (StringUtils.isEmpty(deviceId)) {
                treatmentAppointmentDTOPagination = treatmentAppointmentService.queryByDeviceIdListAndDatePagination(null, dateStart, dateEnd, Arrays.asList(AppointmentStatusEnum.BOOKED), tmpSort, countPerPage, pageNumber);
            } else {
                List<String> deviceIdList = new ArrayList<>();
                deviceIdList.add(deviceId);
                treatmentAppointmentDTOPagination = treatmentAppointmentService.queryByDeviceIdListAndDatePagination(deviceIdList, dateStart, dateEnd, Arrays.asList(AppointmentStatusEnum.BOOKED), tmpSort, countPerPage, pageNumber);
            }
            if (treatmentAppointmentDTOPagination.getLstObject() != null) {
                treatmentAppointmentDTOPagination.getLstObject().forEach(treatmentAppointmentDTO -> {
                    pagination.getLstObject().add(TreatmentAppointmentAssembler.treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO, true));
                });
                pagination.setTotalCount(pagination.getLstObject().size());
            }
        }

        return pagination;
    }

    private Pagination<AppointmentDto> getAppointmentDtos(String hisId, String activityCode, String deviceId, String startDate,
                                                          String endDate, String sort, String countPerPage,
                                                          String pageNumber) {
        String tmpSort = sort;

        String tmpStartDate = startDate;
        String tmpEndDate = endDate;

        if (isBlank(tmpStartDate)) {
            // start date is null, means today.
            tmpStartDate = DateUtil.getCurrentDate();
        }

        // If only one time set, should use appointment time to filter.
        // Would handle this logi
        // if (!(isNotEmpty(tmpStartDate) && isNotEmpty(tmpEndDate))) {
        //     String curDate = DateUtil.getCurrentDate();
        //     tmpStartDate = curDate;
        //     tmpEndDate = curDate;
        // }
        Pagination<AppointmentDto> pagination = new Pagination<>();
        pagination.setLstObject(new ArrayList<>());
        if (isNotEmpty(hisId)) {
            List<PatientDto> rList = queryPatientByHisIdOrAriaIdOrName(hisId, null, null, null, patientAntiCorruptionServiceImp);
            if (!rList.isEmpty()) {
                for (PatientDto patientDto : rList) {
                    Pagination<AppointmentDto> paging;
                    if (StringUtils.isEmpty(deviceId)) {
                        paging = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeAndDateRangeAndPagination(patientDto.getPatientSer(),
                                activityCode, tmpStartDate, tmpEndDate, COUNT_PER_PAGE, 1, Integer.MAX_VALUE);
                    } else {
                        paging = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeAndDeviceIdAndDateRangeAndPagination(patientDto.getPatientSer(),
                                activityCode, deviceId, tmpStartDate, tmpEndDate, COUNT_PER_PAGE, 1, Integer.MAX_VALUE);
                    }
                    if (paging != null && paging.getLstObject().size() > 0) {
                        if (paging.getLstObject() != null) {
                            pagination.getLstObject().addAll(paging.getLstObject());
                        }
                    }
                }
                pagination.setTotalCount(pagination.getLstObject().size());
                if (StringUtils.isNotEmpty(countPerPage) && StringUtils.isNotEmpty(pageNumber)) {
                    manualPagination(pagination, Integer.parseInt(countPerPage), Integer.parseInt(pageNumber));
                }
            }
        } else {
            List<String> deviceIds = new ArrayList<>();
            if (StringUtils.isEmpty(deviceId)) {
                String defaultTemplateName = configuration.getDefaultCarePathTemplateName();
                //取activitycode.yaml文件里appointment对应的预约task节点里配置的设备
                String theTaskCode = ActivityCodesReader.getSourceActivityCodeByRelativeCode(activityCode).getName();
                deviceIds = DeviceUtil.getDevicesByActivityCode(defaultTemplateName, theTaskCode);
            } else {
                deviceIds.add(deviceId);
            }


            if (deviceIds.isEmpty()) {
                log.error("No device for activity code[{}]", activityCode);
                pagination.setLstObject(new ArrayList<>());
                return pagination;
            }

            if (isEmpty(tmpSort)) {
                tmpSort = "asc";
            }

            List<ImmutablePair<AppointmentRankEnum, RankEnum>> lstRank = new ArrayList<>();

            lstRank.add(new ImmutablePair<>(AppointmentRankEnum.START_TIME, RankEnum.fromCode(tmpSort)));

            try {
                Date tStartDate = DateUtil.parse(tmpStartDate);
                Date tEnddate = null;
                if (tEnddate == null) {
                    tEnddate = tStartDate;
                }
                for (; tStartDate.compareTo(tEnddate) <= 0; tStartDate = DateUtil.addDay(tStartDate, 1)) {
                    Pagination<AppointmentDto> pag = null;
                    if (StringUtils.isEmpty(countPerPage) || StringUtils.isEmpty(pageNumber)) {
                        pag = appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRangeWithPagination(deviceIds,
                                activityCode,
                                DateUtil.formatDate(tStartDate, DateUtil.DATE_FORMAT),
                                DateUtil.formatDate(tStartDate, DateUtil.DATE_FORMAT),
                                lstRank, COUNT_PER_PAGE, 1, Integer.MAX_VALUE);
                    } else {
                        pag = appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRangeWithPagination(deviceIds,
                                activityCode,
                                DateUtil.formatDate(tStartDate, DateUtil.DATE_FORMAT),
                                DateUtil.formatDate(tStartDate, DateUtil.DATE_FORMAT),
                                lstRank,
                                Integer.parseInt(countPerPage),
                                Integer.parseInt(pageNumber),
                                Integer.parseInt(pageNumber));
                    }
                    if (pag != null && pag.getLstObject() != null) {
                        pagination.getLstObject().addAll(pag.getLstObject());
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return pagination;
    }

    private List<PatientDto> queryPatientByHisIdOrAriaIdOrName(String condition, String
            activeStatusCode, String urgentCode, List<String> practitionerIdList, PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp) {
        List<PatientDto> rList = new ArrayList<>();
        List<PatientDto> patientDtoList = patientAntiCorruptionServiceImp.queryPatientByHisIdAndActiveStatusAndPractitionerIds(condition, activeStatusCode, urgentCode, practitionerIdList);
        rList.addAll(patientDtoList);
        patientDtoList = patientAntiCorruptionServiceImp.queryPatientByAriaIdAndActiveStatusAndUrgentAndPractitionerIds(condition, activeStatusCode, urgentCode, practitionerIdList);
        rList.addAll(patientDtoList);

        Pagination<PatientDto> pagination = patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(condition, activeStatusCode, practitionerIdList, COUNT_PER_PAGE, 1, Integer.MAX_VALUE);
        if (pagination.getLstObject() != null) {
            rList.addAll(pagination.getLstObject());
        }
        pagination = patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientPinyinAndPractitionerIds(condition, activeStatusCode, practitionerIdList, COUNT_PER_PAGE, 1, Integer.MAX_VALUE);
        if (pagination.getLstObject() != null) {
            rList.addAll(pagination.getLstObject());
        }
//      TODO 去重 rList  由于通过分页查询patient缺失address，导致通过set不能自动合并，需要手工修改代码去重
//        Set<PatientDto> hset = new LinkedHashSet<>(rList);
//        rList.clear();
//        rList.addAll(hset);
        Map<String, PatientDto> hisIdMap = new LinkedHashMap();
        rList.forEach(patientDto -> hisIdMap.put(patientDto.getHisId(), patientDto));
        rList.clear();
        hisIdMap.entrySet().forEach(stringPatientDtoEntry -> rList.add(stringPatientDtoEntry.getValue()));
        return rList;
    }

    private Pagination<OrderDto> getOrderDtos(UserContext userContext, String hisId, String activityCode, String groupId, String countPerPage, String pageNumber, String sort, boolean urgent) {
        boolean isGroupBased = false;
        //如果是Oncologist才会有isGroupBased是true的情况，如果多个carepath中有相同名称的Activity，则每个carepath中的AutoAssignPrimaryOncologist属性需要配置成一样的。
        if (StringUtils.equalsIgnoreCase(userContext.getLogin().getGroup(), SystemConfigPool.queryGroupRoleOncologist())) {
            List<String> supportCarePathTemplateNameList = new ArrayList<>();
            String defaultTemplateName = configuration.getDefaultCarePathTemplateName();
            supportCarePathTemplateNameList.add(defaultTemplateName);
            List<CarePathConfigItem> carePathConfigItems = this.configuration.getCarePathConfig().getCarePath();
            if (carePathConfigItems != null) {
                carePathConfigItems.forEach(carePathConfigItem -> supportCarePathTemplateNameList.add(carePathConfigItem.getTemplateId()));
            }

            for (int i = 0; i < supportCarePathTemplateNameList.size() && !isGroupBased; i++) {
                String templateName = supportCarePathTemplateNameList.get(i);
                CarePathAntiCorruptionServiceImp antiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
                CarePathTemplate template = antiCorruptionServiceImp.queryCarePathByTemplateName(templateName);

                if (template == null) {
                    log.error("Fail to find the carepath template: " + templateName);
                } else {
                    for (PlannedActivity plannedActivity : template.getActivities()) {
                        if (plannedActivity.getActivityCode().equals(activityCode) && StringUtils.isNotEmpty(groupId)) {
                            isGroupBased = plannedActivity.getAutoAssignPrimaryOncologist();
                            break;
                        }
                    }
                }
            }
        }
        List<String> groupIdList = new ArrayList<>();
        List<String> practitionerIdList = new ArrayList<>();
        if (isGroupBased) {
//            practitionerIdList = GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),groupId);
//          登录用户所在的组
            List<String> staffGroups = userContext.getLogin().getStaffGroups();
            if (staffGroups.contains(groupId)) {
//          用户选择的机构正好是用户所在的机构，则查询出该机构及其子机构的所有Practitioner
                practitionerIdList.addAll(GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(groupId));
            } else {
//          用户选择的机构不是用户所在的机构。
//             1.认为groupId是父节点
                GroupTreeNode groupTreeNode = GroupPractitionerHelper.searchGroupById(groupId);
                List<GroupTreeNode> parallelList = GroupPractitionerHelper.parallelTreeNode(groupTreeNode);
//              在parallelList中查询用户所属机构
                List<String> userGroupIdList = new ArrayList<>();
                parallelList.forEach(gt -> {
                    if (staffGroups.contains(gt.getId())) {
                        userGroupIdList.add(gt.getId());
                    }
                });
                for (String gid : userGroupIdList) {
                    practitionerIdList.addAll(GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(gid));
                }
//              2.认为是子节点  如果userGroupIdList是空的，说明点击的是子节点
                if (userGroupIdList.isEmpty()) {
                    practitionerIdList.addAll(GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(groupId));
                }
            }
        } else {
//            groupIdList = GroupPractitionerHelper.getSelfAndSubChildrenIdList(GroupPractitionerHelper.getOncologyGroupTreeNode(),groupId);
            groupIdList = userContext.getLogin().getStaffGroups();
        }

        String tmpSort = sort;
        Pagination<OrderDto> pagination = new Pagination<>();
        pagination.setLstObject(new ArrayList<>());

        String urgentCode = StatusIconPool.get(configuration.getUrgentStatusIconDesc());
        if (!urgent) {
            urgentCode = null;
        }
        if (isNotEmpty(hisId)) {
            List<PatientDto> rList;
            if (isGroupBased) {
                rList = this.queryPatientByHisIdOrAriaIdOrName(hisId, null, null, practitionerIdList, patientAntiCorruptionServiceImp);
            } else {
                rList = this.queryPatientByHisIdOrAriaIdOrName(hisId, null, null, null, patientAntiCorruptionServiceImp);
            }

            if (!rList.isEmpty())
                for (PatientDto patientDto : rList) {
                    Pagination<OrderDto> paging = orderAntiCorruptionServiceImp.queryOrderListByPatientIdAndActivityCodeAndGroupIdsAndPractitionerIdsWithPaging(patientDto.getPatientSer(),
                            activityCode,
                            groupIdList,
                            practitionerIdList,
                            COUNT_PER_PAGE,
                            1,
                            Integer.MAX_VALUE,
                            urgentCode);
                    if (paging.getLstObject() != null) {
                        pagination.getLstObject().addAll(paging.getLstObject());
                    }
                }
            pagination.setTotalCount(pagination.getLstObject().size());
            if (isNotEmpty(countPerPage) && isNotEmpty(pageNumber)) {
                manualPagination(pagination, Integer.parseInt(countPerPage), Integer.parseInt(pageNumber));
            }
            if (pagination.getLstObject().isEmpty()) {
                log.warn("Not found patient with hisId");
                pagination.setLstObject(new ArrayList<>());
            }
        } else {
            if (isEmpty(tmpSort)) {
                tmpSort = "desc";
            }

            List<ImmutablePair<OrderRankEnum, RankEnum>> lstRank = new ArrayList<>();

            lstRank.add(new ImmutablePair<>(OrderRankEnum.TASK_CREATION_DATE, RankEnum.fromCode(tmpSort)));

            if (isEmpty(countPerPage) || isEmpty(pageNumber)) {
                List<OrderDto> orderList;
                if (isGroupBased) {
                    orderList = orderAntiCorruptionServiceImp.queryOrderListByPractitionerIdAndActivityName(
                            practitionerIdList,
                            activityCode,
                            lstRank,
                            urgentCode);
                } else {
                    orderList = orderAntiCorruptionServiceImp.queryOrderListByGroupIDAndActivityName(
                            groupIdList,
                            activityCode,
                            lstRank,
                            urgentCode);
                }

                pagination.setLstObject(orderList);
            } else {
                if (isGroupBased) {
                    pagination = orderAntiCorruptionServiceImp.queryOrderListByPractitionerIdAndActivityNameWithPaging(
                            practitionerIdList,
                            activityCode,
                            lstRank,
                            Integer.parseInt(countPerPage),
                            Integer.parseInt(pageNumber),
                            Integer.parseInt(pageNumber),
                            urgentCode
                    );
                } else {
                    pagination = orderAntiCorruptionServiceImp.queryOrderListByGroupIDAndActivityNameWithPaging(
                            groupIdList,
                            activityCode,
                            lstRank,
                            Integer.parseInt(countPerPage),
                            Integer.parseInt(pageNumber),
                            Integer.parseInt(pageNumber),
                            urgentCode
                    );
                }
            }
        }
        return pagination;
    }

    private Pagination<PatientDto> getPatientDtoList(UserContext userContext, boolean urgent, String hisId,
                                                     String groupId, String countPerPage, String pageNumber) {
        List<PatientDto> patientDtoList;
        Pagination<PatientDto> pagination = new Pagination<>();

        String activeStatusCode = StatusIconPool.get(configuration.getActiveStatusIconDesc());
        String urgentCode = StatusIconPool.get(configuration.getUrgentStatusIconDesc());
        if (!urgent) {
            urgentCode = null;
        }
        if (isEmpty(activeStatusCode)) {
            // Active has changed to Encounter Status.
            log.error("Can't get the Encounter status code, please check if the Encounter Description in config is same as Aria Encouter Status Icon.");
            return pagination;
        }
        List<String> practitionerIdList = getPractitionersOfGroupAndSubGroups(userContext, groupId);
        if (isNotEmpty(hisId)) {
            List<PatientDto> rList = queryPatientByHisIdOrAriaIdOrName(hisId, activeStatusCode, urgentCode, practitionerIdList, patientAntiCorruptionServiceImp);
            if (log.isDebugEnabled()) {
                log.debug("hisId: [{}]", hisId);
                log.debug("rList: [{}]", rList);
            }
            pagination.setTotalCount(rList.size());
            pagination.setLstObject(rList);
            if (isNotEmpty(countPerPage) && isNotEmpty(pageNumber)) {
                manualPagination(pagination, Integer.parseInt(countPerPage), Integer.parseInt(pageNumber));
            }
        } else {
            if (practitionerIdList == null) {
                if (StringUtils.isEmpty(countPerPage) || StringUtils.isEmpty(pageNumber)) {
                    patientDtoList = patientAntiCorruptionServiceImp.queryAllActivePatients();
                    if (log.isDebugEnabled()) {
                        log.debug("patientDtoList: [{}]", patientDtoList);
                        int debugIndex = 0;
                        for (PatientDto dto : patientDtoList) {
                            log.debug("patientDto[{}]: {}", debugIndex++, dto.toString());
                        }
                    }
                    pagination.setLstObject(patientDtoList);
                } else {
                    pagination =
                            patientAntiCorruptionServiceImp.queryAllPatientsWithPaging(Integer.parseInt(countPerPage),
                                    Integer.parseInt(pageNumber), Integer.parseInt(pageNumber), activeStatusCode, urgentCode);
                    logPagination(pagination);
                }
            } else {
                if (StringUtils.isEmpty(countPerPage) || StringUtils.isEmpty(pageNumber)) {
                    patientDtoList = patientAntiCorruptionServiceImp.queryPatientDtoListByPractitionerIdList(practitionerIdList);
                    pagination.setLstObject(patientDtoList);
                    if (log.isDebugEnabled()) {
                        log.debug("patientDtoList: [{}]", patientDtoList);
                    }
                } else {
                    pagination = patientAntiCorruptionServiceImp.queryPatientDtoPaginationByPractitionerIdList(
                            practitionerIdList,
                            Integer.parseInt(countPerPage),
                            Integer.parseInt(pageNumber),
                            Integer.parseInt(pageNumber),
                            activeStatusCode,
                            urgentCode);
                    logPagination(pagination);
                }
            }
        }
        return pagination;
    }

    private void logPagination(Pagination<PatientDto> pagination) {
        if (log.isDebugEnabled()) {
            log.debug("Paging TotalCount: [{}]", pagination.getTotalCount());
            int debugIndex = 0;
            for (PatientDto list : pagination.getLstObject()) {
                log.debug("AppointmentDto[{}]: {}", debugIndex++, list.toString());
            }
        }
    }

    /**
     * Get practitioners of group and all sub groups.
     *
     * @param userContext user context
     * @param groupId     group id
     * @return Return null if group id is constant string 'AllPatients'.
     * Return the login practitioner if group id is constant string 'MyPatients'.
     * Return all practitioners of group and sub groups.
     */
    private List<String> getPractitionersOfGroupAndSubGroups(UserContext userContext, String groupId) {
        List<String> practitionerIdList = new ArrayList<>();
        if (StringUtils.equalsIgnoreCase(groupId, ActivityCodeConstants.MY_PATIENTS)) {
            practitionerIdList.add(userContext.getLogin().getResourceSer().toString());
        } else if (StringUtils.equalsIgnoreCase(groupId, ActivityCodeConstants.ALL_PATIENTS)) {
            practitionerIdList = null;
        } else {
//            获取登录用户查看患者的权限树，已经在登录的时候生成好了
            GroupTreeNode root = (GroupTreeNode) userContext.getLogin().getPatientAuthTree();
//          获取用户点击患者权限树的节点
            GroupTreeNode curTreeNode = null;
            Queue<GroupTreeNode> stack = new ArrayDeque<>();
            if (root != null) {
                while (root != null) {
                    if (groupId.equals(root.getId())) {
                        curTreeNode = root;
                        break;
                    } else {
                        if (!root.getSubItems().isEmpty()) {
                            root.getSubItems().forEach(groupTreeNode -> stack.offer(groupTreeNode));
                        }
                    }
                    root = stack.poll();
                }
                if (curTreeNode != null) {
                    List<String> patientGroupIdList = userContext.getLogin().getPermissionGroupIdList();
                    List<String> havePermissionGroupIdList = new ArrayList<>();
//                    获取当前节点及其子节点的groupId
                    stack.clear();
                    while (curTreeNode != null) {
                        if (patientGroupIdList.contains(curTreeNode.getId())) {
                            havePermissionGroupIdList.add(curTreeNode.getId());
                        }
                        curTreeNode.getSubItems().forEach(groupTreeNode -> stack.offer(groupTreeNode));
                        curTreeNode = stack.poll();
                    }
                    if (!havePermissionGroupIdList.isEmpty()) {
//                        根据GroupId获取医师
                        for (String gid : havePermissionGroupIdList) {
                            List<String> tmpList = GroupPractitionerHelper.getPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(), gid);
                            practitionerIdList.addAll(tmpList);
                        }
                    }
                }
            }
        }
        return practitionerIdList;
    }

    private void manualPagination(Pagination pagination, int countPerPage, int pageNumber) {
        int startIdx = (pageNumber - 1) * countPerPage;
        int endIdx = startIdx + countPerPage;
        if (endIdx > pagination.getTotalCount()) {
            endIdx = pagination.getTotalCount();
        }
        if (startIdx < pagination.getTotalCount()) {
            pagination.setLstObject(pagination.getLstObject().subList(startIdx, endIdx));
        }
    }
}
