package com.varian.oiscn.core.carepath;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 4/24/2017.
 */
@Data
public class CarePathInstance {
    private String id;
    private String carePathTemplateId;
    private String patientID;
    private String encounterID;
    private List<ActivityInstance> activityInstances;

    public void addActivityInstance(ActivityInstance instance) {
        if (activityInstances == null) {
            activityInstances = new ArrayList<>();
        }
        if (!activityInstances.contains(instance)) {
            activityInstances.add(instance);
        }
    }

    /**
     * 注意： 对于治疗的Appointment， 除了第一次治疗的Appointment, 其他的治疗的Appointment，由于没有前后节点，也会被过滤掉.<br>
     * @return
     */
    public List<ActivityInstance> getActivityInstances() {
        //需要过滤掉carepath instance中和此carepath instance无关的task或appointment，
        //如果直接创建和此carepath无关的task或appointment，在此instance里也会出现，所以需要过滤掉
        // 注意： 对于治疗的Appointment， 除了第一次治疗的Appointment, 其他的治疗的Appointment，由于没有前后节点，也会被过滤掉
        if (activityInstances != null && !activityInstances.isEmpty()) {
            List<ActivityInstance> result = new ArrayList<>();
            for (ActivityInstance activityInstance : activityInstances) {
                if (activityInstance.getPrevActivities() == null && activityInstance.getNextActivities() == null) {
                    continue;
                }
                result.add(activityInstance);
            }
            return result;
        }
        return null;
    }

    public List<ActivityInstance> getOriginalActivityInstances() {
        //包含多次治疗的Appointment
        return activityInstances;
    }
}
