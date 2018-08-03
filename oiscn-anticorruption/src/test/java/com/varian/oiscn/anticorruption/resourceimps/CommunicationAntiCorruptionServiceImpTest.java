package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.oiscn.anticorruption.assembler.PhysicianCommentAssembler;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRCommunicationInterface;
import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import org.hl7.fhir.dstu3.model.Communication;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.mockito.Matchers.anyString;

/**
 * Created by gbt1220 on 12/21/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CommunicationAntiCorruptionServiceImp.class, PhysicianCommentAssembler.class})
public class CommunicationAntiCorruptionServiceImpTest {
    private FHIRCommunicationInterface communicationInterface;
    private CommunicationAntiCorruptionServiceImp communicationAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        communicationInterface = PowerMockito.mock(FHIRCommunicationInterface.class);
        PowerMockito.whenNew(FHIRCommunicationInterface.class).withNoArguments().thenReturn(communicationInterface);
        communicationAntiCorruptionServiceImp = new CommunicationAntiCorruptionServiceImp();
    }

    @Test
    public void testUpdatePhysicianCommentReturnEmpty() {
        PowerMockito.when(communicationInterface.queryByPatientId(anyString())).thenReturn(null);
        Assert.assertEquals("", communicationAntiCorruptionServiceImp.updatePhysicianComment(new PhysicianCommentDto()));
    }

    @Test
    public void testUpdatePhysicianCommentReturnUpdatedId() throws Exception {
        Communication communication = new Communication();
        PowerMockito.when(communicationInterface.queryByPatientId(anyString())).thenReturn(communication);
        PowerMockito.mockStatic(PhysicianCommentAssembler.class);
        PowerMockito.doNothing().when(PhysicianCommentAssembler.class, "assemblerCommunication", Matchers.any(), Matchers.any());
        PowerMockito.when(communicationInterface.update(communication)).thenReturn("updatedId");
        PhysicianCommentDto dto = givenAPhysicianCommentDto();
        Assert.assertNotNull(communicationAntiCorruptionServiceImp.updatePhysicianComment(dto));
    }

    @Test
    public void testErrorPhysicianCommentReturnUpdatedId() throws Exception {
        Communication communication = new Communication();
        PowerMockito.when(communicationInterface.queryByPatientId(anyString())).thenReturn(communication);
        PowerMockito.when(communicationInterface.update(Matchers.any())).thenReturn("updatedId");
        PhysicianCommentDto dto = givenAPhysicianCommentDto();
        Assert.assertNotNull(communicationAntiCorruptionServiceImp.errorPhysicianComment(dto));
    }

    @Test
    public void testCreatePhysicianComment(){
        Communication communication = PowerMockito.mock(Communication.class);
        PowerMockito.when(communicationInterface.getCommunicationObject(org.mockito.Matchers.any())).thenReturn(communication);
        PowerMockito.when(communicationInterface.create(communication)).thenReturn("12");
        String id = communicationAntiCorruptionServiceImp.createPhysicianComment(new PhysicianCommentDto());
        Assert.assertTrue("12".equals(id));
    }

    @Test
    public void testQueryPhysicianCommentReturnNull(){
        PowerMockito.when(communicationInterface.queryByPatientId(anyString())).thenReturn(null);
        Assert.assertNull(communicationAntiCorruptionServiceImp.queryPhysicianCommentByPatientId("patientId"));
    }

    @Test
    public void testQueryPhysicianCommentReturnDto(){
        Communication communication = PowerMockito.mock(Communication.class);
        PowerMockito.when(communicationInterface.queryByPatientId(anyString())).thenReturn(communication);
        Communication.CommunicationPayloadComponent payloadComponent = PowerMockito.mock(Communication.CommunicationPayloadComponent.class);
        PowerMockito.when(communication.getPayloadFirstRep()).thenReturn(payloadComponent);
        Type type = PowerMockito.mock(Type.class);
        PowerMockito.when(payloadComponent.getContent()).thenReturn(type);
        PowerMockito.when(type.toString()).thenReturn("content");
        PowerMockito.when(communication.getSent()).thenReturn(new Date());
        Reference sender = PowerMockito.mock(Reference.class);
        PowerMockito.when(communication.getSender()).thenReturn(sender);
        PowerMockito.when(sender.getReference()).thenReturn("practitionerId");
        Assert.assertNotNull(communicationAntiCorruptionServiceImp.queryPhysicianCommentByPatientId("patientId"));
    }

    public static PhysicianCommentDto givenAPhysicianCommentDto() {
        PhysicianCommentDto dto = new PhysicianCommentDto();
        dto.setComments("comments");
        dto.setPractitionerId("practitionerId");
        dto.setPatientSer("12121");
        return dto;
    }
}
