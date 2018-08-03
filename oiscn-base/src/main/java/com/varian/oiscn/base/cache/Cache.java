package com.varian.oiscn.base.cache;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 1/5/2018
 * @Modified By:
 */
public interface Cache<K, V> {

    public String getName();

    public V get(K key);

    public void put(K key, V Value);

    public void invalidate(K key);

    public void remove(K key);

    public boolean isEmpty();

    public int size();

    public void clear();

    public boolean isContains(K key);

}
