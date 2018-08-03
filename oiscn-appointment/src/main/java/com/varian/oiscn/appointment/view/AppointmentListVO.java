package com.varian.oiscn.appointment.view;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by gbt1220 on 2/27/2017.
 */
@Data
@NoArgsConstructor
public class AppointmentListVO {
    private String appointmentId;
    private String orderId;
    private String patientName;
    private String ariaId;
    private String hisId;
    private Long patientSer;
    private String gender;
    private Date birthday;
    private String primaryPhysician;
    private String primaryPhysicianComments;
    private Boolean paid;
    private Boolean checkIn;
    private String scheduleTime;
    private String status;
    /**
     * Reason this appointment is scheduled
     * Encounter reason codes, more clinical than administrative
     */
    private String reason;
    private String reasonContent;
//  设备ID
    private String deviceId;
}
