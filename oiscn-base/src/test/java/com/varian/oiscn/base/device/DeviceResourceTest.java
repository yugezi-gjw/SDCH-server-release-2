package com.varian.oiscn.base.device;

/**
 * Created by gbt1220 on 5/18/2017.
 */

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DeviceUtil;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.config.CarePathConfig;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.DeviceTimeSettingConfiguration;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.*;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.device.DeviceSettingView;
import com.varian.oiscn.core.device.DeviceTimeDto;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DeviceResource.class, DevicesReader.class,ActivityCodesReader.class,
        DeviceUtil.class,SystemConfigPool.class,PatientEncounterHelper.class})
public class DeviceResourceTest {
    private Configuration configuration;

    private Environment environment;

    private DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp;

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    private DeviceResource deviceResource;

    public static CarePathTemplate givenACarePath() {
        CarePathTemplate template = new CarePathTemplate();
        template.setId("1");
        template.setTemplateName("template");
        template.setStatus(CarePathStatusEnum.ACTIVE);
        template.setDepartmentID("1");

        PlannedActivity activity = new PlannedActivity();
        activity.setId("1");
        activity.setDepartmentID("1");
        activity.setDefaultGroupID("10013");
        activity.setActivityType(ActivityTypeEnum.TASK);
        activity.setActivityCode("PlaceOrder");

        PlannedActivity activity2 = new PlannedActivity();
        activity2.setId("2");
        activity2.setDepartmentID("1");
        activity2.setDefaultGroupID("10013");
        activity2.setActivityType(ActivityTypeEnum.TASK);
        activity2.setActivityCode("ScheduleImmobilization");
        activity2.setDeviceIDs(Arrays.asList("1"));

        PlannedActivity activity3 = new PlannedActivity();
        activity3.setId("3");
        activity3.setDepartmentID("1");
        activity3.setDefaultGroupID("10013");
        activity3.setActivityType(ActivityTypeEnum.TASK);
        activity3.setActivityCode("ScheduleCTSim");
        activity2.setDeviceIDs(Arrays.asList("2"));

        template.addPlannedActivity(activity);
        template.addPlannedActivity(activity2);
        template.addPlannedActivity(activity3);
        return template;
    }

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        deviceAntiCorruptionServiceImp = PowerMockito.mock(DeviceAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DeviceAntiCorruptionServiceImp.class).withNoArguments().thenReturn(deviceAntiCorruptionServiceImp);
        carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        deviceResource = new DeviceResource(configuration, environment);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.mockStatic(PatientEncounterHelper.class);
        PowerMockito.mockStatic(ActivityCodesReader.class);
    }

    @Test
    public void givenCarePathWhenDeviceIdsIsNullOfActivityCodeThenReturnNotFound() {
        String defaultTemplateName = "demo";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);
        PowerMockito.mockStatic(DeviceUtil.class);
        PowerMockito.when(DeviceUtil.getDevicesByActivityCode(anyString(), anyString())).thenReturn(new ArrayList<>());
        PowerMockito.when(configuration.getCarePathConfig()).thenReturn(new CarePathConfig(){{
            setCarePath(Arrays.asList(new CarePathConfigItem(){{
                setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
                setTemplateId("BCRITemplate");
                setDescription("BCRIDemo");
                setTemplateName("BCRIDemo");
            }}));
        }});
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode("not existed code")).thenReturn(new ActivityCodeConfig());
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn("DefaultTemplate");
        Response response = deviceResource.searchDevices(new UserContext(), "not existed code", null,null);
        Assert.assertEquals(response.getEntity(), new ArrayList<>());
    }

    @Test
    public void givenCarePathWhenQueryByActivityCodeThenReturnDevices() {
        PowerMockito.mockStatic(DevicesReader.class);
        PowerMockito.mockStatic(DeviceUtil.class);
        CarePathTemplate template = givenACarePath();
        String defaultTemplateName = "demo";
        PowerMockito.when(configuration.getDefaultCarePathTemplateName()).thenReturn(defaultTemplateName);

        PowerMockito.when(configuration.getCarePathConfig()).thenReturn(new CarePathConfig(){{
            setCarePath(Arrays.asList(new CarePathConfigItem(){{
                setCategory(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
                setTemplateId("BCRITemplate");
                setDescription("BCRIDemo");
                setTemplateName("BCRIDemo");
            }}));
        }});
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(defaultTemplateName)).
                thenReturn(template);
        List<String> deviceIds = Arrays.asList("2");
        PowerMockito.when(DeviceUtil.getDevicesByActivityCode(anyString(), anyString())).thenReturn(deviceIds);

        DeviceDto deviceDtoConfig = new DeviceDto() {{
            setId("2");
            setInterval("20");
            setTimeSlotList(Arrays.asList(new DeviceTimeDto("9:00", "12:00"),
                    new DeviceTimeDto("13:00", "18:00")));
        }};
        PowerMockito.when(DevicesReader.getDeviceTimeConfigureByCode("2")).thenReturn(deviceDtoConfig);
        PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByID("2")).thenReturn(new DeviceDto() {{
            setCode("2");
        }});
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode("ScheduleImmobilization")).thenReturn(new ActivityCodeConfig());
        Response response = deviceResource.searchDevices(new UserContext(), "ScheduleImmobilization", null,null);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenCarePathWhenQueryByActivityCodeAndHisIdThenReturnDevices() throws Exception {
        PowerMockito.mockStatic(DevicesReader.class);
         PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        CarePathInstance carePathInstance = MockDtoUtil.givenACarePathInstance();
        carePathInstance.getActivityInstances().get(0).setDeviceIDs(Arrays.asList("2"));
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(patientDto.getPatientSer())).thenReturn(carePathInstance);

        DeviceDto deviceDtoConfig = new DeviceDto() {{
            setId("2");
            setInterval("20");
            setTimeSlotList(Arrays.asList(new DeviceTimeDto("9:00", "12:00"),
                    new DeviceTimeDto("13:00", "18:00")));
        }};
        PowerMockito.when(DevicesReader.getDeviceTimeConfigureByCode("2")).thenReturn(deviceDtoConfig);
        PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByID("2")).thenReturn(new DeviceDto() {{
            setCode("2");
        }});
        PowerMockito.when(SystemConfigPool.queryTreatmentActivityCode()).thenReturn("aaaaa");
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode("activityCode1")).thenReturn(new ActivityCodeConfig());
        Response response = deviceResource.searchDevices(new UserContext(), "activityCode1", Long.parseLong(patientDto.getPatientSer()),null);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGivenActivityCodeAndPatientSerThenReturnDevices() throws Exception {
        Long patientSer= 11L;
        PowerMockito.mockStatic(DevicesReader.class);
        List<CarePathInstance> carePathInstanceList = Arrays.asList(MockDtoUtil.givenACarePathInstance());
        carePathInstanceList.get(0).getActivityInstances().get(0).setDeviceIDs(Arrays.asList("2"));
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(patientSer.toString())).thenReturn(carePathInstanceList);
        PatientEncounterCarePath patientEncounterCarePath = new PatientEncounterCarePath(){{
            setPlannedCarePath(new EncounterCarePathList(){{
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCpInstanceId(1L);
                }},new EncounterCarePath(){{
                    setCpInstanceId(2L);
                }}));
            }});
        }};
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode("doTreatment")).thenReturn(new ActivityCodeConfig(){{
            setName("activityCode1");
        }});
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(patientSer.toString())).thenReturn(patientEncounterCarePath);
        DeviceDto deviceDtoConfig = new DeviceDto() {{
            setId("2");
            setInterval("20");
            setTimeSlotList(Arrays.asList(new DeviceTimeDto("9:00", "12:00"),
                    new DeviceTimeDto("13:00", "18:00")));
        }};
        PowerMockito.when(DevicesReader.getDeviceTimeConfigureByCode("2")).thenReturn(deviceDtoConfig);
        PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByID("2")).thenReturn(new DeviceDto() {{
            setCode("2");
        }});
        PowerMockito.when(SystemConfigPool.queryTreatmentActivityCode()).thenReturn("doTreatment");
        Response response = deviceResource.searchDevices(new UserContext(), "doTreatment", patientSer,null);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }


    @Test
    public void getDefaultDeviceTimeSettingIfNullReturnNoContent() {
        Response response = deviceResource.getDeviceDefaultTimeSetting(new UserContext());
        DeviceTimeSettingConfiguration deviceTimeSettingConfiguration = (DeviceTimeSettingConfiguration) response.getEntity();
        assertThat(deviceTimeSettingConfiguration, equalTo(null));
        assertThat(response.getStatusInfo(), equalTo(Response.Status.NO_CONTENT));
    }


    @Test
    public void givenDeviceCodeWhenGetDeviceSettingByCodeThenReturnObject(){
        PowerMockito.mockStatic(DevicesReader.class);
        DeviceDto deviceDto = new DeviceDto(){{
            setId("23EX");
            setInterval("15");
            setTimeSlotList(Arrays.asList(new DeviceTimeDto("09:00","12:00"),new DeviceTimeDto("13:00","23:59")));
        }};
        PowerMockito.when(DevicesReader.getDeviceTimeConfigureByCode("23EX")).thenReturn(deviceDto);
        Response response = deviceResource.getDeviceSettingByCode(new UserContext(),"23EX");
        Assert.assertNotNull(response);
        DeviceSettingView deviceSettingView = (DeviceSettingView) response.getEntity();
        Assert.assertTrue(deviceSettingView.getCode().equals(deviceDto.getId()));

    }

}
