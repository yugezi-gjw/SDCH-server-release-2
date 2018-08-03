package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.datahelper.MockGroupUtil;
import com.varian.oiscn.anticorruption.datahelper.MockPractitionerUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRPractitionerInterface;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-02-08.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PractitionerAntiCorruptionServiceImp.class, FHIRPractitionerInterface.class})
public class PractitionerAntiCorruptionServiceImpTest {
    private FHIRPractitionerInterface fhirPractitionerInterface;
    private PractitionerAntiCorruptionServiceImp practitionerAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirPractitionerInterface = PowerMockito.mock(FHIRPractitionerInterface.class);
        PowerMockito.whenNew(FHIRPractitionerInterface.class).withNoArguments().thenReturn(fhirPractitionerInterface);
        practitionerAntiCorruptionServiceImp = new PractitionerAntiCorruptionServiceImp();
    }

    @Test
    public void givenAPractitionerIdWhenQueryThenReturnPractitioner() throws Exception {
        final String practitionerId = "PractitionerId";
        Practitioner practitioner = MockPractitionerUtil.givenAPractitioner();
        PowerMockito.whenNew(FHIRPractitionerInterface.class).withNoArguments().thenReturn(fhirPractitionerInterface);
        PowerMockito.when(fhirPractitionerInterface.queryById(anyString(),any())).thenReturn(practitioner);
        PractitionerDto practitionerDto = practitionerAntiCorruptionServiceImp.queryPractitionerById(practitionerId);
        Assert.assertEquals("PractitionerId", practitionerDto.getId());
        Assert.assertEquals("PractitionerName", practitionerDto.getName());
    }

    @Test
    public void givenALoginIdWhenQueryThenReturnPractitioner() {
        final String loginId = "LoginId";
        Practitioner practitioner = MockPractitionerUtil.givenAPractitioner();
        PowerMockito.when(fhirPractitionerInterface.queryPractitionerByLoginId(anyString())).thenReturn(practitioner);
        PractitionerDto practitionerDto = practitionerAntiCorruptionServiceImp.queryPractitionerByLoginId(loginId);
        Assert.assertEquals("PractitionerId", practitionerDto.getId());
        Assert.assertEquals("PractitionerName", practitionerDto.getName());
    }

    @Test
    public void givenAGroupIdWhenQueryThenReturnPractitionerDtoList() throws Exception {
        final String groupId = "GroupId";
        List<Reference> lstMemberRef = MockGroupUtil.givenAGroupMemberRefList();
        PowerMockito.when(fhirPractitionerInterface.queryPractitionerListByGroupId(groupId)).thenReturn(lstMemberRef);
        List<PractitionerDto> lstPractitionerDto = practitionerAntiCorruptionServiceImp.queryPractitionerDtoListByGroupId(groupId);
        Assert.assertThat(1, is(lstPractitionerDto.size()));
    }
}