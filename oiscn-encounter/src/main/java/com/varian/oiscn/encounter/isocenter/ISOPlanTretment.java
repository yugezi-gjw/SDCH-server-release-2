package com.varian.oiscn.encounter.isocenter;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BHP9696 on 2017/7/25.
 */
@Data
public class ISOPlanTretment implements Serializable,Comparable<ISOPlanTretment> {
    private String planId;
    private List<ISOCenterVO> siteList;

    @Override
    public int compareTo(ISOPlanTretment o) {
        return this.getPlanId().compareTo(o.getPlanId());
    }
}
