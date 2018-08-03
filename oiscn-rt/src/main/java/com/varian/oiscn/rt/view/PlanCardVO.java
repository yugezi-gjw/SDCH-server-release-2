package com.varian.oiscn.rt.view;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by BHP9696 on 2017/10/18.
 */
@Data
@AllArgsConstructor
public class PlanCardVO implements Serializable {
    private String planId;
    private String planName;
    private Integer plannedFractions;
    private Integer deliveredFractions;
}
