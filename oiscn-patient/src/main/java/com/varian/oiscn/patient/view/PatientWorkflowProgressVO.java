package com.varian.oiscn.patient.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by BHP9696 on 2017/8/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientWorkflowProgressVO {
    private List<WorkflowProgressNode> activityNodes;
    private List<WorkflowProgressTreatmentNode> treatmentActivityNodes;
}
