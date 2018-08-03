package com.varian.oiscn.core.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by bhp9696 on 2018/1/9.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSettingView {
    private String code;
    private String interval;
    private List<DeviceTimeDto> timeSlotList;
}
