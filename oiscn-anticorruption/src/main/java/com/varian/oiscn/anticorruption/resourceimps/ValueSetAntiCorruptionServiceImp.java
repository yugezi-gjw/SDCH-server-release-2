package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.ValueSet;
import com.varian.oiscn.anticorruption.assembler.ValueSetAssembler;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumValueSetQuery;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRValueSetInterface;
import com.varian.oiscn.core.codesystem.CodeSystem;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-06-16.
 */
public class ValueSetAntiCorruptionServiceImp {
    private FHIRValueSetInterface fhirValueSetInterface;

    /**
     * Default Constructor.<br>
     */
    public ValueSetAntiCorruptionServiceImp() {
        fhirValueSetInterface = new FHIRValueSetInterface();
    }

    /**
     * Return Diagnosis List.<br>
     *
     * @param schemeName Scheme Name
     * @param language   Language
     * @return Code System
     */
    public CodeSystem queryDiagnosisListByScheme(String schemeName, String language) {
        CodeSystem csDiagnosis = null;
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSIS-CODES"));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.NAME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, schemeName));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.LANGUAGE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, language));
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        if (!lstValueSet.isEmpty()) {
            csDiagnosis = ValueSetAssembler.getCodeSystem(lstValueSet.get(0));
        }

        return csDiagnosis;
    }

    /**
     * Return All Scheme List.<br>
     * @param language Language
     * @return Code System
     */
    public CodeSystem queryAllSchemes(String language) {
        CodeSystem csScheme = null;
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DiagnosisCodeScheme"));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.LANGUAGE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, language));
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        if (!lstValueSet.isEmpty()) {
            csScheme = ValueSetAssembler.getCodeSystem(lstValueSet.get(0));
        }

        return csScheme;
    }

    /**
     * Return All Primary Site List.<br>
     * @param language Language
     * @return Code System
     */
    public CodeSystem queryAllPrimarySites(String language) {
        CodeSystem csPrimarySite = null;
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSISPRIMARYSITE"));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.LANGUAGE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, language));
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        if (!lstValueSet.isEmpty()) {
            csPrimarySite = ValueSetAssembler.getCodeSystem(lstValueSet.get(0));
        }

        return csPrimarySite;
    }

    /**
     * Return All Language List.<br>
     * @return Code System
     */
    public CodeSystem queryAllLanguages() {
        return queryCodeSystemByTitle("LANGUAGES");
    }

    /**
     * Return All Status Icon List.<br>
     * @return Code System
     */
    public CodeSystem queryAllPatientStatusIcons() {
        return queryCodeSystemByTitle("STATUS-ICON");
    }

    /**
     * Return All CarePath Template List.<br>
     * @return Code System
     */
    public CodeSystem queryAllCarePathTemplates() {
        return queryCodeSystemByTitle("CAREPATH");
    }

    /**
     * Return Patient Label List.<br>
     * @return Code System
     */
    public CodeSystem queryAllPatientLabels() {
        return queryCodeSystemByTitle("PATIENT-LABEL-LIST");
    }

    /**
     * @return Code System
     */
    public CodeSystem queryStagingBasis() {
        return queryCodeSystemByTitle("DXSTAGINGBASIS");
    }

    /**
     * Query all payor info in ARIA
     * @return Code System
     */
    public CodeSystem queryAllPayorInfo() {
        return queryCodeSystemByTitle(ValueSet.PAYOR_INFO);
    }

    /**
     * Return Staging Scheme List.<br>
     * @param dxCode Dx Code
     * @param dxScheme Dx Scheme
     * @return Code System
     */
    public CodeSystem queryStagingSchemeByDxCodeAndDxScheme(String dxCode, String dxScheme) {
        CodeSystem csStagingScheme = null;
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TYPE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ValueSet.DIAGNOSIS_STAGE_SCHEME_FOR_CODE));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxCode));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_SCHEME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxScheme));
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        if (!lstValueSet.isEmpty()) {
            csStagingScheme = ValueSetAssembler.getCodeSystem(lstValueSet.get(0));
        }

        return csStagingScheme;
    }

    /**
     * Return Staging Value List.<br>
     * @param dxCode Dx Code
     * @param dxScheme Dx Scheme
     * @param stagingScheme Staging Scheme
     * @return Code System
     */
    public CodeSystem queryStagingValueByDxCodeAndDxSchemeAndStagingScheme(String dxCode, String dxScheme, String stagingScheme) {
        CodeSystem csStagingValue = null;
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TYPE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ValueSet.DIAGNOSIS_STAGE_SCHEME_CODE));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxCode));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_SCHEME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxScheme));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_STATE_SCHEME_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, stagingScheme));
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        if (!lstValueSet.isEmpty()) {
            csStagingValue = ValueSetAssembler.getCodeSystem(lstValueSet.get(0));
        }

        return csStagingValue;
    }

    protected CodeSystem queryCodeSystemByTitle(String title) {
        CodeSystem codeSystem = null;
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, title));
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        if (!lstValueSet.isEmpty()) {
            codeSystem = ValueSetAssembler.getCodeSystem(lstValueSet.get(0));
        }

        return codeSystem;
    }

}