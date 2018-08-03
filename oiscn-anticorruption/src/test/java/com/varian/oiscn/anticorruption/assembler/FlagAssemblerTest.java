package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Flag;
import com.varian.oiscn.anticorruption.converter.DataHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.*;

/**
 * Created by fmk9441 on 2017-06-23.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FlagAssembler.class)
public class FlagAssemblerTest {
    @InjectMocks
    private FlagAssembler flagAssembler;

    @Test
    public void givenAPatientIdAndFlagCodeWhenConvertThenReturnFlag() {
        final String patientID = "PatientID";
        final String flagCode = "FlagCode";
        Flag flag = FlagAssembler.getFlag(patientID, flagCode);
        Assert.assertThat(flag, is(not(nullValue())));
        Assert.assertTrue(flag.hasSubject());
        Assert.assertEquals(patientID, DataHelper.getReferenceValue(flag.getSubject()));
    }
}
