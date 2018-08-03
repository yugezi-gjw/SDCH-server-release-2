package com.varian.oiscn.appointment.vo;

import com.varian.oiscn.appointment.dto.CheckInStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by BHP9696 on 2017/10/25.
 */
@Data
@NoArgsConstructor
public class QueuingManagementVO implements Serializable{
    private String id;
    private String appointmentId;
    private String activityCode;
    private String hisId;
    private Long patientSer;
    private CheckInStatusEnum checkInStatus;
    private Integer checkInIdx;
    private Date startTime;
    private Date checkInTime;
    private String deviceId;
}
