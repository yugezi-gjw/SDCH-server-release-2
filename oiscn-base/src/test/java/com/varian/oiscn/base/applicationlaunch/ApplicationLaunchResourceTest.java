package com.varian.oiscn.base.applicationlaunch;

import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.applicationlanuch.ApplicationLaunchContentBuilder;
import com.varian.oiscn.base.applicationlanuch.ApplicationLaunchContentPool;
import com.varian.oiscn.base.applicationlanuch.ApplicationLaunchResource;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.security.util.SHACoder;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by gbt1220 on 7/27/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*"})
@PrepareForTest({ApplicationLaunchResource.class, SHACoder.class, ApplicationLaunchContentBuilder.class, PatientIdMapper.class})
public class ApplicationLaunchResourceTest {

    private Configuration configuration;

    private Environment environment;

    private ApplicationLaunchResource resource;

    private UserContext userContext;
    private Login login;
    private OspLogin ospLogin;

    @Before
    public void setup() {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        resource = new ApplicationLaunchResource(configuration, environment);
        userContext = PowerMockito.mock(UserContext.class);
        login = PowerMockito.mock(Login.class);
        PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
        PowerMockito.when(userContext.getLogin()).thenReturn(login);
        PowerMockito.when(userContext.getName()).thenReturn("sysadmin");

        ospLogin = PowerMockito.mock(OspLogin.class);
        PowerMockito.when(ospLogin.getToken()).thenReturn("ospLoginToken");
        PowerMockito.when(userContext.getOspLogin()).thenReturn(ospLogin);
    }

    @Test
    public void givenEmptyModuleIdWhenGetGuidThenReturnBadRequest() {
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        Response response = resource.getGuid(request, userContext, "", "", "", "", "", null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenEmptyGuidWhenLaunchThenReturnBadRequest() {
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        Response response = resource.launch(request, "");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenEmptyOspTokenWhenGetGuidThenReturnBadRequest() {
        UserContext emptyUserContext = PowerMockito.mock(UserContext.class);
        Login login = PowerMockito.mock(Login.class);
        PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
        PowerMockito.when(emptyUserContext.getLogin()).thenReturn(login);
        PowerMockito.when(emptyUserContext.getName()).thenReturn("sysadmin");

        OspLogin emptyOspLogin = PowerMockito.mock(OspLogin.class);
        PowerMockito.when(emptyOspLogin.getToken()).thenReturn("");
        PowerMockito.when(emptyUserContext.getOspLogin()).thenReturn(emptyOspLogin);

        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        Response response = resource.getGuid(request, emptyUserContext, "moduleId", "taskId", "", "activityCode", "patientName", 111L);
        Assert.assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenWhenGetGuidThenReturnOk() throws Exception {
        String serverIp = "testServerIP";
        PowerMockito.when(configuration.getServerAddressOfCCIP()).thenReturn(serverIp);
        DefaultServerFactory serverFactory = PowerMockito.mock(DefaultServerFactory.class);
        PowerMockito.when(configuration.getServerFactory()).thenReturn(serverFactory);
        HttpConnectorFactory httpConnectionFactory = PowerMockito.mock(HttpConnectorFactory.class);
        List<ConnectorFactory> httpConnectorFactories = new ArrayList<>();
        httpConnectorFactories.add(httpConnectionFactory);
        PowerMockito.when(serverFactory.getApplicationConnectors()).thenReturn(httpConnectorFactories);
        int port = 55070;
        PowerMockito.when(httpConnectionFactory.getPort()).thenReturn(port);
        PowerMockito.mockStatic(SHACoder.class);
        String sha256Url = "testSha256Url";
        try {
            PowerMockito.when(SHACoder.encodeSHA256(Matchers.anyObject())).thenReturn(sha256Url);
        } catch (Exception e) {
            Assert.fail();
        }
        OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(orderAntiCorruptionServiceImp);
        PowerMockito.when(orderAntiCorruptionServiceImp.updateOrder(Matchers.anyObject())).thenReturn("taskId");
//        PowerMockito.mockStatic(ApplicationLaunchContentBuilder.class);
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        UserContext userContext = new UserContext(MockDtoUtil.givenALogin(), MockDtoUtil.givenAnOspLogin());
        PowerMockito.mockStatic(PatientIdMapper.class);
        PowerMockito.when(PatientIdMapper.getPatientId1Mapper()).thenReturn(PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID);
        Response response = resource.getGuid(request, userContext, "moduleId", "taskId", "", "activityCode", "patientName", 111L);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenGuidWhenLaunchThenReturnTheContent() {
        ApplicationLaunchContentPool.put("testGuid", "testContent");
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        Response response = resource.launch(request, "testGuid");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        assertThat(response.getEntity(), is("testContent"));
    }
}
