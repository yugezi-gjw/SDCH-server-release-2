package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.carepath.vo.ActivityInstanceVO;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by gbt1220 on 6/8/2017.
 */
@Slf4j
public class ActivityInstanceForPatientAssembler extends AbstractActivityInstanceAssembler {
    private List<PatientDto> patientDtoList;

    private Map<String, List<CarePathInstance>> carePathInstanceMap;

    private List<String> staffGroups;

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    public ActivityInstanceForPatientAssembler(List<PatientDto> patientDtoList, List<String> staffGroups, Configuration configuration, UserContext userContext) {
        super(configuration, userContext);
        this.patientDtoList = patientDtoList;
        this.staffGroups = staffGroups;
        carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        List<String> patientIdList = getPatientIdList();
        carePathInstanceMap = getCarePathInstanceMapByPatientIdList(patientIdList);
    }

    private Map<String, List<CarePathInstance>> getCarePathInstanceMapByPatientIdList(List<String> patientIdList) {
        return carePathAntiCorruptionServiceImp.queryCarePathListByPatientIDList(patientIdList);
    }

    @Override
    public List<ActivityInstanceVO> getActivityInstances() {
        List<ActivityInstanceVO> result = new ArrayList<>();
        ActivityInstanceVO instanceVO;
        Map<String, Boolean> urgentMap = getUrgentMap();
        Map<String, Boolean> paymentMap = getConfirmedPaymentMap();
        for (PatientDto patientDto : patientDtoList) {
            instanceVO = new ActivityInstanceVO();
            assemblerActivityDataFromPatient(instanceVO, patientDto.getPatientSer());
            assemblerPatientData(instanceVO, patientDto, urgentMap, paymentMap);
            result.add(instanceVO);
        }
        return result;
    }
    private void assemblerActivityDataFromPatient(ActivityInstanceVO instanceVO, String patientSer) {
        List<CarePathInstance> instanceList = carePathInstanceMap.get(patientSer);
        if (instanceList == null || instanceList.isEmpty()) {
            log.error("There is no care path template for patientId : ");
            return;
        }
        List<ActivityInstance> activeInstancesInWorkflow = new ArrayList<>();
        List<ActivityInstance> primaryActiveInstancesInWorkflowOnly = new ArrayList<>();
        ActivityInstance lastInstanceInWorkflow = null;
        CarePathInstance mastInstance = null;
//        获取主CarePath
        PatientEncounterCarePath patientEncounterCarePath =  PatientEncounterHelper.getEncounterCarePathByPatientSer(patientSer);
        if(patientEncounterCarePath != null){
            Long masterCarePathInstanceId = patientEncounterCarePath.getPlannedCarePath().getMasterCarePathInstanceId();
            Optional<CarePathInstance> mastInstanceOptional = instanceList.stream().filter(ins-> ins.getId().equals(String.valueOf(masterCarePathInstanceId))).findFirst();
            if(mastInstanceOptional.isPresent()){
                mastInstance = mastInstanceOptional.get();
                CarePathInstanceHelper helper = new CarePathInstanceHelper(mastInstance);

                for (ActivityInstance activityInstance : mastInstance.getActivityInstances()) {
                    if (activityInstance.getIsActiveInWorkflow()) {
                        primaryActiveInstancesInWorkflowOnly.add(activityInstance);
                        activeInstancesInWorkflow.add(activityInstance);
                    }
                    if (helper.getNextActivities(activityInstance.getId()).isEmpty()) {
                        lastInstanceInWorkflow = activityInstance;
                    }
                }
//                获取可选流程
                List<Long> idList = patientEncounterCarePath.getPlannedCarePath().getOptionalCarePathInstanceId();
                idList.forEach(cpInstanceId -> {
                    Optional<CarePathInstance> optionalInstanceOptional = instanceList.stream().filter(ins-> ins.getId().equals(String.valueOf(cpInstanceId))).findFirst();
                    if(optionalInstanceOptional.isPresent()){
                        CarePathInstance optionalInstance = optionalInstanceOptional.get();
                        for (ActivityInstance activityInstance : optionalInstance.getActivityInstances()) {
                            if (activityInstance.getIsActiveInWorkflow()) {
                                activeInstancesInWorkflow.add(activityInstance);
                            }
                        }
                    }
                });

            }
        }
        if (activeInstancesInWorkflow.isEmpty()) { //The workflow is finished, and all activities are done.
            if (lastInstanceInWorkflow == null) {
                log.error("Can't find the last activity in care path instance by patientId");
                instanceVO.setNextAction(StringUtils.EMPTY);
                return;
            }
            instanceVO.setActivityId(lastInstanceInWorkflow.getId());
            instanceVO.setInstanceId(lastInstanceInWorkflow.getInstanceID());
            instanceVO.setActivityType(lastInstanceInWorkflow.getActivityType().name());
            instanceVO.setActivityCode(lastInstanceInWorkflow.getActivityCode());
            instanceVO.setActiveInWorkflow(false);
            instanceVO.setProgressState(ActivityCodesReader.getActivityCode(lastInstanceInWorkflow.getActivityCode()).getCompletedContent());
            instanceVO.setNextAction(StringUtils.EMPTY);
        } else {
            ActivityInstance nextActiveInstance = getNextActiveActivityInstance(activeInstancesInWorkflow);
            if(!ActivityCodesReader.getActivityCode(nextActiveInstance.getActivityCode()).getNotDisplayedInPatientList()){
                if (staffGroups.contains(nextActiveInstance.getDefaultGroupID())) {
                    instanceVO.setNextAction(ActivityCodesReader.getActivityCode(nextActiveInstance.getActivityCode()).getContent());
                } else {
                    instanceVO.setNextAction(StringUtils.EMPTY);
                }
            } else {
                instanceVO.setNextAction(StringUtils.EMPTY);
            }
            instanceVO.setActivityId(nextActiveInstance.getId());
            instanceVO.setInstanceId(nextActiveInstance.getInstanceID());
            instanceVO.setActivityType(nextActiveInstance.getActivityType().name());
            instanceVO.setActivityCode(nextActiveInstance.getActivityCode());
            instanceVO.setActiveInWorkflow(true);
//        get primary carePath's nextActiveInstance
            if(!primaryActiveInstancesInWorkflowOnly.isEmpty()) {
                nextActiveInstance = getNextActiveActivityInstance(primaryActiveInstancesInWorkflowOnly);
                if (nextActiveInstance.getPrevActivities() == null || nextActiveInstance.getPrevActivities().isEmpty()) {
                    instanceVO.setProgressState(REGISTERED);
                } else {
                    instanceVO.setProgressState(getProgressState(mastInstance));
                }
            }else{
                instanceVO.setProgressState(getProgressState(mastInstance));
            }
        }
        ActivityCodeConfig curActivityCodeConfig = ActivityCodesReader.getActivityCode(instanceVO.getActivityCode());
        instanceVO.setWorkspaceType(curActivityCodeConfig.getWorkspaceType());
        instanceVO.setModuleId(curActivityCodeConfig.getEclipseModuleId());
    }

