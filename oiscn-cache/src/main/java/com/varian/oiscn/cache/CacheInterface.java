/**
 * 
 */
package com.varian.oiscn.cache;

import java.util.List;

/**
 * Cache interface.<br>
 *
 */
public interface CacheInterface<K, V> {
    void put(K key, V value);
    V get(K key);
    V remove(K key);

    /**
     * 获取cache中所有的key
     * @return
     */
    List<K> keys();
}
