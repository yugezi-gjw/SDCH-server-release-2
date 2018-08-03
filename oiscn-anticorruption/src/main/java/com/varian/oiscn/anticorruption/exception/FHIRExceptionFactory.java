package com.varian.oiscn.anticorruption.exception;

/**
 * Created by gbt1220 on 2/9/2018.
 */
public class FHIRExceptionFactory {

    private FHIRExceptionFactory() {
    }

    public static FHIRExceptionBuilderI getFHIRExceptionBuilder(FHIRException.FHIRActionEnum action) {
        switch (action) {
            case PATIENT_CREATE:
                return new FHIRPatientCreateExceptionBuilder();
            case PATIENT_UPDATE:
                return new FHIRPatientUpdateExceptionBuilder();
            default:
                return new UnknownExceptionBuilder();
        }
    }
}
