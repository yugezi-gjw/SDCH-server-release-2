package com.varian.oiscn.core.targetvolume;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bhp9696 on 2018/3/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanTargetVolumeVO implements Serializable {
    private String hisId;
    @JsonIgnore
    private String encounterId;
    private String patientSer;
    private List<PlanTargetVolumeInfo> planTargetVolumeList;
}
