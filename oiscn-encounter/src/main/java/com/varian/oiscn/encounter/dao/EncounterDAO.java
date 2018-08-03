package com.varian.oiscn.encounter.dao;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.base.util.PhotoUtil;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.patient.Contact;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.GenderEnum;
import com.varian.oiscn.core.patient.MaritalStatusEnum;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientStatusEnum;
import com.varian.oiscn.core.patient.RelationshipEnum;
import com.varian.oiscn.core.patient.VIPEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathDAO;
import com.varian.oiscn.encounter.history.EncounterTitleItem;
import com.varian.oiscn.util.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by fmk9441 on 2017-03-29.
 */
public class EncounterDAO extends AbstractDAO<Encounter> {

    public static final String UPDATE_SQL = "UPDATE encounter SET lastUpdatedUser=?,lastUpdatedDate=?,encounterinfo=?,age=?,alert=?,allergyInfo=?,status=?,urgent=?,ecogDesc=?,ecogScore=?,diagnoseCode=?,diagnoseDesc=?,diagnoseSystem=?,diagnosePatientId=?,diagnoseRecurrence=?,diagnoseBodypartCode=?, diagnoseBodypartDesc=?,diagnoseDate=?,diagnosisNote=?,stagingSchemeName=?,stagingBasisCode=?,stagingStage=?,stagingTcode=?,stagingNcode=?,stagingMcode=?,stagingDate=?,positiveSign=?,insuranceTypeCode=?,insuranceType=?,patientSource=?,physicianComment=?,primaryPhysicianId=?,primaryPhysicianGroupId=?,primaryPhysicianGroupName=?,physicianBId=?,physicianBName=?,physicianCId=?,physicianCName=?,primaryPhysicianName=?,physicianPhone=? WHERE status='IN_PROGRESS' AND patientSer=?";

    private EncounterCarePathDAO encounterCarePathDAO;

    public EncounterDAO(UserContext userContext) {
        super(userContext);
        encounterCarePathDAO = new EncounterCarePathDAO(userContext);
    }

    @Override
    protected String getTableName() {
        return "Encounter";
    }

    @Override
    protected String getJsonbColumnName() {
        return "encounterInfo";
    }

