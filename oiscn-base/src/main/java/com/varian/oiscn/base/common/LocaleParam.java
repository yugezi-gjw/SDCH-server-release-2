package com.varian.oiscn.base.common;

import com.varian.oiscn.config.LocaleConfiguration;

import java.util.Locale;

/**
 * Created by gbt1220 on 5/23/2017.
 * Modified by bhp9696 on 5/7/2017.
 */
public class LocaleParam {
    private static String LANGUAGE = "zh";
    private static String COUNTRY = "CN";

    private LocaleParam(){

    }

    public static void initParam(LocaleConfiguration configuration) {
        LANGUAGE = configuration.getLanguage();
        COUNTRY = configuration.getCountry();
        Locale.setDefault(new Locale(LANGUAGE, COUNTRY));
    }

    public static String getLANGUAGE() {
        return LANGUAGE;
    }

    public static String getCOUNTRY() {
        return COUNTRY;
    }
}
