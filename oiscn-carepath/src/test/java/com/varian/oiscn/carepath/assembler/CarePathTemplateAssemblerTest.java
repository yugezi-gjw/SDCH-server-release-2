package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.carepath.vo.ActivityEntryVO;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by gbt1220 on 4/23/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemConfigPool.class})
public class CarePathTemplateAssemblerTest {

    @Before
    public void setup() throws Exception {
        Locale.setDefault(Locale.CHINA);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryGroupRoleNurse()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupRoleOncologist()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupRolePhysicist()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");

        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(MockDtoUtil.givenAPractitionerGroupMap()));
    }

    @Test
    public void givenACarePathTemplateWhenAssemblerThenReturnActivityEntries() {
        CarePathTemplate template = givenACarePathTemplate();
        CarePathTemplateAssembler assembler = new CarePathTemplateAssembler(template);
        List<ActivityEntryVO> entries = assembler.getActivityEntries("2", SystemConfigPool.queryGroupRoleOncologist(), givenGroupList());
        Assert.assertEquals(5, entries.size());
    }

    @Test
    public void testTechnicianGroupCarePathTemplateWhenAssemblerThenReturnActivityEntries() {
        CarePathTemplate template = givenACarePathTemplate();
        CarePathTemplateAssembler assembler = new CarePathTemplateAssembler(template);
        List<ActivityEntryVO> entries = assembler.getActivityEntries("2", SystemConfigPool.queryGroupRoleTherapist(), givenGroupList());
        Assert.assertEquals(5, entries.size());
    }

    private CarePathTemplate givenACarePathTemplate() {
        return MockDtoUtil.givenCarePathTemplate();
    }

    private List<String> givenGroupList() {
        List<String> groupList = new ArrayList<>();
        groupList.add("10000");
        groupList.add("10013");
        groupList.add("2");
        groupList.add("4");
        return groupList;
    }
}
