package com.varian.oiscn.encounter.assembler;

import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.encounter.util.MockDtoUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Created by gbt1220 on 3/31/2017.
 */
public class EncounterAssemblerTest {

    @InjectMocks
    private EncounterAssembler assembler;

    private RegistrationVO registrationVO;

    @Before
    public void setup() {
        registrationVO = MockDtoUtil.givenARegistrationVO();
    }

    @Test
    public void givenPatientDtoWhenAssemblerThenReturnEncounter() {
        Encounter encounter = EncounterAssembler.getEncounter(registrationVO);
//        Assert.assertEquals(registrationVO.getId(), encounter.getPatientID());
        Assert.assertEquals(registrationVO.getPhysicianGroupId(), encounter.getPrimaryPhysicianGroupID());
        Assert.assertEquals(registrationVO.getPhysicianGroupName(), encounter.getPrimaryPhysicianGroupName());
        Assert.assertEquals(registrationVO.getPhysicianId(), encounter.getPrimaryPhysicianID());
        Assert.assertEquals(StatusEnum.IN_PROGRESS, encounter.getStatus());
        Assert.assertEquals(registrationVO.getEcogScore(), encounter.getEcogScore());
        Assert.assertEquals(registrationVO.getEcogDesc(), encounter.getEcogDesc());
        Assert.assertEquals(registrationVO.getPositiveSign(), encounter.getPositiveSign());
        Assert.assertEquals(registrationVO.getInsuranceType(), encounter.getInsuranceType());
        Assert.assertEquals(registrationVO.getPatientSource(), encounter.getPatientSource());
        Assert.assertEquals(registrationVO.getAge(), encounter.getAge());
        Assert.assertEquals(registrationVO.getAllergyInfo(), encounter.getAllergyInfo());
    }
}
