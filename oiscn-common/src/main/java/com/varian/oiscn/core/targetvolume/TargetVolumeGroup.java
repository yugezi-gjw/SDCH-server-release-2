package com.varian.oiscn.core.targetvolume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by bhp9696 on 2018/2/26.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetVolumeGroup {
    private String hisId;
    private String encounterId;
    private List<TargetVolume> targetVolumeList;
}
