package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Appointment;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.assembler.AppointmentAssembler;
import com.varian.oiscn.anticorruption.converter.EnumAppointmentQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.datahelper.MockAppointmentUtil;
import com.varian.oiscn.anticorruption.datahelper.MockTaskUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRAppointmentInterface;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRTaskInterface;
import com.varian.oiscn.cache.AppointmentCache;
import com.varian.oiscn.core.RankEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentRankEnum;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.*;

/**
 * Created by fmk9441 on 2017-02-14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AppointmentAntiCorruptionServiceImp.class, AppointmentAssembler.class, AppointmentCache.class})
public class AppointmentAntiCorruptionServiceImpTest {
    private static final String APPOINTMENT_ID = "AppointmentId";
    private FHIRAppointmentInterface fhirAppointmentInterface;
    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirAppointmentInterface = PowerMockito.mock(FHIRAppointmentInterface.class);
        PowerMockito.whenNew(FHIRAppointmentInterface.class).withNoArguments().thenReturn(fhirAppointmentInterface);
        appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
    }

    @Test
    public void givenAnAppointmentDtoWhenCreateThenReturnAppointmentId() throws Exception {
        Appointment appointment = MockAppointmentUtil.givenAnAppointment();
        AppointmentDto appointmentDto = MockAppointmentUtil.givenAnAppointmentDto();
        PowerMockito.mockStatic(AppointmentAssembler.class);
        PowerMockito.when(AppointmentAssembler.getAppointment(appointmentDto)).thenReturn(appointment);
        PowerMockito.when(fhirAppointmentInterface.create(appointment)).thenReturn(APPOINTMENT_ID);
        Task task = MockTaskUtil.givenATask();
        FHIRTaskInterface fhirTaskInterface = PowerMockito.mock(FHIRTaskInterface.class);
        PowerMockito.whenNew(FHIRTaskInterface.class).withNoArguments().thenReturn(fhirTaskInterface);
        PowerMockito.when(fhirTaskInterface.queryById(anyString(),any())).thenReturn(task);
        String updatedTaskId = "AppointmentId";
        PowerMockito.when(fhirTaskInterface.update(task)).thenReturn(updatedTaskId);

        String createdAppointmentId = appointmentAntiCorruptionServiceImp.createAppointment(appointmentDto);
        Assert.assertEquals(APPOINTMENT_ID, createdAppointmentId);
    }

    @Test
    public void givenAnAppointmentDtoWhenUpdateThenReturnAppointmentId() {
        Appointment appointment = MockAppointmentUtil.givenAnAppointment();
        AppointmentDto appointmentDto = MockAppointmentUtil.givenAnAppointmentDto();
        PowerMockito.when(fhirAppointmentInterface.queryById(anyString(),any())).thenReturn(appointment);
        PowerMockito.when(fhirAppointmentInterface.update(appointment)).thenReturn(APPOINTMENT_ID);
        String updatedAppointmentId = appointmentAntiCorruptionServiceImp.updateAppointment(appointmentDto);
        Assert.assertEquals(updatedAppointmentId, APPOINTMENT_ID);
    }

    @Test
    public void givenAnAppointmentIdWhenQueryThenReturnAppointmentDto() {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.APPOINTMENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, APPOINTMENT_ID));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        PowerMockito.when(fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairLinkedHashMap)).thenReturn(lstAppointment);
        AppointmentDto appointmentDto = appointmentAntiCorruptionServiceImp.queryAppointmentById(APPOINTMENT_ID);
        Assert.assertNotNull(appointmentDto);
    }

    @Test
    public void givenATaskIdWhenQueryThenReturnAppointmentDtoList() {
        final String taskId = "TaskId";
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        PowerMockito.when(fhirAppointmentInterface.queryAppointmentListByTaskId(anyString())).thenReturn(lstAppointment);
        List<AppointmentDto> lstAppointmentDto = appointmentAntiCorruptionServiceImp.queryAppointmentByOrderId(taskId);
        Assert.assertThat(1, is(lstAppointmentDto.size()));
    }

    @Test
    public void givenADeviceIdAndDateWhenQueryThenReturnAppointmentDtoList() {
        final String DEVICE_ID = "DeviceId";
        final Date DATE = new Date();
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(DEVICE_ID)));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, DATE));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        PowerMockito.when(fhirAppointmentInterface.queryAppointmentList(anyObject())).thenReturn(lstAppointment);
        List<AppointmentDto> lstAppointmentDto = appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDate(DEVICE_ID, DATE);
        Assert.assertThat(1, is(lstAppointmentDto.size()));
    }

    @Test
    public void givenAPatientIdWhenQueryThenReturnAppointmentDtoList() {
        final String PATIENT_ID = "PatientId";
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, PATIENT_ID));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        Pagination<Appointment> appointmentList = new Pagination<>();
        appointmentList.setLstObject(lstAppointment);
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(appointmentList);
        List<AppointmentDto> lstAppointmentDto = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientId(PATIENT_ID);
        Assert.assertThat(1, is(lstAppointmentDto.size()));
    }

    @Test
    public void givenAPatientIdAndActivityCodeWhenQueryThenReturnAppointmentDtoList() {
        final String PATIENT_ID = "PatientId";
        final String ACTIVITY_CODE = "ActivityCode";
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, PATIENT_ID));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ACTIVITY_CODE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("status")));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        Pagination<Appointment> appointmentList = new Pagination<>();
        appointmentList.setLstObject(lstAppointment);
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(appointmentList);
        List<AppointmentDto> lstAppointmentDto = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCode(PATIENT_ID, ACTIVITY_CODE);
        Assert.assertThat(1, is(lstAppointmentDto.size()));
    }

    @Test
    public void givenAPatientIdAndActivityCodeWhenQueryThenReturnAppointmentDtoPagingList() {
        final String PATIENT_ID = "PatientId";
        final String ACTIVITY_CODE = "ActivityCode";
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, PATIENT_ID));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ACTIVITY_CODE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("status")));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        Pagination<Appointment> appointmentPagination = new Pagination<>();
        appointmentPagination.setTotalCount(5);
        appointmentPagination.setLstObject(lstAppointment);
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(appointmentPagination);
        Pagination<AppointmentDto> appointmentDtoPagination = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeWithPaging(PATIENT_ID, ACTIVITY_CODE, 5, 1, 5);
        Assert.assertThat(5, is(appointmentDtoPagination.getTotalCount()));
        Assert.assertThat(1, is(appointmentDtoPagination.getLstObject().size()));
    }

    @Test
    public void givenADeviceIdAndDateRangeAndStatusWhenQueryThenReturnAppointmentDtoList() {
        final String DEVICE_ID = "DeviceId";
        final String START_DATE = "StartDate";
        final String END_DATE = "EndDate";
        final List<String> LIST_STATUS = Arrays.asList("Status");
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(DEVICE_ID)));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, START_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, END_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, LIST_STATUS));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        PowerMockito.when(fhirAppointmentInterface.queryAppointmentList(anyObject())).thenReturn(lstAppointment);
        List<AppointmentDto> lstAppointmentDto = appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDateRangeAndStatus(DEVICE_ID, START_DATE, END_DATE, LIST_STATUS);
        Assert.assertThat(1, is(lstAppointmentDto.size()));
    }

    @Test
    public void givenAPatientIdAndDateRangeAndPaginationWhenQueryThenReturnAppointmentDtoPagination() {
        final String PATIENT_ID = "PatientId";
        final String START_DATE = "StartDate";
        final String END_DATE = "EndDate";
        final int COUNT_PER_PAGE = 10;
        final int PAGE_NUMBER = 1;
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, PATIENT_ID));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, START_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, END_DATE));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        Pagination<Appointment> appointmentPagination = new Pagination<>();
        appointmentPagination.setTotalCount(5);
        appointmentPagination.setLstObject(lstAppointment);
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(appointmentPagination);
        Pagination<AppointmentDto> appointmentDtoPagination = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(PATIENT_ID, START_DATE, END_DATE, Arrays.asList(
                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), COUNT_PER_PAGE, PAGE_NUMBER, PAGE_NUMBER + 2);
        Assert.assertThat(5, is(appointmentDtoPagination.getTotalCount()));
        Assert.assertThat(1, is(appointmentDtoPagination.getLstObject().size()));
    }

    @Test
    public void givenAPatientIdAndActivityCodeAndDateRangeAndPaginationWhenQueryThenReturnAppointmentDtoPagination() {
        final String PATIENT_ID = "PatientId";
        final String START_DATE = "StartDate";
        final String END_DATE = "EndDate";
        final String ACTIVITY_CODE = "DoFirstTreatment";
        final int COUNT_PER_PAGE = 10;
        final int PAGE_NUMBER = 1;
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, PATIENT_ID));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ACTIVITY_CODE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, START_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, END_DATE));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        Pagination<Appointment> appointmentPagination = new Pagination<>();
        appointmentPagination.setTotalCount(5);
        appointmentPagination.setLstObject(lstAppointment);
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(appointmentPagination);
        Pagination<AppointmentDto> appointmentDtoPagination = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeAndDateRangeAndPagination(PATIENT_ID, ACTIVITY_CODE, START_DATE, END_DATE, COUNT_PER_PAGE, PAGE_NUMBER, PAGE_NUMBER + 2);
        Assert.assertThat(5, is(appointmentDtoPagination.getTotalCount()));
        Assert.assertThat(1, is(appointmentDtoPagination.getLstObject().size()));
    }

    @Test
    public void givenADeviceIdAndActivityCodeAndDateRangeAndSortingWhenQueryThenReturnAppointmentDtoList() {
        final List<String> LIST_DEVICEID = Arrays.asList("DeviceID");
        final String ACTIVITY_CODE = "ActivityCode";
        final String START_DATE = "StartDate";
        final String END_DATE = "EndDate";
        final List<ImmutablePair<AppointmentRankEnum, RankEnum>> LIST_RANK = new ArrayList<>();
        LIST_RANK.add(new ImmutablePair<>(AppointmentRankEnum.START_TIME, RankEnum.DESC));
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, LIST_DEVICEID.toString()));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ACTIVITY_CODE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, START_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, END_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("status")));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(LIST_RANK.get(0).getRight().name()), AppointmentRankEnum.getDisplay(LIST_RANK.get(0).getLeft())));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        PowerMockito.when(fhirAppointmentInterface.queryAppointmentList(anyObject())).thenReturn(lstAppointment);
        List<AppointmentDto> lstAppointmentDto = appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(LIST_DEVICEID, ACTIVITY_CODE, START_DATE, END_DATE, LIST_RANK);
        Assert.assertThat(1, is(lstAppointmentDto.size()));
    }

    @Test
    public void givenADeviceIdAndActivityCodeAndDateRangeAndSortingAndPaginationWhenQueryThenReturnAppointmentDtoList() {
        final List<String> LIST_DEVICEID = Arrays.asList("1010");
        final String ACTIVITY_CODE = "ActivityCode";
        final String START_DATE = "2018-03-01";
        final String END_DATE = "2018-03-03";
        final List<ImmutablePair<AppointmentRankEnum, RankEnum>> LIST_RANK = new ArrayList<>();
        final Integer COUNT_PER_PAGE = 5;
        final Integer PAGE_NUMBER = 1;
        LIST_RANK.add(new ImmutablePair<>(AppointmentRankEnum.START_TIME, RankEnum.DESC));
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, LIST_DEVICEID.toString()));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ACTIVITY_CODE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, START_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, END_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("status")));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(LIST_RANK.get(0).getRight().name()), AppointmentRankEnum.getDisplay(LIST_RANK.get(0).getLeft())));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        Pagination<Appointment> appointmentPagination = new Pagination<>();
        appointmentPagination.setTotalCount(5);
        appointmentPagination.setLstObject(lstAppointment);
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(appointmentPagination);
        Pagination<AppointmentDto> appointmentDtoPagination = appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRangeWithPagination(LIST_DEVICEID, ACTIVITY_CODE, START_DATE, END_DATE, LIST_RANK, COUNT_PER_PAGE, PAGE_NUMBER, PAGE_NUMBER + 2);
        Assert.assertThat(5, is(appointmentDtoPagination.getTotalCount()));
        Assert.assertThat(1, is(appointmentDtoPagination.getLstObject().size()));
    }

    @Test
    public void givenAPatientIdAndActivityCodeAndDeviceIdAndDateRangeAndPaginationWhenQueryThenReturnAppointmentDtoPagination() {
        final String PATIENT_ID = "1001";
        final String START_DATE = "2018-03-01";
        final String END_DATE = "2018-03-03";
        final String ACTIVITY_CODE = "DoFirstTreatment";
        final String DEVICE_ID = "DeviceId";
        final List<String> LIST_DEVICEID = Arrays.asList(DEVICE_ID);
        final int COUNT_PER_PAGE = 10;
        final int PAGE_NUMBER = 1;
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, PATIENT_ID));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, LIST_DEVICEID.toString()));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ACTIVITY_CODE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, START_DATE));
        appointmentQueryImmutablePairLinkedHashMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, END_DATE));
        List<Appointment> lstAppointment = MockAppointmentUtil.givenAnAppointmentList();
        Pagination<Appointment> appointmentPagination = new Pagination<>();
        appointmentPagination.setTotalCount(5);
        appointmentPagination.setLstObject(lstAppointment);
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(appointmentPagination);
        Pagination<AppointmentDto> appointmentDtoPagination = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeAndDeviceIdAndDateRangeAndPagination(PATIENT_ID, ACTIVITY_CODE, DEVICE_ID, START_DATE, END_DATE, COUNT_PER_PAGE, PAGE_NUMBER, PAGE_NUMBER + 2);
        Assert.assertThat(5, is(appointmentDtoPagination.getTotalCount()));
        Assert.assertThat(1, is(appointmentDtoPagination.getLstObject().size()));
    }

    @Test
    public void givenDeviceIdListAndDateRangeAndStatusThenReturnPagination(){
        Pagination<Appointment> appointmentPagination = new Pagination<>();
        appointmentPagination.setLstObject(new ArrayList<>());
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(Matchers.anyMap(), Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(appointmentPagination);

        Assert.assertTrue(appointmentAntiCorruptionServiceImp.syncAppointmentListByDeviceIdAndDateRangeAndPagination(new ArrayList<String>(), "startDate", "endDate", new ArrayList<>(), Integer.MAX_VALUE, 1, Integer.MAX_VALUE).getLstObject().isEmpty());
    }

    @Test
    public void givenDeviceIdsAndActivityCodeAndStartDateAndEndDateAndLstRankListThenReturnPaginationAppointmentDto(){
        List<AppointmentDto> appointmentList = new ArrayList<>();
        PowerMockito.mockStatic(AppointmentCache.class);
        PowerMockito.when(AppointmentCache.get(Matchers.anyString(), Matchers.anyString())).thenReturn(appointmentList);

        Pagination<Appointment> appointmentPagination = new Pagination<>();
        appointmentPagination.setLstObject(new ArrayList<>());
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(Matchers.anyMap(), Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(appointmentPagination);

        Assert.assertTrue(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRangeWithPagination(new ArrayList<>(), "activityCode", "2018-02-06", "2018-02-06", new ArrayList<>(), Integer.MAX_VALUE, 1, Integer.MAX_VALUE).getLstObject().isEmpty());
    }

    @Test
    public void givenDeviceIdsAndStatusListAndStartDateAndEndDateThenReturnPaginationAppointmentDto(){
        List<AppointmentDto> appointmentList = new ArrayList<>();
        PowerMockito.mockStatic(AppointmentCache.class);
        PowerMockito.when(AppointmentCache.get(Matchers.anyString(), Matchers.anyString())).thenReturn(appointmentList);

        Pagination<Appointment> appointmentPagination = new Pagination<>();
        appointmentPagination.setLstObject(new ArrayList<>());
        PowerMockito.when(fhirAppointmentInterface.queryPagingAppointmentList(Matchers.anyMap(), Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(appointmentPagination);

        Assert.assertTrue(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDateRangeAndStatusWithPagination(new ArrayList<>(), "2018-02-06", "2018-02-06", new ArrayList<>(), Integer.MAX_VALUE, 1, Integer.MAX_VALUE).getLstObject().isEmpty());
    }
}