package com.varian.oiscn.core.appointment;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gbt1220 on 3/24/2017.
 */
public enum AppointmentStatusEnum {
    PROPOSED,
    PENDING,
    BOOKED,
    ARRIVED,
    FULFILLED,
    CANCELLED,
    NOSHOW,
    ENTERED_IN_ERROR;

    public static String getDisplay(AppointmentStatusEnum appointmentStatusEnum) {
        switch (appointmentStatusEnum) {
            case PROPOSED:
                return "proposed";
            case PENDING:
                return "pending";
            case BOOKED:
                return "booked";
            case ARRIVED:
                return "arrived";
            case FULFILLED:
                return "fulfilled";
            case CANCELLED:
                return "cancelled";
            case NOSHOW:
                return "noshow";
            case ENTERED_IN_ERROR:
                return "entered-in-error";
            default:
                return "entered-in-error";
        }
    }

    public static AppointmentStatusEnum fromCode(String appointmentStatus) {
        if (StringUtils.equalsIgnoreCase(appointmentStatus, "proposed")) {
            return AppointmentStatusEnum.PROPOSED;
        } else if (StringUtils.equalsIgnoreCase(appointmentStatus, "pending")) {
            return AppointmentStatusEnum.PENDING;
        } else if (StringUtils.equalsIgnoreCase(appointmentStatus, "booked")) {
            return AppointmentStatusEnum.BOOKED;
        } else if (StringUtils.equalsIgnoreCase(appointmentStatus, "arrived")) {
            return AppointmentStatusEnum.ARRIVED;
        } else if (StringUtils.equalsIgnoreCase(appointmentStatus, "fulfilled")) {
            return AppointmentStatusEnum.FULFILLED;
        } else if (StringUtils.equalsIgnoreCase(appointmentStatus, "cancelled")) {
            return AppointmentStatusEnum.CANCELLED;
        } else if (StringUtils.equalsIgnoreCase(appointmentStatus, "noshow")) {
            return AppointmentStatusEnum.NOSHOW;
        } else {
            return AppointmentStatusEnum.ENTERED_IN_ERROR;
        }
    }
}
