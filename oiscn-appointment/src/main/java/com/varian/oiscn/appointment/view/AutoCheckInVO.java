package com.varian.oiscn.appointment.view;

import com.varian.oiscn.core.appointment.calling.DeviceGuide;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutoCheckInVO {
    String hisId;
    String patientName;
    String scheduleTask;
    String scheduleTime;
    String deviceId;//返回的参数是设备的Mac地址，不是Aria的设备Id
    String deviceRoom;
    String status;
    String result;
    DeviceGuide guide;
}
