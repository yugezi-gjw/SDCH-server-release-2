package com.varian.oiscn.core.targetvolume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by bhp9696 on 2018/2/26.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetVolumeVO implements Serializable{
    private Long id;
//    靶区名称
    private String name;
//    靶区备注
    private String memo;

    private List<LinkedHashMap<String,String>> targetVolumeItemList;


}
