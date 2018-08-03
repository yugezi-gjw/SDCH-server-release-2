package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.oiscn.anticorruption.fhircontext.HttpClientContextFactory;
import com.varian.oiscn.anticorruption.osp.authentication.wsdl.Authentication;
import com.varian.oiscn.anticorruption.osp.authentication.wsdl.IAuthentication;
import com.varian.oiscn.anticorruption.osp.authentication.wsdl.UserInfo;
import com.varian.oiscn.anticorruption.osp.authorization.wsdl.*;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;

/**
 * Created by gbt1220 on 12/29/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, Authorization.class, UserAntiCorruptionServiceImp.class, HttpClientContextFactory.class})
public class UserAntiCorruptionServiceImpTest {

    private UserAntiCorruptionServiceImp userAntiCorruptionServiceImp;

    private Client client;

    private HttpClientContextFactory factory;

    private String fhirServerBaseUri = "testFhirServer";

    private String wsdlUri = "testWSDLUri";

    private URL loginUrl;

    @Before
    public void setup() {
        client = PowerMockito.mock(Client.class);
        loginUrl = PowerMockito.mock(URL.class);
        factory = PowerMockito.mock(HttpClientContextFactory.getInstance().getClass());
        userAntiCorruptionServiceImp = new UserAntiCorruptionServiceImp(fhirServerBaseUri, wsdlUri, "");
    }

    @Test
    public void givenAnUserWhenLoginThenReturnAuthenticationInfo() throws Exception {
        User user = new User();

        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(loginUrl);
        PowerMockito.mockStatic(HttpClientContextFactory.class);
        PowerMockito.when(HttpClientContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.getHttpClient()).thenReturn(client);
        WebTarget webTarget = PowerMockito.mock(WebTarget.class);
        PowerMockito.when(client.target(Matchers.any(URI.class))).thenReturn(webTarget);
        Invocation.Builder builder = PowerMockito.mock(Invocation.Builder.class);
        PowerMockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        Response response = PowerMockito.mock(Response.class);

        Login login = new Login();
        login.setToken("token");

        ArgumentCaptor<Entity> entityArgumentCaptor = ArgumentCaptor.forClass(Entity.class);
        PowerMockito.when(builder.post(entityArgumentCaptor.capture())).thenReturn(response);
        PowerMockito.when(response.readEntity(Login.class)).thenReturn(login);

        Login returned = userAntiCorruptionServiceImp.login(user);

        assertThat(returned, is(login));
        assertThat(user, is(entityArgumentCaptor.getValue().getEntity()));
    }

    @Test
    public void givenAnUserWhenLoginThenThrowMalformedURLException() throws Exception {
        User user = new User();
        PowerMockito.whenNew(URL.class).withArguments(anyString()).thenThrow(MalformedURLException.class);
        Login login = userAntiCorruptionServiceImp.login(user);

        Assert.assertNull(login.getUsername());
    }

    @Test
    public void givenAnUserWhenLoginThenThrowURISyntaxException() throws Exception {
        User user = new User();
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(loginUrl);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(loginUrl);
        PowerMockito.mockStatic(HttpClientContextFactory.class);
        PowerMockito.when(HttpClientContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.getHttpClient()).thenReturn(client);
        PowerMockito.when(client.target(loginUrl.toURI())).thenThrow(URISyntaxException.class);
        Login login = userAntiCorruptionServiceImp.login(user);

        Assert.assertNull(login.getUsername());
    }

    @Test
    public void givenAnUserWhenLoginThenThrowException() throws Exception {
        User user = new User();
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(loginUrl);
        PowerMockito.mockStatic(HttpClientContextFactory.class);
        PowerMockito.when(HttpClientContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.getHttpClient()).thenReturn(client);
        PowerMockito.when(client.target(Matchers.any(URI.class))).thenThrow(Exception.class);
        Login login = userAntiCorruptionServiceImp.login(user);

        Assert.assertNull(login.getUsername());
    }

    @Test
    public void givenUserWhenOspLoginAndThrowExceptionThenReturnEmpty() {
        try {
            User user = new User("user", "123456", "");
            PowerMockito.whenNew(URL.class).withArguments(wsdlUri).thenThrow(Exception.class);
            OspLogin ospLogin = userAntiCorruptionServiceImp.ospLogin(user);
            Assert.assertNull(ospLogin.getName());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void givenUserWhenOspLoginThenReturnOspLogin() {
        try {
            URL url = PowerMockito.mock(URL.class);
            User user = new User("user", "123456", "");
            UserInfo userInfo = givenUserInfo();
            PowerMockito.whenNew(URL.class).withArguments(wsdlUri).thenReturn(url);
            Authentication authentication = PowerMockito.mock(Authentication.class);
            PowerMockito.whenNew(Authentication.class).withArguments(url).thenReturn(authentication);
            DummyIAuthentication iAuthentication = PowerMockito.mock(DummyIAuthentication.class);
            PowerMockito.when(authentication.getBasicHttpBindingIAuthentication()).thenReturn(iAuthentication);
            Binding binding = PowerMockito.mock(Binding.class);
            PowerMockito.when(iAuthentication.getBinding()).thenReturn(binding);
            PowerMockito.when(binding.getHandlerChain()).thenReturn(new ArrayList<>());

            PowerMockito.when(iAuthentication.authenticate(user.getUsername(), user.getPassword())).thenReturn(userInfo);
            OspLogin ospLogin = userAntiCorruptionServiceImp.ospLogin(user);
            Assert.assertEquals(userInfo.getSecToken().getValue(), ospLogin.getToken());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testValidateToken() {
        try {
            String ospToken = "abcd";
            URL url = PowerMockito.mock(URL.class);
            PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(url);
            Authorization authorization = PowerMockito.mock(Authorization.class);
            PowerMockito.whenNew(Authorization.class).withAnyArguments().thenReturn(authorization);
            com.varian.oiscn.anticorruption.osp.authorization.wsdl.UserInfo userInfo = PowerMockito.mock(com.varian.oiscn.anticorruption.osp.authorization.wsdl.UserInfo.class);
            IAuthorization basicAuthorization = PowerMockito.mock(DummyIAuthorization.class);
            PowerMockito.when(authorization.getBasicHttpBindingIAuthorization()).thenReturn(basicAuthorization);
            PowerMockito.when(basicAuthorization.validateToken(ospToken)).thenReturn(userInfo);

            userAntiCorruptionServiceImp.validateToken(ospToken);
            Assert.assertNotNull(userAntiCorruptionServiceImp);
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    private UserInfo givenUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(new JAXBElement(new QName("testUri"), String.class, "username"));
        userInfo.setSecToken(new JAXBElement(new QName("testUri"), String.class, "token"));
        userInfo.setUserCUID(new JAXBElement(new QName("testUri"), String.class, "cuid"));
        return userInfo;
    }

    public class DummyIAuthorization implements IAuthorization {

        @Override
        public ArrayOfboolean hasRights(String secToken, ArrayOfstring strPrivilegeid)
                throws IAuthorizationHasRightsServiceFaultFaultFaultMessage {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public com.varian.oiscn.anticorruption.osp.authorization.wsdl.UserInfo validateToken(String secToken)
                throws IAuthorizationValidateTokenServiceFaultFaultFaultMessage {
            // TODO Auto-generated method stub
            return null;
        }

    }
    public interface DummyIAuthentication extends IAuthentication, BindingProvider {

    }
}
