package com.varian.oiscn.base.codesystem;

import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.codesystem.CodeValueDTO;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Created by gbt1220 on 7/11/2017.
 */
public class CodeValueAssemblerTest {

    @InjectMocks
    private CodeValueAssembler assembler;

    @Test
    public void givenCodeSystemAndCodeValueWhenAssemblerCodeValueDTOThenReturnDto() {
        CodeSystem codeSystem = MockDtoUtil.givenCodeSystem();
        CodeValue codeValue = new CodeValue("code", "desc");
        CodeValueDTO dto = CodeValueAssembler.assemblerCodeValueDTO(codeSystem, codeValue);
        Assert.assertEquals(dto.getCode(), codeSystem.getCode());
        Assert.assertEquals(dto.getSystem(), codeSystem.getSystem());
        Assert.assertEquals(dto.getValue(), codeValue.getCode());
        Assert.assertEquals(dto.getDesc(), codeValue.getDesc());
    }

    @Test
    public void givenStringWhenDescapeThenReturnValidString() {
        String emptyString = null;
        Assert.assertNull(CodeValueAssembler.htmlDescape(emptyString));

        String validString = "test";
        Assert.assertEquals(validString, CodeValueAssembler.htmlDescape(validString));
    }
}
