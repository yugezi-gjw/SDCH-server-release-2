package com.varian.oiscn.patient.registry;

import java.sql.Connection;
import java.sql.SQLException;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.patient.dao.PatientDAO;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class RegistryService {
    private PatientDAO patientDAO;
    private EncounterDAO encounterDAO;

    public RegistryService(UserContext userContext) {
        patientDAO = new PatientDAO(userContext);
        encounterDAO = new EncounterDAO(userContext);
    }

    /**
     * Create a patient with registration information.<br>
     *
     * @return created patient Id
     */
    public boolean create(Patient patient, Encounter encounter, Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);
            patient.setPatientSer(patientSer);
            encounter.setPatientSer(patientSer.toString());
            patientDAO.create(con, patient);
            encounterDAO.create(con, encounter);
            con.commit();
        } catch (SQLException e) {
            log.error("create SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(con);
            return false;
        } finally {
            DatabaseUtil.safeSetAutoCommit(con, true);
            DatabaseUtil.safeCloseConnection(con);
        }
        return true;
    }

    /**
     * Update patient's registration information with photo data and new encounter.<br>
     *
     * @return update result
     */
    public boolean updateWithNewEncounter(Patient patient, Encounter encounter, Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);
            patientDAO.updateByPatientSer(con, patient, patientSer);
            encounter.setPatientSer(patientSer.toString());
            encounterDAO.create(con, encounter);
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

    /**
     * Update patient's registration information with photo data.<br>
     *
     * @return update result
     */
    public boolean update(Patient patient, Encounter encounter, Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);
            patientDAO.updateByPatientSer(con, patient, patientSer);
            encounterDAO.updateByPatientSer(con, encounter, patientSer);
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
}
