package com.varian.oiscn.base.util;

import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.device.DeviceDtoWrap;
import com.varian.oiscn.core.device.DeviceTimeDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by BHP9696 on 2017/8/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DevicesReader.class, Files.class, Paths.class})
public class DevicesReaderTest {

    private InputStream fileInputStream;

    private Yaml yaml;

    private Path path;

    @Before
    public void setup() {
        fileInputStream = PowerMockito.mock(FileInputStream.class);
        path = PowerMockito.mock(Path.class);
        try {
            PowerMockito.mockStatic(Files.class);
            PowerMockito.mockStatic(Paths.class);
            PowerMockito.when(Paths.get("config", "Devices.yaml")).thenReturn(path);
            PowerMockito.when(Files.newInputStream(path)).thenReturn(fileInputStream);
            yaml = PowerMockito.mock(Yaml.class);
            PowerMockito.whenNew(Yaml.class).withNoArguments().thenReturn(yaml);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenDeviceIdWhenGetDeviceTimeListByIdThenReturnList() {
        DeviceDtoWrap deviceDtoWrap = new DeviceDtoWrap();
        String code = "23EX";
        deviceDtoWrap.setDeviceList(Arrays.asList(new DeviceDto() {{
            setId(code);
            setInterval("20");
            setTimeSlotList(Arrays.asList(new DeviceTimeDto("9:00", "12:00"),
                    new DeviceTimeDto("13:00", "18:00")));
        }}));
        PowerMockito.when(yaml.loadAs(fileInputStream, DeviceDtoWrap.class)).thenReturn(deviceDtoWrap);
        DeviceDto deviceDto = DevicesReader.getDeviceTimeConfigureByCode(code);
        Assert.assertNotNull(deviceDto.getTimeSlotList());
        Assert.assertNotNull(deviceDto.getInterval());
        Assert.assertTrue(deviceDto.getTimeSlotList().size() == 2);
    }

    @Test
    public void givenGetAllDeviceDtoThenReturnList(){
        DeviceDtoWrap deviceDtoWrap = new DeviceDtoWrap();
        String code = "23EX";
        deviceDtoWrap.setDeviceList(Arrays.asList(new DeviceDto() {{
            setId(code);
            setInterval("20");
            setCapacity(200);
            setTimeSlotList(Arrays.asList(new DeviceTimeDto("9:00", "12:00"),
                    new DeviceTimeDto("13:00", "18:00")));
        }}));
        PowerMockito.when(yaml.loadAs(fileInputStream, DeviceDtoWrap.class)).thenReturn(deviceDtoWrap);
        List<DeviceDto> list =  DevicesReader.getAllDeviceDto();
        Assert.assertNotNull(list);
        Assert.assertTrue(!list.isEmpty());
    }
}
