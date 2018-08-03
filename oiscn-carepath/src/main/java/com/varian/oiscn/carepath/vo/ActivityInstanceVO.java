package com.varian.oiscn.carepath.vo;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gbt1220 on 4/24/2017.
 */
@Data
public class ActivityInstanceVO {
    private String patientSer;
    private String activityId;
    private String instanceId;
    private String activityType;
    private String activityCode;
    private boolean isActiveInWorkflow;
    private String activityGroupId;
    private String ariaId;
    private String hisId;
    private String nationalId;
    private String chineseName;
    private String englishName;
    private String gender;
    private Date birthday;
    private String telephone;
    private String contactPerson;
    private String contactPhone;
    private String physicianGroupId;
    private String physicianGroupName;
    private String warningText;
    private boolean isUrgent;
    private String physicianId;
    private String physicianName;
    private String physicianBId;
    private String physicianBName;
    private String physicianCId;
    private String physicianCName;
    private String physicianPhone;
    private String progressState;
    private String nextAction;
    private Date preActivityCompletedTime;
    private String preActivityName;
    private String scheduleTime;
    private Date  startTime;
    private boolean confirmedPayment;
    private String workspaceType;
    private String moduleId;
    private String physicianComment;
    /**
     * Patient Source
     */
    private String patientSource;
    /**
     * Insurance Type
     */
    private String insuranceType;

    private String age;
    /**
     * 存储需要动态显示的字段
     * 包含从动态表单中获取的field和值
     */
    private Map<String,String> dynamicField = new HashMap<>();
    
    public void addDynamicField(String fieldName, String fieldValue) {
        if (dynamicField == null) {
            dynamicField = new HashMap<>();
        }
        dynamicField.put(fieldName, fieldValue);
    }
}
