package com.varian.oiscn.patient.service;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.assembler.EncounterAssembler;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.patient.assembler.PatientAssembler;
import com.varian.oiscn.patient.dao.PatientDAO;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 3/30/2017.
 */
@Slf4j
public class PatientServiceImp {

    private PatientDAO patientDAO;


    private EncounterDAO encounterDAO;

    public PatientServiceImp(UserContext userContext) {
        patientDAO = new PatientDAO(userContext);
        encounterDAO = new EncounterDAO(userContext);
    }

    /**
     * Create a patient with registration information.<br>
     *
     * @param registrationVO Registration VO
     * @return created patient Id
     */
    public String create(RegistrationVO registrationVO) {
        String createdPatientId = StringUtils.EMPTY;
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);
            Patient patient = PatientAssembler.getPatient(registrationVO);
            if (StringUtils.isEmpty(registrationVO.getPatientHistory())) {
                patient.setPatientHistory(this.queryPatientHistoryFromHIS(patient.getPatientSer().toString()));
            }
            createdPatientId = patientDAO.create(con, patient);
            if (StringUtils.isNotEmpty(createdPatientId)) {
                registrationVO.setId(createdPatientId);
                encounterDAO.create(con, EncounterAssembler.getEncounter(registrationVO));
            }
            con.commit();
        } catch (SQLException e) {
            log.error("create SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(con);
        } finally {
            DatabaseUtil.safeSetAutoCommit(con, true);
            DatabaseUtil.safeCloseConnection(con);
        }
        return createdPatientId;
    }

    /**
     * Update patient's registration information with photo data.<br>
     *
     * @param registrationVO Registration VO
     * @return update result
     */
    public boolean update(RegistrationVO registrationVO) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);
            Patient patient = patientDAO.queryByPatientSer(con, new Long(registrationVO.getPatientSer()));
            registrationVO.setId(patient.getId());
            final String hisId = registrationVO.getHisId();
            registrationVO.setPatientHistory(patient.getPatientHistory());
            if (StringUtils.isEmpty(registrationVO.getPatientHistory())) {
                registrationVO.setPatientHistory(this.queryPatientHistoryFromHIS(hisId));
            }
            patientDAO.updateByPatientSer(con, PatientAssembler.getPatient(registrationVO), new Long(registrationVO.getPatientSer()));
            encounterDAO.updateByPatientSer(con, EncounterAssembler.getEncounter(registrationVO), new Long(registrationVO.getPatientSer()));
            con.commit();
            return true;
        } catch (SQLException e) {
            log.error("update SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(con);
            return false;
        } finally {
            DatabaseUtil.safeSetAutoCommit(con, true);
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    /**
     * Update patient's registration information with photo data and new encounter.<br>
     *
     * @param registrationVO Registration VO
     * @return update result
     */
    public boolean updateWithNewEncounter(RegistrationVO registrationVO) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);
            Long patientSer = new Long(registrationVO.getPatientSer());
            Patient patient = patientDAO.queryByPatientSer(con, patientSer);
            registrationVO.setId(patient.getId());
            patientDAO.updateByPatientSer(con, PatientAssembler.getPatient(registrationVO), patientSer);
            encounterDAO.create(con, EncounterAssembler.getEncounter(registrationVO));
            con.commit();
            return true;
        } catch (SQLException e) {
            log.error("updateWithNewEncounter SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(con);
            return false;
        } finally {
            DatabaseUtil.safeSetAutoCommit(con, true);
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public Patient queryPatientByPatientSer(String patientSer) {
        Connection con = null;
        Patient patient = null;
        try {
            con = ConnectionPool.getConnection();
            patient = patientDAO.queryByPatientSer(con, new Long(patientSer));
        } catch (SQLException e) {
            log.error("queryPatientByPatientSer SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return patient;
    }

    public Patient queryPatientByHisId(String hisId) {
        Connection con = null;
        Patient patient = null;
        try {
            con = ConnectionPool.getConnection();
            patient = patientDAO.queryByHisId(con, hisId);
        } catch (SQLException e) {
            log.error("queryPatientByHisId SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return patient;
    }

    public boolean updatePatientHistory(Long patientSer, String patientHistory) {
        Connection con = null;
        boolean isUpdate = false;
        try {
            con = ConnectionPool.getConnection();
            Patient patient = patientDAO.queryByPatientSer(con, patientSer);
            patient.setPatientHistory(patientHistory);
            isUpdate = patientDAO.updateByPatientSer(con, patient, patientSer) > 0;
        } catch (SQLException e) {
            log.error("updatePatientHistory SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return isUpdate;
    }

    /**
     * Query patient history from Hospital Information System by patientSer
     *
     * @param patientSer
     * @return
     */
    public String queryPatientHistoryFromHIS(String patientSer) {
        log.debug("get patient[" + patientSer + "] history from HIS");
//      TODO implement query patient history from Hospital Information System
        return "";
    }

    /**
     * Get the photo data list from local storage by HisId List.<br>
     *
     * @param patientSerList Patient's patientSer List
     * @return photo byte List
     */
    public Map<Long, byte[]> getPhotoListByPatientSerList(List<Long> patientSerList) {
    	Connection con = null;
    	Map<Long, byte[]> ret = null;
        try {
            con = ConnectionPool.getConnection();
            ret = patientDAO.getPhotoBytesListByPatientSerList(con, patientSerList);
        } catch (SQLException e) {
            log.error("getPhotoListByPatientSerList SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return ret;
    }

    /**
     * Update the patient's photo data in Base64.<br>
     *
     * @param patientSer     Patient HIS Id
     * @param photoBytes Photo Data in Base64
     * @return
     */
    public int updatePhotoByPatientSer(Long patientSer, byte[] photoBytes) {
    	Connection con = null;
    	int ret = 0;
        try {
            con = ConnectionPool.getConnection();
            ret = patientDAO.updatePhoto(con, patientSer, photoBytes);
        } catch (SQLException e) {
            log.error("updatePhotoByPatientSer SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public List<String> queryAllActivePatientSer(){
        Connection con = null;
        List<String> patientSerList = new ArrayList<>();
        try{
            con = ConnectionPool.getConnection();
            List<Long> list = patientDAO.queryAllActivePatientSer(con);
            list.forEach(patientSer->patientSerList.add(String.valueOf(patientSer)));
        }catch (SQLException e) {
            log.error("queryAllActivePatientSer SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return patientSerList;
    }
}
