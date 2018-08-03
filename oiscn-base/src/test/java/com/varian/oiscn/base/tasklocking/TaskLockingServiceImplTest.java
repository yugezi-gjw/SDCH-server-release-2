package com.varian.oiscn.base.tasklocking;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.user.UserContext;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TaskLockingServiceImpl.class, ConnectionPool.class, BasicDataSourceFactory.class})
public class TaskLockingServiceImplTest {
    private TaskLockingDAO taskLockingDAO;
    private TaskLockingServiceImpl taskLockingService;
    private Connection connection;

    @Before
    public void setup() {
        try {
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            connection = PowerMockito.mock(MockDatabaseConnection.class);
            taskLockingDAO = PowerMockito.mock(TaskLockingDAO.class);
            PowerMockito.whenNew(TaskLockingDAO.class).withAnyArguments().thenReturn(taskLockingDAO);
            taskLockingService = new TaskLockingServiceImpl(new UserContext());
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenTaskLockingDtoWhenFindLockTaskUserNameThenReturnObject() throws SQLException {
        TaskLockingDto taskLockingDto = new TaskLockingDto() {{
            setTaskId("1234");
            setActivityType(ActivityTypeEnum.TASK.name());
        }};
        List<TaskLockingDto> dtoList = Arrays.asList(taskLockingDto);
        PowerMockito.when(taskLockingDAO.findTaskLock(connection, taskLockingDto)).thenReturn(dtoList);
        TaskLockingDto dto = taskLockingService.findLockTaskUserName(taskLockingDto);
        Assert.assertTrue(taskLockingDto.getTaskId().equals(dto.getTaskId()));
    }

    @Test
    public void givenTaskLockingDtoWhenLockTaskThenReturnTrue() throws SQLException {
        TaskLockingDto taskLockingDto = new TaskLockingDto() {{
            setTaskId("1234");
            setActivityType(ActivityTypeEnum.TASK.name());
            setLockUserName("liguozhu");
            setLockTime(new Date());
            setResourceName("lgz");
            setResourceSer(1121L);
        }};
        PowerMockito.when(taskLockingDAO.create(connection, taskLockingDto)).thenReturn("1");
        boolean r = taskLockingService.lockTask(taskLockingDto);
        Assert.assertTrue(r);
    }

    @Test
    public void givenTaskLockingDtoWhenUnLockTaskThenReturnTrue() throws SQLException {
        TaskLockingDto taskLockingDto = new TaskLockingDto() {{
            setTaskId("1234");
            setActivityType(ActivityTypeEnum.TASK.name());
            setLockUserName("liguozhu");
        }};
        PowerMockito.when(taskLockingDAO.delete(connection, taskLockingDto)).thenReturn(1);
        boolean r = taskLockingService.unLockTask(taskLockingDto);
        Assert.assertTrue(r);
    }

    @Test
    public void givenWhenLockCurrentThreadThenReturnVoid() {
        taskLockingService.lockCurrentThread();
    }

    @Test
    public void givenWhenUnLockCurrentThreadThenReturnVoid() {
        taskLockingService.lockCurrentThread();
        taskLockingService.unLockCurrentThread();
    }

}
