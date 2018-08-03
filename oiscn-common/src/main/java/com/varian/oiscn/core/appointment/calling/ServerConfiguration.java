package com.varian.oiscn.core.appointment.calling;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Calling Server Configuration.<br>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfiguration {

    @JsonProperty
    private String callingServiceUrl = "http://127.0.0.1:55000/appointments/mockcallingserver";

    @JsonProperty
    private String method = "POST";
    
    @JsonProperty
    private int connectionTimeout = 10000;

    @JsonProperty
    private int retryTimes = 3;

    @JsonProperty
    private int retryInterval = 5000;

    @JsonProperty
    private String contentType = "application/x-www-form-urlencoded";

    @JsonProperty
    private String charset = "UTF-8";
}
