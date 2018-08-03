package com.varian.oiscn.carepath.resource;

import com.varian.oiscn.core.carepath.CarePathTemplate;
import com.varian.oiscn.core.carepath.PlannedActivity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by gbt1220 on 6/21/2017.
 */
@Slf4j
public class CarePathTemplateHelper {

    private CarePathTemplateHelper() {
    }

    public static void sortActivities(CarePathTemplate carePathTemplate) {
        List<PlannedActivity> activityList = carePathTemplate.getActivities();
        PlannedActivity theFirstActivity = activityList.stream().filter(activity ->
                activity.getPrevActivities() == null || activity.getPrevActivities().isEmpty()).findFirst().get();
        carePathTemplate.setActivities(getSortActivities(carePathTemplate, theFirstActivity));
    }

    public static List<PlannedActivity> getSortActivities(CarePathTemplate carePathTemplate, PlannedActivity firstActivity) {
        List<PlannedActivity> sortedActivities = new ArrayList<>();
        sortedActivities.add(firstActivity);

        //广度搜索节点
        Deque<PlannedActivity> stack = new ArrayDeque<>();
        stack.add(firstActivity);

        while (!stack.isEmpty()) {
            PlannedActivity tempActivity = stack.pop();
            List<String> tempNextActivityIdList = tempActivity.getNextActivities();
            if (tempNextActivityIdList != null) {
                List<PlannedActivity> tempNextActivityList = getActivitiesByIds(carePathTemplate, tempNextActivityIdList);
                Collections.sort(tempNextActivityList);
                tempNextActivityList.forEach(nextActivity -> {
                    if (!sortedActivities.contains(nextActivity)) {
                        stack.push(nextActivity);
                        sortedActivities.add(nextActivity);
                    }
                });
            }
        }
        return sortedActivities;
    }

    private static List<PlannedActivity> getActivitiesByIds(CarePathTemplate carePathTemplate, List<String> activityIds) {
        List<PlannedActivity> result = new ArrayList<>();
        activityIds.forEach(eachId -> {
            Optional<PlannedActivity> optional = carePathTemplate.getActivities().stream().filter(
                    instance -> StringUtils.equalsIgnoreCase(eachId, instance.getId())).findAny();
            if (optional.isPresent()) {
                result.add(optional.get());
            } else {
                log.error("Can't get planned activity by id[{}]", eachId);
            }
        });
        return result;
    }
}
