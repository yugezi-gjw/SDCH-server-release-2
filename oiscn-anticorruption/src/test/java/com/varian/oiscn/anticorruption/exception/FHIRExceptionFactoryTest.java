package com.varian.oiscn.anticorruption.exception;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by bhp9696 on 2018/3/13.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FHIRExceptionFactory.class)
public class FHIRExceptionFactoryTest {
    @Test
    public void testFHIRExceptionBuild(){
        FHIRExceptionBuilderI build = FHIRExceptionFactory.getFHIRExceptionBuilder(FHIRException.FHIRActionEnum.PATIENT_CREATE);
        Assert.assertTrue(build.build(new InternalErrorException("SSNNotUnique")).getAction().equals(FHIRException.FHIRActionEnum.PATIENT_CREATE));
        build = FHIRExceptionFactory.getFHIRExceptionBuilder(FHIRException.FHIRActionEnum.PATIENT_UPDATE);
        Assert.assertTrue(build.build(new InternalErrorException("SSNNotUnique")).getAction().equals(FHIRException.FHIRActionEnum.PATIENT_UPDATE));
        build = FHIRExceptionFactory.getFHIRExceptionBuilder(FHIRException.FHIRActionEnum.UNKNOWN);
        Assert.assertTrue(build.build(new Exception("")).getAction().equals(FHIRException.FHIRActionEnum.UNKNOWN));
    }
}
