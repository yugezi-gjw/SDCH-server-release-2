package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.ActivityDefinition;
import com.varian.fhir.resources.CarePath;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceType;
import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;

/**
 * Created by fmk9441 on 2017-04-21.
 */
@Slf4j
public class CarePathAssembler {
    private static final String APPOINTMENT = "Appointment";

    private CarePathAssembler() {

    }

    /**
     * Return CarePath Template by Fhir CarePath.<br>
     *
     * @param carePath Fhir CarePath
     * @return CarePath Template
     */
    public static CarePathTemplate getCPTemplate(CarePath carePath) {
        CarePathTemplate carePathTemplate = new CarePathTemplate();
        if (carePath != null) {
            carePathTemplate.setId(carePath.hasIdElement() ? carePath.getIdElement().getIdPart() : null);
            carePathTemplate.setTemplateName(null != carePath.getTemplateName() ? carePath.getTemplateName().getValue() : null);
            carePathTemplate.setDepartmentID(null != carePath.getDepartment() ? getReferenceValue(carePath.getDepartment().getReference()) : null);
            carePathTemplate.setStatus(null != carePath.getStatus() ? CarePathStatusEnum.fromCode(carePath.getStatus().getValueAsString()) : null);
            carePathTemplate.setComments(null != carePath.getComments() ? carePath.getComments().getValueAsString() : null);
            carePathTemplate.setActivities(null != carePath.getPlannedActivities() ? getPlannedActivities(carePath) : null);
        }
        return carePathTemplate;
    }

    /**
     * Return CarePath Instance by Fhir CarePath.<br>
     * @param carePath Fhir CarePath
     * @return CarePath Instance
     */
    public static CarePathInstance getCPInstance(CarePath carePath) {
        CarePathInstance carePathInstance = new CarePathInstance();
        if (carePath != null) {
            carePathInstance.setId(carePath.hasIdElement() ? carePath.getIdElement().getIdPart() : null);
            carePathInstance.setCarePathTemplateId(carePath.getTemplateName().toString());
            carePathInstance.setPatientID(null != carePath.getPatient() ? getReferenceValue(carePath.getPatient()) : null);
            carePathInstance.setActivityInstances(null != carePath.getPlannedActivities() ? getActivityInstances(carePath) : null);
        }
        return carePathInstance;
    }

    private static List<PlannedActivity> getPlannedActivities(CarePath carePath) {
        List<PlannedActivity> lstPlannedActivity = new ArrayList<>();
        for (CarePath.PlannedActivity activity : carePath.getPlannedActivities()) {
            PlannedActivity plannedActivity = new PlannedActivity();
            plannedActivity.setId(activity.getId());
            plannedActivity.setDepartmentID(null != carePath.getDepartment() ? getReferenceValue(carePath.getDepartment()) : null);
            plannedActivity.setActivityCode(null != activity.getActivityDefinition() ? activity.getActivityDefinition().getDisplay() : null);
            plannedActivity.setActivityType(null != activity.getActivityType() ? (APPOINTMENT.equalsIgnoreCase(activity.getActivityType().getValue()) ? ActivityTypeEnum.APPOINTMENT : ActivityTypeEnum.TASK) : null);
            plannedActivity.setDefaultGroupID(null != activity.getResourceGroups() ? getReferenceValue(activity.getResourceGroups().get(0)) : getDefaultGroupID(carePath, activity));
            plannedActivity.setCarePathLane(null != activity.getLane() ? activity.getLane().getValue() : null);
            plannedActivity.setAutoAssignPrimaryOncologist(null != activity.getAutoAssignOncologist() ? activity.getAutoAssignOncologist().getValue() : false);
            plannedActivity.setPrevActivities(null != activity.getCausing() ? activity.getCausing().stream().map(r -> getReferenceValue(r)).collect(Collectors.toList()) : null);
            plannedActivity.setNextActivities(null != activity.getResulting() ? activity.getResulting().stream().map(r -> getReferenceValue(r)).collect(Collectors.toList()) : null);
            plannedActivity.setLagAfterPrevActivity(null != activity.getLagAfterPreviousActivity() ? activity.getLagAfterPreviousActivity().getValue().intValue() : null);
            if (null != activity.getResources() && activity.getResources().stream().anyMatch(x -> getReferenceType(x).equalsIgnoreCase(ResourceType.Device.name()))) {
                plannedActivity.setDeviceIDs(activity.getResources().stream().filter(x -> getReferenceType(x).equalsIgnoreCase(ResourceType.Device.name())).map(r -> getReferenceValue(r)).collect(Collectors.toList()));
            }
            lstPlannedActivity.add(plannedActivity);
        }
        return lstPlannedActivity;
    }

