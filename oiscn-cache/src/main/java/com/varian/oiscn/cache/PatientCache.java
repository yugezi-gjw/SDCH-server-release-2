package com.varian.oiscn.cache;

import com.varian.oiscn.core.patient.PatientDto;

import java.util.List;

public class PatientCache {
    protected static CacheInterface<String, PatientDto> cache = CacheFactory.getCache(CacheFactory.PATIENT);

    public static void put(String key, PatientDto value){
        cache.put(key, value);
    }

    public static PatientDto get(String key){
        return cache.get(key);
    }

    public static List<String> allKeys(){
        return cache.keys();
    }

    public static void remove(String key){
        cache.remove(key);
    }

}
