package com.varian.oiscn.anticorruption.fhircontext;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import com.varian.fhir.common.Stu3ContextHelper;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.FhirServerConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URISyntaxException;

/**
 * Created by fmk9441 on 2017-01-18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRContextFactory.class, Stu3ContextHelper.class})
public class FHIRContextFactoryTest {
    private Configuration configuration;
    private String fhirUri = "FHIRURL";
    private IGenericClient iGenericClient;

    @Before
    public void setup() {
        configuration = PowerMockito.mock(Configuration.class);
    }

    // @Test
    public void shouldGenerateClientSingleton() {
        PowerMockito.when(configuration.getFhirApiUri()).thenReturn(fhirUri);
        PowerMockito.mockStatic(FHIRContextFactory.class);
        FHIRContextFactory instance = Mockito.mock(FHIRContextFactory.class);
        iGenericClient = PowerMockito.mock(IGenericClient.class);
        PowerMockito.doReturn(iGenericClient).when(instance).newRestfulGenericClient();
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(instance);

        Assert.assertEquals(FHIRContextFactory.getInstance().newRestfulGenericClient(), iGenericClient);
    }

    // @Test
    public void generateClientThrowURISyntaxException() {
        PowerMockito.mockStatic(FHIRContextFactory.class);
        FHIRContextFactory instance = Mockito.mock(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(instance);
        PowerMockito.when(configuration.getFhirApiUri()).thenThrow(URISyntaxException.class);

        Assert.assertNull(FHIRContextFactory.getInstance().newRestfulGenericClient());
    }

    // @Test
    public void shouldGetXmlParser() {
        PowerMockito.mockStatic(FHIRContextFactory.class);
        FHIRContextFactory singletonMock = Mockito.mock(FHIRContextFactory.class);
        IParser iParser = PowerMockito.mock(IParser.class);
        PowerMockito.when(singletonMock.getXmlParser()).thenReturn(iParser);

        Assert.assertNotNull(iParser);
    }

    // @Test
    public void givenAUriSetFhriContext() {
        PowerMockito.mockStatic(FHIRContextFactory.class);
        FHIRContextFactory singletonMock = Mockito.mock(FHIRContextFactory.class);
        PowerMockito.doNothing().when(singletonMock).setConfiguration(configuration);
    }

    @Test
    public void testGetInstance() {
        FHIRContextFactory factory01 = FHIRContextFactory.getInstance();
        Assert.assertNotNull(factory01);
        FHIRContextFactory factory02 = FHIRContextFactory.getInstance();
        Assert.assertNotNull(factory02);
        Assert.assertSame(factory01, factory02);
    }

    @Test
    public void testNewRestfulGenericClient() {
        Configuration config = PowerMockito.mock(Configuration.class);
        FhirServerConfiguration fhirConfig = PowerMockito.mock(FhirServerConfiguration.class);
        PowerMockito.when(config.getFhirServerConfiguration()).thenReturn(fhirConfig);
        PowerMockito.when(fhirConfig.getFhirConnectionTimeout()).thenReturn(30);
        PowerMockito.when(fhirConfig.getFhirConnectionRequestTimeout()).thenReturn(30);
        PowerMockito.when(fhirConfig.getFhirSocketTimeout()).thenReturn(30);
        PowerMockito.when(config.getFhirApiUri()).thenReturn("fhirApiUri");

        PowerMockito.mockStatic(Stu3ContextHelper.class);
        FhirContext fhirContext = PowerMockito.mock(FhirContext.class);
        IGenericClient mockClient = PowerMockito.mock(IGenericClient.class);
        IRestfulClientFactory mockClientFactory = PowerMockito.mock(IRestfulClientFactory.class);
        PowerMockito.doNothing().when(mockClientFactory).setConnectTimeout(30);
        PowerMockito.doNothing().when(mockClientFactory).setConnectionRequestTimeout(30);
        PowerMockito.doNothing().when(mockClientFactory).setSocketTimeout(30);
        PowerMockito.when(fhirContext.getRestfulClientFactory()).thenReturn(mockClientFactory);

        PowerMockito.when(fhirContext.newRestfulGenericClient(Mockito.anyString())).thenReturn(mockClient);
        IParser mockXmlParser = PowerMockito.mock(IParser.class);
        PowerMockito.when(fhirContext.newXmlParser()).thenReturn(mockXmlParser);


        PowerMockito.doNothing().when(mockClient).registerInterceptor(Mockito.anyObject());
        PowerMockito.when(Stu3ContextHelper.getStu3Context()).thenReturn(fhirContext);

        FHIRContextFactory fac = FHIRContextFactory.getInstance();
        fac.fhirContext = fhirContext;
        fac.setConfiguration(config);

        IClientInterceptor interceptor = PowerMockito.mock(IClientInterceptor.class);
        fac.registerAuthTokenInterceptor(interceptor);

        IGenericClient client = fac.newRestfulGenericClient();
        Assert.assertNotNull(client);

        Assert.assertSame(fac.getXmlParser(), mockXmlParser);

    }
}