package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Group;
import com.varian.oiscn.anticorruption.datahelper.MockGroupUtil;
import com.varian.oiscn.core.group.GroupDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by fmk9441 on 2017-02-08.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GroupAssemblerTest.class})
public class GroupAssemblerTest {
    @InjectMocks
    private GroupAssembler groupAssembler;

    @Test
    public void givenAGroupWhenConvertThenReturnGroupDto() throws Exception {
        Group group = MockGroupUtil.givenAGroup();
        GroupDto groupDto = GroupAssembler.getGroupDto(group);
        Assert.assertEquals("GroupId", groupDto.getGroupId());
        Assert.assertEquals("GroupName", groupDto.getGroupName());
    }
}