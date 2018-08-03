package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Group;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by fmk9441 on 2017-02-07.
 */
@Slf4j
public class FHIRGroupInterface extends FHIRInterface<Group>{

    private static int COUNT_PER_PAGE = Integer.MAX_VALUE;

    /**
     * Return Fhir Group List By Name (Default 'Oncologist[_]').<br>
     *
     * @return Fhir Group List
     */
    public List<Group> queryGroupByName() {
        List<Group> lstGroup = new ArrayList<>();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            long time1 = System.currentTimeMillis();
            Bundle bundle = client.search()
                    .forResource(Group.class)
                    .where(new StringClientParam(Group.SP_NAME).matches().value("Oncologist[_]"))
                    .and(new StringClientParam(Group.SP_ACTIVE).matchesExactly().value("Active"))
                    .returnBundle(Bundle.class)
                    .execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - GroupResource - QueryBy[sp_name,sp_active] : {}", (time2 - time1) / 1000.0);
            if (bundle != null) {
                lstGroup = getListFromBundle(bundle);
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return lstGroup;
    }

    /**
     * Return Fhir Group List By Resource Id.<br>
     * @param resourceID Resource Id
     * @return Fhir Group List
     */
    public List<Group> queryGroupListByResourceID(String resourceID) {
        List<Group> lstGroup = new ArrayList<>();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();

        try {
            long time1 = System.currentTimeMillis();
            Bundle bundle = client.search()
                    .forResource(Group.class)
                    .where(new StringClientParam(Group.SP_MEMBER).matchesExactly().value(resourceID))
                    .returnBundle(Bundle.class)
                    .execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - GroupResource - QueryBy[sp_member] : {}", (time2 - time1) / 1000.0);
            if (bundle != null) {
                lstGroup = getListFromBundle(bundle);
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        return lstGroup;
    }

    /**
     * Return Fhir Group Map with Member Reference By fuzzyName and Department Id.<br>
     * @param fuzzyName Fuzzy Name
     * @param departmentId Department Id
     * @return Fhir Group Map
     */
    public Map<Group, List<Reference>> queryGroupWithMemberRefListMap(String fuzzyName, String departmentId) {
        Map<Group, List<Reference>> hmGroupWithMemberRefList = new HashMap<>();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();

        try {
            long time1 = System.currentTimeMillis();
            Bundle bundle = client.search()
                    .forResource(Group.class)
                    .include(Group.INCLUDE_MEMBER)
                    .where(new StringClientParam(Group.SP_NAME).matches().value(fuzzyName))
                    .and(new StringClientParam(Group.SP_DEPARTMENT).matchesExactly().value(departmentId))
                    .and(new StringClientParam(Group.SP_ACTIVE).matchesExactly().value("Active"))
                    .count(COUNT_PER_PAGE)
                    .returnBundle(Bundle.class)
                    .execute();

            List<Bundle.BundleEntryComponent> bundleEntryComponentList = PaginationHelper.queryPagingBundle(client, bundle, 1, Integer.MAX_VALUE, Arrays.asList(Group.class));
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - GroupResource - QueryBy[sp_name,sp_department,sp_active] : {}", (time2 - time1) / 1000.0);
            bundleEntryComponentList.forEach(bundleEntryComponent -> {
                Group group = (Group) bundleEntryComponent.getResource();
                hmGroupWithMemberRefList.put(group, group.hasMember() ? group.getMember().stream().map(m -> m.getEntity()).collect(Collectors.toList()) : null);
            });
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        return hmGroupWithMemberRefList;
    }

}