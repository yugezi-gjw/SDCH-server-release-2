package com.varian.oiscn.anticorruption.fhircontext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varian.oiscn.config.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.client.Client;

import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-01-18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientContextFactory.class, JerseyClientBuilder.class})
public class HttpClientContextFactoryTest {

    private Client client;
    private Environment environment;
    private Configuration configuration;
    // private ObjectMapper objectMapper;

    @Before
    public void setup() {
        // objectMapper = new ObjectMapper();
        // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // @Test
    public void givenAEnvironmentWhenSetEnvironment() throws Exception {
        PowerMockito.mockStatic(HttpClientContextFactory.class);
        HttpClientContextFactory singletonMock = PowerMockito.mock(HttpClientContextFactory.class);
        PowerMockito.doNothing().when(singletonMock).setEnvironmentAndConfiguration(environment, configuration);
    }

    // @Test
    public void shouldGenerateClientSingleton() throws Exception {
        PowerMockito.mockStatic(HttpClientContextFactory.class);
        HttpClientContextFactory singletonMock = PowerMockito.mock(HttpClientContextFactory.class);
        client = PowerMockito.mock(Client.class);
        PowerMockito.doReturn(client).when(singletonMock).getHttpClient();
        PowerMockito.when(HttpClientContextFactory.getInstance()).thenReturn(singletonMock);

        Assert.assertEquals(HttpClientContextFactory.getInstance().getHttpClient(), client);
    }

    // @Test
    public void shouldGetHttpClient() throws Exception {
        Client clientMock = PowerMockito.mock(Client.class);
        JerseyClientBuilder jerseyClientBuilder = PowerMockito.mock(JerseyClientBuilder.class);
        PowerMockito.whenNew(JerseyClientBuilder.class).withParameterTypes(Environment.class).withArguments(environment).thenReturn(jerseyClientBuilder);
        PowerMockito.when(jerseyClientBuilder.using(new JerseyClientConfiguration())).thenReturn(jerseyClientBuilder);
        PowerMockito.when(jerseyClientBuilder.using(new ObjectMapper())).thenReturn(jerseyClientBuilder);
        PowerMockito.when(jerseyClientBuilder.build(anyString())).thenReturn(clientMock);

        Assert.assertNotNull(clientMock);
    }

    @Test
    public void testGetInstance() throws Exception {
        HttpClientContextFactory factory01 = HttpClientContextFactory.getInstance();
        Assert.assertNotNull(factory01);
        HttpClientContextFactory factory02 = HttpClientContextFactory.getInstance();
        Assert.assertNotNull(factory02);
        Assert.assertSame(factory01, factory02);
    }

    @Test
    public void testSetEnvironmentAndConfigurationAndGetHttpClient() throws Exception {
        HttpClientContextFactory factory = HttpClientContextFactory.getInstance();
        Environment env = PowerMockito.mock(Environment.class);
        JerseyClientBuilder jerseyClientBuilder = PowerMockito.mock(JerseyClientBuilder.class);
        PowerMockito.whenNew(JerseyClientBuilder.class).withAnyArguments().thenReturn(jerseyClientBuilder);
        Client client = PowerMockito.mock(Client.class);
        PowerMockito.when(jerseyClientBuilder.using(Mockito.any(JerseyClientConfiguration.class))).thenReturn(jerseyClientBuilder);
        PowerMockito.when(jerseyClientBuilder.using(Mockito.any(ObjectMapper.class))).thenReturn(jerseyClientBuilder);
        PowerMockito.when(jerseyClientBuilder.build(Mockito.anyString())).thenReturn(client);

        Configuration conf = PowerMockito.mock(Configuration.class);
        conf.httpClientConfiguration = PowerMockito.mock(JerseyClientConfiguration.class);
        factory.setEnvironmentAndConfiguration(env, conf);
        Client client01 = factory.getHttpClient();
        Assert.assertNotNull(client01);
        Client client02 = factory.getHttpClient();
        Assert.assertNotNull(client02);
        Assert.assertSame(client01, client02);
    }
}
