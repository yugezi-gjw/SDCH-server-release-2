package com.varian.oiscn.encounter.view;

import com.varian.oiscn.core.common.KeyValuePair;
import lombok.Data;

import java.util.List;

@Data
public class DynamicFormItemsAndTemplateInfo {
    private List<KeyValuePair> recordInfo;
    private String templateInfo;
}
