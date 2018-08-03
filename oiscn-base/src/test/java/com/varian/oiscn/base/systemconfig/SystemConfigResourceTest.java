package com.varian.oiscn.base.systemconfig;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.varian.oiscn.config.Configuration;

import io.dropwizard.setup.Environment;

/**
 * Created by cxq8822 on Sep. 20, 2017
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SystemConfigResource.class)
public class SystemConfigResourceTest {

    private SystemConfigResource systemConfigResource;

    private Configuration configuration;

    private Environment environment;

    private SystemConfigServiceImp systemConfigServiceImp;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        systemConfigServiceImp = PowerMockito.mock(SystemConfigServiceImp.class);
        PowerMockito.whenNew(SystemConfigServiceImp.class).withNoArguments().thenReturn(systemConfigServiceImp);
        systemConfigResource = new SystemConfigResource(configuration, environment);
    }

    @Test
    public void givenNullOrEmptyNameThenReturnNoContent() {
        String name = null;
        Response response = systemConfigResource.querySystemConfigValueByName(null, name);
        Assert.assertEquals(response.getStatusInfo(), Response.Status.NO_CONTENT);
        name = "";
        response = systemConfigResource.querySystemConfigValueByName(null, name);
        Assert.assertEquals(response.getStatusInfo(), Response.Status.NO_CONTENT);
    }

    @Test
    public void givenNonemptyInputThenReturnCorrectResult() {
        List<String> result = new ArrayList<>();
        result.add("test");
        final String name = "TEST";
        PowerMockito.when(systemConfigServiceImp.queryConfigValueByName(name)).thenReturn(result);
        Response response = systemConfigResource.querySystemConfigValueByName(null, name);
        Assert.assertEquals(response.getStatusInfo(), Response.Status.OK);
        Assert.assertEquals(response.getEntity(), result);
    }

    @Test
    public void queryRecurringAppointmentTimeLimit() {
        final String defaultValue = "33";
        PowerMockito.when(systemConfigServiceImp.queryRecurringAppointmentTimeLimit()).thenReturn(defaultValue);
        Response response = systemConfigResource.queryRecurringAppointmentTimeLimit(null);
        Assert.assertEquals(response.getStatusInfo(), Response.Status.OK);
        Assert.assertEquals(response.getEntity(), defaultValue);
    }
}
