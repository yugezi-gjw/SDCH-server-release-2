package com.varian.oiscn.patient.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by BHP9696 on 2017/8/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowProgressTreatmentNode extends WorkflowProgressNode {
    private Integer totalTreatmentCount;
    private Integer treatedCount;
}
