package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fmk9441 on 2017-08-24.
 */
@Slf4j
public class PaginationHelper {
    private PaginationHelper() {

    }

    /**
     * Return Pagination Bundle.<br>
     *
     * @param client     IGenericClient
     * @param bundle     Bundle
     * @param pageNumberFrom Page Number starting from
     * @param pageNumberTo Page Number ending with
     * @param classList  IBaseResource Class List
     * @return Bundle
     */
    public static List<Bundle.BundleEntryComponent> queryPagingBundle(IGenericClient client, Bundle bundle, int pageNumberFrom, int pageNumberTo, List<Class<? extends IBaseResource>> classList) {
        log.debug("queryPagingBundle page range [{}] - [{}] ", pageNumberFrom, pageNumberTo);
        long t1 = System.currentTimeMillis();
        int totalCount = bundle.getTotal();
        List<Bundle.BundleEntryComponent> resultList = new ArrayList<>();
        if(pageNumberFrom == 1){
            resultList.addAll(bundle.getEntry());
        }
        for (int page = 2; page <= pageNumberTo; page++) {
            if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                if (classList != null && !classList.isEmpty()) {
                    long time1 = System.currentTimeMillis();
                    bundle = client.loadPage().next(bundle).preferResponseTypes(classList).execute();
                    long time2 = System.currentTimeMillis();
                    log.debug("queryPagingBundle [{}] - spent {} ms ", page, (time2 - time1));
                } else {
                    bundle = client.loadPage().next(bundle).execute();
                }
            } else {
                bundle = new Bundle();
                bundle.setTotal(totalCount);
                break;
            }
            if(page >= pageNumberFrom ){
                long time1 = System.currentTimeMillis();
                resultList.addAll(bundle.getEntry());
                long time2 = System.currentTimeMillis();
                log.debug("queryPagingBundle addAll - spent {} ms ", (time2 - time1));
            }
        }
        long t2 = System.currentTimeMillis();
        log.debug("queryPagingBundle - Total spent {} ms ", (t2 - t1));
        return resultList;
    }
}
