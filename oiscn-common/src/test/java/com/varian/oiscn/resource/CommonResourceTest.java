package com.varian.oiscn.resource;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.UserContext;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Created by bhp9696 on 2017/11/20.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonResource.class)
public class CommonResourceTest {
    private CommonResource commonResource;
    private Configuration configuration;
    private Environment environment;

    @Before
    public void setup(){
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        commonResource = new CommonResource(configuration,environment);
    }

    @Test
    public void givenWhenGetServerTimeThenReturnCurDateTime(){
        java.util.Date curDateTime = PowerMockito.mock(java.util.Date.class);
        try {
            PowerMockito.whenNew(Date.class).withAnyArguments().thenReturn(curDateTime);
            PowerMockito.when(curDateTime.getTime()).thenReturn(123456789L);
            Response r = commonResource.getServerTime(new UserContext());
            KeyValuePair keyValuePair = (KeyValuePair) r.getEntity();
            Assert.assertNotNull(keyValuePair);
            Assert.assertEquals("123456789",keyValuePair.getValue());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
