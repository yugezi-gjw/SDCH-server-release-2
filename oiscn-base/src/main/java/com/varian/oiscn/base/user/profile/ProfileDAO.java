package com.varian.oiscn.base.user.profile;

import com.varian.oiscn.util.DatabaseUtil;
import lombok.Cleanup;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Profile Table DAO.<br>
 */
public class ProfileDAO {

    /**
     * Query User specified Property.<br>
     * @param con Connection
     * @param userId User Id
     * @param property Property Name
     * @return Property Value
     * @throws SQLException
     */
    public String queryUserProperty(Connection con, String userId, String property) throws SQLException {
    	return queryProperty(con, userId, UserRoleEnum.LOGIN_USER, property);
    }
    
    /**
     * Query User all Properties.<br>
     * @param con Connection
     * @param userId User Id
     * @return All Property Values
     * @throws SQLException
     */
    public Map<String, String>  queryUserAllProperties(Connection con, String userId) throws SQLException {
    	return queryAllProperties(con, userId, UserRoleEnum.LOGIN_USER);
    }
    
    /**
     * Update User Property.<br>
     * @param con Connection
     * @param userId User Id
     * @param property Property Entity
     * @return The Number of affected row
     * @throws SQLException
     */
    public int updateUserProperty(Connection con, String userId, String property,String value) throws SQLException {
    	return updateProperty(con, userId, UserRoleEnum.LOGIN_USER, property,value);
    }
    
    /**
     * Update Patient Property.<br>
     * @param con Connection
     * @param userId User Id
     * @param property Property Entity
     * @return The Number of affected row
     * @throws SQLException
     */
    public int updatePatientProperty(Connection con, String userId, String property,String value) throws SQLException {
    	return updateProperty(con, userId, UserRoleEnum.PATIENT, property,value);
    }
    
    /**
     * Query Patient specified Property.<br>
     * @param con Connection
     * @param patientId Patient Id
     * @param property Property Name
     * @return Property Value
     * @throws SQLException
     */
    public String queryPatientProperty(Connection con, String patientId, String property) throws SQLException {
    	return queryProperty(con, patientId, UserRoleEnum.PATIENT, property);
    }
    
    /**
     * Query Patient all Properties.<br>
     * @param con Connection
     * @param patientId Patient Id
     * @return All Property Values
     * @throws SQLException
     */
    public Map<String, String>  queryPatientAllProperties(Connection con, String patientId) throws SQLException {
    	return queryAllProperties(con, patientId, UserRoleEnum.PATIENT);
    }

    protected String queryProperty(Connection con, String userId, UserRoleEnum userRole, String property)
            throws SQLException {
    	String propertyValue = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final String sql = "SELECT propertyName, propertyValue "
            		+ " FROM UserProfile"
            		+ " WHERE userId = ? "
            		+ "   AND userRole = ? "
            		+ "   AND propertyName = ? ";
            ps = con.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setInt(2, userRole.getValue());
            ps.setString(3, property);
            rs = ps.executeQuery();
            if (rs.next()) {
            	propertyValue = rs.getString("propertyValue");
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return propertyValue;
    }
    
    protected Map<String, String> queryAllProperties(Connection con, String userId, UserRoleEnum userRole)
            throws SQLException {
    	Map<String, String> propertyList = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final String sql = "SELECT propertyName, propertyValue "
            		+ " FROM UserProfile"
            		+ " WHERE userId = ? "
            		+ "   AND userRole = ? "
            		+ " ORDER BY propertyName ASC";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setInt(2, userRole.getValue());
            rs = ps.executeQuery();
            while (rs.next()) {
            	propertyList.put(rs.getString("propertyName"), rs.getString("propertyValue"));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return propertyList;
    }
    
    protected int updateProperty(Connection con, String userId, UserRoleEnum userRole, String property,String value)
            throws SQLException {
        PreparedStatement ps = null;
        PreparedStatement insertPs = null;
        Timestamp tsNow = new Timestamp(System.currentTimeMillis());
        int affectedRow = 0;
        try {
            final String updateSql = "UPDATE UserProfile "
            		+ " SET propertyValue = ? "
            		+ "     , updUser = ?, updDT = ? "
            		+ " WHERE userId = ? "
            		+ "   AND userRole = ? "
            		+ "   AND propertyName = ?";

            ps = con.prepareStatement(updateSql);
            int columnNum = 1;
            ps.setString(columnNum++, value);
            ps.setString(columnNum++, userId);
            ps.setTimestamp(columnNum++, tsNow);
            ps.setString(columnNum++, userId);
            ps.setInt(columnNum++, userRole.getValue());
            ps.setString(columnNum++, property);
            affectedRow = ps.executeUpdate();
            
            if (affectedRow == 0) {
            	// no row yet.
            	final String insertSql = "INSERT UserProfile (userId, userRole, propertyName, propertyValue, crtUser, crtDT, updUser, updDT) "
                		+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
            	insertPs = con.prepareStatement(insertSql);
            	columnNum = 1;
            	insertPs.setString(columnNum++, userId);
            	insertPs.setInt(columnNum++, userRole.getValue());
            	insertPs.setString(columnNum++, property);
            	insertPs.setString(columnNum++, value);
            	insertPs.setString(columnNum++, userId);
				insertPs.setTimestamp(columnNum++, tsNow);
            	insertPs.setString(columnNum++, userId);
            	insertPs.setTimestamp(columnNum++, tsNow);
                affectedRow = insertPs.executeUpdate();
            }
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
            DatabaseUtil.safeCloseStatement(insertPs);
        }
        return affectedRow;
    }

    /**
     *
     * @param conn
     * @param propertyList
     * @param userRoleEnum
     * @return
     * @throws SQLException
     */
    public Map<String,String> queryValuesByProperties(Connection conn, List<String> propertyList,String userId,UserRoleEnum userRoleEnum) throws SQLException {
        Map<String,String> result = new HashMap<>();

        StringBuffer sql  = new StringBuffer("SELECT propertyName, propertyValue FROM UserProfile WHERE 1=1 ");

        List<Object> paramList = new ArrayList<Object>();
        sql.append("AND (");
        for(int i=0;i<propertyList.size();i++){
            if(i == propertyList.size() -1){
                sql.append(" propertyName =? ");
            }else{
                sql.append("propertyName = ? OR ");
            }
            paramList.add(propertyList.get(i));
        }
        sql.append(")").append(" AND userRole = ? AND userId = ? ORDER BY updDT");
        paramList.add(userRoleEnum.getValue());
        paramList.add(userId);
        @Cleanup
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        for(int i=0;i<paramList.size();i++){
            ps.setObject(i+1,paramList.get(i));
        }
        @Cleanup
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            result.put(rs.getString("propertyName"),rs.getString("propertyValue"));
        }
        return result;
    }
}
