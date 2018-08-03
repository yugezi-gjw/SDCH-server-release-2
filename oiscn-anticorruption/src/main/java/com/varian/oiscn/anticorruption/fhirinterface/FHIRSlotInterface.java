package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.DateClientParam;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Slot;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-02-17.
 */
@Slf4j
public class FHIRSlotInterface extends FHIRInterface<Slot>{

    /**
     * Return Fhir Slot List from Device Id, and Date.<br>
     *
     * @param deviceId Fhir Device Id
     * @param date     Date
     * @return Fhir Slot List
     */
    public List<Slot> querySlotListByDeviceIdAndDate(String deviceId, Date date){
        List<Slot> lstSlot = new ArrayList<>();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();

        try {
            Bundle bundle = client.search()
                    .forResource(Slot.class)
                    .where(new StringClientParam(Slot.SP_ACTOR).matchesExactly().value(deviceId))
                    .and(new StringClientParam(Slot.SP_DEPARTMENT).matchesExactly().value("1"))
                    .and(new DateClientParam(Slot.SP_DATE_RANGE).exactly().day(date))
                    .returnBundle(Bundle.class)
                    .execute();

            if (bundle != null && bundle.hasEntry()) {
                bundle.getEntry().forEach(bundleEntryComponent -> {
                    if (bundleEntryComponent.hasResource()) {
                        lstSlot.add((Slot) bundleEntryComponent.getResource());
                    }
                });
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        return lstSlot;
    }
}