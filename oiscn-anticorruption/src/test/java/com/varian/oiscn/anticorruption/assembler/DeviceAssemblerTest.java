package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Device;
import com.varian.oiscn.anticorruption.datahelper.MockDeviceUtil;
import com.varian.oiscn.core.device.DeviceDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by fmk9441 on 2017-02-23.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DeviceAssembler.class})
public class DeviceAssemblerTest {
    @InjectMocks
    private DeviceAssembler deviceAssembler;

    @Test
    public void givenADeviceDtoWhenConvertThenReturnDevice() {
        DeviceDto deviceDto = MockDeviceUtil.givenADeviceDto();
        Device device = DeviceAssembler.getDevice(deviceDto);
        Assert.assertNotNull(device);
    }

    @Test
    public void givenADeviceWhenConvertThenReturnDeviceDto() {
        Device device = MockDeviceUtil.givenADevice();
        DeviceDto deviceDto = DeviceAssembler.getDeviceDto(device);
        Assert.assertNotNull(deviceDto);
    }
}