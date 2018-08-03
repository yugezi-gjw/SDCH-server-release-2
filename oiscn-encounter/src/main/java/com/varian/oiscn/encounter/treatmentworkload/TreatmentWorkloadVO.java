package com.varian.oiscn.encounter.treatmentworkload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BHP9696 on 2017/11/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentWorkloadVO implements Serializable{
    private String hisId;
    private String encounterId;
    private Long patientSer;
    private String treatmentDate;
    private List<WorkloadPlanVO> planList;
    private List<WorkloadSignatureVO> sign;
    private List<String> worker;
}
