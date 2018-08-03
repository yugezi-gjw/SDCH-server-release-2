package com.varian.oiscn.encounter.service;

import com.varian.oiscn.anticorruption.resourceimps.CommunicationAntiCorruptionServiceImp;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathDAO;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.encounter.history.EncounterTitleItem;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockDtoUtil;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

/**
 * Created by gbt1220 on 6/14/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({EncounterServiceImp.class, ConnectionPool.class, BasicDataSourceFactory.class})
public class EncounterServiceImpTest {

    private Connection con;
    private EncounterServiceImp encounterServiceImp;
    private CommunicationAntiCorruptionServiceImp communicationAntiCorruptionServiceImp;
    private EncounterDAO encounterDAO;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        encounterDAO = PowerMockito.mock(EncounterDAO.class);
        PowerMockito.whenNew(EncounterDAO.class).withAnyArguments().thenReturn(encounterDAO);
        PowerMockito.mockStatic(ConnectionPool.class);
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        communicationAntiCorruptionServiceImp = PowerMockito.mock(CommunicationAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CommunicationAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(communicationAntiCorruptionServiceImp);
        encounterServiceImp = new EncounterServiceImp(new UserContext());
    }

    @Test
    public void givenNonExitedPatientSerWhenDaoThrowExceptionThenReturnNull() throws SQLException {
        Long exitedPatientSer = 121212L;
        PowerMockito.when(encounterDAO.queryByPatientSer(con, exitedPatientSer)).thenThrow(SQLException.class);
        Assert.assertNull(encounterServiceImp.queryByPatientSer(exitedPatientSer));
    }

    @Test
    public void givenExitedPatientSerWhenQueryThenReturnTheEncounter() throws SQLException {
        Long exitedPatientSer = 121212L;
        Encounter encounter = new Encounter();
        PowerMockito.when(encounterDAO.queryByPatientSer(con, exitedPatientSer)).thenReturn(encounter);
        Assert.assertEquals(encounter, encounterServiceImp.queryByPatientSer(exitedPatientSer));
    }

    @Test
    public void givenPatientSerAndCommentWhenModifyPhysicianCommentThenReturnTrue() {
        try {
            Long patientSer = 121212L;
            Encounter encounter = new Encounter();
            PowerMockito.when(encounterDAO.queryByPatientSer(con, patientSer)).thenReturn(encounter);
            PowerMockito.when(encounterDAO.updateByPatientSer(con, encounter, patientSer)).thenReturn(true);


            PowerMockito.when(communicationAntiCorruptionServiceImp.queryPhysicianCommentByPatientId(anyString())).thenReturn(null);
            PowerMockito.when(communicationAntiCorruptionServiceImp.createPhysicianComment(Matchers.any())).thenReturn("12");

            boolean ok = this.encounterServiceImp.modifyPhysicianComment(patientSer, new PhysicianCommentDto());
            Assert.assertTrue(ok);
            PowerMockito.when(communicationAntiCorruptionServiceImp.queryPhysicianCommentByPatientId(anyString())).thenReturn(new PhysicianCommentDto());
            PowerMockito.when(communicationAntiCorruptionServiceImp.updatePhysicianComment(Matchers.any())).thenReturn("12");
            ok = this.encounterServiceImp.modifyPhysicianComment(patientSer, new PhysicianCommentDto());
            Assert.assertTrue(ok);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenHisIdListWhenQueryPhysicianCommentsByHisIdListThenReturnMap() throws SQLException {
        List<String> hisIdList = Arrays.asList("1111", "1112");
        Map<String, String> physicianComments = new HashMap<>();
        physicianComments.put("1111", "physicianComment1");
        physicianComments.put("1112", "physicianComment2");
        PowerMockito.when(encounterDAO.getPhysicianCommentsInBatch(con, hisIdList)).thenReturn(physicianComments);
        Map<String, String> result = this.encounterServiceImp.queryPhysicianCommentsByHisIdList(hisIdList);
        Assert.assertTrue(result.equals(physicianComments));
        PowerMockito.when(encounterDAO.getPhysicianCommentsInBatch(con, hisIdList)).thenThrow(new SQLException("sqlerror"));
        result = this.encounterServiceImp.queryPhysicianCommentsByHisIdList(hisIdList);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void givenHisIdWhenQueryPhysicianCommentFromAriaThenReturnString() throws Exception {
        String hisId = "hisId";
        PhysicianCommentDto dto = MockDtoUtil.givenAPhysicianCommentDto();
        PowerMockito.when(communicationAntiCorruptionServiceImp.queryPhysicianCommentByPatientId(anyString())).thenReturn(dto);

        Assert.assertNotNull(this.encounterServiceImp.queryPhysicianCommentFromAria(hisId));
    }

    @Test
    public void givenPatientSerAndAllergyInfoWhenAllergyInfoThenReturnTrue() {
        try {
            Long patientSer = 1212L;
            String allergyInfo = "allergyInfo";
            Encounter encounter = new Encounter();
            PowerMockito.when(encounterDAO.queryByPatientSer(con, patientSer)).thenReturn(encounter);
            PowerMockito.when(encounterDAO.updateByPatientSer(con, encounter, patientSer)).thenReturn(true);
            boolean ok = this.encounterServiceImp.modifyAllergyInfo(patientSer, allergyInfo);
            Assert.assertTrue(ok);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenHisIdWhenQueryingAllergyInfoFromAriaThenReturnString() {
        String hisId = "hisId";
        Assert.assertTrue("".equals(this.encounterServiceImp.queryAllergyInfoFromAria(hisId)));
    }

    @Test
    public void givenPatientSerAndEncounterIdWhenCancelLocalTreatmentAppointmentThenReturnInt() throws SQLException {
        Long patientSer = 12121L;
        String encounterId = "encounterId";
        PowerMockito.when(encounterDAO.cancelLocalTreatmentAppointment(con, patientSer, encounterId)).thenReturn(1);
        Assert.assertTrue(1 == encounterServiceImp.cancelLocalTreatmentAppointment(patientSer, encounterId));
        PowerMockito.when(encounterDAO.cancelLocalTreatmentAppointment(con, patientSer, encounterId)).thenThrow(new SQLException("sqlerror"));
        Assert.assertTrue(0 == encounterServiceImp.cancelLocalTreatmentAppointment(patientSer, encounterId));
    }

    @Test
    public void givenPatientSerAndEncounterIdWhenCancelQueuingManagementThenReturnInt() throws SQLException {
        Long patientSer = 1212L;
        String encounterId = "encounterId";
        PowerMockito.when(encounterDAO.cancelQueuingManagement(con, patientSer, encounterId)).thenReturn(1);
        Assert.assertTrue(1 == encounterServiceImp.cancelQueuingManagement(patientSer, encounterId));
        PowerMockito.when(encounterDAO.cancelQueuingManagement(con, patientSer, encounterId)).thenThrow(new SQLException("sqlerror"));
        Assert.assertTrue(0 == encounterServiceImp.cancelQueuingManagement(patientSer, encounterId));
    }

    @Test
    public void testUpdateCarePathInstanceId() throws SQLException {
        Long patientSer = 1212L;
        EncounterCarePathDAO dao = PowerMockito.mock(EncounterCarePathDAO.class);
        encounterServiceImp.dao = dao;
        PowerMockito.when(dao.addCarePathInstanceId(anyObject(), anyObject())).thenReturn(1);
        Encounter enc = new Encounter(){{
            setId("11111");
        }};
        PowerMockito.when(encounterDAO.queryByPatientSer(con, patientSer)).thenReturn(enc);
        EncounterCarePath encounterCarePath = new EncounterCarePath(){{
            setCpInstanceId(1212L);
            setCategory(EncounterCarePathCategoryEnum.PRIMARY);
        }};
        Assert.assertTrue(encounterServiceImp.updateCarePathInstanceId(patientSer, encounterCarePath));

        PowerMockito.when(encounterDAO.queryByPatientSer(con, patientSer)).thenThrow(new SQLException("sqlerror"));
        Assert.assertFalse(encounterServiceImp.updateCarePathInstanceId(patientSer, encounterCarePath));


    }

    @Test
    public void testCreateEncounter() throws SQLException{
        Encounter encounter = new Encounter();
        PowerMockito.when(encounterDAO.create(con,encounter)).thenReturn("21");
        String result = encounterServiceImp.create(encounter);
        Assert.assertTrue(result.equals("21"));
    }

    @Test
    public void testCreateEncounterThrowsSQLException() throws SQLException{
        Encounter encounter = new Encounter();
        PowerMockito.when(encounterDAO.create(con,encounter)).thenThrow(new SQLException("sqlerror"));
        String result = encounterServiceImp.create(encounter);
        Assert.assertNull(result);
    }

    @Test
    public void testQueryEncountersByPatientSerList() throws SQLException{
        List<String> patientSerList = Arrays.asList("111","222");
        Map<String, Encounter> map = new HashMap<>();
        map.put("111",new Encounter());
        map.put("222",new Encounter());

        PowerMockito.when(encounterDAO.getPatientSerEncounterMapByPatientSerList(con,patientSerList)).thenReturn(map);
        PowerMockito.when(communicationAntiCorruptionServiceImp.queryPhysicianCommentByPatientId(Matchers.anyString())).thenReturn(new PhysicianCommentDto());
        Map<String, Encounter> rmap = encounterServiceImp.queryPatientSerEncounterMapByPatientSerList(patientSerList);
        Assert.assertTrue(map.equals(rmap));
        PowerMockito.when(encounterDAO.getPatientSerEncounterMapByPatientSerList(con,patientSerList)).thenThrow(new SQLException("sqlerror"));
        rmap = encounterServiceImp.queryPatientSerEncounterMapByPatientSerList(patientSerList);
        Assert.assertTrue(rmap.isEmpty());

    }

    @Test
    public void testListHistory() throws SQLException {
        Long patientSer = 1212L;
        List<EncounterTitleItem> result = PowerMockito.mock(ArrayList.class);
        PowerMockito.when(encounterDAO.listHistory(con, patientSer)).thenReturn(result);
        Assert.assertSame(result, encounterServiceImp.listHistory(patientSer));
    }

    @Test
    public void testListHistorySQLException() throws SQLException {
        Long patientSer = 1212L;
        List<EncounterTitleItem> result = PowerMockito.mock(ArrayList.class);
        PowerMockito.when(encounterDAO.listHistory(con, patientSer)).thenThrow(new SQLException("sqlerror"));
        List<EncounterTitleItem> actual = encounterServiceImp.listHistory(patientSer);
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void givenNonExistedEncounterIdWhenDaoThrowExceptionThenReturnNull() throws SQLException {
        Long encounterId = 123L;
        Long patientSer = 333L;
        PowerMockito.when(encounterDAO.queryEncounterByIdAndPatientSer(con, encounterId, patientSer)).thenThrow(SQLException.class);
        Assert.assertNull(encounterServiceImp.queryEncounterByIdAndPatientSer(encounterId, patientSer));
    }

    @Test
    public void givenExistedEncounterIdWhenDaoQueryThenReturnEncounter() throws SQLException {
        Long encounterId = 123L;
        Long patientSer = 333L;
        Encounter encounter = new Encounter();
        PowerMockito.when(encounterDAO.queryEncounterByIdAndPatientSer(con, encounterId, patientSer)).thenReturn(encounter);
        Assert.assertEquals(encounter, encounterServiceImp.queryEncounterByIdAndPatientSer(encounterId, patientSer));
    }

    @Test
    public void testUpdatePatientThrowException() {
        try {
            Patient patient = new Patient();
            PowerMockito.when(encounterDAO.updatePatient(con, patient, 1L)).thenThrow(new SQLException());
            Assert.assertFalse(encounterServiceImp.updatePatient(patient, 1L));
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdatePatient() {
        try {
            Patient patient = new Patient();
            PowerMockito.when(encounterDAO.updatePatient(con, patient, 1L)).thenReturn(1);
            Assert.assertTrue(encounterServiceImp.updatePatient(patient, 1L));
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void givenHisIdWhenQueryPatientByHisIdThenReturnPatient() throws SQLException {
        Long patientSer = 1111L;
        Patient patient = new Patient();
        patient.setPatientSer(new Long(patientSer));
        PowerMockito.when(encounterDAO.queryPatientByPatientSer(con, patientSer)).thenReturn(patient);
        patient = encounterServiceImp.queryPatientByPatientSer(patientSer);
        Assert.assertNotNull(patient);
        Assert.assertTrue(patientSer.equals(patient.getPatientSer()));
    }

    @Test
    public void givenNotExistsHisIdWhenQueryPatientByHisIdThenReturnNull() throws SQLException {
        Long patientSer = 1111L;
        Patient patient = new Patient();
        patient.setPatientSer(patientSer);
        PowerMockito.when(encounterDAO.queryPatientByPatientSer(con, patientSer)).thenReturn(null);
        patient = encounterServiceImp.queryPatientByPatientSer(patientSer);
        Assert.assertNull(patient);
    }
}
