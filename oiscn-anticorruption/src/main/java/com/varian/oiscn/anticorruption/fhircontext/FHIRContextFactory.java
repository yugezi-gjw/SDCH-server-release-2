package com.varian.oiscn.anticorruption.fhircontext;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.varian.fhir.common.Stu3ContextHelper;
import com.varian.oiscn.config.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 12/30/2016.
 */
@Slf4j
public class FHIRContextFactory {

    protected static FHIRContextFactory instance;
    protected Configuration configuration;
    protected FhirContext fhirContext = Stu3ContextHelper.getStu3Context();

    protected IClientInterceptor tokenInterceptor;

    protected List<IClientInterceptor> interceptorList = new ArrayList<>();
    
    private FHIRContextFactory() {
    }

    /**
     * Return FHIRContext Instance.<br>
     *
     * @return FHIRContext Instance
     */
    public static FHIRContextFactory getInstance() {
        if (instance == null) {
            instance = new FHIRContextFactory();
        }
        return instance;
    }

    /**
     * Return XML Parser.<br>
     *
     * @return XML Parser
     */
    public IParser getXmlParser() {
        return fhirContext.newXmlParser();
    }

    /**
     * Set Configuration and Set FhirContext.<br>
     *
     * @param configuration Configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;

        fhirContext.getRestfulClientFactory().setConnectTimeout(configuration.getFhirServerConfiguration().getFhirConnectionTimeout());
        fhirContext.getRestfulClientFactory().setConnectionRequestTimeout(configuration.getFhirServerConfiguration().getFhirConnectionRequestTimeout());
        fhirContext.getRestfulClientFactory().setSocketTimeout(configuration.getFhirServerConfiguration().getFhirSocketTimeout());
    }

    public FHIRContextFactory addInterceptor(IClientInterceptor interceptor) {
        if (interceptor != null) interceptorList.add(interceptor);
        return this;
    }
    
    public void registerAuthTokenInterceptor(IClientInterceptor interceptor) {
        this.tokenInterceptor = interceptor;
    }

    /**
     * Return a New Restful Client.<br>
     *
     * @return Generic Client
     */
    public IGenericClient newRestfulGenericClient() {
        IGenericClient client;
        client = fhirContext.newRestfulGenericClient(configuration.getFhirApiUri());
        for (IClientInterceptor interceptor: interceptorList) {
            client.registerInterceptor(interceptor);
        }
        if (tokenInterceptor != null) {
            client.registerInterceptor(tokenInterceptor);
        }
        return client;
    }
}
