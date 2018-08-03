package com.varian.oiscn.appointment.calling;

import lombok.Data;

import java.util.List;

/**
 * Call Patient VO for front-end.<br>
 */
@Data
public class CallPatientVO {
    /**
     * Patient Appointment Id
     */
    private String appointmentId;
    /**
     * Patient Name List
     */
    private List<String> patientName;
}
