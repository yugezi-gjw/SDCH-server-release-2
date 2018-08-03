package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.varian.fhir.resources.TreatmentSummary;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FHIRTreatmentSummaryInterface extends FHIRInterface<TreatmentSummary> {

    /**
     * Return Fhir TreatmentSummary by Patient Id.<br>
     *
     * @param patientId Patient Id
     * @return Fhir TreatmentSummary
     */
    public TreatmentSummary getTreatmentSummary(String patientId) {
        TreatmentSummary treatmentSummary = null;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try{
            long time1 = System.currentTimeMillis();
            treatmentSummary = client.read().resource(TreatmentSummary.class).withId(patientId).execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - TreatmentSummaryResource - QueryBy[patientId] : {}", (time2 - time1) / 1000.0);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return treatmentSummary;
    }
}
