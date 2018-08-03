package com.varian.oiscn.carepath.resource;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.helper.CarePathInstanceSorterHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.carepath.vo.SequentialActivityVO;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.activity.SequentialActivityStatusEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderStatusEnum;
import com.varian.oiscn.core.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by gbt1220 on 5/17/2017.
 */
@Slf4j
public class SequentialActivitiesHelper {
    private CarePathInstance carePathInstance;

    private CarePathInstanceHelper instanceHelper;

    private List<String> staffGroups;

    public SequentialActivitiesHelper(CarePathInstance carePathInstance, List<String> staffGroups) {
        this.carePathInstance = carePathInstance;
        this.staffGroups = staffGroups;
        instanceHelper = new CarePathInstanceHelper(this.carePathInstance);
    }

    public List<SequentialActivityVO> querySequentialActivitiesByInstanceId(String instanceId, String type) {
        List<SequentialActivityVO> result = new ArrayList<>();
        final String[] tmpInstanceId = {instanceId};
        try {
            if(SystemConfigPool.queryStoredTreatmentAppointment2Local() && !StringUtils.isNumeric(instanceId)){
                TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(new UserContext());
                TreatmentAppointmentDTO treatmentAppointmentDTO = treatmentAppointmentService.queryByUidOrAppointmentId(instanceId);
                if(treatmentAppointmentDTO != null){
                    tmpInstanceId[0] = treatmentAppointmentDTO.getAppointmentId();
                }
            }
            ActivityInstance curActivityInstance = carePathInstance.getOriginalActivityInstances().stream().filter(
                    instance -> StringUtils.equalsIgnoreCase(tmpInstanceId[0], instance.getInstanceID()) &&
                            type.equals(instance.getActivityType().name())).findAny().get();

            List<ActivityInstance> sequentialActivities = getAllSequentialActivities(curActivityInstance);
            for (ActivityInstance activityInstance : sequentialActivities) {
                if (activityInstance.getIsActiveInWorkflow()) {
                    result.add(assemblerSequentialActivityVO(activityInstance, SequentialActivityStatusEnum.ACTIVE));
                } else if (StringUtils.isEmpty(activityInstance.getInstanceID())) {
                    result.add(assemblerSequentialActivityVO(activityInstance, SequentialActivityStatusEnum.INACTIVE));
                } else if (ActivityTypeEnum.TASK.equals(activityInstance.getActivityType())) {
                    OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
                    OrderDto orderDto = orderAntiCorruptionServiceImp.queryOrderById(activityInstance.getInstanceID());
                    addResult(activityInstance, orderDto.getOrderStatus(), OrderStatusEnum.getDisplay(OrderStatusEnum.COMPLETED), result);
                } else if (ActivityTypeEnum.APPOINTMENT.equals(activityInstance.getActivityType())) {
                    AppointmentAntiCorruptionServiceImp antiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
                    AppointmentDto appointmentDto = antiCorruptionServiceImp.queryAppointmentById(activityInstance.getInstanceID());
                    addResult(activityInstance, appointmentDto.getStatus(), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED), result);
                }
            }
        } catch (NoSuchElementException e) {
            log.error("Not found the element of {}", instanceId);
        }
        return result;
    }

    private void addResult(ActivityInstance activityInstance, String satus, String statusDisplay, List<SequentialActivityVO> result) {
        if (StringUtils.equalsIgnoreCase(satus, statusDisplay)) {
            result.add(assemblerSequentialActivityVO(activityInstance, SequentialActivityStatusEnum.DONE));
        } else {
            result.add(assemblerSequentialActivityVO(activityInstance, SequentialActivityStatusEnum.INACTIVE));
        }
    }


    public List<ActivityInstance> getAllSequentialActivities(ActivityInstance curActivityInstance) {
        CarePathInstanceSorterHelper.sortActivities(carePathInstance);

        List<ActivityInstance> sequentialActivities = new ArrayList<>();

        //广度搜索节点
        Deque<ActivityInstance> stack = new ArrayDeque<>();
        stack.add(curActivityInstance);

        while (!stack.isEmpty()) {
            ActivityInstance tempInstance = stack.pop();
            if (tempInstance.getPrevActivities() != null) {
                instanceHelper.getPrevActivities(tempInstance.getId()).forEach(prevActivity -> {
                    if (staffGroups.contains(prevActivity.getDefaultGroupID()) && !sequentialActivities.contains(prevActivity)) {
                        stack.push(prevActivity);
                        sequentialActivities.add(prevActivity);
                    }
                });
            }
            if (!sequentialActivities.contains(curActivityInstance) && staffGroups.contains(curActivityInstance.getDefaultGroupID())) {
                sequentialActivities.add(curActivityInstance);
            }
            if (tempInstance.getNextActivities() != null) {
                instanceHelper.getNextActivities(tempInstance.getId()).forEach(nextActivity -> {
                    if (staffGroups.contains(nextActivity.getDefaultGroupID()) && !sequentialActivities.contains(nextActivity)) {
                        stack.push(nextActivity);
                        sequentialActivities.add(nextActivity);
                    }
                });
            }
        }
        Collections.sort(sequentialActivities, Comparator.comparing(ActivityInstance::getIndex));
        return sequentialActivities;
    }

    public SequentialActivityVO assemblerSequentialActivityVO(ActivityInstance activityInstance, SequentialActivityStatusEnum status) {
        SequentialActivityVO activityVO = new SequentialActivityVO();
        ActivityCodeConfig activityCodeConfig = ActivityCodesReader.getActivityCode(activityInstance.getActivityCode());
        activityVO.setActivityId(activityInstance.getId());
        activityVO.setActivityCode(activityInstance.getActivityCode());
        activityVO.setDisplayName(activityCodeConfig.getContent());
        activityVO.setInstanceId(activityInstance.getInstanceID());
        activityVO.setStatus(status);
        activityVO.setType(activityInstance.getActivityType());
        activityVO.setWorkspaceType(activityCodeConfig.getWorkspaceType());
        activityVO.setDefaultAppointmentView(activityCodeConfig.getDefaultAppointmentView());
        activityVO.setDynamicFormTemplateIds(activityCodeConfig.getDynamicFormTemplateIds());
        activityVO.setEclipseModuleId(activityCodeConfig.getEclipseModuleId());
        return activityVO;
    }
}
