package com.varian.oiscn.encounter.dynamicform;

import com.varian.oiscn.core.common.KeyValuePair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicFormRecord {
    private String id;
    private String hisId;
    private Long encounterId;
    private Long patientSer;
    private String carePathInstanceId;
    private String templateId;
    private String createdUser;
    private Date createDate;
    private List<KeyValuePair> dynamicFormRecordInfo;
    private String templateInfo;
}
