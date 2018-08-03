package com.varian.oiscn.base.device;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.appointment.AppointmentDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gbt1220 on 3/8/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DeviceService.class)
public class DeviceServiceTest {

    private String deviceId;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    private DeviceService deviceService;

    private String determinativeTime;

    private Date oneDay;

    @Before
    public void setup() throws Exception {
        deviceId = "newDeviceId";
        oneDay = new Date();
        appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withNoArguments().thenReturn(appointmentAntiCorruptionServiceImp);
        deviceService = new DeviceService(deviceId);
    }

    @Test
    public void givenAnEmptyAppointmentListWhenTheDeterminativeTimeIsEmptyThenReturnNull() {
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        determinativeTime = "";
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDate(deviceId, oneDay)).thenReturn(appointmentDtos);
        Assert.assertNull(deviceService.getTheLatestEndTimeOfOneDayWithDeterminativeTime(determinativeTime, oneDay));
    }

    @Test
    public void givenAnEmptyAppointmentListWhenTheDeterminativeTimeIsNotEmptyThenReturnTheDeterminativeTime() {
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        determinativeTime = "09:00";
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDate(deviceId, oneDay)).thenReturn(appointmentDtos);
        Assert.assertEquals(determinativeTime, deviceService.getTheLatestEndTimeOfOneDayWithDeterminativeTime(determinativeTime, oneDay));
    }

    @Test
    public void givenAnAppointmentListWhenTheDeterminativeTimeIsNotEmptyThenReturnTheLatestTime() throws ParseException {
        List<AppointmentDto> appointmentDtos = givenAnAppointmentList();
        determinativeTime = "9:00";
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDate(deviceId, oneDay)).thenReturn(appointmentDtos);
        Assert.assertEquals(determinativeTime, deviceService.getTheLatestEndTimeOfOneDayWithDeterminativeTime(determinativeTime, oneDay));
    }

    @Test
    public void givenAnAppointmentListWhenTheDeterminativeTimeIsEmptyThenReturnTheLatestTimeInAppointmentList() throws ParseException {
        List<AppointmentDto> appointmentDtos = givenAnAppointmentList();
        determinativeTime = "";
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDate(deviceId, oneDay)).thenReturn(appointmentDtos);
        Assert.assertEquals(DateUtil.formatDate(appointmentDtos.get(appointmentDtos.size() - 1).getEndTime(), DateUtil.HOUR_MINUTE_TIME_FORMAT), deviceService.getTheLatestEndTimeOfOneDayWithDeterminativeTime(determinativeTime, oneDay));
    }

    private List<AppointmentDto> givenAnAppointmentList() throws ParseException {
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        appointmentDtos.add(new AppointmentDto("one", "orderId1",
                DateUtil.parse("2017-03-17 07:00"), DateUtil.parse("2017-03-17 07:15"),
                "", "", "", null, null, null));
        appointmentDtos.add(new AppointmentDto("two", "orderId2",
                DateUtil.parse("2017-03-17 08:00"), DateUtil.parse("2017-03-17 08:15"),
                "", "", "", null, null, null));
        return appointmentDtos;
    }
}
