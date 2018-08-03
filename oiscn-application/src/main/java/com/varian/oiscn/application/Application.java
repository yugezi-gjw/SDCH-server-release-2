package com.varian.oiscn.application;

import com.varian.oiscn.application.filters.CacheControlFilter;
import com.varian.oiscn.application.filters.HttpRequestLogFilter;
import com.varian.oiscn.application.resources.ResourceRegistry;
import com.varian.oiscn.application.security.OspTrustManager;
import com.varian.oiscn.config.Configuration;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Application.<br>
 */
@Slf4j
public class Application extends io.dropwizard.Application<Configuration> {

    /**
     * Application Entrance.<br>
     *
     * @param args Argument List
     */
    public static void main(String[] args) {
        try {
            new Application().run("server", getHost(args));
        } catch (Exception e) {
            log.error("Application Exception: {}", e.getMessage());
        }
    }

    protected static String getHost(String[] args) {
        if (args == null || args.length == 0) {
            return "config/local.yaml";
        } else {
            return args[0];
        }
    }

    /* (non-Javadoc)
     * @see io.dropwizard.Application#run(io.dropwizard.Configuration, io.dropwizard.setup.Environment)
     */
    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        log.info("Server: [{}] is starting ...", getName());
        try {
            new ResourceRegistry(configuration, environment).initialize();
            addNoCacheFilter(environment);
            addCorsHeader(environment);
            disableSSL();
            addHttpRequestLogFilter(environment);
        } catch (Exception e) {
            log.error("Server Starting with Error: {}", e.getMessage());
            throw e;
        }
        log.info("Server: [{}] is started !", getName());
    }

    private void addHttpRequestLogFilter(Environment environment) {
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("http request log", HttpRequestLogFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

    protected void addNoCacheFilter(Environment environment) {
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("no-cache", CacheControlFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

    protected void addCorsHeader(Environment environment) {
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        setFilterRegistrationProperties(filter);

        FilterRegistration.Dynamic adminFilter = environment.admin().addFilter("CORS", CrossOriginFilter.class);
        setFilterRegistrationProperties(adminFilter);
    }

    protected void setFilterRegistrationProperties(FilterRegistration.Dynamic filter) {
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter("allowedOrigins", "*");
        filter.setInitParameter("allowedHeaders", "*");
        filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS,HEAD");
        filter.setInitParameter("allowCredentials", "true");
    }

    /* (non-Javadoc)
     * @see io.dropwizard.Application#getName()
     */
    @Override
    public String getName() {
        return "varian-oiscn-server";
    }

    /**
     * Because osp authentication should call osp https webservice.
     * So it needs to disable ssl certification.
     */
    protected void disableSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new OspTrustManager()};

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            log.error("disableSSL Exception: {}", e.getMessage());
        }
    }
}
