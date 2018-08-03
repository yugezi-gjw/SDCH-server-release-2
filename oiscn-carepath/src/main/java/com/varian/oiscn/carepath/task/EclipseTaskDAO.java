package com.varian.oiscn.carepath.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.StringUtils;
import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Eclipse Pending Task table DAO.<br>
 */
public class EclipseTaskDAO extends AbstractDAO<EclipseTask> {

    public EclipseTaskDAO(UserContext userContext) {
        super(null);
    }
    
    public String create(Connection con, EclipseTask task, String userName) throws SQLException {
        return create(con, task, Status.Pending, userName);
    }
    
    public List<EclipseTask> listPending(Connection con) throws SQLException {
        return list(con, Status.Pending);
    }

    public List<EclipseTask> listDoneByQin(Connection con) throws SQLException {
        return list(con, Status.DoneByQin);
    }
    
    public List<EclipseTask> listDoneByEclipse(Connection con) throws SQLException {
        return list(con, Status.DoneByEclipse);
    }
    
    public int doneByQin(Connection con, String orderId, String userName) throws SQLException {
        return updateStatus(con, orderId, Status.DoneByQin, userName);
    }
        
    public int doneByEclipse(Connection con, String orderId, String userName) throws SQLException {
        return updateStatus(con, orderId, Status.DoneByEclipse, userName);
    }
    
    protected String create(Connection con, EclipseTask task, Status status, String userName) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String newId = StringUtils.EMPTY;
        try {
            String sql = " INSERT EclipseTask(moduleId, orderId, patientSer, status, crtUser, crtDate, updUser, updDate) "
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            final Timestamp now = new Timestamp(System.currentTimeMillis());
            int index = 1;
            ps.setString(index++, task.getModuleId());
            ps.setString(index++, task.getOrderId());
            ps.setString(index++, task.getPatientSer());
            ps.setString(index++, status.toString());
            ps.setString(index++, userName);
            ps.setTimestamp(index++, now);
            ps.setString(index++, userName);
            ps.setTimestamp(index++, now);
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getString(1);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return newId;
    }
    
    protected List<EclipseTask> list(Connection con, Status status) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<EclipseTask> taskList = new ArrayList<>();
        EclipseTask task = null;
        try {
            String sql = " SELECT moduleId, orderId, patientSer, status "
                    + " FROM EclipseTask "
                    + " WHERE status = ? "
                    + " ORDER BY crtDate ASC ";
            ps = con.prepareStatement(sql);
            ps.setString(1, status.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                task = new EclipseTask();
                task.setModuleId(rs.getString("moduleId"));
                task.setOrderId(rs.getString("orderId"));
                task.setPatientSer(rs.getString("patientSer"));
                task.setStatus(rs.getString("status"));
                taskList.add(task);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return taskList;
    }
    
    protected int updateStatus(Connection con, String orderId, Status status, String userName) throws SQLException {
        PreparedStatement ps = null;
        int affectedRow = 0;
        try {
            String sql = " UPDATE EclipseTask "
                    + " SET status = ?, "
                    + " updUser = ?, updDate = ? " 
                    + " WHERE orderId = ? ";
            ps = con.prepareStatement(sql);
            ps.setString(1, status.toString());
            ps.setString(2, userName);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, orderId);
            affectedRow = ps.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, null);
        }
        return affectedRow;
    }
    
    @Override
    protected String getTableName() {
        return "EclipseTask";
    }

    @Override
    protected String getJsonbColumnName() {
        return "";
    }
    
    protected enum Status {
        Pending,
        DoneByQin,
        DoneByEclipse
    }
}
