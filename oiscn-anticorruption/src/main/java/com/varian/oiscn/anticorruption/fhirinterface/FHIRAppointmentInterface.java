package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.DateClientParam;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Appointment;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.converter.EnumAppointmentQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;

/**
 * Created by fmk9441 on 2017-02-13.
 */
@Slf4j
public class FHIRAppointmentInterface extends FHIRInterface <Appointment> {

    /**
     * Return Fhir Appointment List by Task Id.<br>
     * @param taskId Task Id
     * @return
     */
    public List<Appointment> queryAppointmentListByTaskId(String taskId) {
        List<Appointment> lstAppointment = new ArrayList<>();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();

        try {
            Task task = client.read().resource(Task.class).withId(taskId).execute();
            if (task != null && task.hasPartOf()) {
                task.getPartOf().forEach(reference -> {
                    String appointmentId = getReferenceValue(reference);
                    Appointment appointment = client.read().resource(Appointment.class).withId(appointmentId).execute();
                    lstAppointment.add(appointment);
                });
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        return lstAppointment;
    }

    /**
     * Return Fhir Appointment List by Appointment Query Immutable PairMap.<br>
     * @param appointmentQueryImmutablePairMap Appointment Query Immutable PairMap
     * @return Fhir Appointment List
     */
    public List<Appointment> queryAppointmentList(Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap) {
        List<Appointment> lstAppointment = new ArrayList<>();
        if (null != appointmentQueryImmutablePairMap && !appointmentQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            IQuery<IBaseBundle> iQuery = buildAppointmentQuery(client, appointmentQueryImmutablePairMap);
            long time1 = System.currentTimeMillis();
            Bundle bundle = iQuery.returnBundle(Bundle.class).execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - AppointmentResource - QueryAppointmentList : {}", (time2 - time1) / 1000.0);
            LogUtil.performanceLogging(log, "FHIR - AppointmentResource - QueryAppointmentList", (time2 - time1));
            lstAppointment = getListFromBundle(bundle);
        }
        return lstAppointment;
    }

    /**
     *
     * @param appointmentQueryImmutablePairMap
     * @param countPerPage
     * @param pageNumberFrom
     * @param pageNumberTo
     * @return
     */
    public Pagination<Appointment> queryPagingAppointmentList(Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Pagination<Appointment> appointmentPagination = new Pagination<>();
        if (null != appointmentQueryImmutablePairMap && !appointmentQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            IQuery<IBaseBundle> iQuery = buildAppointmentQuery(client, appointmentQueryImmutablePairMap);
            long time1 = System.currentTimeMillis();
            Bundle bundle = queryPagingBundle(iQuery, null,countPerPage);
            List<Bundle.BundleEntryComponent> bundleEntryComponentList = PaginationHelper.queryPagingBundle(client, bundle, pageNumberFrom, pageNumberTo, Arrays.asList(Appointment.class));
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - AppointmentResource - QueryAppointmentListByPage : {} s", (time2 - time1) / 1000.0);
            List<Appointment> lstAppointment = getListFromBundleEntryComponent(bundleEntryComponentList);
            appointmentPagination.setLstObject(lstAppointment);
            appointmentPagination.setTotalCount(bundle.getTotal());
        }
        return appointmentPagination;
    }

    @SuppressWarnings("unchecked")
    private IQuery<IBaseBundle> buildAppointmentQuery(IGenericClient client, Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap) {
        boolean first = true;
        IQuery<IBaseBundle> iQuery = client.search().forResource(Appointment.class);
        for (EnumAppointmentQuery enumAppointmentQuery : appointmentQueryImmutablePairMap.keySet()) {
            Object matchs = appointmentQueryImmutablePairMap.get(enumAppointmentQuery).getLeft();
            Object params = appointmentQueryImmutablePairMap.get(enumAppointmentQuery).getRight();
            switch (enumAppointmentQuery) {
                case APPOINTMENT_ID:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Appointment.SP_RES_ID).matchesExactly().value(params.toString()),first);
                    first = false;
                    break;
                case PATIENT_ID:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Appointment.SP_PATIENT).matchesExactly().value(params.toString()),first);
                    first = false;
                    break;
                case PRACTITIONER_ID:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Appointment.SP_PRACTITIONER).matchesExactly().value(params.toString()),first);
                    first = false;
                    break;
                case ACTOR_ID:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Appointment.SP_ACTOR).matchesExactly().values((List<String>) params),first);
                    first = false;
                    break;
                case APPOINTMENT_REASON:
                    iQuery = buildIQuery(iQuery, new StringClientParam("AppointmentReason").matchesExactly().value(params.toString()),first);
                    first = false;
                    break;
                case APPOINTMENT_DATE:
                    iQuery = buildIQuery(iQuery, new DateClientParam(Appointment.SP_DATE).exactly().day(params.toString()),first);
                    first = false;
                    break;
                case DATERANGE_START:
                    iQuery = buildIQuery(iQuery, new DateClientParam(Appointment.SP_DATE_RANGE).afterOrEquals().day(params.toString()),first);
                    first = false;
                    break;
                case DATERANGE_END:
                    iQuery = buildIQuery(iQuery, new DateClientParam(Appointment.SP_DATE_RANGE).beforeOrEquals().day(params.toString()),first);
                    first = false;
                    break;
                case STATUS:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Appointment.SP_STATUS).matchesExactly().values((List<String>) params),first);
                    first = false;
                    break;
                case SORTING:
                    iQuery = buildIQuerySort(iQuery, matchs, params.toString());
                    break;
                default:
                    break;
            }
        }
        return iQuery;
    }


}