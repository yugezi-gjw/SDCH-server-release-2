package com.varian.oiscn.appointment.dao;

import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.util.MockDatabaseConnection;
import com.varian.oiscn.appointment.util.MockPreparedStatement;
import com.varian.oiscn.appointment.util.MockResultSet;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Date;

import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, MockPreparedStatement.class, DateUtil.class})
@SuppressStaticInitializationFor("com.varian.oiscn.connection.ConnectionPool")
public class TreatmentAppointmentDAOTest {

    private Connection con;
    private UserContext context;
    private TreatmentAppointmentDTO dto;

    private TreatmentAppointmentDAO dao;

    @Before
    public void setup() throws Exception {
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

        java.sql.Date date = PowerMockito.mock(java.sql.Date.class);
        PowerMockito.mockStatic(DateUtil.class);
        PowerMockito.when(DateUtil.transferDateFromUtilToSql(anyObject())).thenReturn(date);
        PowerMockito.when(DateUtil.formatDate(anyObject(), anyString())).thenCallRealMethod();
        PowerMockito.when(DateUtil.parse(anyString())).thenReturn(new Date());
        context = mock(UserContext.class);
        Login login = mock(Login.class);
        when(context.getLogin()).thenReturn(login);
        String username = "username";
        when(login.getUsername()).thenReturn(username);

    }

    @Test
    public void givenHisIdListAndDateReturnPagination() {
        try {
            List<Long> patientSerList = new ArrayList<>();
            patientSerList.add(12121L);
            patientSerList.add(12929L);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(2)).thenReturn("appointmentid");
            PowerMockito.when(rs.getString(3)).thenReturn("hisid");
            PowerMockito.when(rs.getString(4)).thenReturn("patientid");
            PowerMockito.when(rs.getString(5)).thenReturn("deviceid");
            PowerMockito.when(rs.getString(6)).thenReturn("starttime");
            PowerMockito.when(rs.getString(7)).thenReturn("endtime");
            PowerMockito.when(rs.getString(8)).thenReturn("activitycode");
            PowerMockito.when(rs.getString(9)).thenReturn("status");

            TreatmentAppointmentDAO treatmentAppointmentDAO = new TreatmentAppointmentDAO(new UserContext());
            Assert.assertEquals(2, treatmentAppointmentDAO.queryByPatientSerListAndDatePagination(con, patientSerList, new Date(), new Date(), "asc", "5", "1").getTotalCount());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void givenDeviceIdListWhenQueryByDeviceIdListAndDatePaginationThenReturnPagination() {
        try {
            List<String> deviceIdList = new ArrayList<>();
            deviceIdList.add("1001");
            deviceIdList.add("1089");

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(2)).thenReturn("appointmentid");
            PowerMockito.when(rs.getString(3)).thenReturn("hisid");
            PowerMockito.when(rs.getString(4)).thenReturn("patientid");
            PowerMockito.when(rs.getString(5)).thenReturn("deviceid");
            PowerMockito.when(rs.getString(6)).thenReturn("starttime");
            PowerMockito.when(rs.getString(7)).thenReturn("endtime");
            PowerMockito.when(rs.getString(8)).thenReturn("activitycode");
            PowerMockito.when(rs.getString(9)).thenReturn("status");

            TreatmentAppointmentDAO treatmentAppointmentDAO = new TreatmentAppointmentDAO(new UserContext());
            Assert.assertEquals(2, treatmentAppointmentDAO.queryByDeviceIdListAndDatePagination(con, deviceIdList, new Date(), new Date(), Arrays.asList(AppointmentStatusEnum.BOOKED), "asc", "5", "1").getTotalCount());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void givenHisIdOrDeviceIdListWhenQueryByHisIdOrDeviceIdListAndDatePaginationnThenReturnPagination() {
        try {
            List<String> deviceIdList = new ArrayList<>();
            deviceIdList.add("1001");
            deviceIdList.add("1089");
            List<Long> patientSerList = new ArrayList<>();
            patientSerList.add(121212L);
            patientSerList.add(12778L);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(2)).thenReturn("appointmentid");
            PowerMockito.when(rs.getString(3)).thenReturn("hisid");
            PowerMockito.when(rs.getString(4)).thenReturn("patientid");
            PowerMockito.when(rs.getString(5)).thenReturn("deviceid");
            PowerMockito.when(rs.getString(6)).thenReturn("starttime");
            PowerMockito.when(rs.getString(7)).thenReturn("endtime");
            PowerMockito.when(rs.getString(8)).thenReturn("activitycode");
            PowerMockito.when(rs.getString(9)).thenReturn("status");

            TreatmentAppointmentDAO treatmentAppointmentDAO = new TreatmentAppointmentDAO(new UserContext());
            Assert.assertEquals(2, treatmentAppointmentDAO.queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, patientSerList, deviceIdList, new Date(), new Date(), Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED), "asc", "5", "1").getTotalCount());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenIdWhenQueryByIdOrAppointmentIdThenReturnObject() throws SQLException {
        String idOrAppointmentId = "10";
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString("id")).thenReturn(idOrAppointmentId);
        PowerMockito.when(rs.getString("appointmentId")).thenReturn("APPOINTMENTID");
        PowerMockito.when(rs.getString("hisId")).thenReturn("HISID");
        PowerMockito.when(rs.getString("patientId")).thenReturn("PATIENTID");
        PowerMockito.when(rs.getString("deviceId")).thenReturn("DEVICEID");
        PowerMockito.when(rs.getTimestamp("startTime")).thenReturn(new Timestamp(new Date().getTime()));
        PowerMockito.when(rs.getTimestamp("endTime")).thenReturn(new Timestamp(new Date().getTime()));
        PowerMockito.when(rs.getString("activityCode")).thenReturn("ACTIVITYCODE");
        PowerMockito.when(rs.getString("status")).thenReturn("STATUS");

        dao = new TreatmentAppointmentDAO(context);
        TreatmentAppointmentDTO treatmentAppointmentDTO = dao.queryByUidOrAppointmentId(con, idOrAppointmentId);
        Assert.assertTrue(treatmentAppointmentDTO.getId().equals(idOrAppointmentId));
    }

