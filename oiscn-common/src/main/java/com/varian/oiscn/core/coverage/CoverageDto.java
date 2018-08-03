package com.varian.oiscn.core.coverage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 11/1/2017
 * @Modified By:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverageDto {

    private String patientSer;
    private String insuranceTypeCode;
    private String insuranceTypeDesc;

}
