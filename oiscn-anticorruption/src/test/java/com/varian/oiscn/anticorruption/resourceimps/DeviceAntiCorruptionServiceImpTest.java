package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Device;
import com.varian.oiscn.anticorruption.converter.EnumDeviceQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.datahelper.MockDeviceUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRDeviceInterface;
import com.varian.oiscn.core.device.DeviceDto;
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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.anyObject;

/**
 * Created by fmk9441 on 2017-02-23.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DeviceAntiCorruptionServiceImp.class, FHIRDeviceInterface.class})
public class DeviceAntiCorruptionServiceImpTest {
    private FHIRDeviceInterface fhirDeviceInterface;
    private DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirDeviceInterface = PowerMockito.mock(FHIRDeviceInterface.class);
        PowerMockito.whenNew(FHIRDeviceInterface.class).withNoArguments().thenReturn(fhirDeviceInterface);
        deviceAntiCorruptionServiceImp = new DeviceAntiCorruptionServiceImp();
    }

    @Test
    public void givenADeviceIDWhenQueryThenReturnDeviceDto() {
        final String deviceID = "DeviceID";
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = PowerMockito.mock(LinkedHashMap.class);
        deviceQueryImmutablePairMap.put(EnumDeviceQuery.ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, deviceID));
        List<Device> lstDevice = MockDeviceUtil.givenADeviceList();
        PowerMockito.when(fhirDeviceInterface.queryDeviceList(anyObject())).thenReturn(lstDevice);
        DeviceDto deviceDto = deviceAntiCorruptionServiceImp.queryDeviceByID(deviceID);
        Assert.assertThat(deviceDto, is(notNullValue()));
        Assert.assertEquals(deviceDto.getModel(), "Model");
    }

    @Test
    public void givenADeviceCodeWhenQueryThenReturnDeviceDto() {
        final String deviceCode = "DeviceCode";
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = PowerMockito.mock(LinkedHashMap.class);
        deviceQueryImmutablePairMap.put(EnumDeviceQuery.CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, deviceCode));
        List<Device> lstDevice = MockDeviceUtil.givenADeviceList();
        PowerMockito.when(fhirDeviceInterface.queryDeviceList(anyObject())).thenReturn(lstDevice);
        DeviceDto deviceDto = deviceAntiCorruptionServiceImp.queryDeviceByCode(deviceCode);
        Assert.assertThat(deviceDto, is(notNullValue()));
        Assert.assertEquals(deviceDto.getModel(), "Model");
    }

    @Test
    public void givenADeviceTypeWhenQueryThenReturnDeviceDtoList() {
        final String deviceType = "DeviceType";
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = PowerMockito.mock(LinkedHashMap.class);
        deviceQueryImmutablePairMap.put(EnumDeviceQuery.TYPE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, deviceType));
        List<Device> lstDevice = MockDeviceUtil.givenADeviceList();
        PowerMockito.when(fhirDeviceInterface.queryDeviceList(anyObject())).thenReturn(lstDevice);
        List<DeviceDto> lstDeviceDto = deviceAntiCorruptionServiceImp.queryDeviceByType(deviceType);
        Assert.assertThat(1, is(lstDeviceDto.size()));
    }
}