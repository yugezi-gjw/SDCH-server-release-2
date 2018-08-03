package com.varian.oiscn.base.helper;

import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * CarePath实例排序类
 * Input: CarePath instance
 * Output: 排序后的CarePath instance或者排序后的Activity instance list
 * 根据广度搜索算法排序
 *
 * @author gbt1220
 * @version 1.0, 17/09/01
 */
@Slf4j
public class CarePathInstanceSorterHelper {
    private CarePathInstanceSorterHelper() {
    }

    /**
     * 对一个carepath instance进行排序
     *
     * @param carePathInstance carepath 实例
     */
    public static void sortActivities(CarePathInstance carePathInstance) {
        List<ActivityInstance> sortedActivities = getSortActivities(carePathInstance);
        int index = 1;

        for (ActivityInstance instance : sortedActivities) {
            instance.setIndex(index++);
        }

        carePathInstance.setActivityInstances(sortedActivities);
    }

    /**
     * 根据activity id list，从carepath实例中获取对应的activity instance list
     *
     * @param carePathInstance carepath 实例
     * @param activityIds      activity id list
     * @return activity id list对应的activity instance list
     */
    private static List<ActivityInstance> getActivitiesByIds(CarePathInstance carePathInstance,
                                                             List<String> activityIds) {
        List<ActivityInstance> result = new ArrayList<>();

        activityIds.forEach(
                eachId -> {
                    Optional<ActivityInstance> optional = carePathInstance.getActivityInstances()
                            .stream()
                            .filter(
                                    instance -> StringUtils.equalsIgnoreCase(
                                            eachId,
                                            instance.getId()))
                            .findAny();

                    if (optional.isPresent()) {
                        result.add(optional.get());
                    } else {
                        log.error("Can't get planned activity by id[" + eachId + "]");
                    }
                });

        return result;
    }

    /**
     * 对carepath实例进行排序，获取排序后的activity instance list
     * 算法：从carepath instance里找到第一个节点，根据广度搜索算法查找next节点,
     * 如果next节点包含多个并列的情况，则按照并列的这些节点按照activity id排序
     *
     * @param carePathInstance carepath instance
     * @return 排序后的activity instance list
     */
    public static List<ActivityInstance> getSortActivities(CarePathInstance carePathInstance) {
        List<ActivityInstance> activityInstances = carePathInstance.getActivityInstances();
        ActivityInstance firstActivity = activityInstances.stream()
                .filter(
                        activityInstance -> (activityInstance.getPrevActivities() == null)
                                || activityInstance.getPrevActivities().isEmpty())
                .findAny()
                .get();
        List<ActivityInstance> sortedActivities = new ArrayList<>();

        sortedActivities.add(firstActivity);

        // 广度搜索节点
        Deque<ActivityInstance> stack = new ArrayDeque<>();

        stack.add(firstActivity);

        while (!stack.isEmpty()) {
            ActivityInstance tempActivity = stack.pop();
            List<String> tempNextActivityIdList = tempActivity.getNextActivities();

            if (tempNextActivityIdList != null) {
                List<ActivityInstance> tempNextActivityList = getActivitiesByIds(carePathInstance,
                        tempNextActivityIdList);

                Collections.sort(tempNextActivityList);
                tempNextActivityList.forEach(
                        nextActivity -> {
                            if (!sortedActivities.contains(nextActivity)) {
                                stack.offerLast(nextActivity);
                                sortedActivities.add(nextActivity);
                            }
                        });
            }
        }

        return sortedActivities;
    }
}
