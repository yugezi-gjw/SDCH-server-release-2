package com.varian.oiscn.core.user;

import lombok.Data;

import java.util.List;

/**
 * Permission Item for a Role.<br>
 */
@Data
public class PermissionItem {

    protected String operation;

    protected List<String> resourceGroups;
}
