package com.varian.oiscn.security;

import com.varian.oiscn.security.util.EncryptionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EncryptionUtil.class})
@PowerMockIgnore({"javax.crypto.*"})
public class MainTest {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(EncryptionUtil.class);
        String clearText = PowerMockito.mock(String.class);
        String encryptedText = PowerMockito.mock(String.class);
        PowerMockito.when(EncryptionUtil.encrypt(Mockito.anyString())).thenReturn(clearText);
        PowerMockito.when(EncryptionUtil.decrypt(Mockito.anyString())).thenReturn(encryptedText);
    }

    @Test
    public void testMainNull() {
        String[] args = {};
        try {
            Main.main(args);
            // default
            Assert.assertNotNull(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMainModeSimpleEnc() {
        String[] args = {"-operation", "enc", "-mode", "simple", "-text", "abc"};
        try {
            Main.main(args);
            // default
            Assert.assertTrue(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testMainModeSimpleDec() {
        String[] args = {"-operation", "dec", "-mode", "simple", "-text", "abc"};
        try {
            Main.main(args);
            // default
            Assert.assertFalse(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testMainModeNormaEnc() {
        String[] args = {"-operation", "enc", "-mode", "normal", "-text", "abc"};
        try {
            Main.main(args);
            // default
            Assert.assertTrue(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMainModeNormalDec() {
        String[] args = {"-operation", "dec", "-mode", "n", "-text", "abc"};
        try {
            Main.main(args);
            // default
            Assert.assertFalse(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMainErrorArgs() {
        String[] args1 = {"operation", "enc", "mode", "simple", "-text", "abc"};
        try {
            Main.main(args1);
            // default
            Assert.assertNotNull(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        String[] args2 = {"-operation", "enc", "mode", "simple", "-text", "abc"};
        try {
            Main.main(args2);
            // default
            Assert.assertNotNull(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        String[] args3 = {"-operation", "", "-mode", "", "-text", ""};
        try {
            Main.main(args3);
            // default
            Assert.assertNotNull(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        String[] args4 = {"-operation", "enc", "-mode", "", "-text", ""};
        try {
            Main.main(args4);
            // default
            Assert.assertNotNull(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        String[] args5 = {"-operation", "enc", "-mode", "sim", "-text", ""};
        try {
            Main.main(args5);
            // default
            Assert.assertNotNull(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        String[] args6 = {"-operation", "enc", "-mode", "sim", "-text",
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789001234567890123456789001234567890"};
        try {
            Main.main(args6);
            // default
            Assert.assertNotNull(Main.isEncryption);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
