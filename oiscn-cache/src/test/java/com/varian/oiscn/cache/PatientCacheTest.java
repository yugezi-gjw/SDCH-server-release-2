package com.varian.oiscn.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.varian.oiscn.core.patient.PatientDto;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class PatientCacheTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPutGet() {
		PatientDto dto = new PatientDto();
		PatientCache.put("key1", dto);
		PatientCache.put("key2", dto);
		Assert.assertNotNull(PatientCache.get("key1"));
		Assert.assertNotNull(PatientCache.get("key2"));
		PatientCache.remove("key1");
		PatientCache.remove("key2");
		Assert.assertEquals(0, PatientCache.allKeys().size());
	}
}
