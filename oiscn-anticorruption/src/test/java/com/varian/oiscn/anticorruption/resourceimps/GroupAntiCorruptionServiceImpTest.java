package com.varian.oiscn.anticorruption.resourceimps;

/**
 * Created by fmk9441 on 2017-02-07.
 */

import com.varian.fhir.resources.Group;
import com.varian.oiscn.anticorruption.datahelper.MockGroupUtil;
import com.varian.oiscn.anticorruption.datahelper.MockPractitionerUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRGroupInterface;
import com.varian.oiscn.core.group.GroupDto;
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
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GroupAntiCorruptionServiceImp.class})
public class GroupAntiCorruptionServiceImpTest {
    private FHIRGroupInterface fhirGroupInterface;
    private GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirGroupInterface = PowerMockito.mock(FHIRGroupInterface.class);
        PowerMockito.whenNew(FHIRGroupInterface.class).withNoArguments().thenReturn(fhirGroupInterface);
        groupAntiCorruptionServiceImp = new GroupAntiCorruptionServiceImp();
    }

    @Test
    public void whenQueryThenReturnPhysicianGroups() throws Exception {
        List<Group> lstGroup = MockGroupUtil.givenAGroupList();
        PowerMockito.when(fhirGroupInterface.queryGroupByName()).thenReturn(lstGroup);
        List<GroupDto> lstGroupDto = groupAntiCorruptionServiceImp.queryAllPhysicianGroups();
        Assert.assertThat(1, is(lstGroupDto.size()));
    }

    @Test
    public void givenAResourceIDWhenQueryThenReturnGroupList() throws Exception {
        final String resourceID = "ResourceID";
        List<Group> lstGroup = MockGroupUtil.givenAGroupList();
        PowerMockito.when(fhirGroupInterface.queryGroupListByResourceID(anyString())).thenReturn(lstGroup);
        List<GroupDto> lstGroupDto = groupAntiCorruptionServiceImp.queryGroupListByResourceID(resourceID);
        Assert.assertThat(1, is(lstGroupDto.size()));
    }

    @Test
    public void givnAFuzzyGroupNameWhenQueryThenReturnGroupWithResourceIdListMap() {
        Map<Group, List<Reference>> hmGroupWithMemberRefList = MockPractitionerUtil.givenAMapofGroupWithMemberRefList();
        PowerMockito.when(fhirGroupInterface.queryGroupWithMemberRefListMap(anyString(), anyString())).thenReturn(hmGroupWithMemberRefList);
        Map<GroupDto, List<PractitionerDto>> hmGroupDtoWithPractitionerDtoList = groupAntiCorruptionServiceImp.queryGroupDtoWithResourceIdListMap("Oncologist", "1");
        Assert.assertThat(1, is(hmGroupDtoWithPractitionerDtoList.size()));
    }
}
