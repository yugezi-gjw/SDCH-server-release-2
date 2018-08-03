package com.varian.oiscn.cache;

public class CacheFactory {
    public final static String APPOINTMENT = "Appointment";
    public final static String PATIENT = "Patient";
    public final static String DEVICE = "Device";
    private static CacheInterface appointmentCache = new ConcurrentHashMapCacheImpl();
    private static CacheInterface patientCache = new ConcurrentHashMapCacheImpl();
    private static CacheInterface deviceCache = new ConcurrentHashMapCacheImpl();

    public static CacheInterface getCache(String name){
    	if (name == null) {
    		return null;
    	}
        switch(name){
            case APPOINTMENT:
                return appointmentCache;
            case PATIENT:
                return patientCache;
            case DEVICE:
                return deviceCache;
            default:
                return null;
        }
    }
}
