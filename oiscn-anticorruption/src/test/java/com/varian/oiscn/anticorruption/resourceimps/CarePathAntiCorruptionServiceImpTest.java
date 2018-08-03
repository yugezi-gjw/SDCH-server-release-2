package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.CarePath;
import com.varian.oiscn.anticorruption.datahelper.MockCarePathUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRCarePathInterface;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-04-25.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CarePathAntiCorruptionServiceImp.class)
public class CarePathAntiCorruptionServiceImpTest {
    private FHIRCarePathInterface fhirCarePathInterface;
    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirCarePathInterface = PowerMockito.mock(FHIRCarePathInterface.class);
        PowerMockito.whenNew(FHIRCarePathInterface.class).withNoArguments().thenReturn(fhirCarePathInterface);
        carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
    }

    @Test
    public void givenATemplateNameWhenQueryThenReturnCarePathTemplate() {
        final String templateName = "TemplateName";
        CarePath carePath = MockCarePathUtil.givenACarePath();
        PowerMockito.when(fhirCarePathInterface.queryCarePathByTemplateName(anyString())).thenReturn(carePath);
        CarePathTemplate carePathTemplate = carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(templateName);
        Assert.assertNotNull(carePathTemplate);
    }

    @Test
    public void givenAPatientIDWhenQueryThenReturnCarePathInstance() {
        final String patientID = "PatientID";
        CarePath carePath = MockCarePathUtil.givenACarePath();
        PowerMockito.when(fhirCarePathInterface.queryLastCarePathByPatientID(anyString())).thenReturn(carePath);
        CarePathInstance carePathInstance = carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(patientID);
        Assert.assertNotNull(carePathInstance);
    }

    @Test
    public void testQueryAllCarePathByPatientID() {
        final String patientID = "PatientID";
        CarePath carePath = MockCarePathUtil.givenACarePath();
        List<CarePath> carePathList = new ArrayList<>();
        carePathList.add(carePath);
        PowerMockito.when(fhirCarePathInterface.queryAllCarePathByPatientID(anyString())).thenReturn(carePathList);
        List<CarePathInstance> carePathInstanceList = carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(patientID);
        Assert.assertNotNull(carePathInstanceList);
        Assert.assertTrue(carePathInstanceList.size() > 0);
    }

    @Test
    public void givenAPatientIDListWhenQueryThenReturnCarePathInstanceList() {
        final List<String> patientIdList = Arrays.asList("PatientID");
        List<CarePath> lstCarePath = Arrays.asList(MockCarePathUtil.givenACarePath());
        PowerMockito.when(fhirCarePathInterface.queryAllCarePathByPatientIDList(anyObject())).thenReturn(lstCarePath);
        Map<String, List<CarePathInstance>> carePathInstanceHashMap = carePathAntiCorruptionServiceImp.queryCarePathListByPatientIDList(patientIdList);
        Assert.assertNotNull(carePathInstanceHashMap);
    }

    @Test
    public void givenACarePathIDAndActivityIDAndOrderDtoWhenScheduleNextTaskThenReturnSuccess() {
        final String carepathId = "CarePathID";
        final String activityInstanceID = "ActivityInstanceID";
        final OrderDto orderDto = new OrderDto();
        PowerMockito.when(fhirCarePathInterface.scheduleNextTask(anyString(), anyString(), anyObject())).thenReturn("1");
        String ret = carePathAntiCorruptionServiceImp.scheduleNextTask(carepathId, activityInstanceID, orderDto);
        Assert.assertNotNull(ret);
    }

    @Test
    public void givenACarePathIDAndActivityIDAndAppointmentDtoWhenScheduleNextAppointmentThenReturnSuccess() {
        final String carepathId = "CarePathID";
        final String activityInstanceID = "ActivityInstanceID";
        final AppointmentDto appointmentDto = new AppointmentDto(){{
            setParticipants(Arrays.asList(new ParticipantDto(){{
                setParticipantId("1291");
                setType(ParticipantTypeEnum.DEVICE);
            }}));
            setStartTime(new Date());
        }};
        PowerMockito.when(fhirCarePathInterface.scheduleNextAppointment(anyString(), anyString(), anyObject())).thenReturn("1");
        String ret = carePathAntiCorruptionServiceImp.scheduleNextAppointment(carepathId, activityInstanceID, appointmentDto);
        Assert.assertNotNull(ret);
    }

    @Test
    public void givenWhenAssignCarePathThenReturnTrue() {
        PowerMockito.doNothing().when(fhirCarePathInterface).assignCarePath(anyString(), anyString(), anyString());
        Assert.assertTrue(carePathAntiCorruptionServiceImp.assignCarePath("1", "1", "1"));
    }

    @Test
    public void testAssignCarePath() {
        PowerMockito.doThrow(new RuntimeException("Testing Exception")).when(fhirCarePathInterface).assignCarePath(anyString(), anyString(), anyString());
        Assert.assertFalse(carePathAntiCorruptionServiceImp.assignCarePath("1", "1", "1"));
    }

    @Test
    public void testQueryCarePathByPatientIDAndActivityInstanceIdAndActivityType() {
        CarePath carePath = MockCarePathUtil.givenACarePath();
        PowerMockito.when(fhirCarePathInterface.queryAllCarePathByPatientID(anyString())).thenReturn(Arrays.asList(carePath));
        Assert.assertEquals(carePath.getId(), carePathAntiCorruptionServiceImp
                .queryCarePathByPatientIDAndActivityInstanceIdAndActivityType("patientId", "1", ActivityTypeEnum.TASK).getId());
    }

    @Test
    public void testQueryLastCarePathByPatientIDAndActivityCode() {
        CarePath carePath = MockCarePathUtil.givenACarePath();
        PowerMockito.when(fhirCarePathInterface.queryAllCarePathByPatientID(anyString())).thenReturn(Arrays.asList(carePath));
        Assert.assertEquals(carePath.getId(), carePathAntiCorruptionServiceImp
                .queryLastCarePathByPatientIDAndActivityCode("patientId", "ActivityCode").getId());
    }

    @Test
    public void testLinkCarePathThrowException() {
        PowerMockito.when(fhirCarePathInterface.queryCarePathByTemplateName(anyString())).thenThrow(new RuntimeException());
        Assert.assertEquals(StringUtils.EMPTY, carePathAntiCorruptionServiceImp.linkCarePath("patient", "1", "BCRIStandard"));
    }

    @Test
    public void testLinkCarePath() {
        CarePath carePath = MockCarePathUtil.givenACarePath();
        PowerMockito.when(fhirCarePathInterface.queryCarePathByTemplateName(anyString())).thenReturn(carePath);
        PowerMockito.doNothing().when(fhirCarePathInterface).assignCarePath(anyString(), anyString(), anyString());
        PowerMockito.when(fhirCarePathInterface.queryAllCarePathByPatientID(anyString())).thenReturn(Arrays.asList(carePath));
        Assert.assertEquals(carePath.getId(), carePathAntiCorruptionServiceImp.linkCarePath("patient", "1", "BCRIStandard"));
    }

    @Test
    public void testQueryCarePathInstanceByInstanceId() {
        CarePath carePath = MockCarePathUtil.givenACarePath();
        PowerMockito.when(fhirCarePathInterface.queryById("1", CarePath.class)).thenReturn(carePath);
        Assert.assertEquals(carePath.getId(), carePathAntiCorruptionServiceImp.queryCarePathInstanceByInstanceId("1").getId());
    }
}