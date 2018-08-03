package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Device;
import com.varian.oiscn.core.device.DeviceDto;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fmk9441 on 2017-03-24.
 */
public final class MockDeviceUtil {
    private MockDeviceUtil() {
    }

    public static Device givenADevice() {
        Device device = new Device();

        device.setId("DeviceId");
        device.addIdentifier().setSystem("Id").setValue("IdValue");
        device.addIdentifier().setSystem("Name").setValue("NameValue");
        device.setType(new CodeableConcept().setText("RadiationDevice"));
        device.setModel("Model");
        device.setSchedulable(new BooleanType(true));

        return device;
    }

    public static DeviceDto givenADeviceDto() {
        DeviceDto deviceDto = new DeviceDto();

        deviceDto.setCode("Code");
        deviceDto.setName("Name");
        deviceDto.setType("Type");
        deviceDto.setModel("Model");
        deviceDto.setStatus("available");
        deviceDto.setSchedulable(true);

        return deviceDto;
    }

    public static Bundle givenADeviceBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenADevice());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponents);

        return bundle;
    }

    public static List<Device> givenADeviceList() {
        return Arrays.asList(givenADevice());
    }

    public static List<DeviceDto> givenADeviceDtoList() {
        return Arrays.asList(givenADeviceDto());
    }
}
