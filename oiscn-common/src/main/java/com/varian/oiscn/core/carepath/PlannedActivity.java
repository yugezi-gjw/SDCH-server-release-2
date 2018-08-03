package com.varian.oiscn.core.carepath;

import com.varian.oiscn.core.activity.ActivityTypeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * Created by gbt1220 on 4/20/2017.
 * Modified by bhp9696 on 5/7/2017.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PlannedActivity implements Comparable<PlannedActivity> {
    private String id;
    private String departmentID;
    private String defaultGroupID;
    private String activityCode;
    private String carePathLane;
    private ActivityTypeEnum activityType;
    private Integer lagAfterPrevActivity;
    private Boolean autoAssignPrimaryOncologist;
    private List<String> deviceIDs;
    private List<String> prevActivities;
    private List<String> nextActivities;

    @Override
    public int compareTo(PlannedActivity target) {
        return this.lagAfterPrevActivity - target.lagAfterPrevActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlannedActivity that = (PlannedActivity) o;

        return Objects.equals(id,that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
