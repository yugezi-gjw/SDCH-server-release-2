package com.varian.oiscn.base.cache;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class CacheListenerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRun() {
		CacheManager<String, String> cacheManager = PowerMockito.mock(CacheManager.class);
		PowerMockito.when(cacheManager.get("abc")).thenReturn("vv");
		CacheCallBackInterface mockCacheCallBack = PowerMockito.mock(MockCacheCallBack.class);
		CacheListener<String, String> cacheListerner = new CacheListener("abc", 10, cacheManager, mockCacheCallBack);
		cacheListerner.run();
		Mockito.verify(mockCacheCallBack).beforeCacheObjectDestroyed("vv");
		Mockito.verify(cacheManager).remove("abc");
		Mockito.verify(mockCacheCallBack).afterCacheObjectDestroyed();
	}

	class MockCacheCallBack implements CacheCallBackInterface<String> {

		@Override
		public void afterCacheObjectDestroyed() {
		}

		@Override
		public void beforeCacheObjectDestroyed(String t) {
		}
		
	}
}
