package com.varian.oiscn.core.device;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by fmk9441 on 2017-02-14.
 */
@Data
@NoArgsConstructor
public class DeviceDto {
    private String id;
    private String code;
    private String name;
    private String type;
    private String model;
    private String status;
    private boolean schedulable = false;
    private String interval;
//  该设备一天内可以预约的总数
    private int capacity;
    private List<DeviceTimeDto> timeSlotList;
    private String color;
//  设备用途 制模设备 CT设备 治疗设备
    private DeviceJobTypeEnum jobType;
    // Usage: activityCode
    private String usage;
}
