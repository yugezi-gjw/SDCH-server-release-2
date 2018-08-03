package com.varian.oiscn.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapCacheImpl<K, V> implements CacheInterface<K, V>{
    private ConcurrentHashMap<K, V> concurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public void put(K key, V value){
        concurrentHashMap.put(key, value);
    }

    @Override
    public V get(K key){
        return concurrentHashMap.get(key);
    }

    @Override
    public V remove(K key) {
        return concurrentHashMap.remove(key);
    }

    @Override
    public List<K> keys() {
        List<K> list = new ArrayList<K>();
        concurrentHashMap.entrySet().forEach(entry->{
            list.add(entry.getKey());
        });
        return list;
    }
}