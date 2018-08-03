package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.ValueSet;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumValueSetQuery;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-06-16.
 */
@Slf4j
public class FHIRValueSetInterface extends FHIRInterface<ValueSet>{

    /**
     * Return Fhir ValueSet List by ValueSet Query Immutable PairMap.<br>
     *
     * @param valueSetQueryImmutablePairMap ValueSet Query Immutable PairMap
     * @return Fhir ValueSet List
     */
    public List<ValueSet> queryValueSetList(Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap) {
        List<ValueSet> lstValueSet = new ArrayList<>();
        if (null == valueSetQueryImmutablePairMap || valueSetQueryImmutablePairMap.isEmpty())
            return lstValueSet;

        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            boolean first = true;
            IQuery<IBaseBundle> iQuery = client.search().forResource(ValueSet.class);
            for (EnumValueSetQuery enumValueSetQuery : valueSetQueryImmutablePairMap.keySet()) {
                Object params = valueSetQueryImmutablePairMap.get(enumValueSetQuery).getRight();
                switch (enumValueSetQuery) {
                    case TYPE:
                        iQuery = iQuery.where(new StringClientParam(ValueSet.SP_TYPE).matchesExactly().value(params.toString()));
                        first = false;
                        break;
                    case TITLE:
                        iQuery = buildIQuery(iQuery, new StringClientParam(ValueSet.SP_TITLE).matchesExactly().value(params.toString()), first);
                        first = false;
                        break;
                    case NAME:
                        iQuery = buildIQuery(iQuery, new StringClientParam(ValueSet.SP_NAME).matchesExactly().value(params.toString()), first);
                        first = false;
                        break;
                    case DIAGNOSIS_CODE:
                        iQuery = iQuery.and(new StringClientParam(ValueSet.SP_DIAGNOSIS_CODE).matchesExactly().value(params.toString()));
                        break;
                    case DIAGNOSIS_SCHEME:
                        iQuery = iQuery.and(new StringClientParam(ValueSet.SP_DIAGNOSIS_SCHEME).matchesExactly().value(params.toString()));
                        break;
                    case DIAGNOSIS_STATE_SCHEME_CODE:
                        iQuery = iQuery.and(new StringClientParam(ValueSet.SP_DIAGNOSIS_STAGE_SCHEME_CODE).matchesExactly().value(params.toString()));
                        break;
                    case LANGUAGE:
                        iQuery = iQuery.and(new StringClientParam(ValueSet.SP_RES_LANGUAGE).matchesExactly().value(params.toString()));
                        break;
                    default:
                        break;
                }
            }

            long time1 = System.currentTimeMillis();
            Bundle bundle = iQuery.returnBundle(Bundle.class).execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - ValueSetResource - QueryList : {}", (time2 - time1) / 1000.0);
            lstValueSet = getListFromBundle(bundle);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return lstValueSet;
    }

}
