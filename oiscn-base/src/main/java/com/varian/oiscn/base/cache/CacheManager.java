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
public class CacheManager<K, V> implements Cache<K, V> {

    private final Map<K, V> cache;

    private final String cacheName;

    private long expireTime;

    private CacheCallBackInterface<V> cacheCallBackInterface;

    public CacheManager(String cacheName) {
        this.cacheName = cacheName;
        cache = new ConcurrentHashMap<>();
    }

    public CacheManager(String cacheName, int initialSize) {
        this.cacheName = cacheName;
        cache = new ConcurrentHashMap<>(initialSize);
    }

    public CacheManager(String cacheName, int initialSize,
                        long expireTime, CacheCallBackInterface<V> cacheCallBackInterface) {
        this(cacheName, initialSize);
        this.expireTime = expireTime;
        this.cacheCallBackInterface = cacheCallBackInterface;
    }

    public String getName() {
        return cacheName;
    }

    public V get(K key) {
        return cache.get(key);
    }

    public boolean isContains(K key) {
        return cache.containsKey(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
        if(expireTime != -1 && !isContains(key)) {
            new CacheListener(key, expireTime, this, cacheCallBackInterface).start();
        }
    }

    public void invalidate(K key) {
        if(isContains(key)) {
            if(cacheCallBackInterface != null) {
                V v = this.get(key);
                cacheCallBackInterface.beforeCacheObjectDestroyed(v);
                cache.remove(key);
                cacheCallBackInterface.afterCacheObjectDestroyed();
            }
            else {
                cache.remove(key);
            }

        }
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        cache.clear();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

}
