package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.varian.fhir.resources.Group;
import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fmk9441 on 2017-02-09.
 */
@Slf4j
public class FHIRPractitionerInterface extends FHIRInterface<Practitioner>{

    /**
     * Return Fhir Practitioner by Id.<br>
     * @param loginId Login Id
     * @return Fhir Practitioner
     */
    public Practitioner queryPractitionerByLoginId(String loginId) {
        Practitioner practitioner = null;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            long time1 = System.currentTimeMillis();
            Bundle bundle = client.search()
                    .forResource(Practitioner.class)
                    .where(new StringClientParam(Practitioner.SP_LOGIN_ID).matchesExactly().value(loginId))
                    .returnBundle(Bundle.class)
                    .execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - PractitionerResource - QueryBy[sp_login_id] : {}", (time2 - time1) / 1000.0);
            if (bundle != null && bundle.hasEntry() && bundle.getEntryFirstRep().hasResource()) {
                practitioner = (Practitioner) bundle.getEntryFirstRep().getResource();
            }
        } catch (ResourceNotFoundException e) {
            log.error("ResourceNotFoundException: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        return practitioner;
    }

    /**
     * Return Practitioner List from Fhir Group Id.<br>
     * @param groupId Group Id
     * @return Practitioner List
     */
    public List<Reference> queryPractitionerListByGroupId(String groupId) {
        List<Reference> lstReference = new ArrayList<>();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();

        try {
            long time1 = System.currentTimeMillis();
            Bundle bundle = client.search()
                    .forResource(Group.class)
                    .include(Group.INCLUDE_MEMBER)
                    .where(new StringClientParam(Group.SP_RES_ID).matchesExactly().value(groupId))
                    .preferResponseTypes(Arrays.asList(Group.class, Practitioner.class))
                    .returnBundle(Bundle.class)
                    .execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - PractitionerResource - QueryBy[sp_res_id] : {}", (time2 - time1) / 1000.0);
            if (bundle != null && bundle.hasEntry() && bundle.getEntryFirstRep().hasResource()) {
                Group group = (Group) bundle.getEntryFirstRep().getResource();
                if (group.hasMember()) {
                    lstReference.addAll(group.getMember().stream().map(m -> m.getEntity()).collect(Collectors.toList()));
                }
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        return lstReference;
    }
}