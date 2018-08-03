package com.varian.oiscn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varian.oiscn.core.appointment.calling.CallingConfig;
import com.varian.oiscn.core.appointment.calling.ServerConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Calling Server Configuration.<br>
 */
@Getter
@Setter
@NoArgsConstructor
public class CallingSystemConfiguration {

    @JsonProperty
    private ServerConfiguration server = new ServerConfiguration();

    @JsonProperty
    private CallingConfig config = new CallingConfig();

}
