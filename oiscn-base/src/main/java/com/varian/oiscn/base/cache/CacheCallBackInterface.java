package com.varian.oiscn.base.cache;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 1/5/2018
 * @Modified By:
 */
public interface CacheCallBackInterface<T> {

    /**
     * Call back interface before cache object destroyed
     * @param t
     */
    public void beforeCacheObjectDestroyed(T t);

    /**
     * Call back interface after cache object destroyed
     */
    public void afterCacheObjectDestroyed();

}
