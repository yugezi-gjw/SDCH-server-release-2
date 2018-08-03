package com.varian.oiscn.base.coverage;

import org.junit.Assert;
import org.junit.Test;

public class PayorInfoPoolTest {

    @Test
    public void putAndGet() throws Exception {
        PayorInfoPool.put("key01", "value01");
        Assert.assertNotNull(PayorInfoPool.get("value01"));
        Assert.assertNull(PayorInfoPool.get("value11"));
    }

    @Test
    public void putAndgetValue() throws Exception {
        PayorInfoPool.put("key02", "value02");
        Assert.assertNotNull(PayorInfoPool.getValue("key02"));
        Assert.assertNull(PayorInfoPool.getValue("key12"));
    }

    @Test
    public void getCachedPayorInfo() throws Exception {
        Assert.assertNotNull(PayorInfoPool.getCachedPayorInfo());
    }
}