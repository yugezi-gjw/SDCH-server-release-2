package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.ValueSet;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumValueSetQuery;
import com.varian.oiscn.anticorruption.datahelper.MockValueSetUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRValueSetInterface;
import com.varian.oiscn.core.codesystem.CodeSystem;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;

/**
 * Created by fmk9441 on 2017-06-19.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ValueSetAntiCorruptionServiceImp.class, FHIRValueSetInterface.class})
public class ValueSetAntiCorruptionServiceImpTest {
    private FHIRValueSetInterface fhirValueSetInterface;
    private ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirValueSetInterface = PowerMockito.mock(FHIRValueSetInterface.class);
        PowerMockito.whenNew(FHIRValueSetInterface.class).withNoArguments().thenReturn(fhirValueSetInterface);
        valueSetAntiCorruptionServiceImp = new ValueSetAntiCorruptionServiceImp();
    }

    @Test
    public void givenASchemeNameAndLanguageWhenQueryThenReturnCodeSystem() {
        final String schemeName = "SchemeName";
        final String language = "Language";
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSIS-CODES"));
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.NAME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, schemeName));
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.LANGUAGE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, language));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairLinkedHashMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryDiagnosisListByScheme(schemeName, language);
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void givenALanguageWhenQueryThenReturnAllPrimarySites() {
        final String language = "CHS";
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSISPRIMARYSITE"));
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.LANGUAGE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, language));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairLinkedHashMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryAllPrimarySites(language);
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void givneALanguageWhenQueryThenReturnAllSchemes() {
        final String language = "CHS";
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DiagnosisCodeScheme"));
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.LANGUAGE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, language));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairLinkedHashMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryAllSchemes(language);
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void doWhenQueryThenReturnAllLanguages() {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "LANGUAGES"));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairLinkedHashMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryAllLanguages();
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void doWhenQueryThenReturnAllStatusIcons() {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "STATUS-ICON"));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairLinkedHashMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryAllPatientStatusIcons();
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void doWhenQueryThenReturnAllCarePathTemplates() {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "CAREPATH"));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairLinkedHashMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryAllCarePathTemplates();
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void doWhenQueryThenReturnAllPatientLabels() {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "PATIENT-LABEL-LIST"));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryAllPatientLabels();
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void givenADxCodeDxSchemeDxStagingSchemeWhenQueryThenReturnAllTNMOptions() {
        final String dxCode = "DxCode";
        final String dxScheme = "DxScheme";
        final String dxStagingScheme = "DxStagingScheme";
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TYPE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ValueSet.DIAGNOSIS_STAGE_SCHEME_CODE));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxCode));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_SCHEME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxScheme));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_STATE_SCHEME_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxStagingScheme));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryStagingValueByDxCodeAndDxSchemeAndStagingScheme(dxCode, dxScheme, dxStagingScheme);
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void testQueryStagingSchemeByDxCodeAndDxScheme() {
        final String dxCode = "DxCode";
        final String dxScheme = "DxScheme";
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TYPE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ValueSet.DIAGNOSIS_STAGE_SCHEME_FOR_CODE));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxCode));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_SCHEME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, dxScheme));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryStagingSchemeByDxCodeAndDxScheme(dxCode, dxScheme);
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void testQueryAllPayorInfo() {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ValueSet.PAYOR_INFO));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairLinkedHashMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryAllPayorInfo();
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }

    @Test
    public void testQueryStagingBasis() {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairLinkedHashMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DXSTAGINGBASIS"));
        List<ValueSet> lstValueSet = MockValueSetUtil.givenAValueSetList();
        PowerMockito.when(fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairLinkedHashMap)).thenReturn(lstValueSet);
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryStagingBasis();
        Assert.assertNotNull(codeSystem);
        Assert.assertThat(1, is(codeSystem.getCodeValues().size()));
    }
}