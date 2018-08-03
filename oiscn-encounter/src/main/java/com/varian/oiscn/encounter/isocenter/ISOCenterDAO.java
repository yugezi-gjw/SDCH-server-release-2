package com.varian.oiscn.encounter.isocenter;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;

import java.sql.*;
import java.util.List;

/**
 * Created by BHP9696 on 2017/7/25.
 */
public class ISOCenterDAO extends AbstractDAO<ISOCenter> {

    public ISOCenterDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "ISOCenter";
    }

    @Override
    protected String getJsonbColumnName() {
        return "isoCenterInfo";
    }

    public ISOCenter selectISOCenterByPatientSer(Connection conn, Long patientSer,Long encounterId) throws SQLException {
        ISOCenter isoCenter = null;
        StringBuilder sb = new StringBuilder();
        sb.append("select i.id,i.").append(this.getJsonbColumnName()).append(" ").append("from ").append(this.getTableName()).append(" as i ").append("where i.patientSer=? ").append("and i.encounterId = ?");
        PreparedStatement ps = conn.prepareStatement(sb.toString());
        ResultSet rs = null;
        try {
            ps.setLong(1, patientSer);
            ps.setLong(2, encounterId);
            rs = ps.executeQuery();
            if (rs.next()) {
                String json = rs.getString(this.getJsonbColumnName());
                JsonSerializer<ISOCenter> jsonSerializer = new JsonSerializer<>();
                isoCenter = jsonSerializer.getObject(json, ISOCenter.class);
                isoCenter.setId(rs.getString("id"));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return isoCenter;
    }

    @Override
    public String create(Connection con, ISOCenter isoCenter) throws SQLException {
        String createId = null;
        Timestamp curTime = new Timestamp(new java.util.Date().getTime());
        String json = new JsonSerializer<ISOCenter>().getJson(isoCenter);
        StringBuilder createSql = new StringBuilder("INSERT INTO ");
        createSql.append(getTableName()).append("(createdUser,createdDate,lastUpdatedUser,lastUpdatedDate,");
        createSql.append(getJsonbColumnName()).append(",hisId,encounterId,patientSer ) VALUES(?,?,?,?,?,?,?,?)");
        PreparedStatement ps = con.prepareStatement(createSql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, userContext.getName());
        ps.setTimestamp(2, curTime);
        ps.setString(3, userContext.getName());
        ps.setTimestamp(4, curTime);
        ps.setString(5, json);
        ps.setString(6, isoCenter.getHisId());
        ps.setLong(7, Long.parseLong(isoCenter.getEncounterId()));
        ps.setLong(8,isoCenter.getPatientSer());
        ps.executeUpdate();

        ResultSet resultSet = ps.getGeneratedKeys();
        if (resultSet.next()) {
            createId = resultSet.getString(1);
            isoCenter.setId(createId);
            batchCreateISOCenterItem(con, isoCenter);
        }
        return createId;
    }

    private int batchCreateISOCenterItem(Connection con, ISOCenter isoCenter) throws SQLException {
        if (isoCenter.getPlanList() != null && !isoCenter.getPlanList().isEmpty()) {
            String sql = "INSERT INTO isocenteritem (isoCenterId,planId,isoName,vrt,lng,lat) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            List<ISOPlanTretment> list = isoCenter.getPlanList();
            int batchSize = 0;
            for (ISOPlanTretment trement : list) {
                if (trement.getSiteList() != null && !trement.getSiteList().isEmpty()) {
                    List<ISOCenterVO> items = trement.getSiteList();
                    for (ISOCenterVO item : items) {
                        int idx = 1;
                        ps.setLong(idx++, Long.parseLong(isoCenter.getEncounterId()));
                        ps.setString(idx++, trement.getPlanId());
                        ps.setString(idx++, item.getIsoName());
                        ps.setObject(idx++, item.getVrt());
                        ps.setObject(idx++, item.getLng());
                        ps.setObject(idx++, item.getLat());
                        ps.addBatch();
                        batchSize++;
                    }
                }
            }
            if (batchSize > 0) {
                return ps.executeBatch().length;
            }
        }
        return 0;
    }


    @Override
    public boolean update(Connection connection, ISOCenter isoCenter, String id) throws SQLException {
        PreparedStatement preparedStatement;
        String updateJson = new JsonSerializer<ISOCenter>().getJson(isoCenter);
        StringBuilder updateSql = new StringBuilder("UPDATE ");
        updateSql.append(getTableName());
        updateSql.append(" SET lastUpdatedUser=?,lastUpdatedDate=?,");
        updateSql.append(getJsonbColumnName()).append("=? WHERE id=?");
        preparedStatement = connection.prepareStatement(updateSql.toString());
        preparedStatement.setString(1, userContext.getLogin().getUsername());
        preparedStatement.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
        preparedStatement.setString(3, updateJson);
        preparedStatement.setLong(4, Long.parseLong(id));
        int effect = preparedStatement.executeUpdate();
        if (effect > 0) {
            if (deleteISOCenterItemByISOCenterId(connection, Long.parseLong(id)) > 0) {
                isoCenter.setId(id);
                batchCreateISOCenterItem(connection, isoCenter);
            }
        }
        return true;
    }

    private int deleteISOCenterItemByISOCenterId(Connection con, Long isoCenterId) throws SQLException {
        PreparedStatement ps = con.prepareStatement("DELETE FROM ISOCenterItem WHERE isoCenterId = ?");
        ps.setLong(1, isoCenterId);
        return ps.executeUpdate();
    }
}
