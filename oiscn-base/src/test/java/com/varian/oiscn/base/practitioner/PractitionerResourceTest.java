package com.varian.oiscn.base.practitioner;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PractitionerAntiCorruptionServiceImp;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.config.CarePathConfig;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.CarePathConfigItem;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import com.varian.oiscn.core.carepath.PlannedActivity;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;

/**
 * Created by gbt1220 on 2/8/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PractitionerResource.class, SystemConfigPool.class, CarePathAntiCorruptionServiceImp.class,SystemConfigPool.class})
public class PractitionerResourceTest {

    private PractitionerResource resource;

    private Configuration configuration;

    private Environment environment;

    private PractitionerAntiCorruptionServiceImp practitionerAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn("standardTPL"); PowerMockito.when(configuration.getCarePathConfig()).thenReturn(new CarePathConfig(){{
            setCarePath(Arrays.asList(new CarePathConfigItem(){{
                setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
                setTemplateId("BCRITemplate");
                setDescription("BCRIDemo");
                setTemplateName("BCRIDemo");
            }}));
        }});
        environment = PowerMockito.mock(Environment.class);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryGroupRoleNurse()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupRoleOncologist()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupRolePhysicist()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupRoleTherapist()).thenReturn("Therapist");

        PowerMockito.when(SystemConfigPool.queryGroupNursePrefix()).thenReturn("Nurse");
        PowerMockito.when(SystemConfigPool.queryGroupOncologistPrefix()).thenReturn("Oncologist");
        PowerMockito.when(SystemConfigPool.queryGroupPhysicistPrefix()).thenReturn("Physicist");
        PowerMockito.when(SystemConfigPool.queryGroupTechnicianPrefix()).thenReturn("Technician");
        practitionerAntiCorruptionServiceImp = PowerMockito.mock(PractitionerAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PractitionerAntiCorruptionServiceImp.class).withNoArguments().thenReturn(practitionerAntiCorruptionServiceImp);
        resource = new PractitionerResource(configuration, environment);
        GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(MockDtoUtil.givenAPractitionerGroupMap()));
    }

    @Test
    public void givenGroupIdWhenQueryPractitionerThenReturnValidPractitionerByTheGroup() {
        List<PractitionerDto> practitioners = new ArrayList<>();
        practitioners.add(new PractitionerDto("1", "BossHead1", ParticipantTypeEnum.PRACTITIONER));
        practitioners.add(new PractitionerDto("2", "Head2", ParticipantTypeEnum.PRACTITIONER));
        practitioners.add(new PractitionerDto("4", "HeadA2", ParticipantTypeEnum.PRACTITIONER));
        practitioners.add(new PractitionerDto("5", "HeadA3", ParticipantTypeEnum.PRACTITIONER));
        practitioners.add(new PractitionerDto("6", "HeadB1", ParticipantTypeEnum.PRACTITIONER));
        practitioners.add(new PractitionerDto("7", "HeadB2", ParticipantTypeEnum.PRACTITIONER));

        Response response = resource.queryPhysiciansByGroupId(MockDtoUtil.givenUserContext(), "2");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        List<PractitionerDto> practitionerDtoList = (List<PractitionerDto>) response.getEntity();
        boolean swapped = true;
        int j = 0;
        PractitionerDto practitionerDtoTemp;
        while (swapped) {
            swapped = false;
            j++;
            for (int i = 0; i < practitionerDtoList.size() - j; i++) {
                if (Integer.parseInt(practitionerDtoList.get(i).getId()) > Integer.parseInt(practitionerDtoList.get(i + 1).getId())) {
                    practitionerDtoTemp = practitionerDtoList.get(i);
                    practitionerDtoList.set(i, practitionerDtoList.get(i + 1));
                    practitionerDtoList.set(i + 1, practitionerDtoTemp);
                    swapped = true;
                }
            }
        }
        assertThat(response.getEntity(), is(practitionerDtoList));
    }

    @Test
    public void givenEmptyOrNullPractitionerListThenReturnEmptyPractitionerDtoList() {
        List<PractitionerDto> practitioners = new ArrayList<>();
        Response response = resource.queryPhysiciansByGroupId(MockDtoUtil.givenUserContext(), "8");
        assertThat(response.getEntity(), is(practitioners));
        response = resource.queryPhysiciansByGroupId(MockDtoUtil.givenUserContext(), "9");
        assertThat(response.getEntity(), is(practitioners));
    }

    @Test
    public void givenPractitionerIdThenReturnRegisterGroup(){
        Response response = resource.queryRegisterGroupByPractitionerId(MockDtoUtil.givenUserContext(), "11");
        GroupDto groupDto = (GroupDto) response.getEntity() ;
        Assert.assertEquals("5", groupDto.getGroupId());
    }

    @Test
    public void givenLoginUserPrimaryPhysicianPatientIdThenReturnFirstActivity() throws Exception{
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        Long patientId = 121212L;
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(String.valueOf(patientId))).thenReturn(MockDtoUtil.givenACarePathInstance());
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(anyString())).thenReturn(new ArrayList<>());
        Login login = new Login();
        login.setStaffGroups(Arrays.asList("1"));
        UserContext userContext = new UserContext(login, null);
        FirstActivityVO firstActivityVO = new FirstActivityVO();
        firstActivityVO.setRedirectedToFirstActivity(true);
        firstActivityVO.setPatientSer(String.valueOf(patientId));
        firstActivityVO.setActivityId("1");
        firstActivityVO.setActivityInstanceId("1");
        firstActivityVO.setActivityType(ActivityTypeEnum.TASK.name());
        firstActivityVO.setActivityCode("activityCode1");

        Response response = resource.checkIfLoginUserCanGoToFirstActivity(userContext, "primaryPhysicianId", patientId);
        Assert.assertEquals(firstActivityVO, response.getEntity());
    }
    
    @Test
    public void testCheckIfLoginUserCanGoToFirstActivity() throws Exception{
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        Long patientId = 1212L;
        CarePathInstance cpInstance = MockDtoUtil.givenACarePathInstance();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(Mockito.anyString())).thenReturn(cpInstance);
        CarePathTemplate template = new CarePathTemplate();
        PlannedActivity plannedActivity = new PlannedActivity();
        plannedActivity.setActivityCode("activityCode1");
        plannedActivity.setAutoAssignPrimaryOncologist(Boolean.TRUE);
		template.addPlannedActivity(plannedActivity);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(Matchers.anyString())).thenReturn(template);
        
        PowerMockito.mockStatic(SystemConfigPool.class);
                
        List<String> tplList = new ArrayList<>();
        tplList.add("tpl1");
        tplList.add("tpl2");
        tplList.add("standardTPL");
		PowerMockito.when(SystemConfigPool.queryConfigValueByName(anyString())).thenReturn(tplList);
        Login login = new Login();
        login.setStaffGroups(Arrays.asList("1"));
        login.setResourceSer(1000L);
        UserContext userContext = new UserContext(login, null);
        FirstActivityVO firstActivityVO = new FirstActivityVO();
        firstActivityVO.setRedirectedToFirstActivity(true);
        firstActivityVO.setPatientSer(String.valueOf(patientId));
        firstActivityVO.setActivityId("1");
        firstActivityVO.setActivityInstanceId("1");
        firstActivityVO.setActivityType(ActivityTypeEnum.TASK.name());
        firstActivityVO.setActivityCode("activityCode1");

        Response response = resource.checkIfLoginUserCanGoToFirstActivity(userContext, "primaryPhysicianId", patientId);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }
    
    @Test
    public void testCheckIfLoginUserCanGoToFirstActivityOK2() throws Exception{
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        Long patientId = 1212L;
        CarePathInstance cpInstance = MockDtoUtil.givenACarePathInstance();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(Mockito.anyString())).thenReturn(cpInstance);
        CarePathTemplate template = new CarePathTemplate();
        PlannedActivity plannedActivity = new PlannedActivity();
        plannedActivity.setActivityCode("activityCode1");
        plannedActivity.setAutoAssignPrimaryOncologist(Boolean.TRUE);
		template.addPlannedActivity(plannedActivity);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(Matchers.anyString())).thenReturn(template);
        
        PowerMockito.mockStatic(SystemConfigPool.class);
                
        List<String> tplList = new ArrayList<>();
        tplList.add("tpl1");
        tplList.add("tpl2");
        tplList.add("standardTPL");
		PowerMockito.when(SystemConfigPool.queryConfigValueByName(anyString())).thenReturn(tplList);
        Login login = new Login();
        login.setStaffGroups(Arrays.asList("1"));
        login.setResourceSer(1L);
        UserContext userContext = new UserContext(login, null);
        FirstActivityVO firstActivityVO = new FirstActivityVO();
        firstActivityVO.setRedirectedToFirstActivity(true);
        firstActivityVO.setPatientSer(String.valueOf(patientId));
        firstActivityVO.setActivityId("1");
        firstActivityVO.setActivityInstanceId("1");
        firstActivityVO.setActivityType(ActivityTypeEnum.TASK.name());
        firstActivityVO.setActivityCode("activityCode1");

        GroupTreeNode rootGroupTreeNode = new GroupTreeNode("nodeId", "nodeName","nodeName");
		GroupPractitionerHelper.setOncologyGroupTreeNode(rootGroupTreeNode);
		PractitionerTreeNode practitionerTreeNode = new PractitionerTreeNode("1", "PractitionerTreeNode1", ParticipantTypeEnum.PRACTITIONER);
		rootGroupTreeNode.addAPractitioner(practitionerTreeNode);
		
        Response response = resource.checkIfLoginUserCanGoToFirstActivity(userContext, "1", patientId);
        
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }
}
