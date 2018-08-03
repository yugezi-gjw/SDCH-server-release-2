package com.varian.oiscn.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by BHP9696 on 2017/9/14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({I18nReader.class, ResourceBundle.class})
public class I18nReaderTest {
    private ResourceBundle resourceBundle;

    @Before
    public void setup() {
        PowerMockito.mockStatic(ResourceBundle.class);
        resourceBundle = PowerMockito.mock(ResourceBundle.class);
        PowerMockito.when(ResourceBundle.getBundle("MessageResource", Locale.getDefault())).thenReturn(resourceBundle);
    }

    @Test
    public void givenKeyWhenGetLocaleValueByKeyThenReturnLocaleValue() {
        PowerMockito.when(resourceBundle.getString("PatientAssembler.family")).thenReturn("family");
        String r = I18nReader.getLocaleValueByKey("PatientAssembler.family");
        Assert.assertTrue("family".equals(r));
    }
}
