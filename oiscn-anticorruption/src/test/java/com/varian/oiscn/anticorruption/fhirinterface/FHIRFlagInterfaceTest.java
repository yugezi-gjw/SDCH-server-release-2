package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import com.varian.fhir.resources.Flag;
import com.varian.oiscn.anticorruption.converter.EnumFlagQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.datahelper.MockFlagUtil;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.*;

/**
 * Created by fmk9441 on 2017-06-23.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRFlagInterface.class, FHIRContextFactory.class,IdType.class})
public class FHIRFlagInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRFlagInterface fhirFlagInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirFlagInterface = new FHIRFlagInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenAFlagWhenCreateThenReturnId() {
        Flag flag = MockFlagUtil.givenAFlag();
        ICreate iCreate = PowerMockito.mock(ICreate.class);
        PowerMockito.when(client.create()).thenReturn(iCreate);
        ICreateTyped iCreateTyped = PowerMockito.mock(ICreateTyped.class);
        PowerMockito.when(iCreate.resource(flag)).thenReturn(iCreateTyped);
        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iCreateTyped.execute()).thenReturn(methodOutcome);
        IdType idType = PowerMockito.mock(IdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(idType);
        PowerMockito.when(idType.getIdPart()).thenReturn("12");
        String ret = fhirFlagInterface.create(flag);
        Assert.assertTrue(StringUtils.isNotEmpty(ret));
    }

    @Test
    public void givenAFlagWhenCreateThenThrowException() {
        Flag flag = MockFlagUtil.givenAFlag();
        PowerMockito.when(client.create()).thenThrow(Exception.class);
        String retId = fhirFlagInterface.create(flag);
        Assert.assertTrue(StringUtils.isEmpty(retId));
    }

    @Test
    public void givenAFlagWhenDeleteThenReturnDeletedFlag() {
        Flag flag = MockFlagUtil.givenAFlag();
        IDelete iDelete = PowerMockito.mock(IDelete.class);
        PowerMockito.when(client.delete()).thenReturn(iDelete);
        IDeleteTyped iDeleteTyped = PowerMockito.mock(IDeleteTyped.class);
        PowerMockito.when(iDelete.resource(flag)).thenReturn(iDeleteTyped);
        IBaseOperationOutcome iBaseOperationOutcome = PowerMockito.mock(IBaseOperationOutcome.class);
        PowerMockito.when(iDeleteTyped.execute()).thenReturn(iBaseOperationOutcome);
        PowerMockito.when(iBaseOperationOutcome.isEmpty()).thenReturn(false);

        Boolean ret = fhirFlagInterface.deleteFlag(flag);
        Assert.assertTrue(ret);
    }

    @Test
    public void givenAFlagWhenDeleteThenThrowException() {
        Flag flag = MockFlagUtil.givenAFlag();
        PowerMockito.when(client.delete()).thenThrow(Exception.class);
        Boolean ret = fhirFlagInterface.deleteFlag(flag);
        Assert.assertFalse(ret);
    }

    @Test
    public void givenANullMapWhenQueryThenThrowEmptyFlagList() {
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = null;
        List<Flag> lstFlag = fhirFlagInterface.queryFlagList(flagQueryImmutablePairMap);
        Assert.assertTrue(lstFlag.isEmpty());
    }

    @Test
    public void givenAnEmptyMapWhenQueryThenThrowEmptyFlagList() {
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = new LinkedHashMap<>();
        List<Flag> lstFlag = fhirFlagInterface.queryFlagList(flagQueryImmutablePairMap);
        Assert.assertTrue(lstFlag.isEmpty());
    }

    @Test
    public void givenAPatientIdAndFlagCodeWhenQueryThenReturnFlagList() throws Exception {
        final String patientId = "PatientID";
        final String flagCode = "FlagCode";
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = new LinkedHashMap<>();
        flagQueryImmutablePairMap.put(EnumFlagQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(patientId)));
        flagQueryImmutablePairMap.put(EnumFlagQuery.FLAG_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, flagCode));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Flag.class)).thenReturn(iQuery);

        StringClientParam stringClientPatientID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Flag.SP_PATIENT).thenReturn(stringClientPatientID);
        StringClientParam.IStringMatch iStringMatchPatientID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientPatientID.matchesExactly()).thenReturn(iStringMatchPatientID);
        ICriterion iCriterionPatientID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPatientID.values(anyList())).thenReturn(iCriterionPatientID);
        PowerMockito.when(iQuery.where(iCriterionPatientID)).thenReturn(iQuery);

        StringClientParam stringClientParamFlagCode = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Flag.SP_CODE).thenReturn(stringClientParamFlagCode);
        StringClientParam.IStringMatch iStringMatchFlagCode = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamFlagCode.matchesExactly()).thenReturn(iStringMatchFlagCode);
        ICriterion iCriterionFlagCode = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchFlagCode.value(anyString())).thenReturn(iCriterionFlagCode);
        PowerMockito.when(iQuery.and(iCriterionFlagCode)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockFlagUtil.givenAFlagBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Flag> lstFlag = fhirFlagInterface.queryFlagList(flagQueryImmutablePairMap);
        Assert.assertThat(1, is(lstFlag.size()));
    }

    @Test
    public void givenANullMapWhenQueryThenThrowEmptyFlagPagingList() {
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = null;
        Pagination<Flag> flagPagination = fhirFlagInterface.queryPagingFlagList(flagQueryImmutablePairMap, 1, 1,1);
        Assert.assertNull(flagPagination.getLstObject());
        Assert.assertThat(0, is(flagPagination.getTotalCount()));
    }

    @Test
    public void givenAnEmptyMapWhenQueryThenThrowEmptyFlagPagingList() {
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = new LinkedHashMap<>();
        Pagination<Flag> flagPagination = fhirFlagInterface.queryPagingFlagList(flagQueryImmutablePairMap, 1, 1,1);
        Assert.assertNull(flagPagination.getLstObject());
        Assert.assertThat(0, is(flagPagination.getTotalCount()));
    }

    @Test
    public void givenAPatientIdAndFlagCodeWhenQueryThenReturnFlagPagingList() throws Exception {
        final String patientId = "PatientID";
        final String flagCode = "FlagCode";
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = new LinkedHashMap<>();
        flagQueryImmutablePairMap.put(EnumFlagQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(patientId)));
        flagQueryImmutablePairMap.put(EnumFlagQuery.FLAG_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, flagCode));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Flag.class)).thenReturn(iQuery);

        StringClientParam stringClientPatientID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Flag.SP_PATIENT).thenReturn(stringClientPatientID);
        StringClientParam.IStringMatch iStringMatchPatientID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientPatientID.matchesExactly()).thenReturn(iStringMatchPatientID);
        ICriterion iCriterionPatientID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPatientID.values(anyList())).thenReturn(iCriterionPatientID);
        PowerMockito.when(iQuery.where(iCriterionPatientID)).thenReturn(iQuery);

        StringClientParam stringClientParamFlagCode = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Flag.SP_CODE).thenReturn(stringClientParamFlagCode);
        StringClientParam.IStringMatch iStringMatchFlagCode = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamFlagCode.matchesExactly()).thenReturn(iStringMatchFlagCode);
        ICriterion iCriterionFlagCode = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchFlagCode.value(anyString())).thenReturn(iCriterionFlagCode);
        PowerMockito.when(iQuery.and(iCriterionFlagCode)).thenReturn(iQuery);

        PowerMockito.when(iQuery.count(anyInt())).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockFlagUtil.givenAFlagBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        Pagination<Flag> flagPagination = fhirFlagInterface.queryPagingFlagList(flagQueryImmutablePairMap, 5, 1,1);
        Assert.assertThat(5, is(flagPagination.getTotalCount()));
        Assert.assertThat(1, is(flagPagination.getLstObject().size()));
    }
}