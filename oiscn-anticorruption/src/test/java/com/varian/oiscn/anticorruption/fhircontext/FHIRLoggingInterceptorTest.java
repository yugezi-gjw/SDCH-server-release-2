package com.varian.oiscn.anticorruption.fhircontext;

import ca.uhn.fhir.rest.client.apache.ApacheHttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletException;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by gbt1220 on 11/1/2017.
 */
public class FHIRLoggingInterceptorTest {
    private ApacheHttpRequest request;

    private FHIRLoggingInterceptor interceptor;

    @Before
    public void setup() {
        request = PowerMockito.mock(ApacheHttpRequest.class);
    }

    @Test
    public void interceptRequestTest() throws IOException, ServletException {
    	interceptor = new FHIRLoggingInterceptor();
    	HttpRequestBase hrBase = PowerMockito.mock(HttpRequestBase.class);
    	URI uri = null;
		try {
			uri = new URI("http://localhost/fhir/prefix?patient:exact=123456&other=abcd");
		} catch (URISyntaxException e) {
			Assert.fail();
		}
    	PowerMockito.when(hrBase.getURI()).thenReturn(uri);
		PowerMockito.when(request.getApacheRequest()).thenReturn(hrBase);
        PowerMockito.when(request.getHttpVerbName()).thenReturn(HttpMethod.POST);
        interceptor.interceptRequest(request);
    }

    @Test
    public void handleGetRequestTest() {
    	interceptor = new FHIRLoggingInterceptor();
    	HttpRequestBase hrBase = PowerMockito.mock(HttpRequestBase.class);
    	URI uri = null;
		try {
			uri = new URI("http://localhost/fhir/Immunization?patient:exact=123456&other=abcd");
		} catch (URISyntaxException e) {
			Assert.fail();
		}
		PowerMockito.when(hrBase.getURI()).thenReturn(uri);
		PowerMockito.when(request.getApacheRequest()).thenReturn(hrBase);
		PowerMockito.when(request.getHttpVerbName()).thenReturn(HttpMethod.GET);
    	Assert.assertNotNull(interceptor.handleGetRequest(request));

		try {
			uri = new URI("http://localhost/fhir/Task?patient:exact=123456&other=abcd");
		} catch (URISyntaxException e) {
			Assert.fail();
		}
		PowerMockito.when(hrBase.getURI()).thenReturn(uri);
		Assert.assertNotNull(interceptor.handleGetRequest(request));

		try {
			uri = new URI("http://localhost/fhir/Appointment?patient:exact=123456&other=abcd");
		} catch (URISyntaxException e) {
			Assert.fail();
		}
		PowerMockito.when(hrBase.getURI()).thenReturn(uri);
		Assert.assertNotNull(interceptor.handleGetRequest(request));

		try {
			uri = new URI("http://localhost/fhir/Diagnosis?patient:exact=123456&other=abcd");
		} catch (URISyntaxException e) {
			Assert.fail();
		}
		PowerMockito.when(hrBase.getURI()).thenReturn(uri);
		Assert.assertNotNull(interceptor.handleGetRequest(request));


		try {
			uri = new URI("http://localhost/fhir/TreatmentSummary?patient:exact=123456&other=abcd");
		} catch (URISyntaxException e) {
			Assert.fail();
		}
		PowerMockito.when(hrBase.getURI()).thenReturn(uri);
		Assert.assertNotNull(interceptor.handleGetRequest(request));
    }

    @Test
    public void getPathPrefixTest() throws ServletException {
    	interceptor = new FHIRLoggingInterceptor();
    	HttpRequestBase hrBase = PowerMockito.mock(HttpRequestBase.class);
    	URI uri = null;
		try {
			uri = new URI("http://localhost/fhir/prefix?patient:exact=123456&other=abcd");
		} catch (URISyntaxException e) {
			Assert.fail();
		}
		PowerMockito.when(hrBase.getURI()).thenReturn(uri);
		PowerMockito.when(request.getApacheRequest()).thenReturn(hrBase);
    	
    	Assert.assertEquals("prefix", interceptor.getPathPrefix(request));
    }
}
