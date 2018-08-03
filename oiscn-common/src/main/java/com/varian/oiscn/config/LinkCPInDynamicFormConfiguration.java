package com.varian.oiscn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varian.oiscn.core.linkcp.LinkCPInDynamicFormItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class LinkCPInDynamicFormConfiguration {
    @JsonProperty
    private List<LinkCPInDynamicFormItem> linkCPInDynamicFormItems;
}
