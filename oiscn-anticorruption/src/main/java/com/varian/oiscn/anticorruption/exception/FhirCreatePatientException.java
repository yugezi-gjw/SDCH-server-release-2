/**
 *
 */
package com.varian.oiscn.anticorruption.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Fhir Server Exception When creating patient.<br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FhirCreatePatientException extends RuntimeException {
    /**
     * default Id
     */
    private static final long serialVersionUID = 1L;
    protected String errorItemId;

    public FhirCreatePatientException(Throwable cause) {
        super(cause);
    }
}
