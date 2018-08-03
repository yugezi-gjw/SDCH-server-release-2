package com.varian.oiscn.encounter.isocenter;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BHP9696 on 2017/7/25.
 */
@Data
public class ISOCenter implements Serializable {
    private String id;
    private String hisId;
    private String encounterId;
    private Long patientSer;
    private List<ISOPlanTretment> planList;
}
