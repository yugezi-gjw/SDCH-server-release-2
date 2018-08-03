package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.CompositeClientParam;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import com.varian.fhir.resources.Patient;
import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumPatientQuery;
import com.varian.oiscn.anticorruption.exception.FhirCreatePatientException;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-01-16.
 */
@Slf4j
public class FHIRPatientInterface extends FHIRInterface<Patient>{

    /**
     * Create Fhir Patient.<br>
     *
     * @param patient Fhir Patient
     * @return new Id
     */
    @Override
    public String create(Patient patient) {
        String createdPatientId = StringUtils.EMPTY;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            long time1 = System.currentTimeMillis();
            MethodOutcome response = client.create().resource(patient).execute();
            createdPatientId = response.getId().getIdPart();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - PatientResource - Create : {}", (time2 - time1) / 1000.0);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            if (e instanceof BaseServerResponseException) {
                BaseServerResponseException fhirServerException = (BaseServerResponseException) e;
                String body = fhirServerException.getResponseBody();
                log.debug("BaseServerResponseException body: [{}]", body);
                FhirCreatePatientException fe = new FhirCreatePatientException(fhirServerException);
                if (body != null && body.contains("SSNNotUnique")) {
                    fe.setErrorItemId("nationalId");
                }
                throw fe;
            }
        }
        return createdPatientId;
    }

    /**
     * Return Fhir Patient List by Patient Query Immutable PairMap.<br>
     * @param patientQueryImmutablePairMap Patient Query Immutable PairMap
     * @return Fhir Patient List
     */
    public List<Patient> queryPatientList(Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap) {
        List<Patient> lstPatient = new ArrayList<>();
        if (patientQueryImmutablePairMap != null && !patientQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            IQuery<IBaseBundle> iQuery = buildPatientQuery(client, patientQueryImmutablePairMap);
            long time1 = System.currentTimeMillis();
            Bundle bundle = queryPatientBundle(iQuery);
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - PatientResource - QueryPatientList : {}", (time2 - time1) / 1000.0);
            if (bundle != null) {
                lstPatient = getListFromBundle(bundle);
            }
        }
        return lstPatient;
    }

    private Bundle queryPatientBundle(IQuery<IBaseBundle> iQuery) {
        return iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))
                .returnBundle(Bundle.class)
                .execute();
    }

    /**
     * Return Fhir Pagination Patient List by Patient Query Immutable PairMap.<br>
     * @param patientQueryImmutablePairMap Patient Query Immutable PairMap
     * @param countPerPage Count Per Page
     * @param pageNumberFrom Page Number From
     * @return Fhir Pagination Patient List
     */
    public Pagination<Patient> queryPagingPatientList(Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap, int countPerPage, int pageNumberFrom,int pageNumberTo) {
        Pagination<Patient> patientPagination = new Pagination<>();
        if (patientQueryImmutablePairMap != null && !patientQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            IQuery<IBaseBundle> iQuery = buildPatientQuery(client, patientQueryImmutablePairMap);
            long time1 = System.currentTimeMillis();
            Bundle bundle = queryPagingBundle(iQuery,Arrays.asList(Patient.class, Practitioner.class), countPerPage);

            if (bundle != null) {
                List<Bundle.BundleEntryComponent> bundleEntryComponentList = PaginationHelper.queryPagingBundle(client, bundle, pageNumberFrom, pageNumberTo, Arrays.asList(Patient.class, Practitioner.class));
                long time2 = System.currentTimeMillis();
                log.debug("FHIR - PatientResource - QueryPatientListByPage : {}", (time2 - time1) / 1000.0);
                List<Patient> lstPatient = getListFromBundleEntryComponent(bundleEntryComponentList);
                patientPagination.setLstObject(lstPatient);
                patientPagination.setTotalCount(bundle.getTotal());
            }
        }
        return patientPagination;
    }



    @SuppressWarnings({"rawtypes", "unchecked"})
    private IQuery<IBaseBundle> buildPatientQuery(IGenericClient client, Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap) {
        boolean first = true;
        IQuery<IBaseBundle> iQuery = client.search().forResource(Patient.class);
        String searchWithARIAId;
        if (StringUtils.equals(PatientIdMapper.getPatientId1Mapper(), PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID)) {
            searchWithARIAId = "ARIA ID1";
        } else {
            searchWithARIAId = "ARIA ID2";
        }
        String searchWithHisId;
        if (StringUtils.equals(PatientIdMapper.getPatientId1Mapper(), PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID)) {
            searchWithHisId = "ARIA ID1";
        } else {
            searchWithHisId = "ARIA ID2";
        }
        for (EnumPatientQuery enumPatientQuery : patientQueryImmutablePairMap.keySet()) {
            ImmutablePair<EnumMatchQuery, Object> enumMatchQueryObjectImmutablePair = patientQueryImmutablePairMap.get(enumPatientQuery);
            Object matchs = enumMatchQueryObjectImmutablePair.getLeft();
            Object params = enumMatchQueryObjectImmutablePair.getRight();
            switch (enumPatientQuery) {
                case PATIENT_ID:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_RES_ID).matchesExactly().value(params.toString()), first);
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_ADDRESS));
                    first = false;
                    break;
                case PATIENT_ID_WITH_PHOTO:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_RES_ID).matchesExactly().value(params.toString()), first);
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_ADDRESS));
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_PHOTO));
                    first = false;
                    break;
                case PATIENT_ID_LIST:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_PATIENT_LIST).matchesExactly().values((List<String>) params), first);
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_ADDRESS));
                    first = false;
                    break;
                case ARIA_ID:
                    iQuery = buildIQuery(iQuery, new CompositeClientParam(Patient.SP_ID_TYPE_VALUE)
                            .withLeft(new StringClientParam(Patient.SP_IDType).matchesExactly().value(searchWithARIAId))
                            .withRight(new StringClientParam(Patient.SP_IDValue).matchesExactly().value(params.toString())), first);
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_ADDRESS));
                    first = false;
                    break;
                case HIS_ID:
                    iQuery = buildIQuery(iQuery, new CompositeClientParam(Patient.SP_ID_TYPE_VALUE)
                            .withLeft(new StringClientParam(Patient.SP_IDType).matchesExactly().value(searchWithHisId))
                            .withRight(new StringClientParam(Patient.SP_IDValue).matchesExactly().value(params.toString())), first);
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_ADDRESS));
                    first = false;
                    break;
                case HIS_ID_WITH_PHOTO:
                    iQuery = buildIQuery(iQuery, new CompositeClientParam(Patient.SP_ID_TYPE_VALUE)
                            .withLeft(new StringClientParam(Patient.SP_IDType).matchesExactly().value(searchWithHisId))
                            .withRight(new StringClientParam(Patient.SP_IDValue).matchesExactly().value(params.toString())), first);
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_ADDRESS));
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_PHOTO));
                    first = false;
                    break;
                case PATIENT_NAME:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_FAMILY).matches().value(params.toString()), first);
                    first = false;
                    break;
                case PATIENT_PINYIN:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_GIVEN).matches().value(params.toString()), first);
                    first = false;
                    break;
                case PRIMARY_PHYSICIAN:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_PRIMARY_PRACTITIONER).matchesExactly().values((List<String>) params), first);
                    first = false;
                    break;
                case STATUS:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_ACTIVE).matchesExactly().value(params.toString()), first);
                    first = false;
                    break;
                case ICON_FLAG_ID:
                    first = false;
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_PATIENT_FLAG).matchesExactly().value(params.toString()), first);
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