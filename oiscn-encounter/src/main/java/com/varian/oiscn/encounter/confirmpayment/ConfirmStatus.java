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
public class ConfirmStatus implements Serializable {
    private String activityCode;
    private String activityContent;
    /**
     * Confirm Payment status.
     * 0-not confirm
     * 1-confirmed
     */
    private Integer status;
    private Long carePathInstanceId;
}
