package com.varian.oiscn.encounter.treatmentworkload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by BHP9696 on 2017/11/1.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkloadPlan implements Serializable{
    private long workloadId;
    private String planId;
    private int deliveredFractions;
//  selected:1 not-selected:0
    private byte selected;
    private String comment;
}
