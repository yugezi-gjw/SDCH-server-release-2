package com.varian.oiscn.patient.workflowprogress;

import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.helper.CarePathInstanceSorterHelper;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.patient.view.WorkflowProgressNode;
import com.varian.oiscn.patient.view.WorkflowProgressNodeStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 获取患者治疗进度的关键路径算法类
 * Input: carepath instance，关键节点类型(By lane or category)
 * Output: 包含所有关键节点的最短路径的节点list
 *
 * @author gbt1220
 * @version 1.0, 17/08/23
 */
@AllArgsConstructor
@Slf4j
public class PatientWorkflowProgressHelper {
    private CarePathInstance carePathInstance;
    private String keyActivityType;
    private String keyActivityTypeValue;

    /**
     * 获取包含所有关键节点的最短路径的节点list
     * 先把carepath里的activity instances排序，获取从顶点到第一个关键节点的最短路径，
     * 每两个关键节点间的最短路径，最后一个关键节点到尾节点的最短路径，将这些最短路径上的所有节点
     * 放到结果集里
     *
     * @return list of WorkflowProgressNode
     */
    public List<WorkflowProgressNode> getKeyActivityWorkflowOfPatient() {
        if (carePathInstance == null) {
            log.error("The carePath instance is null.");
            return new ArrayList<>();
        }
        List<ActivityInstance> theShortestActivityPath = new ArrayList<>();
        List<ActivityInstance> sortedActivityInstances =
                CarePathInstanceSorterHelper.getSortActivities(carePathInstance);
        List<ActivityInstance> activitiesBetweenKeyActivity = new ArrayList<>();

        activitiesBetweenKeyActivity.add(sortedActivityInstances.get(0));
        for (int i = 1; i < sortedActivityInstances.size(); i++) {
            ActivityInstance activityInstance = sortedActivityInstances.get(i);
            if (isKeyActivity(activityInstance)) {
                activitiesBetweenKeyActivity.add(activityInstance);
                theShortestActivityPath.addAll(getShortestActivityPath(activitiesBetweenKeyActivity));
                //将结果集里的最后一个节点去掉，因为此关键节点将作为下一次的头结点
                theShortestActivityPath.remove(theShortestActivityPath.size() - 1);
                activitiesBetweenKeyActivity.clear();
                activitiesBetweenKeyActivity.add(activityInstance);
            } else {
                activitiesBetweenKeyActivity.add(activityInstance);
            }
        }

        //最后一个关键节点之后的剩余节点
        if (!activitiesBetweenKeyActivity.isEmpty()) {
            theShortestActivityPath.addAll(getShortestActivityPath(activitiesBetweenKeyActivity));
        }

        return getWorkflowProgressNodes(theShortestActivityPath);
    }

    private List<WorkflowProgressNode> getWorkflowProgressNodes(List<ActivityInstance> theShortestActivityPath) {
        List<WorkflowProgressNode> result = new ArrayList<>();
        CarePathInstanceHelper helper = new CarePathInstanceHelper(carePathInstance);
        theShortestActivityPath.forEach(
                eachActivity -> {
                    WorkflowProgressNode node = new WorkflowProgressNode();
                    node.setActivityName(ActivityCodesReader.getActivityCode(eachActivity.getActivityCode()).getContent());
                    node.setIsKeyActivity(isKeyActivity(eachActivity));
                    node.setStatus(getActivityStatus(helper, eachActivity));
                    result.add(node);
                });
        return result;
    }

    private WorkflowProgressNodeStatus getActivityStatus(CarePathInstanceHelper helper, ActivityInstance eachActivity) {
        WorkflowProgressNodeStatus result;
        if (CarePathStatusEnum.COMPLETED.equals(eachActivity.getStatus())) {
            result = WorkflowProgressNodeStatus.COMPLETED;
        } else if (CarePathStatusEnum.ACTIVE.equals(eachActivity.getStatus()) &&
                isAllPreActivitiesDoneOfActivityInstance(helper, eachActivity)) {
            result = WorkflowProgressNodeStatus.IN_PROGRESS;
        } else {
            result = WorkflowProgressNodeStatus.NOT_STARTED;
        }
        return result;
    }

    public boolean isAllPreActivitiesDoneOfActivityInstance(CarePathInstanceHelper helper, ActivityInstance instance) {
        List<ActivityInstance> activityInstances = helper.getPrevActivities(instance.getId());
        for (ActivityInstance prevActivity : activityInstances) {
            if (prevActivity.getStatus() != CarePathStatusEnum.COMPLETED) {
                return false;
            }
        }
        return true;
    }

    private boolean isKeyActivity(ActivityInstance activityInstance) {
        if (StringUtils.equalsIgnoreCase(keyActivityType, PatientWorkflowProgressConstants.KEY_ACTIVITY_TYPE_BY_LANE)) {
            return StringUtils.equals(keyActivityTypeValue, activityInstance.getCarePathLane());
        }
        return StringUtils.equals(keyActivityTypeValue, activityInstance.getActivityCategory());
    }

    /**
     * 从一组节点中获取最短路径的节点list
     *
     * @param sourceActivityInstances 一组节点
     * @return 最短路径的节点list
     */
    private List<ActivityInstance> getShortestActivityPath(List<ActivityInstance> sourceActivityInstances) {
        CarePathInstanceHelper carePathInstanceHelper =
                new CarePathInstanceHelper(carePathInstance);
        List<ActivityGraph.ActivityVertex> allVertexList = new LinkedList<>();
        Map<ActivityGraph.ActivityVertex, List<ActivityGraph.ActivityEdge>> vertexEdgeListMap = new HashMap<>();
        sourceActivityInstances.forEach(instance -> allVertexList.add(new ActivityGraph.ActivityVertex(instance)));
        allVertexList.forEach(
                activityVertex -> {
                    List<ActivityGraph.ActivityEdge> edges = new LinkedList<>();
                    carePathInstanceHelper.getNextActivities(activityVertex.getActivityInstance().getId())
                            .forEach(
                                    eachNextInstance -> {
                                        ActivityGraph.ActivityVertex endVertex =
                                                getVertexByActivityCode(
                                                        allVertexList, eachNextInstance.getActivityCode());

                                        if (endVertex != null) {
                                            edges.add(
                                                    new ActivityGraph.ActivityEdge(activityVertex, endVertex, 1));
                                        }
                                    });
                    vertexEdgeListMap.put(activityVertex, edges);
                });

        ActivityGraph activityGraph = new ActivityGraph(allVertexList, vertexEdgeListMap);

        return activityGraph.dijkstraTraversal(allVertexList.get(0), allVertexList.get(allVertexList.size() - 1));
    }

    private ActivityGraph.ActivityVertex getVertexByActivityCode(List<ActivityGraph.ActivityVertex> allVertexList,
                                                                 String activityCode) {
        Optional<ActivityGraph.ActivityVertex> optional = allVertexList.stream()
                .filter(
                        activityVertex -> StringUtils.equals(
                                activityVertex.getActivityInstance()
                                        .getActivityCode(),
                                activityCode))
                .findAny();

        if (optional.isPresent()) {
            return optional.get();
        }

        return null;
    }
}
