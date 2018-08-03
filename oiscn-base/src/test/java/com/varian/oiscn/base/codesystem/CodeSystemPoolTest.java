package com.varian.oiscn.base.codesystem;

import com.varian.oiscn.anticorruption.resourceimps.GroupAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.ValueSetAntiCorruptionServiceImp;
import com.varian.oiscn.base.coverage.PayorInfoPool;
import com.varian.oiscn.base.group.GroupInfoPool;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

/**
 * Created by gbt1220 on 6/19/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CodeSystemPool.class, SystemConfigPool.class, GroupAntiCorruptionServiceImp.class, GroupPractitionerHelper.class})
public class CodeSystemPoolTest {

    @InjectMocks
    private CodeSystemPool codeSystemPool;

    private ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp;

    private CodeSystemServiceImp codeSystemServiceImp;

    @Before
    public void setup() throws Exception {
        valueSetAntiCorruptionServiceImp = PowerMockito.mock(ValueSetAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(ValueSetAntiCorruptionServiceImp.class).withNoArguments().thenReturn(valueSetAntiCorruptionServiceImp);
        codeSystemServiceImp = PowerMockito.mock(CodeSystemServiceImp.class);
        PowerMockito.whenNew(CodeSystemServiceImp.class).withNoArguments().thenReturn(codeSystemServiceImp);
    }

    @Test
    public void givenASchemeWhenDiagnosisNotNullThenDoNothing() {
        String scheme = "scheme";
        PowerMockito.when(codeSystemServiceImp.isDiagnosisExisted(scheme)).thenReturn(true);
        CodeSystemPool.initDiagnosis(scheme, "ENU");
    }

    @Test
    public void givenASchemeWhenDiagnosisIsNullThenQueryFromFhir() {
        String scheme = "scheme";
        String language = "ENU";
        PowerMockito.when(codeSystemServiceImp.isDiagnosisExisted(scheme)).thenReturn(false);

        CodeSystem codeSystem = new CodeSystem();
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryDiagnosisListByScheme(scheme, language)).thenReturn(codeSystem);
        PowerMockito.doNothing().when(codeSystemServiceImp).create(codeSystem);
        CodeSystemPool.initDiagnosis(scheme, language);
    }

    @Test
    public void givenWhenPatientStatusIconIsNotNullThenInitStatusIcon() {
        CodeSystem codeSystem = new CodeSystem();
        codeSystem.addCodeValue(new CodeValue("1", "Urgent"));
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryAllPatientStatusIcons()).thenReturn(codeSystem);
        CodeSystemPool.initStatusIcon();
    }

    @Test
    public void givenWhenPatientLabelIsNotNullThenInitPatientLabel() {
        CodeSystem codeSystem = new CodeSystem();
        codeSystem.addCodeValue(new CodeValue("1", "Alert"));
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryAllPatientLabels()).thenReturn(codeSystem);
        CodeSystemPool.initPatientLabel();
    }

    @Test
    public void testCreateBodyParts(){
        CodeSystem codeSystem = new CodeSystem();
        try {
            codeSystem.setCodeValues(Arrays.asList(new CodeValue("1","121212")));
            CodeSystemServiceImp codeSystemServiceImp = PowerMockito.mock(CodeSystemServiceImp.class);
            PowerMockito.whenNew(CodeSystemServiceImp.class).withAnyArguments().thenReturn(codeSystemServiceImp);
            PowerMockito.doNothing().when(codeSystemServiceImp).createBodyParts(Matchers.anyList());
            PowerMockito.when(valueSetAntiCorruptionServiceImp.queryAllPrimarySites("CHS")).thenReturn(codeSystem);
            CodeSystemPool.initBodyPart("CHS");
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void initCarePath() {
        CodeSystem codeSystem = new CodeSystem();
        codeSystem.addCodeValue(new CodeValue("BCRI", "BCRI.template descrption"));
        codeSystem.addCodeValue(new CodeValue("SD", "Shandong 's description"));
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryAllCarePathTemplates()).thenReturn(codeSystem);
        CodeSystemPool.initCarePath();

        List<CodeValue> carePathList = CodeSystemPool.getCarePathList();
        Assert.assertNotNull(carePathList);
        Assert.assertEquals(2, carePathList.size());
    }
    
    @Test
    public void initGroupPractitionerListMap() {
    	List<String> defaultDepartmentIds = new ArrayList<>();
    	defaultDepartmentIds.add("test");
    	
    	try {
			PowerMockito.mockStatic(SystemConfigPool.class);
			PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.DEFAULT_DEPARTMENT)).thenReturn(defaultDepartmentIds);
			
			GroupAntiCorruptionServiceImp imp = PowerMockito.mock(GroupAntiCorruptionServiceImp.class);
			PowerMockito.whenNew(GroupAntiCorruptionServiceImp.class).withNoArguments().thenReturn(imp);

			Map<GroupDto, List<PractitionerDto>> map = PowerMockito.mock(HashMap.class);
			PowerMockito.when(imp.queryGroupDtoWithResourceIdListMap("Oncologist", "test")).thenReturn(map);
			PowerMockito.verifyStatic();
			GroupPractitionerHelper.convertMapToTree(Matchers.any());
			GroupPractitionerHelper.setOncologyGroupTreeNode(Matchers.any());
		} catch (Exception e) {
			Assert.fail();
		}
    }

    @Test
    public void testInitGroupPractitionerListMap() throws Exception{
        PowerMockito.mockStatic(SystemConfigPool.class);
        List<String> defaultDepartmentIds = Arrays.asList("1");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.DEFAULT_DEPARTMENT)).thenReturn(defaultDepartmentIds);
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");
        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp = PowerMockito.mock(GroupAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(GroupAntiCorruptionServiceImp.class).withNoArguments().thenReturn(groupAntiCorruptionServiceImp);
        Map<GroupDto, List<PractitionerDto>> groupDtoWithResourceIdListMap = new HashMap<>();
        PowerMockito.when(groupAntiCorruptionServiceImp.queryGroupDtoWithResourceIdListMap(Matchers.anyString(), Matchers.anyString())).thenReturn(groupDtoWithResourceIdListMap);
        CodeSystemPool.initGroupPractitionerListMap();
        Assert.assertNull(GroupPractitionerHelper.getNurseGroupTreeNode());
        Assert.assertNull(GroupPractitionerHelper.getOncologyGroupTreeNode());
        Assert.assertNull(GroupPractitionerHelper.getPhysicistGroupTreeNode());
        Assert.assertNull(GroupPractitionerHelper.getTechGroupTreeNode());
    }

    @Test
    public void testInitPhysicistGroupInfoMap() throws Exception{
        PowerMockito.mockStatic(SystemConfigPool.class);
        List<String> defaultDepartmentIds = Arrays.asList("1");
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.DEFAULT_DEPARTMENT)).thenReturn(defaultDepartmentIds);
        GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp = PowerMockito.mock(GroupAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(GroupAntiCorruptionServiceImp.class).withNoArguments().thenReturn(groupAntiCorruptionServiceImp);
        Map<GroupDto, List<PractitionerDto>> groupDtoWithResourceIdListMap = new HashMap<>();
        groupDtoWithResourceIdListMap.put(new GroupDto("10000", "Physicist"), Arrays.asList(new PractitionerDto("id1", "user1", null)));
        PowerMockito.when(groupAntiCorruptionServiceImp.queryGroupDtoWithResourceIdListMap(Matchers.anyString(), Matchers.anyString())).thenReturn(groupDtoWithResourceIdListMap);
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        CodeSystemPool.initPhysicistGroupInfoMap();
        Assert.assertEquals("Physicist", GroupInfoPool.getValue("10000"));
    }

    @Test
    public void testInitPayorInfos() throws Exception{
        ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp = PowerMockito.mock(ValueSetAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(ValueSetAntiCorruptionServiceImp.class).withNoArguments().thenReturn(valueSetAntiCorruptionServiceImp);
        CodeSystem codeSystem = new CodeSystem();
        List<CodeValue> codeValues = new ArrayList<>();
        CodeValue codeValue = new CodeValue("code", "desc");
        codeValues.add(codeValue);
        codeSystem.setCodeValues(codeValues);
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryAllPayorInfo()).thenReturn(codeSystem);
        CodeSystemPool.initPayorInfos();
        Assert.assertEquals("desc", PayorInfoPool.getValue("code"));
    }
}
