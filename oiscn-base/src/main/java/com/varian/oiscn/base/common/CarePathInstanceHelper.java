package com.varian.oiscn.base.common;

import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.*;

/**
 * Created by gbt1220 on 5/11/2017.
 * Modified by bhp9696 on 7/6/2017.
 */
@Slf4j
@AllArgsConstructor
public class CarePathInstanceHelper {
    private static final String NOT_FOUND_ELEMENT_OF="Not found the element of ";
    private CarePathInstance carePathInstance;

    public List<ActivityInstance> getPrevActivitiesByInstanceId(String instanceId, String activityType) {
        try {
            ActivityInstance activityInstance = carePathInstance.getActivityInstances().stream().filter(
                    instance -> StringUtils.equalsIgnoreCase(instanceId, instance.getInstanceID()) &&
                            StringUtils.equalsIgnoreCase(activityType, instance.getActivityType().name())).findAny().get();
            List<String> prevActivityIds = activityInstance.getPrevActivities();
            if (prevActivityIds != null && !prevActivityIds.isEmpty()) {
                log.debug("Get prev activities by instanceId[" + instanceId + "]");
                return getActivitiesByIds(prevActivityIds);
            }
        } catch (NoSuchElementException e) {
            log.error(NOT_FOUND_ELEMENT_OF + instanceId);
        }
        return new ArrayList<>();
    }

    public List<ActivityInstance> getNextActivitiesByInstanceId(String instanceId, String activityType) {
        try {
            ActivityInstance activityInstance = carePathInstance.getActivityInstances().stream().filter(
                    instance -> StringUtils.equalsIgnoreCase(instanceId, instance.getInstanceID()) &&
                            StringUtils.equalsIgnoreCase(activityType, instance.getActivityType().name())).findAny().get();
            List<String> nextActivityIds = activityInstance.getNextActivities();
            if (nextActivityIds != null && !nextActivityIds.isEmpty()) {
                log.debug("Get next activities by instanceId[" + instanceId + "]");
                return getActivitiesByIds(nextActivityIds);
            }
        } catch (NoSuchElementException e) {
            log.error(NOT_FOUND_ELEMENT_OF + instanceId);
        }
        return new ArrayList<>();
    }

    public List<ActivityInstance> getPrevActivities(String id) {
        try {
            ActivityInstance activityInstance = carePathInstance.getActivityInstances().stream().filter(
                    instance -> StringUtils.equalsIgnoreCase(id, instance.getId())).findAny().get();
            List<String> prevActivityIds = activityInstance.getPrevActivities();
            if (prevActivityIds != null && !prevActivityIds.isEmpty()) {
                log.debug("Get prev activities by activityId[" + id + "]");
                return getActivitiesByIds(prevActivityIds);
            }
        } catch (NoSuchElementException e) {
            log.error(NOT_FOUND_ELEMENT_OF + id);
        }
        return new ArrayList<>();
    }

    public List<ActivityInstance> getNextActivities(String id) {
        try {
            ActivityInstance activityInstance = carePathInstance.getActivityInstances().stream().filter(
                    instance -> StringUtils.equalsIgnoreCase(id, instance.getId())).findAny().get();
            List<String> nextActivityIds = activityInstance.getNextActivities();
            if (nextActivityIds != null && !nextActivityIds.isEmpty()) {
                log.debug("Get next activities by activityId[" + id + "]");
                return getActivitiesByIds(nextActivityIds);
            }
        } catch (NoSuchElementException e) {
            log.error(NOT_FOUND_ELEMENT_OF + id);
        }
        return new ArrayList<>();
    }

    public boolean isAllPreActivitiesDoneOfEachNextActivity(String doneInstanceId, ActivityInstance eachNextActivity) {
        List<ActivityInstance> activityInstances = this.getPrevActivities(eachNextActivity.getId());
        if (activityInstances.size() == 1 && StringUtils.equals(doneInstanceId, activityInstances.get(0).getInstanceID())) {
            return true;
        }
        for (ActivityInstance prevActivity : activityInstances) {
            if (!StringUtils.equals(doneInstanceId, prevActivity.getInstanceID())
                    && (StringUtils.isEmpty(prevActivity.getInstanceID())
                    || prevActivity.getStatus() != CarePathStatusEnum.COMPLETED)) {
                return false;
            }
        }
        return true;
    }

    public ActivityInstance getActivityByInstanceIdAndActivityType(String instanceId, String activityType) {
        Optional<ActivityInstance> optional = carePathInstance.getOriginalActivityInstances().stream().filter(
                instance -> StringUtils.equalsIgnoreCase(instanceId, instance.getInstanceID()) &&
                        StringUtils.equalsIgnoreCase(activityType, instance.getActivityType().name())).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public ActivityInstance getActivityByCode(String code) {
        Optional<ActivityInstance> optional = carePathInstance.getActivityInstances().stream().filter(
                instance -> StringUtils.equalsIgnoreCase(code, instance.getActivityCode())).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public ActivityInstance getFirstActivity() {
        Optional<ActivityInstance> first = carePathInstance.getActivityInstances().stream().filter(
                activityInstance -> activityInstance.getPrevActivities() == null || activityInstance.getPrevActivities().isEmpty()
        ).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    public List<ActivityInstance> getActivitiesByIds(List<String> activityIds) {
        List<ActivityInstance> result = new ArrayList<>();
        activityIds.forEach(eachId -> {
            ActivityInstance eachInstance = carePathInstance.getActivityInstances().stream().filter(
                    instance -> StringUtils.equalsIgnoreCase(eachId, instance.getId())).findAny().get();
            result.add(eachInstance);
            log.debug(ReflectionToStringBuilder.toString(eachInstance));
        });
        return result;
    }

    public ActivityInstance getPreActiveInWorkflowActivity(ActivityInstance needDoneActivity) {
        // 广度搜索节点
        Deque<ActivityInstance> stack = new ArrayDeque<>();
        stack.add(needDoneActivity);
        while (!stack.isEmpty()) {
            ActivityInstance tempActivity = stack.pop();
            List<String> tempNextActivityIdList = tempActivity.getPrevActivities();

            if (tempNextActivityIdList != null) {
                List<ActivityInstance> tempNextActivityList = getActivitiesByIds(tempNextActivityIdList);
                Collections.sort(tempNextActivityList);
                for (ActivityInstance activity : tempNextActivityList) {
                    if (CarePathStatusEnum.ACTIVE.equals(activity.getStatus())) {
                        return activity;
                    }
                    stack.offerLast(activity);
                }
            }
        }
        return null;
    }
}
