package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Device;
import com.varian.oiscn.anticorruption.assembler.DeviceAssembler;
import com.varian.oiscn.anticorruption.converter.EnumDeviceQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRDeviceInterface;
import com.varian.oiscn.cache.DeviceCache;
import com.varian.oiscn.core.device.DeviceDto;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-02-23.
 */
public class DeviceAntiCorruptionServiceImp {
    private FHIRDeviceInterface fhirDeviceInterface;

    /**
     * Default Constructor.<br>
     */
    public DeviceAntiCorruptionServiceImp() {
        fhirDeviceInterface = new FHIRDeviceInterface();
    }

    /**
     * Return Fhir Device by Id.<br>
     *
     * @param deviceID Device id
     * @return Fhir Device
     */
    public DeviceDto queryDeviceByID(String deviceID){
    	// fetch from Cache first.
        DeviceDto deviceDto = DeviceCache.getByAriaId(deviceID);
        if (deviceDto == null) {
	        LinkedHashMap<EnumDeviceQuery,ImmutablePair<EnumMatchQuery,Object>> deviceQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
	        deviceQueryImmutablePairLinkedHashMap.put(EnumDeviceQuery.ID,new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, deviceID));
	        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairLinkedHashMap);
	        if (!lstDevice.isEmpty()) {
	            deviceDto = DeviceAssembler.getDeviceDto(lstDevice.get(0));
	            DeviceCache.put(deviceDto);
	        }
        }
        return deviceDto;
    }

    /**
     * Return Device DTO by Device Code.<br>
     * @param deviceCode Device Code
     * @return Device DTO
     */
    public DeviceDto queryDeviceByCode(String deviceCode){
    	// fetch from Cache first.
        DeviceDto deviceDto = DeviceCache.getByAriaCode(deviceCode);
	    if (deviceDto == null) {
	        LinkedHashMap<EnumDeviceQuery,ImmutablePair<EnumMatchQuery,Object>> deviceQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
	        deviceQueryImmutablePairLinkedHashMap.put(EnumDeviceQuery.CODE,new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, deviceCode));
	        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairLinkedHashMap);
	        if (!lstDevice.isEmpty()) {
	            deviceDto = DeviceAssembler.getDeviceDto(lstDevice.get(0));
	            DeviceCache.put(deviceDto);
	        }
	    }
        return deviceDto;
    }

    /**
     * Return Device DTO by Device Type.<br>
     * @param deviceType Device Type
     * @return Device DTO List
     */
    public List<DeviceDto> queryDeviceByType(String deviceType){
        List<DeviceDto> lstDeviceDto = new ArrayList<>();
        Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap = new LinkedHashMap<>();
        deviceQueryImmutablePairMap.put(EnumDeviceQuery.TYPE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, deviceType));
        List<Device> lstDevice = fhirDeviceInterface.queryDeviceList(deviceQueryImmutablePairMap);
        if (!lstDevice.isEmpty()) {
            lstDevice.forEach(device -> lstDeviceDto.add(DeviceAssembler.getDeviceDto(device)));
        }

        return lstDeviceDto;
    }
}