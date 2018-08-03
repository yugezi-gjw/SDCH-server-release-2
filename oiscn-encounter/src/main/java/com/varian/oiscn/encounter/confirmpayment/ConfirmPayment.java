package com.varian.oiscn.encounter.confirmpayment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bhp9696 on 2017/7/31.
 */
@Data
@NoArgsConstructor
public class ConfirmPayment implements Serializable {
    private String id;
    private String hisId;
    private String encounterId;
    private Long patientSer;

    private List<ConfirmStatus> confirmStatusList;
    private TreatmentConfirmStatus treatmentConfirmStatus;

}
