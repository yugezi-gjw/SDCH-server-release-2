package com.varian.oiscn.appointment.calling;

import com.varian.oiscn.core.appointment.calling.ServerConfiguration;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.junit.Assert.fail;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClients.class, HttpClientContext.class, CallingHttpClient.class})
public class CallingHttpClientTest {

    protected ServerConfiguration configuration;
    
    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(ServerConfiguration.class);
        String serviceUrl = PowerMockito.mock(String.class);

        PowerMockito.when(configuration.getCallingServiceUrl()).thenReturn(serviceUrl);
        PowerMockito.when(configuration.getMethod()).thenReturn("POST");
        PowerMockito.when(configuration.getConnectionTimeout()).thenReturn(3000);
        PowerMockito.when(configuration.getContentType()).thenReturn(javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED);
        PowerMockito.when(configuration.getCharset()).thenReturn("UTF-8");
        PowerMockito.when(configuration.getRetryInterval()).thenReturn(122);
        PowerMockito.when(configuration.getRetryTimes()).thenReturn(222);
        CallingHttpClient.initConfig(configuration);
    }

    @Test
    public void testCallingHttpClient() {
        try {
            PowerMockito.mockStatic(HttpClients.class);
            CloseableHttpClient mockClient = PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(mockClient);

            PowerMockito.mockStatic(HttpClientContext.class);
            HttpClientContext mockHttpClientContext = PowerMockito.mock(HttpClientContext.class);
            PowerMockito.when(HttpClientContext.create()).thenReturn(mockHttpClientContext);

            HttpPost mockRequest = PowerMockito.mock(HttpPost.class);
            PowerMockito.whenNew(HttpPost.class).withAnyArguments().thenReturn(mockRequest);

            PowerMockito.when(configuration.getMethod()).thenReturn("GET");
            CallingHttpClient.initConfig(configuration);
            new CallingHttpClient();
            PowerMockito.when(configuration.getMethod()).thenReturn("PUT");
            CallingHttpClient.initConfig(configuration);
            new CallingHttpClient();
            PowerMockito.when(configuration.getMethod()).thenReturn("PUT");
            CallingHttpClient.initConfig(configuration);
            new CallingHttpClient();
            PowerMockito.when(configuration.getMethod()).thenReturn("DELETE");
            CallingHttpClient.initConfig(configuration);
            new CallingHttpClient();
            PowerMockito.when(configuration.getMethod()).thenReturn("DEFAULT");
            CallingHttpClient.initConfig(configuration);
            new CallingHttpClient();
            PowerMockito.when(configuration.getMethod()).thenReturn("POST");
            CallingHttpClient.initConfig(configuration);
            CallingHttpClient callingHttpClient = new CallingHttpClient();
            Assert.assertNotNull(callingHttpClient.client);
            Assert.assertNotNull(callingHttpClient.context);
            Assert.assertNotNull(callingHttpClient.request);
            Mockito.verify(mockRequest).addHeader(Mockito.anyString(), Mockito.anyString());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSendMsg() {
        try {
            Map<String, Object> msg = PowerMockito.mock(Map.class);

            PowerMockito.mockStatic(HttpClients.class);
            CloseableHttpClient mockClient = PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(mockClient);

            PowerMockito.mockStatic(HttpClientContext.class);
            HttpClientContext mockHttpClientContext = PowerMockito.mock(HttpClientContext.class);
            PowerMockito.when(HttpClientContext.create()).thenReturn(mockHttpClientContext);

            HttpPost mockRequest = PowerMockito.mock(HttpPost.class);
            PowerMockito.whenNew(HttpPost.class).withAnyArguments().thenReturn(mockRequest);


            CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
            PowerMockito.when(mockClient.execute(mockRequest, mockHttpClientContext)).thenReturn(response);
            Header header = PowerMockito.mock(Header.class);
            String contentType = "text/html;charset=UTF-8";
            PowerMockito.when(header.getValue()).thenReturn(contentType);
            PowerMockito.when(response.getFirstHeader("Content-Type")).thenReturn(header);

            StatusLine statusLine = PowerMockito.mock(StatusLine.class);
            PowerMockito.when(statusLine.getStatusCode()).thenReturn(200);
            PowerMockito.when(response.getStatusLine()).thenReturn(statusLine);
            CallingHttpClient callingHttpClient = new CallingHttpClient();
            callingHttpClient.sendMsg(msg);
            callingHttpClient.sendMsg("MSG");

            Assert.assertTrue(true);

            HttpGet mockGetRequest = PowerMockito.mock(HttpGet.class);
            PowerMockito.whenNew(HttpGet.class).withAnyArguments().thenReturn(mockGetRequest);
            PowerMockito.when(mockClient.execute(mockGetRequest, mockHttpClientContext)).thenReturn(response);
            callingHttpClient.sendMsg(msg);
            callingHttpClient.sendMsg("MSG");

            Assert.assertTrue(true);


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testClose() {
        PowerMockito.mockStatic(HttpClients.class);
        CloseableHttpClient mockClient = PowerMockito.mock(CloseableHttpClient.class);
        PowerMockito.when(HttpClients.createDefault()).thenReturn(mockClient);

        PowerMockito.mockStatic(HttpClientContext.class);
        HttpClientContext mockHttpClientContext = PowerMockito.mock(HttpClientContext.class);
        PowerMockito.when(HttpClientContext.create()).thenReturn(mockHttpClientContext);

        try {
            CallingHttpClient callingHttpClient = new CallingHttpClient();
            callingHttpClient.close();
            Mockito.verify(mockClient).close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
