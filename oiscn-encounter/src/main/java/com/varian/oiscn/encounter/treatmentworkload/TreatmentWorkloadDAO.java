package com.varian.oiscn.encounter.treatmentworkload;

import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BHP9696 on 2017/8/17.
 */
public class TreatmentWorkloadDAO extends AbstractDAO<TreatmentWorkload> {

    public TreatmentWorkloadDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "TreatmentWorkload";
    }

    @Override
    protected String getJsonbColumnName() {
        return "";
    }


    @Override
    public String create(Connection con, TreatmentWorkload treatmentWorkload) throws SQLException {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String createId;
        try {
            Timestamp curTime = new Timestamp(new java.util.Date().getTime());
            StringBuilder createSql = new StringBuilder("INSERT INTO ");
            createSql.append(getTableName()).append("(createdUser,createdDate,lastUpdatedUser,lastUpdatedDate,");
            createSql.append("hisId,encounterId,patientSer,treatmentDate ) VALUES(?,?,?,?,?,?,?,?)");
            ps = con.prepareStatement(createSql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, userContext.getName());
            ps.setTimestamp(2, curTime);
            ps.setString(3, userContext.getName());
            ps.setTimestamp(4, curTime);
            ps.setString(5,treatmentWorkload.getHisId());
            ps.setLong(6,Long.parseLong(treatmentWorkload.getEncounterId()));
            ps.setLong(7,treatmentWorkload.getPatientSer());
            ps.setTimestamp(8,new Timestamp(treatmentWorkload.getTreatmentDate().getTime()));
            ps.executeUpdate();
            resultSet = ps.getGeneratedKeys();
            resultSet.next();
            createId = resultSet.getString(1);
//
            if(treatmentWorkload.getWorkloadPlans() != null ){
                treatmentWorkload.getWorkloadPlans().forEach(workloadPlan -> {
                    workloadPlan.setWorkloadId(Long.parseLong(createId));
                });
                batchCreateWorkloadPlan(con,treatmentWorkload.getWorkloadPlans());
            }
            if(treatmentWorkload.getWorkloadSignatures() != null){
                treatmentWorkload.getWorkloadSignatures().forEach(workloadSignature -> {
                    workloadSignature.setWorkloadId(Long.parseLong(createId));
                });
                batchCreateWorkloadSignature(con,treatmentWorkload.getWorkloadSignatures());
            }
            if(treatmentWorkload.getWorkloadWorkers() != null){
                treatmentWorkload.getWorkloadWorkers().forEach(workloadWorker -> {
                    workloadWorker.setWorkloadId(Long.parseLong(createId));
                });
                batchCreateWorkloadWorker(con,treatmentWorkload.getWorkloadWorkers());
            }

        } finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
        return createId;
    }

    private int batchCreateWorkloadPlan(Connection con, List<WorkloadPlan> workloadPlanList) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO TreatmentWorkloadPlan (workloadId,planId,deliveredFractions,selected,comment) VALUES (?,?,?,?,?)");
        for(WorkloadPlan workloadPlan :workloadPlanList){
            ps.setLong(1,workloadPlan.getWorkloadId());
            ps.setString(2,workloadPlan.getPlanId());
            ps.setInt(3,workloadPlan.getDeliveredFractions());
            ps.setInt(4,workloadPlan.getSelected());
            ps.setString(5,workloadPlan.getComment());
            ps.addBatch();
        }
        if(!workloadPlanList.isEmpty()){
            return  ps.executeBatch().length;
        }
        return 0;
    }

    private int batchCreateWorkloadSignature(Connection con,List<WorkloadSignature> workloadSignatureList) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO TreatmentWorkloadSignature (workloadId,userName,resourceName,resourceSer,signDate,signType) VALUES (?,?,?,?,?,?)");
        for(WorkloadSignature workloadSignature :workloadSignatureList){
            ps.setLong(1,workloadSignature.getWorkloadId());
            ps.setString(2,workloadSignature.getUserName());
            ps.setString(3,workloadSignature.getResourceName());
            ps.setObject(4,workloadSignature.getResourceSer());
            ps.setTimestamp(5,new Timestamp(workloadSignature.getSignDate().getTime()));
            ps.setString(6,workloadSignature.getSignType().name());
            ps.addBatch();
        }
        if(!workloadSignatureList.isEmpty()){
            return  ps.executeBatch().length;
        }
        return 0;
    }

    private int batchCreateWorkloadWorker(Connection con,List<WorkloadWorker> workloadWorkerList) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO TreatmentWorkloadWorker (workloadId,workerName,orderNum) VALUES (?,?,?)");
        for(WorkloadWorker workloadWorker:workloadWorkerList){
            ps.setLong(1,workloadWorker.getWorkloadId());
            ps.setString(2,workloadWorker.getWorkerName());
            ps.setInt(3,workloadWorker.getOrderNum());
            ps.addBatch();
        }
        if(!workloadWorkerList.isEmpty()){
            return ps.executeBatch().length;
        }
        return 0;
    }

    public List<WorkloadPlan> selectLatestWorkloadPlanByPatientSer(Connection conn, Long patientSer,Long encounterId) throws SQLException {
        List<WorkloadPlan> workloadPlanList = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement("SELECT p.workloadId,p.planId,p.deliveredFractions,p.selected,p.comment FROM "+getTableName()+ " a,TreatmentWorkloadPlan p where a.patientSer=? and a.encounterId=? and a.id=p.workloadId and  a.treatmentDate=(select max(b.treatmentDate) treatmentDate  from "+getTableName()+" b where a.patientSer=b.patientSer and a.encounterId=b.encounterId)");
        ps.setLong(1,patientSer);
        ps.setLong(2,encounterId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            workloadPlanList.add(rs2WorkloadPlan(rs));
        }
        return workloadPlanList;
    }

    public List<TreatmentWorkload> queryTreatmentWorkloadListByPatientSer(Connection conn, Long patientSer,Long encounterId) throws SQLException {
        List<TreatmentWorkload> list = new ArrayList<>();
        TreatmentWorkload treatmentWorkload;
        String sql = "SELECT "
                + "    t.id, "
                + "    t.hisId, "
                + "    t.patientSer, "
                + "    t.encounterId, "
                + "    t.treatmentDate "
                + "  FROM TreatmentWorkload AS t "
                + "  WHERE t.patientSer= ? "
                + "    AND t.encounterId = ? "
                + "  ORDER BY t.treatmentDate ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, patientSer);
            ps.setLong(2, encounterId);
            rs = ps.executeQuery();
            while(rs.next()){
                treatmentWorkload = rs2TreatmentWorkload(rs);
                buildTreatmentWorkload(treatmentWorkload, conn);
                list.add(treatmentWorkload);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return list;
    }

    public Map<String,Integer> queryTotalTreatmentCount(Connection connection, List<String> patientSerList) throws SQLException {
        Map<String,Integer> map = new HashMap<>();
        List<String> paramList = new ArrayList<>();
        paramList.add(StatusEnum.IN_PROGRESS.name());
        paramList.addAll(patientSerList);
        StringBuilder sql = new StringBuilder("SELECT tw.patientSer AS patientSer, COUNT(*) AS treatmentedCount FROM TreatmentWorkload tw,Encounter e WHERE tw.encounterId=e.id AND e.status=? AND ( ");
        for(int i=0;i<patientSerList.size();i++){
            if(i < patientSerList.size() -1){
                sql.append("tw.patientSer = ? OR ");
            }else{
                sql.append("tw.patientSer= ?");
            }
        }
        sql.append(")");
        sql.append(" GROUP BY tw.patientSer ORDER BY tw.patientSer DESC ");
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql.toString());
            for(int i=0;i<paramList.size();i++){
                ps.setString(i+1,paramList.get(i));
            }
            rs = ps.executeQuery();
            while(rs.next()){
                map.put(rs.getString("patientSer"),rs.getInt("treatmentedCount"));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        
        return map;
    }


    private void buildTreatmentWorkload(TreatmentWorkload treatmentWorkload, Connection conn) throws SQLException {
        PreparedStatement ps;
        ResultSet rs;
//                workload plan
        ps = conn.prepareStatement("SELECT workloadId,planId,selected,deliveredFractions,comment FROM TreatmentWorkloadPlan WHERE workloadId = ?");
        ps.setLong(1,Long.parseLong(treatmentWorkload.getId()));
        rs = ps.executeQuery();
        List<WorkloadPlan> workloadPlanList = new ArrayList<>();
        while(rs.next()){
            workloadPlanList.add(rs2WorkloadPlan(rs));
        }
        treatmentWorkload.setWorkloadPlans(workloadPlanList);
        ps.close();

//              workload signature
        ps = conn.prepareStatement("SELECT workloadId,userName,resourceName,resourceSer,signDate,signType FROM TreatmentWorkloadSignature WHERE workloadId = ?");
        ps.setLong(1,Long.parseLong(treatmentWorkload.getId()));
        rs = ps.executeQuery();
        List<WorkloadSignature> workloadSignatureList = new ArrayList<>();
        while(rs.next()){
            workloadSignatureList.add(rs2WorkloadSignature(rs));
        }
        treatmentWorkload.setWorkloadSignatures(workloadSignatureList);
        ps.close();

//               workload worker
        ps = conn.prepareStatement("SELECT workloadId,workerName,orderNum FROM TreatmentWorkloadWorker WHERE workloadId = ? ORDER BY orderNum");
        ps.setLong(1,Long.parseLong(treatmentWorkload.getId()));
        rs = ps.executeQuery();
        List<WorkloadWorker> workloadWorkerList = new ArrayList<>();
        while(rs.next()){
            workloadWorkerList.add(rs2WorkloadWorker(rs));
        }
        treatmentWorkload.setWorkloadWorkers(workloadWorkerList);
    }

    private TreatmentWorkload rs2TreatmentWorkload(ResultSet rs) throws SQLException {
        TreatmentWorkload treatmentWorkload = new TreatmentWorkload();
        treatmentWorkload.setId(rs.getString("id"));
        treatmentWorkload.setHisId(rs.getString("hisId"));
        treatmentWorkload.setPatientSer(rs.getLong("patientSer"));
        treatmentWorkload.setEncounterId(rs.getString("encounterId"));
        treatmentWorkload.setTreatmentDate(new Date(rs.getTimestamp("treatmentDate").getTime()));
        return treatmentWorkload;
    }

    private WorkloadPlan rs2WorkloadPlan(ResultSet rs) throws SQLException {
        WorkloadPlan workloadPlan = new WorkloadPlan();
        workloadPlan.setWorkloadId(rs.getLong("workloadId"));
        workloadPlan.setPlanId(rs.getString("planId"));
        workloadPlan.setSelected(rs.getByte("selected"));
        workloadPlan.setComment(rs.getString("comment"));
        workloadPlan.setDeliveredFractions(rs.getInt("deliveredFractions"));
        return workloadPlan;
    }

    private WorkloadSignature rs2WorkloadSignature(ResultSet rs) throws SQLException {
        WorkloadSignature workloadSignature = new WorkloadSignature();
        workloadSignature.setWorkloadId(rs.getLong("workloadId"));
        workloadSignature.setUserName(rs.getString("userName"));
        workloadSignature.setResourceName(rs.getString("resourceName"));
        workloadSignature.setResourceSer(rs.getLong("resourceSer"));
        workloadSignature.setSignDate(new Date(rs.getTimestamp("signDate").getTime()));
        workloadSignature.setSignType(WorkloadSignature.SignatureTypeEnum.valueOf(rs.getString("signType")));
        return workloadSignature;
    }

    private WorkloadWorker rs2WorkloadWorker(ResultSet rs) throws SQLException {
        WorkloadWorker workloadWorker = new WorkloadWorker();
        workloadWorker.setWorkloadId(rs.getLong("workloadId"));
        workloadWorker.setWorkerName(rs.getString("workerName"));
        workloadWorker.setOrderNum(rs.getInt("orderNum"));
        return workloadWorker;
    }
}
