package com.varian.oiscn.core.appointment;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gbt1220 on 3/24/2017.
 */
public class AppointmentStatusEnumTest {
    @Test
    public void givenAppointmentStatusEnumWhenGetDisplayThenReturnStatusString() {
        Assert.assertEquals("proposed", AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.PROPOSED));
        Assert.assertEquals("booked", AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
        Assert.assertEquals("arrived", AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.ARRIVED));
        Assert.assertEquals("fulfilled", AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED));
        Assert.assertEquals("cancelled", AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.CANCELLED));
        Assert.assertEquals("noshow", AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.NOSHOW));
        Assert.assertEquals("entered-in-error", AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.ENTERED_IN_ERROR));
    }

    @Test
    public void givenAppointmentStatusWhenFromCodeThenReturnEnumCode() {
        Assert.assertEquals(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.PROPOSED), "proposed");
        Assert.assertEquals(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), "booked");
        Assert.assertEquals(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.ARRIVED), "arrived");
        Assert.assertEquals(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED), "fulfilled");
        Assert.assertEquals(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.CANCELLED), "cancelled");
        Assert.assertEquals(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.ENTERED_IN_ERROR), "entered-in-error");
    }
}
