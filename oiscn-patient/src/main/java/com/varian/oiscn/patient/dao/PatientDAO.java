package com.varian.oiscn.patient.dao;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.base.util.PhotoUtil;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.patient.*;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by gbt1220 on 3/28/2017.
 */
public class PatientDAO extends AbstractDAO<Patient> {

    /**
     * Default Constructor.<br>
     *
     * @param userContext
     */
    public PatientDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "Patient";
    }

    @Override
    protected String getJsonbColumnName() {
        return "patientInfo";
    }

    /* (non-Javadoc)
     * @see com.varian.oiscn.base.dao.AbstractDAO#create(java.sql.Connection, java.lang.Object)
     */
    @Override
    public String create(Connection con, Patient patient) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String newId = StringUtils.EMPTY;
        try {
            Timestamp curTime = new Timestamp(new java.util.Date().getTime());

            // Retrieve the photo data from Json
            byte[] photoBytes = PhotoUtil.decode(patient.getPhoto());
            // photo data in Json will be null
            patient.setPhoto(null);

            String json = new JsonSerializer<Patient>().getJson(patient);
            StringBuilder sql = new StringBuilder("INSERT INTO ").append(getTableName());
            sql.append(" (createdUser, createdDate, lastUpdatedUser, lastUpdatedDate, ").append(getJsonbColumnName())
                    .append(", vip,hisId,vId,patientSer,pinyin,address,contactName,contactAddress,contactHomePhone,contactWorkPhone,contactMobilePhone,contactRelationship,")
                    .append("birthDate,homePhone,workPhone,mobilePhone,nationalId,chineseName,citizenship,englishName,ethnicGroup,radiationId,maritalStatus,patientStatus,patientHistory, gender, photo)");
            sql.append(" VALUES(?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            int idx = 1;
            ps = con.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(idx++, userContext.getName());
            ps.setTimestamp(idx++, curTime);
            ps.setString(idx++, userContext.getName());
            ps.setTimestamp(idx++, curTime);
            ps.setString(idx++, json);

            ps.setString(idx++, patient.getVip() != null ? patient.getVip().name() : null);
            ps.setString(idx++, patient.getHisId());
            ps.setString(idx++, null);
            ps.setLong(idx++, patient.getPatientSer());
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
            ps.setBytes(idx++, photoBytes);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getString(1);
            }

        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return newId;
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

    /**
     * Update patient information in Json and photo column.<br>
     *
     * @param connection The connection
     * @param patient    Patient information with photo bytes.
     * @param patientSer The Patient's patientSer
     * @return the affected row number
     * @throws SQLException
     */
    public int updateByPatientSer(Connection connection, Patient patient, Long patientSer) throws SQLException {
        PreparedStatement ps = null;
        int affectedRow = 0;
        try {
            // Retrieve the photo data from Json
            byte[] photoBytes = PhotoUtil.decode(patient.getPhoto());
            // photo data in Json will be null
            patient.setPhoto(null);

            String updateJson = new JsonSerializer<Patient>().getJson(patient);
            StringBuilder updateSql = new StringBuilder("UPDATE patient");
            updateSql.append(" SET lastUpdatedUser= ?, lastUpdatedDate= ?, " + getJsonbColumnName() + " = ?, ");
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

    /**
     * Query the patient information without photo data by HIS id.<br>
     *
     * @param connection The connection
     * @param patientSer Patient's patientSer
     * @return Patient information
     * @throws SQLException
     */
    public Patient queryByPatientSer(Connection connection, Long patientSer) throws SQLException {
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

    /**
     * Query the patient information without photo data by HIS id.<br>
     *
     * @param connection The connection
     * @param hisId      Patient's HIS id
     * @return Patient information
     * @throws SQLException
     */
    public Patient queryByHisId(Connection connection, String hisId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Patient patient = null;
        String id;
        try {
            String querySql = "SELECT "
                + " id, "
                + " vip, "
                + " hisId, "
                + " patientSer, "
                + " vId, "
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
                    + " gender "
                + "  FROM patient "
                + "  WHERE hisId = ? ORDER BY lastUpdatedDate DESC";
            ps = connection.prepareStatement(querySql);
            ps.setString(1,hisId);
            rs = ps.executeQuery();
            if (rs.next()) {
                patient = buildPatientFromResultSet(rs);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return patient;
    }

    /**
     * Return the photo data list in bytes by HIS Id List.<br>
     *
     * @param patientSerList Patient HIS Id List
     * @return The photo data in bytes List
     * @throws SQLException
     */
    public Map<Long, byte[]> getPhotoBytesListByPatientSerList(Connection con, List<Long> patientSerList) throws SQLException {

        Map<Long, byte[]> photoMap = new HashMap<>();
        if (patientSerList == null || patientSerList.size() == 0) {
            // no id
            return photoMap;
        }

        long id;
        byte[] photoBytes;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder(" SELECT patientSer, photo FROM patient WHERE patientSer IN (");
            int idSize = patientSerList.size();
            sql.append("? ");
            for (int index = 1; index < idSize; ++index) {
                sql.append(",? ");
            }
            sql.append(")");
            ps = con.prepareStatement(sql.toString());
            int index = 1;
            for (Long patientSer : patientSerList) {
                ps.setLong(index++, patientSer);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getLong("patientSer");
                photoBytes = rs.getBytes("photo");
                photoMap.put(id, photoBytes);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return photoMap;
    }

    /**
     * Update patient photo data.<br>
     *
     * @param patientSer the patientSer
     * @param photoBytes photo data in bytes
     * @return updated row
     * @throws SQLException
     */
    public int updatePhoto(Connection con, Long patientSer, byte[] photoBytes) throws SQLException {
        int affectedRow = 0;
        PreparedStatement ps = null;
        try {
            StringBuilder sql = new StringBuilder("UPDATE patient");
            sql.append(" SET lastUpdatedUser= ?, lastUpdatedDate= ?, photo= ? ");
            sql.append(" WHERE patientSer= ? ");
            ps = con.prepareStatement(sql.toString());
            ps.setString(1, userContext.getLogin().getUsername());
            ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            ps.setBytes(3, photoBytes);
            ps.setLong(4, patientSer);
            affectedRow = ps.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
        return affectedRow;
    }

    /**
     * 返回所有有Encounter信息的并且是未结束治疗的患者的PatientSer
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public List<Long> queryAllActivePatientSer(Connection conn) throws SQLException {
        List<Long> patientSerList = new ArrayList<>();
        String sql = " SELECT DISTINCT p.patientSer "
                + "  FROM Patient p, Encounter e "
                + " WHERE p.patientSer = e.patientSer "
                + "   AND e.status = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, StatusEnum.IN_PROGRESS.name());
            rs = ps.executeQuery();
            while (rs.next()) {
                patientSerList.add(rs.getLong(1));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return patientSerList;
    }

    private Patient buildPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        // patient = jsonSerializer.getObject(rs.getString(2), Patient.class);
        patient.setId(rs.getString("id"));
        String vip = rs.getString("vip");
        patient.setVip(vip == null? null: VIPEnum.fromString(vip));
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
        contact.setRelationship(contactRelationship == null ? null: RelationshipEnum.fromString(contactRelationship));
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
        patient.setMaritalStatus(maritalStatus == null? null: MaritalStatusEnum.fromString(maritalStatus));
        String patientStatus = rs.getString("patientStatus");
        patient.setPatientStatus(patientStatus == null? null: PatientStatusEnum.fromString(patientStatus));
        patient.setPatientHistory(rs.getString("patientHistory"));
        patient.setGender(isNotBlank(rs.getString("gender")) ? GenderEnum.valueOf(rs.getString("gender")) : null);
        return patient;
    }
}
