package com.varian.oiscn.security.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.crypto.NoSuchPaddingException;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*"})
@PrepareForTest({EncryptionUtil.class})
public class EncryptionUtilTest {

    @Before
    public void setup() {
    }

    @Test
    public void testHashMD5() {
        try {
            EncryptionUtil.init();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        String[] list = {"xxx", "sdfasdfa", "sdfasdfa", "sdfasdfaasdfadfasdfasdf", "123421@#$!@XVAsdf"};
        for (String text : list) {
            String hash = EncryptionUtil.hashMD5(text);
            Assert.assertNotNull(hash);
            Assert.assertTrue(hash.length() > 0);
        }

        String[] list2 = {null, "", "  ", "      "};
        for (String text : list2) {
            String hash = EncryptionUtil.hashMD5(text);
            Assert.assertNotNull(hash);
            Assert.assertTrue(hash.length() == 0);
        }
    }

    @Test
    public void testHashMD5Exception() {
        EncryptionUtil.ALGORITHM_MD5 = null;
        PowerMockito.mockStatic(EncryptionUtil.class);
        PowerMockito.spy(EncryptionUtil.class);
        NoSuchPaddingException npe = PowerMockito.mock(NoSuchPaddingException.class);
        try {
            PowerMockito.doThrow(npe).when(EncryptionUtil.class, "init");
            String actual = EncryptionUtil.hashMD5("abcd###");
            Assert.assertEquals("", actual);
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void testEncryptDecrypt() {
        try {
            EncryptionUtil.init();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        try {
            String[] list = {"xxx", " dfasdfa", "  dfasdfa", "  dfasdfa  ", "  dfa  sdfa  ", "sdfasdfa", "sdfasdfa",
                    "sdfasdfaasdfadfasdfasdf", "123421@#$!@XVAsdf"};
            for (String text : list) {
                String encrypt = EncryptionUtil.encrypt(text);
                Assert.assertNotNull(encrypt);
                Assert.assertTrue(encrypt.length() > 0);

                String decrypt = EncryptionUtil.decrypt(encrypt);
                Assert.assertNotNull(decrypt);
                Assert.assertTrue(decrypt.length() > 0);
                Assert.assertEquals(text, decrypt);
            }
            String empty = StringUtils.EMPTY;
            Assert.assertEquals(empty, EncryptionUtil.encrypt(empty));
            Assert.assertEquals(empty, EncryptionUtil.decrypt(empty));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testEncryptInit() {
        EncryptionUtil.CIPHER_ENCRYPTION = null;
        try {
            String actual = EncryptionUtil.encrypt("abcd###");
            Assert.assertNotNull(actual);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecryptInit() {
        EncryptionUtil.CIPHER_DECRYPTION = null;
        try {
            String actual = EncryptionUtil.decrypt("gPHdGcNdgpM00v3zJZ2Qug==");
            Assert.assertNotNull(actual);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
