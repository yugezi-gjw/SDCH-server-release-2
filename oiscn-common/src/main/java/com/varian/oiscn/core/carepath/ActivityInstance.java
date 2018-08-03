package com.varian.oiscn.core.carepath;

import com.varian.oiscn.core.activity.ActivityTypeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by gbt1220 on 4/24/2017.
 * Modified by bhp9696 on 5/7/2017.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ActivityInstance implements Comparable<ActivityInstance> {
    private Integer index;
    private String id;
    private String instanceID;//task id or appointment id in ARIA
    private String departmentID;
    private String defaultGroupID;
    private String activityCode;
    private String activityCategory;
    private String carePathLane;
    private ActivityTypeEnum activityType;
    private String primaryPhysicianID;
    private CarePathStatusEnum status;
    private Boolean isActiveInWorkflow;
    private Integer lagAfterPrevActivity;   //by minute
    private Date lastModifiedDT;
    private Date dueDateOrScheduledStartDate;
    private List<String> deviceIDs;
    private List<String> prevActivities;
    private List<String> nextActivities;

    @Override
    public int compareTo(ActivityInstance obj) {
        return this.getId().compareTo(obj.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActivityInstance that = (ActivityInstance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 状态是active的节点
     *
     * @return
     */
    public boolean getIsActiveInWorkflow() {
        return this.status != null && this.status.equals(CarePathStatusEnum.ACTIVE);
    }

    /**
     * 返回isActiveInWorkflow标志
     *
     * @return
     */
    public boolean getOriginalActiveInWorkflowFlag() {
        return this.isActiveInWorkflow;
    }
}
