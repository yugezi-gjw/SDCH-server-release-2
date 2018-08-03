package com.varian.oiscn.base.systemconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.user.ViewConfig;
import com.varian.oiscn.util.DatabaseUtil;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by cxq8822 on Sep. 20, 2017
 * <p>
 * This class retrieves some configuration data such as insurance type from the database.
 */
@Slf4j
public class SystemConfigServiceImp {

    private static final String SQL_QUERY_RECUR_APPOINTMENT_TIME_LIMIT = "SELECT value FROM SystemConfig WHERE name= '"
            + SystemConfigConstant.RECURRING_APPOINTMENT_TIME_LIMIT + "' ORDER BY orderby ASC";
    private static final String SELECT_ALL = "SELECT name, value FROM SystemConfig GROUP BY name,value,orderby order by orderby";

    public List<String> queryConfigValueByName(String name) {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet rs = null;
        List<String> result = new ArrayList<>();
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("select value from SystemConfig where name=? order by orderby");
            preparedStatement.setString(1, name);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseAll(connection, preparedStatement, rs);
        }
        return result;
    }

    public Map<String, List<String>> queryAllConfigValues() {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet rs = null;
        Map<String, List<String>> configs = new HashMap<>();
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_ALL);
            rs = preparedStatement.executeQuery();
            String name;
            String value;
            while (rs.next()) {
                name = rs.getString("name");
                if (!configs.containsKey(name)) {
                    configs.put(name, new ArrayList<>());
                }
                value = rs.getString("value");
                configs.get(name).add(value);
            }
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseAll(connection, preparedStatement, rs);
        }
        return configs;
    }

    /**
     * Query default view config of specified group list.<br>
     *
     * @param groupList Group list
     * @return the default view of group
     */
    public Map<String, String> queryGroupDefaultView(List<GroupDto> groupList) {
        log.debug("queryGroupDefaultView groupList:[{}]", groupList);
        Map<String, String> viewConfig = new HashMap<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
//        String defaultView = null;
//        String defaultConfig = null;
        String patientBanner = null;
        if (groupList != null && groupList.size() > 0) {
            try {
                con = ConnectionPool.getConnection();
                StringBuilder sql = new StringBuilder();
                int size = groupList.size();

                sql.append("SELECT category, value FROM SystemConfig WHERE (name= ? ");
                for (int i = 1; i < size; i++) {
                    sql.append(" OR name = ? ");
                }
                sql.append(") ORDER BY category ASC, orderby ASC ");

                ps = con.prepareStatement(sql.toString());
                for (int i = 0; i < size; i++) {
                    ps.setString(i + 1, groupList.get(i).getGroupName());
                }
                rs = ps.executeQuery();
                String category;
//                while ((defaultView == null || defaultConfig == null || patientBanner == null) && rs.next()) {
                while (patientBanner == null && rs.next()) {
                    category = rs.getString("category");
                    switch (category) {
//                        case ViewConfig.CATEGORY_VIEW_ID:
//                            defaultView = rs.getString("value");
//                            break;
//                        case ViewConfig.CATEGORY_TAB_ID:
//                            defaultConfig = rs.getString("value");
//                            break;
                        case ViewConfig.CATEGORY_PATIENT_BANNER_VIEW_ID:
                            patientBanner = rs.getString("value");
                            break;
                        default:
                    }
                }
            } catch (SQLException e) {
                log.error("queryGroupDefaultView SQLException SQLState=[{}]", e.getSQLState());
            } finally {
                DatabaseUtil.safeCloseAll(con, ps, rs);
            }
        }

//        viewConfig.put(ViewConfig.CATEGORY_VIEW_ID, defaultView != null ? defaultView : ViewConfig.DEFAULT_VIEW_ID);
//        viewConfig.put(ViewConfig.CATEGORY_TAB_ID, defaultConfig != null ? defaultConfig : ViewConfig.DEFAULT_CONFIG);
        viewConfig.put(ViewConfig.CATEGORY_PATIENT_BANNER_VIEW_ID, patientBanner != null ? patientBanner : ViewConfig.CATEGORY_PATIENT_BANNER_TEMPLATE_ID);
        return viewConfig;
    }

    public String queryRecurringAppointmentTimeLimit() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String timeLimit = SystemConfigConstant.RECURRING_APPOINTMENT_TIME_LIMIT_DEFAULT_VALUE;
        try {
            con = ConnectionPool.getConnection();
            ps = con.prepareStatement(SQL_QUERY_RECUR_APPOINTMENT_TIME_LIMIT);
            rs = ps.executeQuery();
            if (rs.next()) {
                timeLimit = rs.getString("value");
            }
        } catch (SQLException e) {
            log.error("queryRecurringAppointmentTimeLimit SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseAll(con, ps, rs);
        }
        return timeLimit;
    }

    public Map<String, String> getDefaultConf() {
        return queryConf("default");
    }


    public Map<String, String> getFHIRServerConf() {
        return queryConf("FHIRServer");
    }

    public Map<String, String> getHttpClientConf() {
        return queryConf("HttpClient");
    }

    public Map<String, String> getLocaleConf() {
        return queryConf("locale");
    }

    public Map<String, String> getAuditLog() {
        return queryConf(SystemConfigConstant.AUDIT_LOG_CATEGORY);
    }

    private Map<String, String> queryConf(String category) {
        Map<String, String> retMap = new HashMap<>();
        try {
            @Cleanup
            Connection con = ConnectionPool.getConnection();

            @Cleanup
            PreparedStatement ps = con.prepareStatement("SELECT name, value FROM SystemConfig WHERE category = ? ORDER BY orderBy ");
            ps.setString(1, category);

            @Cleanup
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                retMap.put(rs.getString("name"), rs.getString("value"));
            }
        } catch (SQLException e) {
            log.error("queryConf SQLException SQLState=[{}]", e.getSQLState());
        }

        return retMap;
    }
}
