package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.ValueSet;
import com.varian.oiscn.anticorruption.datahelper.MockValueSetUtil;
import com.varian.oiscn.core.codesystem.CodeSystem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;

/**
 * Created by fmk9441 on 2017-06-19.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ValueSetAssembler.class)
public class ValueSetAssemblerTest {
    @InjectMocks
    private ValueSetAssembler valueSetAssembler;

    @Test
    public void givenAValueSetWhenConvertThenReturnCodeSystem() {
        ValueSet valueSet = MockValueSetUtil.givenAValueSet();
        CodeSystem codeSystem = ValueSetAssembler.getCodeSystem(valueSet);
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }
}
