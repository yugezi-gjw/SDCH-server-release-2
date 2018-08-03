package com.varian.oiscn.encounter;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhp9696 on 2017/11/9.
 * Patient 对应的所有的CarePathInstance
 */
@Data
public class PatientEncounterCarePath {
    private String patientSer;
//  正在使用的Encounter对应的carePath instanceId
    private EncounterCarePathList plannedCarePath;
//  已经完成的Encounter对应的carePath instanceId
    private List<EncounterCarePathList> completedCarePath = new ArrayList<>();
}
