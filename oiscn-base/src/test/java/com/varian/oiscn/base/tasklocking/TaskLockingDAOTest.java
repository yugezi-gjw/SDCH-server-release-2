package com.varian.oiscn.base.tasklocking;

import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.base.util.MockPreparedStatement;
import com.varian.oiscn.base.util.MockResultSet;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.*;
import java.util.Date;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({MockPreparedStatement.class})
public class TaskLockingDAOTest {
    private TaskLockingDAO taskLockingDAO;
    private Connection connection;

    @Before
    public void setup() {
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        taskLockingDAO = new TaskLockingDAO(new UserContext());
    }

    @Test
    public void givenTaskLockingDtoWhenCreateThenReturnString() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        TaskLockingDto taskLockingDto = new TaskLockingDto() {{
            setLockTime(new Date());
            setResourceSer(1188L);
        }};
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        String rStr = taskLockingDAO.create(connection, taskLockingDto);
        Assert.assertTrue("1".equals(rStr));
    }

    @Test
    public void givenTaskLockingDtoWhenDeleteThenReturnInt() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        TaskLockingDto taskLockingDto = new TaskLockingDto() {{
            setLockUserName("liguozhu");
            setTaskId("123");
            setActivityType(ActivityTypeEnum.TASK.name());
            setResourceSer(1000L);
            setResourceName("resourceName");
            setLockTime(new Date());
        }};
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        int effect = taskLockingDAO.delete(connection, taskLockingDto);
        Assert.assertTrue(effect == 1);
    }

    @Test
    public void testFindTaskLock() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        TaskLockingDto taskLockingDto = new TaskLockingDto() {{
            setLockUserName("liguozhu");
            setTaskId("123");
            setActivityType(ActivityTypeEnum.TASK.name());
            setResourceSer(1000L);
            setResourceName("resourceName");
            setLockTime(new Date());
        }};

        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString(Mockito.anyString())).thenReturn("result");
        PowerMockito.when(rs.getLong(Mockito.anyString())).thenReturn(1000L);
        PowerMockito.when(rs.getTimestamp(Mockito.anyString())).thenReturn(new Timestamp(new Date().getTime()));

		List<TaskLockingDto> list = taskLockingDAO.findTaskLock(connection, taskLockingDto);
        Assert.assertTrue(list.size() == 1);
    }
}
