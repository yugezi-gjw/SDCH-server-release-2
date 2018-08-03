package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.varian.fhir.resources.Group;
import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.datahelper.MockGroupUtil;
import com.varian.oiscn.anticorruption.datahelper.MockPractitionerUtil;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-02-09.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRPractitionerInterface.class, FHIRContextFactory.class})
public class FHIRPractitionerInterfaceTest {
    private static final String PRACTITIONER_ID = "PractitionerId";
    private static final String LOGIN_ID = "LoginId";
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRPractitionerInterface fhirPractitionerInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirPractitionerInterface = new FHIRPractitionerInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenAPractitionerIdWhenQueryThenReturnPractitioner() throws Exception {
        Practitioner practitionerMock = MockPractitionerUtil.givenAPractitioner();

        IRead iRead = PowerMockito.mock(IRead.class);
        PowerMockito.when(client.read()).thenReturn(iRead);
        IReadTyped iReadTyped = PowerMockito.mock(IReadTyped.class);
        PowerMockito.when(iRead.resource(Practitioner.class)).thenReturn(iReadTyped);
        IReadExecutable iReadExecutable = PowerMockito.mock(IReadExecutable.class);
        PowerMockito.when(iReadTyped.withId(PRACTITIONER_ID)).thenReturn(iReadExecutable);
        PowerMockito.when(iReadExecutable.execute()).thenReturn(practitionerMock);

        Practitioner practitionerReal = fhirPractitionerInterface.queryById(PRACTITIONER_ID,Practitioner.class);
        Assert.assertEquals(practitionerReal, practitionerMock);
    }

    @Test
    public void givenAPractitionerIdWhenQueryThenThrowResourceNotFoundException() {
        PowerMockito.when(client.read()).thenThrow(ResourceNotFoundException.class);
        Practitioner practitioner = fhirPractitionerInterface.queryById(PRACTITIONER_ID,Practitioner.class);
        Assert.assertNull(practitioner);
    }

    @Test
    public void givenAPractitionerIdWhenQueryThenThrowException() {
        PowerMockito.when(client.read()).thenThrow(Exception.class);
        Practitioner practitioner = fhirPractitionerInterface.queryById(PRACTITIONER_ID,Practitioner.class);
        Assert.assertNull(practitioner);
    }

    @Test
    public void givenALoginIdWhenQueryThenReturnPractitioner() throws Exception {
        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Practitioner.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Practitioner.SP_LOGIN_ID).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);

        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockPractitionerUtil.givenAPractitionerBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        Practitioner practitioner = fhirPractitionerInterface.queryPractitionerByLoginId(LOGIN_ID);
        Assert.assertNotNull(practitioner);
    }

    @Test
    public void givenALoginIdWhenQueryThenThrowResourceNotFoundException() {
        PowerMockito.when(client.search()).thenThrow(ResourceNotFoundException.class);
        Practitioner practitioner = fhirPractitionerInterface.queryPractitionerByLoginId(LOGIN_ID);
        Assert.assertNull(practitioner);
    }

    @Test
    public void givenALoginIdWhenQueryThenThrowException() {
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        Practitioner practitioner = fhirPractitionerInterface.queryPractitionerByLoginId(LOGIN_ID);
        Assert.assertNull(practitioner);
    }

    @Test
    public void givenAGroupIdWhenQueryThenReturnMemberRefList() throws Exception {
        final String groupId = "GroupId";

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Group.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.include(Group.INCLUDE_MEMBER)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Group.SP_RES_ID).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(groupId)).thenReturn(iCriterion);

        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Group.class, Practitioner.class))).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockGroupUtil.givenAGroupBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Reference> lstGroupMemberRef = fhirPractitionerInterface.queryPractitionerListByGroupId(groupId);
        Assert.assertThat(1, is(lstGroupMemberRef.size()));
    }

    @Test
    public void givenAGroupIdWhenQueryThenThrowException() {
        final String groupId = "GroupId";
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        List<Reference> lstGroupMemberRef = fhirPractitionerInterface.queryPractitionerListByGroupId(groupId);
        Assert.assertThat(0, is(lstGroupMemberRef.size()));
    }
}