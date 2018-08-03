package com.varian.oiscn.security.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by gbt1220 on 7/27/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SHACoder.class})
@PowerMockIgnore({"javax.crypto.*"})
public class SHACoderTest {

    @Test
    public void givenStringWhenEncodeSHA256ThenReturnThenEncodeString() {
        String testString = "testString";
        try {
            Assert.assertNotNull(SHACoder.encodeSHA256(testString.getBytes()));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void givenStringWhenMacSHA256ThenReturnThenEncodeString() {
        String testString = "testString";
        try {
            Assert.assertNotNull(SHACoder.macSHA256(testString, "key"));
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
