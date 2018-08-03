package com.varian.oiscn.appointment.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Patient Appointment VO.<br>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientAppointmentVO {
    private String appointmentId;
    private Date startTime;
    private Date endTime;
    private String status;
}
