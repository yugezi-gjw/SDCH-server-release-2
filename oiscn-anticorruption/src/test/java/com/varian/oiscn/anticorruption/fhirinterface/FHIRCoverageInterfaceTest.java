package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import com.varian.fhir.resources.Coverage;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IIdType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Created by bhp9696 on 2017/11/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRContextFactory.class,FHIRCoverageInterface.class,PaginationHelper.class})
public class FHIRCoverageInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRCoverageInterface fhirCoverageInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        PowerMockito.mockStatic(FHIRContextFactory.class);
        factory = PowerMockito.mock(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
        fhirCoverageInterface = new FHIRCoverageInterface();
        PowerMockito.mockStatic(PaginationHelper.class);
    }

    @Test
    public void givenCoverageWhenCreateThenReturnId(){
        Coverage coverage = PowerMockito.mock(Coverage.class);
        ICreate iCreate = PowerMockito.mock(ICreate.class);
        PowerMockito.when(client.create()).thenReturn(iCreate);
        ICreateTyped iCreateTyped = PowerMockito.mock(ICreateTyped.class);
        PowerMockito.when(iCreate.resource(coverage)).thenReturn(iCreateTyped);
        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iCreateTyped.execute()).thenReturn(methodOutcome);
        IIdType idType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(idType);
        PowerMockito.when(idType.getIdPart()).thenReturn("12");
        String rid = fhirCoverageInterface.create(coverage);
        Assert.assertTrue("12".equals(rid));
    }
    @Test
    public void givenCoverageWhenUpdateThenReturnId(){
        Coverage coverage = PowerMockito.mock(Coverage.class);
        IUpdate iUpdate = PowerMockito.mock(IUpdate.class);
        PowerMockito.when(client.update()).thenReturn(iUpdate);
        IUpdateTyped iUpdateTyped = PowerMockito.mock(IUpdateTyped.class);
        PowerMockito.when(iUpdate.resource(coverage)).thenReturn(iUpdateTyped);
        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iUpdateTyped.execute()).thenReturn(methodOutcome);
        IIdType idType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(idType);
        PowerMockito.when(idType.getIdPart()).thenReturn("12");
        String rid = fhirCoverageInterface.update(coverage);
        Assert.assertTrue("12".equals(rid));
    }

    @Test
    public void givenIdWhenQueryByIdThenReturnCoverage(){
        String id = "12";
        Coverage coverage = PowerMockito.mock(Coverage.class);
        IRead iRead = PowerMockito.mock(IRead.class);
        PowerMockito.when(client.read()).thenReturn(iRead);
        IReadTyped iReadTyped = PowerMockito.mock(IReadTyped.class);
        PowerMockito.when(iRead.resource(Coverage.class)).thenReturn(iReadTyped);
        IReadExecutable iReadExecutable = PowerMockito.mock(IReadExecutable.class);
        PowerMockito.when(iReadTyped.withId(id)).thenReturn(iReadExecutable);
        PowerMockito.when(iReadExecutable.execute()).thenReturn(coverage);
        Coverage r = fhirCoverageInterface.queryById(id,Coverage.class);
        Assert.assertNotNull(r);
        Assert.assertEquals(coverage,r);
    }

    @Test
    public void testQueryByPatientId() throws Exception {
        String id = "12";
        Coverage coverage = PowerMockito.mock(Coverage.class);
        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Coverage.class)).thenReturn(iQuery);
        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Coverage.SP_POLICY_HOLDER).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);
        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)).thenReturn(iQuery);
        org.hl7.fhir.dstu3.model.Bundle bundle = new org.hl7.fhir.dstu3.model.Bundle();
        bundle.addEntry().setResource(coverage);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        Assert.assertEquals(coverage, fhirCoverageInterface.queryByPatientId(id));
    }

    @Test
    public void givenPatientSerListWhenQueryCoveragePaginationByPatientSerListThenReturnPagination(){
        List<String> patientSerList = Arrays.asList("12","13");
        Coverage coverage = PowerMockito.mock(Coverage.class);
        IUntypedQuery iQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iQuery);
        IQuery<Bundle> bundleIQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iQuery.forResource(Coverage.class)).thenReturn(bundleIQuery);
        PowerMockito.when(bundleIQuery.where(any(ICriterion.class))).thenReturn(bundleIQuery);
        PowerMockito.when(bundleIQuery.preferResponseTypes(Arrays.asList(Coverage.class))).thenReturn(bundleIQuery);
        PowerMockito.when(bundleIQuery.count(Matchers.anyInt())).thenReturn(bundleIQuery);
        org.hl7.fhir.dstu3.model.Bundle bundle = PowerMockito.mock(org.hl7.fhir.dstu3.model.Bundle.class);
        PowerMockito.when(bundleIQuery.returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)).thenReturn(bundleIQuery);
        PowerMockito.when(bundleIQuery.execute()).thenReturn(bundle);
        int pageNumberTo = 10;
        org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent bundleEntryComponent = PowerMockito.mock(org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent.class);
        PowerMockito.when(PaginationHelper.queryPagingBundle(client,bundle,1, pageNumberTo, Arrays.asList(Coverage.class))).thenReturn(Arrays.asList(bundleEntryComponent));
        PowerMockito.when(bundle.hasEntry()).thenReturn(true);
        PowerMockito.when(bundle.getEntry()).thenReturn(Arrays.asList(bundleEntryComponent));
        PowerMockito.when(bundleEntryComponent.hasResource()).thenReturn(true);
        PowerMockito.when(bundleEntryComponent.getResource()).thenReturn(coverage);
        PowerMockito.when(bundle.getTotal()).thenReturn(1);
        Pagination<Coverage> pagination = fhirCoverageInterface.queryCoveragePaginationByPatientSerList(patientSerList,10,pageNumberTo);
        Assert.assertNotNull(pagination);
        Assert.assertTrue(pagination.getTotalCount() == 1);
    }

}
