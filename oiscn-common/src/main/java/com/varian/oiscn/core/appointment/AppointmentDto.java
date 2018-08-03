package com.varian.oiscn.core.appointment;

import com.varian.oiscn.core.participant.ParticipantDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-02-13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto implements Comparable<AppointmentDto>{
    private String appointmentId;
    private String orderId;
    private Date startTime;
    private Date endTime;

    /**
     * Reason this appointment is scheduled
     * Encounter reason codes, more clinical than administrative
     */
    private String reason;

    private String status;
    private String comment;
    private Date createdDT;
    private Date lastModifiedDT;
    private List<ParticipantDto> participants;

    public int compareTo(AppointmentDto a){
        return startTime.compareTo(a.getStartTime());
    }
}
