package com.varian.oiscn.anticorruption.exception;

/**
 * Created by gbt1220 on 2/9/2018.
 */
public interface FHIRExceptionBuilderI {
    FHIRException build(Exception e);
}
