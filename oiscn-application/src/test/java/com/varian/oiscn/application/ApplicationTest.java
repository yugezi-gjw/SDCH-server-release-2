package com.varian.oiscn.application;

import com.varian.oiscn.application.filters.CacheControlFilter;
import com.varian.oiscn.application.resources.ResourceRegistry;
import com.varian.oiscn.application.security.OspTrustManager;
import com.varian.oiscn.config.Configuration;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.AdminEnvironment;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Environment.class, SSLContext.class, HttpsURLConnection.class, OspTrustManager.class})
public class ApplicationTest {

	@Test
	public void testGetHost() {
		String[] arg0 = new String[0];
		String firstString = mock(String.class);
		String[] arg1 = {firstString};
		String[] arg3 = {firstString, mock(String.class), mock(String.class)};

		Assert.assertEquals("config/local.yaml", Application.getHost(null));
		Assert.assertEquals("config/local.yaml", Application.getHost(arg0));
		Assert.assertSame(firstString, Application.getHost(arg1));
		Assert.assertSame(firstString, Application.getHost(arg3));
	}

	@Test
	public void testGetName() {
		Application app = new Application();
		Assert.assertSame("varian-oiscn-server", app.getName());
	}

	@Test
	public void testRunConfigurationEnvironment() {
		Application app = PowerMockito.mock(Application.class);
		Configuration configuration = mock(Configuration.class);
		Environment environment = mock(Environment.class);
		ResourceRegistry resourceRegistry = mock(ResourceRegistry.class);
		try {
			PowerMockito.whenNew(ResourceRegistry.class).withArguments(configuration, environment)
					.thenReturn(resourceRegistry);
			PowerMockito.doNothing().when(resourceRegistry).initialize();
			PowerMockito.doNothing().when(app).addCorsHeader(environment);
			PowerMockito.doNothing().when(app).addNoCacheFilter(environment);
			PowerMockito.doNothing().when(app).disableSSL();
			PowerMockito.doCallRealMethod().when(app).run(configuration, environment);
		} catch (Exception e) {
			Assert.fail();
		}
		PowerMockito.verifyNew(ResourceRegistry.class);

	}

	@Test
	public void testAddNoCacheFilter() {
		Application app = new Application();
		// Configuration configuration = mock(Configuration.class);
		Environment environment = mock(Environment.class);
		FilterRegistration.Dynamic filter = mock(FilterRegistration.Dynamic.class);
		ServletEnvironment servlets = mock(ServletEnvironment.class);
		PowerMockito.when(environment.servlets()).thenReturn(servlets);
		PowerMockito.when(servlets.addFilter("no-cache", CacheControlFilter.class)).thenReturn(filter);
		try {
			app.addNoCacheFilter(environment);
			Mockito.verify(filter).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testAddCorsHeader() {
		Application app = PowerMockito.mock(Application.class);
		// Configuration configuration = mock(Configuration.class);
		Environment environment = mock(Environment.class);
		FilterRegistration.Dynamic filter = mock(FilterRegistration.Dynamic.class);
		FilterRegistration.Dynamic adminFilter = mock(FilterRegistration.Dynamic.class);
		ServletEnvironment servlets = mock(ServletEnvironment.class);
		AdminEnvironment admin = mock(AdminEnvironment.class);
		PowerMockito.when(environment.servlets()).thenReturn(servlets);
		PowerMockito.when(environment.admin()).thenReturn(admin);
		PowerMockito.when(servlets.addFilter("CORS", CrossOriginFilter.class)).thenReturn(filter);
		PowerMockito.when(admin.addFilter("CORS", CrossOriginFilter.class)).thenReturn(adminFilter);
		try {
			PowerMockito.doCallRealMethod().when(app).addCorsHeader(environment);
			app.addCorsHeader(environment);
			Mockito.verify(app).setFilterRegistrationProperties(filter);
			Mockito.verify(app).setFilterRegistrationProperties(adminFilter);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testSetFilterRegistrationProperties() {
		FilterRegistration.Dynamic filter = mock(FilterRegistration.Dynamic.class);
		Application app = new Application();
		app.setFilterRegistrationProperties(filter);

		Mockito.verify(filter).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		Mockito.verify(filter).setInitParameter("allowedOrigins", "*");
		Mockito.verify(filter).setInitParameter("allowedHeaders", "*");
		Mockito.verify(filter).setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS,HEAD");
		Mockito.verify(filter).setInitParameter("allowCredentials", "true");
	}

	@Test
	public void testDisableSSL() {
		Application app = new Application();
		Environment environment = mock(Environment.class);
		ServletEnvironment servlets = mock(ServletEnvironment.class);
		AdminEnvironment admin = mock(AdminEnvironment.class);
		PowerMockito.when(environment.servlets()).thenReturn(servlets);
		PowerMockito.when(environment.admin()).thenReturn(admin);
		OspTrustManager ospTm = mock(OspTrustManager.class);
		PowerMockito.mockStatic(SSLContext.class);

		try {
			PowerMockito.whenNew(OspTrustManager.class).withNoArguments().thenReturn(ospTm);

			SSLContext sc = mock(SSLContext.class);
			PowerMockito.when(SSLContext.getInstance("SSL")).thenReturn(sc);

			java.security.SecureRandom secureRandom = mock(java.security.SecureRandom.class);
			PowerMockito.whenNew(java.security.SecureRandom.class).withNoArguments().thenReturn(secureRandom);

			PowerMockito.mockStatic(HttpsURLConnection.class);
			PowerMockito.doNothing().when(HttpsURLConnection.class, "setDefaultHostnameVerifier", Matchers.any());
			PowerMockito.doNothing().when(HttpsURLConnection.class, "setDefaultSSLSocketFactory", Matchers.any());

			app.disableSSL();
			Mockito.verify(sc).init(Matchers.any(), Matchers.any(), Matchers.any());
		} catch (Exception e) {
			Assert.fail();
		}
	}
}