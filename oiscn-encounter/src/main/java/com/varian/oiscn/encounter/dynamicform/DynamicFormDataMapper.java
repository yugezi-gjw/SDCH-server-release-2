package com.varian.oiscn.encounter.dynamicform;

import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.patient.PatientDto;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 10/17/2017.
 */
public class DynamicFormDataMapper {

    private PatientDto patientInfo;

    public DynamicFormDataMapper(List<KeyValuePair> dynamicFormItems) {
        Map<String, Object> patient = new HashMap();
        for (KeyValuePair item : dynamicFormItems) {
            if (StringUtils.startsWithIgnoreCase(item.getKey(), "Patient")) {
                String[] itemKey = StringUtils.split(item.getKey(), ".");
                if (itemKey != null && itemKey.length == 2) {
                    patient.put(itemKey[1], item.getValue());
                }
            }
        }
        if (!patient.isEmpty()) {
            patientInfo = new ClassValueMapper<PatientDto>().newClassInstanceWithValues(PatientDto.class, patient);
        }
    }

    public PatientDto getPatientInfo() {
        return this.patientInfo;
    }
}
