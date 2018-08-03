package com.varian.oiscn.core.appointment;

/**
 * Created by fmk9441 on 2017-06-20.
 */
public enum AppointmentRankEnum {
    START_TIME;

    public static String getDisplay(AppointmentRankEnum appointmentRankEnum) {
        if (START_TIME.equals(appointmentRankEnum)) {
            return "StartTime";
        } else {
            return "entered-in-error";
        }
    }
}
