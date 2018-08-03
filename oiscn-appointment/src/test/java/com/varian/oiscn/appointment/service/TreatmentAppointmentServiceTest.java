package com.varian.oiscn.appointment.service;

import com.varian.oiscn.appointment.dao.TreatmentAppointmentDAO;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.util.MockDatabaseConnection;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.user.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, TreatmentAppointmentDAO.class,SystemConfigPool.class})
@SuppressStaticInitializationFor("com.varian.oiscn.connection.ConnectionPool")
public class TreatmentAppointmentServiceTest {

    private UserContext context;
    private Connection con;
    private TreatmentAppointmentDAO dao;
    private TreatmentAppointmentDTO dto;
    private TreatmentAppointmentService service;
    private SQLException sqlException;

    @Before
    public void setup() throws Exception {
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

        dao = mock(TreatmentAppointmentDAO.class);
        whenNew(TreatmentAppointmentDAO.class).withArguments(context).thenReturn(dao);

        PowerMockito.mockStatic(SystemConfigPool.class);

        sqlException = mock(SQLException.class);

        dto = mock(TreatmentAppointmentDTO.class);

        context = mock(UserContext.class);

    }

    @Test
    public void givenHisIdListAndDateReturnPagination() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Pagination<TreatmentAppointmentDTO> dtoList = new Pagination<>();
        Date day = mock(Date.class);
        String countPerPage = mock(String.class);
        String pageNumber = mock(String.class);
        String sort = mock(String.class);
        List<Long> patientSerList = mock(List.class);
        try {
            when(dao.queryByPatientSerListAndDatePagination(con, patientSerList, day, day, sort, countPerPage, pageNumber)).thenReturn(dtoList);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        Pagination<TreatmentAppointmentDTO> actual = service.queryByPatientSerListAndDatePagination(patientSerList, day, day, sort, countPerPage, pageNumber);
        Assert.assertSame(dtoList, actual);
    }

    @Test
    public void givenHisIdListAndDateReturnPaginationWithException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Date day = mock(Date.class);
        String countPerPage = mock(String.class);
        String pageNumber = mock(String.class);
        List<Long> patientSerList = mock(List.class);
        String sort = mock(String.class);
        try {
            when(dao.queryByPatientSerListAndDatePagination(con, patientSerList, day, day, sort, countPerPage, pageNumber)).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        Pagination<TreatmentAppointmentDTO> actual = service.queryByPatientSerListAndDatePagination(patientSerList, day, day, sort, countPerPage, pageNumber);
        Assert.assertEquals(0, actual.getTotalCount());
    }

    @Test
    public void testTreatmentAppointmentService() {
        service = new TreatmentAppointmentService(context);
        Assert.assertNotNull(service.dao);
    }

    @Test
    public void testCreate() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        String expected = mock(String.class);
        try {
            when(dao.create(con, dto)).thenReturn(expected);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        String actual = service.create(dto);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCreateWithSQLException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        try {
            when(dao.create(con, dto)).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        String actual = service.create(dto);

        Assert.assertEquals(StringUtils.EMPTY, actual);
    }

    @Test
    public void testCreateList() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        TreatmentAppointmentDTO dto1 = mock(TreatmentAppointmentDTO.class);
        TreatmentAppointmentDTO dto2 = mock(TreatmentAppointmentDTO.class);
        dtoList.add(dto1);
        dtoList.add(dto2);

        String expected1 = mock(String.class);
        String expected2 = mock(String.class);
        try {
            when(dao.create(anyObject(), anyObject())).thenReturn(expected1).thenReturn(expected2);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        List<String> actual = service.createList(dtoList);

        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(expected1, actual.get(0));
        Assert.assertEquals(expected2, actual.get(1));
    }

    @Test
    public void testCreateListWithSQLException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto);
        dtoList.add(dto);
        try {
            when(dao.create(con, dto)).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        List<String> actual = service.createList(dtoList);

        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void testUpdateByStartTimeAndHisIdAndActivity() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        TreatmentAppointmentDTO dto1 = mock(TreatmentAppointmentDTO.class);
        TreatmentAppointmentDTO dto2 = mock(TreatmentAppointmentDTO.class);
        dtoList.add(dto1);
        dtoList.add(dto2);

        String expected1 = "notNull";
        try {
            when(dao.updateByStartTimeAndPatientSerAndActivity(con, dto1)).thenReturn(0);
            when(dao.updateByStartTimeAndPatientSerAndActivity(con, dto2)).thenReturn(1);
            when(dao.create(con, dto1)).thenReturn(expected1);

        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        int actual = service.updateByStartTimeAndPatientSerAndActivity(dtoList);

        Assert.assertEquals(2, actual);
    }

    @Test
    public void testUpdateByStartTimeAndHisIdAndActivityWithSqlException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto);
        dtoList.add(dto);
        try {
            when(dao.updateByStartTimeAndPatientSerAndActivity(con, dto)).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        int actual = service.updateByStartTimeAndPatientSerAndActivity(dtoList);

        Assert.assertEquals(0, actual);
    }

