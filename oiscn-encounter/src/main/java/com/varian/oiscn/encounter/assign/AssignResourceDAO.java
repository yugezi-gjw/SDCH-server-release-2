package com.varian.oiscn.encounter.assign;

import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.assign.AssignResource;
import com.varian.oiscn.core.assign.AssignResourceVO;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by bhp9696 on 2018/3/23.
 */
public class AssignResourceDAO extends AbstractDAO<AssignResource> {

    private UserContext userContext;

    public AssignResourceDAO(UserContext userContext) {
        super(userContext);
        this.userContext = userContext;
    }

    @Override
    protected String getTableName() {
        return "AssignResource";
    }

    @Override
    protected String getJsonbColumnName() {
        return null;
    }


    public int createResource(Connection conn, AssignResource assignResource) throws SQLException {
        int[] items = batchCreate(conn, Arrays.asList(assignResource));
        if (items.length > 0) {
            return items[0];
        }
        return 0;
    }


    public int[] batchCreate(Connection conn, List<AssignResource> assignResourceList) throws SQLException {
        String sql = "INSERT INTO " + getTableName() 
                + " (resourceId, patientSer, encounterId, activityCode, createdUser, createdDate) "
                + " VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        int[] affectedRows = new int[]{};
        try {
            ps = conn.prepareStatement(sql);
            if(assignResourceList != null && !assignResourceList.isEmpty()) {
                Timestamp now = new Timestamp(new Date().getTime());
                String user = userContext.getLogin().getName();
                for(AssignResource assignResource : assignResourceList) {
                    int idx = 0;
                    ps.setString(++idx, assignResource.getResourceId());
                    ps.setLong(++idx, assignResource.getPatientSer());
                    ps.setLong(++idx, assignResource.getEncounterId());
                    ps.setString(++idx, assignResource.getActivityCode());
                    ps.setString(++idx, user);
                    ps.setTimestamp(++idx, now);
                    ps.addBatch();
                }
                affectedRows = ps.executeBatch();
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, null);
        }
        return affectedRows;
    }

    /**
     * Remove released Resource by activityCode.<br>
     * @param conn
     * @param patientSer
     * @param activityCode
     * @return
     * @throws SQLException
     */
    public boolean delete(Connection conn, Long patientSer, String activityCode) throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(this.getTableName()).append(" WHERE patientSer = ?");
        List<String> activityList = null;
        if (StringUtils.isNotBlank(activityCode)) {
            activityList = Arrays.asList(activityCode.split(","));
            if (activityList != null && activityList.size() > 0) {
                sql.append(" AND activityCode IN ( ? ");// 0
                for (int i = 1; i < activityList.size(); i++) {
                    sql.append(" , ?");
                }
                sql.append(" ) ");
            }
        }
        PreparedStatement ps = null;
        int affectedRow = 0;
        try {
            ps = conn.prepareStatement(sql.toString());
            ps.setLong(1, patientSer);
            if (StringUtils.isNotBlank(activityCode)) {
                if (activityList != null && activityList.size() > 0) {
                    ps.setString(2, activityList.get(0));
                    for (int i = 1; i < activityList.size(); i++) {
                        ps.setString(i + 2, activityList.get(i));
                    }
                }
            }
            affectedRow = ps.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, null);
        }
        return affectedRow > 0;
    }
    
    public List<AssignResourceVO> listAssignResourceSummary(Connection con, String activityCode) throws SQLException {
        List<AssignResourceVO> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = " SELECT ar.resourceId AS RESOURCE_ID, COUNT(ar.patientSer) AS RESOURCE_CNT "
                + "  FROM AssignResource ar "
                + " INNER JOIN Encounter en "
                + "    ON en.status= ? "
                + "   AND ar.encounterId = en.id "
                + " WHERE ar.activityCode = ? GROUP BY ar.resourceId ";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, StatusEnum.IN_PROGRESS.name());
            ps.setString(2, activityCode);
            rs = ps.executeQuery();
            AssignResourceVO vo;
            while (rs.next()) {
                vo = new AssignResourceVO();
                vo.setId(rs.getString("RESOURCE_ID"));
                vo.setCode(rs.getString("RESOURCE_ID"));
                vo.setName(rs.getString("RESOURCE_ID"));
                vo.setAmount(rs.getInt("RESOURCE_CNT"));
                result.add(vo);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return result;
    }

    public List<DeviceDto> getAllResourceConfig(Connection con, String activityCode) throws SQLException {
        List<DeviceDto> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = " SELECT resourceId,resourceCode, resourceName, color, activityCode "
                + "  FROM AssignResourceConfig config WHERE activityCode = ?  ORDER BY orderNo ";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, activityCode);
            rs = ps.executeQuery();
            while (rs.next()) {
                DeviceDto dto = new DeviceDto();
                dto.setId(rs.getString("resourceId"));
                dto.setCode(rs.getString("resourceCode"));
                dto.setName(rs.getString("resourceName"));
                dto.setColor(rs.getString("color"));
                dto.setUsage(rs.getString("activityCode"));
                result.add(dto);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return result;
    }

    public Map<Long, String> getPatientSerResourceMapByPatientSerList(Connection con, List<Long> patientSerList, String physicistGroupingActivityCode) throws SQLException{
        Map<Long, String> resultMap = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT patientSer, resourceId FROM AssignResource WHERE activityCode = '" + physicistGroupingActivityCode + "'");
        if(!patientSerList.isEmpty()){
            stringBuilder.append("and (");
            int size = patientSerList.size();
            for(int i = 0; i < size; i++){
                stringBuilder.append("patientSer = ?");
                if(i != size - 1){
                    stringBuilder.append(" or ");
                }
            }
            stringBuilder.append(")");
        }
        try{
            ps = con.prepareStatement(stringBuilder.toString());
            for(int i = 0; i < patientSerList.size(); i++){
                ps.setLong(i + 1, patientSerList.get(i));
            }
            rs = ps.executeQuery();
            while(rs.next()){
                resultMap.put(rs.getLong(1), rs.getString(2));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return resultMap;
    }
}
