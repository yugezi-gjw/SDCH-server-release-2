package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.datahelper.MockPractitionerUtil;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.*;

/**
 * Created by fmk9441 on 2017-02-10.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PractitionerAssembler.class})
public class PractitionerAssemblerTest {
    @InjectMocks
    private PractitionerAssembler practitionerAssembler;

    @Test
    public void givenAPractitionerWhenConvertThenReturnPractitionerDto() throws Exception {
        Practitioner practitioner = MockPractitionerUtil.givenAPractitioner();
        PractitionerDto practitionerDto = PractitionerAssembler.getPractitionerDto(practitioner);

        Assert.assertThat(practitionerDto, is(not(nullValue())));
        Assert.assertThat("PractitionerId", is(practitionerDto.getId()));
        Assert.assertThat("PractitionerName", is(practitionerDto.getName()));
    }
}