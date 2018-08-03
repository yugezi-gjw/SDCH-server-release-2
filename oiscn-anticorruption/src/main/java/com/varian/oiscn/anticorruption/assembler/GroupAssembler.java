package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Group;
import com.varian.oiscn.core.group.GroupDto;

/**
 * Created by fmk9441 on 2017-02-08.
 */
public class GroupAssembler {
    private GroupAssembler() {}

    /**
     * Return DTO from Fhir Group.<br>
     *
     * @param group Fhir Group
     * @return DTO
     */
    public static GroupDto getGroupDto(Group group){
        GroupDto groupDto = new GroupDto();
        if (group != null) {
            groupDto.setGroupId(group.getIdElement().getIdPart());
            groupDto.setGroupName(group.getName());
        }
        return groupDto;
    }
}