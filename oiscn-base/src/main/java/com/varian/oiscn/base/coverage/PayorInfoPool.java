package com.varian.oiscn.base.coverage;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 11/1/2017
 * @Modified By:
 */
public class PayorInfoPool {

    private static Map<String, String> payorInfoMap = new HashMap<>();

    private PayorInfoPool() {

    }

    public static void put(String payorInfoCode, String payorInfoDesc) {
        payorInfoMap.put(payorInfoCode, payorInfoDesc);
    }

    public static String get(String payorInfoDesc) {
        Optional<Map.Entry<String, String>> optional = payorInfoMap.entrySet().stream().
                filter(entry -> StringUtils.equalsIgnoreCase(entry.getValue(), payorInfoDesc)).findAny();
        if(optional.isPresent()) {
            return optional.get().getKey();
        }
        return null;
    }

    public static String getValue(String key) {
        return payorInfoMap.get(key);
    }

    public static Map<String, String> getCachedPayorInfo() {
        return payorInfoMap;
    }

}
