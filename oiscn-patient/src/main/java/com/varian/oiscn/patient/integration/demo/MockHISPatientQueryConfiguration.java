package com.varian.oiscn.patient.integration.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by gbt1220 on 1/2/2018.
 */
@Data
@NoArgsConstructor
public class MockHISPatientQueryConfiguration {
    @JsonProperty
    private List<MockHISPatientQueryDto> patients;
}
