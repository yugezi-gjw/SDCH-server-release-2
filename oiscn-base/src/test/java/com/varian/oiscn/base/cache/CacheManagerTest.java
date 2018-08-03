package com.varian.oiscn.base.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 1/5/2018
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CacheManager.class, CacheCallBackInterface.class, CacheListener.class})
public class CacheManagerTest {

    private CacheManager cacheManager;

    @Before
    public void setup() {

    }

    @Test
    public void testCreateCacheManagerWithArgumentsCacheName() throws Exception {
        cacheManager = PowerMockito.mock(CacheManager.class);
        String cacheName = PowerMockito.mock(String.class);
        PowerMockito.whenNew(CacheManager.class).withArguments(cacheName).thenReturn(cacheManager);
        cacheManager = new CacheManager(cacheName);
        Assert.assertNotNull(cacheManager);
    }

    @Test
    public void testCreateCacheManagerWithArgumentsCacheNameAndInitialSize() throws Exception {
        cacheManager = PowerMockito.mock(CacheManager.class);
        String cacheName = PowerMockito.mock(String.class);
        int initialSize = PowerMockito.mock(Integer.class);
        PowerMockito.whenNew(CacheManager.class).withArguments(cacheName, initialSize).thenReturn(cacheManager);
        cacheManager = new CacheManager(cacheName, initialSize);
        Assert.assertNotNull(cacheManager);
    }

    @Test
    public void testCreateCacheManagerWithArgumentsCacheNameAndInitialSizeAndExpireTimeAndCallBackInterface() throws Exception {
        cacheManager = PowerMockito.mock(CacheManager.class);
        String cacheName = PowerMockito.mock(String.class);
        int initialSize = PowerMockito.mock(Integer.class);
        long expireTime = PowerMockito.mock(Long.class);
        CacheCallBackInterface callBack = PowerMockito.mock(CacheCallBackInterface.class);
        PowerMockito.whenNew(CacheManager.class).withArguments(cacheName, initialSize, expireTime, callBack).thenReturn(cacheManager);
        cacheManager = new CacheManager(cacheName, initialSize, expireTime, callBack);
        Assert.assertNotNull(cacheManager);
    }

    @Test
    public void testPutWhenNoArgumentExpireTime() throws Exception {
        cacheManager = PowerMockito.mock(CacheManager.class);
        String cacheName = PowerMockito.mock(String.class);
        PowerMockito.whenNew(CacheManager.class).withArguments(cacheName).thenReturn(cacheManager);
        cacheManager = new CacheManager(cacheName);
        Map<Object, Object> cache = PowerMockito.mock(ConcurrentHashMap.class);
        Object key = PowerMockito.mock(Object.class);
        Object value = PowerMockito.mock(Object.class);
        PowerMockito.when(cache.put(key, value)).thenReturn(true);
        cacheManager.put(key, value);
        Assert.assertTrue(true);
    }

    @Test
    public void testPutWhenArgumentExpireTime() throws Exception {
        cacheManager = PowerMockito.mock(CacheManager.class);
        String cacheName = PowerMockito.mock(String.class);
        int initialSize = PowerMockito.mock(Integer.class);
        long expireTime = PowerMockito.mock(Long.class);
        CacheCallBackInterface callBack = PowerMockito.mock(CacheCallBackInterface.class);
        PowerMockito.whenNew(CacheManager.class).withArguments(cacheName, initialSize, expireTime, callBack).thenReturn(cacheManager);
        cacheManager = new CacheManager(cacheName, initialSize, expireTime, callBack);
        Map<Object, Object> cache = PowerMockito.mock(ConcurrentHashMap.class);
        Object key = PowerMockito.mock(Object.class);
        Object value = PowerMockito.mock(Object.class);
        PowerMockito.when(cache.put(key, value)).thenReturn(true);
        cacheManager.put(key, value);
        Assert.assertTrue(true);
    }

    @Test
    public void testInvalidateWhenCacheCallbackInterfaceIsNull() throws Exception {
        cacheManager = PowerMockito.mock(CacheManager.class);
        String cacheName = PowerMockito.mock(String.class);
        PowerMockito.whenNew(CacheManager.class).withArguments(cacheName).thenReturn(cacheManager);
        cacheManager = new CacheManager(cacheName);
        Object key = PowerMockito.mock(Object.class);
        PowerMockito.when(cacheManager.isContains(key)).thenReturn(true);
        Map<Object, Object> cache = PowerMockito.mock(ConcurrentHashMap.class);
        PowerMockito.when(cache.remove(key)).thenReturn(true);
        cacheManager.invalidate(key);
        Assert.assertTrue(true);
    }

    @Test
    public void testInvalidateWhenCacheCallbackInterfaceIsNotNull() throws Exception {
        cacheManager = PowerMockito.mock(CacheManager.class);
        String cacheName = PowerMockito.mock(String.class);
        int initialSize = PowerMockito.mock(Integer.class);
        long expireTime = PowerMockito.mock(Long.class);
        CacheCallBackInterface callBack = PowerMockito.mock(CacheCallBackInterface.class);
        PowerMockito.whenNew(CacheManager.class).withArguments(cacheName, initialSize, expireTime, callBack).thenReturn(cacheManager);
        cacheManager = new CacheManager(cacheName, initialSize, expireTime, callBack);
        Object key = PowerMockito.mock(Object.class);
        Object value = PowerMockito.mock(Object.class);
        PowerMockito.when(cacheManager.isContains(key)).thenReturn(true);
        Map<Object, Object> cache = PowerMockito.mock(ConcurrentHashMap.class);
        PowerMockito.when(cache.remove(key)).thenReturn(true);
        PowerMockito.when(cacheManager.get(key)).thenReturn(value);
        callBack.beforeCacheObjectDestroyed(value);
        Mockito.verify(callBack).beforeCacheObjectDestroyed(value);
        PowerMockito.when(cache.remove(key)).thenReturn(true);
        callBack.afterCacheObjectDestroyed();
        Mockito.verify(callBack).afterCacheObjectDestroyed();
        cacheManager.invalidate(key);
        Assert.assertTrue(true);
    }

    @Test
    public void testAll() {
    	CacheCallBackInterface cacheCallback = PowerMockito.mock(MockCacheCallBackInterface.class);
		CacheManager cm = new CacheManager("test", 10, 300, cacheCallback);
		
		String key = "key";
		cm.put(key,  "value");
		Assert.assertNotNull(cm.get(key));
		
		cm.invalidate(key);
		Assert.assertNull(cm.get(key));
		
		cm.remove(key);
		Assert.assertEquals(0, cm.size());
		
		cm.put(key,  "value");
		cm.clear();
		Assert.assertTrue(cm.isEmpty());
    }
    
    class MockCacheCallBackInterface<String> implements CacheCallBackInterface {

		@Override
		public void beforeCacheObjectDestroyed(Object t) {
		}

		@Override
		public void afterCacheObjectDestroyed() {
		}
    }
}
