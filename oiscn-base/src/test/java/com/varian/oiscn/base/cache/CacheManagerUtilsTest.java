package com.varian.oiscn.base.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CacheManager.class, CacheCallBackInterface.class, CacheListener.class})
public class CacheManagerUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPut() {
		String cacheName = "cacheName";
		
		CacheManagerUtils.put(cacheName, "key", "value");
		
		Assert.assertNotNull(CacheManagerUtils.get(cacheName));
		Assert.assertEquals("value", CacheManagerUtils.get(cacheName, "key"));
	}
}
