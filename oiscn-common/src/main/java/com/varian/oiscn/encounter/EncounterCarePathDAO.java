package com.varian.oiscn.encounter;

import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhp9696 on 2017/11/9.
 */
@Slf4j
public class EncounterCarePathDAO {

    private static final String SQL_INSERT = "INSERT INTO EncounterCarePath (encounterId,cpInstanceId,category,crtUser,crtTime) VALUES (?,?,?,?,?)";
    private UserContext userContext;

    public EncounterCarePathDAO(UserContext userContext){
        this.userContext = userContext;
    }
    /**
     * Add new CarePath Instance Id in EncounterCarePath table.<br>
     *
     * @param con                Connection
     * @param encounterCarePath  EncounterCarePath
     * @return affectedRow
     * @throws SQLException
     */
    public int addCarePathInstanceId(Connection con, EncounterCarePath encounterCarePath) throws SQLException {
        PreparedStatement ps = null;
        int affectedRow = 0;
        try {
            ps = con.prepareStatement(SQL_INSERT);
            ps.setLong(1, encounterCarePath.getEncounterId());
            ps.setLong(2, encounterCarePath.getCpInstanceId());
            ps.setString(3,encounterCarePath.getCategory().name());
            ps.setString(4,userContext.getLogin().getUsername());
            ps.setTimestamp(5,new Timestamp(new java.util.Date().getTime()));
            affectedRow = ps.executeUpdate();
        } catch (RuntimeException re) {
            log.error("addCarePathInstanceId RuntimeException: {}", re.getMessage());
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, null);
        }
        return affectedRow;
    }
    /**
     * Count the carepath in encounter by specified HisId.<br>
     *
     * @param con   Connection
     * @param patientSer patientSer
     * @return count
     * @throws SQLException
     */
    public int countCarePathByPatientSer(Connection con, Long patientSer) throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) AS CP_CNT FROM EncounterCarePath ecp "
                + " INNER JOIN Encounter e ON e.patientSer=? AND  e.status = 'PLANNED' AND ecp.encounterId = e.id ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setLong(1, patientSer);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            DatabaseUtil.safeCloseResultSet(rs);
            DatabaseUtil.safeCloseStatement(ps);
        }
        return count;
    }

    /**
     *
     * @param connection
     * @param patientSerList
     * @return
     * @throws SQLException
     */
    public List<PatientEncounterCarePath> selectCarePathInstanceIdList(Connection connection, List<String> patientSerList) throws SQLException {
        List<PatientEncounterCarePath> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT e.id, e.patientSer, e.status, ea.cpInstanceId,ea.category,ea.crtUser,ea.crtTime FROM  EncounterCarePath ea, Encounter e WHERE e.id = ea.encounterId ");
        if(patientSerList != null && !patientSerList.isEmpty()){
            sql.append(" AND (");
            for(int i=0;i<patientSerList.size();i++){
                if(i <patientSerList.size()-1){
                    sql.append("e.patientSer = ? OR ");
                }else{
                    sql.append("e.patientSer = ? ");
                }
            }
            sql.append(")");
        }
        sql.append(" ORDER BY e.patientSer ASC, ea.category DESC,ea.cpInstanceId DESC");
        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for(int i=0;i<patientSerList.size();i++){
            ps.setString(i+1,patientSerList.get(i));
        }
        ResultSet rs = ps.executeQuery();
        Map<String,PatientEncounterCarePath> tmpMap = new LinkedHashMap<>();
        while(rs.next()){
            String patientSer = rs.getString("patientSer");
//          从map中根据patientSer获取EncounterHeaderActivity
            PatientEncounterCarePath patientEncounterCarePath = tmpMap.get(patientSer);
            if(patientEncounterCarePath == null){
                patientEncounterCarePath = new PatientEncounterCarePath();
                patientEncounterCarePath.setPatientSer(patientSer);
                tmpMap.put(patientSer, patientEncounterCarePath);
            }
            Long encounterId = rs.getLong("id");
            Long activityHeaderId = rs.getLong("cpInstanceId");
            EncounterCarePath.EncounterCarePathCategoryEnum category = EncounterCarePath.EncounterCarePathCategoryEnum.fromCode(StringUtils.trimToEmpty(rs.getString("category")));
            EncounterCarePath encounterCarePath = new EncounterCarePath(){{
                setEncounterId(encounterId);
                setCpInstanceId(activityHeaderId);
                setCategory(category);
                setCrtUser(rs.getString("crtUser"));
                setCrtTime(rs.getTimestamp("crtTime"));
            }};
            StatusEnum statusEnum = StatusEnum.valueOf(rs.getString("status"));
            if(StatusEnum.IN_PROGRESS.equals(statusEnum)){//正在治疗的Encounter 一般一个患者只有一个正在进行的Encounter
                EncounterCarePathList plannedActivity = patientEncounterCarePath.getPlannedCarePath();
                if(plannedActivity == null){
                    plannedActivity = new EncounterCarePathList(){{
                        setEncounterId(encounterId);
                        getEncounterCarePathList().add(encounterCarePath);
                    }};
                    patientEncounterCarePath.setPlannedCarePath(plannedActivity);
                }else{
                    plannedActivity.getEncounterCarePathList().add(encounterCarePath);
                }
            }else{//已经结束的治疗
                List<EncounterCarePathList> finishEncounterCarePathListList = patientEncounterCarePath.getCompletedCarePath();
//              判断一下 encounterId对应的EncounterHeaderId是否已经存在
                EncounterCarePathList existsEncounterCarePathList = null;
                for(EncounterCarePathList encounterCarePathList : finishEncounterCarePathListList){
                    if(encounterId == encounterCarePathList.getEncounterId()){
                        existsEncounterCarePathList = encounterCarePathList;
                        break;
                    }
                }
                if(existsEncounterCarePathList == null){
                    existsEncounterCarePathList = new EncounterCarePathList(){{
                        setEncounterId(encounterId);
                    }};
                    finishEncounterCarePathListList.add(existsEncounterCarePathList);
                }
                existsEncounterCarePathList.getEncounterCarePathList().add(encounterCarePath);
            }
        }
        tmpMap.forEach((s, encounterHeaderActivity) -> {
            list.add(encounterHeaderActivity);
        });
        return list;
    }
}
