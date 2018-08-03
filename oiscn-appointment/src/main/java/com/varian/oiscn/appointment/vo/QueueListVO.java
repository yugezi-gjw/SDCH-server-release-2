package com.varian.oiscn.appointment.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by gbt1220 on 10/23/2017.
 */
@Data
public class QueueListVO {
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
    private String patientSource;
    private String insuranceType;
    private String status;

    private String checkInStatus;
    private Integer checkInIdx;
    private String age;
}
