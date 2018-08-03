package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DomainResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 2/6/2018.
 */
@Slf4j
public class FHIRInterfaceV2<T extends DomainResource> {
    /**
     * Create Fhir DomainResource and return the Id.<br>
     *
     * @param domainResource Fhir DomainResource
     * @return the Created Fhir DomainResource Id
     */
    public String create(T domainResource) {
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        long time1 = System.currentTimeMillis();
        MethodOutcome outcome = client.create().resource(domainResource).execute();
        String resourceId = outcome.getId().getIdPart();
        long time2 = System.currentTimeMillis();
        log.debug("FHIR - {}Resource - Create : {}", domainResource, (time2 - time1) / 1000.0);
        return resourceId;
    }

    /**
     * Update Fhir DomainResource.<br>
     *
     * @param domainResource Fhir DomainResource
     * @return id
     */
    public String update(T domainResource) {
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        long time1 = System.currentTimeMillis();
        MethodOutcome outcome = client.update().resource(domainResource).execute();
        String resourceId = outcome.getId().getIdPart();
        long time2 = System.currentTimeMillis();
        log.debug("FHIR - {}Resource - Update : {}", domainResource, (time2 - time1) / 1000.0);
        return resourceId;
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
}
