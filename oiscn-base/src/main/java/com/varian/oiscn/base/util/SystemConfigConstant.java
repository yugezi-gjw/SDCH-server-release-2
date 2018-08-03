package com.varian.oiscn.base.util;

/**
 * Created by gbt1220 on 9/26/2017.
 */
public class SystemConfigConstant {
	
	public static final String MSG_CHECK_FHIR = "\r\n\tFHIR Error - Please check if FHIR URL is correct, and there are correct settings in Aria!!!\r\n";
    public static final String MSG_CHECK_SYSTEM_CONFIG = "Please check the settings in SystemConfig!";
    
    public static final String INSURANCE_TYPE = "insuranceType";

    public static final String ACTIVITY_REFRESH_TIME = "activityRefreshTime";

    public static final String DEFAULT_DEPARTMENT = "defaultDepartment";

    public static final String TREATMENT_ACTIVITY_CODE = "TreatmentActivityCode";

    public static final String APPOINTMENT_STORED_TO_LOCAL = "appointmentStoredToLocal";

    public static final String VID_PREFIX = "VIDPrefix";

    public static final String VID = "VID";

    public static final String REFRESH_CACHE_FROM_FHIR_INTERVAL = "RefreshCacheFromFHIRInterval";

    public static final String DIAGNOSIS_SEARCH_TOP_N = "DiagnosisSearchTopN";
    public static final String PATIENT_ID_ONE = "PatientIdInAria";

    public static final String PATIENT_ID_TWO = "PatientId2InAria";
    /**
     * Recurring Appointment Time Limit: 50 times by default
     */
    public static final String RECURRING_APPOINTMENT_TIME_LIMIT = "RecurringAppointmentTimeLimit";
    public static final String RECURRING_APPOINTMENT_TIME_LIMIT_DEFAULT_VALUE = "50";

    public static final String COUNT_PER_SLOT = "CountPerSlot";

    /**
     * For Physicists grouping
     */
    public static final String VIEW_ALLPATIENTS_PHYSICIST_CONFIG = "ViewAllPatientsForPhysicist";

    public static final String AUDIT_LOG_ENTRY_NUM_PER_BATCH = "auditLogEntryNumPerBatch";

    /**
     * For MRi care path
     */
//  当前节点Done后，根据实际情况创建MRI流程的节点名称
    public static final String CREATE_MRICAREPATH_ACTIVITYCODE = "CreateMRICarePathActivityCode";
    //    MRI 流程的carePath模板ID
    public static final String MRICAREPATH_TEMPLATEID = "MRICarePathTemplateId";
    //    动态表单中，选择MRI的CheckBox的名称
    public static final String DYNAMICFORM_MRICHECKBOX_NAME = "DynamicFormMRICheckboxName";

//  在Aria中的角色
    public static final String GROUP_ROLE_ONCOLOGIST = "GroupRoleOncologist";
    public static final String GROUP_ROLE_NURSE = "GroupRoleNurse";
    public static final String GROUP_ROLE_PHYSICIST = "GroupRolePhysicist";
    public static final String GROUP_ROLE_THERAPIST = "GroupRoleTherapist";

//  主治医师分组前缀
    public static final String GROUP_ONCOLOGIST_PREFIX= "GroupOncologistPrefix";
//  护士分组前缀
    public static final String GROUP_NURSE_PREFIX= "GroupNursePrefix";
//  物理师分组前缀
    public static final String GROUP_PHYSICIST_PREFIX = "GroupPhysicistPrefix";
//  技师分组前缀
    public static final String GROUP_TECHNICIAN_PREFIX= "GroupTechnicianPrefix";

    public static final String AUDIT_LOG_CATEGORY= "AuditLog";

    public static final String PHYSICIST_GROUPING_ACTIVITY_CODE = "physicistGroupingActivityCode";

}
