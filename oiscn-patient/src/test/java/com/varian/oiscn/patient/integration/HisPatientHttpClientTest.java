package com.varian.oiscn.patient.integration;

import com.varian.oiscn.base.integration.config.HisPatientInfo;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfiguration;
import com.varian.oiscn.patient.integration.exception.HisServiceException;
import com.varian.oiscn.patient.integration.service.HisPatientHttpClient;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.fail;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/25/2017
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClients.class, HttpClientContext.class, HisPatientHttpClient.class,
        HisPatientInfoConfigService.class})
public class HisPatientHttpClientTest {

    protected HisPatientInfoConfiguration configuration;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(HisPatientInfoConfiguration.class);
        String serviceUrl = PowerMockito.mock(String.class);
        HisPatientInfo info = PowerMockito.mock(HisPatientInfo.class);

        PowerMockito.when(configuration.getPatientInfoServer()).thenReturn(info);
        PowerMockito.when(info.getHisPatientInfoServiceUrl()).thenReturn(serviceUrl);
        PowerMockito.when(info.getMethod()).thenReturn("GET");
        PowerMockito.when(info.getConnectionTimeout()).thenReturn(3000);
        PowerMockito.mockStatic(HisPatientInfoConfigService.class);
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(configuration);
    }

    @Test
    public void testHisPatientHttpClientThrowException() {
        PowerMockito.mockStatic(HttpClients.class);
        PowerMockito.when(HttpClients.createDefault()).thenThrow(new RuntimeException("test"));

        String params = "testParam";
        HisPatientHttpClient httpClient = null;
        try {
            httpClient = new HisPatientHttpClient(params);
        } catch (HisServiceException e) {
            Assert.assertNull(httpClient);
        }
    }

    @Test
    public void testHisPatientHttpClient() {
        try {
            PowerMockito.mockStatic(HttpClients.class);
            CloseableHttpClient mockClient = PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(mockClient);

            PowerMockito.mockStatic(HttpClientContext.class);
            HttpClientContext mockHttpClientContext = PowerMockito.mock(HttpClientContext.class);
            PowerMockito.when(HttpClientContext.create()).thenReturn(mockHttpClientContext);

            HttpGet mockRequest = PowerMockito.mock(HttpGet.class);
            PowerMockito.whenNew(HttpGet.class).withAnyArguments().thenReturn(mockRequest);

            String params = "testParam";
            HisPatientHttpClient hisPatientHttpClient = new HisPatientHttpClient(params);

            Assert.assertNotNull(hisPatientHttpClient.client);
            Assert.assertNotNull(hisPatientHttpClient.context);
            Assert.assertNotNull(hisPatientHttpClient.request);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSendMessage() {
        try {
            PowerMockito.mockStatic(HttpClients.class);
            CloseableHttpClient mockClient = PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(mockClient);

            PowerMockito.mockStatic(HttpClientContext.class);
            HttpClientContext mockHttpClientContext = PowerMockito.mock(HttpClientContext.class);
            PowerMockito.when(HttpClientContext.create()).thenReturn(mockHttpClientContext);

            HttpGet mockRequest = PowerMockito.mock(HttpGet.class);
            PowerMockito.whenNew(HttpGet.class).withAnyArguments().thenReturn(mockRequest);

            CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
            PowerMockito.when(mockClient.execute(mockRequest, mockHttpClientContext)).thenReturn(response);

            Header header = PowerMockito.mock(Header.class);
            String contentType = "application/json";
            PowerMockito.when(header.getValue()).thenReturn(contentType);
            PowerMockito.when(response.getFirstHeader("Content-Type")).thenReturn(header);


            StatusLine statusLine = PowerMockito.mock(StatusLine.class);
            PowerMockito.when(statusLine.getStatusCode()).thenReturn(200);
            PowerMockito.when(response.getStatusLine()).thenReturn(statusLine);

            HttpEntity entity = PowerMockito.mock(HttpEntity.class);
            PowerMockito.when(response.getEntity()).thenReturn(entity);
            InputStream inputStream = PowerMockito.mock(InputStream.class);
            PowerMockito.when(entity.getContent()).thenReturn(inputStream);
            InputStreamReader reader = PowerMockito.mock(InputStreamReader.class);
            PowerMockito.whenNew(InputStreamReader.class).withAnyArguments().thenReturn(reader);
            PowerMockito.when(reader.read(Matchers.any(char[].class))).thenReturn(10).thenReturn(-1);
            String params = PowerMockito.mock(String.class);
            HisPatientHttpClient hisPatientHttpClient = new HisPatientHttpClient(params);
            String result = hisPatientHttpClient.sendMessage();
            Assert.assertNotNull(result);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testClose() {
        try {
            PowerMockito.mockStatic(HttpClients.class);
            CloseableHttpClient mockClient = PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(mockClient);

            PowerMockito.mockStatic(HttpClientContext.class);
            HttpClientContext mockHttpClientContext = PowerMockito.mock(HttpClientContext.class);
            PowerMockito.when(HttpClientContext.create()).thenReturn(mockHttpClientContext);

            HttpGet mockRequest = PowerMockito.mock(HttpGet.class);
            PowerMockito.whenNew(HttpGet.class).withAnyArguments().thenReturn(mockRequest);

            String params = PowerMockito.mock(String.class);

            HisPatientHttpClient hisPatientHttpClient = new HisPatientHttpClient(params);
            hisPatientHttpClient.close();
            Mockito.verify(mockClient).close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
