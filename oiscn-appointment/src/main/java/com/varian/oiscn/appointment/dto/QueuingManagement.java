package com.varian.oiscn.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by BHP9696 on 2017/10/24.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueuingManagement implements Serializable{
    private String id;
    private String appointmentId;
    private String activityCode;
    private String hisId;
    private String encounterId;
    private Long patientSer;
    private String deviceId;
    private CheckInStatusEnum checkInStatus;
    private Integer checkInIdx;
    private Date startTime;
    private Date checkInTime;
}
