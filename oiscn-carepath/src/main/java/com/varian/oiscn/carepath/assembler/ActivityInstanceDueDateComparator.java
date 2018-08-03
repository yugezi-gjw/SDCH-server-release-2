package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.core.carepath.ActivityInstance;

import java.util.Comparator;
import java.util.Date;

import static org.apache.http.util.TextUtils.isEmpty;


/**
 * Created by gbt1220 on 7/19/2017.
 */
public class ActivityInstanceDueDateComparator implements Comparator<ActivityInstance> {
    @Override
    public int compare(ActivityInstance firstInstance, ActivityInstance secondInstance) {
        if (isEmpty(firstInstance.getInstanceID())) {
            return -1;
        } else if (isEmpty(secondInstance.getInstanceID())) {
            return 1;
        } else {
            Date tmpDate = new Date();
            Date dueDateOfFirstInstance = firstInstance.getDueDateOrScheduledStartDate();
            if(dueDateOfFirstInstance == null){
                dueDateOfFirstInstance = tmpDate;
            }
            Date dueDateOfSecondInstance = secondInstance.getDueDateOrScheduledStartDate();
            if(dueDateOfSecondInstance == null){
                dueDateOfSecondInstance = tmpDate;
            }
            return dueDateOfFirstInstance.compareTo(dueDateOfSecondInstance);
        }
    }
}
