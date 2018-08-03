package com.varian.oiscn.base.codesystem;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by gbt1220 on 7/6/2017.
 */
public class PatientLabelPool {
    private static Map<String, String> patientLabelMap = new HashMap<>();

    private PatientLabelPool() {
    }

    public static void put(String patientLabelCode, String patientLabelDesc) {
        patientLabelMap.put(patientLabelCode, patientLabelDesc);
    }

    public static String get(String patientLabelText) {
        Optional<Map.Entry<String, String>> optional = patientLabelMap.entrySet().stream().filter(entry -> StringUtils.equalsIgnoreCase(entry.getValue(), patientLabelText)).findAny();
        if (optional.isPresent()) {
            return optional.get().getKey();
        }
        return null;
    }
}
