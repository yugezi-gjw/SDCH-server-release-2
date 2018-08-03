package com.varian.oiscn.base.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.varian.oiscn.base.practitioner.PractitionerTreeNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupTreeNode {

    private String id;
//  Show name  BB
    private String name;
//  This node is Show or not in client
    private Boolean isShow;
//  Group name in Aria  Oncologist_AA_BB
    @JsonIgnore
    private String originalName;

    private final List<GroupTreeNode> subItems = new ArrayList<>();
    private final List<PractitionerTreeNode> practitionerList = new ArrayList<>();

    public GroupTreeNode(String id, String name,String originalName) {
        this.id = id;
        this.name = name;
        this.originalName = originalName;
        this.isShow = true;
    }
    public void addAChildGroup(GroupTreeNode groupTreeNode) {
        subItems.add(groupTreeNode);
    }

    public void addAPractitioner(PractitionerTreeNode practitionerTreeNode) {
        practitionerList.add(practitionerTreeNode);
    }
}
