package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.IUntypedQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Device;
import com.varian.oiscn.anticorruption.converter.EnumDeviceQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.datahelper.MockDeviceUtil;
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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-02-17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRDeviceInterface.class, FHIRContextFactory.class})
public class FHIRDeviceInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRDeviceInterface fhirDeviceInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirDeviceInterface = new FHIRDeviceInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenANullLinkedHashMapWhenQueryThenThrowEmptyDeviceList() throws Exception {
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = null;
        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairMap);
        Assert.assertTrue(lstDevice.isEmpty());
    }

    @Test
    public void givenAnEmptyLinkedHashMapWhenQueryThenThrowEmptyDeviceList() throws Exception {
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = new LinkedHashMap<>();
        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairMap);
        Assert.assertTrue(lstDevice.isEmpty());
    }

    @Test
    public void givenAMapWhenQueryThenThrowException() throws Exception {
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = new LinkedHashMap<>();
        deviceQueryImmutablePairMap.put(EnumDeviceQuery.ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "ID"));
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairMap);
        Assert.assertTrue(lstDevice.isEmpty());
    }

    @Test
    public void givenAMapWithIDWhenQueryThenReturnDeviceList() throws Exception {
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = new LinkedHashMap<>();
        deviceQueryImmutablePairMap.put(EnumDeviceQuery.ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "ID"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Device.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Device.SP_RES_ID).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);

        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockDeviceUtil.givenADeviceBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairMap);
        Assert.assertThat(1, is(lstDevice.size()));
    }

    @Test
    public void givenAMapWithTypeWhenQueryThenReturnDeviceList() throws Exception {
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = new LinkedHashMap<>();
        deviceQueryImmutablePairMap.put(EnumDeviceQuery.TYPE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Type"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Device.class)).thenReturn(iQuery);

        StringClientParam stringClientParamType = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Device.SP_TYPE).thenReturn(stringClientParamType);
        StringClientParam.IStringMatch iStringMatchType = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamType.matchesExactly()).thenReturn(iStringMatchType);
        ICriterion iCriterionType = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchType.value(anyString())).thenReturn(iCriterionType);
        PowerMockito.when(iQuery.where(iCriterionType)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockDeviceUtil.givenADeviceBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairMap);
        Assert.assertThat(1, is(lstDevice.size()));
    }

    @Test
    public void givenAMapWithCodeWhenQueryThenReturnDeviceList() throws Exception {
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = new LinkedHashMap<>();
        deviceQueryImmutablePairMap.put(EnumDeviceQuery.CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Code"));
        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Device.class)).thenReturn(iQuery);

        StringClientParam stringClientParamCode = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Device.SP_DEVICE_ID).thenReturn(stringClientParamCode);
        StringClientParam.IStringMatch iStringMatchCode = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamCode.matchesExactly()).thenReturn(iStringMatchCode);
        ICriterion iCriterionCode = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchCode.values(anyString())).thenReturn(iCriterionCode);
        PowerMockito.when(iQuery.where(iCriterionCode)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockDeviceUtil.givenADeviceBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairMap);
        Assert.assertThat(1, is(lstDevice.size()));
    }
}