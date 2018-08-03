package com.varian.oiscn.base.util;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gbt1220 on 11/6/2017.
 */
@Slf4j
public class DeviceUtil {

    private DeviceUtil(){}

    /**
     * Get devices from multi-carepath by activity code
     * @param defaultTemplateName the default carepath template
     * @param activityCode the activity code
     * @return device id list
     */
    public static List<String> getDevicesByActivityCode(String defaultTemplateName, String activityCode) {
        List<String> deviceIds = new ArrayList<>();
        if (StringUtils.isEmpty(activityCode)) {
            log.error("The activity code is null, can't get devices from the code.");
            return deviceIds;
        }
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();


        CarePathTemplate carePathTemplate;
        Set<String> deviceIdSet = new HashSet<>();
        carePathTemplate = carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName);
        if (carePathTemplate != null) {
            carePathTemplate.getActivities().forEach(plannedActivity -> {
                        if (StringUtils.equalsIgnoreCase(plannedActivity.getActivityCode(), activityCode) &&
                                plannedActivity.getDeviceIDs() != null) {
                            deviceIdSet.addAll(plannedActivity.getDeviceIDs());
                        }
                    }
            );
        }

        deviceIds.addAll(deviceIdSet);
        deviceIdSet.clear();
        return deviceIds;
    }
}
