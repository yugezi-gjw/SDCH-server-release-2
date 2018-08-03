package com.varian.oiscn.appointment.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bhp9696 on 2017/11/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDataVO implements Serializable {
    private String patientSer;
    private String activityType;
    private String deviceId;
    private String activityCode;
    private String instanceId;
    private List<AppointmentDataTimeSlotVO> appointTimeList;
}
