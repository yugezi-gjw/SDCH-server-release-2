package com.varian.oiscn.patient.assembler;

import com.varian.oiscn.patient.util.MockDtoUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Created by gbt1220 on 3/29/2017.
 */
public class PatientAssemblerTest {

    @InjectMocks
    private PatientAssembler assembler;

    @Test
    public void givenAPatientDtoWhenThenReturnPatient() {
        Assert.assertNotNull(PatientAssembler.getPatient(MockDtoUtil.givenARegistrationVO()));
    }
}
