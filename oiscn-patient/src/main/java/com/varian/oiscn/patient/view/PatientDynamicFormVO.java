package com.varian.oiscn.patient.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by BHP9696 on 2017/8/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDynamicFormVO implements Comparable<PatientDynamicFormVO> {
    private String id;
    private String selectedTemplateId;
    private String selectedTemplateHeader;
    private String carePathInstanceId;
    private String completedDt;

    @Override
    public int compareTo(PatientDynamicFormVO o) {
        return this.getCompletedDt().compareTo(o.completedDt);
    }
}
