package com.varian.oiscn.core.targetvolume;

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
public class PlanTargetVolumeInfo implements Serializable{
    private String planId;
    private List<String> nameList;
}
