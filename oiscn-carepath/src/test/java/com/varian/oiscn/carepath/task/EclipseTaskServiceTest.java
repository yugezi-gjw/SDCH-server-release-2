package com.varian.oiscn.carepath.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.carepath.util.MockDatabaseConnection;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.user.UserContext;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EclipseTaskService.class, ConnectionPool.class, BasicDataSourceFactory.class, TaskLockingServiceImpl.class})
public class EclipseTaskServiceTest {

    private Connection con;
    private EclipseTaskService service;
    private OrderAntiCorruptionServiceImp orderService;
    
    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        
        PowerMockito.mockStatic(ConnectionPool.class);
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        orderService = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        service = new EclipseTaskService(new UserContext());
        service.orderService = orderService;
    }

    @Test
    public void testThrowSqlException() throws SQLException {
        EclipseTaskDAO dao = PowerMockito.mock(EclipseTaskDAO.class);
        service.dao = dao;
        
        PowerMockito.when(dao.create(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(SQLException.class);
        
        PowerMockito.when(dao.listPending(con)).thenThrow(SQLException.class);
        
        PowerMockito.when(dao.doneByQin(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(SQLException.class);
        PowerMockito.when(dao.doneByEclipse(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(SQLException.class);
        
        EclipseTask task = new EclipseTask();
        String ret = service.createPendingTask(task, "tester");
        Assert.assertTrue(ret.length() == 0);
        
        List<EclipseTask> list = service.listPendingTask();
        Assert.assertTrue(list.size() == 0);
        
        int row = service.doneByEclipse("1234", "tester");
        Assert.assertTrue(row == 0);
        
        row = service.doneByQin("1234", "tester");
        Assert.assertTrue(row == 0);
    }

    @Test
    public void testUnlockQinTask() throws Exception {
        TaskLockingServiceImpl taskLockingService = PowerMockito.mock(TaskLockingServiceImpl.class);
        PowerMockito.whenNew(TaskLockingServiceImpl.class).withAnyArguments().thenReturn(taskLockingService);
        PowerMockito.when(taskLockingService.unLockTask(Mockito.any())).thenReturn(true);
        Assert.assertTrue(service.unlockQinTask("1234", "tester"));
    }

    @Test
    public void testQueryOrderById() {
        OrderDto dto = PowerMockito.mock(OrderDto.class);
        PowerMockito.when(orderService.queryOrderById("1234")).thenReturn(dto);
        Assert.assertSame(dto, service.queryOrderById("1234"));
    }

}
