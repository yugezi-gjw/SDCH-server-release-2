package com.varian.oiscn.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.varian.oiscn.core.device.DeviceDto;

public class DeviceCacheTest {

	@Before
	public void setUp() throws Exception {
		DeviceDto dto01 = new DeviceDto();
		dto01.setId("id01");
		dto01.setCode("code01");
		dto01.setName("name01");
		DeviceCache.put(dto01);
		
		DeviceDto dto02 = new DeviceDto();
		dto02.setId("id02");
		dto02.setCode("code02");
		dto02.setName("name02");
		DeviceCache.put(dto02);
	}

	@Test
	public void testGetByAriaId() {
		Assert.assertNotNull(DeviceCache.getByAriaId("id01"));
		Assert.assertNotNull(DeviceCache.getByAriaId("id02"));
		Assert.assertNull(DeviceCache.getByAriaId("id03"));
	}

	@Test
	public void testGetByAriaCode() {
		Assert.assertNotNull(DeviceCache.getByAriaCode("code01"));
		Assert.assertNotNull(DeviceCache.getByAriaCode("code02"));
		Assert.assertNull(DeviceCache.getByAriaCode("code03"));
	}

}
