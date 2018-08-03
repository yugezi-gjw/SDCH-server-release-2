package com.varian.oiscn.base.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.device.DeviceDtoWrap;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

/**
 * Created by BHP9696 on 2017/8/16.
 */
@Slf4j
public class DevicesReader {
    private static Object lock = new Object();
    private static List<DeviceDto> deviceDtoList;

    private DevicesReader() {

    }

    public static DeviceDto getDeviceTimeConfigureByCode(String code) {
        if (deviceDtoList == null || deviceDtoList.isEmpty()) {
            synchronized (lock) {
                init();
            }
        }
        for (DeviceDto deviceDto : deviceDtoList) {
            if (code.equals(deviceDto.getId())) {
                return deviceDto;
            }
        }
        log.warn("Can't find device from Devices.yaml by code [{}]", code);
        return new DeviceDto();
    }

    public static List<DeviceDto> getDeviceByUsage(String activityCode) {
        // FIXME: need initialization forcedly.
        if(deviceDtoList == null){
            init();
        }
        List<DeviceDto> list = new ArrayList<>();
        if (deviceDtoList != null && activityCode != null) {
            for (DeviceDto dto: deviceDtoList) {
                if (activityCode.equals(dto.getUsage())) {
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
    /**
     * 返回所有的设备信息
     * @return
     */
    public static List<DeviceDto> getAllDeviceDto(){
        if(deviceDtoList == null){
            init();
        }
        return deviceDtoList;
    }

    public static void init() {
        if (deviceDtoList == null || deviceDtoList.isEmpty()) {
            try {
                DeviceDtoWrap devicesWrap = new Yaml().loadAs(Files.newInputStream(Paths.get("config", "Devices.yaml")), DeviceDtoWrap.class);
                if (!devicesWrap.getDeviceList().isEmpty()) {
                    deviceDtoList = devicesWrap.getDeviceList();
                } else {
                    deviceDtoList = new ArrayList<>();
                }
            } catch (IOException e) {
                log.error("IOException {}", e.getMessage());
            }
        }
    }
}
