package com.varian.oiscn.appointment.resource;

import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.appointment.calling.*;
import com.varian.oiscn.appointment.dto.CheckInStatusEnum;
import com.varian.oiscn.appointment.dto.QueueListAssembler;
import com.varian.oiscn.appointment.dto.QueuingManagementDTO;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.service.QueuingManagementServiceImpl;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.appointment.util.MockDtoUtil;
import com.varian.oiscn.appointment.view.AppointmentListVO;
import com.varian.oiscn.appointment.view.AppointmentListVOAssembler;
import com.varian.oiscn.appointment.view.AppointmentVO;
import com.varian.oiscn.appointment.view.AutoCheckInVO;
import com.varian.oiscn.appointment.vo.DeviceScheduleViewVO;
import com.varian.oiscn.appointment.vo.QueueListVO;
import com.varian.oiscn.appointment.vo.QueuingManagementVO;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfiguration;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.config.CarePathConfig;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.activity.ActivityCodeEnum;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.appointment.calling.*;
import com.varian.oiscn.core.carepath.*;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.resource.BaseResponse;
import io.dropwizard.setup.Environment;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Appointment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;

/**
 * Created by gbt1220 on 2/23/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AppointmentResource.class, SystemConfigPool.class,
        BasicDataSourceFactory.class, CallingService.class,
        ActivityCodesReader.class, PatientEncounterHelper.class,
        CallingGuideHelper.class, DevicesReader.class, HisPatientInfoConfigService.class})
@PowerMockIgnore({"javax.crypto.*"})
public class AppointmentResourceTest {
    private Configuration configuration;

    private Environment environment;

    private AppointmentResource resource;

    private AppointmentAntiCorruptionServiceImp antiCorruptionServiceImp;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;
    private TreatmentAppointmentService treatmentAppointmentService;

    private QueuingManagementServiceImpl queuingManagementService;

    private EncounterServiceImp encounterServiceImp;

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    private Date startTime;

    private Date endTime;

    private PatientCacheService patientCacheService;
    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        startTime = new Date();
        endTime = new Date();
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        antiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withNoArguments().thenReturn(antiCorruptionServiceImp);
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        patientCacheService = PowerMockito.mock(PatientCacheService.class);
        PowerMockito.whenNew(PatientCacheService.class).withAnyArguments().thenReturn(patientCacheService);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        resource = new AppointmentResource(configuration, environment);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL)).thenReturn(Arrays.asList("true"));

        treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
        PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(treatmentAppointmentService);

        queuingManagementService = PowerMockito.mock(QueuingManagementServiceImpl.class);
        PowerMockito.whenNew(QueuingManagementServiceImpl.class).withAnyArguments().thenReturn(queuingManagementService);

        PowerMockito.mockStatic(ActivityCodesReader.class);

        encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.when(encounterServiceImp.queryByPatientSer(Matchers.anyLong())).thenReturn(new Encounter() {{
            setId("1213");
        }});
        PowerMockito.mockStatic(DevicesReader.class);
    }

    @Test
    public void givenAnAppointmentWhenSaveThenReturnResponseCreated() {
        AppointmentDto dto = givenAnAppointment();
        String appointmentId = "createdAppointmentId";
        PowerMockito.when(antiCorruptionServiceImp.createAppointment(dto)).thenReturn(appointmentId);

        Response response = resource.createAppointment(new UserContext(), dto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.CREATED));
        assertThat(response.getEntity(), is(appointmentId));
    }

    @Test
    public void givenAnAppointmentWhenFhirSaveFailThenReturnResponseInternalError() {
        AppointmentDto dto = givenAnAppointment();
        String appointmentId = StringUtils.EMPTY;
        PowerMockito.when(antiCorruptionServiceImp.createAppointment(dto)).thenReturn(appointmentId);

        Response response = resource.createAppointment(new UserContext(), dto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
        assertThat(response.getEntity(), is(appointmentId));
    }

    @Test
    public void givenWhenPatientIdIsEmptyThenReturnResponseNotFound() {
        Response response = resource.searchAppointments(new UserContext(), "", "", "", "", null);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void givenPatientIdWhenFhirNotFoundAppointmentsThenReturnResponseNotFound() {
        Long patientId = 1212L;
        String deviceId = "deviceId";
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        Pagination<AppointmentDto> pagination = new Pagination<>();
        pagination.setLstObject(appointmentDtos);
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(String.valueOf(patientId), DateUtil.getCurrentDate(), DateUtil.getCurrentDate(), Arrays.asList(
                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), 30, 1, Integer.MAX_VALUE)).thenReturn(pagination);
        Response response = resource.searchAppointments(new UserContext(), deviceId, "", "", "", patientId);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void givenPatientIdWhenFhirResultNotIncludePatientInfoThenReturnResponseNotFound() {
        Long patientId = 121212L;
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        appointmentDtos.add(givenAnAppointment());
        Pagination<AppointmentDto> pagination = new Pagination<>();
        pagination.setLstObject(appointmentDtos);

        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(String.valueOf(patientId), DateUtil.getCurrentDate(), DateUtil.getCurrentDate(), Arrays.asList(
                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), 30, 1, Integer.MAX_VALUE)).thenReturn(pagination);

        Response response = resource.searchAppointments(new UserContext(), "", "", "", "", patientId);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void testSearchByPatient() {
        AppointmentDto dto = givenAnAppointment();
        Long patientSer = 1921L;
        String deviceId = "deviceId";
        List<AppointmentDto> dtoList = new ArrayList<>();
        dtoList.add(dto);

        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
        List<TreatmentAppointmentDTO> taList = new ArrayList<>();
        TreatmentAppointmentDTO taDto = new TreatmentAppointmentDTO();
        taDto.setHisId("setHisId");
        taDto.setAppointmentId("setAppointmentId");
        taDto.setStartTime(new Date(System.currentTimeMillis()));
        taDto.setEndTime(new Date(System.currentTimeMillis()+10000));
        taList.add(taDto);
        PowerMockito.when(treatmentAppointmentService.queryAppointmentListByPatientSerAndDeviceId(patientSer, deviceId)).thenReturn(taList);
        PowerMockito.when(antiCorruptionServiceImp.queryByPatientIdAndDeviceId(patientSer.toString(), deviceId)).thenReturn(dtoList);

        Response response = resource.searchByPatient(new UserContext(), patientSer, deviceId);

        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        List resp = (List) response.getEntity();
        assertThat(resp.size(), is(2));
    }

    @Test
    public void searchByPatientBadRequest() {
        Response response = resource.searchByPatient(new UserContext(), null, "");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        response = resource.searchByPatient(new UserContext(), null, "activityCode");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenWhenSearchByPatientIdThenReturnAllAppointmentListWithThePatient() throws Exception {
        Long patientSer = 121212L;
        String participantId = "participantId";
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        AppointmentDto dto = givenAnAppointment();
        dto.getParticipants().add(new ParticipantDto(ParticipantTypeEnum.PATIENT, participantId));
        appointmentDtos.add(dto);
        Pagination<AppointmentDto> pagination = new Pagination<>();
        pagination.setLstObject(appointmentDtos);
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(String.valueOf(patientSer), DateUtil.getCurrentDate(), DateUtil.getCurrentDate(), Arrays.asList(
                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), 30, 1, Integer.MAX_VALUE)).thenReturn(pagination);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer(String.valueOf(patientSer));

        AppointmentListVOAssembler assembler = PowerMockito.mock(AppointmentListVOAssembler.class);
        PowerMockito.whenNew(AppointmentListVOAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getViewDto()).thenReturn(new AppointmentListVO());

        Response response = resource.searchAppointments(new UserContext(), "", DateUtil.getCurrentDate(), DateUtil.getCurrentDate(), "", patientSer);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenWhenOrderIdIsEmptyThenReturnResponseNotFound() {
        String orderId = "";
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId("")).thenReturn(new PatientDto());
        Response response = resource.searchAppointments(new UserContext(), "", "", "", orderId, null);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void givenOrderIdWhenFhirNotFoundAppointmentsThenReturnResponseNotFound() {
        String orderId = "orderId";
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentByOrderId(orderId)).thenReturn(appointmentDtos);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId("")).thenReturn(new PatientDto());
        Response response = resource.searchAppointments(new UserContext(), "", "", "", orderId, null);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void givenOrderIdWhenFhirResultNotIncludePatientInfoThenReturnResponseNotFound() {
        String orderId = "orderId";
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        appointmentDtos.add(givenAnAppointment());
        appointmentDtos.get(0).setParticipants(new ArrayList<>());
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentByOrderId(orderId)).thenReturn(appointmentDtos);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId("")).thenReturn(new PatientDto());
        Response response = resource.searchAppointments(new UserContext(), "", "", "", orderId, null);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void givenWhenSearchByOrderIdThenReturnAllAppointmentListWithTheOrder() throws Exception {
        String orderId = "orderId";
        String participantId = "participantId";
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        AppointmentDto dto = givenAnAppointment();
        dto.setParticipants(new ArrayList<>());
        dto.getParticipants().add(new ParticipantDto(ParticipantTypeEnum.PATIENT, participantId));
        appointmentDtos.add(dto);
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentByOrderId(orderId)).thenReturn(appointmentDtos);
        PatientDto patientDto = new PatientDto();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(participantId)).thenReturn(patientDto);

        AppointmentListVOAssembler assembler = PowerMockito.mock(AppointmentListVOAssembler.class);
        PowerMockito.whenNew(AppointmentListVOAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getViewDto()).thenReturn(new AppointmentListVO());

        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId("")).thenReturn(new PatientDto());

        PowerMockito.when(patientCacheService.queryPatientByPatientId(participantId)).thenReturn(patientDto);

        Response response = resource.searchAppointments(new UserContext(), "", "", "", orderId, null);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenWhenDeviceIdIsEmptyThenReturnResponseNotFound() {
        String deviceId = "";
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId("")).thenReturn(new PatientDto());
        Response response = resource.searchAppointments(new UserContext(), deviceId, "", "", "", null);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void givenWhenSearchByDeviceIdThenReturnAllAppointmentListWithTheDevice() throws Exception {
        String deviceId = "deviceId";
        String participantId = "participantId";
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        AppointmentDto dto = givenAnAppointment();
        dto.getParticipants().add(new ParticipantDto(ParticipantTypeEnum.PATIENT, participantId));
        appointmentDtos.add(dto);
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDateRangeAndStatus(deviceId,
                DateUtil.getCurrentDate(), DateUtil.getCurrentDate(), Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED)))).thenReturn(appointmentDtos);
        PatientDto patientDto = new PatientDto();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(participantId)).thenReturn(patientDto);

        AppointmentListVOAssembler assembler = PowerMockito.mock(AppointmentListVOAssembler.class);
        PowerMockito.whenNew(AppointmentListVOAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getViewDto()).thenReturn(new AppointmentListVO());

        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId("")).thenReturn(new PatientDto());
        Response response = resource.searchAppointments(new UserContext(), deviceId, "", "", "", null);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenNullPatientIdWhenSearchByPatientThenReturnNodFound() {
        Response response = resource.searchAppointmentsByPatientId(new UserContext(), null);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void givenPatientIdwhenSearchByPatientAndFhirNodFoundThenReturnNodFound() {
        Long patientId = 1212L;
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientId(String.valueOf(patientId))).thenReturn(new ArrayList<>());
        Response response = resource.searchAppointmentsByPatientId(new UserContext(), patientId);
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void givenPatientIdwhenSearchByPatientThenReturnAppointmentsWithThePatient() {
        Long patientId = 121212L;
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        appointmentDtos.add(givenAnAppointment());
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientId(String.valueOf(patientId))).thenReturn(appointmentDtos);
        Response response = resource.searchAppointmentsByPatientId(new UserContext(), patientId);
        AppointmentVO result = ((List<AppointmentVO>) response.getEntity()).get(0);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        Assert.assertEquals(appointmentDtos.get(0).getReason(), result.getActivityType());
        Assert.assertEquals(DateUtil.formatDate(startTime, DateUtil.SHORT_DATE_TIME_FORMAT) + "-" +
                        DateUtil.formatDate(endTime, DateUtil.HOUR_MINUTE_TIME_FORMAT),
                result.getScheduleTime());
    }

    @Test
    public void givenEmptyIdwhenUpdateAppointmentThenReturnBadRequestResponse() {
        Response response = resource.updateAppointment(new UserContext(), "", new AppointmentDto());
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void testAppointmentStartTimeBeforeNowDateThenReturnOkResponse() throws ParseException {
        String id = "updateId";
        Date startTime = DateUtil.parse("2018-05-09 10:00:00");
        Date endTime = DateUtil.parse("2018-05-09 10:15:00");
        AppointmentDto updateDto = new AppointmentDto(){{
            setStartTime(startTime);
            setEndTime(endTime);
            setAppointmentId(id);
        }};
        AppointmentDto needupdate = new AppointmentDto(){{
            setAppointmentId(id);
            setParticipants(Arrays.asList(new ParticipantDto(){{
                setParticipantId("1212");
                setType(ParticipantTypeEnum.PATIENT);
            }},new ParticipantDto(){{
                setParticipantId("1234");
                setType(ParticipantTypeEnum.DEVICE);
            }}));
            setReason("DoCT");
        }};
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentById(id)).thenReturn(needupdate);
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(Matchers.anyList(),Matchers.anyString(),Matchers.any(),Matchers.any(),Matchers.anyList()))
        .thenReturn(new ArrayList());
        PowerMockito.when(antiCorruptionServiceImp.queryAllByPatientSer(Matchers.anyString())).thenReturn(new ArrayList<AppointmentDto>());

        PowerMockito.when(antiCorruptionServiceImp.updateAppointment(updateDto)).thenReturn(id);
        Response response = resource.updateAppointment(new UserContext(), id, updateDto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }


    @Test
    public void testAppointmentWhenUpdateAppointmentSlotFilledOutOfRangeSlotLimit() throws ParseException {
        String id = "updateId";
        java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
        Date time = DateUtil.addDay(new Date(),1);
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Date startTime = calendar.getTime();
        calendar.set(Calendar.MINUTE,10);
        Date endTime =  calendar.getTime();

        AppointmentDto updateDto = new AppointmentDto(){{
            setStartTime(startTime);
            setEndTime(endTime);
            setAppointmentId(id);
        }};
        AppointmentDto needupdate = new AppointmentDto(){{
            setAppointmentId(id);
            setParticipants(Arrays.asList(new ParticipantDto(){{
                setParticipantId("12126");
                setType(ParticipantTypeEnum.PATIENT);
            }},new ParticipantDto(){{
                setParticipantId("12346");
                setType(ParticipantTypeEnum.DEVICE);
            }}));
            setReason("DoCT");
        }};
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentById(id)).thenReturn(needupdate);
        List<AppointmentDto> appointmentDtoList = Arrays.asList(new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }},new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }},new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }});
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(Matchers.anyList(),Matchers.anyString(),Matchers.any(),Matchers.any(),Matchers.anyList()))
                .thenReturn(appointmentDtoList);

        PowerMockito.when(SystemConfigPool.queryTimeSlotCount()).thenReturn("3");
        Response response = resource.updateAppointment(new UserContext(), id, updateDto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        BaseResponse baseResponse = (BaseResponse) response.getEntity();
        Assert.assertTrue(baseResponse.getStatus().equals("99"));
    }


   @Test
    public void testWhenUpdateFulfilledAppointment() throws ParseException {
        String id = "updateId";
        java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
        Date time = DateUtil.addDay(new Date(),1);
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Date startTime = calendar.getTime();
        calendar.set(Calendar.MINUTE,10);
        Date endTime =  calendar.getTime();

        AppointmentDto updateDto = new AppointmentDto(){{
            setStartTime(startTime);
            setEndTime(endTime);
            setAppointmentId(id);
            setReason("DoCT");
        }};
        AppointmentDto needupdate = new AppointmentDto(){{
            setAppointmentId(id);
            setParticipants(Arrays.asList(new ParticipantDto(){{
                setParticipantId("12124");
                setType(ParticipantTypeEnum.PATIENT);
            }},new ParticipantDto(){{
                setParticipantId("12344");
                setType(ParticipantTypeEnum.DEVICE);
            }}));
            setReason("DoCT");
        }};
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentById(id)).thenReturn(needupdate);
        List<AppointmentDto> appointmentDtoList = Arrays.asList(new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }},new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }});
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(Matchers.anyList(),Matchers.anyString(),Matchers.any(),Matchers.any(),Matchers.anyList()))
                .thenReturn(appointmentDtoList);
       List<AppointmentDto> list = new ArrayList();
       list.add(new AppointmentDto(){{
           setStatus(AppointmentStatusEnum.FULFILLED.name());
           setAppointmentId(id);
           setReason(updateDto.getReason());
       }});
        PowerMockito.when(antiCorruptionServiceImp.queryAllByPatientSer(Matchers.anyString())).thenReturn(list);

        PowerMockito.when(SystemConfigPool.queryTimeSlotCount()).thenReturn("3");
        PowerMockito.when(antiCorruptionServiceImp.updateAppointment(updateDto)).thenReturn(id);

       PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(Matchers.anyString(),Matchers.anyString(),Matchers.any()))
               .thenReturn(new CarePathInstance(){{
                   setActivityInstances(Arrays.asList(new ActivityInstance(){{
                       setActivityType(ActivityTypeEnum.APPOINTMENT);
                       setInstanceID(id);
                       setNextActivities(Arrays.asList("1"));
                   }}));
               }});

        Response response = resource.updateAppointment(new UserContext(), id, updateDto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        BaseResponse baseResponse = (BaseResponse) response.getEntity();
        Assert.assertTrue(baseResponse.getStatus().equals("99"));
    }

    @Test
    public void testWhenUpdateBookedAppointment() throws ParseException {
        String id = "updateId";
        java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
        Date time = DateUtil.addDay(new Date(),1);
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Date startTime = calendar.getTime();
        calendar.set(Calendar.MINUTE,10);
        Date endTime =  calendar.getTime();

        AppointmentDto updateDto = new AppointmentDto(){{
            setStartTime(startTime);
            setEndTime(endTime);
            setAppointmentId(id);
            setReason("DoCT");
        }};
        AppointmentDto needupdate = new AppointmentDto(){{
            setAppointmentId(id);
            setParticipants(Arrays.asList(new ParticipantDto(){{
                setParticipantId("12124");
                setType(ParticipantTypeEnum.PATIENT);
            }},new ParticipantDto(){{
                setParticipantId("12344");
                setType(ParticipantTypeEnum.DEVICE);
            }}));
            setReason("DoCT");
        }};
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentById(id)).thenReturn(needupdate);
        List<AppointmentDto> appointmentDtoList = Arrays.asList(new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }},new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }});
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(Matchers.anyList(),Matchers.anyString(),Matchers.any(),Matchers.any(),Matchers.anyList()))
                .thenReturn(appointmentDtoList);
        PowerMockito.when(antiCorruptionServiceImp.queryAllByPatientSer(Matchers.anyString())).thenReturn(Arrays.asList(new AppointmentDto(){{
            setStatus(AppointmentStatusEnum.BOOKED.name());
            setAppointmentId(id);
            setReason(updateDto.getReason());
            setStartTime(new Date(startTime.getTime()-2000));
            setEndTime(new Date(endTime.getTime()-2000));
        }},new AppointmentDto(){{
            setStatus(AppointmentStatusEnum.BOOKED.name());
            setAppointmentId(id);
            setReason(updateDto.getReason());
            setStartTime(new Date(startTime.getTime()));
            setEndTime(new Date(endTime.getTime()));
        }}));
        PowerMockito.when(ActivityCodesReader.getActivityCode(updateDto.getReason())).thenReturn(new ActivityCodeConfig(){{
            setContent("DoCT");
        }});
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode(updateDto.getReason())).thenReturn(new ActivityCodeConfig(){{
            setContent("DoImmo");
        }});
        PowerMockito.when(SystemConfigPool.queryTimeSlotCount()).thenReturn("3");
        PowerMockito.when(antiCorruptionServiceImp.updateAppointment(updateDto)).thenReturn(id);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(Matchers.anyString(),Matchers.anyString(),Matchers.any()))
                .thenReturn(new CarePathInstance(){{
                    setActivityInstances(Arrays.asList(new ActivityInstance(){{
                        setActivityType(ActivityTypeEnum.APPOINTMENT);
                        setInstanceID(id);
                    }}));
                }});


        Response response = resource.updateAppointment(new UserContext(), id, updateDto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        BaseResponse baseResponse = (BaseResponse) response.getEntity();
        Assert.assertTrue(baseResponse.getStatus().equals("99"));
    }

    @Test
    public void testWhenUpdateBookedAppointment2() throws ParseException {
        String id = "updateId";
        java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
        Date time = DateUtil.addDay(new Date(),1);
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Date startTime = calendar.getTime();
        calendar.set(Calendar.MINUTE,10);
        Date endTime =  calendar.getTime();

        AppointmentDto updateDto = new AppointmentDto(){{
            setStartTime(startTime);
            setEndTime(endTime);
            setAppointmentId(id);
            setReason("DoCT");
        }};
        AppointmentDto needupdate = new AppointmentDto(){{
            setAppointmentId(id);
            setParticipants(Arrays.asList(new ParticipantDto(){{
                setParticipantId("12124");
                setType(ParticipantTypeEnum.PATIENT);
            }},new ParticipantDto(){{
                setParticipantId("12344");
                setType(ParticipantTypeEnum.DEVICE);
            }}));
            setReason("DoCT");
        }};
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentById(id)).thenReturn(needupdate);
        List<AppointmentDto> appointmentDtoList = Arrays.asList(new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }},new AppointmentDto(){{
            setStartTime(new Date(startTime.getTime()+1000));
            setEndTime(new Date(endTime.getTime()-1000));
        }});
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(Matchers.anyList(),Matchers.anyString(),Matchers.any(),Matchers.any(),Matchers.anyList()))
                .thenReturn(appointmentDtoList);
        PowerMockito.when(antiCorruptionServiceImp.queryAllByPatientSer(Matchers.anyString())).thenReturn(Arrays.asList(new AppointmentDto(){{
            setStatus(AppointmentStatusEnum.BOOKED.name());
            setAppointmentId(id);
            setReason(updateDto.getReason());
            setStartTime(new Date(startTime.getTime()-2000));
            setEndTime(new Date(endTime.getTime()-2000));
        }},new AppointmentDto(){{
            setStatus(AppointmentStatusEnum.BOOKED.name());
            setAppointmentId(id);
            setReason(updateDto.getReason());
            setStartTime(new Date(endTime.getTime()+10000));
            setEndTime(new Date(endTime.getTime()+15000));
        }}));
        PowerMockito.when(ActivityCodesReader.getActivityCode(updateDto.getReason())).thenReturn(new ActivityCodeConfig(){{
            setContent("DoCT");
        }});
        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode(updateDto.getReason())).thenReturn(new ActivityCodeConfig(){{
            setContent("DoImmo");
        }});
        PowerMockito.when(SystemConfigPool.queryTimeSlotCount()).thenReturn("3");
        PowerMockito.when(antiCorruptionServiceImp.updateAppointment(updateDto)).thenReturn(id);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(Matchers.anyString(),Matchers.anyString(),Matchers.any()))
                .thenReturn(new CarePathInstance(){{
                    setActivityInstances(Arrays.asList(new ActivityInstance(){{
                        setActivityType(ActivityTypeEnum.APPOINTMENT);
                        setInstanceID(id);
                    }}));
                }});

        Response response = resource.updateAppointment(new UserContext(), id, updateDto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        BaseResponse baseResponse = (BaseResponse) response.getEntity();
        Assert.assertTrue(baseResponse.getStatus().equals("99"));
    }


    @Test
    public void givenActivityCodeWhenSearchQueueThenReturnOk() throws Exception {
        QueuingManagementServiceImpl service = PowerMockito.mock(QueuingManagementServiceImpl.class);
        PowerMockito.whenNew(QueuingManagementServiceImpl.class).withNoArguments().thenReturn(service);
        List<QueuingManagementVO> queue = new ArrayList<>();
        QueuingManagementVO qmVo = createQueuingManagementVO();
        testGetMethod(qmVo);
        queue.add(qmVo);
        PowerMockito.when(service.queryCheckInList(Matchers.any())).thenReturn(queue);
        List<QueueListVO> queueList = new ArrayList<>();
        QueueListVO vo = createVoWithData();
        testGetMethod(vo);
        queueList.add(vo);
        QueueListAssembler assembler = PowerMockito.mock(QueueListAssembler.class);
        PowerMockito.whenNew(QueueListAssembler.class).withAnyArguments().thenReturn(assembler);
        PowerMockito.when(assembler.getQueue()).thenReturn(queueList);

        Response response = resource.searchQueue(new UserContext(), "activityCode", "deviceId");
        Assert.assertEquals(queueList, response.getEntity());
        testGetMethod(vo);
    }

    @Test
    public void testCalling2waiting() throws Exception {
        QueuingManagementServiceImpl service = PowerMockito.mock(QueuingManagementServiceImpl.class);
        PowerMockito.whenNew(QueuingManagementServiceImpl.class).withNoArguments().thenReturn(service);
        PowerMockito.when(service.checkInStick(Matchers.any())).thenReturn(true);
        Response response = resource.calling2waiting(new UserContext(),new QueuingManagementDTO(){{
            setId("1212");
            setCheckInIdx(1);
        }});
        Assert.assertTrue((Boolean) response.getEntity());
    }

    @Test
    public void testCallingNext() throws Exception {
        QueuingManagementServiceImpl service = PowerMockito.mock(QueuingManagementServiceImpl.class);
        PowerMockito.whenNew(QueuingManagementServiceImpl.class).withNoArguments().thenReturn(service);
        PowerMockito.when(service.callingNext(Matchers.anyList())).thenReturn(true);
        Response response = resource.callingNextPatient(new UserContext(),Arrays.asList(new QueuingManagementDTO()));
        Assert.assertTrue((Boolean) response.getEntity());
    }

    private void testGetMethod(QueuingManagementVO qmVo){
        Assert.assertNotNull(qmVo.getStartTime());
        Assert.assertNotNull(qmVo.getPatientSer());
        Assert.assertNotNull(qmVo.getActivityCode());
        Assert.assertNotNull(qmVo.getAppointmentId());
        Assert.assertNotNull(qmVo.getCheckInStatus());
        Assert.assertNotNull(qmVo.getCheckInIdx());
        Assert.assertNotNull(qmVo.getDeviceId());
        Assert.assertNotNull(qmVo.getHisId());
        Assert.assertNotNull(qmVo.getId());
    }

    private void testGetMethod(QueueListVO vo){
        Assert.assertNotNull(vo.getActivityCode());
        Assert.assertNotNull(vo.getActivityGroupId());
        Assert.assertNotNull(vo.getActivityId());
        Assert.assertNotNull(vo.getActivityType());
        Assert.assertNotNull(vo.getAge());
        Assert.assertNotNull(vo.getAriaId());
        Assert.assertNotNull(vo.getBirthday());
        Assert.assertNotNull(vo.getCheckInIdx());
        Assert.assertNotNull(vo.getCheckInStatus());
        Assert.assertNotNull(vo.getContactPerson());
        Assert.assertNotNull(vo.getContactPhone());
        Assert.assertNotNull(vo.getEnglishName());
        Assert.assertNotNull(vo.getGender());
        Assert.assertNotNull(vo.getHisId());
        Assert.assertNotNull(vo.getInstanceId());
        Assert.assertNotNull(vo.getInsuranceType());
        Assert.assertNotNull(vo.getModuleId());
        Assert.assertNotNull(vo.getNationalId());
        Assert.assertNotNull(vo.getPatientSer());
        Assert.assertNotNull(vo.getPatientSource());
        Assert.assertNotNull(vo.getPhysicianComment());
        Assert.assertNotNull(vo.getPhysicianGroupId());
        Assert.assertNotNull(vo.getPhysicianId());
        Assert.assertNotNull(vo.getPhysicianName());
        Assert.assertNotNull(vo.getPreActivityCompletedTime());
        Assert.assertNotNull(vo.getPreActivityName());
        Assert.assertNotNull(vo.getProgressState());
        Assert.assertNotNull(vo.getScheduleTime());
        Assert.assertNotNull(vo.getStatus());
        Assert.assertNotNull(vo.getStartTime());
        Assert.assertNotNull(vo.getTelephone());
        Assert.assertNotNull(vo.getWarningText());
        Assert.assertNotNull(vo.getWorkspaceType());
    }
    @Test
    public void givenQueuingManagementDTOWhenManualCheckInWaitingThenResponseTrue() {
        UserContext userContext = new UserContext();
        QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO();
        PowerMockito.when(queuingManagementService.checkIn(queuingManagementDTO)).thenReturn(true);
        Response response = resource.manualCheckInWaiting(userContext, queuingManagementDTO);
        Assert.assertTrue((Boolean) response.getEntity());
    }

    @Test
    public void givenQueuingManagementDTOWhenStickCheckInWaitingThenReturnTrue() {
        UserContext userContext = new UserContext();
        QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO();
        queuingManagementDTO.setCheckInIdx(11);
        queuingManagementDTO.setAppointmentId("1212");
        queuingManagementDTO.setActivityCode("DoCT");
        PowerMockito.when(queuingManagementService.checkInStick(queuingManagementDTO)).thenReturn(true);
        Response response = resource.stickCheckInWaiting(userContext, queuingManagementDTO);
        Assert.assertTrue((Boolean) response.getEntity());
    }

    @Test
    public void givenAppointmentIdsWhenSearchAppointmentStatusByIdThenReturnStatus() throws Exception {
        UserContext userContext = new UserContext();
        String appointmentId = "1212";
        AppointmentDto appointmentDto = new AppointmentDto() {{
            setAppointmentId(appointmentId);
            setStatus(Appointment.AppointmentStatus.BOOKED.getDisplay());
        }};
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentById(appointmentId)).thenReturn(appointmentDto);
        Response response = resource.searchAppointmentStatusById(userContext, Arrays.asList(new KeyValuePair(appointmentId, appointmentId)));
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertTrue(((List<KeyValuePair>) response.getEntity()).get(0).getValue().equals(Appointment.AppointmentStatus.BOOKED.getDisplay()));

        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
        TreatmentAppointmentService treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
        PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(treatmentAppointmentService);
        TreatmentAppointmentDTO treatmentAppointmentDTO = new TreatmentAppointmentDTO(){{
            setAppointmentId(appointmentId);
        }};
        PowerMockito.when(treatmentAppointmentService.queryByUidOrAppointmentId(appointmentId)).thenReturn(treatmentAppointmentDTO);
        response = resource.searchAppointmentStatusById(userContext, Arrays.asList(new KeyValuePair(appointmentId, appointmentId)));
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertTrue(((List<KeyValuePair>) response.getEntity()).get(0).getValue().equals(Appointment.AppointmentStatus.BOOKED.getDisplay()));

        treatmentAppointmentDTO = new TreatmentAppointmentDTO(){{
            setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
        }};
        PowerMockito.when(treatmentAppointmentService.queryByUidOrAppointmentId(appointmentId)).thenReturn(treatmentAppointmentDTO);
        response = resource.searchAppointmentStatusById(userContext, Arrays.asList(new KeyValuePair(appointmentId, appointmentId)));
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertTrue(((List<KeyValuePair>) response.getEntity()).get(0).getValue().equals(Appointment.AppointmentStatus.BOOKED.getDisplay()));

        PowerMockito.when(treatmentAppointmentService.queryByUidOrAppointmentId(appointmentId)).thenReturn(null);
        response = resource.searchAppointmentStatusById(userContext, Arrays.asList(new KeyValuePair(appointmentId, appointmentId)));
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertTrue(((List<KeyValuePair>) response.getEntity()).get(0).getValue().equals(Appointment.AppointmentStatus.BOOKED.getDisplay()));

    }

    @Test
    public void testCallPatientAndCallingSystemDisable() {
        PowerMockito.mockStatic(HisPatientInfoConfigService.class);
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(null);
        UserContext userContext = new UserContext();
        CallPatientVO vo = new CallPatientVO() {{
            setAppointmentId("1212");
        }};
        Response response = resource.callPatient(userContext, vo);
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertSame(ServerStatusEnum.NO_CONFIGURATION, response.getEntity());


        HisPatientInfoConfiguration configuration = PowerMockito.mock(HisPatientInfoConfiguration.class);
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(configuration);
        PowerMockito.when(configuration.isCallingSystemEnable()).thenReturn(false);
        response = resource.callPatient(userContext, vo);
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertSame(ServerStatusEnum.NO_CONFIGURATION, response.getEntity());
    }

    @Test
    public void testCallPatient() {
        UserContext userContext = new UserContext();
        CallPatientVO vo = new CallPatientVO(){{
            setAppointmentId("1212");
        }};
        vo.setPatientName(Arrays.asList("张三", "李四", "王五"));
        PowerMockito.mockStatic(CallingService.class);
        ServerStatusEnum status = ServerStatusEnum.NO_CONFIGURATION;
        PowerMockito.when(CallingService.sendMsg(Mockito.anyObject())).thenReturn(status);
        String deviceRoom = PowerMockito.mock(String.class);
        PowerMockito.when(CallingService.getDeviceRoomByAriaDeviceId(Mockito.anyObject())).thenReturn(deviceRoom);
        String ariaDeviceId = PowerMockito.mock(String.class);
        PowerMockito.when(CallingService.getCallingDeviceIdByAriaDeviceId(Mockito.anyObject())).thenReturn(ariaDeviceId);
        String callingDeviceId = PowerMockito.mock(String.class);
        PowerMockito.when(CallingService.getCallingDeviceIdByAriaDeviceId(Mockito.anyObject())).thenReturn(callingDeviceId);

        PowerMockito.when(CallingService.sendMsg(Mockito.anyObject())).thenReturn(status);
        PowerMockito.when(CallingService.isReady()).thenReturn(true);
        DeviceGuide deviceGuide = new DeviceGuide(){{
            setTexts(Arrays.asList("张三","李四"));
            setImageUrls(Arrays.asList("http://varian.com/image/img1.gif"));
            setVideoUrls(Arrays.asList("http://video.varain.com"));
        }};
        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(false);
        AppointmentDto dto = new AppointmentDto(){{
            setAppointmentId(vo.getAppointmentId());
            setParticipants(Arrays.asList(new ParticipantDto(){{
                setType(ParticipantTypeEnum.DEVICE);
                setParticipantId("121");
            }}));
        }};
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentById(vo.getAppointmentId())).thenReturn(dto);
        PowerMockito.mockStatic(CallingGuideHelper.class);
        PowerMockito.when(CallingGuideHelper.getDeviceGuideByAriaDeviceId(configuration,"121")).thenReturn(deviceGuide);
        List<String> list = vo.getPatientName();
        Assert.assertTrue(list.size() == 3);
        Response response = resource.callPatient(userContext, vo);

        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertSame(status, response.getEntity());
    }

    @Test
    public void testGetPatientGuide() {
        List<String> textList = new ArrayList<>();
        textList.add("text01");
        textList.add("text02");

        List<String> imageList = new ArrayList<>();
        imageList.add("image01");
        imageList.add("image02");

        List<String> videoList = new ArrayList<>();
        videoList.add("video01");
        videoList.add("video02");
        DeviceGuide deviceGuide = new DeviceGuide();
        deviceGuide.setTexts(textList);
        deviceGuide.setImageUrls(imageList);
        deviceGuide.setVideoUrls(videoList);
        PowerMockito.mockStatic(CallingGuideHelper.class);
        PowerMockito.when(CallingGuideHelper.getDeviceGuideByAriaDeviceId(configuration, "appointmentId")).thenReturn(deviceGuide);
        CallingGuide result = resource.getPatientGuide("appointmentId");
        Assert.assertNotNull(result.toString());
    }

    @Test
    public void givenAppointmentIdWhenUnCheckInFromWaitingListThenReturnTrue() {
        String appointmentId = "1212";
        QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO() {{
            setAppointmentId(appointmentId);
            setCheckInIdx(-1);
            setCheckInStatus(CheckInStatusEnum.DELETED);
        }};
        PowerMockito.when(queuingManagementService.unCheckIn(queuingManagementDTO)).thenReturn(1);
        Response response = resource.unCheckInFromWaitingList(new UserContext(), queuingManagementDTO);
        Assert.assertTrue((Boolean) response.getEntity());
    }

    @Test
    public void testAutoCheckWhenHisIdIsNull(){
        Response response = resource.autoCheckIn(null,"1212");
        AutoCheckInVO autoCheckInVO = (AutoCheckInVO) response.getEntity();
        Assert.assertThat("N",equalTo(autoCheckInVO.getStatus()));
        Assert.assertThat(AppointmentResource.AUTO_CHECK_IN_PATIENT_NOT_FOUND,equalTo(autoCheckInVO.getResult()));
    }

    @Test
    public void givenHisIdAndDeviceMacAddressReturnAutoCheckInResult() throws Exception {

        PowerMockito.when(configuration.getCallingConfig()).thenReturn(givenACallingConfig());
        PowerMockito.when(ActivityCodesReader.getActivityCode(anyString())).thenReturn(new ActivityCodeConfig() {{
            setContent("content");
        }});
        String hisId = "hisId";
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(null);
        Response response = resource.autoCheckIn(hisId, null);
        AutoCheckInVO autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_PATIENT_NOT_FOUND, autoCheckInVOResponse.getResult());


        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(null);
        response = resource.autoCheckIn(hisId, null);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_PATIENT_NOT_FOUND, autoCheckInVOResponse.getResult());

        PatientDto patientDto = MockDtoUtil.givenAPatient();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByHisId(hisId)).thenReturn(patientDto);
        patientDto.setPatientSer("50000");

        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
        PowerMockito.when(SystemConfigPool.queryTreatmentActivityCode()).thenReturn("DoTreatment");
        TreatmentAppointmentService treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
        PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(treatmentAppointmentService);
        PowerMockito.when(treatmentAppointmentService.
                queryTreatmentsAppointmentByPatientId(new Long(patientDto.getPatientSer()),"DoTreatment"))
             .thenReturn(Arrays.asList(new TreatmentAppointmentDTO(){{
                 setActivityCode("DoTreatment");
                 setId("1212");
                 setStartTime(new Date());
                 setEndTime(new Date());
                 setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
             }}));
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientId(patientDto.getPatientSer())).thenReturn(appointmentDtoList);
        String activityCode = "activityCode";
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE)).thenReturn(Arrays.asList(activityCode));
        PowerMockito.when(treatmentAppointmentService.queryTreatmentsAppointmentByPatientId(new Long(patientDto.getPatientSer()), activityCode)).thenReturn(new ArrayList<>());
        response = resource.autoCheckIn(hisId, null);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_INTERNAL_ERROR, autoCheckInVOResponse.getResult());

        PowerMockito.when(treatmentAppointmentService.
                queryTreatmentsAppointmentByPatientId(new Long(patientDto.getPatientSer()),"DoTreatment"))
                .thenReturn(null);
        response = resource.autoCheckIn(hisId, null);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_INTERNAL_ERROR, autoCheckInVOResponse.getResult());

        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(false);

        appointmentDtoList.add(givenAnAppointmentForAnOldDay());
        appointmentDtoList.add(givenAnAppointmentForAFutureDay());
        response = resource.autoCheckIn(hisId, null);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_NO_APPOINTMENT_FOR_TODAY, autoCheckInVOResponse.getResult());

        appointmentDtoList.clear();
        appointmentDtoList.add(givenAnAppointment100DaysLaterButForThisDevice());
        appointmentDtoList.add(givenAnAppointment5DaysLaterButForThisDevice());
        String deviceMacAddress = "macaddress1000";
        response = resource.autoCheckIn(hisId, deviceMacAddress);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_INCORRECT_DATE, autoCheckInVOResponse.getResult());
        Assert.assertEquals(DateUtil.formatDate(givenAnAppointment5DaysLaterButForThisDevice().getStartTime(), DateUtil.SHORT_DATE_TIME_FORMAT), autoCheckInVOResponse.getScheduleTime());

        deviceMacAddress = "macaddress2000";
        appointmentDtoList.add(givenAnAppointmentForTodayNotForThisDevice());
        response = resource.autoCheckIn(hisId, deviceMacAddress);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_INCORRECT_DEVICE, autoCheckInVOResponse.getResult());
        Assert.assertEquals("Room1000", autoCheckInVOResponse.getDeviceRoom());

        appointmentDtoList.remove(appointmentDtoList.size() - 1);
        response = resource.autoCheckIn(hisId, deviceMacAddress);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_INCORRECT_DATE_AND_DEVICE, autoCheckInVOResponse.getResult());
        Assert.assertEquals(DateUtil.formatDate(givenAnAppointment5DaysLaterButForThisDevice().getStartTime(), DateUtil.SHORT_DATE_TIME_FORMAT), autoCheckInVOResponse.getScheduleTime());

        appointmentDtoList.clear();
        appointmentDtoList.add(givenAnAppointmentForTodayForThisDevice());
        deviceMacAddress = "macaddress1000";
        PowerMockito.when(queuingManagementService.checkIn(Matchers.any())).thenReturn(false);
        response = resource.autoCheckIn(hisId, deviceMacAddress);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_INTERNAL_ERROR, autoCheckInVOResponse.getResult());

        PowerMockito.when(queuingManagementService.ifAlreadyCheckedIn(anyString())).thenReturn(1);
        response = resource.autoCheckIn(hisId, deviceMacAddress);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_ALREADY_CHECKED_IN, autoCheckInVOResponse.getResult());

        PowerMockito.when(queuingManagementService.ifAlreadyCheckedIn(anyString())).thenReturn(0);
        PowerMockito.when(queuingManagementService.checkIn(Matchers.any())).thenReturn(true);
        response = resource.autoCheckIn(hisId, deviceMacAddress);
        autoCheckInVOResponse = (AutoCheckInVO) response.getEntity();
        Assert.assertEquals(AppointmentResource.AUTO_CHECK_IN_SUCCESS, autoCheckInVOResponse.getResult());
    }

    @Test
    public void testAutoCheckInVO(){
        AutoCheckInVO autoCheckInVOResponse2 = new AutoCheckInVO("","","","","","","","",null);
        autoCheckInVOResponse2.getPatientName();
        autoCheckInVOResponse2.getHisId();
        autoCheckInVOResponse2.getStatus();
        autoCheckInVOResponse2.getGuide();
        autoCheckInVOResponse2.getResult();
        autoCheckInVOResponse2.getScheduleTime();
        autoCheckInVOResponse2.getDeviceId();
        autoCheckInVOResponse2.getScheduleTask();
        autoCheckInVOResponse2.getDeviceRoom();
        autoCheckInVOResponse2.setDeviceId("");
        autoCheckInVOResponse2.setHisId("");
        autoCheckInVOResponse2.setStatus("");
        autoCheckInVOResponse2.setResult("");
        autoCheckInVOResponse2.setScheduleTime("");
        autoCheckInVOResponse2.setPatientName("");
        autoCheckInVOResponse2.setScheduleTask("");
        autoCheckInVOResponse2.setGuide(null);
        autoCheckInVOResponse2.setDeviceRoom("");
    }

//    @Test
//    public void givenAppointmentWhenCheckAppointmentTimeThenReturnObject() throws ParseException {
//        String patientId = "11111";
//        List<AppointmentDataTimeSlotVO> list = new ArrayList<>();
//        list.add(new AppointmentDataTimeSlotVO() {{
//            setStartTime("2017-11-06 10:30");
//            setEndTime("2017-11-06 10:45");
//            setAppointmentId("121");
//            setConflictActName("");
//            setActName("DoCT");
//            setAction(AppointmentTimeSlotActionEnum.ADD.ordinal());
//        }});
//        Assert.assertNotNull(list.get(0).getEndTime());
//        Assert.assertNotNull(list.get(0).getStartTime());
//        Assert.assertNotNull(list.get(0).getConflictActName());
//        Assert.assertNotNull(list.get(0).getAppointmentId());
//        Assert.assertNotNull(list.get(0).getActName());
//        Assert.assertNotNull(list.get(0).getAction());
//        list.add(new AppointmentDataTimeSlotVO() {{
//            setStartTime("2017-11-06 11:30");
//            setEndTime("2017-11-06 11:45");
//            setConflictActName("");
//            setActName("DoImmobilization");
//            setAction(AppointmentTimeSlotActionEnum.ADD.ordinal());
//        }});
//        list.add(new AppointmentDataTimeSlotVO() {{
//            setStartTime("2017-11-06 11:30");
//            setEndTime("2017-11-06 11:45");
//            setConflictActName("");
//            setAppointmentId("111");
//            setActName("DoImmobilization");
//            setAction(AppointmentTimeSlotActionEnum.DELETED.ordinal());
//        }});
//        list.add(new AppointmentDataTimeSlotVO() {{
//            setStartTime("2017-11-06 11:30");
//            setEndTime("2017-11-06 11:45");
//            setAppointmentId("1212");
//            setConflictActName("");
//            setActName("DoImmobilization");
//            setAction(AppointmentTimeSlotActionEnum.CANCEL.ordinal());
//        }});
//        Collections.sort(list);
//        AppointmentDataVO appointmentDataVO = new AppointmentDataVO() {{
//            setActivityType(ActivityTypeEnum.TASK.name());
//            setDeviceId("11212");
//            setPatientSer(patientId);
//            setActivityCode("ScheduleCTSim");
//            setAppointTimeList(list);
//            setInstanceId("1212");
//        }};
//        Assert.assertNotNull(appointmentDataVO.getActivityCode());
//        Assert.assertNotNull(appointmentDataVO.getActivityType());
//        Assert.assertNotNull(appointmentDataVO.getAppointTimeList());
//        Assert.assertNotNull(appointmentDataVO.getDeviceId());
//        Assert.assertNotNull(appointmentDataVO.getPatientSer());
//        Date startTime = DateUtil.parse("2017-11-06 10:15");
//        Date endTime = DateUtil.parse("2017-11-06 10:30");
//        Pagination<AppointmentDto> pagination = new Pagination<>();
//        pagination.setLstObject(Arrays.asList(new AppointmentDto() {{
//            setStartTime(startTime);
//            setEndTime(endTime);
//            setReason("DoImmobilization");
//        }}));
//        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(patientId, null, null, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED)),
//                30, 1, Integer.MAX_VALUE)).thenReturn(pagination);
//        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(patientId, null, null, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED)),
//                30, 1, Integer.MAX_VALUE)).thenReturn(new Pagination<AppointmentDto>() {{
//            setLstObject(new ArrayList<>());
//            setTotalCount(0);
//        }});
//        PowerMockito.when(ActivityCodesReader.getActivityCode("ScheduleCTSim")).thenReturn(new ActivityCodeConfig() {{
//            setEntryContent("预约CT");
//        }});
//        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode("DoImmobilization")).thenReturn(new ActivityCodeConfig() {{
//            setEntryContent("预约制模");
//        }});
//
//        CarePathInstance carePathInstance = PowerMockito.mock(CarePathInstance.class);
//        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(patientId,appointmentDataVO.getInstanceId(),ActivityTypeEnum.TASK))
//                .thenReturn(carePathInstance);
//        PowerMockito.when(carePathInstance.getOriginalActivityInstances()).thenReturn(Arrays.asList(new ActivityInstance(){{
//            setActivityCode("ScheduleCTSim");
//            setActivityType(ActivityTypeEnum.TASK);
//            setInstanceID("11");
//        }}));
//
//        Response response = resource.checkAppointmentTime(new UserContext(), appointmentDataVO);
//        Object entity = response.getEntity();
//        Assert.assertNotNull(entity);
//        if (entity instanceof BaseResponse) {
//            BaseResponse res = (BaseResponse) entity;
//            List<Map<String, Object>> errors = res.getErrors();
//            Assert.assertTrue(errors.size() > 0);
//        } else {
//            Assert.fail("No Error Failed!");
//        }
//    }

//    @Test
//    public void testCheckUpdateTimeWhenPatientSerIsNull(){
//        Response response = resource.checkUpdateTime(new UserContext(),new AppointmentDataVO(){{
//            setPatientSer(null);
//        }});
//        Assert.assertThat(new Integer(response.getStatus()),equalTo(new Integer(Response.Status.BAD_REQUEST.getStatusCode())));
//    }

//    @Test
//    public void givenAppointmentWhenCheckUpdateTimeThenReturnObject() throws ParseException {
//
//        PowerMockito.when(SystemConfigPool.queryTimeSlotCount()).thenReturn("2");
//
//        String patientId = "patientId";
//        List<AppointmentDataTimeSlotVO> list = new ArrayList<>();
//        list.add(new AppointmentDataTimeSlotVO() {{
//            setStartTime("2020-11-06 10:30");
//            setEndTime("2020-11-06 10:45");
//            setAppointmentId("121");
//            setConflictActName("");
//            setActName("DoCT");
//            setAction(AppointmentTimeSlotActionEnum.ADD.ordinal());
//        }});
//        Assert.assertNotNull(list.get(0).getEndTime());
//        Assert.assertNotNull(list.get(0).getStartTime());
//        Assert.assertNotNull(list.get(0).getConflictActName());
//        Assert.assertNotNull(list.get(0).getAppointmentId());
//        Assert.assertNotNull(list.get(0).getActName());
//        Assert.assertNotNull(list.get(0).getAction());
//        list.add(new AppointmentDataTimeSlotVO() {{
//            setStartTime("2020-11-06 11:30");
//            setEndTime("2020-11-06 11:45");
//            setConflictActName("");
//            setActName("DoImmobilization");
//            setAction(AppointmentTimeSlotActionEnum.ADD.ordinal());
//        }});
//        list.add(new AppointmentDataTimeSlotVO() {{
//            setStartTime("2020-11-06 11:30");
//            setEndTime("2020-11-06 11:45");
//            setConflictActName("");
//            setAppointmentId("111");
//            setActName("DoImmobilization");
//            setAction(AppointmentTimeSlotActionEnum.DELETED.ordinal());
//        }});
//        list.add(new AppointmentDataTimeSlotVO() {{
//            setStartTime("2020-11-06 11:30");
//            setEndTime("2020-11-06 11:45");
//            setAppointmentId("1212");
//            setConflictActName("");
//            setActName("DoImmobilization");
//            setAction(AppointmentTimeSlotActionEnum.CANCEL.ordinal());
//        }});
//        Collections.sort(list);
//        AppointmentDataVO appointmentDataVO = new AppointmentDataVO() {{
//            setActivityType(ActivityTypeEnum.TASK.name());
//            setDeviceId("11212");
//            setPatientSer(patientId);
//            setActivityCode("ScheduleCTSim");
//            setAppointTimeList(list);
//        }};
//        Assert.assertNotNull(appointmentDataVO.getActivityCode());
//        Assert.assertNotNull(appointmentDataVO.getActivityType());
//        Assert.assertNotNull(appointmentDataVO.getAppointTimeList());
//        Assert.assertNotNull(appointmentDataVO.getDeviceId());
//        Assert.assertNotNull(appointmentDataVO.getPatientSer());
//        Date startTime = DateUtil.parse("2020-11-06 10:15");
//        Date endTime = DateUtil.parse("2020-11-06 10:30");
//        Pagination<AppointmentDto> pagination = new Pagination<>();
//        pagination.setLstObject(Arrays.asList(new AppointmentDto() {{
//            setStartTime(startTime);
//            setEndTime(endTime);
//            setReason("DoImmobilization");
//        }}));
//        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(patientId, null, null, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED)),
//                30, 1, Integer.MAX_VALUE)).thenReturn(pagination);
//        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(patientId, null, null, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED)),
//                30, 1, Integer.MAX_VALUE)).thenReturn(new Pagination<AppointmentDto>() {{
//            setLstObject(new ArrayList<>());
//            setTotalCount(0);
//        }});
//
//        List<AppointmentDto> appointmentList = new ArrayList<>();
//        AppointmentDto dd1 = new AppointmentDto();
//        dd1.setStatus(AppointmentStatusEnum.FULFILLED.toString());
//        dd1.setReason("DoImmobilization");
//        AppointmentDto dd2 = new AppointmentDto();
//        dd2.setStatus(AppointmentStatusEnum.BOOKED.toString());
//        dd2.setReason("DoImmobilization");
//
//        appointmentList.add(dd1);
//        appointmentList.add(dd2);
//        PowerMockito.when(antiCorruptionServiceImp.queryAllByPatientSer(patientId)).thenReturn(appointmentList);
//
//        PowerMockito.when(ActivityCodesReader.getActivityCode("ScheduleCTSim")).thenReturn(new ActivityCodeConfig() {{
//            setEntryContent("预约CT");
//        }});
//        PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode("DoImmobilization")).thenReturn(new ActivityCodeConfig() {{
//            setEntryContent("预约制模");
//        }});
//
//        CarePathInstance cpInstance = new CarePathInstance();
//        ActivityInstance instance = new ActivityInstance();
//        String activityCode = "ScheduleCTSim";
//        instance.setActivityCode(activityCode);
//        List<String> nextActivities = Arrays.asList(new String[]{"12345"});
//        instance.setNextActivities(nextActivities);
//        List<String> prevActivities = Arrays.asList(new String[]{"45678"});
//        instance.setPrevActivities(prevActivities);
//        cpInstance.addActivityInstance(instance);
//        PowerMockito.when(carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(Mockito.anyString())).thenReturn(cpInstance);
//
//        Response response = resource.checkUpdateTime(new UserContext(), appointmentDataVO);
//        Object entity = response.getEntity();
//        Assert.assertNotNull(entity);
//        if (entity instanceof BaseResponse) {
//            BaseResponse res = (BaseResponse) entity;
//            List<Map<String, Object>> errors = res.getErrors();
//            Assert.assertTrue(errors.size() == 0);
//        } else {
//            Assert.fail("No Error Failed!");
//        }
//    }

    @Test
    public void givenDateWhenScheduleViewsThenList(){
        DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp = PowerMockito.mock(DeviceAntiCorruptionServiceImp.class);
        try {
            PowerMockito.whenNew(DeviceAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(deviceAntiCorruptionServiceImp);
            DeviceDto deviceDto = new DeviceDto(){{
                setId("12121");
                setCode("23EX");
                setName("DoCT Machine");
            }};
            PowerMockito.when(DevicesReader.getAllDeviceDto()).thenReturn(Arrays.asList(new DeviceDto(){{
                setId("23EX");
                setCapacity(300);
            }}));
            PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByCode(Matchers.anyString())).thenReturn(deviceDto);
            PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
            ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig(){{
                setName("ScheduleTreatment");
            }};
            PowerMockito.when(ActivityCodesReader.getSourceActivityCodeByRelativeCode(Matchers.anyString())).thenReturn(activityCodeConfig);
            TreatmentAppointmentService treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
            PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(treatmentAppointmentService);
            Pagination<TreatmentAppointmentDTO> pagination = new Pagination<TreatmentAppointmentDTO>(){{
                setTotalCount(1);
                setLstObject(Arrays.asList(new TreatmentAppointmentDTO(){{
                    setStartTime(new Date());
                    setDeviceId("12121");
                }}));
            }};
            PowerMockito.when(treatmentAppointmentService.treatmentAppointmentDTO2AppointmentDto(Matchers.any(TreatmentAppointmentDTO.class))).thenReturn(givenAnAppointment());
            CarePathConfig carePathConfig = PowerMockito.mock(CarePathConfig.class);
            PowerMockito.when(configuration.getCarePathConfig()).thenReturn(carePathConfig);
            PowerMockito.when(carePathConfig.getCarePath()).thenReturn(Arrays.asList(new CarePathConfigItem(){{
                setTemplateId("BCRIStandard");
                setTemplateName("TemplateName");
            }}));
            CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(carePathAntiCorruptionServiceImp);
            CarePathTemplate carePathTemplate = PowerMockito.mock(CarePathTemplate.class);
            PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(Matchers.anyString())).thenReturn(carePathTemplate);
            PowerMockito.when(carePathTemplate.getActivities()).thenReturn(Arrays.asList(new PlannedActivity(){{
                setActivityCode("ScheduleTreatment");
                setDeviceIDs(Arrays.asList("1106"));
            }}));
            PowerMockito.when(treatmentAppointmentService.queryByDeviceIdListAndDatePagination(Matchers.anyListOf(String.class),Matchers.any(java.util.Date.class),Matchers.any(java.util.Date.class),
                    Matchers.anyListOf(AppointmentStatusEnum.class),Matchers.anyString(),Matchers.anyString(),Matchers.anyString())).thenReturn(pagination);
            PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDateRangeAndStatusWithPagination(Matchers.anyListOf(String.class),Matchers.anyString(),Matchers.anyString(),
                    Matchers.anyListOf(String.class), Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(new Pagination<AppointmentDto>(){
                        {
                            setTotalCount(1);
                            setLstObject(Arrays.asList(givenAnAppointment()));
                        }
                    });
            Response response = resource.scheduleViews(new UserContext(),"2018-01-03");
            Assert.assertNotNull(response);
            List<DeviceScheduleViewVO>  list = (List<DeviceScheduleViewVO>) response.getEntity();
            Assert.assertNotNull(list);
            DeviceScheduleViewVO viewVO = list.get(0);
            Assert.assertNotNull(viewVO.getDeviceId());
            Assert.assertNotNull(viewVO.getAfternoonOccupied());
            Assert.assertNotNull(viewVO.getCapacity());
            Assert.assertNotNull(viewVO.getCode());
            Assert.assertNotNull(viewVO.getForenoonOccupied());
            Assert.assertNotNull(viewVO.getName());
            Assert.assertNotNull(viewVO.getOccupied());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchByPatientInCurrentEncounterWhenPatienSerNull(){
        Long patientSer = null;
       Response response = resource.searchByPatientInCurrentEncounter(new UserContext(),patientSer);
       Assert.assertThat(response.getStatus(),equalTo(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void testSearchByPatientInCurrentEncounterWhenEncounterCpIsNull(){
        Long patientSer = 1111L;
        PowerMockito.mockStatic(PatientEncounterHelper.class);
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(String.valueOf(patientSer))).thenReturn(null);
        Response response = resource.searchByPatientInCurrentEncounter(new UserContext(),patientSer);
        Assert.assertThat(response.getEntity(),equalTo(new ArrayList<>()));
    }

    @Test
    public void givenHisIdThenReturnAllAppointmentsInCurrentEncounter() throws ParseException{
        Long patientId = 12L;
        long encounterId = 100L;
        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setType(ParticipantTypeEnum.PATIENT);
        participantDto.setParticipantId("participantId");
        PatientEncounterCarePath patientEncounterCarePath = new PatientEncounterCarePath();
        EncounterCarePathList encounterCarePathList = new EncounterCarePathList();
        encounterCarePathList.setEncounterId(encounterId);
        encounterCarePathList.setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
            setCategory(EncounterCarePathCategoryEnum.PRIMARY);
            setCpInstanceId(112l);
            setCrtTime(new Date());
        }}));
        patientEncounterCarePath.setPlannedCarePath(encounterCarePathList);
        PowerMockito.mockStatic(PatientEncounterHelper.class);
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(String.valueOf(patientId))).thenReturn(patientEncounterCarePath);
        List<TreatmentAppointmentDTO> localDtoList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        TreatmentAppointmentDTO treatmentAppointmentDTO1 = new TreatmentAppointmentDTO();
        Date date1 = formatter.parse("2018/03/15 15:30:00");
        Date date2 = formatter.parse("2018/03/15 16:00:00");
        treatmentAppointmentDTO1.setStartTime(date1);
        treatmentAppointmentDTO1.setEndTime(date1);
        localDtoList.add(treatmentAppointmentDTO1);
        TreatmentAppointmentDTO treatmentAppointmentDTO2 = new TreatmentAppointmentDTO();
        treatmentAppointmentDTO2.setStartTime(date2);
        treatmentAppointmentDTO2.setEndTime(date2);
        localDtoList.add(treatmentAppointmentDTO2);
        Date date3 = formatter.parse("2018/03/15 16:30:00");
        AppointmentDto appointmentDto1 = new AppointmentDto();
        appointmentDto1.setAppointmentId("1");
        appointmentDto1.setStartTime(date1);
        appointmentDto1.setEndTime(date1);
        appointmentDto1.setStatus("booked");
        appointmentDto1.setParticipants(Arrays.asList(participantDto));
        AppointmentDto appointmentDto3 = new AppointmentDto();
        appointmentDto3.setAppointmentId("2");
        appointmentDto3.setStartTime(date3);
        appointmentDto3.setEndTime(date3);
        appointmentDto3.setStatus("booked");
        appointmentDto3.setParticipants(Arrays.asList(participantDto));
        Date date4 = formatter.parse("2018/03/15 17:00:00");
        AppointmentDto appointmentDto4 = new AppointmentDto();
        appointmentDto4.setAppointmentId("3");
        appointmentDto4.setStartTime(date4);
        appointmentDto4.setEndTime(date4);
        appointmentDto4.setAppointmentId("4");
        List<AppointmentDto> ariaAppointmentDto = new ArrayList<>();
        ariaAppointmentDto.add(appointmentDto1);
        ariaAppointmentDto.add(appointmentDto3);
        ariaAppointmentDto.add(appointmentDto4);
        PowerMockito.when(treatmentAppointmentService.queryAppointmentListByPatientSerAndEncounterId(patientId, new Long(encounterId).intValue()))
                .thenReturn(localDtoList);
        AppointmentDto appointmentDtoTreatmentAppointmentDTO1 = new AppointmentDto();
        appointmentDtoTreatmentAppointmentDTO1.setStartTime(date1);
        appointmentDtoTreatmentAppointmentDTO1.setEndTime(date1);
        appointmentDtoTreatmentAppointmentDTO1.setStatus("booked");
        appointmentDtoTreatmentAppointmentDTO1.setParticipants(Arrays.asList(participantDto));
        AppointmentDto appointmentDtoTreatmentAppointmentDTO2 = new AppointmentDto();
        appointmentDtoTreatmentAppointmentDTO2.setStartTime(date2);
        appointmentDtoTreatmentAppointmentDTO2.setEndTime(date2);
        appointmentDtoTreatmentAppointmentDTO2.setStatus("booked");
        appointmentDtoTreatmentAppointmentDTO2.setParticipants(Arrays.asList(participantDto));
        PowerMockito.when(treatmentAppointmentService.treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO1)).thenReturn(appointmentDtoTreatmentAppointmentDTO1);
        PowerMockito.when(treatmentAppointmentService.treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO2)).thenReturn(appointmentDtoTreatmentAppointmentDTO2);
        Pagination<AppointmentDto> pagination = new Pagination<>();
        pagination.setLstObject(ariaAppointmentDto);
        PowerMockito.when(antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(String.valueOf(patientId), null, null, Arrays.asList(
                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)),
                Integer.MAX_VALUE, 1, Integer.MAX_VALUE)).thenReturn(pagination);
        CarePathInstance carePathInstance1 = MockDtoUtil.givenACarePathInstance();
        carePathInstance1.setId("112");
        carePathInstance1.getOriginalActivityInstances().add(new ActivityInstance(){{
            setInstanceID("121212");
            setActivityCode("DoTreatment");
            setDeviceIDs(Arrays.asList("111111"));
        }});
        PowerMockito.when(SystemConfigPool.queryTreatmentActivityCode()).thenReturn("DoTreatment");
        CarePathInstance carePathInstance3 = MockDtoUtil.givenACarePathInstance();
        carePathInstance3.setId("3");
        List<CarePathInstance> carePathInstanceList = new ArrayList<>();
        carePathInstanceList.add(carePathInstance1);
        carePathInstanceList.add(carePathInstance3);
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(String.valueOf(patientId))).thenReturn(carePathInstanceList);
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(participantDto.getParticipantId())).thenReturn(MockDtoUtil.givenAPatient());
        ActivityCodeConfig activityCodeConfig = new ActivityCodeConfig();
        activityCodeConfig.setContent("content");
        PowerMockito.mockStatic(ActivityCodesReader.class);
        PowerMockito.when(ActivityCodesReader.getActivityCode(Matchers.any())).thenReturn(activityCodeConfig);
        PowerMockito.when(queuingManagementService.queryCheckInList(Matchers.any())).thenReturn(new ArrayList<>());
        PowerMockito.when(patientCacheService.queryPatientByPatientId(Matchers.anyString())).thenReturn(new PatientDto(){{
            setPatientSer("12");
        }});
        Response response = resource.searchByPatientInCurrentEncounter(new UserContext(), patientId);
        Map<String, Object> resultMap = (Map<String, Object>) response.getEntity();
        List<AppointmentListVO> resultList = (ArrayList) resultMap.get("appointmentList");
        Assert.assertEquals(2, resultList.size());
    }

    private AppointmentDto givenAnAppointment() {
        AppointmentDto dto = new AppointmentDto();
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setOrderId("orderId");
        dto.setComment("");
        dto.setReason(ActivityCodeEnum.getDisplay(ActivityCodeEnum.IMMOBILIZATION_APPOINTMENT));
        dto.setStatus("");
        dto.setParticipants(new ArrayList<>());
        dto.getParticipants().add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "121212"));
        dto.getParticipants().add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "12121"));
        return dto;
    }

    private AppointmentDto givenAnAppointmentForTodayForThisDevice() {
        AppointmentDto dto = new AppointmentDto();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        dto.setStartTime(c.getTime());
        dto.setEndTime(c.getTime());
        dto.setOrderId("orderId");
        dto.setComment("");
        dto.setReason("DoImmobilization");
        dto.setStatus("");
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1"));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "1000"));
        dto.setParticipants(participantDtos);
        return dto;
    }

    private AppointmentDto givenAnAppointmentForTodayNotForThisDevice() {
        AppointmentDto dto = new AppointmentDto();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        dto.setStartTime(c.getTime());
        dto.setEndTime(c.getTime());
        dto.setOrderId("orderId1");
        dto.setComment("");
        dto.setReason("DoImmobilization");
        dto.setStatus("");
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1"));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "1000"));
        dto.setParticipants(participantDtos);
        return dto;
    }

    private AppointmentDto givenAnAppointment5DaysLaterButForThisDevice() {
        AppointmentDto dto = new AppointmentDto();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 5);
        dto.setStartTime(c.getTime());
        dto.setEndTime(c.getTime());
        dto.setOrderId("orderId");
        dto.setComment("");
        dto.setReason("DoImmobilization");
        dto.setStatus("");
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1"));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "1000"));
        dto.setParticipants(participantDtos);
        return dto;
    }

    private AppointmentDto givenAnAppointment100DaysLaterButForThisDevice() {
        AppointmentDto dto = new AppointmentDto();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 100);
        dto.setStartTime(c.getTime());
        dto.setEndTime(c.getTime());
        dto.setOrderId("orderId");
        dto.setComment("");
        dto.setReason("DoImmobilization");
        dto.setStatus("");
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1"));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "1000"));
        dto.setParticipants(participantDtos);
        return dto;
    }

    private AppointmentDto givenAnAppointmentForAnOldDay() {
        AppointmentDto dto = new AppointmentDto();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -10);
        dto.setStartTime(c.getTime());
        dto.setEndTime(c.getTime());
        dto.setOrderId("orderId");
        dto.setComment("");
        dto.setReason("DoImmobilization");
        dto.setStatus("");
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1"));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "1000"));
        dto.setParticipants(participantDtos);
        return dto;
    }

    private AppointmentDto givenAnAppointmentForAFutureDay() {
        AppointmentDto dto = new AppointmentDto();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 10);
        dto.setStartTime(c.getTime());
        dto.setEndTime(c.getTime());
        dto.setOrderId("orderId");
        dto.setComment("");
        dto.setReason("DoImmobilization");
        dto.setStatus("");
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1"));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.DEVICE, "1000"));
        dto.setParticipants(participantDtos);
        return dto;
    }

    private CallingConfig givenACallingConfig() {
        CallingConfig callingConfig = new CallingConfig();
        CheckInGuide checkInGuide = new CheckInGuide();
        callingConfig.setCheckInGuide(checkInGuide);
        DevicesGuide devicesGuide = new DevicesGuide();
        checkInGuide.setDevicesGuide(devicesGuide);
        Device device1000 = new Device("macaddress1000", "test1000", "1000", "Room1000", new DeviceGuide());
        Device device2000 = new Device("macaddress2000", "test2000", "2000", "Room2000", new DeviceGuide());
        devicesGuide.setDevices(new ArrayList<>());
        devicesGuide.getDevices().add(device1000);
        devicesGuide.getDevices().add(device2000);
        return callingConfig;
    }


    private QueueListVO createVoWithData() {
        QueueListVO vo = new QueueListVO();

        vo.setPatientSer("12");
        vo.setActivityId("activityId");
        vo.setInstanceId("instanceId");
        vo.setActivityType("activityType");

        vo.setActivityId("mockString");
        vo.setInstanceId("mockString");
        vo.setActivityType("mockString");
        vo.setActivityCode("mockString");
        vo.setActiveInWorkflow(false);
        vo.setActivityGroupId("mockString");
        vo.setAriaId("mockString");
        vo.setHisId("mockString");
        vo.setNationalId("mockString");
        vo.setChineseName("mockString");
        vo.setEnglishName("mockString");
        vo.setGender("mockString");
        vo.setBirthday(new Date());
        vo.setTelephone("mockString");
        vo.setContactPerson("mockString");
        vo.setContactPhone("mockString");
        vo.setPhysicianGroupId("mockString");
        vo.setPhysicianGroupName("mockString");
        vo.setWarningText("mockString");
        vo.setUrgent(true);
        vo.setPhysicianId("mockString");
        vo.setPhysicianName("mockString");
        vo.setPhysicianPhone("mockString");
        vo.setProgressState("mockString");
        vo.setNextAction("mockString");
        vo.setPreActivityCompletedTime(new Date());
        vo.setPreActivityName("mockString");
        vo.setScheduleTime("mockString");
        vo.setStartTime(new Date());
        vo.setConfirmedPayment(false);
        vo.setWorkspaceType("mockString");
        vo.setModuleId("mockString");
        vo.setPhysicianComment("mockString");
        vo.setPatientSource("mockString");
        vo.setInsuranceType("mockString");
        vo.setStatus("mockString");

        vo.setCheckInStatus("mockString");
        vo.setCheckInIdx(66);
        vo.setAge("33");

        return vo;
    }


    private QueuingManagementVO createQueuingManagementVO() {
        QueuingManagementVO vo = new QueuingManagementVO();
        vo.setId("mockString");
        vo.setAppointmentId("mockString");
        vo.setActivityCode("mockString");
        vo.setHisId("342");
        vo.setPatientSer(33L);
        vo.setCheckInStatus(CheckInStatusEnum.CALLED);
        vo.setCheckInIdx(33);
        vo.setStartTime(new Date());
        vo.setCheckInTime(new Date());
        vo.setDeviceId("mockString");

        return vo;
    }
}
