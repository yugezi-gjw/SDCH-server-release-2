package com.varian.oiscn.base.tasklocking;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.util.DatabaseUtil;
import com.varian.oiscn.core.user.UserContext;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by BHP9696 on 2017/10/16.
 */
@Slf4j
public class TaskLockingServiceImpl {
    private static Lock lock = new ReentrantLock();
    private TaskLockingDAO taskLockingDAO;
    private UserContext userContext;

    public TaskLockingServiceImpl(UserContext userContext) {
        this.userContext = userContext;
        taskLockingDAO = new TaskLockingDAO(userContext);
    }

    /**
     * 获取给定的taskId被锁定的用户
     *
     * @param taskLockingDto 需要参数taskId,activityType
     * @return
     */
    public TaskLockingDto findLockTaskUserName(TaskLockingDto taskLockingDto) {
        Connection conn = null;
        TaskLockingDto dto = null;
        try {
            conn = ConnectionPool.getConnection();
            List<TaskLockingDto> list = taskLockingDAO.findTaskLock(conn, taskLockingDto);
            if (!list.isEmpty()) {
                dto = list.get(0);
            }
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return dto;
    }

    /**
     * 锁定任务
     *
     * @param taskLockingDto
     * @return
     */
    public boolean lockTask(TaskLockingDto taskLockingDto) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            return Integer.parseInt(taskLockingDAO.create(conn, taskLockingDto)) > 0;
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return false;
    }

    /**
     * 解锁任务
     *
     * @param taskLockingDto
     * @return
     */
    public boolean unLockTask(TaskLockingDto taskLockingDto) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            taskLockingDto.setLockTime(null);
            return taskLockingDAO.delete(conn, taskLockingDto) > 0;
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return false;
    }


    public void lockCurrentThread() {
        lock.lock();
    }

    public void unLockCurrentThread() {
        lock.unlock();
    }
}
