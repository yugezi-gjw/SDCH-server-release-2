package com.varian.oiscn.base.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 1/5/2018
 * @Modified By:
 */
@Slf4j
public class CacheManagerPool {

    private volatile static CacheManagerPool instance = null;

    private static Map<String, CacheManager> cachePool = null;

    private CacheManagerPool() {
        cachePool = new ConcurrentHashMap<>();
    }

    public static CacheManagerPool getInstance() {
        if(instance == null) {
            synchronized (CacheManagerPool.class) {
                if(instance == null) {
                    instance = new CacheManagerPool();
                }
            }
        }
        return instance;
    }

    public void putToCachePool(String cacheName, CacheManager cacheManager) {
        cachePool.put(cacheName, cacheManager);
    }

    public CacheManager getCacheManager(String cacheName) {
        if(isContains(cacheName)) {
            return cachePool.get(cacheName);
        }
        return null;
    }

    public void clear() {
        cachePool.clear();
    }

    public void remove(String cacheName) {
        cachePool.remove(cacheName);
    }

    public boolean isContains(String cacheName) {
        return cachePool.containsKey(cacheName);
    }

}
