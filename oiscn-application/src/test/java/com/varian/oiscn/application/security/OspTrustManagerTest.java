package com.varian.oiscn.application.security;

import org.junit.Assert;
import org.junit.Test;

import java.security.cert.X509Certificate;

public class OspTrustManagerTest {
    @Test
    public void testCheckClientTrusted() {
        try {
            OspTrustManager manager = new OspTrustManager();
            manager.checkClientTrusted(null, null);
            Assert.assertNotNull(manager);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCheckServerTrusted() {
        try {
            OspTrustManager manager = new OspTrustManager();
            manager.checkServerTrusted(null, null);
            Assert.assertNotNull(manager);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAcceptedIssuers() {
        try {
            OspTrustManager manager = new OspTrustManager();
            X509Certificate[] actual = manager.getAcceptedIssuers();
            Assert.assertNull(actual);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}