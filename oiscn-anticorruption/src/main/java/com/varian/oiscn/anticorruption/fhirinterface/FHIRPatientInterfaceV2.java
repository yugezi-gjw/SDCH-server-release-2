package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.CompositeClientParam;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Patient;
import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumPatientQuery;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 2/6/2018.
 */
@Slf4j
public class FHIRPatientInterfaceV2 extends FHIRInterfaceV2<Patient> {
    /**
     * Return Fhir Patient List by Patient Query Immutable PairMap.<br>
     *
     * @param patientQueryImmutablePairMap Patient Query Immutable PairMap
     * @return Fhir Patient List
     */
    public List<Patient> queryPatientList(Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap) {
        List<Patient> lstPatient = new ArrayList<>();
        if (patientQueryImmutablePairMap != null && !patientQueryImmutablePairMap.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            IQuery<IBaseBundle> iQuery = buildPatientQuery(client, patientQueryImmutablePairMap);
            long time1 = System.currentTimeMillis();
            org.hl7.fhir.dstu3.model.Bundle bundle = queryPatientBundle(iQuery);
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - PatientResource - QueryPatientList : {}", (time2 - time1) / 1000.0);
            if (bundle != null) {
                lstPatient = getListFromBundle(bundle);
            }
        }
        return lstPatient;
    }

    private org.hl7.fhir.dstu3.model.Bundle queryPatientBundle(IQuery<IBaseBundle> iQuery) {
        return iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))
                .returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)
                .execute();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private IQuery<IBaseBundle> buildPatientQuery(IGenericClient client, Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap) {
        boolean first = true;
        IQuery<IBaseBundle> iQuery = client.search().forResource(Patient.class);
        for (EnumPatientQuery enumPatientQuery : patientQueryImmutablePairMap.keySet()) {
            ImmutablePair<EnumMatchQuery, Object> enumMatchQueryObjectImmutablePair = patientQueryImmutablePairMap.get(enumPatientQuery);
            Object matchs = enumMatchQueryObjectImmutablePair.getLeft();
            Object params = enumMatchQueryObjectImmutablePair.getRight();
            switch (enumPatientQuery) {
                case PATIENT_ID:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_RES_ID).matchesExactly().value(params.toString()), first);
                    first = false;
                    break;
                case PATIENT_ID_LIST:
                    iQuery = buildIQuery(iQuery, new StringClientParam(Patient.SP_PATIENT_LIST).matchesExactly().values((List<String>) params), first);
                    first = false;
                    break;
                case ARIA_ID:
                    iQuery = buildIQuery(iQuery, new CompositeClientParam(Patient.SP_ID_TYPE_VALUE)
                            .withLeft(new StringClientParam(Patient.SP_IDType).matchesExactly().value("ARIA ID2"))
                            .withRight(new StringClientParam(Patient.SP_IDValue).matchesExactly().value(params.toString())), first);
                    first = false;
                    break;
                case HIS_ID:
                    iQuery = buildIQuery(iQuery, new CompositeClientParam(Patient.SP_ID_TYPE_VALUE)
                            .withLeft(new StringClientParam(Patient.SP_IDType).matchesExactly().value("ARIA ID1"))
                            .withRight(new StringClientParam(Patient.SP_IDValue).matchesExactly().value(params.toString())), first);
                    iQuery = iQuery.include(new Include(Patient.INCLUDE_ADDRESS));
                    first = false;
                    break;
                case HIS_ID_WITH_PHOTO:
                    iQuery = buildIQuery(iQuery, new CompositeClientParam(Patient.SP_ID_TYPE_VALUE)
                            .withLeft(new StringClientParam(Patient.SP_IDType).matchesExactly().value("ARIA ID1"))
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

    protected IQuery<IBaseBundle> buildIQuery(IQuery<IBaseBundle> iQuery, ICriterion<?> iCriterion, boolean first) {
        IQuery<IBaseBundle> tmpIQuery;
        if (first) {
            tmpIQuery = iQuery.where(iCriterion);
        } else {
            tmpIQuery = iQuery.and(iCriterion);
        }
        return tmpIQuery;
    }

    protected IQuery<IBaseBundle> buildIQuerySort(IQuery<IBaseBundle> iQuery, Object matchs, String params) {
        IQuery<IBaseBundle> tmpIQuery;
        if (matchs.equals(EnumMatchQuery.ASC)) {
            tmpIQuery = iQuery.sort().ascending(params);
        } else {
            tmpIQuery = iQuery.sort().descending(params);
        }
        return tmpIQuery;
    }
}
