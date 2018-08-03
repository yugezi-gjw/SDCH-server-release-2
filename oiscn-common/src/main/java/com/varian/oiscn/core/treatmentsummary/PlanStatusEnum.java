package com.varian.oiscn.core.treatmentsummary;

/**
 * Created by bhp9696 on 2018/3/13.
 */
public enum PlanStatusEnum {
//    Plan is approved after creation but not validated and scheduled for treatment
    PlanApproval,
    //    Plan is validated and scheduled for Treatment
    TreatApproval,
    Unapproved,
    Retired,
    Rejected,
    Completed,
    CompletedEarly,
//    Not Sure need to check
    ExternalApproval,
    Reviewed,
    UNKNOWN;

    public static PlanStatusEnum code2Enum(String code){
        if("PlanApproval".equalsIgnoreCase(code)){
            return PlanApproval;
        }
        if("Unapproved".equalsIgnoreCase(code)){
            return Unapproved;
        }
        if("Retired".equalsIgnoreCase(code)){
            return Retired;
        }
        if("Rejected".equalsIgnoreCase(code)){
            return Rejected;
        }
        if("Completed".equalsIgnoreCase(code)){
            return Completed;
        }
        if("CompletedEarly".equalsIgnoreCase(code)){
            return CompletedEarly;
        }
        if("ExternalApproval".equalsIgnoreCase(code)){
            return ExternalApproval;
        }
        if("TreatApproval".equalsIgnoreCase(code)){
            return TreatApproval;
        }
        if("Reviewed".equalsIgnoreCase(code)){
            return Reviewed;
        }
        return UNKNOWN;
    }
}
