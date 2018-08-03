package com.varian.oiscn.base.user;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.PermissionItem;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * User Permission Service.<br>
 */
@Slf4j
public class PermissionService {
    
    protected static final String DEFAULT_PERMISSION_CONFIG_FILE = "config/Permission.yaml";
    protected static final String KEY_OPERATION = "operation";
    protected static final String KEY_RESOURCE = "resourceGroups";
    protected static final String VIEW_PATIENT_LIST = "ViewPatientList";

    /**
     * Permission List: <groupName, Privileged Group List>
     */
    protected static Map<String, List<PermissionItem>> groupPermissionMap = new HashMap<>();

    
    /**
     * Initial Permission.<br>
     * @param rootConfig
     */
    public static void init(Configuration rootConfig) {
        final String configFile = rootConfig.getPermissionConfigFile();
        
        // On Permission file, would shutdown the server!
        if (configFile == null) {
            log.warn("No Permission Configuration found in local File !!!");
            // TODO not yet implemented.
            generateDefaultPermissionFile();
            loadConfigFile(DEFAULT_PERMISSION_CONFIG_FILE);
        } else {
            loadConfigFile(configFile);
        }
    }

    /**
     * Get operation list of a group.
     * @param groupName
     * @return
     */
    public static List<String> getOperationListByGroup(String groupName) {
        List<String> operationList = new ArrayList<>();
        if (!groupPermissionMap.containsKey(groupName)) {
            return operationList;
        }
        groupPermissionMap.get(groupName).forEach(permissionItem -> operationList.add(permissionItem.getOperation()));
        return operationList;
    }

    public static List<String> getResourceGroupsOfViewPatientList(String groupName) {
        List<String> resourceGroupList = new ArrayList<>();
        if (!groupPermissionMap.containsKey(groupName)) {
            return resourceGroupList;
        }
        for (PermissionItem permissionItem : groupPermissionMap.get(groupName)) {
            if (StringUtils.equalsIgnoreCase(permissionItem.getOperation(), VIEW_PATIENT_LIST)) {
                resourceGroupList = permissionItem.getResourceGroups();
                break;
            }
        }
        return resourceGroupList;
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, List<PermissionItem>> loadConfigFile(String configFile) {
        Map<String, List<PermissionItem>> listMap = new HashMap<>();
        if (StringUtils.isNotBlank(configFile)) {
            File file = new File(configFile);
            if (file != null && file.exists() && file.isFile()) {
                try {
                    @Cleanup
                    FileInputStream fis = new FileInputStream(file);
                    Yaml yaml = new Yaml();
                    LinkedHashMap content = (LinkedHashMap)yaml.load(fis);
                    combinePermission(content);
                } catch (Exception e) {
                    log.error("Bad Permission Configuration File: {}", e.getMessage());
                    // throw new RuntimeException(e.getMessage());
                }
            }
        }
        return listMap;
    }

    /**
     * Fetch Practitioner Group and write default permission to config folder.<br>
     */
    protected static void generateDefaultPermissionFile() {
        log.info("Generate the Default Permission File [{}]!", DEFAULT_PERMISSION_CONFIG_FILE);
    }

    @SuppressWarnings("unchecked")
    protected static void combinePermission(LinkedHashMap content) {
        if (content == null || content.size() == 0) {
            log.error("File is BLANK, No Permission Configuration Found!");
            // throw new RuntimeException("No Permission Configuration Found");
            return;
        }
        Map<String, PermissionItem> ret = new HashMap<>();
        Iterator it = content.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry groupEntry = (Map.Entry) it.next();
            String actorGroup = groupEntry.getKey().toString();
            Object operations = groupEntry.getValue();
            if (operations instanceof List) {
                List<LinkedHashMap> operationList = (List<LinkedHashMap>) operations;
                operationList.forEach(operationMap -> {
                    PermissionItem item;
                    String operation = (String) operationMap.get(KEY_OPERATION);
                    Object resourceGroups = operationMap.get(KEY_RESOURCE);
                    item = new PermissionItem();
                    item.setOperation(operation);
                    item.setResourceGroups(new ArrayList<>());
                    if (resourceGroups != null && resourceGroups instanceof List) {
                        List<String> resourceGroupList = (List<String>) resourceGroups;
                        item.setResourceGroups(resourceGroupList);
                    }
                    addPermissionItem(actorGroup, item);
                });
            }
        }
    }

    private static void addPermissionItem(String groupName, PermissionItem item) {
        if (!groupPermissionMap.containsKey(groupName)) {
            groupPermissionMap.put(groupName, new ArrayList<>());
        }
        groupPermissionMap.get(groupName).add(item);
    }
}