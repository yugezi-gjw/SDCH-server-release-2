package com.varian.oiscn.core.patient;

import com.varian.oiscn.core.identifier.Identifier;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bhp9696 on 1/19/2018.
 */
@Data
public class PatientV2 {
    private String id;
    private String hisId;
    //  V号
    private String ariaId;
    //    患者照片
    private String photo;
    //    身份证
    private String nationalId;
    //    患者姓名
    private String fullName;
    //    患者姓名拼音
    private String pinyinName;
    //    性别
    private GenderEnum gender;
    //    出生日期
    private Date birthdate;
    //    联系电话
    private String telephone;
    //    联系地址
    private String address;
    //    紧急联系人姓名
    private String contactPerson;
    //    紧急联系人电话
    private String contactPhone;
    //    既往病史
    private String medicalHistory;
    private PatientStatusEnum patientStatus;
    //    结婚状况
    private MaritalStatusEnum maritalStatus;
    //    国籍
    private String citizenship;

    //    民族
    private String ethnicGroup;
    private List<Identifier> identifiers;

    public void addIdentifier(Identifier identifier) {
        if (identifiers == null) {
            identifiers = new ArrayList<>();
        }
        identifiers.add(identifier);
    }
}
