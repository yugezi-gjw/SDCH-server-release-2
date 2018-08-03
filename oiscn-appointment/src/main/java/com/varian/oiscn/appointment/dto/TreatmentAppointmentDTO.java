package com.varian.oiscn.appointment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Treatment Appointment DTO.<br>
 */
@Data
@NoArgsConstructor
public class TreatmentAppointmentDTO {
	private String id;
	private String uid;
	private String appointmentId; // from Aria
	private String hisId; // from HIS
	private Long  patientSer; //the primary key for Patient in Aria
	private String encounterId;
	private String deviceId;
	private Date startTime;
	private Date endTime;
	private String activityCode;
	/** Appointment Status */
	private String status;
}