    @Override
    public String create(Connection con, Encounter encounter) throws SQLException {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String createId;
        try {
            Timestamp curTime = new Timestamp(new java.util.Date().getTime());
            String json = new JsonSerializer<Encounter>().getJson(encounter);
            StringBuilder createSql = new StringBuilder("INSERT INTO ");
//            createSql.append(getTableName()).append("(createdUser,createdDate,lastUpdatedUser,lastUpdatedDate,patientId,age,alert,allergyInfo,bedNo,status,urgent,ecogDesc," +
//                    "ecogScore,diagnoseCode,diagnoseDesc,diagnoseSystem,diagnosePatientId,diagnoseRecurrence,diagnoseBodypartCode," +
//                    "diagnoseBodypartDesc,diagnoseDate,diagnosisNote,stagingSchemeName,stagingBasisCode,stagingStage,stagingTcode,stagingNcode,stagingMcode,stagingDate," +
//                    "positiveSign,inPatientArea,insuranceTypeCode,insuranceType,patientSource,organizationId,physicianComment," +
//                    "patientSourceEnum,primaryPhysicianId,primaryPhysicianGroupId,primaryPhysicianGroupName,physicianBId,physicianBName,physicianCId,physicianCName,");
//            createSql.append(getJsonbColumnName()).append(" ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            createSql.append(getTableName()).append("(createdUser,createdDate,lastUpdatedUser,lastUpdatedDate, patientSer, age,alert,allergyInfo,status,urgent,ecogDesc," +
                    "ecogScore,diagnoseCode,diagnoseDesc,diagnoseSystem,diagnosePatientId,diagnoseRecurrence,diagnoseBodypartCode," +
                    "diagnoseBodypartDesc,diagnoseDate,diagnosisNote,stagingSchemeName,stagingBasisCode,stagingStage,stagingTcode,stagingNcode,stagingMcode,stagingDate," +
                    "positiveSign,insuranceTypeCode,insuranceType,patientSource,physicianComment," +
                    "primaryPhysicianId,primaryPhysicianGroupId,primaryPhysicianGroupName,physicianBId,physicianBName,physicianCId,physicianCName, primaryPhysicianName, physicianPhone, ");
            createSql.append(getJsonbColumnName()).append(" ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps = con.prepareStatement(createSql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
            int idx = 1;
            ps.setString(idx++, userContext.getName());
            ps.setTimestamp(idx++, curTime);
            ps.setString(idx++, userContext.getName());
            ps.setTimestamp(idx++, curTime);
            ps.setLong(idx++, Long.parseLong(encounter.getPatientSer()));
            ps.setString(idx++, encounter.getAge());
            ps.setString(idx++, encounter.getAlert());
            ps.setString(idx++, encounter.getAllergyInfo());
//            ps.setString(idx++,encounter.getBedNo());
//            ps.setString(idx++, StatusEnum.PLANNED.name());
            ps.setString(idx++, StatusEnum.IN_PROGRESS.name());
            ps.setString(idx++, Boolean.toString(encounter.isUrgent()));
            ps.setString(idx++, encounter.getEcogDesc());
            ps.setString(idx++, encounter.getEcogScore());
            idx = buildDiagnosesParam(encounter, ps, idx);
            ps.setString(idx++, encounter.getPositiveSign());
//            ps.setString(idx++,encounter.getInPatientArea());
            ps.setString(idx++, encounter.getInsuranceTypeCode());
            ps.setString(idx++, encounter.getInsuranceType());
            ps.setString(idx++, encounter.getPatientSource());
//            ps.setString(idx++,encounter.getOrganizationID());
            ps.setString(idx++, encounter.getPhysicianComment());
//            ps.setString(idx++,encounter.getPatientSourceEnum() != null?encounter.getPatientSourceEnum().name():null);
            ps.setString(idx++, encounter.getPrimaryPhysicianID());
            ps.setString(idx++, encounter.getPrimaryPhysicianGroupID());
            ps.setString(idx++, encounter.getPrimaryPhysicianGroupName());
            ps.setString(idx++, encounter.getPhysicianBId());
            ps.setString(idx++, encounter.getPhysicianBName());
            ps.setString(idx++, encounter.getPhysicianCId());
            ps.setString(idx++, encounter.getPhysicianCName());
            ps.setString(idx++, encounter.getPrimaryPhysicianName());
            ps.setString(idx++, encounter.getPhysicianPhone());
            ps.setString(idx++, json);

            ps.executeUpdate();

            resultSet = ps.getGeneratedKeys();
            resultSet.next();
            createId = resultSet.getString(1);

            if (encounter.getEncounterCarePathList() != null && !encounter.getEncounterCarePathList().isEmpty()) {
                DatabaseUtil.safeCloseAll(null, ps, resultSet);
                for (int i = 0; i < encounter.getEncounterCarePathList().size(); i++) {
                    encounterCarePathDAO.addCarePathInstanceId(con, new EncounterCarePath(
                            new Long(createId),
                            encounter.getEncounterCarePathList().get(i).getCpInstanceId(),
                            EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY, null, null
                    ));
                }
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
        return createId;
    }

    private int buildDiagnosesParam(Encounter encounter, PreparedStatement ps, int idx) throws SQLException {
        if (encounter.getDiagnoses() != null && !encounter.getDiagnoses().isEmpty()) {
            List<Diagnosis> diagnoses = encounter.getDiagnoses();
            Diagnosis diagnosis = diagnoses.get(0);
            ps.setString(idx++, diagnosis.getCode());
            ps.setString(idx++, diagnosis.getDesc());
            ps.setString(idx++, diagnosis.getSystem());
            ps.setString(idx++, diagnosis.getPatientID());
            ps.setString(idx++, diagnosis.getRecurrence() != null ? Boolean.toString(diagnosis.getRecurrence().booleanValue()) : null);
            ps.setString(idx++, diagnosis.getBodypartCode());
            ps.setString(idx++, diagnosis.getBodypartDesc());
            ps.setDate(idx++, diagnosis.getDiagnosisDate() != null ? new Date(diagnosis.getDiagnosisDate().getTime()) : null);
            ps.setString(idx++, diagnosis.getDiagnosisNote());
            Diagnosis.Staging staging = diagnosis.getStaging();
            if (staging != null) {
                ps.setString(idx++, staging.getSchemeName());
                ps.setString(idx++, staging.getBasisCode());
                ps.setString(idx++, staging.getStage());
                ps.setString(idx++, staging.getTcode());
                ps.setString(idx++, staging.getNcode());
                ps.setString(idx++, staging.getMcode());
                if (staging.getDate() != null) {
                    ps.setDate(idx++, new Date(staging.getDate().getTime()));
                } else {
                    ps.setDate(idx++, new Date(diagnosis.getDiagnosisDate().getTime()));
                }
            } else {
                for (int i = 0; i < 7; i++) {
                    ps.setString(idx++, null);
                }
            }
        } else {
            for (int i = 0; i < 16; i++) {
                ps.setString(idx++, null);
            }
        }
        return idx;
    }

    /**
     * Update By His Id.<br>
     *
     * @param connection
     * @param encounter
     * @param patientSer
     * @return
     * @throws SQLException
     */
    public boolean updateByPatientSer(Connection connection, Encounter encounter, Long patientSer) throws SQLException {
        PreparedStatement ps = null;
        try {
            String updateJson = new JsonSerializer<Encounter>().getJson(encounter);
            ps = connection.prepareStatement(UPDATE_SQL);
            int idx = 1;
            ps.setString(idx++, userContext.getLogin().getUsername());
            ps.setTimestamp(idx++, new Timestamp(new java.util.Date().getTime()));
            ps.setString(idx++, updateJson);
            ps.setString(idx++, encounter.getAge());
            ps.setString(idx++, encounter.getAlert());
            ps.setString(idx++, encounter.getAllergyInfo());
//            ps.setString(idx++,encounter.getBedNo());
            if (encounter.getStatus() == null) {
//                encounter.setStatus(StatusEnum.PLANNED);
                encounter.setStatus(StatusEnum.IN_PROGRESS);
            }
            ps.setString(idx++, encounter.getStatus().name());
            ps.setString(idx++, Boolean.toString(encounter.isUrgent()));
            ps.setString(idx++, encounter.getEcogDesc());
            ps.setString(idx++, encounter.getEcogScore());
            idx = buildDiagnosesParam(encounter, ps, idx);
            ps.setString(idx++, encounter.getPositiveSign());
//            ps.setString(idx++,encounter.getInPatientArea());
            ps.setString(idx++, encounter.getInsuranceTypeCode());
            ps.setString(idx++, encounter.getInsuranceType());
            ps.setString(idx++, encounter.getPatientSource());
//            ps.setString(idx++,encounter.getOrganizationID());
            ps.setString(idx++, encounter.getPhysicianComment());
//            ps.setString(idx++,encounter.getPatientSourceEnum()!=null?encounter.getPatientSourceEnum().name():null);
            ps.setString(idx++, encounter.getPrimaryPhysicianID());
            ps.setString(idx++, encounter.getPrimaryPhysicianGroupID());
            ps.setString(idx++, encounter.getPrimaryPhysicianGroupName());
            ps.setString(idx++, encounter.getPhysicianBId());
            ps.setString(idx++, encounter.getPhysicianBName());
            ps.setString(idx++, encounter.getPhysicianCId());
            ps.setString(idx++, encounter.getPhysicianCName());
            ps.setString(idx++, encounter.getPrimaryPhysicianName());
            ps.setString(idx++, encounter.getPhysicianPhone());
            ps.setLong(idx++, patientSer);
            ps.executeUpdate();
            if (encounter.getEncounterCarePathList() != null && !encounter.getEncounterCarePathList().isEmpty()) {
                for (int i = 0; i < encounter.getEncounterCarePathList().size(); i++) {
                    encounterCarePathDAO.addCarePathInstanceId(connection, new EncounterCarePath(
                            encounter.getEncounterCarePathList().get(i).getEncounterId(),
                            encounter.getEncounterCarePathList().get(i).getCpInstanceId(),
                            EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY, null, null
                    ));
                }
            }

        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
        return true;
    }

    /**
     * Query By His Id. <br>
     *
     * @param con
     * @param patientSer
     * @return
     * @throws SQLException
     */
    public Encounter queryByPatientSer(Connection con, Long patientSer) throws SQLException {
        Encounter encounter = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT *  FROM Encounter  WHERE status = ? AND patientSer= ? ";
            ps = con.prepareStatement(sql);
            ps.setString(1, StatusEnum.IN_PROGRESS.toString());
            ps.setLong(2, patientSer);
            rs = ps.executeQuery();
            if (rs.next()) {
                encounter = fetchEncounter(rs);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return encounter;
    }

    /**
     * Return Physician Comments In Batch.<br>
     *
     * @param con
     * @param patientSerList
     * @return
     * @throws SQLException
     */
    public Map<String, String> getPhysicianCommentsInBatch(Connection con, List<String> patientSerList) throws SQLException {
        Map<String, Encounter> map = this.getPatientSerEncounterMapByPatientSerList(con, patientSerList);
        Map<String, String> result = new HashMap<>();
        map.entrySet().forEach(stringEncounterEntry ->
                result.put(stringEncounterEntry.getKey(), stringEncounterEntry.getValue().getPhysicianComment())
        );
        return result;
    }

    /**
     * Return Map<patientSer,Encounter.<br>
     *
     * @param con
     * @param patientSerList
     * @return
     * @throws SQLException
     */
    public Map<String, Encounter> getPatientSerEncounterMapByPatientSerList(Connection con, List<String> patientSerList) throws SQLException {
        PreparedStatement ps = null;
        Map<String, Encounter> result = new HashMap<>();
        ResultSet rs = null;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String sql = "SELECT "
                    + "  p.patientSer, "
                    + "  e.physicianComment,"
                    + "  e.age,"
                    + "  e.physicianBId,"
                    + "  e.physicianBName,"
                    + "  e.physicianCId,"
                    + "  e.physicianCName,"
                    + "  e.insuranceTypeCode,"
                    + "  e.insuranceType "
                    + " FROM Patient p, Encounter e "
                    + " WHERE e.status='" + StatusEnum.IN_PROGRESS + "'"
                    + "   AND p.patientSer = e.patientSer "
                    + "   AND p.patientSer in (";
            int total = patientSerList.size();
            for (int i = 0; i < total; i++) {
                if (i != total - 1) {
                    stringBuilder.append("?, ");
                } else {
                    stringBuilder.append("?)");
                }
            }
            sql = sql + stringBuilder.toString();
            ps = con.prepareStatement(sql.toString());
            for (int i = 0; i < total; i++) {
                ps.setLong(i + 1, Long.parseLong(patientSerList.get(i)));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Encounter encounter = new Encounter();
                encounter.setPhysicianComment(rs.getString("physicianComment"));
                encounter.setAge(rs.getString("age"));
                encounter.setPhysicianBId(rs.getString("physicianBId"));
                encounter.setPhysicianBName(rs.getString("physicianBName"));
                encounter.setPhysicianCId(rs.getString("physicianCId"));
                encounter.setPhysicianCName(rs.getString("physicianCName"));
                encounter.setInsuranceTypeCode(rs.getString("insuranceTypeCode"));
                encounter.setInsuranceType(rs.getString("insuranceType"));
                result.put(String.valueOf(rs.getLong("patientSer")), encounter);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return result;
    }

    /**
     * 取消本地预约
     *
     * @param connection
     * @param patientSer
     * @param encounterId
     * @return
     * @throws SQLException
     */
    public int cancelLocalTreatmentAppointment(Connection connection, Long patientSer, String encounterId) throws SQLException {
        String sql = "UPDATE TreatmentAppointment SET status = ?,lastUpdatedUser = ?,lastUpdatedDate = ? WHERE patientSer = ? and encounterId = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.CANCELLED));
        ps.setString(2, userContext.getName());
        ps.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
        ps.setLong(4, patientSer);
        ps.setLong(5, Long.parseLong(encounterId));
        return ps.executeUpdate();
    }

    /**
     * 取消预约排队列表
     *
     * @param connection
     * @param patientSer
     * @param encounterId
     * @return
     * @throws SQLException
     */
    public int cancelQueuingManagement(Connection connection, Long patientSer, String encounterId) throws SQLException {
        String sql = "UPDATE QueuingManagement SET checkInStatus = 'DELETED',checkInIdx = -3 WHERE patientSer = ? and encounterId = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, patientSer);
        ps.setLong(2, Long.parseLong(encounterId));
        return ps.executeUpdate();
    }

    public List<EncounterTitleItem> listHistory(Connection con, Long patientSer) throws SQLException {
        PreparedStatement ps = null;
        List<EncounterTitleItem> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            String sql = "SELECT en.id AS ID, en.status AS STATUS, en.urgent AS URGENT, en.createdDate AS CREATED_DATE, en.diagnoseDate AS DIAGNOSE_DATE "
                    + " FROM Encounter en "
                    + " WHERE en.patientSer = ? "
                    + " ORDER BY en.createdDate DESC, en.diagnoseDate DESC, en.id DESC ";
            ps = con.prepareStatement(sql);
            ps.setLong(1, patientSer);
            rs = ps.executeQuery();
            while (rs.next()) {
                EncounterTitleItem item = new EncounterTitleItem();
                item.setId(rs.getString("ID"));
                item.setStatus(rs.getString("STATUS"));
                item.setUrgent(rs.getString("URGENT"));
                item.setCreatedDate(rs.getDate("CREATED_DATE"));
                item.setDiagnoseDate(rs.getDate("DIAGNOSE_DATE"));
                list.add(item);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return list;
    }

    public Encounter queryEncounterByIdAndPatientSer(Connection con, Long encounterId, Long patientSer)
            throws SQLException {
        PreparedStatement ps = null;
        Encounter encounter = null;
        List<Diagnosis> diagnoses = new ArrayList<>();
        Diagnosis diagnosis = null;
        Diagnosis.Staging staging = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT e.id, e.patientSer, e.age, e.alert, e.allergyInfo," +
                    " e.status, e.urgent, e.ecogDesc, e.ecogScore, e.diagnoseCode, e.diagnoseDesc, e.diagnoseSystem, " +
                    " e.diagnosePatientId, e.diagnoseRecurrence, e.diagnoseBodypartCode, e.diagnoseBodypartDesc," +
                    " e.diagnoseDate, e.diagnosisNote, e.stagingSchemeName, e.stagingBasisCode, e.stagingStage," +
                    " e.stagingTcode, e.stagingNcode, e.stagingMcode, e.stagingDate, e.positiveSign, e.insuranceTypeCode," +
                    " e.insuranceType, e.patientSource, e.physicianComment, e.primaryPhysicianId, e.primaryPhysicianGroupId, " +
                    " e.primaryPhysicianGroupName, e.physicianBId, e.physicianBName, e.physicianCId, e.physicianCName," +
                    " e.createdUser, e.createdDate, e.lastUpdatedUser, e.lastUpdatedDate, e.encounterInfo, e.primaryPhysicianName, e.physicianPhone" +
                    " FROM Encounter e " +
                    " WHERE e.patientSer = ? AND e.id = ?";
            ps = con.prepareStatement(sql);
            ps.setLong(1, patientSer);
            ps.setLong(2, encounterId);
            rs = ps.executeQuery();
            if (rs.next()) {
                encounter = fetchEncounter(rs);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return encounter;
    }

    protected Encounter fetchEncounter(ResultSet rs) throws SQLException {
        Encounter encounter;
        encounter = new Encounter();
        encounter.setId(rs.getString("id"));
        encounter.setStatus(StatusEnum.valueOf(rs.getString("status")));
        encounter.setPrimaryPhysicianGroupID(rs.getString("primaryPhysicianGroupId"));
        encounter.setPrimaryPhysicianGroupName(rs.getString("primaryPhysicianGroupName"));
        encounter.setPrimaryPhysicianID(rs.getString("primaryPhysicianId"));
        encounter.setPhysicianBId(rs.getString("physicianBId"));
        encounter.setPhysicianBName(rs.getString("physicianBName"));
        encounter.setPhysicianCId(rs.getString("physicianCId"));
        encounter.setPhysicianCName(rs.getString("physicianCName"));
        encounter.setPrimaryPhysicianName(rs.getString("primaryPhysicianName"));
        encounter.setPhysicianPhone(rs.getString("physicianPhone"));
        encounter.setPatientSer(String.valueOf(rs.getLong("patientSer")));
        encounter.setAge(rs.getString("age"));
        encounter.setAlert(rs.getString("alert"));
        encounter.setUrgent(Boolean.valueOf(rs.getString("urgent")));
        encounter.setEcogScore(rs.getString("ecogScore"));
        encounter.setEcogDesc(rs.getString("ecogDesc"));
        encounter.setPositiveSign(rs.getString("positiveSign"));
        encounter.setInsuranceType(rs.getString("insuranceType"));
        encounter.setInsuranceTypeCode(rs.getString("insuranceTypeCode"));
        encounter.setPatientSource(rs.getString("patientSource"));
        encounter.setPhysicianComment(rs.getString("physicianComment"));
        encounter.setAllergyInfo(rs.getString("allergyInfo"));

        Diagnosis diagnosis = null;
        diagnosis = new Diagnosis();
        diagnosis.setPatientID(rs.getString("diagnosePatientId"));
        diagnosis.setCode(rs.getString("diagnoseCode"));
        diagnosis.setDesc(rs.getString("diagnoseDesc"));
        diagnosis.setSystem(rs.getString("diagnoseSystem"));
        String diagnoseRecurrence = rs.getString("diagnoseRecurrence");
        //非空的话设置，空的保持空值。
        if (diagnoseRecurrence != null) {
            diagnosis.setRecurrence(Boolean.valueOf(diagnoseRecurrence));
        }

        Diagnosis.Staging staging = new Diagnosis.Staging();
        staging.setSchemeName(rs.getString("stagingSchemeName"));
        staging.setBasisCode(rs.getString("stagingBasisCode"));
        staging.setStage(rs.getString("stagingStage"));
        staging.setTcode(rs.getString("stagingTcode"));
        staging.setNcode(rs.getString("stagingNcode"));
        staging.setMcode(rs.getString("stagingMcode"));
        staging.setDate(rs.getDate("stagingDate"));

        diagnosis.setStaging(staging);
        diagnosis.setDiagnosisDate(rs.getDate("diagnoseDate"));
        diagnosis.setBodypartCode(rs.getString("diagnoseBodypartCode"));
        diagnosis.setBodypartDesc(rs.getString("diagnoseBodypartDesc"));
        diagnosis.setDiagnosisNote(rs.getString("diagnosisNote"));

        List<Diagnosis> diagnoses = new ArrayList<>();
        diagnoses.add(diagnosis);
        encounter.setDiagnoses(diagnoses);
        return encounter;
    }

    public int updatePatient(Connection connection, Patient patient, Long patientSer) throws SQLException {
        PreparedStatement ps = null;
        int affectedRow = 0;
        try {
            // Retrieve the photo data from Json
            byte[] photoBytes = PhotoUtil.decode(patient.getPhoto());
            // photo data in Json will be null
            patient.setPhoto(null);

            String updateJson = new JsonSerializer<Patient>().getJson(patient);
            StringBuilder updateSql = new StringBuilder("UPDATE patient");
            updateSql.append(" SET lastUpdatedUser= ?, lastUpdatedDate= ?, patientInfo = ?, ");
            if (photoBytes != null && photoBytes.length > 0) {
                updateSql.append(" photo= ?, ");
            }
            updateSql.append(" hisId=?, ");
            updateSql.append(" vip= ?, ");
            updateSql.append(" pinyin= ?, ");
            updateSql.append(" address= ?, ");
            updateSql.append(" contactName= ?, ");
            updateSql.append(" contactAddress= ?, ");
            updateSql.append(" contactHomePhone= ?, ");
            updateSql.append(" contactWorkPhone= ?, ");
            updateSql.append(" contactMobilePhone= ?,");
            updateSql.append(" contactRelationship= ?, ");
            updateSql.append(" birthDate= ?, ");
            updateSql.append(" homePhone= ?, ");
            updateSql.append(" workPhone= ?, ");
            updateSql.append(" mobilePhone= ?, ");
            updateSql.append(" nationalId= ?, ");
            updateSql.append(" chineseName= ?, ");
            updateSql.append(" citizenship= ?, ");
            updateSql.append(" englishName= ?, ");
            updateSql.append(" ethnicGroup= ?, ");
            updateSql.append(" radiationId= ?, ");
            updateSql.append(" maritalStatus= ?, ");
            updateSql.append(" patientStatus= ?, ");
            updateSql.append(" patientHistory= ?, ");
            updateSql.append(" gender= ? ");
            updateSql.append(" WHERE patientSer = ? ");

            ps = connection.prepareStatement(updateSql.toString());
            int idx = 1;
            ps.setString(idx++, userContext.getLogin().getUsername());
            ps.setTimestamp(idx++, new Timestamp(new java.util.Date().getTime()));
            ps.setString(idx++, updateJson);
            if (photoBytes != null && photoBytes.length > 0) {
                ps.setBytes(idx++, photoBytes);
            }
            ps.setString(idx++, patient.getHisId());
            ps.setString(idx++, patient.getVip() != null ? patient.getVip().name() : null);
            ps.setString(idx++, patient.getPinyin());
            ps.setString(idx++, patient.getAddress());
            idx = buildContactStatement(patient, ps, idx);
            java.sql.Date birthDate = null;
            if (patient.getBirthDate() != null) {
                birthDate = new java.sql.Date(patient.getBirthDate().getTime());
            }
            ps.setDate(idx++, birthDate);
            ps.setString(idx++, patient.getHomePhone());
            ps.setString(idx++, patient.getWorkPhone());
            ps.setString(idx++, patient.getMobilePhone());
            ps.setString(idx++, patient.getNationalId());
            ps.setString(idx++, patient.getChineseName());
            ps.setString(idx++, patient.getCitizenship() != null ? patient.getCitizenship().getCode() : null);
            ps.setString(idx++, patient.getEnglishName());
            ps.setString(idx++, patient.getEthnicGroup() != null ? patient.getEthnicGroup().getCode() : null);
            ps.setString(idx++, patient.getRadiationId());
            ps.setString(idx++, patient.getMaritalStatus() != null ? patient.getMaritalStatus().name() : null);
            ps.setString(idx++, patient.getPatientStatus() != null ? patient.getPatientStatus().name() : null);
            ps.setString(idx++, patient.getPatientHistory());
            ps.setString(idx++, patient.getGender() != null ? patient.getGender().name() : null);
            ps.setLong(idx++, patientSer);
            affectedRow = ps.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
        return affectedRow;
    }

    private int buildContactStatement(Patient patient, PreparedStatement ps, int idx) throws SQLException {
        if (patient.getContacts() != null && !patient.getContacts().isEmpty()) {
            List<Contact> contacts = patient.getContacts();
            Contact contact = contacts.get(0);
            ps.setString(idx++, contact.getName());
            ps.setString(idx++, contact.getAddress());
            ps.setString(idx++, contact.getHomePhone());
            ps.setString(idx++, contact.getWorkPhone());
            ps.setString(idx++, contact.getMobilePhone());
            if (contact.getRelationship() != null) {
                ps.setString(idx++, contact.getRelationship().name());
            } else {
                ps.setString(idx++, null);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                ps.setString(idx++, null);
            }
        }
        return idx;
    }

    public Patient queryPatientByPatientSer(Connection connection, Long patientSer) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Patient patient = null;
        try {
            String querySql = "SELECT "
                    + " id, "
                    + " vip, "
                    + " hisId, "
                    + " vId, "
                    + " patientSer, "
                    //+ " photo, "
                    + " pinyin, "
                    + " address, "
                    + " contactName, "
                    + " contactAddress, "
                    + " contactHomePhone, "
                    + " contactWorkPhone, "
                    + " contactMobilePhone, "
                    + " contactRelationship, "
                    + " birthDate, "
                    + " homePhone, "
                    + " workPhone, "
                    + " mobilePhone, "
                    + " nationalId, "
                    + " chineseName, "
                    + " citizenship, "
                    + " englishName, "
                    + " ethnicGroup, "
                    + " radiationId, "
                    + " maritalStatus, "
                    + " patientStatus, "
                    + " patientHistory,"
                    + " gender"
                    + "  FROM patient "
                    + "  WHERE patientSer= ? ";
            ps = connection.prepareStatement(querySql);
            ps.setLong(1, patientSer);
            rs = ps.executeQuery();
            if (rs.next()) {
                patient = buildPatientFromResultSet(rs);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return patient;
    }

    private Patient buildPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        // patient = jsonSerializer.getObject(rs.getString(2), Patient.class);
        patient.setId(rs.getString("id"));
        String vip = rs.getString("vip");
        patient.setVip(vip == null ? null : VIPEnum.fromString(vip));
        patient.setHisId(rs.getString("hisId"));
        patient.setPatientSer(rs.getLong("patientSer"));
        // patient.setvid(rs.getString("vId"));
        // patient.setPhoto(PhotoUtil.encode(rs.getBytes("photo")));
        patient.setPinyin(rs.getString("pinyin"));
        patient.setAddress(rs.getString("address"));

        Contact contact = new Contact();
        contact.setName(rs.getString("contactName"));
        contact.setAddress(rs.getString("contactAddress"));
        contact.setHomePhone(rs.getString("contactHomePhone"));
        contact.setWorkPhone(rs.getString("contactWorkPhone"));
        contact.setMobilePhone(rs.getString("contactMobilePhone"));
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        patient.setContacts(contacts);

        String contactRelationship = rs.getString("contactRelationship");
        contact.setRelationship(contactRelationship == null ? null : RelationshipEnum.fromString(contactRelationship));
        patient.setBirthDate(rs.getDate("birthDate"));
        patient.setHomePhone(rs.getString("homePhone"));
        patient.setWorkPhone(rs.getString("workPhone"));
        patient.setMobilePhone(rs.getString("mobilePhone"));
        patient.setNationalId(rs.getString("nationalId"));
        patient.setChineseName(rs.getString("chineseName"));

        String citizenship = rs.getString("citizenship");
        CodeSystem citizenshipCS;
        if (StringUtils.isBlank(citizenship)) {
            citizenshipCS = null;
        } else {
            citizenshipCS = new CodeSystem();
            citizenshipCS.setCode(citizenship);
        }
        patient.setCitizenship(citizenshipCS);

        patient.setEnglishName(rs.getString("englishName"));

        String ethnicGroup = rs.getString("ethnicGroup");
        CodeSystem ethnicGroupCS;
        if (StringUtils.isBlank(ethnicGroup)) {
            ethnicGroupCS = null;
        } else {
            ethnicGroupCS = new CodeSystem();
            ethnicGroupCS.setCode(ethnicGroup);
        }
        patient.setEthnicGroup(ethnicGroupCS);

        patient.setRadiationId(rs.getString("radiationId"));

        String maritalStatus = rs.getString("maritalStatus");
        patient.setMaritalStatus(maritalStatus == null ? null : MaritalStatusEnum.fromString(maritalStatus));
        String patientStatus = rs.getString("patientStatus");
        patient.setPatientStatus(patientStatus == null ? null : PatientStatusEnum.fromString(patientStatus));
        patient.setPatientHistory(rs.getString("patientHistory"));
        patient.setGender(isNotBlank(rs.getString("gender")) ? GenderEnum.valueOf(rs.getString("gender")) : null);
        return patient;
    }
}