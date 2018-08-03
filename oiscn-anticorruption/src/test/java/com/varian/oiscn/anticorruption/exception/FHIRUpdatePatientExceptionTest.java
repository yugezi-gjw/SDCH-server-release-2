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
@PrepareForTest(FHIRUpdatePatientException.class)
public class FHIRUpdatePatientExceptionTest {

    @Test
    public void testCreateException(){
        FHIRUpdatePatientException fhirUpdatePatientException =new FHIRUpdatePatientException(new Exception("error"));
        fhirUpdatePatientException.getErrorCode();
        String msg = fhirUpdatePatientException.getMessage();
        Assert.assertTrue(msg.contains("error"));
        fhirUpdatePatientException =  new FHIRUpdatePatientException("errorCode",new Exception("error-error"));
        String errorCode = fhirUpdatePatientException.getErrorCode();
        Assert.assertTrue("errorCode".equals(errorCode));
        msg = fhirUpdatePatientException.getMessage();
        Assert.assertTrue(msg.contains("error-error"));

    }
}
