package com.varian.oiscn.base.integration.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/20/2017
 * @Modified By:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HisPatientInfo {
    private String hisPatientInfoServiceUrl;
    private String method;
    private int connectionTimeout;
}
