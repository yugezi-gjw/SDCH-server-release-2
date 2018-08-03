package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Device;
import com.varian.oiscn.core.device.DeviceDto;
import org.hl7.fhir.dstu3.model.BooleanType;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by fmk9441 on 2017-02-23.
 */
public class DeviceAssembler {
    private DeviceAssembler() {

    }

    /**
     * Return Fhir Device By DTO.<br>
     *
     * @param deviceDto DTO
     * @return Fhir Device
     */
    public static Device getDevice(DeviceDto deviceDto) {
        Device device = new Device();
        device.setId(isNotBlank(deviceDto.getId()) ? deviceDto.getId() : null);
        device.setModel(isNotBlank(deviceDto.getModel()) ? deviceDto.getModel() : null);
        device.setSchedulable(new BooleanType(deviceDto.isSchedulable()));
        return device;
    }

    /**
     * Return DTO from Fhir Device.<br>
     * @param device Fhir Device
     * @return DTO
     */
    public static DeviceDto getDeviceDto(Device device) {
        DeviceDto deviceDto = new DeviceDto();
        if (device != null) {
            deviceDto.setId(device.getIdElement().getIdPart());
            if (device.hasIdentifier()) {
                device.getIdentifier().forEach(identifier -> {
                    if ("Id".equalsIgnoreCase(identifier.getSystem())) {
                        deviceDto.setCode(identifier.getValue());
                    } else if ("Name".equalsIgnoreCase(identifier.getSystem())) {
                        deviceDto.setName(identifier.getValue());
                    }
                });
            }

            if (device.hasType()) {
                deviceDto.setType(device.getType().getText());
            }
            if (device.hasModel()) {
                deviceDto.setModel(device.getModel());
            }
            if (device.hasStatus()) {
                deviceDto.setStatus(device.getStatus().toCode());
            }
            deviceDto.setSchedulable(device.getSchedulable().booleanValue());
        }
        return deviceDto;
    }
}