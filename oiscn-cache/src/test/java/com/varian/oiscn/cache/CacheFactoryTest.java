/**
 * 
 */
package com.varian.oiscn.cache;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CacheFactoryTest {

	@Test
	public void testGetCache() {
		String[] validCacheList = {CacheFactory.APPOINTMENT, CacheFactory.PATIENT, CacheFactory.DEVICE};
		String[] invalidCacheList = {null, "", "  ", "xxx", "123556"};
		for(String name: validCacheList) {
			Assert.assertNotNull(CacheFactory.getCache(name));
		}
		for(String name: invalidCacheList) {
			Assert.assertNull(CacheFactory.getCache(name));
		}
	}
}
