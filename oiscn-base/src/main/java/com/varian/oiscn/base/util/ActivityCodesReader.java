package com.varian.oiscn.base.util;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by gbt1220 on 6/6/2017.
 */
@Slf4j
public class ActivityCodesReader {
    private static final String DEFAULT_CONFIG_ACTIVITYCODE = "config/ActivityCode.yaml";
    public static final String COMPLETED_CONTENT = "completedContent";
    public static final String CONTENT = "content";
    public static final String ENTRY_CONTENT = "entryContent";
    public static final String NEED_CHARGE_BILL = "needChargeBill";
    public static final String RELEASE_RESOURCE_FOR_ACTIVITY = "releaseResourceForActivity";
//    public static final String NEED_RELEASE_ASSIGNED_DEVICE = "needReleaseAssignedDevice";
//    public static final String NEED_RELEASE_ASSIGNED_TPS = "needReleaseAssignedTPS";
    public static final String NEED_RESOURCE_FOR_DYNAMIC_FIELD = "needResourceForDynamicField";
    public static final String DYNAMIC_FIELD_LIST_SOURCE_ACTIVITY_CODE = "sourceActivityCode";
    public static final String DYNAMIC_FIELD_LIST_TARGET_FIELD_NAME = "targetFieldName";
    public static final String WORKSPACE = "workspace";
    public static final String WORKSPACE_TYPE = "workspaceType";
    public static final String DEFAULT_APPOINTMENT_VIEW = "defaultAppointmentView";
    public static final String DYNAMIC_FORM_TEMPLATE = "dynamicFormTemplate";
    public static final String TEMPLATE_ID = "templateId";
    public static final String ECLIPSE_MODULE_ID = "eclipseModuleId";
    public static final String DYNAMIC_FORM_DEFAULT_VALUE_TEMPLATE = "defaultValue";
    public static final String NOT_DISPLAYED_IN_PATIENT_LIST = "notDisplayedInPatientList";
    private static List<ActivityCodeConfig> activityCodeConfigs;

    private ActivityCodesReader() {
    }

    public static ActivityCodeConfig getSourceActivityCodeByRelativeCode(String relativeCode) {
        if (activityCodeConfigs == null || activityCodeConfigs.isEmpty() || StringUtils.isEmpty(relativeCode)) {
            return new ActivityCodeConfig();
        }
        for (ActivityCodeConfig activityCodeConfig : activityCodeConfigs) {
            if (StringUtils.equalsIgnoreCase(relativeCode, activityCodeConfig.getRelativeCode())) {
                return activityCodeConfig;
            }
        }
        log.error("Not found the activity code by relative code[" + relativeCode + "] in config file.");
        return new ActivityCodeConfig();
    }

