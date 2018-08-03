package com.varian.oiscn.core.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asharma0 on 12/21/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    private String ariaId;
    private String hisId;
    private String nationalId;
    private String chineseName;
    private String englishName;
    private String pinyin;
    private String gender;
    private Date birthday;
    private String contactPerson;
    private String contactPhone;
    private String patientSer;
    private String physicianGroupId;
    private String physicianId;
    private String physicianName;
    private String physicianPhone;
    private String telephone;
    private String address;
    private List<PatientLabel> labels;
    private Date createdDT;
    private byte[] photo;
    private String cpTemplateId;
    private String cpTemplateName;
    // FIXME Task 313510:Server: anti- 保存新的输入项
    /** ECOG评分: 0,1,2,3,4 or free text,32 English chars */
    private String ecogScore;
    /** ECOG评分描述 */
    private String ecogDesc;
    /** 阳性标识: free text, 32 English chars */
    private String positiveSign;
    /** 医疗保险类型 */
    private String insuranceType;
    /** 患者来源: 门诊/住院 */
    private String patientSource;

    private String patientHistory;

    public void addPatientLabel(PatientLabel patientLabel) {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        labels.add(patientLabel);
    }

    @Data
    public static class PatientLabel {
        private String labelId;
        private String labelTag;
        private String labelText;
    }
}