    private static List<ActivityInstance> getActivityInstances(CarePath carePath) {
        List<ActivityInstance> lstActivityInstance = new ArrayList<>();
        for (CarePath.PlannedActivity activity : carePath.getPlannedActivities()) {
            ActivityInstance activityInstance = new ActivityInstance();
            activityInstance.setId(activity.getId());
            activityInstance.setInstanceID(null != activity.getActivityInstance() ? getReferenceValue(activity.getActivityInstance()) : null);
            activityInstance.setDepartmentID(null != carePath.getDepartment() ? getReferenceValue(carePath.getDepartment()) : null);
            activityInstance.setActivityType(null != activity.getActivityType() ? (APPOINTMENT.equalsIgnoreCase(activity.getActivityType().getValue()) ? ActivityTypeEnum.APPOINTMENT : ActivityTypeEnum.TASK) : null);
            activityInstance.setActivityCode(null != activity.getActivityDefinition() ? activity.getActivityDefinition().getDisplay() : null);
            activityInstance.setDefaultGroupID(null != activity.getResourceGroups() ? getReferenceValue(activity.getResourceGroups().get(0)) : getDefaultGroupID(carePath, activity));
            activityInstance.setActivityCategory(null != activity.getActivityCategory() ? activity.getActivityCategory().getValue() : null);
            activityInstance.setCarePathLane(null != activity.getLane() ? activity.getLane().getValue() : null);
            activityInstance.setStatus(null != activity.getStatus() ? CarePathStatusEnum.fromCode(activity.getStatus().toCode()) : null);
            activityInstance.setPrevActivities(null != activity.getCausing() ? activity.getCausing().stream().map(r -> getReferenceValue(r)).collect(Collectors.toList()) : null);
            activityInstance.setNextActivities(null != activity.getResulting() ? activity.getResulting().stream().map(r -> getReferenceValue(r)).collect(Collectors.toList()) : null);
            activityInstance.setLagAfterPrevActivity(null != activity.getLagAfterPreviousActivity() ? activity.getLagAfterPreviousActivity().getValue().intValue() : null);
            if (null != activity.getDuration() && null != activity.getActivityType()) {
                if (APPOINTMENT.equalsIgnoreCase(activity.getActivityType().getValue())) {
                    activityInstance.setDueDateOrScheduledStartDate(activity.getDuration().getStart());
                } else {
                    activityInstance.setDueDateOrScheduledStartDate(activity.getDuration().getEnd());
                }
            }
            activityInstance.setIsActiveInWorkflow(null != activity.getIsAvailableInWorkflow() ? activity.getIsAvailableInWorkflow().booleanValue() : null);
            activityInstance.setLastModifiedDT(null != activity.getLastModified() ? activity.getLastModified().getValue() : null);
            if (null != activity.getResources()) {
                if (activity.getResources().stream().anyMatch(x -> getReferenceType(x).equalsIgnoreCase(ResourceType.Device.name()))) {
                    activityInstance.setDeviceIDs(activity.getResources().stream().filter(x -> getReferenceType(x).equalsIgnoreCase(ResourceType.Device.name())).map(r -> getReferenceValue(r)).collect(Collectors.toList()));
                }
                if (activity.getResources().stream().anyMatch(x -> getReferenceType(x).equalsIgnoreCase(ResourceType.Practitioner.name()))) {
                    activityInstance.setPrimaryPhysicianID(getReferenceValue(activity.getResources().stream().filter(x -> getReferenceType(x).equalsIgnoreCase(ResourceType.Practitioner.name())).findAny().get()));
                }
            }
            lstActivityInstance.add(activityInstance);
        }
        return lstActivityInstance;
    }

    private static String getDefaultGroupID(CarePath carePath, CarePath.PlannedActivity activity) {
        String defaultGroupID = StringUtils.EMPTY;
        if (carePath.hasContained() && null != activity.getActivityDefinition()) {
            Optional<Resource> resource = carePath.getContained().stream().filter(x -> x.getResourceType().equals(ResourceType.ActivityDefinition) &&getReferenceValue( x.getIdElement().getIdPart()).equals(getReferenceValue(activity.getActivityDefinition()))).findAny();
            if (resource.isPresent()) {
                try {
                    ActivityDefinition activityDefinition = (ActivityDefinition) resource.get();
                    defaultGroupID = getReferenceValue(activityDefinition.getDefaultGroup());
                }catch (ClassCastException e){
                    log.error("CarePathAssembler ClassCastException:{}",e);
                }
            }
        }
        return defaultGroupID;
    }
}