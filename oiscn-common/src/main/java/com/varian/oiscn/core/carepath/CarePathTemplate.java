package com.varian.oiscn.core.carepath;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 4/20/2017.
 */
@Data
public class CarePathTemplate {
    private String id;
    private String templateName;
    private CarePathStatusEnum status;
    private String comments;
    private String departmentID;
    private List<PlannedActivity> activities;

    public void addPlannedActivity(PlannedActivity activity) {
        if (activities == null) {
            activities = new ArrayList();
        }
        activities.add(activity);
    }
}
