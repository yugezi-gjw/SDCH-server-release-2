package com.varian.oiscn.carepath.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by gbt1220 on 5/11/2017.
 */
@Data
public class AppointmentFormDataVO {
    private String id;
    private Long patientSer;
    private String activityType;
    private String deviceId;
    private List<AppointmentFormTimeDataVO> appointTimeList;
}
