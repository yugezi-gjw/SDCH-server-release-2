/**
 *
 */
package com.varian.oiscn.anticorruption.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FHIR Server Exception When updating patient.<br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FHIRException extends RuntimeException {

    /**
     * default Id
     */
    private static final long serialVersionUID = 1L;
    protected FHIRActionEnum action;
    protected FHIRErrorCodeEnum errorCode;

    public FHIRException(Throwable cause) {
        super(cause);
    }

    public FHIRException(FHIRActionEnum action, FHIRErrorCodeEnum errorCode, Throwable cause) {
        super(cause);
        this.action = action;
        this.errorCode = errorCode;
    }

    public enum FHIRActionEnum {
        PATIENT_CREATE,
        PATIENT_UPDATE,
        UNKNOWN
    }

    public enum FHIRErrorCodeEnum {
        UNKNOWN_INTERVAL_ERROR("000", "Interval error in FHIR"),
        PAT_DUPLICATE_SSN("Pat_001", "Duplicate SSN for patient.");
        private final String code;
        private final String reason;

        FHIRErrorCodeEnum(final String statusCode, final String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
        }

        public String getCode() {
            return code;
        }

        public String getReason() {
            return reason;
        }
    }
}
