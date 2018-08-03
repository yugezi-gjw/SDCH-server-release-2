package com.varian.oiscn.base.common;

import com.varian.oiscn.config.LocaleConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by gbt1220 on 5/23/2017.
 */
public class LocaleParamTest {
    private String language;

    private String country;

    private LocaleConfiguration configuration = new LocaleConfiguration();

    @Before
    public void setup() {
        language = "zh";
        country = "CN";
        configuration.setLanguage(language);
        configuration.setCountry(country);
        LocaleParam.initParam(configuration);
    }

    @Test
    public void givenConfigurationWhenInitThenInitSuccess() {
        Assert.assertEquals(language, LocaleParam.getLANGUAGE());
        Assert.assertEquals(country, LocaleParam.getCOUNTRY());
    }
}
