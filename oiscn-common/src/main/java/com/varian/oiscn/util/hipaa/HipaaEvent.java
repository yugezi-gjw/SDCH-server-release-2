package com.varian.oiscn.util.hipaa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum HipaaEvent {
    FailedLogin,
    LogOut,
    AuthorizedLogin,
    AuthorizedLoginOutsideAccessTime,
    View,
    Print,
    Export,
    Other;

    @JsonCreator
    public static HipaaEvent forValue(String value) {
        try{
            return valueOf(value);
        }catch (IllegalArgumentException e){
            log.error("Wrong HippaEvent argument: " + value);
            return HipaaEvent.Other;
        }
    }

    @JsonValue
    public String toValue() {
        return name(); // or fail
    }
}
