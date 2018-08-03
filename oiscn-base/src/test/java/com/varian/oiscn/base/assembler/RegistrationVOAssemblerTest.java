package com.varian.oiscn.base.assembler;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.patient.RegistrationVO;

/**
 * Created by gbt1220 on 6/13/2017.
 */
public class RegistrationVOAssemblerTest {

    @InjectMocks
    private RegistrationVOAssembler assembler;

    @Test
    public void givenAPatientDtoWhenGetThenReturnRegistrationVO() {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        RegistrationVO registrationVO = RegistrationVOAssembler.getRegistrationVO(patientDto);
        Assert.assertEquals(patientDto.getAriaId(), registrationVO.getAriaId());
        Assert.assertEquals(patientDto.getHisId(), registrationVO.getHisId());
        Assert.assertEquals(patientDto.getNationalId(), registrationVO.getNationalId());
        Assert.assertEquals(patientDto.getChineseName(), registrationVO.getChineseName());
        Assert.assertEquals(patientDto.getEnglishName(), registrationVO.getEnglishName());
        Assert.assertEquals(patientDto.getGender(), registrationVO.getGender());
        Assert.assertEquals(patientDto.getBirthday(), registrationVO.getBirthday());
        Assert.assertEquals(patientDto.getContactPerson(), registrationVO.getContactPerson());
        Assert.assertEquals(patientDto.getContactPhone(), registrationVO.getContactPhone());
        Assert.assertEquals(patientDto.getPatientSer(), registrationVO.getPatientSer());
        Assert.assertEquals(patientDto.getPhysicianGroupId(), registrationVO.getPhysicianGroupId());
        Assert.assertEquals(patientDto.getPhysicianId(), registrationVO.getPhysicianId());
        Assert.assertEquals(patientDto.getTelephone(), registrationVO.getTelephone());
        Assert.assertEquals(patientDto.getAddress(), registrationVO.getAddress());
        Assert.assertEquals(patientDto.getCpTemplateId(), registrationVO.getCpTemplateId());
        Assert.assertEquals(patientDto.getCpTemplateName(), registrationVO.getCpTemplateName());
    }

    @Test
    public void givenARegistrationVOWhenGetThenReturnPatientDto() {
        RegistrationVO registrationVO = MockDtoUtil.givenARegistrationVO();
        PatientDto patientDto = RegistrationVOAssembler.getPatientDto(registrationVO);
        Assert.assertEquals(registrationVO.getAriaId(), patientDto.getAriaId());
        Assert.assertEquals(registrationVO.getHisId(), patientDto.getHisId());
        Assert.assertEquals(registrationVO.getNationalId(), patientDto.getNationalId());
        Assert.assertEquals(registrationVO.getChineseName(), patientDto.getChineseName());
        Assert.assertEquals(registrationVO.getEnglishName(), patientDto.getEnglishName());
        Assert.assertEquals(registrationVO.getGender(), patientDto.getGender());
        Assert.assertEquals(registrationVO.getBirthday(), patientDto.getBirthday());
        Assert.assertEquals(registrationVO.getContactPerson(), patientDto.getContactPerson());
        Assert.assertEquals(registrationVO.getContactPhone(), patientDto.getContactPhone());
        Assert.assertEquals(registrationVO.getPatientSer(), patientDto.getPatientSer());
        Assert.assertEquals(registrationVO.getPhysicianGroupId(), patientDto.getPhysicianGroupId());
        Assert.assertEquals(registrationVO.getPhysicianId(), patientDto.getPhysicianId());
        Assert.assertEquals(registrationVO.getTelephone(), patientDto.getTelephone());
        Assert.assertEquals(registrationVO.getAddress(), patientDto.getAddress());
        Assert.assertEquals(registrationVO.getCpTemplateId(), patientDto.getCpTemplateId());
        Assert.assertEquals(registrationVO.getCpTemplateName(), patientDto.getCpTemplateName());
    }
    
    @Test
    public void testGetDiagnosis() {
        RegistrationVO vo = MockDtoUtil.givenARegistrationVO();
        Diagnosis diagnosis = RegistrationVOAssembler.getDiagnosis(vo);
        Assert.assertEquals(vo.getDiagnosisCode(), diagnosis.getCode());
        Assert.assertEquals(vo.getDiagnosisDesc(), diagnosis.getDesc());
        Assert.assertEquals(vo.getRecurrent(), diagnosis.getRecurrence().toString());
        Assert.assertEquals(vo.getTcode(), diagnosis.getStaging().getTcode());
        Assert.assertEquals(vo.getNcode(), diagnosis.getStaging().getNcode());
        Assert.assertEquals(vo.getMcode(), diagnosis.getStaging().getMcode());
        Assert.assertEquals(vo.getStagingScheme(), diagnosis.getStaging().getSchemeName());
        Assert.assertEquals(vo.getStaging(), diagnosis.getStaging().getStage());
        Assert.assertEquals(vo.getDiagnosisDate(), diagnosis.getStaging().getDate());
        Assert.assertEquals(vo.getDiagnosisDate(), diagnosis.getDiagnosisDate());
        Assert.assertEquals(vo.getBodypart(), diagnosis.getBodypartCode());
        Assert.assertEquals(vo.getBodypartDesc(), diagnosis.getBodypartDesc());
        Assert.assertEquals(vo.getDiagnosisNote(), diagnosis.getDiagnosisNote());
        
        RegistrationVOAssembler.assemblerDiagnosisData2RegistrationVO(vo, diagnosis);
        Assert.assertEquals(vo.getDiagnosisCode(), diagnosis.getCode());
        Assert.assertEquals(vo.getDiagnosisDesc(), diagnosis.getDesc());
        Assert.assertEquals(vo.getRecurrent(), diagnosis.getRecurrence().toString());
        Assert.assertEquals(vo.getTcode(), diagnosis.getStaging().getTcode());
        Assert.assertEquals(vo.getNcode(), diagnosis.getStaging().getNcode());
        Assert.assertEquals(vo.getMcode(), diagnosis.getStaging().getMcode());
        Assert.assertEquals(vo.getStagingScheme(), diagnosis.getStaging().getSchemeName());
        Assert.assertEquals(vo.getStaging(), diagnosis.getStaging().getStage());
        Assert.assertEquals(vo.getDiagnosisDate(), diagnosis.getStaging().getDate());
        Assert.assertEquals(vo.getDiagnosisDate(), diagnosis.getDiagnosisDate());
        Assert.assertEquals(vo.getBodypart(), diagnosis.getBodypartCode());
        Assert.assertEquals(vo.getBodypartDesc(), diagnosis.getBodypartDesc());
        Assert.assertEquals(vo.getDiagnosisNote(), diagnosis.getDiagnosisNote());
    }
}
