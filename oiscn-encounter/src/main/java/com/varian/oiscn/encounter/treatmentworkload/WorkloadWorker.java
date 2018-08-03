package com.varian.oiscn.encounter.treatmentworkload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by BHP9696 on 2017/11/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadWorker implements Serializable{
    private long workloadId;
    private String workerName;
    private int orderNum;
}
