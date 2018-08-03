package com.varian.oiscn.base.practitioner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirstActivityVO {
    boolean redirectedToFirstActivity;
    String patientSer;
    String activityId;
    String activityInstanceId;
    String activityType;
    String activityCode;
}
