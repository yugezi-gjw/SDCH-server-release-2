package com.varian.oiscn.base.group;

import com.varian.oiscn.anticorruption.resourceimps.GroupAntiCorruptionServiceImp;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.I18nReader;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by gbt1220 on 2/7/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GroupResource.class, SystemConfigPool.class, I18nReader.class})
public class GroupResourceTest {
    private GroupResource groupResource;

    private Configuration configuration;

    private Environment environment;

    private GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        groupAntiCorruptionServiceImp = PowerMockito.mock(GroupAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(GroupAntiCorruptionServiceImp.class).withNoArguments().thenReturn(groupAntiCorruptionServiceImp);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryGroupRoleNurse()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupRoleOncologist()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupRolePhysicist()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");

        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");
        groupResource = new GroupResource(configuration, environment);
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(MockDtoUtil.givenAPractitionerGroupMap()));

    }

    @Test
    public void givenWhenQueryThenReturnAllPhysicianGroups() {
        List<GroupDto> groupsList = new ArrayList<>();
        groupsList.add(new GroupDto("2", "Head"));
        groupsList.add(new GroupDto("5", "Chest"));
        groupsList.add(new GroupDto("8", "Oncologist_EmptyPractitionerList"));
        groupsList.add(new GroupDto("9", "Oncologist_NullPractitionerList"));

        Response response = groupResource.queryAllPhysicianGroups(new UserContext());
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        List<GroupDto> groupDtoList = (List<GroupDto>) response.getEntity();
        boolean swapped = true;
        int j = 0;
        GroupDto groupDtoTemp;
        while (swapped) {
            swapped = false;
            j++;
            for (int i = 0; i < groupDtoList.size() - j; i++) {
                if (Integer.parseInt(groupDtoList.get(i).getGroupId()) > Integer.parseInt(groupDtoList.get(i + 1).getGroupId())) {
                    groupDtoTemp = groupDtoList.get(i);
                    groupDtoList.set(i, groupDtoList.get(i + 1));
                    groupDtoList.set(i + 1, groupDtoTemp);
                    swapped = true;
                }
            }
        }
        assertThat(response.getEntity(), is(groupDtoList));
    }

    @Test
    public void givenNoGroupNameWithOneUnderscoreThenReturnEmptyPhysicianGroups() {
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(givenAllPractitionerGroupsWithNoOneUnderscoreInName()));
        PowerMockito.mockStatic(I18nReader.class);
        PowerMockito.when(I18nReader.getLocaleValueByKey("Oncologist.DisplayName.NoSecondaryGroup")).thenReturn("AllOncologistGroup");
        Response response = groupResource.queryAllPhysicianGroups(new UserContext());
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenEmptyGroupPractitionerMapThenReturnInternalServerError() {
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(new HashMap<>()));
        Response response = groupResource.queryAllPhysicianGroups(new UserContext());
        assertThat(response.getStatusInfo(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
    }

    private Map<GroupDto, List<PractitionerDto>> givenAllPractitionerGroupsWithNoOneUnderscoreInName() {
        Map<GroupDto, List<PractitionerDto>> groupDtoListMap = new HashMap<>();
        groupDtoListMap.put(new GroupDto("1", "Oncologist"), new ArrayList<>());
        groupDtoListMap.put(new GroupDto("2", "Oncologist_Head_HeadA"), new ArrayList<>());
        groupDtoListMap.put(new GroupDto("3", "Oncologist_Chest_ChestA"), new ArrayList<>());
        return groupDtoListMap;
    }
}
