package com.varian.oiscn.encounter.service;

import com.varian.oiscn.anticorruption.resourceimps.CommunicationAntiCorruptionServiceImp;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathDAO;
import com.varian.oiscn.encounter.EncounterEndPlanDAO;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.encounter.history.EncounterTitleItem;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by gbt1220 on 6/14/2017.
 */
@Slf4j
public class EncounterServiceImp {

    private EncounterDAO encounterDAO;
    protected EncounterCarePathDAO dao;
    private EncounterEndPlanDAO encounterEndPlanDAO;
    private CommunicationAntiCorruptionServiceImp communicationAntiCorruptionServiceImp;

    public EncounterServiceImp(UserContext userContext) {
        encounterDAO = new EncounterDAO(userContext);
        dao = new EncounterCarePathDAO(userContext);
        communicationAntiCorruptionServiceImp = new CommunicationAntiCorruptionServiceImp();
        encounterEndPlanDAO = new EncounterEndPlanDAO();
    }

    /**
     * Create encounter
     *
     * @param encounter encounter
     * @return encounter id
     */
    public String create(Encounter encounter) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.create(con, encounter);
        } catch (SQLException e) {
            log.error("create SQLException SQLState=[{}]", e.getSQLState());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    /**
     * Update encounter by HIS id
     *
     * @param encounter  encounter info
     * @param patientSer PatientSer  patient primary key in aira
     * @return true/false
     */
    public boolean updateByPatientSer(Encounter encounter, Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.updateByPatientSer(con, encounter, patientSer);
        } catch (SQLException e) {
            log.error("updateByPatientSer SQLException SQLState=[{}]", e.getSQLState());
            return false;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    /**
     * Query encounter by HIS ID
     *
     * @param patientSer HIS id
     * @return the encounter
     */
    public Encounter queryByPatientSer(Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.queryByPatientSer(con, patientSer);
        } catch (SQLException e) {
            log.error("queryByPatientSer SQLException SQLState=[{}]", e.getSQLState());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    /**
     * Modify physician comment to local db and ARIA
     *
     * @param patientSer patient primary key in aria
     * @param dto        physcian comment dto
     * @return true if modified successfully, otherwise false
     */
    public boolean modifyPhysicianComment(Long patientSer, PhysicianCommentDto dto) {
        Encounter encounter = this.queryByPatientSer(patientSer);
        encounter.setPhysicianComment(dto.getComments());
        encounter.setEncounterCarePathList(null);
        PhysicianCommentDto physicianCommentDto = communicationAntiCorruptionServiceImp.queryPhysicianCommentByPatientId(String.valueOf(patientSer));
        String id;
        if (physicianCommentDto == null) {
            id = communicationAntiCorruptionServiceImp.createPhysicianComment(dto);
        } else {
            id = communicationAntiCorruptionServiceImp.updatePhysicianComment(dto);
        }
        return StringUtils.isNotEmpty(id) && this.updateByPatientSer(encounter, patientSer);
    }

    /**
     * Query physician comment by HIS id
     *
     * @param patientSer HIS id
     * @return the physician comments
     */
    public String queryPhysicianCommentFromAria(String patientSer) {
        log.debug("Get " + patientSer + " physician comment from aira");
        PhysicianCommentDto dto = communicationAntiCorruptionServiceImp.queryPhysicianCommentByPatientId(patientSer);
        return dto == null ? StringUtils.EMPTY : dto.getComments();
    }

    public String queryAllergyInfoFromAria(String hisId) {
        // TODO Get allergy information from aria
        log.debug("Get {} allergy information from Aria", hisId);
        return "";
    }

    public Map<String, String> queryPhysicianCommentsByHisIdList(List<String> hisIds) {
        Connection con = null;
        Map<String, String> result = new HashMap<>();
        try {
            con = ConnectionPool.getConnection();
            result = encounterDAO.getPhysicianCommentsInBatch(con, hisIds);

            for (Map.Entry<String, String> entry : result.entrySet()) {
                if (isEmpty(entry.getValue())) {
                    //TODO Add the logic of fetching data from FHIR.
                    result.put(entry.getKey(), this.queryPhysicianCommentFromAria(entry.getKey()));
                    log.debug("use patientAntiCorruptionServiceImp query PhysicianComments from aria");
                }
            }
        } catch (SQLException e) {
            log.error("queryPhysicianCommentsByHisIdList SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return result;
    }

    public Map<String, String> getPhysicianCommentsInBatch(List<String> patientSerList) {
        Connection con = null;
        Map<String, String> result = new HashMap<>();
        try {
            con = ConnectionPool.getConnection();
            result = encounterDAO.getPhysicianCommentsInBatch(con, patientSerList);
        } catch (SQLException e) {
            log.error("getPhysicianCommentsInBatch SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return result;
    }

    public Map<String, Encounter> queryPatientSerEncounterMapByPatientSerList(List<String> patientSerList) {
        Connection con = null;
        Map<String, Encounter> result = new HashMap<>();
        try {
            con = ConnectionPool.getConnection();
            result = encounterDAO.getPatientSerEncounterMapByPatientSerList(con, patientSerList);

            for (Map.Entry<String, Encounter> entry : result.entrySet()) {
                if (entry != null) {
                    //TODO Add the logic of fetching data from FHIR.
                    String comment = this.queryPhysicianCommentFromAria(entry.getKey());
                    if (StringUtils.isNotEmpty(comment)) {
                        entry.getValue().setPhysicianComment(comment);
                    }
                    log.debug("use patientAntiCorruptionServiceImp query PhysicianComments from aria");
                }
            }
        } catch (SQLException e) {
            log.error("queryPatientSerEncounterMapByPatientSerList SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return result;
    }

    public boolean modifyAllergyInfo(Long patientSer, String allergyInfo) {
        Encounter encounter = this.queryByPatientSer(patientSer);
        encounter.setAllergyInfo(allergyInfo);
        encounter.setEncounterCarePathList(null);
        return this.updateByPatientSer(encounter, patientSer);
    }

    public int cancelLocalTreatmentAppointment(Long patientSer, String encounterId) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.cancelLocalTreatmentAppointment(con, patientSer, encounterId);
        } catch (SQLException e) {
            log.error("cancelLocalTreatmentAppointment SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return 0;
    }

    public int cancelQueuingManagement(Long patientSer, String encounterId) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.cancelQueuingManagement(con, patientSer, encounterId);
        } catch (SQLException e) {
            log.error("cancelQueuingManagement SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return 0;
    }

    public boolean updateCarePathInstanceId(Long patientSer, EncounterCarePath encounterCarePath) {
        Connection con = null;
        int affectedRow = 0;
        try {
            con = ConnectionPool.getConnection();
            Encounter encounter = encounterDAO.queryByPatientSer(con, patientSer);
            if (encounter != null) {
                encounterCarePath.setEncounterId(new Long(encounter.getId()));
                affectedRow = dao.addCarePathInstanceId(con, encounterCarePath);
            }
        } catch (SQLException e) {
            log.error("updateCarePathInstanceId SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return affectedRow > 0;
    }

    public boolean createEncounterEndPlan(List<EncounterEndPlan> encounterEndPlansList) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterEndPlanDAO.batchCreate(con, encounterEndPlansList);
        } catch (SQLException e) {
            log.error("createEncounterEndPlan SQLException SQLState=[{}]", e.getSQLState());
            return false;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public List<EncounterTitleItem> listHistory(Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.listHistory(con, patientSer);
        } catch (SQLException e) {
            log.error("listHistory SQLException SQLState=[{}]", e.getSQLState());
            return new ArrayList<>();
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public Encounter queryEncounterByIdAndPatientSer(Long encounterId, Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.queryEncounterByIdAndPatientSer(con, encounterId, patientSer);
        } catch (SQLException e) {
            log.error("queryEncounterByIdAndPatientSer SQLException SQLState=[{}], error message: {}", e.getSQLState(), e.getMessage());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public boolean updatePatient(Patient patient, Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.updatePatient(con, patient, patientSer) > 0;
        } catch (SQLException e) {
            log.error("updatePatient SQLException SQLState=[{}]", e.getSQLState());
            return false;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public Patient queryPatientByPatientSer(Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            return encounterDAO.queryPatientByPatientSer(con, patientSer);
        } catch (SQLException e) {
            log.error("updatePatient SQLException SQLState=[{}]", e.getSQLState());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }
}
