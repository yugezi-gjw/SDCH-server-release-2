package com.varian.oiscn.patient.workflowprogress;

import com.varian.oiscn.core.carepath.ActivityInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 根据dijkstra算法计算节点间的最短路径
 * Input: 所有节点集合，每一个节点对应的有向边集合
 * Output: 最短路径的节点集合
 *
 * @author gbt1220
 * @version 1.0, 17/08/23
 */
@AllArgsConstructor
@Data
@Slf4j
public class ActivityGraph {
    private List<ActivityVertex> vertexList;           // 图的节点集
    private Map<ActivityVertex, List<ActivityEdge>> vertexEdgeListMap;    // 图的每个节点对应的有向边

    /**
     * @param start dijkstra遍历的起点节点
     * @param dest  dijkstra遍历的终点节点
     * @return 最短路径的节点集合
     */
    public List<ActivityInstance> dijkstraTraversal(ActivityVertex start, ActivityVertex dest) {
        String path = "[" + dest.getActivityInstance().getActivityCode() + "]";
        List<ActivityInstance> result = new ArrayList<>();

        setRoot(start);
        updateChildren(start);
        result.add(dest.getActivityInstance());

        while ((dest.getParent() != null) && (!dest.equals(start))) {
            path = "[" + dest.getParent().getActivityInstance().getActivityCode() + "] --> " + path;
            dest = dest.getParent();
            result.add(dest.getActivityInstance());
        }

        log.debug("[" + start.getActivityInstance().getActivityCode() + "] to ["
                + dest.getActivityInstance().getActivityCode() + "] dijkstra shortest path :: " + path);
        Collections.reverse(result);

        return result;
    }

    /**
     * 从初始节点开始递归更新邻接表
     *
     * @param vertex 初始节点
     */
    private void updateChildren(ActivityVertex vertex) {
        if ((vertexEdgeListMap.get(vertex) == null) || (vertexEdgeListMap.get(vertex).size() == 0)) {
            return;
        }

        List<ActivityVertex> childrenList = new LinkedList<>();

        for (ActivityEdge edge : vertexEdgeListMap.get(vertex)) {
            ActivityVertex childVertex = edge.getEndVertex();

            // 如果子节点之前未知，则把当前子节点假如更新列表
            if (!childVertex.isKnown()) {
                childVertex.setKnown(true);
                childVertex.setVertexDist(vertex.getVertexDist() + edge.getWeight());
                childVertex.setParent(vertex);
                childrenList.add(childVertex);
            }

            // 子节点之前已知，则比较子节点的ajduDist&&nowDist
            int nowDist = vertex.getVertexDist() + edge.getWeight();

            if (nowDist >= childVertex.getVertexDist()) {
                continue;
            } else {
                childVertex.setVertexDist(nowDist);
                childVertex.setParent(vertex);
            }
        }

        // 更新每一个子节点
        for (ActivityVertex vc : childrenList) {
            updateChildren(vc);
        }
    }

    /**
     * 设置初始节点
     *
     * @param v 节点
     */
    public void setRoot(ActivityVertex v) {
        v.setParent(null);
        v.setVertexDist(0);
    }

    /**
     * 节点的有向边类
     *
     * @author gbt1220
     * @version 1.0, 17/08/23
     */
    @Data
    @AllArgsConstructor
    static class ActivityEdge {
        private ActivityVertex startVertex;    // 此有向边的起始点
        private ActivityVertex endVertex;      // 此有向边的终点
        private int weight;         // 此有向边的权值
    }


    /**
     * 节点类
     *
     * @author gbt1220
     * @version 1.0, 17/08/23
     */
    static class ActivityVertex {
        private final static int INFINITE_DIST = Integer.MAX_VALUE;
        private ActivityInstance activityInstance;    // 节点名字
        private boolean known;               // 此节点之前是否已知
        private int vertexDist;          // 此节点距离
        private ActivityVertex parent;              // 当前从初始节点到此节点的最短路径下的父节点。

        /**
         * Constructs ...
         *
         * @param activityInstance
         */
        public ActivityVertex(ActivityInstance activityInstance) {
            this.known = false;
            this.vertexDist = INFINITE_DIST;
            this.parent = null;
            this.activityInstance = activityInstance;
        }

        public ActivityInstance getActivityInstance() {
            return activityInstance;
        }

        public void setActivityInstance(ActivityInstance activityInstance) {
            this.activityInstance = activityInstance;
        }

        public boolean isKnown() {
            return known;
        }

        public void setKnown(boolean known) {
            this.known = known;
        }

        public ActivityVertex getParent() {
            return parent;
        }

        public void setParent(ActivityVertex parent) {
            this.parent = parent;
        }

        public int getVertexDist() {
            return vertexDist;
        }

        public void setVertexDist(int vertexDist) {
            this.vertexDist = vertexDist;
        }
    }
}