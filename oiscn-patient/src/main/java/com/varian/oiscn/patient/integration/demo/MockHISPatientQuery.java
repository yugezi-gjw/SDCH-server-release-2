package com.varian.oiscn.patient.integration.demo;

import com.varian.oiscn.base.codesystem.CodeSystemServiceImp;
import com.varian.oiscn.base.coverage.PayorInfoPool;
import com.varian.oiscn.base.diagnosis.BodyPartVO;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.patient.integration.IPatientQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Mock patient query class
 * Created by gbt1220 on 11/21/2017.
 */
public class MockHISPatientQuery implements IPatientQuery {

    public static final String CONFIG_HIS_PATIENT_QUERY_DEMO_YAML = "config\\dev\\HISPatientQueryDemoData.yaml";

    @Override
    public RegistrationVO queryByHisId(String hisId) {
        List<MockHISPatientQueryDto> list = new MockHISPatientQueryConfigService(CONFIG_HIS_PATIENT_QUERY_DEMO_YAML).getDemoData();
        RegistrationVO mockData = null;
        for (MockHISPatientQueryDto mockHISPatientQueryDto : list) {
            if (StringUtils.equalsIgnoreCase(hisId, mockHISPatientQueryDto.getHisId())) {
                mockData = new RegistrationVO();
                mockData.setHisId(hisId);
                mockData.setNationalId(mockHISPatientQueryDto.getNationalId());
                mockData.setChineseName(mockHISPatientQueryDto.getFullName());
                mockData.setGender(mockHISPatientQueryDto.getGender());
                mockData.setBirthday(mockHISPatientQueryDto.getBirthDate());
                mockData.setContactPerson(mockHISPatientQueryDto.getContactPerson());
                mockData.setContactPhone(mockHISPatientQueryDto.getContactPhone());
                mockData.setTelephone(mockHISPatientQueryDto.getTelephone());
                mockData.setAddress(mockHISPatientQueryDto.getAddress());
                mockData.setPatientHistory(mockHISPatientQueryDto.getMedicalHistory());
                mockData.setAge(mockHISPatientQueryDto.getAge());

                mockData.setUrgent(mockHISPatientQueryDto.isUrgent());
                mockData.setPositiveSign(mockHISPatientQueryDto.getPositiveSign());
                mockData.setInsuranceTypeCode(PayorInfoPool.get(mockHISPatientQueryDto.getInsuranceType()));
                mockData.setInsuranceType(mockHISPatientQueryDto.getInsuranceType());
                mockData.setPatientSource(mockHISPatientQueryDto.getPatientSource());
                mockData.setPhysicianComment(mockHISPatientQueryDto.getPhysicianComment());
                mockData.setAllergyInfo(mockHISPatientQueryDto.getAllergyInfo());
                mockData.setBodypart(searchBodyPart(mockHISPatientQueryDto.getBodypart()));
                mockData.setBodypartDesc(mockHISPatientQueryDto.getBodypart());
                mockData.setDiagnosisDate(mockHISPatientQueryDto.getDiagnosisDate());
                mockData.setDiagnosisNote(mockHISPatientQueryDto.getDiagnosisNote());
                mockData.setEcogScore(mockHISPatientQueryDto.getEcogScore());
                mockData.setEcogDesc(mockHISPatientQueryDto.getEcogDesc());
                mockData.setWarningText(mockHISPatientQueryDto.getAlert());
                mockData.setRecurrent(mockHISPatientQueryDto.getRecurrent());
                mockData.setPhysicianGroupId(mockHISPatientQueryDto.getPhysicianGroupId());
                mockData.setPhysicianId(mockHISPatientQueryDto.getPhysicianId());
                mockData.setPhysicianName(mockHISPatientQueryDto.getPhysicianName());
                mockData.setPhysicianBName(mockHISPatientQueryDto.getPhysicianBName());
                mockData.setPhysicianCName(mockHISPatientQueryDto.getPhysicianCName());
                mockData.setDiagnosisCode(mockHISPatientQueryDto.getDiagnosisCode());
                mockData.setDiagnosisDesc(mockHISPatientQueryDto.getDiagnosisDesc());
                mockData.setTcode(mockHISPatientQueryDto.getTcode());
                mockData.setNcode(mockHISPatientQueryDto.getNcode());
                mockData.setMcode(mockHISPatientQueryDto.getMcode());
                mockData.setStaging(mockHISPatientQueryDto.getStaging());

                List<KeyValuePair> dynamicFormItems = mockHISPatientQueryDto.getDynamicFormItems();
                if (dynamicFormItems != null && !dynamicFormItems.isEmpty()) {
                    for (KeyValuePair item : dynamicFormItems) {
                        mockData.getDynamicFormItems().put(item.getKey(), item.getValue());
                    }
                }
            }
        }
        return mockData;
    }

    public String searchBodyPart(String condition) {
        CodeSystemServiceImp codeSystemServiceImp = new CodeSystemServiceImp();
        List<BodyPartVO> bodyParts = codeSystemServiceImp.queryBodyParts(condition, "1");
        String result = StringUtils.EMPTY;
        if (bodyParts != null && !bodyParts.isEmpty()) {
            result = bodyParts.get(0).getCode();
        }
        return result;
    }
}
