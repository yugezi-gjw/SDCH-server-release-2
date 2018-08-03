package com.varian.oiscn.encounter.isocenter;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by BHP9696 on 2017/7/25.
 */
@Data
public class ISOCenterVO implements Serializable {
    private String isoName;
    private Double vrt;
    private Double lng;
    private Double lat;
}
