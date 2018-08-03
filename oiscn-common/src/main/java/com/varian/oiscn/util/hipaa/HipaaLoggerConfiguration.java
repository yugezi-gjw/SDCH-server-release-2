package com.varian.oiscn.util.hipaa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HipaaLoggerConfiguration {
    @JsonProperty
    private String hostname = "localhost";

    @JsonProperty
    private int port = 55020;

    @JsonProperty
    private int timeoutInMs = 300;
}
