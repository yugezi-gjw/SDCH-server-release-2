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
@NoArgsConstructor
@AllArgsConstructor
public class TargetVolume implements Serializable{
    private Long id;
    private String hisId;
    private String encounterId;
    private Long patientSer;
//    靶区名称
    private String name;
//    靶区备注
    private String memo;

    private List<TargetVolumeItem> targetVolumeItemList;
}
