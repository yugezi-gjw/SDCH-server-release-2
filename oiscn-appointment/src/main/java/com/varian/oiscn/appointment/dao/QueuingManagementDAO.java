package com.varian.oiscn.appointment.dao;

import com.varian.oiscn.appointment.dto.CheckInStatusEnum;
import com.varian.oiscn.appointment.dto.QueuingManagement;
import com.varian.oiscn.appointment.dto.QueuingManagementDTO;
import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/20.
 */
@Slf4j
public class QueuingManagementDAO extends AbstractDAO<QueuingManagement> {

    private static String BUSINESS_COLUMN = "appointmentId,activityCode,hisId,encounterId,patientSer,deviceId,checkInStatus,checkInIdx,startTime,checkInTime";
    private static String ALL_COLUMN = "id," + BUSINESS_COLUMN;
    private static int DEFAULT_CHECKIN_IDX = 1;



    public QueuingManagementDAO(UserContext userContext) {
        super(userContext);
    }

    public QueuingManagementDAO() {
        super(null);
    }
    @Override
    protected String getTableName() {
        return "QueuingManagement";
    }

    @Override
    protected String getJsonbColumnName() {
        return null;
    }

    @Override
    public String create(Connection con, QueuingManagement queuingManagement) throws SQLException {
        String insertSql = "INSERT INTO " + getTableName() + " (" + BUSINESS_COLUMN + ") VALUES (?,?,?,?,?,?,?,?,?,?)";
        String key = null;
        PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
        List<Object> param = new ArrayList<>();
        param.add(queuingManagement.getAppointmentId());
        param.add(queuingManagement.getActivityCode());
        param.add(queuingManagement.getHisId());
        param.add(new Long(queuingManagement.getEncounterId()));
        param.add(queuingManagement.getPatientSer());
        param.add(queuingManagement.getDeviceId());
        param.add(queuingManagement.getCheckInStatus().name());
        param.add(queuingManagement.getCheckInIdx() == null ? DEFAULT_CHECKIN_IDX : queuingManagement.getCheckInIdx());
        param.add(new Timestamp(queuingManagement.getStartTime().getTime()));
        param.add(new Timestamp(queuingManagement.getCheckInTime().getTime()));
        for(int i=0;i<param.size();i++){
            ps.setObject(i+1,param.get(i));
        }
        int effect = ps.executeUpdate();
        if (effect > 0) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getString(1);
            }
        }
        return key;
    }

    /**
     * 根据id或者appointmentId更新checkInStatus  checkInIdx checkInStep
     *
     * @param con
     * @param queuingManagement
     * @return
     * @throws SQLException
     */
    public int updateIdx(Connection con, QueuingManagement queuingManagement) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ").append(this.getTableName()).append(" SET checkInIdx = ? WHERE ");
        List<Object> param = new ArrayList<>();
        if (StringUtils.isNotEmpty(queuingManagement.getId())) {
            sb.append(" id = ? ");
            param.add(new Long(queuingManagement.getId()));
        } else {
            if(StringUtils.isNotEmpty(queuingManagement.getAppointmentId())){
                sb.append(" appointmentId = ? ");
                param.add(queuingManagement.getAppointmentId());
                if(StringUtils.isNotEmpty(queuingManagement.getActivityCode())) {
                    sb.append(" AND  activityCode = ? ");
                    param.add(queuingManagement.getActivityCode());
                }
            }
        }
        if(!CheckInStatusEnum.DELETED.equals(queuingManagement.getCheckInStatus())){
            sb.append(" AND checkInStatus != ?");
            param.add(CheckInStatusEnum.DELETED.name());
        }
        PreparedStatement ps = con.prepareStatement(sb.toString());
        ps.setInt(1, queuingManagement.getCheckInIdx());
        for(int i=0;i<param.size();i++){
            ps.setObject(i+2,param.get(i));
        }
        return ps.executeUpdate();
    }

    /**
     * 根据id或者appointmentId更新checkInStatus  checkInIdx checkInStep
     *
     * @param con
     * @param queuingManagement
     * @return
     * @throws SQLException
     */
    public int updateStatusAndIdx(Connection con, QueuingManagement queuingManagement) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ").append(this.getTableName()).append(" SET checkInStatus = ?,checkInIdx = ? WHERE ");
        List<Object> param = new ArrayList<>();
        if (StringUtils.isNotEmpty(queuingManagement.getId())) {
            sb.append(" id = ? ");
            param.add(new Long(queuingManagement.getId()));
        } else {
            if(StringUtils.isNotEmpty(queuingManagement.getAppointmentId())){
                sb.append(" appointmentId = ? ");
                param.add(queuingManagement.getAppointmentId());
                if(StringUtils.isNotEmpty(queuingManagement.getActivityCode())) {
                    sb.append(" AND  activityCode = ? ");
                    param.add(queuingManagement.getActivityCode());
                }
            }
        }
        if(!CheckInStatusEnum.DELETED.equals(queuingManagement.getCheckInStatus())){
            sb.append(" AND checkInStatus != ?");
            param.add(CheckInStatusEnum.DELETED.name());
        }
        PreparedStatement ps = con.prepareStatement(sb.toString());
        ps.setString(1, queuingManagement.getCheckInStatus().name());
        ps.setInt(2,queuingManagement.getCheckInIdx());
        for(int i=0;i<param.size();i++){
            ps.setObject(i+3,param.get(i));
        }
        return ps.executeUpdate();
    }

    public int updateUid2AriaAppointmentId(Connection conn, String uid,String appointmentId) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ").append(this.getTableName()).append(" SET appointmentId=? WHERE appointmentId=?");
        PreparedStatement ps = conn.prepareStatement(sb.toString());
        ps.setString(1,appointmentId);
        ps.setString(2,uid);
        return ps.executeUpdate();
    }

    public int queryMaxCheckIdxByCheckInTime(Connection con, QueuingManagementDTO queuingManagementDTO) throws SQLException {
        StringBuffer sb = new StringBuffer("SELECT max(checkInIdx) FROM ").append("(SELECT checkInIdx FROM ").append(this.getTableName()).append(" WHERE ")
                .append(" checkInTime >= ? AND checkInTime < ?  AND checkInStatus = ? AND activityCode = ? ) t ");

        Calendar calendar = GregorianCalendar.getInstance();
        try {
            java.util.Date checkInTimeStart = DateUtil.parse(DateUtil.formatDate(DateUtil.parse(DateUtil.formatDate(queuingManagementDTO.getCheckInTime(), DateUtil.DATE_FORMAT)), DateUtil.DATE_TIME_FORMAT));
            calendar.setTime(checkInTimeStart);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            java.util.Date checkInTimeEnd = calendar.getTime();

            PreparedStatement ps = con.prepareStatement(sb.toString());
            ps.setTimestamp(1, new Timestamp(checkInTimeStart.getTime()));
            ps.setTimestamp(2, new Timestamp(checkInTimeEnd.getTime()));
            ps.setString(3,queuingManagementDTO.getCheckInStatus().name());
            ps.setString(4,queuingManagementDTO.getActivityCode());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (ParseException e) {
            log.error("Exception: {}", e.getMessage());
        }
        return -1;
    }

    public List<QueuingManagement> queryList(Connection con, QueuingManagementDTO queuingManagementDTO) throws SQLException {
        List<QueuingManagement> list = new ArrayList<>();
        StringBuffer sb = new StringBuffer("SELECT ").append(ALL_COLUMN).append(" FROM ").append(getTableName()).append(" WHERE 1=1 ");
        List<Object> param = new ArrayList<>();
        try {
            if (StringUtils.isNotEmpty(queuingManagementDTO.getAppointmentId())) {
                sb.append(" AND appointmentId = ? ");
                param.add(queuingManagementDTO.getAppointmentId());
            }
            if(queuingManagementDTO.getAppointmentIdList() != null && !queuingManagementDTO.getAppointmentIdList().isEmpty()){
                sb.append("AND (");
                for(int i=0;i<queuingManagementDTO.getAppointmentIdList().size();i++){
                    if(i < queuingManagementDTO.getAppointmentIdList().size() -1){
                        sb.append(" appointmentId = ? OR ");
                    }else{
                        sb.append("appointmentId = ?");
                    }
                }
                sb.append(")");
                param.addAll(queuingManagementDTO.getAppointmentIdList());
            }
            if (queuingManagementDTO.getStartTimeStart() != null) {
                java.util.Date startTimeStart = DateUtil.parse(DateUtil.formatDate(DateUtil.parse(DateUtil.formatDate(queuingManagementDTO.getStartTimeStart(), DateUtil.DATE_FORMAT)), DateUtil.DATE_TIME_FORMAT));
                sb.append(" AND startTime >= ? ");
                param.add(new Timestamp(startTimeStart.getTime()));
            }
            if (queuingManagementDTO.getStartTimeEnd() != null) {
                java.util.Date startTimeEnd = DateUtil.parse(DateUtil.formatDate(DateUtil.parse(DateUtil.formatDate(queuingManagementDTO.getStartTimeEnd(), DateUtil.DATE_FORMAT)), DateUtil.DATE_TIME_FORMAT));
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(startTimeEnd);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                startTimeEnd = calendar.getTime();
                sb.append(" AND startTime < ? ");
                param.add(new Timestamp(startTimeEnd.getTime()));
            }
            if (queuingManagementDTO.getCheckInStartTime() != null) {
                java.util.Date checkInTime = DateUtil.parse(DateUtil.formatDate(DateUtil.parse(DateUtil.formatDate(queuingManagementDTO.getCheckInStartTime(), DateUtil.DATE_FORMAT)), DateUtil.DATE_TIME_FORMAT));
                sb.append(" AND checkInTime >= ? ");
                param.add(new Timestamp(checkInTime.getTime()));
            }
            if (queuingManagementDTO.getCheckInEndTime() != null) {
                Calendar calendar = GregorianCalendar.getInstance();
                java.util.Date checkInTime = DateUtil.parse(DateUtil.formatDate(DateUtil.parse(DateUtil.formatDate(queuingManagementDTO.getCheckInEndTime(), DateUtil.DATE_FORMAT)), DateUtil.DATE_TIME_FORMAT));
                calendar.setTime(checkInTime);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                java.util.Date checkInTimeEnd = calendar.getTime();
                sb.append(" AND checkInTime < ? ");
                param.add(new Timestamp(checkInTimeEnd.getTime()));
            }
            if(queuingManagementDTO.getCheckInStatusList() != null && !queuingManagementDTO.getCheckInStatusList().isEmpty()){
                sb.append("AND (");
                for(int i=0;i<queuingManagementDTO.getCheckInStatusList().size();i++){
                    param.add(queuingManagementDTO.getCheckInStatusList().get(i).name());
                    if(i == 0){
                        sb.append("checkInStatus = ? ");
                    }else{
                        sb.append(" OR checkInStatus = ? ");
                    }
                }
                sb.append(")");
            }
            if (StringUtils.isNotEmpty(queuingManagementDTO.getActivityCode())) {
                sb.append(" AND activityCode = ? ");
                param.add(queuingManagementDTO.getActivityCode());
            }
            if (StringUtils.isNotEmpty(queuingManagementDTO.getHisId())) {
                sb.append(" AND hisId = ? ");
                param.add(queuingManagementDTO.getHisId());
            }
            if (queuingManagementDTO.getPatientSer() != null) {
                sb.append(" AND patientSer = ? ");
                param.add(queuingManagementDTO.getPatientSer());
            }
            if (queuingManagementDTO.getDeviceId() != null) {
                sb.append(" AND deviceId = ? ");
                param.add(queuingManagementDTO.getDeviceId());
            }
            sb.append(" ORDER BY checkInIdx DESC,startTime ASC,checkInTime ASC");
            PreparedStatement ps = con.prepareStatement(sb.toString());
            for (int i = 0; i < param.size(); i++) {
                ps.setObject(i + 1, param.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs2Dto(rs));
            }
        } catch (ParseException e) {
            log.error("ParseException: {}", e.getMessage());
        }
        return list;
    }

    public boolean ifAlreadyCheckedIn(Connection con, String appointmentId) throws SQLException{
        String sql = "SELECT appointmentid FROM " + getTableName() + " WHERE appointmentid = ? AND checkinstatus != '" + CheckInStatusEnum.DELETED + "'";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, appointmentId);
        ResultSet rs = ps.executeQuery();
        boolean result = rs.next();
        rs.close();
        return result;
    }

    private QueuingManagement rs2Dto(ResultSet rs) throws SQLException {
        QueuingManagement queuingManagement = new QueuingManagement(){{
            setId(rs.getString("id"));
            setAppointmentId(rs.getString("appointmentId"));
            setActivityCode(rs.getString("activityCode"));
            setHisId(rs.getString("hisId"));
            setEncounterId(rs.getString("encounterId"));
            setPatientSer(rs.getLong("patientSer"));
            setDeviceId(rs.getString("deviceId"));
            setCheckInStatus(CheckInStatusEnum.valueOf(rs.getString("checkInStatus")));
            setCheckInIdx(rs.getInt("checkInIdx"));
            setStartTime(new java.util.Date(rs.getTimestamp("startTime").getTime()));
            setCheckInTime(new java.util.Date(rs.getTimestamp("checkInTime").getTime()));
        }};
        return queuingManagement;
    }
}
