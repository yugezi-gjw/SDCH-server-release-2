package com.varian.oiscn.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by BHP9696 on 2017/9/12.
 */
@Slf4j
public class I18nReader {
    private static Object lock = new Object();
    private static ResourceBundle resources;

    private I18nReader() {
    }

    public static String getLocaleValueByKey(String key) {
        if (resources == null || resources.keySet().isEmpty()) {
            synchronized (lock) {
                init();
            }
        }
        return resources.getString(key);
    }

    private static void init() {
        if (resources == null || resources.keySet().isEmpty()) {
            resources = ResourceBundle.getBundle("MessageResource", Locale.getDefault());
        }
    }
}