    /**
     *
     * @param mastInstance
     * @return
     */
    private String getProgressState(CarePathInstance mastInstance) {
        List<ActivityInstance> completedActivityInstances = mastInstance.getActivityInstances().stream().filter(activityInstance ->
                activityInstance.getLastModifiedDT() != null &&
                        CarePathStatusEnum.COMPLETED.equals(activityInstance.getStatus())
        ).collect(Collectors.toList());
        Collections.sort(completedActivityInstances, new ActivityInstanceLastModifiedDtComparator());
        return ActivityCodesReader.getActivityCode(completedActivityInstances.get(0).getActivityCode()).getCompletedContent();
    }

    //从并行的节点里优先获取属于登录用户分组的节点，然后根据due date排序
    private ActivityInstance getNextActiveActivityInstance(List<ActivityInstance> activityInstances) {
        if (activityInstances.size() == 1) {
            return activityInstances.get(0);
        }
        ArrayList<ActivityInstance> listOfTheGroup = new ArrayList<>();
        activityInstances.stream().forEach(activityInstance -> {
            if (this.staffGroups.contains(activityInstance.getDefaultGroupID())) {
                listOfTheGroup.add(activityInstance);
            }
        });
        if (listOfTheGroup.isEmpty()) {
            Collections.sort(activityInstances, new ActivityInstanceDueDateComparator());
            return activityInstances.get(0);
        } else if (listOfTheGroup.size() == 1) {
            return listOfTheGroup.get(0);
        } else {
            Collections.sort(listOfTheGroup, new ActivityInstanceDueDateComparator());
            return listOfTheGroup.get(0);
        }
    }

    @Override
    List<String> getPatientIdList() {
        List<String> patientIdList = new ArrayList<>();
        patientDtoList.forEach(patientDto -> patientIdList.add(patientDto.getPatientSer()));
        return patientIdList;
    }

    @Override
    List<String> getPatientSerList() {
        List<String> hisIdList = new ArrayList<>();
        patientDtoList.forEach(patientDto -> hisIdList.add(patientDto.getPatientSer()));
        return hisIdList;
    }
}
