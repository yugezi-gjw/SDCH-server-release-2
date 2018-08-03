package com.varian.oiscn.encounter.confirmpayment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by bhp9696 on 2017/7/31.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentConfirmStatus implements Serializable {
    private String activityCode;
    private String activityContent;
    private Integer totalPaymentCount;
    private Integer confirmPaymentCount;
}
