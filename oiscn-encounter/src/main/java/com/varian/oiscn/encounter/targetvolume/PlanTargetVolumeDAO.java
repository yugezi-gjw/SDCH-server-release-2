package com.varian.oiscn.encounter.targetvolume;

import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.targetvolume.PlanTargetVolume;
import com.varian.oiscn.core.user.UserContext;
import lombok.Cleanup;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhp9696 on 2018/3/1.
 */
public class PlanTargetVolumeDAO extends AbstractDAO<PlanTargetVolume> {

    public PlanTargetVolumeDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "PlanTargetVolume";
    }

    @Override
    protected String getJsonbColumnName() {
        return null;
    }


    /**
     * 创建患者计划Id和靶区的关联
     * @param conn
     * @param planTargetVolumeList
     * @return
     * @throws SQLException
     */
    public boolean create(Connection conn, List<PlanTargetVolume> planTargetVolumeList) throws SQLException {
        String sql = "INSERT INTO "+this.getTableName()+"(hisId,encounterId,patientSer,planId,targetVolumeName) VALUES(?,?,?,?,?)";
        @Cleanup
        PreparedStatement ps = conn.prepareStatement(sql);
        for(PlanTargetVolume ptv : planTargetVolumeList){
            if(StringUtils.isNotEmpty(ptv.getTargetVolumeName())){
                ps.setString(1,ptv.getHisId());
                ps.setString(2,ptv.getEncounterId());
                ps.setLong(3,ptv.getPatientSer());
                ps.setString(4,ptv.getPlanId());
                ps.setString(5,ptv.getTargetVolumeName());
                ps.addBatch();
            }
        }
        int [] r = ps.executeBatch();
        return r.length >= 0;
    }

    /**
     * 通过HisId查询计划对应的靶区
     * @param conn
     * @param patientSer
     * @return
     * @throws SQLException
     */
    public List<PlanTargetVolume> queryPlanTargetVolumeListByPatientSer(Connection conn,Long patientSer,Long encounterId) throws SQLException {
        List<PlanTargetVolume> planTargetVolumeList = new ArrayList<>();
        String sql = "SELECT  hisId,encounterId,planId,targetVolumeName,ptv.patientSer FROM "+this.getTableName()+" ptv WHERE" +
                " ptv.patientSer= ? AND ptv.encounterId = ? ORDER BY planId";
        @Cleanup
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, patientSer);
        ps.setLong(2,encounterId);
        @Cleanup
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            planTargetVolumeList.add(new PlanTargetVolume(rs.getString("hisId"),rs.getString("encounterId"),rs.getLong("patientSer"),rs.getString("planId"),rs.getString("targetVolumeName")));
        }
        return planTargetVolumeList;
    }

    public int delete(Connection con, Long patientSer,Long encounterId) throws SQLException{
        String sql = "DELETE FROM " + this.getTableName() + " WHERE patientSer = ? AND encounterId = ?";
        @Cleanup
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1,patientSer);
        ps.setLong(2,encounterId);
        return ps.executeUpdate();

    }
}
