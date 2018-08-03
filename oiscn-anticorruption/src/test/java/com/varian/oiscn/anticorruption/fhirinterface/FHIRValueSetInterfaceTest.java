package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.IUntypedQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.ValueSet;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumValueSetQuery;
import com.varian.oiscn.anticorruption.datahelper.MockValueSetUtil;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
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
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-06-19.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRValueSetInterface.class, FHIRContextFactory.class})
public class FHIRValueSetInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRValueSetInterface fhirValueSetInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirValueSetInterface = new FHIRValueSetInterface();
        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenANullMapWhenQueryThenReturnEmptyValueSetList() throws Exception {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = null;
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        Assert.assertTrue(lstValueSet.isEmpty());
    }

    @Test
    public void givenAnEmptyMapWhenQueryThenReturnEmptyValueSetList() throws Exception {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        Assert.assertTrue(lstValueSet.isEmpty());
    }

    @Test
    public void givenAMapWhenQueryThenThrowException() throws Exception {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSIS-CODES"));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.NAME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSIS-SCHEME"));
        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        Assert.assertTrue(lstValueSet.isEmpty());
    }

    @Test
    public void givenAMapWithTitleAndNameWhenQueryThenReturnValueSetList() throws Exception {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSIS-CODES"));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.NAME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSIS-SCHEME"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(ValueSet.class)).thenReturn(iQuery);

        StringClientParam stringClientParamTitle = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(ValueSet.SP_TITLE).thenReturn(stringClientParamTitle);
        StringClientParam.IStringMatch iStringMatchTitle = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamTitle.matchesExactly()).thenReturn(iStringMatchTitle);
        ICriterion iCriterionTitle = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchTitle.value(anyString())).thenReturn(iCriterionTitle);
        PowerMockito.when(iQuery.where(iCriterionTitle)).thenReturn(iQuery);

        StringClientParam stringClientParamName = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(ValueSet.SP_NAME).thenReturn(stringClientParamName);
        StringClientParam.IStringMatch iStringMatchName = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamName.matchesExactly()).thenReturn(iStringMatchName);
        ICriterion iCriterionName = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchName.value(anyString())).thenReturn(iCriterionName);
        PowerMockito.when(iQuery.and(iCriterionName)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockValueSetUtil.givenAValueSetBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        Assert.assertThat(1, is(lstValueSet.size()));
    }

    @Test
    public void givenAMapWithTitleAndLanguageWhenQueryThenReturnValueSetList() throws Exception {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TITLE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DIAGNOSIS-CODES"));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.LANGUAGE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "LANGUAGES"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(ValueSet.class)).thenReturn(iQuery);

        StringClientParam stringClientParamTitle = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(ValueSet.SP_TITLE).thenReturn(stringClientParamTitle);
        StringClientParam.IStringMatch iStringMatchTitle = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamTitle.matchesExactly()).thenReturn(iStringMatchTitle);
        ICriterion iCriterionTitle = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchTitle.value(anyString())).thenReturn(iCriterionTitle);
        PowerMockito.when(iQuery.where(iCriterionTitle)).thenReturn(iQuery);

        StringClientParam stringClientParamLanguage = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(ValueSet.SP_RES_LANGUAGE).thenReturn(stringClientParamLanguage);
        StringClientParam.IStringMatch iStringMatchLanguage = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamLanguage.matchesExactly()).thenReturn(iStringMatchLanguage);
        ICriterion iCriterionLanguage = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchLanguage.value(anyString())).thenReturn(iCriterionLanguage);
        PowerMockito.when(iQuery.and(iCriterionLanguage)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockValueSetUtil.givenAValueSetBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        Assert.assertThat(1, is(lstValueSet.size()));
    }

    @Test
    public void givenAMapWithTypeAndDxCodeAndDxSchemdAndStagingSchemeCodeThenReturnValueSetList() throws Exception {
        Map<EnumValueSetQuery, ImmutablePair<EnumMatchQuery, Object>> valueSetQueryImmutablePairMap = new LinkedHashMap<>();
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.TYPE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ValueSet.DIAGNOSIS_STAGE_SCHEME_CODE));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DxCode"));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_SCHEME, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DxScheme"));
        valueSetQueryImmutablePairMap.put(EnumValueSetQuery.DIAGNOSIS_STATE_SCHEME_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "DxStagingSchemeCode"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(ValueSet.class)).thenReturn(iQuery);

        StringClientParam stringClientParamType = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(ValueSet.SP_TYPE).thenReturn(stringClientParamType);
        StringClientParam.IStringMatch iStringMatchType = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamType.matchesExactly()).thenReturn(iStringMatchType);
        ICriterion iCriterionType = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchType.value(anyString())).thenReturn(iCriterionType);
        PowerMockito.when(iQuery.where(iCriterionType)).thenReturn(iQuery);

        StringClientParam stringClientParamDxCode = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(ValueSet.SP_DIAGNOSIS_CODE).thenReturn(stringClientParamDxCode);
        StringClientParam.IStringMatch iStringMatchDxCode = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamDxCode.matchesExactly()).thenReturn(iStringMatchDxCode);
        ICriterion iCriterionDxCode = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchDxCode.value(anyString())).thenReturn(iCriterionDxCode);
        PowerMockito.when(iQuery.and(iCriterionDxCode)).thenReturn(iQuery);

        StringClientParam stringClientParamDxScheme = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(ValueSet.SP_DIAGNOSIS_SCHEME).thenReturn(stringClientParamDxScheme);
        StringClientParam.IStringMatch iStringMatchDxScheme = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamDxScheme.matchesExactly()).thenReturn(iStringMatchDxScheme);
        ICriterion iCriterionDxScheme = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchDxScheme.value(anyString())).thenReturn(iCriterionDxScheme);
        PowerMockito.when(iQuery.and(iCriterionDxScheme)).thenReturn(iQuery);

        StringClientParam stringClientParamDxStagingScheme = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(ValueSet.SP_DIAGNOSIS_STAGE_SCHEME_CODE).thenReturn(stringClientParamDxStagingScheme);
        StringClientParam.IStringMatch iStringMatchDxStagingScheme = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamDxStagingScheme.matchesExactly()).thenReturn(iStringMatchDxStagingScheme);
        ICriterion iCriterionDxStagingScheme = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchDxStagingScheme.value(anyString())).thenReturn(iCriterionDxStagingScheme);
        PowerMockito.when(iQuery.and(iCriterionDxStagingScheme)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockValueSetUtil.givenAValueSetBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<ValueSet> lstValueSet = fhirValueSetInterface.queryValueSetList(valueSetQueryImmutablePairMap);
        Assert.assertThat(1, is(lstValueSet.size()));
    }
}