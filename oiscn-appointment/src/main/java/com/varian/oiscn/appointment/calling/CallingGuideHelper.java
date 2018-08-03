package com.varian.oiscn.appointment.calling;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.appointment.calling.CallingConfig;
import com.varian.oiscn.core.appointment.calling.Device;
import com.varian.oiscn.core.appointment.calling.DeviceGuide;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by gbt1220 on 12/6/2017.
 */
public class CallingGuideHelper {
    private CallingGuideHelper() {}

    /**
     * 根据Aria中的deviceId获取设备的Mac地址.<br>
     *
     * @param ariaDeviceId Aria Device Id
     * @return DeviceMacAddress
     */
    public static String getDeviceMacAddressByAriaDeviceId(Configuration configuration, String ariaDeviceId) {
        CallingConfig callingConfig = configuration.getCallingConfig();
        for (Device device : callingConfig.getCheckInGuide().getDevicesGuide().getDevices()) {
            if (device.getAriaDeviceId().equals(ariaDeviceId)) {
                return device.getDeviceId();
            }
        }
        return "";
    }

    /**
     * 根据设备的Mac地址获取该设备在Aria中的deviceId.<br>
     * @param deviceMacAddress mac address of device
     * @return device id of ARIA
     */
    public static String getAriaDeviceIdByDeviceMacAddress(Configuration configuration, String deviceMacAddress) {
        CallingConfig callingConfig = configuration.getCallingConfig();
        for (Device device : callingConfig.getCheckInGuide().getDevicesGuide().getDevices()) {
            if (device.getDeviceId().equals(deviceMacAddress)) {
                return device.getAriaDeviceId();
            }
        }
        return "";
    }

    /**
     * 根据Aria中的deviceId获取设备房间名称
     * @param configuration
     * @param ariaDeviceId
     * @return
     */
    public static String getDeviceRoomByAriaDeviceId(Configuration configuration, String ariaDeviceId) {
        CallingConfig callingConfig = configuration.getCallingConfig();
        for (Device device : callingConfig.getCheckInGuide().getDevicesGuide().getDevices()) {
            if (device.getAriaDeviceId().equals(ariaDeviceId)) {
                return device.getDeviceRoom();
            }
        }
        return "";
    }

    //根据设备Mac地址获取设备房间名称
    public static String getDeviceRoomByDeviceMacAddress(Configuration configuration, String deviceMacAddress) {
        CallingConfig callingConfig = configuration.getCallingConfig();
        for (Device device : callingConfig.getCheckInGuide().getDevicesGuide().getDevices()) {
            if (device.getDeviceId().equals(deviceMacAddress)) {
                return device.getDeviceRoom();
            }
        }
        return "";
    }

    public static DeviceGuide getDeviceGuideByAriaDeviceId(Configuration configuration, String deviceId) {
        CallingConfig callingConfig = configuration.getCallingConfig();
        for (Device device : callingConfig.getCheckInGuide().getDevicesGuide().getDevices()) {
            if (StringUtils.equals(device.getAriaDeviceId(), deviceId)) {
                return device.getDeviceGuide();
            }
        }
        return new DeviceGuide();
    }

    public static DeviceGuide getDeviceGuideByDeviceMacAddress(Configuration configuration, String deviceMacAddress) {
        CallingConfig callingConfig = configuration.getCallingConfig();
        for (Device device : callingConfig.getCheckInGuide().getDevicesGuide().getDevices()) {
            if (StringUtils.equals(device.getDeviceId(), deviceMacAddress)) {
                return device.getDeviceGuide();
            }
        }
        return new DeviceGuide();
    }
}
