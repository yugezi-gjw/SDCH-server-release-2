package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.anticorruption.fhircontext.HttpClientContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by bhp9696 on 2017/11/15.
 */
@Slf4j
public class FHIRInterface<T extends DomainResource> {

    /**
     * Check FHIR metadata available.<br> 
     * @param baseUri FHIR base URI
     * @return true FHIR metadata exists
     */
    public static boolean isAvailabel(String metadataUri) {
        boolean isOK = false;
        try {
            URL metadataUrl = new URL(metadataUri);
            WebTarget target = HttpClientContextFactory.getInstance().getHttpClient().target(metadataUrl.toURI());
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.get();
            isOK = response.getStatusInfo().equals(Response.Status.OK);
        } catch (Exception e) {
            log.error("existMetadata Exception: {}", e.getMessage());
            isOK = false;
        }
        return isOK;
    }

    /**
     * Create Fhir DomainResource and return the Id.<br>
     *
     * @param domainResource Fhir DomainResource
     * @return the Created Fhir DomainResource Id
     */
    public String create(T domainResource) {
        String resourceId = StringUtils.EMPTY;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            long time1 = System.currentTimeMillis();
            MethodOutcome outcome = client.create().resource(domainResource).execute();
            resourceId = outcome.getId().getIdPart();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - {}Resource - Create : {}", domainResource,(time2 - time1) / 1000.0);
        } catch (Exception e) {
            log.error("create Exception: {}", e.getMessage());
            logFhirException(e);
        }
        return resourceId;
    }

    /**
     * Update Fhir DomainResource.<br>
     * @param domainResource Fhir DomainResource
     * @return id
     */
    public String update(T domainResource) {
        String resourceId = StringUtils.EMPTY;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            long time1 = System.currentTimeMillis();
            MethodOutcome outcome = client.update().resource(domainResource).execute();
            resourceId = outcome.getId().getIdPart();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - {}Resource - Update : {}",domainResource, (time2 - time1) / 1000.0);
        } catch (Exception e) {
            log.error("Update Exception: {}", e.getMessage());
            logFhirException(e);
        }
        return resourceId;
    }

    /**
     * Return Fhir Task by Id.<br>
     * @param resourceId Fhir resource Id
     * @return Fhir Task
     */
    public T queryById(String resourceId, Class<T> resourceType) {
        // no need to query
        if (StringUtils.isBlank(resourceId)) {
            log.warn("FHIR - queryById with NULL ID");
            return null;
        }
        
        T resource = null;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            long time1 = System.currentTimeMillis();
            resource = client.read().resource(resourceType).withId(resourceId).execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - {}Resource - QueryById : {}", (time2 - time1) / 1000.0);
        } catch (ResourceNotFoundException e) {
            log.error("queryById ResourceNotFoundException: {}", e.getMessage());
            logFhirException(e);
        } catch (Exception e) {
            log.error("queryById Exception: {}", e.getMessage());
            logFhirException(e);
        }
        return resource;
    }

    protected IQuery<IBaseBundle> buildIQuery(IQuery<IBaseBundle> iQuery, ICriterion<?> iCriterion, boolean first) {
        IQuery<IBaseBundle> tmpIQuery;
        if (first) {
            tmpIQuery = iQuery.where(iCriterion);
        } else {
            tmpIQuery = iQuery.and(iCriterion);
        }
        return tmpIQuery;
    }

    protected IQuery<IBaseBundle> buildIQuerySort(IQuery<IBaseBundle> iQuery, Object matchs, String params) {
        IQuery<IBaseBundle> tmpIQuery;
        if (matchs.equals(EnumMatchQuery.ASC)) {
            tmpIQuery = iQuery.sort().ascending(params);
        } else {
            tmpIQuery = iQuery.sort().descending(params);
        }
        return tmpIQuery;
    }

    protected List<T> getListFromBundle(Bundle bundle) {
        List<T> lstPatient = new ArrayList<>();
        if (bundle.hasEntry()) {
            bundle.getEntry().forEach(bundleEntryComponent -> {
                if (bundleEntryComponent.hasResource()) {
                    lstPatient.add((T) bundleEntryComponent.getResource());
                }
            });
        }
        return lstPatient;
    }

    protected List<T> getListFromBundleEntryComponent(List<Bundle.BundleEntryComponent> bundleEntryComponentList){
        List<T> resultList = new ArrayList<>();
        bundleEntryComponentList.forEach(bundleEntryComponent -> {
            if (bundleEntryComponent.hasResource()) {
                resultList.add((T) bundleEntryComponent.getResource());
            }
        });
        return resultList;
    }

    protected Bundle queryPagingBundle(IQuery<IBaseBundle> iQuery, List<Class<? extends IBaseResource>> theTypes, int countPerPage) {
        if( theTypes != null){
            iQuery = iQuery.preferResponseTypes(theTypes);
        }
        return iQuery.count(countPerPage)
                .returnBundle(Bundle.class)
                .execute();
    }
    
    protected void logFhirException(Exception e) {
        if (e instanceof BaseServerResponseException) {
            BaseServerResponseException fhirServerException = (BaseServerResponseException) e;
            String body = fhirServerException.getResponseBody();
            log.info("FhirException:{}", body);
            log.debug("FhirException cause:{}", fhirServerException.getCause());
        }
    }
}
