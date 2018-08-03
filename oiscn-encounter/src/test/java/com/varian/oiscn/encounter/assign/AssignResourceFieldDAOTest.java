package com.varian.oiscn.encounter.assign;

import com.varian.oiscn.core.assign.AssignResourceField;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockPreparedStatement;
import com.varian.oiscn.encounter.util.MockResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by bhp9696 on 2018/5/7.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AssignResourceFieldDAO.class})
public class AssignResourceFieldDAOTest {

    private AssignResourceFieldDAO assignResourceFieldDAO;


    @Test
    public void testGetTableName(){
        assignResourceFieldDAO = new AssignResourceFieldDAO(new UserContext());
        Assert.assertTrue("AssignResourceField".equals(assignResourceFieldDAO.getTableName()));
    }

    @Test
    public void testGetJsonbColumnName(){
        assignResourceFieldDAO = new AssignResourceFieldDAO(new UserContext());
        Assert.assertNull(assignResourceFieldDAO.getJsonbColumnName());
    }

    @Test
    public void testQueryAssignResourceFieldByCategory() throws SQLException {
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        assignResourceFieldDAO = new AssignResourceFieldDAO(new UserContext());
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);

        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("category")).thenReturn("AssignDevice");
        PowerMockito.when(rs.getString("name")).thenReturn("saomiaobuwei");
        PowerMockito.when(rs.getString("value")).thenReturn("saomiaobuwei");
        PowerMockito.when(rs.getInt("sortNumber")).thenReturn(1);
        List<AssignResourceField> list = assignResourceFieldDAO.queryAssignResourceFieldByCategory(connection,"AssignDevice");
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertTrue(list.get(0).getName().equals("saomiaobuwei"));
    }

    @Test
    public void testQueryAssignResourceFieldValue() throws SQLException {
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        assignResourceFieldDAO = new AssignResourceFieldDAO(new UserContext());
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);

        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("category")).thenReturn("AssignDevice");
        PowerMockito.when(rs.getString("name")).thenReturn("saomiaobuwei");
        PowerMockito.when(rs.getString("value")).thenReturn("saomiaobuwei");
        PowerMockito.when(rs.getInt("sortNumber")).thenReturn(1);
        List<AssignResourceField> list = assignResourceFieldDAO.queryAssignResourceFieldValue(connection,"AssignResourceFieldServiceImp");
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertTrue(list.get(0).getName().equals("saomiaobuwei"));
    }



    @Test
    public void testCreate() throws SQLException {
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        PowerMockito.when(userContext.getName()).thenReturn("sysAdmin");
        assignResourceFieldDAO = new AssignResourceFieldDAO(userContext);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString(),Matchers.anyInt())).thenReturn(ps);

        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString(1)).thenReturn("11");
        String primaryKey = assignResourceFieldDAO.create(connection,new AssignResourceField());
        Assert.assertNotNull(primaryKey);
        Assert.assertTrue("11".equals(primaryKey));
    }


    @Test
    public void testUpdate() throws SQLException {
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        UserContext userContext = new UserContext(new Login(){{
            setUsername("sysAdmin");
        }},new OspLogin());

        assignResourceFieldDAO = new AssignResourceFieldDAO(userContext);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        boolean ok = assignResourceFieldDAO.update(connection,new AssignResourceField(),"11");
        Assert.assertTrue(ok);
    }
}
