package com.varian.oiscn.appointment.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by bhp9696 on 2018/1/5.
 * 护士预约homePage中对应的dashboard的设备卡片
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceScheduleViewVO implements Serializable{
//    设备id，在Aria中的主键
    private String deviceId;
//    设备Code
    private String code;
//    设备名称
    private String name;
    //  该设备一天内可以预约的总数
    private int capacity;
//  已预约数量
    private int occupied;
//    上午预约总数
    private int forenoonOccupied;
//    下午预约总数
    private int afternoonOccupied;
}
