package com.varian.oiscn.core.participant;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gbt1220 on 2/27/2017.
 */
public enum ParticipantTypeEnum {
    PATIENT,
    LOCATION,
    DEVICE,
    PRACTITIONER;

    public static String getDisplay(ParticipantTypeEnum type) {
        String display;
        switch (type) {
            case DEVICE:
                display = ParticipantTypeEnum.DEVICE.name();
                break;
            case LOCATION:
                display = ParticipantTypeEnum.LOCATION.name();
                break;
            case PRACTITIONER:
            display = ParticipantTypeEnum.PRACTITIONER.name();
            break;
            default:
                display = ParticipantTypeEnum.PATIENT.name();
        }
        return display;
    }

    public static ParticipantTypeEnum fromCode(String type) {
        if (StringUtils.equalsIgnoreCase(type, ParticipantTypeEnum.PATIENT.name())) {
            return PATIENT;
        } else if (StringUtils.equalsIgnoreCase(type, ParticipantTypeEnum.DEVICE.name())) {
            return DEVICE;
        } else if (StringUtils.equalsIgnoreCase(type, ParticipantTypeEnum.LOCATION.name())) {
            return LOCATION;
        }
        return PRACTITIONER;
    }
}