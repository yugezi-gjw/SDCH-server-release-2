package com.varian.oiscn.appointment.calling;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Calling Service VO for integration.<br>
 */
@Data
public class CallingServiceVO implements Serializable {
    /**
     * Default Serial Id
     */
    private static final long serialVersionUID = 1L;
    protected CallingGuide guide;
    /** Calling Device Id */
    private String deviceId;
    /** Calling Device Room Name */
    private String deviceRoom;
    /** Patient Name List */
    private List<String> patients;

    public CallingServiceVO() {
        patients = new LinkedList<>();
        guide = new CallingGuide();
    }

    public CallingServiceVO addPatient(String patientName) {
        patients.add(patientName);
        return this;
    }

    public CallingServiceVO addPatientList(List<String> patientNameList) {
        for (String patient : patientNameList) {
            addPatient(patient);
        }
        return this;
    }

    public CallingServiceVO addText(String text) {
        guide.addText(text);
        return this;
    }

    public CallingServiceVO addImage(String imageUrl) {
        guide.addImage(imageUrl);
        return this;
    }

    public CallingServiceVO addVideo(String videoUrl) {
        guide.addVideo(videoUrl);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("deviceRoom", deviceRoom);
        data.put("patients", patients);
        data.put("guide", guide);
        return data;
    }
}
