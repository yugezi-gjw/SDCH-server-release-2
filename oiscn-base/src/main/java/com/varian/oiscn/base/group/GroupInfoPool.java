package com.varian.oiscn.base.group;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 2/24/2018
 * @Modified By:
 */
public class GroupInfoPool {

    private static Map<String, String> groupInfoMap = new HashMap<>();

    private GroupInfoPool() {

    }

    public static void put(String groupID, String groupName) {
        groupInfoMap.put(groupID, groupName);
    }

    public static String get(String groupName) {
        Optional<Map.Entry<String, String>> optional = groupInfoMap.entrySet().stream().
                filter(entry -> StringUtils.equalsIgnoreCase(entry.getValue(), groupName)).findAny();
        if(optional.isPresent()) {
            return optional.get().getKey();
        }
        return null;
    }

    public static String getValue(String key) {
        return groupInfoMap.get(key);
    }

}
