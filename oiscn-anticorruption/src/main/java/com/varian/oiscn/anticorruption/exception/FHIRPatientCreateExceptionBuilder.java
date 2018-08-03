package com.varian.oiscn.anticorruption.exception;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;

/**
 * Created by gbt1220 on 2/9/2018.
 */
public class FHIRPatientCreateExceptionBuilder implements FHIRExceptionBuilderI {
    @Override
    public FHIRException build(Exception e) {
        FHIRException.FHIRErrorCodeEnum errorCode = FHIRException.FHIRErrorCodeEnum.UNKNOWN_INTERVAL_ERROR;
        if (e instanceof InternalErrorException) {
            InternalErrorException fhirServerException = (InternalErrorException) e;
            String body = fhirServerException.getResponseBody();
            if (body != null && body.contains("SSNNotUnique")) {
                errorCode = FHIRException.FHIRErrorCodeEnum.PAT_DUPLICATE_SSN;
            }
        }
        return new FHIRException(
                FHIRException.FHIRActionEnum.PATIENT_CREATE,
                errorCode,
                e
        );
    }
}
