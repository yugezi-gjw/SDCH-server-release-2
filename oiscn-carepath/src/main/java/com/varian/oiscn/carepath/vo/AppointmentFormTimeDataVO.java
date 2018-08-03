package com.varian.oiscn.carepath.vo;

import com.varian.oiscn.base.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by BHP9696 on 2017/9/7.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AppointmentFormTimeDataVO implements Comparable<AppointmentFormTimeDataVO>, Serializable {
    private String appointmentId;
    private String startTime;
    private String endTime;
    private Integer action;


    @Override
    public int compareTo(AppointmentFormTimeDataVO o) {
        int r;
        try {
            Date startTime = DateUtil.parse(o.getStartTime());
            Date selfStartTime = DateUtil.parse(this.getStartTime());
            r = selfStartTime.compareTo(startTime);
        } catch (ParseException e) {
            r = 0;
            log.error("ParseException: {}", e.getMessage());
        }
        return r;
    }
}