    @Test
    public void testQueryByHisIdAndDateAndActivity() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        TreatmentAppointmentDTO dto1 = mock(TreatmentAppointmentDTO.class);
        TreatmentAppointmentDTO dto2 = mock(TreatmentAppointmentDTO.class);
        dtoList.add(dto1);
        dtoList.add(dto2);

        Long patientSer = mock(Long.class);
        String activityCode = mock(String.class);
        Date day = mock(Date.class);
        try {
            when(dao.queryByPatientSerAndDateAndActivity(con, patientSer, day, activityCode)).thenReturn(dtoList);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        List<TreatmentAppointmentDTO> actual = service.queryByPatientSerAndDateAndActivity(patientSer, day, activityCode);

        Assert.assertSame(dtoList, actual);
    }

    @Test
    public void tesQueryByHisIdAndDateAndActivityWithSQLException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto);
        dtoList.add(dto);

        Long patientSer = mock(Long.class);
        String activityCode = mock(String.class);
        Date day = mock(Date.class);

        try {
            when(dao.queryByPatientSerAndDateAndActivity(con, patientSer, day, activityCode)).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }


        List<TreatmentAppointmentDTO> actual = service.queryByPatientSerAndDateAndActivity(patientSer, day, activityCode);

        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void testUpdateStatusByHisIdAndActivity() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        TreatmentAppointmentDTO dto1 = mock(TreatmentAppointmentDTO.class);
        TreatmentAppointmentDTO dto2 = mock(TreatmentAppointmentDTO.class);
        dtoList.add(dto1);
        dtoList.add(dto2);

        Long patientSer = mock(Long.class);
        String activityCode = mock(String.class);
        String status = mock(String.class);
        int affectedRow = 3;
        try {
            when(dao.updateStatusByPatientSerAndActivity(con, patientSer, activityCode, status)).thenReturn(affectedRow);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }

        int actual = service.updateStatusByPatientSerAndActivity(patientSer, activityCode, status);

        Assert.assertSame(affectedRow, actual);
    }

    @Test
    public void testUpdateStatusByHisIdAndActivityWithSQLException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto);
        dtoList.add(dto);

        Long patientSer = mock(Long.class);
        String activityCode = mock(String.class);
        String status = mock(String.class);

        try {
            when(dao.updateStatusByPatientSerAndActivity(con, patientSer, activityCode, status)).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }


        int actual = service.updateStatusByPatientSerAndActivity(patientSer, activityCode, status);

        Assert.assertEquals(0, actual);
    }

    @Test
    public void givenDeviceIdListWhenQueryByDeviceIdListAndDatePaginationThenReturnPagination() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto);
        dtoList.add(dto);
        Pagination<TreatmentAppointmentDTO> pagination = new Pagination<>();
        pagination.setLstObject(dtoList);
        List<String> deviceIdList = Arrays.asList("1001", "1098");
        Date day = new Date();
        try {
            when(dao.queryByDeviceIdListAndDatePagination(con, deviceIdList, day, day, Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", null, null)).thenReturn(pagination);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }
        Pagination<TreatmentAppointmentDTO> retsult = service.queryByDeviceIdListAndDatePagination(deviceIdList, day, day, Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", null, null);
        Assert.assertTrue(retsult.equals(pagination));
    }

    @Test
    public void testQueryByDeviceIdListAndDatePaginationThrowException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<String> deviceIdList = Arrays.asList("1001", "1098");
        Date day = new Date();
        try {
            when(dao.queryByDeviceIdListAndDatePagination(con, deviceIdList, day, day, Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", null, null)).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }
        Pagination<TreatmentAppointmentDTO> result = service.queryByDeviceIdListAndDatePagination(deviceIdList, day, day, Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", null, null);
        Assert.assertEquals(0, result.getTotalCount());
    }

    @Test
    public void givenHisIdAndStatusWhenQueryByHisIdAndStatusThenReturnList() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        Long patientSer = 12121L;
        List<AppointmentStatusEnum> statusEnumList = Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED);
        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto);
        dtoList.add(dto);
        try {
            when(dao.queryByPatientSer(con, patientSer, statusEnumList)).thenReturn(dtoList);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }
        List<TreatmentAppointmentDTO> result = service.queryByPatientSerAndStatus(patientSer, statusEnumList);
        Assert.assertTrue(result.equals(dtoList));
    }

    @Test
    public void testQueryByPatientSerAndStatusThrowException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        Long patientSer = 12121L;
        List<AppointmentStatusEnum> statusEnumList = Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED);
        try {
            when(dao.queryByPatientSer(con, patientSer, statusEnumList)).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail("SQLException failed.");
        }
        List<TreatmentAppointmentDTO> result = service.queryByPatientSerAndStatus(patientSer, statusEnumList);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void givenHisIdActivityCodeStatEndTimeStatusWhenUpdateStatusByHisIdAndActivityAndStartTimeAndEndTimeThenReturnInteger() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Long patientSer = 1212L;
        String activityCode = "DoFirstTreatment";
        String status = AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED);
        Date statTime = new Date();
        Date endTime = new Date();
        try {
            when(dao.updateStatusByPatientSerAndActivityAndStartTimeAndEndTime(con, patientSer, activityCode, statTime, endTime, status)).thenReturn(1);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
        int result = service.updateStatusByPatientSerAndActivityAndStartTimeAndEndTime(patientSer, activityCode, statTime, endTime, status);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void testUpdateStatusByPatientSerAndActivityAndStartTimeAndEndTimeThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Long patientSer = 1212L;
        String activityCode = "DoFirstTreatment";
        String status = AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED);
        Date statTime = new Date();
        Date endTime = new Date();
        when(dao.updateStatusByPatientSerAndActivityAndStartTimeAndEndTime(con, patientSer, activityCode, statTime, endTime, status)).thenThrow(sqlException);
        int result = service.updateStatusByPatientSerAndActivityAndStartTimeAndEndTime(patientSer, activityCode, statTime, endTime, status);
        Assert.assertTrue(result == 0);
    }

    @Test
    public void givenDateRangeWhenQueryByDatePaginationThenReturnPagination() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Date day = new Date();
        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto);
        dtoList.add(dto);
        Pagination<TreatmentAppointmentDTO> pagination = new Pagination<>();
        pagination.setLstObject(dtoList);
        try {
            when(dao.queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, null, null, day, day, Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", "15", "1")).thenReturn(pagination);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
        Pagination<TreatmentAppointmentDTO> result = service.queryByDatePagination(day, day, "asc", "15", "1");
        Assert.assertTrue(result.equals(pagination));
    }

    @Test
    public void testQueryByDatePaginationThrowException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Date day = new Date();
        try {
            when(dao.queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, null, null, day, day, Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", "15", "1")).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
        Pagination<TreatmentAppointmentDTO> result = service.queryByDatePagination(day, day, "asc", "15", "1");
        Assert.assertEquals(0, result.getTotalCount());
    }

    @Test
    public void givenAppointmentIdAndStatusWhenUpdateStatusByAppointmentIdThenReturnInt() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        String appointmentId = "id";
        AppointmentStatusEnum status = AppointmentStatusEnum.FULFILLED;

        PowerMockito.when(dao.updateStatusByAppointmentId(con, appointmentId, AppointmentStatusEnum.getDisplay(status))).thenReturn(1);
        int result = service.updateStatusByAppointmentId(appointmentId, status);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void testUpdateStatusByAppointmentIdThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        String appointmentId = "id";
        AppointmentStatusEnum status = AppointmentStatusEnum.FULFILLED;

        PowerMockito.when(dao.updateStatusByAppointmentId(con, appointmentId, AppointmentStatusEnum.getDisplay(status))).thenThrow(sqlException);
        int result = service.updateStatusByAppointmentId(appointmentId, status);
        Assert.assertTrue(result == 0);
    }

    @Test
    public void givenIdOrAppointmentWhenQueryByIdOrAppointmentIdThenReturnObject() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        String idOrAppointmentId = "id";
        TreatmentAppointmentDTO treatmentAppointmentDTO = new TreatmentAppointmentDTO() {{
            setId(idOrAppointmentId);
        }};

        PowerMockito.when(dao.queryByUidOrAppointmentId(con, idOrAppointmentId)).thenReturn(treatmentAppointmentDTO);
        TreatmentAppointmentDTO result = service.queryByUidOrAppointmentId(idOrAppointmentId);
        Assert.assertTrue(result.equals(treatmentAppointmentDTO));
    }

    @Test
    public void testQueryByUidOrAppointmentIdThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        String idOrAppointmentId = "id";
        PowerMockito.when(dao.queryByUidOrAppointmentId(con, idOrAppointmentId)).thenThrow(sqlException);
        TreatmentAppointmentDTO result = service.queryByUidOrAppointmentId(idOrAppointmentId);
        Assert.assertNull(result);
    }

    @Test
    public void givenDateRangeWhenQueryByHisIdListAndDeviceIdAndDatePaginationThenReturnPagination() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<Long> patientSerList = mock(List.class);
        String deviceId = mock(String.class);

        Date day = new Date();
        List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto);
        dtoList.add(dto);
        Pagination<TreatmentAppointmentDTO> pagination = new Pagination<>();
        pagination.setLstObject(dtoList);
        try {
            when(dao.queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, patientSerList, Arrays.asList(deviceId), day, day, Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", "15", "1")).thenReturn(pagination);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
        Pagination<TreatmentAppointmentDTO> result = service.queryByPatientSerListAndDeviceIdAndDatePagination(patientSerList, deviceId, day, day, "asc", "15", "1");
        Assert.assertTrue(result.equals(pagination));
    }

    @Test
    public void testQueryByPatientSerListAndDeviceIdAndDatePaginationThrowException() {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        List<Long> patientSerList = mock(List.class);
        String deviceId = mock(String.class);

        Date day = new Date();
        Pagination<TreatmentAppointmentDTO> pagination = new Pagination<>();
        try {
            when(dao.queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, patientSerList, Arrays.asList(deviceId), day, day, Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", "15", "1")).thenThrow(sqlException);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
        Pagination<TreatmentAppointmentDTO> result = service.queryByPatientSerListAndDeviceIdAndDatePagination(patientSerList, deviceId, day, day, "asc", "15", "1");
        Assert.assertTrue(result.equals(pagination));
    }

    @Test
    public void givenPatientIdAndActivityCodeThenReturnList() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Long patientId = 11212L;
        String activityCode = "activityCode";

        PowerMockito.when(dao.queryByPatientSerAndActivityCode(con, patientId, activityCode)).thenReturn(new ArrayList<>());
        List<TreatmentAppointmentDTO> result = service.queryTreatmentsAppointmentByPatientId(patientId, activityCode);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testQueryTreatmentsAppointmentByPatientIdThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Long patientId = 11212L;
        String activityCode = "activityCode";

        PowerMockito.when(dao.queryByPatientSerAndActivityCode(con, patientId, activityCode)).thenThrow(sqlException);
        List<TreatmentAppointmentDTO> result = service.queryTreatmentsAppointmentByPatientId(patientId, activityCode);
        Assert.assertNull(result);
    }

    @Test
    public void givenPatientIdAndEncounterIdThenReturnList() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Long patientId = 12L;
        int encounterId = 100;

        TreatmentAppointmentDTO treatmentAppointmentDTO = new TreatmentAppointmentDTO();
        treatmentAppointmentDTO.setId("Id");
        PowerMockito.when(dao.queryByPatientSerAndEncounterId(con, patientId, encounterId, Arrays.asList(AppointmentStatusEnum.BOOKED,AppointmentStatusEnum.FULFILLED))).thenReturn(Arrays.asList(treatmentAppointmentDTO));
        List<TreatmentAppointmentDTO> result = service.queryAppointmentListByPatientSerAndEncounterId(patientId, encounterId);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Id", result.get(0).getId());
    }

    @Test
    public void testQueryAppointmentListByPatientSerAndEncounterIdThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;

        Long patientId = 12L;
        int encounterId = 100;

        TreatmentAppointmentDTO treatmentAppointmentDTO = new TreatmentAppointmentDTO();
        treatmentAppointmentDTO.setId("Id");
        PowerMockito.when(dao.queryByPatientSerAndEncounterId(con, patientId, encounterId, Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED))).thenThrow(sqlException);
        List<TreatmentAppointmentDTO> result = service.queryAppointmentListByPatientSerAndEncounterId(patientId, encounterId);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testWhenQueryTotalAndCompletedTreatmentThenReturnMap() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        Long patientSer = 12121L;
        Date startTime = new Date();
        Date endTime = new Date();
        Map<String,Integer> map = new HashMap<String,Integer>(){{
            put("totalNum",12);
            put("completedNum",1);
        }};
        PowerMockito.when(dao.selectTotalAndCompletedTreatment(con,patientSer,startTime,endTime)).thenReturn(map);
        Map<String,Integer> rmap = service.queryTotalAndCompletedTreatment(patientSer,startTime,endTime);
        Assert.assertNotNull(rmap);
        Assert.assertFalse(rmap.isEmpty());
        Assert.assertTrue(rmap.get("totalNum") == 12);
        Assert.assertTrue(rmap.get("completedNum") == 1);
    }

    @Test
    public void testWhenQueryTotalAndCompletedTreatmentThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        Long patientSer = 12121L;
        Date startTime = new Date();
        Date endTime = new Date();
        Map<String, Integer> map = new HashMap<String, Integer>() {{
            put("totalNum", 12);
            put("completedNum", 1);
        }};
        PowerMockito.when(dao.selectTotalAndCompletedTreatment(con, patientSer, startTime, endTime)).thenThrow(sqlException);
        Map<String, Integer> rmap = service.queryTotalAndCompletedTreatment(patientSer, startTime, endTime);
        Assert.assertNull(rmap);
    }

    @Test
    public void testUpdate() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        TreatmentAppointmentDTO dto = new TreatmentAppointmentDTO();
        String id = "2212";
        PowerMockito.when(dao.update(con,dto,id)).thenReturn(true);
        Assert.assertTrue(service.update(dto,id));
    }

    @Test
    public void testUpdateThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        TreatmentAppointmentDTO dto = new TreatmentAppointmentDTO();
        String id = "2212";
        PowerMockito.when(dao.update(con, dto, id)).thenThrow(sqlException);
        Assert.assertFalse(service.update(dto, id));
    }

    @Test
    public void testQueryTheFirstTreatmentAppointmentByPatientId() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        String patientId = "1221";
        String activityCode = "DoTreatment";
        List<TreatmentAppointmentDTO> dtoList = Arrays.asList(new TreatmentAppointmentDTO(){{
            setPatientSer(new Long(patientId));
        }});
        PowerMockito.when(dao.queryByPatientSerAndActivityCode(con,new Long(patientId),activityCode)).thenReturn(dtoList);
        TreatmentAppointmentDTO dto = service.queryTheFirstTreatmentAppointmentByPatientSer(new Long(patientId),activityCode);
        Assert.assertTrue(dto.getPatientSer().equals(new Long(patientId)));
    }

    @Test
    public void testQueryTheFirstTreatmentAppointmentByPatientIdThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        String patientId = "1221";
        String activityCode = "DoTreatment";
        PowerMockito.when(dao.queryByPatientSerAndActivityCode(con, new Long(patientId), activityCode)).thenThrow(sqlException);
        TreatmentAppointmentDTO dto = service.queryTheFirstTreatmentAppointmentByPatientSer(new Long(patientId), activityCode);
        Assert.assertNull(dto);
    }

    @Test
    public void testQueryAppointmentListByHisIdAndDeviceId() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        List<TreatmentAppointmentDTO> list = new ArrayList<>();
        Long patientSer = 12121L;
        String deviceId = "1111";
        PowerMockito.when(dao.queryByPatientSerAndDeviceId(con, patientSer, deviceId, Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED))).thenReturn(list);
        List<TreatmentAppointmentDTO> resultList = service.queryAppointmentListByPatientSerAndDeviceId(patientSer,deviceId);
        Assert.assertTrue(resultList.equals(list));
    }

    @Test
    public void testQueryAppointmentListByHisIdAndDeviceIdThrowException() throws SQLException {
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        Long patientSer = 12121L;
        String deviceId = "1111";
        PowerMockito.when(dao.queryByPatientSerAndDeviceId(con, patientSer, deviceId, Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED))).thenThrow(sqlException);
        List<TreatmentAppointmentDTO> resultList = service.queryAppointmentListByPatientSerAndDeviceId(patientSer, deviceId);
        Assert.assertEquals(0, resultList.size());
    }

    @Test
    public void testSearchAppointmentFromLocal() throws SQLException {
        String deviceId = "1212";
        service = new TreatmentAppointmentService(context);
        service.dao = this.dao;
        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(true);
        Pagination<TreatmentAppointmentDTO> treatmentAppointmentDTOPage = new Pagination<TreatmentAppointmentDTO>(){{
            setTotalCount(1);
            setLstObject(Arrays.asList(new TreatmentAppointmentDTO(){{
                setHisId("21212");
//                setAppointmentId("1212");
                setEncounterId("121222");
                setPatientSer(1111L);
                setDeviceId(deviceId);
                setStartTime(new Date());
                setActivityCode("DoCT");
                setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
            }}));
        }};
        PowerMockito.when(dao.queryByDeviceIdListAndDatePagination(Matchers.any(), Matchers.anyList(), Matchers.any(), Matchers.any(), Matchers.anyList(),Matchers.anyString(), Matchers.anyString(), Matchers.anyString()))
        .thenReturn(treatmentAppointmentDTOPage);
        PowerMockito.when(dao.queryByPatientSerListAndDatePagination(Matchers.any(), Matchers.anyList(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.anyString(), Matchers.anyString())).thenReturn(treatmentAppointmentDTOPage);
        List<AppointmentDto> dtoList = new ArrayList<>();
        List<AppointmentDto> list = service.searchAppointmentFromLocal(deviceId,"2018-02-02","2018-02-09","",0L,dtoList);
        Assert.assertNotNull(list);
        list = service.searchAppointmentFromLocal("","2018-02-02","2018-02-09","",1111L,dtoList);
        Assert.assertNotNull(list);
    }
}
