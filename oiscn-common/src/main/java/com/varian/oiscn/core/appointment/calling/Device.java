package com.varian.oiscn.core.appointment.calling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 10/25/2017
 * @Modified By:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private String deviceId;

    private String deviceName;

    private String ariaDeviceId;

    private String deviceRoom;

    private DeviceGuide deviceGuide;

}
