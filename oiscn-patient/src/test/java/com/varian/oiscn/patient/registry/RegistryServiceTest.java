package com.varian.oiscn.patient.registry;

import java.sql.Connection;
import java.sql.SQLException;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.patient.assembler.PatientRegistryAssembler;
import com.varian.oiscn.patient.dao.PatientDAO;
import com.varian.oiscn.patient.util.MockDatabaseConnection;
import com.varian.oiscn.patient.util.MockDtoUtil;
import com.varian.oiscn.util.DatabaseUtil;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistryService.class, ConnectionPool.class, PatientRegistryAssembler.class,
        DatabaseUtil.class, BasicDataSourceFactory.class})
public class RegistryServiceTest {
    private PatientDAO patientDAO;
    private EncounterDAO encounterDAO;
    private Connection con;
    private RegistryService registryService;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        patientDAO = PowerMockito.mock(PatientDAO.class);
        PowerMockito.whenNew(PatientDAO.class).withAnyArguments().thenReturn(patientDAO);
        encounterDAO = PowerMockito.mock(EncounterDAO.class);
        PowerMockito.whenNew(EncounterDAO.class).withAnyArguments().thenReturn(encounterDAO);
        PowerMockito.mockStatic(ConnectionPool.class);
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        PowerMockito.mockStatic(PatientRegistryAssembler.class);
        registryService = new RegistryService(new UserContext());
    }

    @Test
    public void testCreateSuccessfully() throws SQLException {
        Patient p = givenPatient();
        Encounter e = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(patientDAO.create(con, p)).thenReturn("1");
        PowerMockito.when(encounterDAO.create(con, e)).thenReturn("1");
        Assert.assertTrue(registryService.create(p, e, 1L));
    }

    @Test
    public void testCreateFail() throws SQLException {
        Patient p = givenPatient();
        Encounter e = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(patientDAO.create(con, p)).thenThrow(SQLException.class);
        Assert.assertFalse(registryService.create(p, e, 1L));
    }

    @Test
    public void testUpdateWithNewEncounterSuccessfully() throws SQLException {
        Patient p = givenPatient();
        Encounter e = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(patientDAO.updateByPatientSer(con, p, 1L)).thenReturn(1);
        PowerMockito.when(encounterDAO.create(con, e)).thenReturn("1");
        Assert.assertTrue(registryService.updateWithNewEncounter(p, e, 1L));
    }

    @Test
    public void testUpdateWithNewEncounterFail() throws SQLException {
        Patient p = givenPatient();
        Encounter e = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(patientDAO.updateByPatientSer(con, p, 1L)).thenThrow(SQLException.class);
        Assert.assertFalse(registryService.updateWithNewEncounter(p, e, 1L));
    }

    @Test
    public void testUpdateSuccessfully() throws SQLException {
        Patient p = givenPatient();
        Encounter e = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(patientDAO.updateByPatientSer(con, p, 1L)).thenReturn(1);
        PowerMockito.when(encounterDAO.updateByPatientSer(con, e, 1L)).thenReturn(true);
        Assert.assertTrue(registryService.update(p, e, 1L));
    }

    @Test
    public void testUpdateFail() throws SQLException {
        Patient p = givenPatient();
        Encounter e = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(patientDAO.updateByPatientSer(con, p, 1L)).thenThrow(SQLException.class);
        Assert.assertFalse(registryService.update(p, e, 1L));
    }

    private Patient givenPatient() {
        return new Patient();
    }
}
