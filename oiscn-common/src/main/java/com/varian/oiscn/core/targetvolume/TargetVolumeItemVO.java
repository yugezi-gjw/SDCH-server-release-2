package com.varian.oiscn.core.targetvolume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by bhp9696 on 2018/2/26.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetVolumeItemVO implements Serializable{
//  外键，表TargetVolume表的主键
    private Long targetVolumeGroupId;
//   字段Id
    private String fieldId;
//    字段值
    private String fieldValue;
//    所属行号
    private Integer rNum;
//    所属行中的序号
    private Integer seq;
}
