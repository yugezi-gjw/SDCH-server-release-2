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
public class WorkloadPlanVO implements Comparable<WorkloadPlanVO>,Serializable{
    private String planId;
    private int num;
    private boolean selected;
    private String comments;
    private int deliveredFractions;
    private int plannedFractions;
    private double deliveredDose;
    private double plannedDose;

    @Override
    public int compareTo(WorkloadPlanVO workloadPlanVO) {
        return this.planId.compareTo(workloadPlanVO.getPlanId());
    }
}
