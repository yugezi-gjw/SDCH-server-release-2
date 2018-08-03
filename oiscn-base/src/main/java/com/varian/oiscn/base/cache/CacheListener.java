package com.varian.oiscn.base.cache;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 1/5/2018
 * @Modified By:
 */
@Slf4j
public class CacheListener<K, V> extends Thread {

    private K cacheKey;

    private long expireTime;

    private CacheManager<K, V> cacheManager;

    private CacheCallBackInterface<V> cacheCallBack;

    public CacheListener(K cacheKey, long expireTime,
                         CacheManager<K, V> cacheManager, CacheCallBackInterface<V> cacheCallBack) {
        this.cacheKey = cacheKey;
        this.expireTime = expireTime;
        this.cacheManager = cacheManager;
        this.cacheCallBack = cacheCallBack;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(expireTime);
            V v = cacheManager.get(cacheKey);
            if(cacheCallBack != null) {
                cacheCallBack.beforeCacheObjectDestroyed(v);
                cacheManager.remove(cacheKey);
                cacheCallBack.afterCacheObjectDestroyed();
            }
            else {
                cacheManager.remove(cacheKey);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
