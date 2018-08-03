package com.varian.oiscn.base.cache;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 1/5/2018
 * @Modified By:
 */
@Slf4j
public class CacheManagerUtils<K, V> {

    public static <K, V> void put(String cacheName, K key, V value) {
        CacheManager<K, V> cacheManager = CacheManagerPool.getInstance().getCacheManager(cacheName);
        if(cacheManager == null) {
            cacheManager = new CacheManager(cacheName);
            CacheManagerPool.getInstance().putToCachePool(cacheName, cacheManager);
        }
        cacheManager.put(key, value);
    }

    public static <K, V> V get(String cacheName, K key) {
        CacheManagerPool cacheManagerPool = CacheManagerPool.getInstance();
        CacheManager<K, V> cacheManager = cacheManagerPool.getCacheManager(cacheName);
        V v = null;
        if(cacheManagerPool.isContains(cacheName)) {
            v = cacheManager.get(key);
        }
        return v;
    }

    public static CacheManager get(String cacheName){
        CacheManagerPool cacheManagerPool = CacheManagerPool.getInstance();
        return cacheManagerPool.getCacheManager(cacheName);
    }

}
