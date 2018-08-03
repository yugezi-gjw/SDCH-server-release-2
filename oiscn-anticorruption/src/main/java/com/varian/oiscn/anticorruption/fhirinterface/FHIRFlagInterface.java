package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Flag;
import com.varian.oiscn.anticorruption.converter.EnumFlagQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-06-23.
 */
@Slf4j
public class FHIRFlagInterface extends FHIRInterface<Flag>{


    /**
     * Delete Fhir Flag.<br>
     * @param flag Fhir Flag
     * @return Execute Result
     */
    public Boolean deleteFlag(Flag flag) {
        Boolean ret = false;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            long time1 = System.currentTimeMillis();
            IBaseOperationOutcome outcome = client.delete().resource(flag).execute();
            ret = !outcome.isEmpty();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - FlagResource - Delete : {}", (time2 - time1) / 1000.0);
        } catch (Exception e) {
            log.error("deleteFlag Exception: {}", e.getMessage());
        }
        return ret;
    }

    /**
     * Return Flag List by Flag Query Immutable PairMap.<br>
     * @param flagQueryImmutablePairMap Flag Query Immutable PairMap
     * @return Flag List
     */
    public List<Flag> queryFlagList(Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap) {
        List<Flag> lstFlag = new ArrayList<>();
        if (null != flagQueryImmutablePairMap && !flagQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            long time1 = System.currentTimeMillis();
            IQuery<IBaseBundle> iQuery = buildFlagQuery(client, flagQueryImmutablePairMap);
            Bundle bundle = iQuery.returnBundle(Bundle.class).execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - FlagResource - QueryFlagList : {}", (time2 - time1) / 1000.0);
            lstFlag = getListFromBundle(bundle);
        }
        return lstFlag;
    }

    /**
     * Return Pagination Fhir Flag List from Flag Query Immutable PairMap.<br>
     * @param flagQueryImmutablePairMap Flag Query Immutable PairMap
     * @param countPerPage Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo Page Number To
     * @return Pagination Fhir Flag List
     */
    public Pagination<Flag> queryPagingFlagList(Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap, int countPerPage, int pageNumberFrom,int pageNumberTo) {
        Pagination<Flag> flagPagination = new Pagination<>();
        if (null != flagQueryImmutablePairMap && !flagQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            long time1 = System.currentTimeMillis();
            IQuery<IBaseBundle> iQuery = buildFlagQuery(client, flagQueryImmutablePairMap);
            Bundle bundle = queryPagingBundle(iQuery,null,countPerPage);
            List<Bundle.BundleEntryComponent> bundleEntryComponentList = PaginationHelper.queryPagingBundle(client, bundle, pageNumberFrom, pageNumberTo, Arrays.asList(Flag.class));
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - FlagResource - QueryFlagListByPage : {}", (time2 - time1) / 1000.0);
            List<Flag> lstFlag = getListFromBundleEntryComponent(bundleEntryComponentList);
            flagPagination.setLstObject(lstFlag);
            flagPagination.setTotalCount(bundle.getTotal());
        }
        return flagPagination;
    }

    @SuppressWarnings("unchecked")
    private IQuery<IBaseBundle> buildFlagQuery(IGenericClient client, Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap) {
        boolean first = true;
        IQuery<IBaseBundle> iQuery = client.search().forResource(Flag.class);
        for (EnumFlagQuery enumFlagQuery : flagQueryImmutablePairMap.keySet()) {
            Object params = flagQueryImmutablePairMap.get(enumFlagQuery).getRight();
            switch (enumFlagQuery) {
                case PATIENT_ID:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Flag.SP_PATIENT).matchesExactly().values((List<String>) params), first);
                    first = false;
                    break;
                case FLAG_CODE:
                    iQuery = iQuery.and(new StringClientParam(Flag.SP_CODE).matchesExactly().value(params.toString()));
                    break;
                default:
                    break;
            }
        }
        return iQuery;
    }


}