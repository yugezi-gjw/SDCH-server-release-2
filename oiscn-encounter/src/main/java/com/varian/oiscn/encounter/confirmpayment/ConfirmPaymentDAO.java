package com.varian.oiscn.encounter.confirmpayment;

import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.util.DatabaseUtil;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.user.UserContext;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by bhp9696 on 2017/7/31.
 */
public class ConfirmPaymentDAO extends AbstractDAO<ConfirmPayment> {
    public ConfirmPaymentDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String    getTableName() {
        return "ConfirmPayment";
    }

    @Override
    protected String getJsonbColumnName() {
        return "";
    }


    @Override
    public String create(Connection con, ConfirmPayment confirmPayment) throws SQLException {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String createId  = null;
        try {
            Timestamp curTime = new Timestamp(new java.util.Date().getTime());
            StringBuilder createSql = new StringBuilder("INSERT INTO ");
            createSql.append(getTableName()).append("(createdUser,createdDate,lastUpdatedUser,lastUpdatedDate,hisId,encounterId,patientSer)");
            createSql.append(" VALUES(?,?,?,?,?,?,?)");
            ps = con.prepareStatement(createSql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, userContext.getName());
            ps.setTimestamp(2, curTime);
            ps.setString(3, userContext.getName());
            ps.setTimestamp(4, curTime);
            ps.setString(5, confirmPayment.getHisId());
            ps.setLong(6,Long.parseLong(confirmPayment.getEncounterId()));
            ps.setLong(7,confirmPayment.getPatientSer());
            ps.executeUpdate();
            resultSet = ps.getGeneratedKeys();
            if(resultSet.next()) {
                createId = resultSet.getString(1);
                if(confirmPayment.getConfirmStatusList() != null) {
                    batchCreateConfirmStatus(con, Long.parseLong(createId), confirmPayment.getConfirmStatusList());
                }
                if(confirmPayment.getTreatmentConfirmStatus() != null && StringUtils.isNotEmpty(confirmPayment.getTreatmentConfirmStatus().getActivityCode())) {
                    createTreatmentPaymentConfirmStatus(con, Long.parseLong(createId), confirmPayment.getTreatmentConfirmStatus());
                }
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
        return createId;
    }

    @Override
    public boolean update(Connection connection, ConfirmPayment confirmPayment, String id) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            StringBuilder updateSql = new StringBuilder("UPDATE ");
            updateSql.append(getTableName());
            updateSql.append(" SET lastUpdatedUser=?,lastUpdatedDate=? ");
            updateSql.append(" WHERE id=?");
            preparedStatement = connection.prepareStatement(updateSql.toString());
            preparedStatement.setString(1, userContext.getLogin().getUsername());
            preparedStatement.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            preparedStatement.setLong(3, Long.parseLong(id));
            int update = preparedStatement.executeUpdate();
            if(update > 0){
                if(confirmPayment.getConfirmStatusList() != null) {
                    int updateCount = batchUpdateConfirmStatus(connection, Long.parseLong(id), confirmPayment.getConfirmStatusList());
                    if(updateCount < confirmPayment.getConfirmStatusList().size()){
                        deleteConfirmStatusByConfirmPaymentId(connection, Long.parseLong(id), "ConfirmStatus");
                        batchCreateConfirmStatus(connection,Long.parseLong(id),confirmPayment.getConfirmStatusList());
                    }
                }
                if(confirmPayment.getTreatmentConfirmStatus() != null && StringUtils.isNotEmpty(confirmPayment.getTreatmentConfirmStatus().getActivityCode())) {
                    updateTreatmentPaymentConfirmStatus(connection,Long.parseLong(id),confirmPayment.getTreatmentConfirmStatus());
                }
            }
        } finally {
            DatabaseUtil.safeCloseStatement(preparedStatement);
        }
        return true;
    }

