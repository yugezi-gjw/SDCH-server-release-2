package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.CarePath;
import com.varian.oiscn.anticorruption.datahelper.MockCarePathUtil;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by fmk9441 on 2017-04-25.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CarePathAssembler.class)
public class CarePathAssemblerTest {
    @InjectMocks
    private CarePathAssembler carePathAssembler;

    @Test
    public void givenACarePathWhenConvertThenReturnCarePathTemplate() {
        CarePath carePath = MockCarePathUtil.givenACarePath();
        CarePathTemplate carePathTemplate = CarePathAssembler.getCPTemplate(carePath);
        Assert.assertNotNull(carePathTemplate);
        Assert.assertEquals(carePathTemplate.getTemplateName(), "TemplateName");
    }

    @Test
    public void givenACarePathWhenConvertThenReturnCarePathInstance() {
        CarePath carePath = MockCarePathUtil.givenACarePath();
        CarePathInstance carePathInstance = CarePathAssembler.getCPInstance(carePath);
        Assert.assertNotNull(carePathInstance);
        Assert.assertNotNull(carePath.getPatient());
    }
}
