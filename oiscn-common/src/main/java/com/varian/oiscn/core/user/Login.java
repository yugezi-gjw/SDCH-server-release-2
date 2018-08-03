package com.varian.oiscn.core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Login Information.<br>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Login {
    private String name;
    private String username;
    private Long resourceSer;
    private String resourceName;
    private String group;

    private Map<String, String> view;
    private String token;
    private List<String> staffGroups;

    private List<String> permissionList;

//  Object type is GroupTreeNode
    @JsonIgnore
    private Object patientAuthTree;
    @JsonIgnore
    private List<String> permissionGroupIdList;
}
