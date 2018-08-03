package com.varian.oiscn.carepath.service;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.tasklocking.TaskLockingDto;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.carepath.vo.AppointmentActionEnum;
import com.varian.oiscn.carepath.vo.AppointmentFormDataVO;
import com.varian.oiscn.carepath.vo.AppointmentFormTimeDataVO;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 1/31/2018
 * @Modified By:
 */
@Slf4j
public class ActivityServiceImp {

    protected TreatmentAppointmentService treatmentAppointmentService;
    protected AppointmentAntiCorruptionServiceImp antiCorruptionServiceImp;
    protected OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp;

    public ActivityServiceImp(UserContext userContext) {
        this.treatmentAppointmentService = new TreatmentAppointmentService(userContext);
        this.antiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
        this.orderAntiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
    }

    public List<AppointmentFormTimeDataVO> checkMultiAppointmentsConflict(AppointmentFormDataVO vo) {
        List<AppointmentFormTimeDataVO> failToScheduleList = new ArrayList<>();

        if(vo != null && StringUtils.isNotBlank(vo.getDeviceId())) {

            String startDate = null;
            String endDate = null;
            Date startDateTime = null;
            Date endDateTime = null;
            String deviceId = vo.getDeviceId();
            String patientId = String.valueOf(vo.getPatientSer());

            List<AppointmentDto> appointmentDtoList = new ArrayList<>();
            Pagination<AppointmentDto> pagination = new Pagination<AppointmentDto>(){{
                setLstObject(new ArrayList());
            }};

            List<AppointmentFormTimeDataVO> allAppointmentTimeList = vo.getAppointTimeList();
            Collections.sort(allAppointmentTimeList);
            if(allAppointmentTimeList != null && !allAppointmentTimeList.isEmpty()) {
                startDate = allAppointmentTimeList.get(0).getStartTime(); //The start time of first timeslot
                endDate = allAppointmentTimeList.get(allAppointmentTimeList.size() - 1).getStartTime(); //The start time of last timeslot
            }


            if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                try {
                    startDateTime = DateUtil.parse(startDate);
                    endDateTime = DateUtil.parse(endDate);
                    startDate = DateUtil.formatDate(startDateTime, DateUtil.DATE_FORMAT);
                    endDate = DateUtil.formatDate(endDateTime, DateUtil.DATE_FORMAT);
                    Date tmpStartDate = DateUtil.parse(startDate);
                    Date tmpEndDate = DateUtil.parse(endDate);
                    for(;tmpStartDate.compareTo(tmpEndDate)<=0;tmpStartDate=DateUtil.addDay(tmpStartDate,1)){
                        Pagination<AppointmentDto> pag = antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDateRangeAndStatusWithPagination(Arrays.asList(deviceId),
                                DateUtil.formatDate(tmpStartDate,DateUtil.DATE_FORMAT), DateUtil.formatDate(tmpStartDate,DateUtil.DATE_FORMAT), Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED),
                                        AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
                        if(pag != null && pag.getLstObject() != null){
                            pagination.getLstObject().addAll(pag.getLstObject());
                        }
                    }
                } catch(ParseException e) {
                    log.error(e.getMessage());
                }

                treatmentAppointmentService.searchAppointmentFromLocal(deviceId, startDate, endDate, null, null, appointmentDtoList);
                if(pagination != null && !pagination.getLstObject().isEmpty()) {
                    if(log.isDebugEnabled()) {
                        log.debug("Paing TotalCount: [{}]", pagination.getTotalCount());
                    }
                    List<AppointmentDto> appointmentLst = pagination.getLstObject();
                    appointmentDtoList.addAll(appointmentLst);

                    //remove the duplicate elements
                    Set<AppointmentDto> appointmentSet = new HashSet<>(appointmentDtoList);
                    appointmentDtoList.clear();
                    appointmentDtoList.addAll(appointmentSet);
                }

                Map<String, List<AppointmentDto>> appointmentDtoMap = getAppointmentMapFromLst(appointmentDtoList);

                List<AppointmentFormTimeDataVO> cancelList = new ArrayList<>();
                List<AppointmentFormTimeDataVO> notCancelList = new ArrayList<>();

                allAppointmentTimeList.forEach(appointmentFormTimeDataVO -> {
                    if (AppointmentActionEnum.DELETED.ordinal() == appointmentFormTimeDataVO.getAction()) {//删除
                        cancelList.add(appointmentFormTimeDataVO);
                    } else {
                        notCancelList.add(appointmentFormTimeDataVO);
                    }
                });

                for(Iterator<AppointmentFormTimeDataVO> it = cancelList.iterator(); it.hasNext();) {
                    AppointmentFormTimeDataVO appointmentFormTimeDataVO = it.next();
                    String startTime = appointmentFormTimeDataVO.getStartTime();
                    List<AppointmentDto> dtoList = appointmentDtoMap.get(startTime);
                    if(dtoList != null){
                        for(Iterator<AppointmentDto> iterator = dtoList.iterator(); iterator.hasNext();){
                            for(ParticipantDto participantDto : iterator.next().getParticipants()) {
                                if (participantDto.getType().equals(ParticipantTypeEnum.PATIENT)) {
                                    if (participantDto.getParticipantId().equals(patientId)) {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }
                }

                int countPerSlot = Integer.parseInt(SystemConfigPool.queryTimeSlotCount());
                Date startTimeDate = null;
                Date voStartTime = null;

                for(Iterator<AppointmentFormTimeDataVO> it = notCancelList.iterator(); it.hasNext();) {
                    AppointmentFormTimeDataVO appointmentFormTimeDataVO = it.next();
                    int tempCount = 0;
                    String startTime = appointmentFormTimeDataVO.getStartTime();
                    List<AppointmentDto> dtoList = appointmentDtoMap.get(startTime);
                    if(dtoList != null) {
                        try {
                            startTimeDate = DateUtil.parse(startTime);
                            voStartTime = DateUtil.parse(appointmentFormTimeDataVO.getStartTime());
                        } catch(ParseException e) {
                            log.error(e.getMessage());
                        }

                        for(AppointmentDto appointmentDto : dtoList) {
                            List<ParticipantDto> participantDtoList = appointmentDto.getParticipants();
                            for (ParticipantDto participantDto : participantDtoList) {
                                if (ParticipantTypeEnum.PATIENT.equals(participantDto.getType())) {
                                    if (!participantDto.getParticipantId().equals(patientId)) {
                                        tempCount++;
                                    }
                                }
                            }
                        }
                        if (tempCount >= countPerSlot) {
                            failToScheduleList.add(appointmentFormTimeDataVO);
                        }
                    }
                }


            }
        }
        return failToScheduleList;
    }

    /**
     * Set task status to done
     * @param instanceId
     * @return
     */
    public boolean setTaskDone(String patientSer, String instanceId, UserContext userContext) {
        boolean result = false;
        if(Long.parseLong(patientSer) < 0 || StringUtils.isBlank(instanceId)) {
            log.error("PatientSer {} or instanceId {} has been error ", patientSer, instanceId );
            return false;
        }

        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        CarePathInstance carePathInstance =
                carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(patientSer,instanceId,ActivityTypeEnum.TASK);
        CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
        List<ActivityInstance> nextActivities;
        ActivityInstance needDoneActivity = helper.getActivityByInstanceIdAndActivityType(instanceId, ActivityTypeEnum.TASK.name());

        if (!CarePathStatusEnum.COMPLETED.equals(needDoneActivity.getStatus())) {
            OrderDto theDoneOrder = new OrderDto();
            theDoneOrder.setOrderId(instanceId);
            Date dueDate = DateUtil.addMillSecond(getDueDateOfTheActivity(needDoneActivity.getId(), helper), 60 * 1000);
            log.debug("Done task[{}], due date [{}].", instanceId, dueDate);
            theDoneOrder.setDueDate(dueDate);
            theDoneOrder.setOwnerId(String.valueOf(userContext.getLogin().getResourceSer()));
            theDoneOrder.setParticipants(Arrays.asList(new ParticipantDto() {{
                setType(ParticipantTypeEnum.PRACTITIONER);
                setParticipantId(String.valueOf(userContext.getLogin().getResourceSer()));
            }}));
            theDoneOrder.setOrderStatus(OrderStatusEnum.getDisplay(OrderStatusEnum.COMPLETED));
            orderAntiCorruptionServiceImp.updateOrder(theDoneOrder);
        }
        nextActivities = helper.getNextActivitiesByInstanceId(instanceId, ActivityTypeEnum.TASK.name());

        if ((nextActivities != null) && !nextActivities.isEmpty()) {
            for (ActivityInstance eachNextActivity : nextActivities) {
                // check if all pre activities of each next activity are done.
                if (!helper.isAllPreActivitiesDoneOfEachNextActivity(instanceId, eachNextActivity)) {
                    break;
                }

                if(StringUtils.isEmpty(eachNextActivity.getInstanceID())) {
                    OrderDto newOrderDto = new OrderDto();
                    newOrderDto.setOrderType(eachNextActivity.getActivityCode());
                    newOrderDto.setOrderStatus(OrderStatusEnum.getDisplay(OrderStatusEnum.READY));
                    newOrderDto.setOrderGroup(eachNextActivity.getDefaultGroupID());
                    newOrderDto.setDueDate(DateUtil.addMillSecond(getDueDateOfTheActivity(eachNextActivity.getId(), helper), 1 * 1000));

                    List<ParticipantDto> participantList = new ArrayList<>();
                    ParticipantDto patient = new ParticipantDto(ParticipantTypeEnum.PATIENT,
                            String.valueOf(patientSer));
                    participantList.add(patient);
                    newOrderDto.setParticipants(participantList);
                    log.debug("Schedule next order of id[{}]: activityCode[{}], status[{}], group[{}], dueDate[{}], patient[{}]",
                            eachNextActivity.getId(), newOrderDto.getOrderType(),
                            newOrderDto.getOrderStatus(), newOrderDto.getOrderGroup(),
                            newOrderDto.getDueDate(), String.valueOf(patientSer));
                    String id = carePathAntiCorruptionServiceImp.scheduleNextTask(carePathInstance.getId(),
                            eachNextActivity.getId(),
                            newOrderDto);
                    result = isNotEmpty(id);
                }
            }
        }

        TaskLockingServiceImpl taskLockingService = new TaskLockingServiceImpl(userContext);
        TaskLockingDto taskLockingDto = new TaskLockingDto(instanceId, needDoneActivity.getActivityType().name(), userContext.getName(), null, null, null);
        taskLockingService.unLockTask(taskLockingDto);
        return result;
    }

    private Map<String, List<AppointmentDto>> getAppointmentMapFromLst(List<AppointmentDto> appointmentDtoList) {
        Map<String, List<AppointmentDto>> appointmentDtoMap = new HashMap<>();
        if(appointmentDtoList != null && appointmentDtoList.size() > 0) {
            for(Iterator<AppointmentDto> it = appointmentDtoList.iterator(); it.hasNext();) {
                AppointmentDto dto = it.next();
                if(dto.getStartTime() != null) {
                    String startTime = DateUtil.formatDate(dto.getStartTime(), "yyyy-MM-dd HH:mm");
                    if(appointmentDtoMap.get(startTime) == null){
                        List<AppointmentDto> dtoList = new ArrayList<>();
                        dtoList.add(dto);
                        appointmentDtoMap.put(startTime, dtoList);
                    } else {
                        appointmentDtoMap.get(startTime).add(dto);
                    }
                }
            }
        }
        return appointmentDtoMap;
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

    private Date calculateDueDate(Date theDueDateOfActivity, ActivityInstance activityInstance) {
        Date tmpTheDueDateOfActivity = theDueDateOfActivity;
        if (activityInstance.getActivityType().equals(ActivityTypeEnum.TASK)) {
            OrderDto thePrevOrder = orderAntiCorruptionServiceImp.queryOrderById(activityInstance.getInstanceID());
            if (thePrevOrder.getDueDate().compareTo(theDueDateOfActivity) > 0) {
                tmpTheDueDateOfActivity = thePrevOrder.getDueDate();
            }
        } else {
            AppointmentDto appointmentDto =
                    antiCorruptionServiceImp.queryAppointmentById(activityInstance.getInstanceID());
            if (appointmentDto.getEndTime().compareTo(theDueDateOfActivity) > 0) {
                tmpTheDueDateOfActivity = appointmentDto.getEndTime();
            }
        }
        return tmpTheDueDateOfActivity;
    }

}
