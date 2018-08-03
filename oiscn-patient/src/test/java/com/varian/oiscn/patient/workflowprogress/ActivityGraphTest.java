package com.varian.oiscn.patient.workflowprogress;

import com.varian.oiscn.core.carepath.ActivityInstance;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by gbt1220 on 8/17/2017.
 */
public class ActivityGraphTest {

    @Test
    public void givenActivityGraphWhenTraversalThenReturnLatestPath() {
        List<ActivityGraph.ActivityVertex> vertexList = givenVertexList();
        Map<ActivityGraph.ActivityVertex, List<ActivityGraph.ActivityEdge>> map = givenVertexEdgeListMap(vertexList);
        ActivityGraph activityGraph = new ActivityGraph(vertexList, map);
        List<ActivityInstance> latestPath = activityGraph.dijkstraTraversal(vertexList.get(0), vertexList.get(vertexList.size() - 1));
        Assert.assertEquals(4, latestPath.size());
    }

    private Map<ActivityGraph.ActivityVertex, List<ActivityGraph.ActivityEdge>> givenVertexEdgeListMap(List<ActivityGraph.ActivityVertex> verList) {
        Map<ActivityGraph.ActivityVertex, List<ActivityGraph.ActivityEdge>> vertexEdgeListMap = new HashMap<ActivityGraph.ActivityVertex, List<ActivityGraph.ActivityEdge>>();

        List<ActivityGraph.ActivityEdge> v1List = new LinkedList<>();
        v1List.add(new ActivityGraph.ActivityEdge(verList.get(0), verList.get(1), 1));

        List<ActivityGraph.ActivityEdge> v2List = new LinkedList<>();
        v2List.add(new ActivityGraph.ActivityEdge(verList.get(1), verList.get(2), 1));
        v2List.add(new ActivityGraph.ActivityEdge(verList.get(1), verList.get(3), 1));

        List<ActivityGraph.ActivityEdge> v3List = new LinkedList<>();
        v3List.add(new ActivityGraph.ActivityEdge(verList.get(2), verList.get(5), 1));

        List<ActivityGraph.ActivityEdge> v4List = new LinkedList<>();
        v4List.add(new ActivityGraph.ActivityEdge(verList.get(3), verList.get(4), 1));

        List<ActivityGraph.ActivityEdge> v5List = new LinkedList<>();
        v5List.add(new ActivityGraph.ActivityEdge(verList.get(4), verList.get(5), 1));

        vertexEdgeListMap.put(verList.get(0), v1List);
        vertexEdgeListMap.put(verList.get(1), v2List);
        vertexEdgeListMap.put(verList.get(2), v3List);
        vertexEdgeListMap.put(verList.get(3), v4List);
        vertexEdgeListMap.put(verList.get(4), v5List);
        return vertexEdgeListMap;
    }

    private List<ActivityGraph.ActivityVertex> givenVertexList() {
        List<ActivityInstance> instances = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            ActivityInstance one = new ActivityInstance();
            one.setActivityCode("code" + i);
            instances.add(one);
        }

        List<ActivityGraph.ActivityVertex> verList = new LinkedList<>();
        for (ActivityInstance instance : instances) {
            verList.add(new ActivityGraph.ActivityVertex(instance));
        }
        return verList;
    }
}
