package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IRead;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import ca.uhn.fhir.rest.gclient.IReadTyped;
import com.varian.fhir.resources.TreatmentSummary;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by asharma0 on 12-07-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRTreatmentSummaryInterface.class, FHIRContextFactory.class})
public class FHIRTreatmentSummaryInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRTreatmentSummaryInterface treatmentSummaryInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        treatmentSummaryInterface = new FHIRTreatmentSummaryInterface();
    }

    @Test
    public void givenAPatientIdWhenQueryThenReturnTreatmentSummary() throws Exception {
        final String PATIENT_ID = "patientId";
        TreatmentSummary treatmentSummary = new TreatmentSummary();
        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
        IRead iRead = PowerMockito.mock(IRead.class);
        IReadTyped iReadTyped = PowerMockito.mock(IReadTyped.class);
        IReadExecutable iReadExecutable = PowerMockito.mock(IReadExecutable.class);
        PowerMockito.when(client.read()).thenReturn(iRead);
        PowerMockito.when(iRead.resource(TreatmentSummary.class)).thenReturn(iReadTyped);
        PowerMockito.when(iReadTyped.withId(PATIENT_ID)).thenReturn(iReadExecutable);
        PowerMockito.when(iReadExecutable.execute()).thenReturn(treatmentSummary);
        TreatmentSummary tsReturned = treatmentSummaryInterface.getTreatmentSummary(PATIENT_ID);
        Assert.assertThat(treatmentSummary, is(tsReturned));
    }
}