package com.varian.oiscn.base.tasklocking;

import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/16.
 */
@Slf4j
public class TaskLockingDAO extends AbstractDAO<TaskLockingDto> {

    public TaskLockingDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "TaskLocking";
    }

    @Override
    protected String getJsonbColumnName() {
        return null;
    }

    @Override
    public String create(Connection conn, TaskLockingDto taskLockingDto) throws SQLException {
        String inserSql = "insert into " + this.getTableName() + " (taskId,activityType,lockUserName,resourceSer,resourceName,lockTime) values (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(inserSql);
        ps.setString(1, taskLockingDto.getTaskId());
        ps.setString(2, taskLockingDto.getActivityType());
        ps.setString(3, taskLockingDto.getLockUserName());
        ps.setLong(4, taskLockingDto.getResourceSer());
        ps.setString(5, taskLockingDto.getResourceName());
        ps.setTimestamp(6, new Timestamp(taskLockingDto.getLockTime().getTime()));
        log.debug("insertSql={}", inserSql);
        return ps.executeUpdate() + "";
    }

    public int delete(Connection conn, TaskLockingDto taskLockingDto) throws SQLException {
        StringBuilder sb = new StringBuilder("delete from ");
        sb.append(this.getTableName()).append(" where 1=1 ");
        List<Object> param = new ArrayList<>();
        if (StringUtils.isNotEmpty(taskLockingDto.getTaskId())) {
            param.add(taskLockingDto.getTaskId());
            sb.append(" and taskId = ? ");
        }
        if (StringUtils.isNotEmpty(taskLockingDto.getActivityType())) {
            param.add(taskLockingDto.getActivityType());
            sb.append(" and activityType = ? ");
        }
        if (StringUtils.isNotEmpty(taskLockingDto.getLockUserName())) {
            param.add(taskLockingDto.getLockUserName());
            sb.append(" and lockUserName = ? ");
        }
        if (taskLockingDto.getResourceSer() != null) {
            param.add(taskLockingDto.getResourceSer());
            sb.append(" and resourceSer = ? ");
        }
        if (StringUtils.isNotEmpty(taskLockingDto.getResourceName())) {
            param.add(taskLockingDto.getResourceName());
            sb.append(" and resourceName = ? ");
        }
        if (taskLockingDto.getLockTime() != null) {
            param.add(taskLockingDto.getLockTime());
            sb.append(" and lockTime = ? ");
        }
        log.debug("deleteSql={}", sb.toString());
        if (param.size() > 0) {
            PreparedStatement ps = conn.prepareStatement(sb.toString());
            for (int i = 0; i < param.size(); i++) {
                ps.setObject(i + 1, param.get(i));
            }
            return ps.executeUpdate();
        } else {
            throw new SQLException("Parameter for delete task lock must not be empty");
        }
    }

    /**
     * 根据条件查询除了给定的LockUser之外被锁定的任务
     *
     * @param conn
     * @param taskLockingDto
     * @return
     * @throws SQLException
     */
    public List<TaskLockingDto> findTaskLock(Connection conn, TaskLockingDto taskLockingDto) throws SQLException {
        List<TaskLockingDto> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("select taskId,activityType,lockUserName,resourceSer,resourceName,lockTime from ");
        sb.append(this.getTableName()).append(" where 1=1 ");

        List<Object> param = new ArrayList<>();

        if (StringUtils.isNotEmpty(taskLockingDto.getTaskId())) {
            param.add(taskLockingDto.getTaskId());
            sb.append(" and taskId = ? ");
        }
        if (StringUtils.isNotEmpty(taskLockingDto.getActivityType())) {
            param.add(taskLockingDto.getActivityType());
            sb.append(" and activityType = ? ");
        }
        if (StringUtils.isNotEmpty(taskLockingDto.getLockUserName())) {
            param.add(taskLockingDto.getLockUserName());
            sb.append(" and lockUserName = ? ");
        }
        if (taskLockingDto.getResourceSer() != null) {
            param.add(taskLockingDto.getResourceSer());
            sb.append(" and resourceSer = ? ");
        }
        if (StringUtils.isNotEmpty(taskLockingDto.getResourceName())) {
            param.add(taskLockingDto.getResourceName());
            sb.append(" and resourceName = ? ");
        }
        if (taskLockingDto.getLockTime() != null) {
            param.add(taskLockingDto.getLockTime());
            sb.append(" and lockTime = ? ");
        }
        log.debug("selectSql={}", sb.toString());
        if (param.size() > 0) {
            PreparedStatement ps = conn.prepareStatement(sb.toString());
            for (int i = 0; i < param.size(); i++) {
                ps.setObject(i + 1, param.get(i));
            }
            ResultSet rs = ps.executeQuery();
            TaskLockingDto taskLockingDto1;

            while (rs.next()) {
                taskLockingDto1 = new TaskLockingDto(rs.getString("taskId"),
                        rs.getString("activityType"),
                        rs.getString("lockUserName"),
                        rs.getLong("resourceSer"),
                        rs.getString("resourceName"),
                        new Date(rs.getTimestamp("lockTime").getTime()));
                list.add(taskLockingDto1);
            }
        } else {
            throw new SQLException("Parameter for delete task lock must not be empty");
        }
        return list;
    }


}
