package com.varian.oiscn.base.statusicon;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by gbt1220 on 6/23/2017.
 */
public class StatusIconPool {
    private static Map<String, String> statusIconMap = new HashMap<>();

    private StatusIconPool() {
    }

    public static void put(String statusIconCode, String statusIconDesc) {
        statusIconMap.put(statusIconCode, statusIconDesc);
    }

    public static String get(String statusIconDesc) {
        Optional<Map.Entry<String, String>> optional = statusIconMap.entrySet().stream().
                filter(entry -> StringUtils.equalsIgnoreCase(entry.getValue(), statusIconDesc)).findAny();
        if (optional.isPresent()) {
            return optional.get().getKey();
        }
        return null;
    }
}
