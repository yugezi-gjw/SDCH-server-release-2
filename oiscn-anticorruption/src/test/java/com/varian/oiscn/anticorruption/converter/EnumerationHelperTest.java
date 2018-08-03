package com.varian.oiscn.anticorruption.converter;

import org.hl7.fhir.dstu3.model.Enumerations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by fmk9441 on 2017-01-18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({EnumerationHelper.class})
public class EnumerationHelperTest {
    @InjectMocks
    private EnumerationHelper enumerationHelper;

    @Test
    public void givenAGenderWhenConvertThenReturnFhirGender() throws Exception {
        Assert.assertEquals(EnumerationHelper.getGender("Male"), Enumerations.AdministrativeGender.MALE);
        Assert.assertEquals(EnumerationHelper.getGender("Female"), Enumerations.AdministrativeGender.FEMALE);
        Assert.assertEquals(EnumerationHelper.getGender("Other"), Enumerations.AdministrativeGender.OTHER);
        Assert.assertEquals(EnumerationHelper.getGender("Unknown"), Enumerations.AdministrativeGender.UNKNOWN);
    }
}
