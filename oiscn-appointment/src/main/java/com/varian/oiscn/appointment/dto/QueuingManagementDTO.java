package com.varian.oiscn.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/20.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueuingManagementDTO implements Serializable{
    private String id;
    private String appointmentId;
    private String activityCode;
    private String hisId;
    private String encounterId;
    private Long patientSer;
    private String deviceId;
    private CheckInStatusEnum checkInStatus;
    private Integer checkInIdx;
    private String startTime;
    private Date checkInTime;

//  查询条件
    private Date startTimeStart;
    private Date startTimeEnd;

    private Date checkInStartTime;
    private Date checkInEndTime;
    private List<CheckInStatusEnum> checkInStatusList;
    private List<String> appointmentIdList;
}
