package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.core.carepath.ActivityInstance;

import java.util.Comparator;

/**
 * Created by gbt1220 on 7/28/2017.
 */
public class ActivityInstanceLastModifiedDtComparator implements Comparator<ActivityInstance> {
    @Override
    public int compare(ActivityInstance firstInstance, ActivityInstance secondInstance) {
        if (firstInstance.getLastModifiedDT() == null) {
            return 1;
        } else if (secondInstance.getLastModifiedDT() == null) {
            return -1;
        } else {
            return secondInstance.getLastModifiedDT().compareTo(firstInstance.getLastModifiedDT());
        }
    }
}
