package com.varian.oiscn.encounter;

import com.varian.oiscn.core.encounter.EncounterEndPlan;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhp9696 on 2018/3/23.
 */
@Data
public class PatientEncounterEndPlan {
    private String patientSer;
    //  已经完成的Encounter对应的plan
    private List<EncounterEndPlan> completedPlan = new ArrayList<>();
}
