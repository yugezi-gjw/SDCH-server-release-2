package com.varian.oiscn.anticorruption.exception;

/**
 * Created by gbt1220 on 2/9/2018.
 */
public class UnknownExceptionBuilder implements FHIRExceptionBuilderI {
    @Override
    public FHIRException build(Exception e) {
        return new FHIRException(
                FHIRException.FHIRActionEnum.UNKNOWN,
                FHIRException.FHIRErrorCodeEnum.UNKNOWN_INTERVAL_ERROR,
                e
        );
    }
}
