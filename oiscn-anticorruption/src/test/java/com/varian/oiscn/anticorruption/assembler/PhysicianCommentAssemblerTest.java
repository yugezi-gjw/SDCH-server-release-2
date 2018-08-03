package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Communication;
import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

/**
 * Created by gbt1220 on 12/25/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PhysicianCommentAssembler.class})
public class PhysicianCommentAssemblerTest {
    @InjectMocks
    private PhysicianCommentAssembler assembler;

    @Test
    public void testAssemblerCommunication() {
        Communication communication = new Communication();
        PhysicianCommentDto dto = givenAPhysicianCommentDto();
        assembler.assemblerCommunication(communication, dto);
        Assert.assertEquals(dto.getComments(), communication.getPayloadFirstRep().getContent().toString());
    }

    public static PhysicianCommentDto givenAPhysicianCommentDto() {
        PhysicianCommentDto dto = new PhysicianCommentDto();
        dto.setComments("comment");
        dto.setPractitionerId("practitionerId");
        dto.setPatientSer("121221");
        dto.setLastUpdateTime(new Date());
        return dto;
    }
}
