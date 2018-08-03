package com.varian.oiscn.encounter.targetvolume;

import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.targetvolume.TargetVolume;
import com.varian.oiscn.core.targetvolume.TargetVolumeItem;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BHP9696 on 2017/7/25.
 */
@Slf4j
public class TargetVolumeDAO extends AbstractDAO<TargetVolume> {

    public TargetVolumeDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "TargetVolume";
    }

    @Override
    protected String getJsonbColumnName() {
        return "";
    }

    public List<TargetVolume> selectTargetVolumeByPatientSer(Connection conn, Long patientSer,Long encounterId) throws SQLException {
        List<TargetVolume> targetVolumeList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select t.id,t.hisId,t.encounterId,t.patientSer,t.name,memo from " + this.getTableName() + " as t where t.patientSer = ? and t.encounterId = ?  order by t.id");
        StringBuilder sqlItem = new StringBuilder("SELECT targetVolumeId,fieldId,fieldValue,rNum,seq FROM TargetVolumeItem WHERE targetVolumeId = ? ORDER BY rNum,seq");
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        PreparedStatement psItem = conn.prepareStatement(sqlItem.toString());
        ResultSet rs = null;
        ResultSet rsItem = null;
        try {
            TargetVolume targetVolume;
            ps.setLong(1, patientSer);
            ps.setLong(2, encounterId);
            rs = ps.executeQuery();
            while (rs.next()) {
                targetVolume = new TargetVolume(rs.getLong("id"),rs.getString("hisId"),rs.getString("encounterId"),rs.getLong("patientSer"),rs.getString("name"),rs.getString("memo"),new ArrayList<>());
                psItem.setLong(1,targetVolume.getId());
                rsItem = psItem.executeQuery();
                while(rsItem.next()){
                    targetVolume.getTargetVolumeItemList().add(new TargetVolumeItem(targetVolume.getId(),rsItem.getString("fieldId"),rsItem.getString("fieldValue"),rsItem.getInt("rNum"),rsItem.getInt("seq")));
                }
                rsItem.close();
                targetVolumeList.add(targetVolume);
            }

        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
            DatabaseUtil.safeCloseAll(null, psItem, rsItem);
        }
        return targetVolumeList;
    }


    public boolean create(Connection conn,List<TargetVolume> targetVolumeList) throws SQLException{
            boolean ok = true;
            StringBuilder targetVolumeInsertSql = new StringBuilder("INSERT INTO TargetVolume (hisId,encounterId,patientSer,name,memo) VALUES (?,?,?,?,?)");
            StringBuilder itemInsertSql = new StringBuilder("INSERT INTO TargetVolumeItem (targetVolumeId,fieldId,fieldValue,rNum,seq) VALUES (?,?,?,?,?)");
            @Cleanup
            PreparedStatement targetVolumePs = conn.prepareStatement(targetVolumeInsertSql.toString(), Statement.RETURN_GENERATED_KEYS);
            @Cleanup
            PreparedStatement itemPs = conn.prepareStatement(itemInsertSql.toString());
            for(TargetVolume targetVolume :targetVolumeList){
                targetVolumePs.setString(1,targetVolume.getHisId());
                targetVolumePs.setString(2,targetVolume.getEncounterId());
                targetVolumePs.setLong(3,targetVolume.getPatientSer());
                targetVolumePs.setString(4,targetVolume.getName());
                targetVolumePs.setString(5,targetVolume.getMemo());
                int effect = targetVolumePs.executeUpdate();
                if(effect == 1){
                    ResultSet rs = targetVolumePs.getGeneratedKeys();
                    if(rs.next()){
                        long targetVolumeId = rs.getLong(1);
                        List<TargetVolumeItem> targetVolumeItemList = targetVolume.getTargetVolumeItemList();
                        if(targetVolumeItemList != null && !targetVolumeItemList.isEmpty()){
                            for(TargetVolumeItem targetVolumeItem:targetVolumeItemList){
                                itemPs.setLong(1,targetVolumeId);
                                itemPs.setString(2,targetVolumeItem.getFieldId());
                                itemPs.setString(3,targetVolumeItem.getFieldValue());
                                itemPs.setInt(4,targetVolumeItem.getRNum());
                                itemPs.setInt(5,targetVolumeItem.getSeq());
                                itemPs.addBatch();
                            }
                            itemPs.executeBatch();
                        }
                    }
                    rs.close();
                }else{
                    ok = false;
                    break;
                }
            }
        return ok;
    }

    /**
     * 查询hisId下面的所有的靶区
     * @param conn
     * @param patientSer
     * @return
     * @throws SQLException
     */
    public List<TargetVolume> selectTargetVolumeExceptItemByPatientSer(Connection conn, Long patientSer,Long encounterId) throws SQLException {
        List<TargetVolume> targetVolumeList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select t.id,t.hisId,t.patientSer,t.encounterId,t.name,memo from " + this.getTableName() + " as t where t.patientSer = ? and t.encounterId = ? order by t.id");
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        ResultSet rs = null;
        try {
            TargetVolume targetVolume;
            ps.setLong(1, patientSer);
            ps.setLong(2, encounterId);
            rs = ps.executeQuery();
            while (rs.next()) {
                targetVolume = new TargetVolume(rs.getLong("id"),rs.getString("hisId"),rs.getString("encounterId"),rs.getLong("patientSer"),rs.getString("name"),rs.getString("memo"),new ArrayList<>());
                targetVolumeList.add(targetVolume);
            }

        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return targetVolumeList;
    }

    /**
     *
     * @param conn
     * @param idList
     * @return
     * @throws SQLException
     */
    public boolean batchDelete(Connection conn,List<Long> idList) throws SQLException {
        boolean ok = false;
        StringBuffer delSubItemsSql = new StringBuffer("DELETE FROM TargetVolumeItem WHERE EXISTS (SELECT 'X' FROM ")
                .append(this.getTableName()).append(" tv WHERE tv.id=TargetVolumeItem.targetVolumeId AND (");
        StringBuffer delSql = new StringBuffer("DELETE FROM ").append(this.getTableName()).append(" WHERE (");

        for(int i=0;i<idList.size();i++){
            if(i<idList.size()-1){
                delSubItemsSql.append("tv.id=? OR ");
                delSql.append("id=? OR ");
            }else{
                delSubItemsSql.append("tv.id=?").append(") )");
                delSql.append("id=?").append(")");
            }
        }
        @Cleanup
        PreparedStatement delSubItemPs = conn.prepareStatement(delSubItemsSql.toString());
        for(int i=0;i<idList.size();i++){
            delSubItemPs.setLong(i+1,idList.get(i));
        }
        if(delSubItemPs.executeUpdate() > 0) {
            @Cleanup
            PreparedStatement delPs = conn.prepareStatement(delSql.toString());
            for (int i = 0; i < idList.size(); i++) {
                delPs.setLong(i + 1, idList.get(i));
            }
            if(delPs.executeUpdate()>0){
                 ok = true;
            }
        }
        return ok;
    }

}
