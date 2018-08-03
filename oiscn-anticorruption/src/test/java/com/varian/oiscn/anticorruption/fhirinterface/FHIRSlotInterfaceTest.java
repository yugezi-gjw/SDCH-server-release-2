package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import com.varian.fhir.resources.Slot;
import com.varian.oiscn.anticorruption.datahelper.MockSlotUtil;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import org.hl7.fhir.dstu3.model.Bundle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-02-17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRSlotInterface.class, FHIRContextFactory.class})
public class FHIRSlotInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRSlotInterface fhirSlotInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirSlotInterface = new FHIRSlotInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenADeviceIdAndDateWhenQueryThenReturnSlotList() throws Exception {
        final String deviceId = "DeviceId";
        final Date date = new Date();

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Slot.class)).thenReturn(iQuery);

        StringClientParam stringClientParamActor = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Slot.SP_ACTOR).thenReturn(stringClientParamActor);
        StringClientParam.IStringMatch iStringMatchActor = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamActor.matchesExactly()).thenReturn(iStringMatchActor);
        ICriterion iCriterionActor = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchActor.value(anyString())).thenReturn(iCriterionActor);
        PowerMockito.when(iQuery.where(iCriterionActor)).thenReturn(iQuery);

        StringClientParam stringClientParamDepartment = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Slot.SP_DEPARTMENT).thenReturn(stringClientParamDepartment);
        StringClientParam.IStringMatch iStringMatchDepartment = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamDepartment.matchesExactly()).thenReturn(iStringMatchDepartment);
        ICriterion iCriterionDepartment = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchDepartment.value(anyString())).thenReturn(iCriterionDepartment);
        PowerMockito.when(iQuery.and(iCriterionDepartment)).thenReturn(iQuery);

        DateClientParam dateClientParam = PowerMockito.mock(DateClientParam.class);
        PowerMockito.whenNew(DateClientParam.class).withArguments(Slot.SP_DATE_RANGE).thenReturn(dateClientParam);
        DateClientParam.IDateSpecifier iDateSpecifier = PowerMockito.mock(DateClientParam.IDateSpecifier.class);
        PowerMockito.when(dateClientParam.exactly()).thenReturn(iDateSpecifier);
        DateClientParam.IDateCriterion iDateCriterion = PowerMockito.mock(DateClientParam.IDateCriterion.class);
        PowerMockito.when(iDateSpecifier.day(date)).thenReturn(iDateCriterion);
        PowerMockito.when(iQuery.and(iDateCriterion)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockSlotUtil.givenASlotBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Slot> lstSlot = fhirSlotInterface.querySlotListByDeviceIdAndDate(deviceId, date);
        Assert.assertThat(1, is(lstSlot.size()));
    }

    @Test
    public void givenADeviceIdAndDateWhenQueryThenThrowException() throws Exception {
        final String deviceId = "DeviceId";
        final Date date = new Date();
        List<Slot> lstSlot = fhirSlotInterface.querySlotListByDeviceIdAndDate(deviceId, date);
        Assert.assertThat(0, is(lstSlot.size()));
    }
}