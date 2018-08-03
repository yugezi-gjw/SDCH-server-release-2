package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Condition;
import com.varian.oiscn.anticorruption.converter.EnumConditionQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-05-16.
 */
@Slf4j
public class FHIRConditionInterface extends FHIRInterface<Condition>{
    /**
     * Calculate Stage Summary.<br>
     * @param dxCode Dx Code
     * @param dxScheme Dx Scheme
     * @param stagingScheme Staging Scheme
     * @param tCode T Code
     * @param nCode N Code
     * @param mCode M Code
     * @return Stage Summary
     */
    public String calculateStageSummary(String dxCode, String dxScheme, String stagingScheme, String tCode, String nCode, String mCode) {
        String stageSummary = StringUtils.EMPTY;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        Parameters inParams = new Parameters();
        inParams.addParameter().setName("dxCode").setValue(new StringType(dxCode));
        inParams.addParameter().setName("dxScheme").setValue(new StringType(dxScheme));
        inParams.addParameter().setName("stagingScheme").setValue(new StringType(stagingScheme));
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding().setCode(tCode).setSystem("T");
        codeableConcept.addCoding().setCode(nCode).setSystem("N");
        codeableConcept.addCoding().setCode(mCode).setSystem("M");
        inParams.addParameter().setName("stagingCriteria").setValue(codeableConcept);
        Parameters outParams = client.operation().onType(Condition.class).named("$calculateStage").withParameters(inParams).execute();
        if (!outParams.isEmpty()) {
            stageSummary = ((StringType) outParams.getParameter().get(0).getValue()).getValue().trim();
        }
        return stageSummary;
    }

    /**
     * Return Fhir Condition List by Condition Query Immutable PairMap.<br>
     * @param conditionQueryImmutablePairMap Condition Query Immutable PairMap
     * @return Fhir Condition List
     */
    public List<Condition> queryConditionList(Map<EnumConditionQuery, ImmutablePair<EnumMatchQuery, Object>> conditionQueryImmutablePairMap) {
        List<Condition> lstCondition = new ArrayList<>();
        if (null == conditionQueryImmutablePairMap || conditionQueryImmutablePairMap.isEmpty())
            return lstCondition;

        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            boolean first = true;
            IQuery<IBaseBundle> iQuery = client.search().forResource(Condition.class);
            for (EnumConditionQuery enumConditionQuery : conditionQueryImmutablePairMap.keySet()) {
                ImmutablePair<EnumMatchQuery, Object> enumMatchQueryObjectImmutablePair = conditionQueryImmutablePairMap.get(enumConditionQuery);
                Object params = enumMatchQueryObjectImmutablePair.getRight();
                switch (enumConditionQuery) {
                    case CATEGORY:
                        iQuery = buildIQuery(iQuery, new StringClientParam(Condition.SP_CATEGORY).matchesExactly().value(params.toString()), first);
                        first = false;
                        break;
                    case PATIENT_ID:
                        iQuery = buildIQuery(iQuery, new StringClientParam(Condition.SP_PATIENT).matchesExactly().value(params.toString()), first);
                        first = false;
                        break;
                    default:
                        break;
                }
            }

            long time1 = System.currentTimeMillis();
            Bundle bundle = iQuery.returnBundle(Bundle.class).execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - ConditionResource - QueryConditionList : {}", (time2 - time1) / 1000.0);
            lstCondition = getListFromBundle(bundle);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        return lstCondition;
    }


}