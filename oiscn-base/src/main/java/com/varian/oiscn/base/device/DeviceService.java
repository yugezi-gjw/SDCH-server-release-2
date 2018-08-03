package com.varian.oiscn.base.device;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.appointment.AppointmentDto;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by gbt1220 on 3/8/2017.
 */
public class DeviceService {
    private String deviceId;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    public DeviceService(String deviceId) {
        this.deviceId = deviceId;
        this.appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
    }

    public String getTheLatestEndTimeOfOneDayWithDeterminativeTime(String determinativeTime, Date oneDay) {
        List<AppointmentDto> appointmentDtoList = appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDate(deviceId, oneDay);
        if (appointmentDtoList.isEmpty()) {
            return StringUtils.isNotEmpty(determinativeTime) ? determinativeTime : null;
        }
        String theFirstAppointmentEndTime = DateUtil.formatDate(appointmentDtoList.get(0).getEndTime(), DateUtil.HOUR_MINUTE_TIME_FORMAT);
        String theLatestEndTime = StringUtils.isNotEmpty(determinativeTime) ? determinativeTime : theFirstAppointmentEndTime;
        String eachAppointmentEndTime;
        for (AppointmentDto dto : appointmentDtoList) {
            eachAppointmentEndTime = DateUtil.formatDate(dto.getEndTime(), DateUtil.HOUR_MINUTE_TIME_FORMAT);
            if (eachAppointmentEndTime.compareTo(theLatestEndTime) > 0) {
                theLatestEndTime = eachAppointmentEndTime;
            }
        }
        return theLatestEndTime;
    }
}
