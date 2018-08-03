package com.varian.oiscn.application.resources;

/**
 * Created by gbt1220 on 7/27/2017.
 */

import com.varian.oiscn.anticorruption.resourceimps.UserAntiCorruptionServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.OspLogin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RunWith(PowerMockRunner.class)
@PrepareForTest({OspTokenValidateThread.class, Calendar.class})
public class OspTokenValidateThreadTest {
    private Configuration configuration;

    private UserAntiCorruptionServiceImp userAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        configuration = new Configuration();
        configuration.setFhirServerBaseUri("fhirUri");
        configuration.setOspAuthenticationWsdlUrl("authenticationUrl");
        configuration.setOspAuthorizationWsdlUrl("authorizationUrl");
        userAntiCorruptionServiceImp = PowerMockito.mock(UserAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(UserAntiCorruptionServiceImp.class).
                withArguments(Matchers.anyString(), Matchers.anyString(), Matchers.anyString()).
                thenReturn(userAntiCorruptionServiceImp);
    }

    @Test
    public void givenOspTokenListWhenRunThreadThenValidate() {
        List<OspLogin> list = givenOspLoginList();
        PowerMockito.doNothing().when(userAntiCorruptionServiceImp).validateToken(list.get(0).getToken());
        PowerMockito.mockStatic(Calendar.class);
        Date curDate = new Date();
        PowerMockito.when(Calendar.getInstance().getTime()).thenReturn(curDate);
        OspTokenValidateThread ospTokenValidateThread = new OspTokenValidateThread(configuration, list);
        ospTokenValidateThread.run();
        Assert.assertEquals(curDate, list.get(0).getLastModifiedDt());
    }

    private List<OspLogin> givenOspLoginList() {
        List<OspLogin> list = new ArrayList<>();
        OspLogin ospLogin = new OspLogin();
        ospLogin.setUsername("username");
        ospLogin.setDisplayName("displayName");
        ospLogin.setName("name");
        ospLogin.setUserCUID("cuid");
        ospLogin.setToken("token");
        ospLogin.setLastModifiedDt(new Date());
        list.add(ospLogin);
        return list;
    }
}
