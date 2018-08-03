package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Condition;
import com.varian.oiscn.anticorruption.assembler.ConditionAssembler;
import com.varian.oiscn.anticorruption.converter.EnumConditionQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRConditionInterface;
import com.varian.oiscn.core.patient.Diagnosis;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.codesystems.ConditionCategory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-05-16.
 */
public class DiagnosisAntiCorruptionServiceImp {
    private FHIRConditionInterface fhirConditionInterface;

    /**
     * Default Constructor.<br>
     */
    public DiagnosisAntiCorruptionServiceImp() {
        fhirConditionInterface = new FHIRConditionInterface();
    }

    /**
     * Create Fhir Diagnosis.<br>
     *
     * @param diagnosis Fhir Diagnosis
     * @return new Id
     */
    public String createDiagnosis(Diagnosis diagnosis){
        Condition condition = ConditionAssembler.getCondition(diagnosis);
        return fhirConditionInterface.create(condition);
    }

    /**
     * Update Fhir Diagnosis
     * @param diagnosis Fhir Diagnosis
     * @return id
     */
    public String updateDiagnosis(Diagnosis diagnosis) {
        String updatedDiagnosisID = StringUtils.EMPTY;
        List<Condition> lstCondition = getConditionListByPatientID(diagnosis.getPatientID());
        if (!lstCondition.isEmpty()) {
            Condition condition = lstCondition.get(0);
            ConditionAssembler.updateCondition(condition, diagnosis);
            updatedDiagnosisID = fhirConditionInterface.update(condition);
        }
        return updatedDiagnosisID;
    }

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
        return fhirConditionInterface.calculateStageSummary(dxCode, dxScheme, stagingScheme, tCode, nCode, mCode);
    }

    /**
     * Return Fhir Diagnosis List by Patient Id.<br>
     * @param patientID Patient Id
     * @return Fhir Diagnosis List
     */
    public List<Diagnosis> queryDiagnosisListByPatientID(String patientID) {
        List<Diagnosis> lstDiagnosis = new ArrayList<>();
        List<Condition> lstCondition = getConditionListByPatientID(patientID);
        lstCondition.forEach(condition -> lstDiagnosis.add(ConditionAssembler.getDiagnosis(condition)));
        return lstDiagnosis;
    }

    private List<Condition> getConditionListByPatientID(String patientID) {
        Map<EnumConditionQuery, ImmutablePair<EnumMatchQuery, Object>> conditionQueryImmutablePairMap = new LinkedHashMap<>();
        conditionQueryImmutablePairMap.put(EnumConditionQuery.CATEGORY, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ConditionCategory.ENCOUNTERDIAGNOSIS.toCode()));
        conditionQueryImmutablePairMap.put(EnumConditionQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientID));
        return fhirConditionInterface.queryConditionList(conditionQueryImmutablePairMap);
    }
}