    @Test
    public void testCreate() {
        String newId = "newId";
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString(), anyInt())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeUpdate()).thenReturn(1);
            PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getString(1)).thenReturn(newId);

        } catch (SQLException e) {
            Assert.fail();
        }

        try {
            dao = new TreatmentAppointmentDAO(context);
            dto = mock(TreatmentAppointmentDTO.class);
            PowerMockito.when(dto.getEncounterId()).thenReturn("1221");
            String actual = dao.create(con, dto);
            Assert.assertEquals(newId, actual);
        } catch (SQLException e) {
            Assert.fail();
        }

    }

    @Test
    public void testQueryByHisIdAndDateAndActivity() {
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            when(rs.getString(anyString())).thenReturn("returnString");
            when(rs.getDate(anyString())).thenReturn(new java.sql.Date(System.currentTimeMillis()));

            dao = new TreatmentAppointmentDAO(context);
            dto = mock(TreatmentAppointmentDTO.class);
            Long patientSer = mock(Long.class);

            String activityCode = mock(String.class);
            Date day = mock(Date.class);
            List<TreatmentAppointmentDTO> actual = dao.queryByPatientSerAndDateAndActivity(con, patientSer, day, activityCode);
            Assert.assertEquals(2, actual.size());
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdate() {
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeUpdate()).thenReturn(1);

            when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            when(rs.getString(anyString())).thenReturn("returnString");
            when(rs.getDate(anyString())).thenReturn(new java.sql.Date(System.currentTimeMillis()));

            dao = new TreatmentAppointmentDAO(context);
            dto = mock(TreatmentAppointmentDTO.class);
            String hisId = mock(String.class);

            boolean actual = dao.update(con, dto, "123");
            Assert.assertTrue(actual);
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void givenAppointmentIdAndStatusWhenUpdateStatusByAppointmentIdThenReturnId() {
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            PowerMockito.when(ps.executeUpdate()).thenReturn(1);
            dao = new TreatmentAppointmentDAO(context);
            String appointmentId = "12345";
            String status = "fulfilled";

            int effect = dao.updateStatusByAppointmentId(con, appointmentId, status);
            Assert.assertTrue(1 == effect);
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateByStartTimeAndHisIdAndActivity() {
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeUpdate()).thenReturn(1);

            when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            when(rs.getString(anyString())).thenReturn("returnString");
            when(rs.getDate(anyString())).thenReturn(new java.sql.Date(System.currentTimeMillis()));

            dao = new TreatmentAppointmentDAO(context);
            dto = mock(TreatmentAppointmentDTO.class);
            int actual = dao.updateByStartTimeAndPatientSerAndActivity(con, dto);
            Assert.assertEquals(1, actual);
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryByAppointmentId() {
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            when(rs.getString(anyString())).thenReturn("returnString");
            when(rs.getDate(anyString())).thenReturn(new java.sql.Date(System.currentTimeMillis()));

            dao = new TreatmentAppointmentDAO(context);
            dto = mock(TreatmentAppointmentDTO.class);
            String appointmentId = mock(String.class);

            TreatmentAppointmentDTO actual = dao.queryByAppointmentId(con, appointmentId);
            Assert.assertNotNull(actual);
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryByHisId() {
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            when(rs.getString(anyString())).thenReturn("returnString");
            when(rs.getDate(anyString())).thenReturn(new java.sql.Date(System.currentTimeMillis()));

            dao = new TreatmentAppointmentDAO(context);
            dto = mock(TreatmentAppointmentDTO.class);
            Long patientSer = mock(Long.class);

            List<TreatmentAppointmentDTO> actual = dao.queryByPatientSer(con, patientSer);
            Assert.assertEquals(2, actual.size());
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateStatusByHisIdAndActivity() {
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeUpdate()).thenReturn(2);

            when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            when(rs.getString(anyString())).thenReturn("returnString");
            when(rs.getDate(anyString())).thenReturn(new java.sql.Date(System.currentTimeMillis()));

            dao = new TreatmentAppointmentDAO(context);
            dto = mock(TreatmentAppointmentDTO.class);

            Long patientSer = mock(Long.class);
            String activityCode = mock(String.class);
            String status = mock(String.class);
            int actual = dao.updateStatusByPatientSerAndActivity(con, patientSer, activityCode, status);
            Assert.assertEquals(2, actual);
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSelectTotalAndCompletedTreatmentThenReturnMap(){
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true);
            PowerMockito.when(rs.getInt("totalNum")).thenReturn(12);
            PowerMockito.when(rs.getInt("fulfilledNum")).thenReturn(2);
            dao = new TreatmentAppointmentDAO(context);
            Map<String,Integer> map = dao.selectTotalAndCompletedTreatment(con,0L,new java.util.Date(),new java.util.Date());
            Assert.assertNotNull(map);
            Assert.assertFalse(map.isEmpty());
            Assert.assertTrue(map.get("totalNum")==12);
            Assert.assertTrue(map.get("completedNum")==2);

        }catch (SQLException e){
            Assert.fail();
        }
    }

    @Test
    public void givenHisIdAndEncounterIdThenReturnList(){
        try {
            String idOrAppointmentId = "10";
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString("id")).thenReturn(idOrAppointmentId);
            PowerMockito.when(rs.getString("appointmentId")).thenReturn("APPOINTMENTID");
            PowerMockito.when(rs.getString("hisId")).thenReturn("HISID");
            PowerMockito.when(rs.getLong("patientSer")).thenReturn(12L);
            PowerMockito.when(rs.getString("deviceId")).thenReturn("DEVICEID");
            PowerMockito.when(rs.getTimestamp("startTime")).thenReturn(new Timestamp(new Date().getTime()));
            PowerMockito.when(rs.getTimestamp("endTime")).thenReturn(new Timestamp(new Date().getTime()));
            PowerMockito.when(rs.getString("activityCode")).thenReturn("ACTIVITYCODE");
            PowerMockito.when(rs.getString("status")).thenReturn("STATUS");
            dao = new TreatmentAppointmentDAO(context);
            List<TreatmentAppointmentDTO> treatmentAppointmentDTOList = dao.queryByPatientSerAndEncounterId(con, 12L, 100, Arrays.asList(AppointmentStatusEnum.BOOKED,AppointmentStatusEnum.FULFILLED));
            Assert.assertEquals(1, treatmentAppointmentDTOList.size());
            Assert.assertEquals(idOrAppointmentId, treatmentAppointmentDTOList.get(0).getId());

        }catch (SQLException e){
            Assert.fail();
        }
    }
}
