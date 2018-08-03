package com.varian.oiscn.anticorruption.converter;

import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by fmk9441 on 2017-05-11.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DataHelper.class)
public class DataHelperTest {
    @InjectMocks
    private DataHelper dataHelper;

    @Test
    public void givenAValueWhenGetReferenceThenReturnValue() throws Exception {
        Reference reference1 = new Reference();
        Reference reference2 = new Reference();
        reference1.setReference("#Patient/ID");
        reference2.setReference("ID").setDisplay("Patient");
        Assert.assertEquals(DataHelper.getReferenceType(reference1), "Patient");
        Assert.assertEquals(DataHelper.getReferenceValue(reference1), "ID");
        Assert.assertEquals(DataHelper.getReferenceValue("Patient/ID"), "ID");
        Assert.assertEquals(DataHelper.getReferenceValue("#ID"), "ID");
        Assert.assertEquals(DataHelper.getReferenceValue(new Reference()), "");
    }
}
