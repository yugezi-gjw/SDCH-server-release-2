package com.varian.oiscn.base.extend;

import com.varian.oiscn.config.ImplementationExtendConfiguration;
import com.varian.oiscn.core.extend.ImplementationExtension;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 11/22/2017.
 */
@RunWith(PowerMockRunner.class)
public class ImplementationExtensionServiceTest {
    private String configFile = "../config/ImplementationExtend.yaml";

    @Test
    public void testGetImplementationClass() {
        ImplementationExtensionService.init(configFile);
        Assert.assertEquals(StringUtils.EMPTY, ImplementationExtensionService.getImplementationClassOf("NoContractClass"));
    }
    
    @Test
    public void testGetImplementationClassOf() {
    	ImplementationExtendConfiguration config = PowerMockito.mock(ImplementationExtendConfiguration.class);
        ImplementationExtensionService.configuration = config;
        
        ImplementationExtension ie = new ImplementationExtension();
        ie.setContract("contract");
        ie.setImplementation("implementation");
        
        ImplementationExtension ie2 = new ImplementationExtension("contract2", "implementation2");
        
        List<ImplementationExtension> extensions = new ArrayList<>();
        extensions.add(ie2);
        extensions.add(ie);
        
        PowerMockito.mock(ImplementationExtension.class);
        PowerMockito.when(config.getExtensions()).thenReturn(extensions);
        
        Assert.assertEquals("implementation2", ImplementationExtensionService.getImplementationClassOf("contract2"));
    }
    
    
}
