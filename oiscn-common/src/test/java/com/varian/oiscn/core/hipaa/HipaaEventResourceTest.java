package com.varian.oiscn.core.hipaa;


import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.hipaa.HipaaEvent;
import com.varian.oiscn.util.hipaa.HipaaObjectType;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;

@RunWith(PowerMockRunner.class)
public class HipaaEventResourceTest {

    HipaaEventResource hipaaEventResource;
    private Configuration configuration;

    private Environment environment;
    @Before
    public void setup(){
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        hipaaEventResource = new HipaaEventResource(configuration, environment);
    }
    @Test
    public void givenUserAndHipaaLogMessageDtoThenReturnTrue(){
        Response response = hipaaEventResource.insert(new UserContext(), new HipaaLogMessageDto("hisId", HipaaEvent.Other, HipaaObjectType.Other, "comment"));
        boolean ok = (boolean) response.getEntity();
        Assert.assertTrue(ok);
    }
    @Test
    public void givenHipaaLogMessageDtoThenReturnTrue(){
        Response response = hipaaEventResource.insert(new HipaaLogMessageDto("hisId", HipaaEvent.Other, HipaaObjectType.Other, "comment"));
        boolean ok = (boolean) response.getEntity();
        Assert.assertTrue(ok);
    }
}