    private int deleteConfirmStatusByConfirmPaymentId(Connection conn,Long id,String tblName) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM "+tblName+" WHERE confirmPaymentId = ?");
        ps.setLong(1,id);
        return ps.executeUpdate();
    }



    private int batchCreateConfirmStatus(Connection conn,Long confirmPaymentId,List<ConfirmStatus> confirmStatusList) throws SQLException {
        String sql = "INSERT INTO ConfirmStatus (confirmPaymentId,activityCode,activityContent,status,carePathInstanceId) VALUES (?,?,?,?,?) ";
        PreparedStatement ps = conn.prepareStatement(sql);
        for(ConfirmStatus confirmStatus : confirmStatusList){
            ps.setLong(1,confirmPaymentId);
            ps.setString(2,confirmStatus.getActivityCode());
            ps.setString(3,confirmStatus.getActivityContent());
            ps.setInt(4,confirmStatus.getStatus());
            ps.setLong(5,confirmStatus.getCarePathInstanceId());
            ps.addBatch();
        }
        return ps.executeBatch().length;
    }

    private int batchUpdateConfirmStatus(Connection conn,Long confirmPaymentId,List<ConfirmStatus> confirmStatusList) throws SQLException {
        String sql = "UPDATE ConfirmStatus SET status = ? WHERE confirmPaymentId= ? AND activityCode = ? AND carePathInstanceId = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        for(ConfirmStatus confirmStatus : confirmStatusList){
            ps.setInt(1,confirmStatus.getStatus());
            ps.setLong(2,confirmPaymentId);
            ps.setString(3,confirmStatus.getActivityCode());
            ps.setLong(4,confirmStatus.getCarePathInstanceId());
            ps.addBatch();
        }
        int[] r = ps.executeBatch();
        int effectSize = 0;
        for(int x:r){
            if(x > 0){
                effectSize++;
            }
        }
        return effectSize;
    }
    private int createTreatmentPaymentConfirmStatus(Connection conn,Long confirmPaymentId, TreatmentConfirmStatus treatmentConfirmStatus) throws SQLException {
        String sql = "INSERT INTO TreatmentConfirmStatus (confirmPaymentId,activityCode,activityContent,totalPaymentCount,confirmPaymentCount) VALUES (?,?,?,?,?) ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1,confirmPaymentId);
        ps.setString(2,treatmentConfirmStatus.getActivityCode());
        ps.setString(3,treatmentConfirmStatus.getActivityContent());
        ps.setInt(4,treatmentConfirmStatus.getTotalPaymentCount());
        ps.setInt(5,treatmentConfirmStatus.getConfirmPaymentCount());
        return ps.executeUpdate();
    }

    private int updateTreatmentPaymentConfirmStatus(Connection conn,Long confirmPaymentId, TreatmentConfirmStatus treatmentConfirmStatus) throws SQLException {
        String sql = "UPDATE TreatmentConfirmStatus SET totalPaymentCount = ? ,confirmPaymentCount = ? WHERE confirmPaymentId =?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1,treatmentConfirmStatus.getTotalPaymentCount());
        ps.setInt(2,treatmentConfirmStatus.getConfirmPaymentCount());
        ps.setLong(3,confirmPaymentId);
        return ps.executeUpdate();
    }
    public ConfirmPayment selectConfirmPaymentByPatientSer(Connection connection, Long patientSer) throws SQLException {
        List<ConfirmPayment> confirmPaymentList = this.selectConfirmPaymentByPatientSerList(connection, Arrays.asList(String.valueOf(patientSer)));
        if (!confirmPaymentList.isEmpty()) {
            return confirmPaymentList.get(0);
        } else {
            return null;
        }
    }

    public List<ConfirmPayment> selectConfirmPaymentByPatientSerList(Connection connection, List<String> patientSerList) throws SQLException {
        List<ConfirmPayment> confirmPaymentList;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder builder = new StringBuilder("select c.id,c.hisId,c.encounterId,c.patientSer,cs.activityCode,cs.activityContent,cs.status,cs.carePathInstanceId from " + this.getTableName() + " as c,ConfirmStatus as cs,Encounter as e where (");
            for (int i = 0; i < patientSerList.size(); i++) {
                if (i == patientSerList.size() - 1) {
                    builder.append("c.patientSer = ?");
                } else {
                    builder.append("c.patientSer = ? or ");
                }
            }
            builder.append(") and c.encounterId = e.id  and e.status = ? and c.id = cs.confirmPaymentId order by c.hisId,cs.carePathInstanceId");

            ps = connection.prepareStatement(builder.toString());
            for (int i = 0; i < patientSerList.size(); i++) {
                ps.setString(i + 1, patientSerList.get(i));
            }
            ps.setString(patientSerList.size() + 1, StatusEnum.IN_PROGRESS.name());
            rs = ps.executeQuery();
            confirmPaymentList = rs2ConfirmPayment(rs);
            setTreatmentConfirmStatus(connection,patientSerList,confirmPaymentList);
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return confirmPaymentList;
    }

    private List<ConfirmPayment> rs2ConfirmPayment(ResultSet rs) throws SQLException {
        List<ConfirmPayment> confirmPaymentList = new ArrayList<>();
        Map<String,ConfirmPayment> map = new HashMap<>();

        ConfirmStatus confirmStatus;
        String patientSer ;
        String encounterId ;
        ConfirmPayment confirmPayment;
        while(rs.next()){
            patientSer = rs.getString("patientSer");
            encounterId = rs.getString("encounterId");
            confirmPayment = map.get(patientSer);
            if(confirmPayment == null){
                confirmPayment = new ConfirmPayment();
                confirmPayment.setId(rs.getString("id"));
                confirmPayment.setEncounterId(encounterId);
                confirmPayment.setPatientSer(new Long(patientSer));
                confirmPayment.setConfirmStatusList(new ArrayList<>());
                map.put(patientSer,confirmPayment);
                confirmPaymentList.add(confirmPayment);
            }
            confirmStatus = new ConfirmStatus(){{
                setActivityCode(rs.getString("activityCode"));
                setActivityContent(rs.getString("activityContent"));
                setStatus(rs.getInt("status"));
                setCarePathInstanceId(rs.getLong("carePathInstanceId"));
            }};
            confirmPayment.getConfirmStatusList().add(confirmStatus);
        }
        return confirmPaymentList;
    }


    private void setTreatmentConfirmStatus(Connection conn,List<String> patientSerList,List<ConfirmPayment> list) throws SQLException {
        PreparedStatement ps;
        ResultSet rs;
        StringBuilder builder = new StringBuilder("select c.id,c.hisId,c.encounterId,cs.activityCode,cs.activityContent,cs.totalPaymentCount,cs.confirmPaymentCount from " + this.getTableName() + " as c,TreatmentConfirmStatus as cs,Encounter as e where (");
        for (int i = 0; i < patientSerList.size(); i++) {
            if (i == patientSerList.size() - 1) {
                builder.append("c.patientSer = ?");
            } else {
                builder.append("c.patientSer = ? or ");
            }
        }
        builder.append(") and c.encounterId = e.id  and e.status = ? and c.id = cs.confirmPaymentId order by c.patientSer");
        ps = conn.prepareStatement(builder.toString());
        for (int i = 0; i < patientSerList.size(); i++) {
            ps.setLong(i + 1, Long.parseLong(patientSerList.get(i)));
        }
        ps.setString(patientSerList.size() + 1, StatusEnum.IN_PROGRESS.name());
        rs = ps.executeQuery();
        while(rs.next()){
            String confirmPaymentId = rs.getString("id");
            for(ConfirmPayment confirmPayment:list){
                if(confirmPaymentId.equals(confirmPayment.getId())){
                    confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus(){{
                        setActivityCode(rs.getString("activityCode"));
                        setActivityContent(rs.getString("activityContent"));
                        setTotalPaymentCount(rs.getInt("totalPaymentCount"));
                        setConfirmPaymentCount(rs.getInt("confirmPaymentCount"));
                    }});
                    break;
                }
            }
        }

    }

    /**
     * 查询根据activityCode查询是否确费 -制模 CT 复位
     * @param connection
     * @param patientSerList
     * @param activityCode
     * @return
     * @throws SQLException
     */
    public List<ConfirmPayment> queryConfirmStatusByPatientSerList(Connection connection, List<String> patientSerList, String activityCode) throws SQLException {
        List<ConfirmPayment> resultList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT cp.hisId,cp.encounterId,cp.patientSer,cs.activityCode,cs.status,cs.carePathInstanceId FROM ConfirmPayment cp,ConfirmStatus cs,Encounter e " +
                "WHERE cp.encounterId=e.id AND e.status=? AND cp.id=cs.confirmPaymentId AND cs.activityCode=? AND (");
        List<String> paramList = new ArrayList<>();
        paramList.add(StatusEnum.IN_PROGRESS.name());
        paramList.add(activityCode);
        paramList.addAll(patientSerList);
        for(int i=0;i<patientSerList.size();i++){
            if(i<patientSerList.size() -1){
                sql.append("cp.patientSer = ? OR ");
            }else{
                sql.append("cp.patientSer = ?");
            }
        }
        sql.append(")");
        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for(int i=0;i<paramList.size();i++){
            ps.setString(i+1,paramList.get(i));
        }
        ResultSet rs = ps.executeQuery();
        ConfirmPayment confirmPayment;
        while(rs.next()){
            confirmPayment = new ConfirmPayment();
            confirmPayment.setHisId(rs.getString("hisId"));
            confirmPayment.setEncounterId(rs.getString("encounterId"));
            confirmPayment.setPatientSer(rs.getLong("patientSer"));
            confirmPayment.setConfirmStatusList(new ArrayList<>());
            confirmPayment.getConfirmStatusList().add(new ConfirmStatus(){{
                setStatus(rs.getInt("status"));
                setActivityCode(rs.getString("activityCode"));
                setCarePathInstanceId(rs.getLong("carePathInstanceId"));
            }});
            resultList.add(confirmPayment);
        }
        return resultList;
    }

    /**
     *查 询根据activityCode查询是否确费 -治疗
     * @param connection
     * @param patientSerList
     * @param activityCode
     * @return
     * @throws SQLException
     */
    public List<ConfirmPayment> queryTreatmentConfirmStatusByPatientSerList(Connection connection, List<String> patientSerList, String activityCode) throws SQLException {
        List<ConfirmPayment> resultList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT cp.hisId,cp.encounterId,cp.patientSer,cs.activityCode,cs.confirmPaymentCount,cs.totalPaymentCount FROM ConfirmPayment cp,TreatmentConfirmStatus cs,Encounter e WHERE cp.encounterId=e.id AND e.status = ? AND cp.id=cs.confirmPaymentId AND cs.activityCode=? AND (");
        List<String> paramList = new ArrayList<>();
        paramList.add(StatusEnum.IN_PROGRESS.name());
        paramList.add(activityCode);
        paramList.addAll(patientSerList);
        for(int i=0;i<patientSerList.size();i++){
            if(i<patientSerList.size() -1){
                sql.append("cp.patientSer = ? OR ");
            }else{
                sql.append("cp.patientSer = ?");
            }
        }
        sql.append(")");
        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for(int i=0;i<paramList.size();i++){
            ps.setString(i+1,paramList.get(i));
        }
        ResultSet rs = ps.executeQuery();
        ConfirmPayment confirmPayment;
        while(rs.next()){
            confirmPayment = new ConfirmPayment();
            confirmPayment.setHisId(rs.getString("hisId"));
            confirmPayment.setEncounterId(rs.getString("encounterId"));
            confirmPayment.setPatientSer(rs.getLong("patientSer"));
            confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus(){{
                setActivityCode(rs.getString("activityCode"));
                setConfirmPaymentCount(rs.getInt("confirmPaymentCount"));
                setTotalPaymentCount(rs.getInt("totalPaymentCount"));
            }});
            resultList.add(confirmPayment);
        }
        return resultList;
    }


    public Map<String,Integer> queryTreatmentNumForPatientSerList(Connection connection, List<String> patientSerList, String deviceId, Date startTimeBegin, Date startTimeEnd) throws SQLException {
        Map<String,Integer> map = new HashMap<>();
        StringBuffer sb = new StringBuffer("SELECT ta.patientSer,count(*) AS treatmentNum FROM TreatmentAppointment ta,Encounter e WHERE ta.encounterId = e.id AND e.status = ? AND ta.deviceId = ? AND ta.startTime >=? AND ta.endTime <? AND  (");
        List<Object> param = new ArrayList<>();
        param.add(StatusEnum.IN_PROGRESS.name());
        param.add(deviceId);
        param.add(new java.sql.Date(startTimeBegin.getTime()));
        param.add(new java.sql.Date(startTimeEnd.getTime()));
        param.addAll(patientSerList);
        for(int i=0;i<patientSerList.size();i++){
            if(i < patientSerList.size()-1){
                sb.append("ta.patientSer = ? OR ");
            }else{
                sb.append("ta.patientSer = ? ");
            }
        }
        sb.append(") GROUP BY ta.patientSer");
        PreparedStatement ps = connection.prepareStatement(sb.toString());
        for(int i = 0;i<param.size();i++){
            ps.setObject(i+1,param.get(i));
        }
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            map.put(rs.getString("patientSer"),rs.getInt("treatmentNum"));
        }
        return map;
    }
}
