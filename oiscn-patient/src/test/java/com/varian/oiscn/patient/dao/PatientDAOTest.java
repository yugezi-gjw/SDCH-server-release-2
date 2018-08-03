package com.varian.oiscn.patient.dao;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.patient.GenderEnum;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.patient.util.MockDatabaseConnection;
import com.varian.oiscn.patient.util.MockPreparedStatement;
import com.varian.oiscn.patient.util.MockResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

/**
 * Created by BHP9696 on 2017/7/27.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, PatientDAO.class, MockPreparedStatement.class})
public class PatientDAOTest {
    private static final String SELECT_SQL = "SELECT id, patientinfo FROM patient WHERE patientinfo->>'hisId'=?";
    private static final String UPDATE_SQL = "UPDATE patient SET LastUpdatedUser=?,LastUpdatedDate=?,patientinfo=?::jsonb WHERE patientinfo->>'hisId'=?";
    private Connection connection;
    private UserContext userContext;
    private Login login;

    @Before
    public void setup() {
        Locale.setDefault(Locale.CHINA);
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        userContext = PowerMockito.mock(UserContext.class);
        login = PowerMockito.mock(Login.class);
        PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
        PowerMockito.when(userContext.getLogin()).thenReturn(login);
        PowerMockito.when(userContext.getName()).thenReturn("sysadmin");
    }

    @Test
    public void givenExistPatientIdThenQueryByPatientSerReturnObject() throws SQLException {
        Long patientId = 9527L;
        PatientDAO patientDAO = new PatientDAO(userContext);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString(Mockito.anyString())).thenReturn("M");
        PowerMockito.when(rs.getDate(Mockito.anyString())).thenReturn(new java.sql.Date(new Date().getTime()));
        Patient patient = patientDAO.queryByPatientSer(connection, patientId);
        Assert.assertNotNull(patient);
        Assert.assertTrue("M".equals(patient.getId()));
    }

    @Test
    public void givenNotExistPatientIdThenQueryByHisIdReturnNull() throws SQLException {
        Long patientId = 95271L;
        PatientDAO patientDAO = new PatientDAO(userContext);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(false);
        Patient patient = patientDAO.queryByPatientSer(connection, patientId);
        Assert.assertNull(patient);
    }

    @Test
    public void givenPatientIdAndPatientThenUpdatePatientReturnTrue() throws SQLException {
        Long patientId = 95272L;
        PatientDAO patientDAO = new PatientDAO(userContext);
        Patient patient = new Patient();
        patient.setGender(GenderEnum.M);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        int actual = patientDAO.updateByPatientSer(connection, patient, patientId);
        Assert.assertEquals(1, actual);
    }

    @Test
    public void testCreateNormal() {
        Connection con = null;
        String newId = "newId";
        try {
            con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString(), anyInt())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);

            // no data
            PowerMockito.when(rs.next()).thenReturn(true);
            PowerMockito.when(rs.getString(1)).thenReturn(newId);

        } catch (SQLException e) {
            Assert.fail();
        }

        PatientDAO patientDAO = new PatientDAO(userContext);
        Patient patient = new Patient();
        patient.setGender(GenderEnum.M);
        patient.setPatientSer(0L);
        String actual = null;
        try {
            actual = patientDAO.create(con, patient);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(newId, actual);
    }

    @Test
    public void testCreateException() {
        Connection con = null;
        String newId = "newId";
        try {
            con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString(), anyInt())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.getGeneratedKeys()).thenThrow(new SQLException("testing Create"));

            // no data
            PowerMockito.when(rs.next()).thenReturn(true);
            PowerMockito.when(rs.getString(1)).thenReturn(newId);

        } catch (SQLException e) {
            Assert.fail();
        }

        PatientDAO patientDAO = new PatientDAO(userContext);
        Patient patient = new Patient();
        patient.setGender(GenderEnum.M);
        patient.setPatientSer(0L);
        try {
            patientDAO.create(con, patient);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof SQLException);
        }
    }

    @Test
    public void testGetPhotoBytesByHisIdListNormal() {

        List<Long> patientSerList = Arrays.asList(1L, 2L, 3L);

        Map<Long, byte[]> mapPhoto = new HashMap<>();
        mapPhoto.put(1L, "111".getBytes());
        mapPhoto.put(2L, "222".getBytes());
        mapPhoto.put(3L, "333".getBytes());

        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);

            // no data
            PowerMockito.when(rs.next()).thenReturn(true, true, true, false);
            PowerMockito.when(rs.getLong("patientSer")).thenReturn(1L, 2L, 3L);
            PowerMockito.when(rs.getBytes("photo")).thenReturn("111".getBytes(), "222".getBytes(), "333".getBytes());


            PatientDAO patientDAO = new PatientDAO(userContext);
            Map<Long, byte[]> actualMap = patientDAO.getPhotoBytesListByPatientSerList(con, patientSerList);
            for (Long key : patientSerList) {
                byte[] expectedByte = mapPhoto.get(key);
                byte[] actualByte = actualMap.get(key);
                Assert.assertArrayEquals(expectedByte, actualByte);
            }
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetPhotoBytesByHisIdWithoutHisId() {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        try {
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        } catch (SQLException e) {
            Assert.fail();
        }

        try {
            List<Long> hisIdList = null;
            PatientDAO patientDAO = new PatientDAO(userContext);
            Map<Long, byte[]> actualMap = patientDAO.getPhotoBytesListByPatientSerList(con, hisIdList);
            Assert.assertEquals(0, actualMap.size());

            hisIdList = new ArrayList<>(0);
            actualMap = patientDAO.getPhotoBytesListByPatientSerList(con, hisIdList);
            Assert.assertEquals(0, actualMap.size());
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetPhotoBytesByHisIdNoPhotoData() {
        List<Long> patientSerList = Arrays.asList(1L, 2L, 3L);

        Map<Long, byte[]> mapPhoto = new HashMap<>();
        mapPhoto.put(1L, "111".getBytes());
        mapPhoto.put(2L, "222".getBytes());
        mapPhoto.put(3L, "333".getBytes());

        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);

            // no data
            PowerMockito.when(rs.next()).thenReturn(false);


            PatientDAO patientDAO = new PatientDAO(userContext);
            Map<Long, byte[]> actualMap = patientDAO.getPhotoBytesListByPatientSerList(con, patientSerList);

            Assert.assertEquals(0, actualMap.size());
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetPhotoBytesListByPatientSerListWithSQLException() {
        @SuppressWarnings("unchecked")
        List<Long> hisIdList = Arrays.asList(111L, 222L, 333L);
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenThrow(new SQLException("testing"));

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);

            // no data
            PowerMockito.when(rs.next()).thenReturn(false);


            PatientDAO patientDAO = new PatientDAO(userContext);
            patientDAO.getPhotoBytesListByPatientSerList(con, hisIdList);
        } catch (SQLException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testUpdatePhotoNormal() {
        Long patientId = 95272L;
        byte[] photoBytes = new byte[]{23, 31, 34, 44};
        PatientDAO patientDAO = new PatientDAO(userContext);
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            PowerMockito.when(ps.executeUpdate()).thenReturn(1);


            int actual = patientDAO.updatePhoto(con, patientId, photoBytes);
            Assert.assertEquals(1, actual);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdatePhotoNoData() {
        Long patientSer = 94234L;
        byte[] photoBytes = new byte[]{23, 31, 34, 44};
        PatientDAO patientDAO = new PatientDAO(userContext);
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            PowerMockito.when(ps.executeUpdate()).thenReturn(1);


            int actual = patientDAO.updatePhoto(con, patientSer, photoBytes);
            Assert.assertEquals(1, actual);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdatePhotoWithException() {
        Long patientSer = 94234L;
        byte[] photoBytes = new byte[]{23, 31, 34, 44};
        PatientDAO patientDAO = new PatientDAO(userContext);
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenThrow(new SQLException("testing"));

            patientDAO.updatePhoto(con, patientSer, photoBytes);

        } catch (SQLException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testQueryAllActivePatientSerList() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getLong(1)).thenReturn(12121L);
        PatientDAO patientDAO = new PatientDAO(userContext);


        List<Long> list = patientDAO.queryAllActivePatientSer(connection);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.get(0) == 12121L);
    }
}
