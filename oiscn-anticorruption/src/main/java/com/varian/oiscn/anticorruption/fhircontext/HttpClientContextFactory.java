package com.varian.oiscn.anticorruption.fhircontext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varian.oiscn.config.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;

/**
 * Created by gbt1220 on 1/3/2017.
 */
public class HttpClientContextFactory {
    private Client client;
    private Environment environment;
    private Configuration configuration;
    private ObjectMapper objectMapper;

    private static HttpClientContextFactory instance;

    private HttpClientContextFactory(){
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Return a HttpClientContext Instance.<br>
     *
     * @return HttpClientContext
     */
    public static HttpClientContextFactory getInstance() {
        if (instance == null) {
            instance = new HttpClientContextFactory();
        }
        return instance;
    }

    /**
     * Return a Http Client.<br>
     * @return a Http Client
     */
    public Client getHttpClient() {
        if (client == null) {
            JerseyClientBuilder builder = new JerseyClientBuilder(environment);
            builder.using(objectMapper);
            if (configuration != null && configuration.httpClientConfiguration != null) {
                builder.using(configuration.httpClientConfiguration);
            }
            client = builder.build("oiscnHttpClient");
        }
        return client;
    }

    /**
     * Set Environment and Configuration.<br>
     * @param environment Environment
     * @param configuration Configuration
     */
    public void setEnvironmentAndConfiguration(Environment environment, Configuration configuration) {
        this.environment = environment;
        this.configuration = configuration;
    }
}