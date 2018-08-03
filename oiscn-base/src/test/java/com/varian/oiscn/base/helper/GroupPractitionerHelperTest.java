package com.varian.oiscn.base.helper;

import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.practitioner.PractitionerTreeNode;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemConfigPool.class})
public class GroupPractitionerHelperTest {

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryGroupRoleNurse()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupRoleOncologist()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupRolePhysicist()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");

        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(MockDtoUtil.givenAPractitionerGroupMap()));
    }

    @Test
    public void givenInputThenReturnCorrectResult() {
        Assert.assertEquals("1", GroupPractitionerHelper.getTopmostGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"1",ParticipantTypeEnum.PRACTITIONER).getId());
        Assert.assertEquals("2", GroupPractitionerHelper.getTopmostGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"2",ParticipantTypeEnum.PRACTITIONER).getId());
        Assert.assertEquals("4", GroupPractitionerHelper.getTopmostGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"6",ParticipantTypeEnum.PRACTITIONER).getId());
    }

    @Test
    public void givenEmptyGroupIdListOrEmptyGroupPractitionerMapThenReturnNullGroupDto() {
        String practitionerId = "";
        Assert.assertEquals(null, GroupPractitionerHelper.getTopmostGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),practitionerId, ParticipantTypeEnum.PRACTITIONER));
        practitionerId = null;
        Assert.assertEquals(null, GroupPractitionerHelper.getTopmostGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),practitionerId,ParticipantTypeEnum.PRACTITIONER));
        practitionerId = "6";
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(new HashMap<>()));
        Assert.assertEquals(null, GroupPractitionerHelper.getTopmostGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),practitionerId,ParticipantTypeEnum.PRACTITIONER));
    }

    @Test
    public void givenGroupIdThenReturnCorrectPractitionerDtoList() {
        List<String> practitionerDtoListBossHead1 = new ArrayList<>();
        practitionerDtoListBossHead1.add("1");
        practitionerDtoListBossHead1.add("10");
        practitionerDtoListBossHead1.add("11");
        practitionerDtoListBossHead1.add("2");
        practitionerDtoListBossHead1.add("3");
        practitionerDtoListBossHead1.add("4");
        practitionerDtoListBossHead1.add("5");
        practitionerDtoListBossHead1.add("6");
        practitionerDtoListBossHead1.add("7");
        practitionerDtoListBossHead1.add("8");
        practitionerDtoListBossHead1.add("9");

        List<String> resultList = GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"1");
        Collections.sort(resultList);
        Assert.assertEquals(practitionerDtoListBossHead1, resultList);

        List<String> practitionerDtoListHead2 = new ArrayList<>();
        practitionerDtoListHead2.add("1");
        practitionerDtoListHead2.add("2");
        practitionerDtoListHead2.add("3");
        practitionerDtoListHead2.add("4");
        practitionerDtoListHead2.add("5");
        practitionerDtoListHead2.add("6");
        practitionerDtoListHead2.add("7");

        resultList = GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"2");
        Collections.sort(resultList);
        Assert.assertEquals(practitionerDtoListHead2, resultList);

        List<String> practitionerDtoListHeadB = new ArrayList<>();
        practitionerDtoListHeadB.add("2");
        practitionerDtoListHeadB.add("6");
        practitionerDtoListHeadB.add("7");

        resultList = GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"4");
        Collections.sort(resultList);
        Assert.assertEquals(practitionerDtoListHeadB, resultList);
    }

    @Test
    public void testGetAllPractitionerIdsOfAGroupByGroupId() throws Exception {
        Assert.assertEquals(0, GroupPractitionerHelper.getAllPractitionerIdsOfAGroup("groupId").size());
    }

    @Test
    public void testSearchGroupById() {
        Assert.assertNotNull(GroupPractitionerHelper.searchGroupById("1"));
    }

    @Test
    public void givenNonExistingGroupIdThenReturnEmptyPractitionerList() {
        Assert.assertEquals(null, GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"10000"));
        Assert.assertEquals(null, GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),null));
    }

    @Test
    public void givenEmptyMapThenReturnEmptyPractitionerList() {
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(new HashMap<>()));
        Assert.assertEquals(null, GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"1"));
    }

    @Test
    public void givenEmptyPractitionerListOrNullPractitionerListThenReturnEmptyPractitionerList() {
        Assert.assertEquals(new ArrayList<String>(), GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"8"));
        Assert.assertEquals(new ArrayList<String>(), GroupPractitionerHelper.getAllPractitionerIdsOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),"9"));
    }

    @Test
    public void givenAPractitionerIdThenReturnRegisterGroup(){
        GroupTreeNode groupTreeNodeHead = new GroupTreeNode("2", "Oncologist_Head","Oncologist_Head");
        Assert.assertEquals(groupTreeNodeHead.getId(), GroupPractitionerHelper.getRegisterGroupByPractitionerId(GroupPractitionerHelper.getOncologyGroupTreeNode(),"7").getId());
        Assert.assertEquals(groupTreeNodeHead.getId(), GroupPractitionerHelper.getRegisterGroupByPractitionerId(GroupPractitionerHelper.getOncologyGroupTreeNode(),"1").getId());
        Assert.assertEquals(groupTreeNodeHead.getId(), GroupPractitionerHelper.getRegisterGroupByPractitionerId(GroupPractitionerHelper.getOncologyGroupTreeNode(),"2").getId());

        GroupTreeNode groupTreeNodeChest = new GroupTreeNode("5", "Oncologist_Chest","Oncologist_Chest");
        Assert.assertEquals(groupTreeNodeChest.getId(), GroupPractitionerHelper.getRegisterGroupByPractitionerId(GroupPractitionerHelper.getOncologyGroupTreeNode(),"8").getId());
        Assert.assertEquals(groupTreeNodeChest.getId(), GroupPractitionerHelper.getRegisterGroupByPractitionerId(GroupPractitionerHelper.getOncologyGroupTreeNode(),"11").getId());
    }

    @Test
    public void testCopy(){
        Assert.assertNull(GroupPractitionerHelper.copy(null));

        GroupTreeNode root = new GroupTreeNode("121","Oncology","Oncology");
        root.addAChildGroup(new GroupTreeNode("122","Oncology_Header","Oncology_Header"));
        root.addAPractitioner(new PractitionerTreeNode("1111","zhaoxin", ParticipantTypeEnum.PRACTITIONER));
        GroupTreeNode copy = GroupPractitionerHelper.copy(root);
        Assert.assertNotNull(copy);
        Assert.assertTrue(copy != root);
        Assert.assertTrue(copy.getId().equals(root.getId()));
        Assert.assertTrue(copy.getPractitionerList().size() == 1);
    }

    @Test
    public void testGetPractitionerIdsOfAGroup() throws Exception {
        GroupTreeNode groupInput = givenRootNode();
        List<String> result = GroupPractitionerHelper.getPractitionerIdsOfAGroup(groupInput, "1");
        Assert.assertEquals(1, result.size());


    }

    @Test
    public void testParallelTreeNode() {
        GroupTreeNode root = givenRootNode();
        Assert.assertTrue(GroupPractitionerHelper.parallelTreeNode(root).size() > 0);
    }

    @Test
    public void testGetTopGroupName() {
        Assert.assertNull(GroupPractitionerHelper.getTopGroupName(null, "1"));

        GroupTreeNode root = givenRootNode();
        Assert.assertEquals("头组", GroupPractitionerHelper.getTopGroupName(root.getSubItems().get(0), "practitionerId2"));
    }

    @Test
    public void testGetPractitionerTreeNodeByName() {
        Assert.assertNull(GroupPractitionerHelper.getPractitionerTreeNodeByName(null, "practitionerId2"));

        GroupTreeNode root = givenRootNode();
        PractitionerTreeNode practitionerTreeNode = GroupPractitionerHelper.getPractitionerTreeNodeByName(root, "practitionerId2");
        Assert.assertEquals("practitionerId2", practitionerTreeNode.getName());

        Assert.assertNull(GroupPractitionerHelper.getPractitionerTreeNodeByName(root, "nonExist"));
    }

    @Test
    public void testFindDelGroupIdList() {
        GroupTreeNode root = givenRootNode();
        List<String> result = GroupPractitionerHelper.findDelGroupIdList(root.getSubItems().get(0), Arrays.asList("1.2"), new ArrayList<>());
        Assert.assertEquals(1, result.size());


        List<String> result2 = GroupPractitionerHelper.findDelGroupIdList(root, Arrays.asList("1.2"), new ArrayList<>());
        Assert.assertEquals(2, result2.size());
    }

    @Test
    public void testCleanNode() {
        GroupTreeNode root = givenRootNode();
        GroupPractitionerHelper.cleanNode(root, Arrays.asList("1.1"));
        Assert.assertEquals(0, root.getSubItems().size());
    }

    private GroupTreeNode givenRootNode() {
        GroupTreeNode root = new GroupTreeNode("1", "Oncologist", "root");
        root.addAPractitioner(new PractitionerTreeNode("practitionerId1", "practitionerId1", ParticipantTypeEnum.PRACTITIONER));

        GroupTreeNode child1 = new GroupTreeNode("1.1", "Oncologist_头组", "child");
        child1.addAPractitioner(new PractitionerTreeNode("practitionerId2", "practitionerId2", ParticipantTypeEnum.PRACTITIONER));
        root.addAChildGroup(child1);
        return root;
    }
}
