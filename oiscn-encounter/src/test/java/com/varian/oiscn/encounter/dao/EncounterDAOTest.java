package com.varian.oiscn.encounter.dao;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.GenderEnum;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.history.EncounterTitleItem;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockPreparedStatement;
import com.varian.oiscn.encounter.util.MockResultSet;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Matchers.anyString;

/**
 * Created by BHP9696 on 2017/7/27.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, EncounterDAO.class, MockPreparedStatement.class})
public class EncounterDAOTest {
    private EncounterDAO encounterDAO;
    private Connection conn;

    @Before
    public void setup() {
        Locale.setDefault(Locale.CHINA);
        conn = PowerMockito.mock(MockDatabaseConnection.class);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        Login login = PowerMockito.mock(Login.class);
        PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
        PowerMockito.when(userContext.getLogin()).thenReturn(login);
        PowerMockito.when(userContext.getName()).thenReturn("sysadmin");
        encounterDAO = new EncounterDAO(userContext);
    }

    @Test
    public void givenExistPatientIdForQueryEncounterThenReturnObject() throws SQLException {
        Long patientSer = 9527L;
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Mockito.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString(Mockito.anyString())).thenReturn("xxxx");
        PowerMockito.when(rs.getString("status")).thenReturn("IN_PROGRESS");
        PowerMockito.when(rs.getString("diagnoseRecurrence")).thenReturn("true");
        PowerMockito.when(rs.getLong(Mockito.anyString())).thenReturn(123456L);
        Encounter encounter = encounterDAO.queryByPatientSer(conn, patientSer);
        Assert.assertNotNull(encounter);
        Assert.assertTrue("xxxx".equals(encounter.getId()));
    }

    @Test
    public void givenNotExistPatientIdForQueryEncounterThenReturnNull() throws SQLException {
        Long patientSer = 95271L;
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Mockito.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(false);
        Encounter encounter = encounterDAO.queryByPatientSer(conn, patientSer);
        Assert.assertNull(encounter);
    }

    @Test
    public void givenPatientIdAndEncounterThenUpdateReturnTrue() throws SQLException {
        Long patientSer = 952712L;
        Encounter encounter = new Encounter();
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(EncounterDAO.UPDATE_SQL)).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        boolean ok = encounterDAO.updateByPatientSer(conn, encounter, patientSer);
        Assert.assertTrue(ok);
    }

    @Test
    public void testGetPhysicianCommentsInBatch() {
        HashMap<String, String> result = new HashMap<>();
        result.put("121212", "value");
        result.put("12222", "value");
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getLong("patientSer")).thenReturn(121212L);
            PowerMockito.when(rs.getString("physicianComment")).thenReturn("value");
            PowerMockito.when(rs.getString("age")).thenReturn("33");

            Assert.assertEquals(result.get(0), encounterDAO.getPhysicianCommentsInBatch(con, new ArrayList<>()).get(0));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void givenPatientSerAndEncounterIdWhenCancelLocalTreatmentAppointmentThenReturnInt() throws SQLException {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        Assert.assertTrue(1 == encounterDAO.cancelLocalTreatmentAppointment(con, 121212L, "12345"));
    }

    @Test
    public void givenPatientSerAndEncounterIdWhenCancelQueuingManagementThenReturnInt() throws SQLException {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        Assert.assertTrue(1 == encounterDAO.cancelQueuingManagement(con, 1212122L, "12345"));
    }

    @Test
    public void testCreateEncounter() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString(),Matchers.anyInt())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.getString(1)).thenReturn("1212");
        PreparedStatement ps2 = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps2);
        PowerMockito.when(ps2.executeBatch()).thenReturn(new int[]{1,1});

        Encounter encounter = new Encounter(){{
           setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
               setCpInstanceId(10L);
               setEncounterId(12L);
           }}));
//           setPatientID("1212");
            setPatientSer("1212");
           setDiagnoses(Arrays.asList(new Diagnosis(){{
               setStaging(new Staging(){{
                   setDate(new Date());
               }});
           }}));
        }};
        String pk = encounterDAO.create(conn,encounter);
        Assert.assertNotNull(pk);
    }

    @Test
    public void testGetEncounterByHisIds() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        List<String> patientSerList = Arrays.asList("1234");
        Map<String,Encounter> map = encounterDAO.getPatientSerEncounterMapByPatientSerList(conn,patientSerList);
        Assert.assertNotNull(map);
    }

    @Test
    public void testListHistory() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        List<String> patientSerList = Arrays.asList("1234");
        Long patientSer = 123456L;
        List<EncounterTitleItem> result = encounterDAO.listHistory(conn, patientSer);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 2);
    }

    @Test
    public void testQueryEncounterByIdAndPatientSer() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString("id")).thenReturn("123");
        PowerMockito.when(rs.getString("status")).thenReturn("IN_PROGRESS");
        PowerMockito.when(rs.getString("diagnosePatientId")).thenReturn("123");
        Long patientSer = 9999L;
        Long encounterId = 8888L;
        Encounter encounter = encounterDAO.queryEncounterByIdAndPatientSer(conn, encounterId, patientSer);
        Assert.assertNotNull(encounter);
        Assert.assertTrue("123".equals(encounter.getId()));
    }

    @Test
    public void testUpdatePatient() throws SQLException {
        Long patientId = 95272L;
        Patient patient = new Patient();
        patient.setGender(GenderEnum.M);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        int actual = encounterDAO.updatePatient(conn, patient, patientId);
        Assert.assertEquals(1, actual);
    }

    @Test
    public void testQueryByPatientSerReturnObject() throws SQLException {
        Long patientId = 9527L;
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString(Mockito.anyString())).thenReturn("M");
        PowerMockito.when(rs.getDate(Mockito.anyString())).thenReturn(new java.sql.Date(new Date().getTime()));
        Patient patient = encounterDAO.queryPatientByPatientSer(conn, patientId);
        Assert.assertNotNull(patient);
        Assert.assertTrue("M".equals(patient.getId()));
    }
}
