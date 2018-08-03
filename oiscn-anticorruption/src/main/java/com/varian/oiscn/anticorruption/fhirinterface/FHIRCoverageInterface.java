package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.varian.fhir.resources.Coverage;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 11/1/2017
 * @Modified By:
 */
@Slf4j
public class FHIRCoverageInterface extends FHIRInterface<Coverage>{

    @Override
    public String create(Coverage domainResource) {
        log.debug("Only use father's method");
        return super.create(domainResource);
    }

    public Coverage queryByPatientId(String patientId) {
        Coverage coverage = null;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        Bundle bundle = client.search().forResource(Coverage.class)
                .where(new StringClientParam(Coverage.SP_POLICY_HOLDER).matchesExactly().value(patientId))
                .returnBundle(Bundle.class).execute();
        if (bundle != null && bundle.hasEntry() && bundle.getEntryFirstRep().hasResource()) {
            coverage = (Coverage) bundle.getEntryFirstRep().getResource();
        }
        return coverage;
    }


    public Pagination<Coverage> queryCoveragePaginationByPatientSerList(List<String> patientSerList,int countPerPage, int pageNumberTo){
        Pagination<Coverage> pagination = new Pagination<>();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            long time1 = System.currentTimeMillis();
            IQuery<IBaseBundle> iQuery = client.search().forResource(Coverage.class)
                    .where(new StringClientParam(Coverage.SP_POLICY_HOLDER_LIST).matchesExactly().values(patientSerList));
            Bundle bundle = queryCoveragePagingBundle(iQuery,countPerPage);
            long time2 = 0L;
            if (bundle != null) {
                List<Bundle.BundleEntryComponent> resultList = PaginationHelper.queryPagingBundle(client, bundle, 1, pageNumberTo, Arrays.asList(Coverage.class));
                time2 = System.currentTimeMillis();
                List<Coverage> coverageList = getListFromBundleEntryComponent(resultList);
                pagination.setLstObject(coverageList);
                pagination.setTotalCount(bundle.getTotal());
            }
            log.debug("FHIR - {}Resource - QueryByPatientSerList : {}", (time2 - time1) / 1000.0);
        } catch (ResourceNotFoundException e) {
            log.error("queryCoveragePaginationByPatientSerList ResourceNotFoundException: {}", e.getMessage());
            logFhirException(e);
        } catch (Exception e) {
            log.error("queryCoveragePaginationByPatientSerList Exception: {}", e.getMessage());
            logFhirException(e);
        }
        return pagination;
    }

    private Bundle queryCoveragePagingBundle(IQuery<IBaseBundle> iQuery, int countPerPage) {
        return iQuery.preferResponseTypes(Arrays.asList(Coverage.class))
                .count(countPerPage)
                .returnBundle(Bundle.class)
                .execute();
    }
}
