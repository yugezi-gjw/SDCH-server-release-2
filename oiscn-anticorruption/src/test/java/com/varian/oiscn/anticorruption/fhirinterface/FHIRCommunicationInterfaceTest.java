package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import org.hl7.fhir.dstu3.model.Communication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by gbt1220 on 12/21/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRCommunicationInterface.class, FHIRContextFactory.class})
public class FHIRCommunicationInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRCommunicationInterface fhirCommunicationInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        PowerMockito.mockStatic(FHIRContextFactory.class);
        factory = PowerMockito.mock(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
        fhirCommunicationInterface = new FHIRCommunicationInterface();
    }

    @Test
    public void testUpdatePhysicianComments() {
        Assert.assertTrue(true);
    }

    @Test
    public void testGetCommunicationObjectThenReturnCommunication(){
//        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
//        IQuery iQuery = PowerMockito.mock(IQuery.class);
//        PowerMockito.when(iUntypedQuery.forResource(ValueSet.class)).thenReturn(iQuery);
//        PowerMockito.when(iQuery.where(Matchers.any())).thenReturn(iQuery);
//
//        IClientExecutable iClientExecutable = PowerMockito.mock(IClientExecutable.class);
//        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iClientExecutable);
//        Bundle bundle = PowerMockito.mock(Bundle.class);
//        PowerMockito.when(iClientExecutable.execute()).thenReturn(bundle);
//        List<Bundle.BundleEntryComponent> bundleEntryComponentList = Arrays.asList(PowerMockito.mock(Bundle.BundleEntryComponent.class));
//        PowerMockito.when(bundle.getEntry()).thenReturn(bundleEntryComponentList);
//        org.hl7.fhir.dstu3.model.ValueSet valueSet = PowerMockito.mock(ValueSet.class);
//        PowerMockito.when(bundleEntryComponentList.get(0).getResource()).thenReturn(valueSet);
//        org.hl7.fhir.dstu3.model.ValueSet.ValueSetComposeComponent valueSetComposeComponent = PowerMockito.mock(org.hl7.fhir.dstu3.model.ValueSet.ValueSetComposeComponent.class);
//        PowerMockito.when(valueSet.getCompose()).thenReturn(valueSetComposeComponent);
//        org.hl7.fhir.dstu3.model.ValueSet.ConceptSetComponent conceptSetComponent = PowerMockito.mock(org.hl7.fhir.dstu3.model.ValueSet.ConceptSetComponent.class);
//        PowerMockito.when(valueSetComposeComponent.getIncludeFirstRep()).thenReturn(conceptSetComponent);
//        org.hl7.fhir.dstu3.model.ValueSet.ConceptReferenceComponent conceptReferenceComponent = PowerMockito.mock(org.hl7.fhir.dstu3.model.ValueSet.ConceptReferenceComponent.class);
//        PowerMockito.when(conceptSetComponent.getConceptFirstRep()).thenReturn(conceptReferenceComponent);
//        PowerMockito.when(conceptReferenceComponent.getCode()).thenReturn("physicianComment");

        Communication communication = fhirCommunicationInterface.getCommunicationObject(new PhysicianCommentDto());
        Assert.assertNotNull(communication);
    }
}
