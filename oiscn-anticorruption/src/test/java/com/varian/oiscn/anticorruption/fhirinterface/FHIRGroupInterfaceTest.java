package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.IUntypedQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Group;
import com.varian.oiscn.anticorruption.datahelper.MockGroupUtil;
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

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-02-08.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRGroupInterface.class, FHIRContextFactory.class})
public class FHIRGroupInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRGroupInterface fhirGroupInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirGroupInterface = new FHIRGroupInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void whenQueryThenReturnPhysicianGroups() throws Exception {
        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Group.class)).thenReturn(iQuery);

        StringClientParam stringClientParamName = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Group.SP_NAME).thenReturn(stringClientParamName);
        StringClientParam.IStringMatch iStringMatchName = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamName.matches()).thenReturn(iStringMatchName);
        ICriterion iCriterionName = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchName.value(anyString())).thenReturn(iCriterionName);
        PowerMockito.when(iQuery.where(iCriterionName)).thenReturn(iQuery);

        StringClientParam stringClientParamActive = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Group.SP_ACTIVE).thenReturn(stringClientParamActive);
        StringClientParam.IStringMatch iStringMatchActive = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamActive.matchesExactly()).thenReturn(iStringMatchActive);
        ICriterion iCriterionActive = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchActive.value(anyString())).thenReturn(iCriterionActive);
        PowerMockito.when(iQuery.and(iCriterionActive)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockGroupUtil.givenAGroupBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Group> lstGroup = fhirGroupInterface.queryGroupByName();
        Assert.assertThat(1, is(lstGroup.size()));
    }

    @Test
    public void whenQueryThenThrowException() {
        PowerMockito.when(client.search()).thenThrow(Exception.class);

        List<Group> lstGroup = fhirGroupInterface.queryGroupByName();
        Assert.assertTrue(lstGroup.isEmpty());
    }

    @Test
    public void givenAResourceIDWhenQueryThenReturnGroupList() throws Exception {
        final String resourceId = "ResourceID";

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Group.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Group.SP_MEMBER).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);

        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockGroupUtil.givenAGroupBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Group> lstGroup = fhirGroupInterface.queryGroupListByResourceID(resourceId);
        Assert.assertThat(1, is(lstGroup.size()));
    }

    @Test
    public void givenAResourceIDWhenQueryThenThrowException() {
        final String resourceId = "ResourceID";
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        List<Group> lstGroup = fhirGroupInterface.queryGroupListByResourceID(resourceId);
        Assert.assertTrue(lstGroup.isEmpty());
    }

    @Test
    public void givenAFuzzyGroupNameWhenQueryThenReturnGroupWithMemberRefListHashMap() throws Exception {
        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Group.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.include(Group.INCLUDE_MEMBER)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Group.SP_NAME).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matches()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);
        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);

        StringClientParam stringClientParamDepartment = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Group.SP_DEPARTMENT).thenReturn(stringClientParamDepartment);
        StringClientParam.IStringMatch iStringMatchDepartment = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamDepartment.matchesExactly()).thenReturn(iStringMatchDepartment);
        ICriterion iCriterionDepartment = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchDepartment.value(anyString())).thenReturn(iCriterionDepartment);
        PowerMockito.when(iQuery.and(iCriterionDepartment)).thenReturn(iQuery);

        StringClientParam stringClientParamActive = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Group.SP_ACTIVE).thenReturn(stringClientParamActive);
        StringClientParam.IStringMatch iStringMatchActive = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamActive.matchesExactly()).thenReturn(iStringMatchActive);
        ICriterion iCriterionActive = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchActive.value(anyString())).thenReturn(iCriterionActive);
        PowerMockito.when(iQuery.and(iCriterionActive)).thenReturn(iQuery);
        PowerMockito.when(iQuery.count(anyInt())).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockGroupUtil.givenAGroupBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        Map<Group, List<Reference>> hmGroupWithMemberRefList = fhirGroupInterface.queryGroupWithMemberRefListMap("Oncologist", "1");
        Assert.assertThat(1, is(hmGroupWithMemberRefList.size()));
    }

    @Test
    public void givenAFuzzyGroupNameWhenQueryGroupWithMemberRefListThenThrowException() {
        PowerMockito.when(client.search()).thenThrow(Exception.class);
        Map<Group, List<Reference>> hmGroupWithMemberRefList = fhirGroupInterface.queryGroupWithMemberRefListMap("Oncologist", "1");
        Assert.assertThat(0, is(hmGroupWithMemberRefList.size()));
    }
}