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
public class FHIRUpdatePatientException extends RuntimeException {
    /**
     * default Id
     */
    private static final long serialVersionUID = 1L;
    protected String errorCode;

    public FHIRUpdatePatientException(Throwable cause) {
        super(cause);
    }

    public FHIRUpdatePatientException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}
