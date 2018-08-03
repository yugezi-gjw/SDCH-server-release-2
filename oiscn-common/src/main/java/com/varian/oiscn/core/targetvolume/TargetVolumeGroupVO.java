package com.varian.oiscn.core.targetvolume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bhp9696 on 2018/2/26.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetVolumeGroupVO implements Serializable{
    private String hisId;
    private String encounterId;
    private Long patientSer;
    private List<TargetVolumeVO> targetVolumeList;

}