    public static ActivityCodeConfig getActivityCode(String code) {
        if (activityCodeConfigs == null || activityCodeConfigs.isEmpty() || StringUtils.isEmpty(code)) {
            return new ActivityCodeConfig();
        }
        for (ActivityCodeConfig activityCodeConfig : activityCodeConfigs) {
            if (StringUtils.equalsIgnoreCase(code, activityCodeConfig.getName())) {
                return activityCodeConfig;
            }
        }
        log.error("Not found the activity code[" + code + "] in config file.");
        return new ActivityCodeConfig();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void init(Configuration rootConfig) {
        String configFile = rootConfig.getActivityCodeConfigFile();
        if (StringUtils.isEmpty(configFile)) {
            // default value
            configFile = DEFAULT_CONFIG_ACTIVITYCODE;
            log.warn("No ActivityCode Configuration in Local File !!! Using default file [{}]", configFile);
        }
        activityCodeConfigs = new ArrayList<>();
        Map activityCodesMap = null;
        try {
            activityCodesMap = (LinkedHashMap) new Yaml().load(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: {}", e.getMessage());
        }
        if (activityCodesMap == null) {
            return;
        }
        Iterator activityCodesIterator = activityCodesMap.entrySet().iterator();
        ActivityCodeConfig activityCodeConfig;
        while (activityCodesIterator.hasNext()) {
            activityCodeConfig = new ActivityCodeConfig();
            Map.Entry activityCodeEntry = (Map.Entry) activityCodesIterator.next();
            String code = activityCodeEntry.getKey().toString();
            if (StringUtils.contains(code, ">>")) {
                String[] codes = StringUtils.split(code, ">>");
                activityCodeConfig.setName(codes[0]);
                activityCodeConfig.setRelativeCode(codes[1]);
            } else {
                activityCodeConfig.setName(code);
            }
            LinkedHashMap eachActivityCodeMap = (LinkedHashMap) activityCodeEntry.getValue();
            activityCodeConfig.setCompletedContent(eachActivityCodeMap.get(COMPLETED_CONTENT).toString());
            activityCodeConfig.setContent(eachActivityCodeMap.get(CONTENT).toString());
            activityCodeConfig.setEntryContent(eachActivityCodeMap.get(ENTRY_CONTENT).toString());
            Boolean needChargeBill = (Boolean) eachActivityCodeMap.get(NEED_CHARGE_BILL);
            activityCodeConfig.setNeedChargeBill(needChargeBill == null ? false : needChargeBill);
            Boolean notDisplayedInPatientList = (Boolean)eachActivityCodeMap.get(NOT_DISPLAYED_IN_PATIENT_LIST);
            activityCodeConfig.setNotDisplayedInPatientList(notDisplayedInPatientList == null ? false : notDisplayedInPatientList);

//            Boolean needReleaseAssignedDevice = (Boolean) eachActivityCodeMap.get(NEED_RELEASE_ASSIGNED_DEVICE);
//            activityCodeConfig.setNeedReleaseAssignedDevice(needReleaseAssignedDevice == null ? false : needReleaseAssignedDevice);
//            Boolean needReleaseAssignedTPS = (Boolean) eachActivityCodeMap.get(NEED_RELEASE_ASSIGNED_TPS);
//            activityCodeConfig.setNeedReleaseAssignedTPS(needReleaseAssignedTPS == null ? false : needReleaseAssignedTPS);
            activityCodeConfig.setReleaseResourceForActivity((String)eachActivityCodeMap.get(RELEASE_RESOURCE_FOR_ACTIVITY));
            LinkedHashMap needResourceForDynamicField = (LinkedHashMap) eachActivityCodeMap.get(NEED_RESOURCE_FOR_DYNAMIC_FIELD);
            if (needResourceForDynamicField != null) {
                activityCodeConfig.setSourceActivityCode((String)needResourceForDynamicField.get(DYNAMIC_FIELD_LIST_SOURCE_ACTIVITY_CODE));
                activityCodeConfig.setTargetFieldName((String)needResourceForDynamicField.get(DYNAMIC_FIELD_LIST_TARGET_FIELD_NAME));
            }
            
            LinkedHashMap workspaceMap = (LinkedHashMap) eachActivityCodeMap.get(WORKSPACE);
            activityCodeConfig.setWorkspaceType(workspaceMap.get(WORKSPACE_TYPE).toString());
            if(workspaceMap.get(DEFAULT_APPOINTMENT_VIEW) != null){
                activityCodeConfig.setDefaultAppointmentView(workspaceMap.get(DEFAULT_APPOINTMENT_VIEW).toString());
            }
            if (workspaceMap.get(DYNAMIC_FORM_TEMPLATE) != null) {
                List<LinkedHashMap> templateIds = (ArrayList) workspaceMap.get(DYNAMIC_FORM_TEMPLATE);
                if (templateIds != null) {
                    for (LinkedHashMap templateIdMap : templateIds) {
                        if (templateIdMap.get(TEMPLATE_ID) != null) {
                            String dynamicFormTemplateId = (String)templateIdMap.get(TEMPLATE_ID);
                            activityCodeConfig.addDynamicFormTemplateId(dynamicFormTemplateId);
                            List<String> defaultValueTemplateIdList = new ArrayList<>();
                            if(templateIdMap.get(DYNAMIC_FORM_DEFAULT_VALUE_TEMPLATE) != null){
                                List<Map<String,String>> defaultValueIdList = (List<Map<String,String>>)templateIdMap.get(DYNAMIC_FORM_DEFAULT_VALUE_TEMPLATE);
                                for (Map<String, String> stringStringMap : defaultValueIdList) {
                                    defaultValueTemplateIdList.add(stringStringMap.get(TEMPLATE_ID));
                                }
                            }
                            activityCodeConfig.addTemplateDefaultValues(dynamicFormTemplateId,defaultValueTemplateIdList);
                        }
                    }
                }
            }
            if (workspaceMap.get(ECLIPSE_MODULE_ID) != null) {
                activityCodeConfig.setEclipseModuleId((String) workspaceMap.get(ECLIPSE_MODULE_ID));
            }
            activityCodeConfigs.add(activityCodeConfig);
        }
    }

    public static List<ActivityCodeConfig> getNeedChargeBillActivityCodeList() {
        if (activityCodeConfigs == null || activityCodeConfigs.isEmpty()) {
            return new ArrayList<>();
        }
        List<ActivityCodeConfig> activityCodeConfigList = new ArrayList<>();
        activityCodeConfigs.forEach(activityCode -> {
            if (activityCode.getNeedChargeBill()) {
                activityCodeConfigList.add(activityCode);
            }
        });
        return activityCodeConfigList;
    }
}
