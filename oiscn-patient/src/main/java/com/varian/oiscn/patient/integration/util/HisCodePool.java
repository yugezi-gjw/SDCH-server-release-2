package com.varian.oiscn.patient.integration.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/21/2017
 * @Modified By:
 */
public class HisCodePool {

    private static Map<String, String> codePool = new HashMap<>();

    public static void put(String code, String desc) {
        codePool.put(code, desc);
    }

    public static String get(String desc) {
        Optional<Map.Entry<String, String>> optional = codePool.entrySet().stream().filter(entry -> StringUtils.equalsIgnoreCase(entry.getValue(), desc)).findAny();
        if (optional.isPresent()) {
            return optional.get().getKey();
        }
        return null;
    }

    public static String getValue(String key) {
        return codePool.get(key);
    }

}
