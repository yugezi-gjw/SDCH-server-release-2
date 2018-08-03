package com.varian.oiscn.core.encounter;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * Created by bhp9696 on 2018/3/23.
 */
@Data
public class EncounterEndPlan implements Serializable {
    private Long encounterId;
    private String planSetupId;
    private Date planCreatedDt;
}
