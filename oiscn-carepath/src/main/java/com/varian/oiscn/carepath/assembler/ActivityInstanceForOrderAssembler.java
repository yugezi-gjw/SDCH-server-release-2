package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.carepath.vo.ActivityInstanceVO;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 6/8/2017.
 */
@Slf4j
public class ActivityInstanceForOrderAssembler extends AbstractActivityInstanceAssembler {

    private List<OrderDto> orderDtoList;

    private Map<String, List<CarePathInstance>> carePathInstanceMap;

    private Map<String, PatientDto> patientDtoMap;

    private Map<String, Boolean> urgentMap;

    private Map<String, Boolean> paymentMap;

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    public ActivityInstanceForOrderAssembler(List<OrderDto> orderDtoList, Configuration configuration, UserContext userContext) {
        super(configuration, userContext);
        this.orderDtoList = orderDtoList;
        this.patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        this.carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
    }

    @Override
    public List<ActivityInstanceVO> getActivityInstances() {
        List<ActivityInstanceVO> result = new ArrayList<>();
        ActivityInstanceVO instanceVO;
        List<String> patientIdList = getPatientIdList();
        carePathInstanceMap = getCarePathInstanceMapByPatientIdList(patientIdList);
        patientDtoMap = getPatientDtoMapByPatientIdList(patientIdList);
        urgentMap = getUrgentMap();
        paymentMap = getConfirmedPaymentMap();
        for (OrderDto orderDto : orderDtoList) {
            instanceVO = new ActivityInstanceVO();
            assemblerActivityDataFromOrder(instanceVO, orderDto);
            for (ParticipantDto participantDto : orderDto.getParticipants()) {
                if (participantDto.getType() == ParticipantTypeEnum.PATIENT) {
                    assemblerPatientData(instanceVO, patientDtoMap.get(participantDto.getParticipantId()), urgentMap, paymentMap);
                    break;
                }
            }
            result.add(instanceVO);
        }
        return result;
    }
    private Map<String, PatientDto> getPatientDtoMapByPatientIdList(List<String> patientIdList) {
        return patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(patientIdList);
    }

    private void assemblerActivityDataFromOrder(ActivityInstanceVO instanceVO, OrderDto orderDto) {
        instanceVO.setInstanceId(orderDto.getOrderId());
        instanceVO.setActivityType(ActivityTypeEnum.TASK.name());
        instanceVO.setActivityCode(orderDto.getOrderType());
        instanceVO.setActivityGroupId(orderDto.getOrderGroup());

        instanceVO.setNextAction(ActivityCodesReader.getActivityCode(orderDto.getOrderType()).getContent());
        orderDto.getParticipants().forEach(participantDto -> {
            if (participantDto.getType() == ParticipantTypeEnum.PATIENT) {
                assemblerPatientStateForOrder(instanceVO, participantDto.getParticipantId());
            }
        });
        ActivityCodeConfig curActivityCodeConfig = ActivityCodesReader.getActivityCode(instanceVO.getActivityCode());
        instanceVO.setWorkspaceType(curActivityCodeConfig.getWorkspaceType());
        instanceVO.setModuleId(curActivityCodeConfig.getEclipseModuleId());
    }

    private void assemblerPatientStateForOrder(ActivityInstanceVO instanceVO, String patientSer) {
        if (!StringUtils.equalsIgnoreCase(instanceVO.getActivityType(), ActivityTypeEnum.TASK.name())) {
            return;
        }
        List<CarePathInstance> instanceList = carePathInstanceMap.get(patientSer);
        ActivityInstance preLatestActivityInstance = null;
        if (instanceList == null || instanceList.isEmpty()) {
            return;
        }
        for (CarePathInstance carePathInstance : instanceList) {
            CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
            ActivityInstance curActivityInstance = helper.getActivityByInstanceIdAndActivityType(instanceVO.getInstanceId(), instanceVO.getActivityType());
            if (curActivityInstance == null) {
                log.warn("Cant find instance in Activity instances list . instance id is {}, patientSer is {}", instanceVO.getInstanceId(), patientSer);
                continue;
            }
            instanceVO.setActivityId(curActivityInstance.getId());
            List<ActivityInstance> prevActivities = helper.getPrevActivitiesByInstanceId(instanceVO.getInstanceId(), instanceVO.getActivityType());
            if (prevActivities != null && !prevActivities.isEmpty()) {
                if (prevActivities.size() > 1) {
                    Collections.sort(prevActivities, new ActivityInstanceDueDateComparator());
                }
                preLatestActivityInstance = prevActivities.get(prevActivities.size() - 1);
            } else {
                instanceVO.setPreActivityName(REGISTERED);
                instanceVO.setProgressState(REGISTERED);
            }
            if (preLatestActivityInstance != null) {
                ActivityCodeConfig activityCodeConfig = ActivityCodesReader.getActivityCode(preLatestActivityInstance.getActivityCode());
                instanceVO.setPreActivityName(activityCodeConfig.getContent());
                instanceVO.setProgressState(activityCodeConfig.getContent());
                instanceVO.setPreActivityCompletedTime(preLatestActivityInstance.getLastModifiedDT());
            }
        }
    }

    private Map<String, List<CarePathInstance>> getCarePathInstanceMapByPatientIdList(List<String> patientIdList) {
        return carePathAntiCorruptionServiceImp.queryCarePathListByPatientIDList(patientIdList);
    }

    @Override
    public List<String> getPatientIdList() {
        List<String> patientIdList = new ArrayList<>();
        for (OrderDto orderDto : orderDtoList) {
            orderDto.getParticipants().forEach(participantDto -> {
                if (participantDto.getType() == ParticipantTypeEnum.PATIENT) {
                    patientIdList.add(participantDto.getParticipantId());
                }
            });
        }
        return patientIdList;
    }

    @Override
    List<String> getPatientSerList() {
        List<String> hisIdList = new ArrayList<>();
        patientDtoMap.values().forEach(patientDto -> hisIdList.add(patientDto.getPatientSer()));
        return hisIdList;
    }
}
