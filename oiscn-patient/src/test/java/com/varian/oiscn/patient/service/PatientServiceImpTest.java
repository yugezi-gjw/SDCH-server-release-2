package com.varian.oiscn.patient.service;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.*;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.assembler.EncounterAssembler;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.patient.assembler.PatientAssembler;
import com.varian.oiscn.patient.dao.PatientDAO;
import com.varian.oiscn.patient.util.MockDatabaseConnection;
import com.varian.oiscn.patient.util.MockDtoUtil;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;

/**
 * Created by gbt1220 on 3/31/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientServiceImp.class, ConnectionPool.class, EncounterAssembler.class,
        PatientAssembler.class, BasicDataSourceFactory.class})
public class PatientServiceImpTest {
    private PatientDAO patientDAO;

    private EncounterDAO encounterDAO;

    private PatientServiceImp patientServiceImp;

    private Connection con;

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
        patientServiceImp = new PatientServiceImp(new UserContext());
        PowerMockito.mockStatic(PatientAssembler.class);
        PowerMockito.mockStatic(EncounterAssembler.class);
    }


    @Test
    public void givenWhenDAOCreateThrowExceptionThenReturnEmptyString() throws SQLException {
        Patient p = givenAPatient();
        p.setPatientSer(0L);
        RegistrationVO registrationVO = new RegistrationVO();
        PowerMockito.when(PatientAssembler.getPatient(registrationVO)).thenReturn(p);
        PowerMockito.when(patientDAO.create(con, p)).thenThrow(SQLException.class);
        Assert.assertEquals(StringUtils.EMPTY, patientServiceImp.create(registrationVO));
    }


    @Test
    public void givenRegistrationVOWhenDAOUpdateThrowExceptionThenReturnFalse() throws SQLException {
        Patient p = givenAPatient();
        RegistrationVO registrationVO = MockDtoUtil.givenARegistrationVO();
        PowerMockito.when(PatientAssembler.getPatient(registrationVO)).thenReturn(p);
        PowerMockito.when(patientDAO.queryByPatientSer(con, new Long(registrationVO.getPatientSer()))).thenThrow(SQLException.class);
        Assert.assertFalse(patientServiceImp.update(registrationVO));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetPhotoDataByHisIdNormal() {
        List<Long> hisIdList = PowerMockito.mock(List.class);
        Map<Long, byte[]> mapPhoto = PowerMockito.mock(Map.class);
        try {
            PowerMockito.when(patientDAO.getPhotoBytesListByPatientSerList(con, hisIdList)).thenReturn(mapPhoto);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(mapPhoto, patientServiceImp.getPhotoListByPatientSerList(hisIdList));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetGetPhotoDataByHisIdNullData() {
        List<Long> hisIdList = PowerMockito.mock(List.class);
        try {
            PowerMockito.when(patientDAO.getPhotoBytesListByPatientSerList(con, hisIdList)).thenReturn(new HashMap<>());
        } catch (SQLException e) {
            Assert.fail();
        }
        Map<Long, byte[]> actual = patientServiceImp.getPhotoListByPatientSerList(hisIdList);
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void testUpdatePhotoByPatientSer() {
        Long patientSer = 111L;
        byte[] photoBytes = new byte[]{123, 33, 44};
        int expected = 1;
        try {
            PowerMockito.when(patientDAO.updatePhoto(con, patientSer, photoBytes)).thenReturn(expected);
        } catch (SQLException e) {
            Assert.fail();
        }
        int actual = patientServiceImp.updatePhotoByPatientSer(patientSer, photoBytes);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void givenHisIdWhenQueryPatientByHisIdThenReturnPatient() throws SQLException {
        Long patientSer =  1111L;
        Patient patient = new Patient();
        patient.setPatientSer(new Long(patientSer));
        PowerMockito.when(patientDAO.queryByPatientSer(con, patientSer)).thenReturn(patient);
        patient = patientServiceImp.queryPatientByPatientSer(String.valueOf(patientSer));
        Assert.assertNotNull(patient);
        Assert.assertTrue(patientSer.equals(patient.getPatientSer()));
    }

    @Test
    public void givenNotExistsHisIdWhenQueryPatientByHisIdThenReturnNull() throws SQLException {
        Long patientSer =  1111L;
        Patient patient = new Patient();
        patient.setPatientSer(patientSer);
        PowerMockito.when(patientDAO.queryByPatientSer(con, patientSer)).thenReturn(null);
        patient = patientServiceImp.queryPatientByPatientSer(patientSer.toString());
        Assert.assertNull(patient);
    }

    @Test
    public void givenHisIdWhenQueryPatientHistoryFromHISThenReturnHistory() {
        String hisId = "hisIdNotExists";
        String history = patientServiceImp.queryPatientHistoryFromHIS(hisId);
        Assert.assertNotNull(history);
    }

    @Test
    public void testCreatePatientSuccess() throws SQLException {
        Patient p = givenAPatient();
        RegistrationVO registrationVO = new RegistrationVO(){{
            setPatientSer("12121");
        }};
        PowerMockito.when(PatientAssembler.getPatient(registrationVO)).thenReturn(p);
        PowerMockito.when(patientDAO.create(con,p)).thenReturn("121");
        Encounter encounter = new Encounter();
        PowerMockito.when(EncounterAssembler.getEncounter(registrationVO)).thenReturn(encounter);
        PowerMockito.when(encounterDAO.create(con,encounter)).thenReturn("111");
        String pk = patientServiceImp.create(registrationVO);
        Assert.assertNotNull(pk);
    }

    @Test
    public void testUpdatePatientSuccess() throws SQLException {
        Long patientSer = 111L;
        RegistrationVO registrationVO = new RegistrationVO(){{
          setPatientSer(String.valueOf(patientSer));
        }};
        Patient patient = givenAPatient();
        PowerMockito.when(patientDAO.queryByPatientSer(con,patientSer)).thenReturn(patient);
        PowerMockito.when(PatientAssembler.getPatient(registrationVO)).thenReturn(patient);
        PowerMockito.when(patientDAO.updateByPatientSer(con,patient,patientSer)).thenReturn(1);
        Encounter encounter = new Encounter();
        PowerMockito.when(EncounterAssembler.getEncounter(registrationVO)).thenReturn(encounter);
        PowerMockito.when(encounterDAO.updateByPatientSer(con,encounter,patientSer)).thenReturn(true);
        Assert.assertTrue(patientServiceImp.update(registrationVO));
    }

    @Test
    public void testUpdateWithNewEncounterWithException() throws SQLException {
        PowerMockito.when(patientDAO.queryByPatientSer(Matchers.any(),Matchers.anyLong())).thenThrow(new SQLException("sqlerror"));
        Assert.assertFalse(patientServiceImp.updateWithNewEncounter(new RegistrationVO(){{
            setPatientSer("1212");
        }}));
    }


    @Test
    public void testUpdateWithNewEncounterSuccess() throws SQLException {
        Long patientSer = 111L;
        RegistrationVO registrationVO = new RegistrationVO(){{
           setPatientSer(patientSer.toString());
        }};
        Patient patient = givenAPatient();
        PowerMockito.when(patientDAO.queryByPatientSer(con,patientSer)).thenReturn(patient);
        PowerMockito.when(PatientAssembler.getPatient(registrationVO)).thenReturn(patient);
        PowerMockito.when(patientDAO.updateByPatientSer(con,patient,patientSer)).thenReturn(1);
        Encounter encounter = new Encounter();
        PowerMockito.when(EncounterAssembler.getEncounter(registrationVO)).thenReturn(encounter);
        PowerMockito.when(encounterDAO.create(con,encounter)).thenReturn("1212");
        Assert.assertTrue(patientServiceImp.updateWithNewEncounter(registrationVO));
    }

    @Test
    public void testUpdatePatientHistorySuccess() throws SQLException {
        Long patientSer = 111L;
        Patient patient = givenAPatient();
        PowerMockito.when(patientDAO.queryByPatientSer(con,patientSer)).thenReturn(patient);
        PowerMockito.when(patientDAO.updateByPatientSer(con,patient,patientSer)).thenReturn(1);
        Assert.assertTrue(patientServiceImp.updatePatientHistory(patientSer,"dfdf"));
    }

    @Test
    public void testUpdatePatientHistoryException() throws SQLException {
        Long patientSer = 111L;
        PowerMockito.when(patientDAO.queryByPatientSer(con,patientSer)).thenThrow(new SQLException("sqlerror"));
        Assert.assertFalse(patientServiceImp.updatePatientHistory(patientSer,"dfdf"));
    }

    @Test
    public void testQueryAllActivePatientSer() throws SQLException {
        List<Long> patientSerList = Arrays.asList(1212L);
        PowerMockito.when(patientDAO.queryAllActivePatientSer(con)).thenReturn(patientSerList);
        List<String> rlist = patientServiceImp.queryAllActivePatientSer();
        Assert.assertNotNull(rlist);
        Assert.assertFalse(rlist.isEmpty());
        Assert.assertTrue(String.valueOf(patientSerList.get(0)).equals(rlist.get(0)));
    }

    @Test
    public void testQueryAllActivePatientSerWithException() throws SQLException {
        PowerMockito.when(patientDAO.queryAllActivePatientSer(con)).thenThrow(new SQLException());
        List<String> rlist = patientServiceImp.queryAllActivePatientSer();
        Assert.assertNotNull(rlist);
        Assert.assertTrue(rlist.isEmpty());
    }

//    private PatientVO givenAPatientVO() {
//        PatientVO patientVO = new PatientVO();
//        patientVO.setId("id");
//        patientVO.setPatientId("patientId");
//        patientVO.setHisId("hisId");
//        return patientVO;
//    }

    private Patient givenAPatient() {
        Patient p = new Patient();
        p.setId("id");
        p.setHisId("hisId");
        p.setPatientSer(1212L);
        p.setRadiationId("radiationId");
        p.setNationalId("nationalId");
        p.setChineseName("chineseName");
        p.setEnglishName("englishName");
        p.setGender(GenderEnum.M);
        p.setBirthDate(new Date());
        p.setPatientStatus(PatientStatusEnum.N);
        p.setMaritalStatus(MaritalStatusEnum.A);
        p.setVip(VIPEnum.N);
        p.setCitizenship(new CodeSystem());
        p.setEthnicGroup(new CodeSystem());
        p.setPhoto("photo");
        p.setWorkPhone("workPhone");
        p.setHomePhone("homePhone");
        p.setMobilePhone("mobilePhone");
        p.setAddress("address");
        p.setIdentifiers(new ArrayList<>());
        p.setHumanNames(new ArrayList<>());
        p.setContacts(new ArrayList<>());
        return p;
    }
}
