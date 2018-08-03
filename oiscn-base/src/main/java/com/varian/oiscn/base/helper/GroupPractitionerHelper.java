package com.varian.oiscn.base.helper;

import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.practitioner.PractitionerTreeNode;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class GroupPractitionerHelper {

    @Getter
    @Setter
    private static GroupTreeNode oncologyGroupTreeNode = null;
    @Getter
    @Setter
    private static GroupTreeNode physicistGroupTreeNode = null;
    @Getter
    @Setter
    private static GroupTreeNode nurseGroupTreeNode = null;
    @Getter
    @Setter
    private static GroupTreeNode techGroupTreeNode = null;

    public static GroupTreeNode convertMapToTree(Map<GroupDto, List<PractitionerDto>> map) {
        if (map == null) {
            return null;
        }
        //找到根节点
        GroupTreeNode rootGroup = null;
        for (Map.Entry<GroupDto, List<PractitionerDto>> entry : map.entrySet()) {
            if ( entry.getKey().getGroupName().equals(SystemConfigPool.queryGroupOncologistPrefix())
                    || entry.getKey().getGroupName().equals(SystemConfigPool.queryGroupNursePrefix())
                    || entry.getKey().getGroupName().equals(SystemConfigPool.queryGroupPhysicistPrefix())
                    || entry.getKey().getGroupName().equals(SystemConfigPool.queryGroupTechnicianPrefix())
               ){
                        rootGroup = new GroupTreeNode(entry.getKey().getGroupId(), entry.getKey().getGroupName(),
                        entry.getKey().getGroupName());
                for (PractitionerDto practitionerDto : entry.getValue()) {
                    rootGroup.addAPractitioner(new PractitionerTreeNode(practitionerDto.getId(),
                            practitionerDto.getName(), practitionerDto.getParticipantType()));
                }
                break;
            }
        }
        if (rootGroup == null) {
            return null;
        }
        int currentUnderscoreNum = 1;
        int currentMaxUnderscoreNum = 1;
        while (currentUnderscoreNum - currentMaxUnderscoreNum <= 1) {
            boolean foundCurrentUnderscoreNum = false;
            for (Map.Entry<GroupDto, List<PractitionerDto>> entry : map.entrySet()) {
                if (StringUtils.countMatches(entry.getKey().getGroupName(), '_') == currentUnderscoreNum) {
                    GroupTreeNode parentGroup = searchParentNode(rootGroup, entry.getKey().getGroupName());
                    if (parentGroup == null) {
                        log.warn("Failed to find the parent node for entry: " + entry.getKey().getGroupName());
                    } else {
                        GroupTreeNode currentNode = new GroupTreeNode(entry.getKey().getGroupId(), entry.getKey().getGroupName(), entry.getKey().getGroupName());
                        parentGroup.addAChildGroup(currentNode);
                        if (entry.getValue() != null) {
                            entry.getValue().forEach(practitioner -> currentNode.addAPractitioner(new PractitionerTreeNode(practitioner.getId(), practitioner.getName(), practitioner.getParticipantType())));
                        }
                        foundCurrentUnderscoreNum = true;
                    }
                }
            }
            if (foundCurrentUnderscoreNum) {
                currentMaxUnderscoreNum++;
            }
            currentUnderscoreNum++;
        }
        initGroupName(rootGroup);
        return rootGroup;
    }

    private static void initGroupName(GroupTreeNode root) {
        if (StringUtils.countMatches(root.getName(), "_") > 0) {
            root.setName(StringUtils.substringAfterLast(root.getName(), "_"));
        }
        if (root.getSubItems().isEmpty()) {
            return;
        } else {
            root.getSubItems().forEach(groupTreeNode -> initGroupName(groupTreeNode));
        }
    }

    private static GroupTreeNode searchParentNode(GroupTreeNode rootNode, String groupName) {
        if (rootNode == null) {
            return null;
        }
        if (groupName.startsWith(rootNode.getName()) && rootNode.getName().length() == groupName.lastIndexOf('_')) {
            return rootNode;
        } else {
            for (GroupTreeNode childGroup : rootNode.getSubItems()) {
                GroupTreeNode parentGroup = searchParentNode(childGroup, groupName);
                if (parentGroup != null) {
                    return parentGroup;
                }
            }
            return null;
        }
    }

    public static GroupTreeNode getTopmostGroup(GroupTreeNode groupTreeNode, String practitionerId, ParticipantTypeEnum type) {
        if (groupTreeNode == null) {
            return null;
        }
        Queue<GroupTreeNode> groupQueue = new LinkedList<>();
        groupQueue.offer(groupTreeNode);
        while (!groupQueue.isEmpty()) {
            GroupTreeNode currentGroupTreeNode = groupQueue.poll();
            for (PractitionerTreeNode practitioner : currentGroupTreeNode.getPractitionerList()) {
                if (practitioner.getId().equals(practitionerId) && type.equals(practitioner.getParticipantType())) {
                    return currentGroupTreeNode;
                }
            }
            currentGroupTreeNode.getSubItems().forEach(group -> groupQueue.offer(group));
        }
        return null;
    }

    public static List<PractitionerTreeNode> getAllPractitionersOfAGroup(GroupTreeNode groupTreeNode, String groupId) {
        if (groupTreeNode == null) {
            return null;
        }
        GroupTreeNode groupInput = searchGroupById(groupTreeNode, groupId);
        if (groupInput == null) {
            return null;
        }
        Set<PractitionerTreeNode> practitionerTreeNodeSet = new HashSet<>();
        Queue<GroupTreeNode> groupQueue = new LinkedList<>();
        groupQueue.offer(groupInput);
        while (!groupQueue.isEmpty()) {
            GroupTreeNode currentGroupTreeNode = groupQueue.poll();
            currentGroupTreeNode.getPractitionerList().forEach(practitioner -> {
                if (ParticipantTypeEnum.PRACTITIONER.equals(practitioner.getParticipantType())) {
                    practitionerTreeNodeSet.add(practitioner);
                }
            });
            currentGroupTreeNode.getSubItems().forEach(group -> groupQueue.offer(group));
        }
        List<PractitionerTreeNode> practitionerIdList = new ArrayList<>();
        practitionerTreeNodeSet.forEach(practitioner -> practitionerIdList.add(practitioner));

        return practitionerIdList;
    }

    public static GroupTreeNode getRegisterGroupByPractitionerId(GroupTreeNode groupTreeNode, String practitionerId) {
        if (groupTreeNode == null) {
            return null;
        }
        List<GroupTreeNode> registerLevelGroups = groupTreeNode.getSubItems();
        //如果二级分组存在，则返回二级分组的组，否则返回所有医生分组
        if(!registerLevelGroups.isEmpty()){
            for (GroupTreeNode gTreeNode : registerLevelGroups) {
                if (searchGroupByPractitionerId(gTreeNode, practitionerId) != null) {
                    return gTreeNode;
                }
            }
        } else {
            for(PractitionerTreeNode practitionerTreeNode : groupTreeNode.getPractitionerList()){
                if(practitionerTreeNode.getId().equals(practitionerId)){
                    return groupTreeNode;
                }
            }
        }

        return null;
    }

    public static List<String> getAllPractitionerIdsOfAGroup(GroupTreeNode groupTreeNode, String groupId) {
        List<PractitionerTreeNode> practitionerTreeNodeList = getAllPractitionersOfAGroup(groupTreeNode, groupId);
        if (practitionerTreeNodeList == null) {
            return null;
        } else {
            List<String> practitionerIdList = new ArrayList<>();
            practitionerTreeNodeList.forEach(practitionerTreeNode -> practitionerIdList.add(practitionerTreeNode.getId()));
            return practitionerIdList;
        }
    }

    /**
     *
     * @param groupTreeNode
     * @param groupId
     * @return
     */
    public static List<String> getPractitionerIdsOfAGroup(GroupTreeNode groupTreeNode, String groupId) {
        List<String> practitionerIdList = new ArrayList<>();
        GroupTreeNode groupInput = searchGroupById(groupTreeNode, groupId);
        List<PractitionerTreeNode> practitionerTreeNodeList = groupInput.getPractitionerList();
        if(practitionerTreeNodeList != null){
            practitionerTreeNodeList.forEach(practitionerTreeNode -> {
                if(ParticipantTypeEnum.PRACTITIONER.equals(practitionerTreeNode.getParticipantType())){
                    practitionerIdList.add(practitionerTreeNode.getId());
                }
            });
        }
        return practitionerIdList;

    }
    /**
     * Get all practitioners of group and sub groups
     *
     * @param groupId the group id
     * @return practitioners
     */
    public static List<String> getAllPractitionerIdsOfAGroup(String groupId) {
        List<String> practitionerIdList = null;
        if (oncologyGroupTreeNode != null) {
            practitionerIdList = getAllPractitionerIdsOfAGroup(oncologyGroupTreeNode, groupId);
        }
        if (practitionerIdList == null && physicistGroupTreeNode != null) {
            practitionerIdList = getAllPractitionerIdsOfAGroup(physicistGroupTreeNode, groupId);
        }
        if (practitionerIdList == null && techGroupTreeNode != null) {
            practitionerIdList = getAllPractitionerIdsOfAGroup(techGroupTreeNode, groupId);
        }
        if (practitionerIdList == null && nurseGroupTreeNode != null) {
            practitionerIdList = getAllPractitionerIdsOfAGroup(nurseGroupTreeNode, groupId);
        }
        return practitionerIdList != null ? practitionerIdList : new ArrayList<>();
    }

    private static GroupTreeNode searchGroupById(GroupTreeNode rootGroupTreeNode, String groupId) {
        if (rootGroupTreeNode == null) {
            return null;
        }
        if (rootGroupTreeNode.getId().equals(groupId)) {
            return rootGroupTreeNode;
        } else {
            for (GroupTreeNode childGroup : rootGroupTreeNode.getSubItems()) {
                GroupTreeNode groupFound = searchGroupById(childGroup, groupId);
                if (groupFound != null) {
                    return groupFound;
                }
            }
            return null;
        }
    }

    public static GroupTreeNode searchGroupById(String groupId) {
        GroupTreeNode groupTreeNode = null;
        groupTreeNode = searchGroupById(copy(getOncologyGroupTreeNode()),groupId);
        if(groupTreeNode == null){
            groupTreeNode = searchGroupById(copy(getNurseGroupTreeNode()),groupId);
        }
        if(groupTreeNode == null){
            groupTreeNode = searchGroupById(copy(getPhysicistGroupTreeNode()),groupId);
        }
        if(groupTreeNode == null){
            groupTreeNode = searchGroupById(copy(getTechGroupTreeNode()),groupId);
        }
        return groupTreeNode;
    }

    /**
     * 将树形结构平铺（先根遍历）
     * @param root
     * @return
     */
    public static List<GroupTreeNode> parallelTreeNode(GroupTreeNode root){
        List<GroupTreeNode> list = new ArrayList<>();
        Queue<GroupTreeNode> stack = new ArrayDeque<>();
        while(root != null){
            list.add(root);
            root.getSubItems().forEach(groupTreeNode -> {
                stack.offer(groupTreeNode);
            });
            root = stack.poll();
        }
        return list;
    }


    //暂时该方法逻辑和取最大组的效果是一样的，以后交换代码顺序可返回一个医生的最小组
    private static GroupTreeNode searchGroupByPractitionerId(GroupTreeNode rootGroupTreeNode, String practitionerId) {
        if (rootGroupTreeNode == null) {
            return null;
        }
        List<PractitionerTreeNode> practitionerTreeNodeList = rootGroupTreeNode.getPractitionerList();
        for (PractitionerTreeNode practitionerTreeNode : practitionerTreeNodeList) {
            if (practitionerTreeNode.getId().equals(practitionerId)) {
                return rootGroupTreeNode;
            }
        }
        for (GroupTreeNode childGroup : rootGroupTreeNode.getSubItems()) {
            GroupTreeNode groupFound = searchGroupByPractitionerId(childGroup, practitionerId);
            if (groupFound != null) {
                return groupFound;
            }
        }
        return null;
    }

    /**
     * @param practitionerId
     * @return
     */
    public static String getTopGroupName(GroupTreeNode groupTreeNode, String practitionerId) {
        String groupName = StringUtils.EMPTY;
        if (groupTreeNode == null) {
            return null;
        }
        Queue<GroupTreeNode> groupQueue = new LinkedList<>();
        groupQueue.offer(groupTreeNode);
        while (!groupQueue.isEmpty()) {
            GroupTreeNode currentGroupTreeNode = groupQueue.poll();
            for (PractitionerTreeNode practitioner : currentGroupTreeNode.getPractitionerList()) {
                if (practitioner.getId().equals(practitionerId)) {
                    String realGroupName = currentGroupTreeNode.getName();
                    if (realGroupName.startsWith(SystemConfigPool.queryGroupOncologistPrefix() + "_")) {
                        // Oncologist_大组_小组
                        String[] subStr = realGroupName.split("_");
                        if (subStr.length > 1) {
                            // 返回 [大组]
                            return subStr[1];
                        }
                    }
                }
            }
            currentGroupTreeNode.getSubItems().forEach(group -> groupQueue.offer(group));
        }
        return groupName;
    }

    public static PractitionerTreeNode getPractitionerTreeNodeByName(GroupTreeNode groupTreeNode, String practitionerName) {
        if (groupTreeNode == null) {
            return null;
        }
        Queue<GroupTreeNode> groupTreeNodeQueue = new LinkedList<>();
        groupTreeNodeQueue.offer(groupTreeNode);
        while (!groupTreeNodeQueue.isEmpty()) {
            GroupTreeNode currentGroupTreeNode = groupTreeNodeQueue.poll();
            for (PractitionerTreeNode practitionerTreeNode : currentGroupTreeNode.getPractitionerList()) {
                if (practitionerTreeNode.getName().equals(practitionerName)) {
                    return practitionerTreeNode;
                }
            }
            for (GroupTreeNode gTreeNode : currentGroupTreeNode.getSubItems()) {
                groupTreeNodeQueue.offer(gTreeNode);
            }
        }
        return null;
    }

//    public static List<String> getSelfAndSubChildrenIdList(GroupTreeNode root, String groupId) {
//        List<GroupTreeNode> list = new ArrayList<>();
//        getSelfAndAllSubTreeNodes(root, groupId, list);
//        List<String> idList = new ArrayList<>();
//        idList.addAll(list.stream().map(groupTreeNode -> groupTreeNode.getId()).collect(Collectors.toList()));
//        return idList;
//    }

//    private static void getSelfAndAllSubTreeNodes(GroupTreeNode groupTreeNode, String groupId, List<GroupTreeNode> subList) {
//        if (groupTreeNode.getId().equals(groupId)) {
//            subList.add(groupTreeNode);
//            if (groupTreeNode.getSubItems().isEmpty()) {
//                return;
//            } else {
//                groupTreeNode.getSubItems().forEach(groupTreeNode1 -> getSelfAndAllSubTreeNodes(groupTreeNode1, groupTreeNode1.getId(), subList));
//            }
//        } else {
//            groupTreeNode.getSubItems().forEach(groupTreeNode1 -> getSelfAndAllSubTreeNodes(groupTreeNode1, groupId, subList));
//        }
//    }

    public static GroupTreeNode copy(GroupTreeNode groupTreeNode){
        if (groupTreeNode == null) {
            return null;
        }
        GroupTreeNode g = new GroupTreeNode(groupTreeNode.getId(),groupTreeNode.getName(),groupTreeNode.getOriginalName());
        groupTreeNode.getPractitionerList().forEach(practitionerTreeNode -> g.addAPractitioner(practitionerTreeNode));
        if(groupTreeNode.getSubItems().isEmpty()){
            return g;
        }
        for(GroupTreeNode gt :groupTreeNode.getSubItems()){
            g.getSubItems().add(copy(gt));
        }
        return g;
    }
    /**
     * query the group which not in the permission
     * @param root
     * @param groupIdList
     * @param needDelGroupIdList
     */
    public static List<String> findDelGroupIdList(GroupTreeNode root,List<String> groupIdList,List<String> needDelGroupIdList){
        if(!groupIdList.contains(root.getId())){
//          the name of root node is not int groupNameList
            if(root.getSubItems().isEmpty()){//if root's sub itmes is empty,the node should be added to del group
                needDelGroupIdList.add(root.getId());
            }else{//if root's node is not empty,find need del group .call method self
                root.getSubItems().forEach(groupTreeNode -> findDelGroupIdList(groupTreeNode,groupIdList,needDelGroupIdList));
                boolean allDel = true;
//              查看节点的所有子节点。如果所有子节点都需要被删除，则将根节点添加到需要删除的group中
                for(GroupTreeNode groupTreeNode : root.getSubItems()){
                    if(!needDelGroupIdList.contains(groupTreeNode.getId())){
                        allDel = false;
                        break;
                    }
                }
                if(allDel){
                    needDelGroupIdList.add(root.getId());
                }
            }
        }
        return needDelGroupIdList;
    }
    /**
     *  clean need del group id from tree
     * @param root
     * @param needDelGroupIdList
     */
    public static void cleanNode(GroupTreeNode root,List<String> needDelGroupIdList){
        Iterator<GroupTreeNode> it = root.getSubItems().iterator();
        while(it.hasNext()){
            if(needDelGroupIdList.contains(it.next().getId())){
                it.remove();
            }
        }
        if(root.getSubItems().isEmpty()){
            if(needDelGroupIdList.contains(root.getId())){
                root.setId(null);
            }
        }
        root.getSubItems().forEach(groupTreeNode -> cleanNode(groupTreeNode,needDelGroupIdList));
    }
}
