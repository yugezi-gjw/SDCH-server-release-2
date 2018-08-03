package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.codesystems.CommunicationCategory;

import java.util.List;

/**
 * Created by gbt1220 on 12/21/2017.
 */
public class FHIRCommunicationInterface extends FHIRInterface<Communication>{

    private static String CATEGORY = CommunicationCategory.INSTRUCTION.toCode();
    public Communication queryByPatientId(String patientId){
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        Bundle bundle = client.search().forResource(Communication.class)
                .where(new StringClientParam(Communication.SP_CATEGORY).matches().value(CATEGORY))
                .and(new StringClientParam(Communication.SP_PATIENT).matches().value(patientId))
                .returnBundle(Bundle.class).execute();
        if(bundle != null){
            List<Communication> communicationList = getListFromBundle(bundle);
            if(communicationList.isEmpty()){
                return null;
            }
            Communication communication = communicationList.get(0);
            return communication;
        }
        return null;
    }


    public  org.hl7.fhir.dstu3.model.Communication getCommunicationObject(PhysicianCommentDto physicianCommentDto) {
        org.hl7.fhir.dstu3.model.Communication c = new org.hl7.fhir.dstu3.model.Communication();
        c.addCategory().setText(CATEGORY);
        c.setSubject(getReference(physicianCommentDto.getPatientSer(), null, ResourceType.Patient.name(), false));
        c.setSent(physicianCommentDto.getLastUpdateTime());

        c.setSender(getReference(physicianCommentDto.getPractitionerId(), null, ResourceType.Practitioner.name(), false));
        c.addPayload().setContent(new StringType(physicianCommentDto.getComments()));
        // Keep status in Preparation only if you need to update it several times, if it is in COMPLETED state it can not be updated.
        c.setStatus(com.varian.fhir.resources.Communication.CommunicationStatus.PREPARATION);
        return c;
    }

    private static Reference getReference(String id, String display, String resourceName, boolean contained) {
        String t = contained ? (StringUtils.startsWith(id, "#") ? id : ("#" + resourceName + "/" + id)) : resourceName + "/" + id;
        Reference reference = new Reference().setReference(t).setDisplay(display);
        reference.setId(t);
        return reference;
    }

}
