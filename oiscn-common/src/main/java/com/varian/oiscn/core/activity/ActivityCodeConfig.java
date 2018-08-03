package com.varian.oiscn.core.activity;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 6/6/2017.
 */
@Data
public class ActivityCodeConfig {
    private String name;
    private String entryContent;
    private String content;
    private String completedContent;
    private String workspaceType;
    private String defaultAppointmentView;
    private List<String> dynamicFormTemplateIds;
    private Map<String,List<String>> templateDefaultValues;
    private String eclipseModuleId;
    private String relativeCode;
    private Boolean needChargeBill;
    private String releaseResourceForActivity;
    private Boolean notDisplayedInPatientList = false;
    /** dynamic Field List <ActivityCode, fieldName> */
    private String sourceActivityCode;
    private String targetFieldName;
    
    public void addDynamicFormTemplateId(String templateId) {
        if (dynamicFormTemplateIds == null) {
            dynamicFormTemplateIds = new ArrayList<>();
        }
        dynamicFormTemplateIds.add(templateId);
    }

    public void addTemplateDefaultValues(String dynamicFormTemplateId ,List<String> defaultValueList){
        if(templateDefaultValues == null){
            templateDefaultValues = new LinkedHashMap<>();
        }
        if(!templateDefaultValues.containsKey(dynamicFormTemplateId)) {
            templateDefaultValues.put(dynamicFormTemplateId, defaultValueList);
        }
    }
}
