package com.varian.oiscn.util.hipaa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum HipaaObjectType {
    Patient,
    Plan,
    Diagnosis,
    PatientDemographics,
    Appointment,
    Activities,
    Administration,
    Other;

    @JsonCreator
    public static HipaaObjectType forValue(String value) {
        try{
            return valueOf(value);
        }catch (IllegalArgumentException e){
            log.error("Wrong HipaaObjectType argument: " + value);
            return HipaaObjectType.Other;
        }
    }

    @JsonValue
    public String toValue() {
        return name(); // or fail
    }
}
