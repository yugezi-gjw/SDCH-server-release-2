package com.varian.oiscn.core.carepath;

/**
 * Created by gbt1220 on 4/20/2017.
 */
public enum CarePathStatusEnum {
    /**
     * The plan has been suggested but no commitment to it has yet been made.
     */
    PROPOSED,
    /**
     * The plan is in development or awaiting use but is not yet intended to be acted upon.
     */
    DRAFT,
    /**
     * The plan is intended to be followed and used as part of patient care.
     */
    ACTIVE,
    /**
     * The plan has been temporarily stopped but is expected to resume in the future.
     */
    SUSPENDED,
    /**
     * The plan is no longer in use and is not expected to be followed or used in patient care.
     */
    COMPLETED,
    /**
     * The plan was entered in error and voided.
     */
    ENTEREDINERROR,
    /**
     * The plan has been terminated prior to reaching completion (though it may have been replaced by a new plan).
     */
    CANCELLED,
    /**
     * The authoring system doesn't know the current state of the care plan.
     */
    UNKNOWN;

    public static CarePathStatusEnum fromCode(String codeString) {
        if ("proposed".equals(codeString)) {
            return PROPOSED;
        } else if ("draft".equals(codeString)) {
            return DRAFT;
        } else if ("active".equals(codeString)) {
            return ACTIVE;
        } else if ("suspended".equals(codeString)) {
            return SUSPENDED;
        } else if ("completed".equals(codeString)) {
            return COMPLETED;
        } else if ("entered-in-error".equals(codeString)) {
            return ENTEREDINERROR;
        } else if ("cancelled".equals(codeString)) {
            return CANCELLED;
        } else {
            return UNKNOWN;
        }
    }

    public static String getDisplay(CarePathStatusEnum status) {
        switch (status) {
            case PROPOSED:
                return "Proposed";
            case DRAFT:
                return "Pending";
            case ACTIVE:
                return "Active";
            case SUSPENDED:
                return "Suspended";
            case COMPLETED:
                return "Completed";
            case ENTEREDINERROR:
                return "Entered In Error";
            case CANCELLED:
                return "Cancelled";
            case UNKNOWN:
                return "Unknown";
            default:
                return "?";
        }
    }
}
