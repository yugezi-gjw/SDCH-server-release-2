package com.varian.oiscn.config;

/**
 * Created by gbt1220 on 10/31/2017.
 */
public enum TokenAuthenticationMode {
    DISABLED,
    BEAR;

    public static TokenAuthenticationMode fromCode(Integer code) {
        switch (code) {
            case 1:
                return BEAR;
            default:
                return DISABLED;
        }
    }
}
