package com.varian.oiscn.core.targetvolume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by bhp9696 on 2018/3/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanTargetVolume implements Serializable {
    private String hisId;
    private String encounterId;
    private Long patientSer;
    private String planId;
    private String targetVolumeName;
}
