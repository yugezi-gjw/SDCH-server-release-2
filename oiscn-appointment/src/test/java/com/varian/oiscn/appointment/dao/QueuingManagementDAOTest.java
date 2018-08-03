package com.varian.oiscn.appointment.dao;

import com.varian.oiscn.appointment.dto.CheckInStatusEnum;
import com.varian.oiscn.appointment.dto.QueuingManagement;
import com.varian.oiscn.appointment.dto.QueuingManagementDTO;
import com.varian.oiscn.appointment.util.MockDatabaseConnection;
import com.varian.oiscn.appointment.util.MockPreparedStatement;
import com.varian.oiscn.appointment.util.MockResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/23.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MockPreparedStatement.class)
public class QueuingManagementDAOTest {

    private QueuingManagementDAO queuingManagementDAO;
    private Connection conn;

    @Before
    public void setup() {
        queuingManagementDAO = new QueuingManagementDAO();
        conn = PowerMockito.mock(MockDatabaseConnection.class);
    }

    @Test
    public void givenQueuingManagementDTOWhenCreateThenReturnId() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString(), Matchers.anyInt())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);

        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString(1)).thenReturn("5");

        String result = queuingManagementDAO.create(conn, getQueuingManagement());
        Assert.assertTrue(result.equals("5"));
    }

    @Test
    public void givenQueuingManagementDTOWhenUpdateIdxThenReturnInt() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        int result = queuingManagementDAO.updateIdx(conn, getQueuingManagement());
        Assert.assertTrue(result == 1);
    }

    @Test
    public void givenAppointmentIdThenReturnIfThePatientHasCheckedIn() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);

        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        Assert.assertEquals(true, queuingManagementDAO.ifAlreadyCheckedIn(conn, "appointmentId"));
    }

    @Test
    public void givenUidAndAriaAppointmentWhenUpdateUid2AriaAppointmentIdThenReturnInt() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        Assert.assertTrue(1 == queuingManagementDAO.updateUid2AriaAppointmentId(conn, "uid", "12121"));
    }

    @Test
    public void testUpdateStatusAndIdx() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        QueuingManagement queuingManagement = getQueuingManagement();
        queuingManagement.setId("232");
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        int r = queuingManagementDAO.updateStatusAndIdx(conn,queuingManagement);
        Assert.assertTrue(r == 1);
        queuingManagement.setId("");
        queuingManagement.setAppointmentId("1212");
        r = queuingManagementDAO.updateStatusAndIdx(conn,queuingManagement);
        Assert.assertTrue(r == 1);
    }

    @Test
    public void testQueryMaxCheckIdxByCheckInTime() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getInt(1)).thenReturn(3);
        QueuingManagementDTO queuingManagement =getQueuingManagementDTO();
        int idx = queuingManagementDAO.queryMaxCheckIdxByCheckInTime(conn,queuingManagement);
        Assert.assertTrue(idx == 3);
    }

    @Test
    public void testQueryList() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        QueuingManagementDTO dto = getQueuingManagementDTO();
        dto.setAppointmentIdList(Arrays.asList("1212","1222"));
        dto.setStartTimeStart(new Date());
        dto.setStartTimeEnd(new Date());
        dto.setCheckInStartTime(new Date());
        dto.setCheckInEndTime(new Date());
        dto.setCheckInStatusList(Arrays.asList(CheckInStatusEnum.CALLING,CheckInStatusEnum.WAITING));
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("id")).thenReturn("11");
        PowerMockito.when(rs.getString("appointmentId")).thenReturn("112");
        PowerMockito.when(rs.getString("activityCode")).thenReturn("DoCT");
        PowerMockito.when(rs.getString("hisId")).thenReturn("H1212");
        PowerMockito.when(rs.getString("encounterId")).thenReturn("1232");
        PowerMockito.when(rs.getInt("patientId")).thenReturn(234);
        PowerMockito.when(rs.getString("deviceId")).thenReturn("1221");
        PowerMockito.when(rs.getString("checkInStatus")).thenReturn(CheckInStatusEnum.CALLING.name());
        PowerMockito.when(rs.getInt("checkInIdx")).thenReturn(2);
        PowerMockito.when(rs.getTimestamp("startTime")).thenReturn(new Timestamp(new Date().getTime()));
        PowerMockito.when(rs.getTimestamp("checkInTime")).thenReturn(new Timestamp(new Date().getTime()));

        List<QueuingManagement> list = queuingManagementDAO.queryList(conn,dto);
        Assert.assertTrue(list.size() == 1);

    }
    private QueuingManagement getQueuingManagement() {
        return new QueuingManagement() {{
            setCheckInStatus(CheckInStatusEnum.WAITING);
            setEncounterId("12345");
            setCheckInTime(new Date());
            setStartTime(new Date());
            setCheckInIdx(10);
        }};
    }

    private QueuingManagementDTO getQueuingManagementDTO(){
        return new QueuingManagementDTO(){{
            setAppointmentId("1212");
            setCheckInStatus(CheckInStatusEnum.WAITING);
            setEncounterId("12345");
            setCheckInTime(new Date());
            setStartTime("2018-03-15 12:11:11");
            setCheckInIdx(10);
            setActivityCode("DoCT");
            setHisId("h121");
            setPatientSer(12121L);
            setDeviceId("1222");
        }};
    }


}
