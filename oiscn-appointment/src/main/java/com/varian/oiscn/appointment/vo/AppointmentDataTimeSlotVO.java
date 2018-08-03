package com.varian.oiscn.appointment.vo;

import com.varian.oiscn.base.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by bhp9696 on 2017/11/6.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AppointmentDataTimeSlotVO implements Comparable<AppointmentDataTimeSlotVO>, Serializable{
    private String appointmentId;
    private String startTime;
    private String endTime;
    private Integer action; // 0: Add, 1: Remove, No update action.
    private String actName;
    private String conflictActName;
//    private String currActivityCodeName;
//    private String preActivityCodeName;


    @Override
    public int compareTo(AppointmentDataTimeSlotVO o) {
        int r;
         try {
            Date start = DateUtil.parse(o.getStartTime());
            Date sTime = DateUtil.parse(this.getStartTime());
            r = sTime.compareTo(start);
        } catch (ParseException e) {
             log.error("ParseException: {}", e.getMessage());
             r = 0;
        }

        return r;
    }

    public enum AppointmentTimeSlotActionEnum {
        ADD,DELETED,CANCEL
    }
}
