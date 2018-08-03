package com.varian.oiscn.patient.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by BHP9696 on 2017/8/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowProgressNode {
    private String activityName;
    private Boolean isKeyActivity;
    private WorkflowProgressNodeStatus status;
}
