package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Group;
import com.varian.oiscn.anticorruption.assembler.GroupAssembler;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRGroupInterface;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceType;
import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;

/**
 * Created by fmk9441 on 2017-02-07.
 */
public class GroupAntiCorruptionServiceImp {
    private FHIRGroupInterface fhirGroupInterface;

    /**
     * Default Constructor.<br>
     */
    public GroupAntiCorruptionServiceImp() {
        fhirGroupInterface = new FHIRGroupInterface();
    }

    /**
     * Return All Physician Groups.<br>
     *
     * @return All Physician Groups
     */
    public List<GroupDto> queryAllPhysicianGroups(){
        List<Group> lstGroup = fhirGroupInterface.queryGroupByName();
        return getGroupDtoList(lstGroup);
    }

    /**
     * Return Physician Group DTO List.<br>
     * @param resourceID Resource Id
     * @return Group DTO List
     */
    public List<GroupDto> queryGroupListByResourceID(String resourceID) {
        List<Group> lstGroup = fhirGroupInterface.queryGroupListByResourceID(resourceID);
        return getGroupDtoList(lstGroup);
    }

    /**
     * Return Group DTO and Practitioner DTO List.<br>
     * @param fuzzyGroupName Fuzzy Group Name
     * @param departmentId Department Id
     * @return Group DTO and Practitioner DTO List
     */
    public Map<GroupDto, List<PractitionerDto>> queryGroupDtoWithResourceIdListMap(String fuzzyGroupName, String departmentId) {
        Map<GroupDto, List<PractitionerDto>> hmGroupDtoWithPractitionerDtoList = new HashMap<>();
        Map<Group, List<Reference>> hmGroupWithMemberRefList = fhirGroupInterface.queryGroupWithMemberRefListMap(fuzzyGroupName, departmentId);
        if (!hmGroupWithMemberRefList.isEmpty()) {
            for (Map.Entry<Group, List<Reference>> entry : hmGroupWithMemberRefList.entrySet()) {
                List<Reference> lstReference = entry.getValue();
                GroupDto groupDto = GroupAssembler.getGroupDto(entry.getKey());
                List<PractitionerDto> lstPractitionerDto = new ArrayList<>();
                if (null != lstReference && !lstReference.isEmpty()) {
                    lstPractitionerDto.addAll(lstReference.stream().map(r -> new PractitionerDto(getReferenceValue(r), r.getDisplay(), ParticipantTypeEnum.fromCode(getReferenceType(r)))).collect(Collectors.toList()));
                }
                hmGroupDtoWithPractitionerDtoList.put(groupDto, lstPractitionerDto);
            }
        }

        return hmGroupDtoWithPractitionerDtoList;
    }

    private List<GroupDto> getGroupDtoList(List<Group> lstGroup) {
        List<GroupDto> lstGroupDto = new ArrayList<>();
        if (!lstGroup.isEmpty()) {
            lstGroup.forEach(group -> lstGroupDto.add(GroupAssembler.getGroupDto(group)));
        }
        return lstGroupDto;
    }
}
