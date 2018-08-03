package com.varian.oiscn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varian.oiscn.core.carepath.CarePathConfigItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Care Path Configuration.<br>
 */
@NoArgsConstructor
@Getter
@Setter
public class CarePathConfig {

    @JsonProperty
    private String defaultCarePathTemplateName = "StandardCarePath";

    @JsonProperty
    private List<CarePathConfigItem> carePath = new ArrayList<>();
}
