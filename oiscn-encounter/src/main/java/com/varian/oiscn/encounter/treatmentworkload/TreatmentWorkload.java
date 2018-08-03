package com.varian.oiscn.encounter.treatmentworkload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * Created by BHP9696 on 2017/8/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentWorkload implements Serializable{
    private String id;
    private String hisId;
    private String encounterId;
    private Long patientSer;
    private Date treatmentDate;
    private List<WorkloadPlan> workloadPlans;
    private List<WorkloadSignature> workloadSignatures;
    private List<WorkloadWorker> workloadWorkers;
}
