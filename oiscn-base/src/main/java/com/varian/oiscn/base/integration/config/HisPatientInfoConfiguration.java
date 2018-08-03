package com.varian.oiscn.base.integration.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/20/2017
 * @Modified By:
 */
@Getter
@Setter
@NoArgsConstructor
public class HisPatientInfoConfiguration {

    @JsonProperty
    private boolean hisPatientQueryEnable;

    @JsonProperty
    private boolean callingSystemEnable;

    @JsonProperty
    private HisPatientInfo patientInfoServer;

}
