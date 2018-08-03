package com.varian.oiscn.base.applicationlanuch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gbt1220 on 7/20/2017.
 */
public class ApplicationLaunchContentPool {

    private static Map<String, String> contentMap = new HashMap<>();

    private ApplicationLaunchContentPool() {
    }

    /**
     * Put Content into Pool.<br>
     *
     * @param guid    guid
     * @param content Content Text
     */
    public static void put(String guid, String content) {
        contentMap.put(guid, content);
    }

    /**
     * Return Content by guid.<br>
     * @param guid guid
     * @return Content
     */
    public static String get(String guid) {
        return contentMap.get(guid);
    }

    /**
     * Remove Content by guid.<br>
     * @param guid guid
     */
    public static void remove(String guid) {
        contentMap.remove(guid);
    }
}
