package com.varian.oiscn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by gbt1220 on 2/21/2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class FhirServerConfiguration {
    @JsonProperty
    private int fhirConnectionTimeout = 10000;

    @JsonProperty
    private int fhirConnectionRequestTimeout = 10000;

    @JsonProperty
    private int fhirSocketTimeout = 10000;

    @JsonProperty
    private String fhirLanguage = "ENU";
}
