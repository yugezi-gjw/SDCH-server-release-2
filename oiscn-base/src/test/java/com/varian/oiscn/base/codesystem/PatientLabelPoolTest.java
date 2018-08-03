package com.varian.oiscn.base.codesystem;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Created by gbt1220 on 7/6/2017.
 */
public class PatientLabelPoolTest {

    @InjectMocks
    private PatientLabelPool patientLabelPool;

    @Test
    public void givenMapAndPutWhenGetThenReturnValue() {
        PatientLabelPool.put("code", "desc");
        Assert.assertEquals("code", PatientLabelPool.get("desc"));
        Assert.assertNull(PatientLabelPool.get("notExistedDesc"));
    }
}
