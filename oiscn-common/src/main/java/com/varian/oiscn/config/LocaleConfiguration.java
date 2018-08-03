package com.varian.oiscn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by gbt1220 on 5/23/2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class LocaleConfiguration {
    @JsonProperty
    private String language;

    @JsonProperty
    private String country;
}
