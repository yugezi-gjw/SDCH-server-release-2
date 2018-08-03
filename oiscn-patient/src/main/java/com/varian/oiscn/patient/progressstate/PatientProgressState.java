package com.varian.oiscn.patient.progressstate;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by gbt1220 on 3/9/2017.
 */
@Data
@AllArgsConstructor
public class PatientProgressState {
    private ProgressStateEnum currentProgress;
    private NextActionEnum nextAction;
}
