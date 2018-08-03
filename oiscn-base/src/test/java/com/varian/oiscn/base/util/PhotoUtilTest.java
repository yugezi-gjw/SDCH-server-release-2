/**
 *
 */
package com.varian.oiscn.base.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PhotoUtil.class)
public class PhotoUtilTest {

    /**
     * Test method for {@link com.varian.oiscn.base.util.PhotoUtil#encode(byte[])}.
     */
    @Test
    public void testBase64NormalCoding() {
        String photo = "AEBnhPM39SrGe7KSW+2YiCKCOU7dW3r9shSMOv9bdPM=";
        byte[] photoBytes = PhotoUtil.decode(photo);
        String actual = PhotoUtil.encode(photoBytes);
        Assert.assertEquals(photo, actual);
    }

    /**
     * Test method for {@link com.varian.oiscn.base.util.PhotoUtil#encode(byte[])}.
     */
    @Test
    public void testBase64NullCoding() {
        byte[] photoBytes = PhotoUtil.decode(null);
        String actual = PhotoUtil.encode(photoBytes);
        Assert.assertEquals(StringUtils.EMPTY, actual);
    }

    /**
     * Test method for {@link com.varian.oiscn.base.util.PhotoUtil#encode(byte[])}.
     */
    @Test
    public void testBase64EmptyCoding() {
        byte[] photoBytes = PhotoUtil.decode(StringUtils.EMPTY);
        String actual = PhotoUtil.encode(photoBytes);
        Assert.assertEquals(StringUtils.EMPTY, actual);
    }
    
    @Test
    public void testGenerateUID() {
        Assert.assertNotNull(PhotoUtil.generateUID("deviceId"));
        Assert.assertTrue(PhotoUtil.generateUID("deviceId").startsWith("deviceId"));
    }
}
