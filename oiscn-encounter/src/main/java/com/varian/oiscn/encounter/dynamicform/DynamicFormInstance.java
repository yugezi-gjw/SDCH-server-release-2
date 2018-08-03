package com.varian.oiscn.encounter.dynamicform;

import com.varian.oiscn.core.common.KeyValuePair;
import lombok.Data;

import java.util.List;

/**
 * Created by gbt1220 on 6/30/2017.
 */
@Data
public class DynamicFormInstance {
    private String id;
    private String hisId;
    private String encounterId;
    private String patientSer;
    private List<KeyValuePair> dynamicFormItems;
}
