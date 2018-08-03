package com.varian.oiscn.anticorruption.exception;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by bhp9696 on 2018/3/13.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRException.class})
public class FHIRExceptionTest {



    @Test
    public void testFHIRException(){
        FHIRException fhirException = new FHIRException(new Throwable(""));
        Assert.assertNull(fhirException.getAction());
        Assert.assertNull(fhirException.getErrorCode());
    }


    @Test
    public void testPatientCreateSSNException(){
        FHIRException fhirException = new FHIRException(FHIRException.FHIRActionEnum.PATIENT_CREATE,FHIRException.FHIRErrorCodeEnum.PAT_DUPLICATE_SSN,new Throwable(""));
        Assert.assertTrue(fhirException.getAction().equals(FHIRException.FHIRActionEnum.PATIENT_CREATE));
        Assert.assertTrue(fhirException.getErrorCode().equals(FHIRException.FHIRErrorCodeEnum.PAT_DUPLICATE_SSN));
    }

    @Test
    public void testPatientCreateInterException(){
        FHIRException fhirException = new FHIRException(FHIRException.FHIRActionEnum.PATIENT_CREATE,FHIRException.FHIRErrorCodeEnum.UNKNOWN_INTERVAL_ERROR,new Throwable(""));
        Assert.assertTrue(fhirException.getAction().equals(FHIRException.FHIRActionEnum.PATIENT_CREATE));
        Assert.assertTrue(fhirException.getErrorCode().equals(FHIRException.FHIRErrorCodeEnum.UNKNOWN_INTERVAL_ERROR));
    }

    @Test
    public void testPatientUpdateException(){
        FHIRException fhirException = new FHIRException(FHIRException.FHIRActionEnum.PATIENT_UPDATE,FHIRException.FHIRErrorCodeEnum.PAT_DUPLICATE_SSN,new Throwable(""));
        Assert.assertTrue(fhirException.getAction().equals(FHIRException.FHIRActionEnum.PATIENT_UPDATE));
        Assert.assertTrue(fhirException.getErrorCode().equals(FHIRException.FHIRErrorCodeEnum.PAT_DUPLICATE_SSN));
    }

    @Test
    public void testPatientUpdateInterException(){
        FHIRException fhirException = new FHIRException(FHIRException.FHIRActionEnum.PATIENT_UPDATE,FHIRException.FHIRErrorCodeEnum.UNKNOWN_INTERVAL_ERROR,new Throwable(""));
        Assert.assertTrue(fhirException.getAction().equals(FHIRException.FHIRActionEnum.PATIENT_UPDATE));
        Assert.assertTrue(fhirException.getErrorCode().equals(FHIRException.FHIRErrorCodeEnum.UNKNOWN_INTERVAL_ERROR));
    }


    @Test
    public void testPatientUnknownException(){
        FHIRException fhirException = new FHIRException(FHIRException.FHIRActionEnum.UNKNOWN,FHIRException.FHIRErrorCodeEnum.UNKNOWN_INTERVAL_ERROR,new Throwable(""));
        Assert.assertTrue(fhirException.getAction().equals(FHIRException.FHIRActionEnum.UNKNOWN));
        Assert.assertTrue(fhirException.getErrorCode().equals(FHIRException.FHIRErrorCodeEnum.UNKNOWN_INTERVAL_ERROR));
    }



}
