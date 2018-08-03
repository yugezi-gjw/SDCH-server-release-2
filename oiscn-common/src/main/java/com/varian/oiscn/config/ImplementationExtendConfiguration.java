package com.varian.oiscn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varian.oiscn.core.extend.ImplementationExtension;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Implementation extend configuration
 * Created by gbt1220 on 11/21/2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class ImplementationExtendConfiguration {
    @JsonProperty
    private List<ImplementationExtension> extensions ;
}
