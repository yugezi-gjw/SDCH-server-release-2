package com.varian.oiscn.carepath.task;

import com.varian.oiscn.carepath.util.MockDatabaseConnection;
import com.varian.oiscn.carepath.util.MockPreparedStatement;
import com.varian.oiscn.carepath.util.MockResultSet;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EclipseTaskDAO.class, MockPreparedStatement.class})
public class EclipseTaskDAOTest {

    private EclipseTaskDAO dao;
    private Connection con;
    
    @Before
    public void setUp() throws Exception {
        con = PowerMockito.mock(MockDatabaseConnection.class);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        Login login = PowerMockito.mock(Login.class);
        PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
        PowerMockito.when(userContext.getLogin()).thenReturn(login);
        dao = new EclipseTaskDAO(userContext);
    }

    @Test
    public void testCreate() {
        String primarykey = "9527";
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        try {
            PowerMockito.when(con.prepareStatement(Mockito.anyString(), Mockito.anyInt())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
            EclipseTask task = new EclipseTask();
            String moduleId = "moduleId";
            task.setModuleId(moduleId);
            String orderId = "3456";
            task.setOrderId(orderId);
            String patientSer = "12346";
            task.setPatientSer(patientSer);
            String status = "pending";
            task.setStatus(status);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(1)).thenReturn(primarykey);
            String returnId = dao.create(con, task, "tester");
            Assert.assertTrue(primarykey.equals(returnId));
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testList() {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        try {
            PowerMockito.when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(Mockito.anyString())).thenReturn("data");
            List<EclipseTask> list = dao.listPending(con);
            Assert.assertNotNull(list);
            Assert.assertTrue(list.size() > 0);
            
            list = dao.listDoneByEclipse(con);
            Assert.assertNotNull(list);
            Assert.assertTrue(list.size() > 0);
            
            list = dao.listDoneByQin(con);
            Assert.assertNotNull(list);
            Assert.assertTrue(list.size() > 0);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateStatus() {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        try {
            PowerMockito.when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);

            PowerMockito.when(ps.executeUpdate()).thenReturn(1);
            int affectedRow = dao.doneByQin(con, "1234", "tester");
            Assert.assertTrue(affectedRow > 0);
            
            affectedRow = dao.doneByEclipse(con, "1234", "tester");
            Assert.assertTrue(affectedRow > 0);
            
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

}
