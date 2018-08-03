package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.DateClientParam;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumTaskQuery;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-02-13.
 */
@Slf4j
public class FHIRTaskInterface extends FHIRInterface<Task>{

    /**
     * Return Fhir Task List by Task Immutable PairMap.<br>
     * @param taskQueryImmutablePairMap Task Immutable PairMap
     * @return Fhir Task List
     */
    public List<Task> queryTaskList(Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap) {
        List<Task> lstTask = new ArrayList<>();
        if (null != taskQueryImmutablePairMap && !taskQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            IQuery<IBaseBundle> iQuery = buildTaskQuery(client, taskQueryImmutablePairMap);
            long time1 = System.currentTimeMillis();
            Bundle bundle = queryTaskBundle(iQuery);
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - TaskResource - QueryTaskList : {}", (time2 - time1) / 1000.0);
            if (bundle != null) {
                lstTask = getListFromBundle(bundle);
            }
        }
        return lstTask;
    }

    /**
     * Return Fhir Pagination Task List by Task Query Immutable PairMap.<br>
     * @param taskQueryImmutablePairMap Task Query Immutable PairMap
     * @param countPerPage Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo Page Number To
     * @return Fhir Pagination Task List
     */
    public Pagination<Task> queryPagingTaskList(Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap, int countPerPage, int pageNumberFrom,int pageNumberTo) {
        Pagination<Task> taskPagination = new Pagination<>();
        if (null != taskQueryImmutablePairMap && !taskQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            IQuery<IBaseBundle> iQuery = buildTaskQuery(client, taskQueryImmutablePairMap);
            long time1 = System.currentTimeMillis();
            Bundle bundle = queryPagingBundle(iQuery, null,countPerPage);

            if (bundle != null) {
                List<Bundle.BundleEntryComponent> bundleEntryComponentList = PaginationHelper.queryPagingBundle(client, bundle, pageNumberFrom, pageNumberTo, Arrays.asList(Task.class));
                long time2 = System.currentTimeMillis();
                log.debug("FHIR - TaskResource - QueryTaskListByPage : {}", (time2 - time1) / 1000.0);
                List<Task> lstTask = getListFromBundleEntryComponent(bundleEntryComponentList);
                taskPagination.setLstObject(lstTask);
                taskPagination.setTotalCount(bundle.getTotal());
            }
        }
        return taskPagination;
    }

    private Bundle queryTaskBundle(IQuery<IBaseBundle> iQuery) {
        return iQuery.returnBundle(Bundle.class).execute();
    }

    @SuppressWarnings("unchecked")
    private IQuery<IBaseBundle> buildTaskQuery(IGenericClient client, Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap) {
        boolean first = true;
        IQuery<IBaseBundle> iQuery = client.search().forResource(Task.class);
        for (EnumTaskQuery enumTaskQuery : taskQueryImmutablePairMap.keySet()) {
            Object matchs = taskQueryImmutablePairMap.get(enumTaskQuery).getLeft();
            Object params = taskQueryImmutablePairMap.get(enumTaskQuery).getRight();
            switch (enumTaskQuery) {
                case PATIENT_ID:
                    iQuery = iQuery.where(new StringClientParam(Task.SP_PATIENT).matchesExactly().value(params.toString()));
                    first = false;
                    break;
                case GROUP_ID:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Task.SP_GROUP).matchesExactly().values((List<String>) params), first);
                    first = false;
                    break;
                case REASON_CODE:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Task.SP_REASON).matchesExactly().value(params.toString()), first);
                    first = false;
                    break;
                case DATERANGE_START:
                    iQuery = buildIQuery(iQuery, new DateClientParam(Task.SP_DATE_RANGE).afterOrEquals().day(params.toString()), first);
                    first = false;
                    break;
                case DATERANGE_END:
                    iQuery = buildIQuery(iQuery, new DateClientParam(Task.SP_DATE_RANGE).beforeOrEquals().day(params.toString()), first);
                    first = false;
                    break;
                case BUSINESS_STATUS:
                    iQuery = iQuery.and(new StringClientParam(Task.SP_BUSINESS_STATUS).matchesExactly().value(params.toString()));
                    first = false;
                    break;
                case STATUS:
                    iQuery = iQuery.and(new StringClientParam(Task.SP_STATUS).matchesExactly().value(params.toString()));
                    first = false;
                    break;
                case SORTING:
                    iQuery = buildIQuerySort(iQuery, matchs, params.toString());
                    break;
                case RECIPIENT:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Task.SP_RECIPIENT).matchesExactly().values((List<String>)params), first);
                    first = false;
                    break;
                case URGENT:
                    iQuery = iQuery.and(new StringClientParam(Task.SP_PATIENT_FLAG_TYPE).matchesExactly().value(params.toString()));
                    first = false;
                    break;
                default:
                    break;
            }
        }
        return iQuery;
    }
}