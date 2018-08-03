package com.varian.oiscn.application.resources;

import com.varian.oiscn.application.util.MockDtoUtil;
import com.varian.oiscn.base.user.AuthenticationCache;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by gbt1220 on 7/27/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(OspTokenValidateThread.class)
public class OspTokenValidateMonitorThreadTest {

    private AuthenticationCache cache;

    private Configuration configuration;

    @Test
    public void givenCacheWhenRunMonitorThreadThenRun() throws Exception {
        OspTokenValidateThread ospTokenValidateThread = PowerMockito.mock(OspTokenValidateThread.class);
        PowerMockito.whenNew(OspTokenValidateThread.class).withArguments(configuration, Matchers.anyListOf(OspLogin.class)).thenReturn(ospTokenValidateThread);
        PowerMockito.doNothing().when(ospTokenValidateThread).run();
        cache = new AuthenticationCache(10);
        configuration = new Configuration();
        configuration.setOspTokenValidationInterval(10);
        cache.put("token", new UserContext(MockDtoUtil.givenALogin(), MockDtoUtil.givenAnOspLogin()));
        OspTokenValidateMonitorThread monitorThread = new OspTokenValidateMonitorThread(cache, configuration);
        try {
            monitorThread.run();
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
