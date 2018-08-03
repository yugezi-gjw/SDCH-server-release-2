package com.varian.oiscn.cache;

import java.util.HashMap;
import java.util.Map;

import com.varian.oiscn.core.device.DeviceDto;

/**
 * Device Cache by device id/code of Aria.<br>
 *
 */
public class DeviceCache {
    protected static CacheInterface<String, DeviceDto> cache = CacheFactory.getCache(CacheFactory.DEVICE);

    /** Id map to Code */
    protected static Map<String, String> id2CodeMap = new HashMap<>();
    
    public static void put(DeviceDto dto){
    	if (dto != null) {
	    	id2CodeMap.put(dto.getId(), dto.getCode());
	        cache.put(dto.getCode(), dto);
    	}
    }

    public static DeviceDto getByAriaId(String deviceId){
    	String deviceCode = id2CodeMap.get(deviceId);
    	if (deviceCode != null) {
    		return cache.get(deviceCode);
    	}
        return null;
    }
    
    public static DeviceDto getByAriaCode(String deviceCode){
        return cache.get(deviceCode);
    }
}
