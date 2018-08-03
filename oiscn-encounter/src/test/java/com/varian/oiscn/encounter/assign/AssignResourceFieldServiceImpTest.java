package com.varian.oiscn.encounter.assign;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.assign.AssignResourceField;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
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
import java.util.List;

/**
 * Created by bhp9696 on 2018/5/7.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AssignResourceFieldServiceImp.class, ConnectionPool.class, BasicDataSourceFactory.class})
public class AssignResourceFieldServiceImpTest {
    private Connection connection;
    private AssignResourceFieldServiceImp assignResourceFieldServiceImp;
    private AssignResourceFieldDAO assignResourceFieldDAO;

    @Before
    public void setup() throws Exception {
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);

        assignResourceFieldDAO = PowerMockito.mock(AssignResourceFieldDAO.class);
        PowerMockito.whenNew(AssignResourceFieldDAO.class).withAnyArguments().thenReturn(assignResourceFieldDAO);
        assignResourceFieldServiceImp = new AssignResourceFieldServiceImp(new UserContext());
    }

    @Test
    public void  testQueryAssignResourceFieldByCategory() throws SQLException {
        List<AssignResourceField> list = Arrays.asList(new AssignResourceField());
        PowerMockito.when(assignResourceFieldDAO.queryAssignResourceFieldByCategory(connection,"AssignTPS")).thenReturn(list);
        List<AssignResourceField> rlist = assignResourceFieldServiceImp.queryAssignResourceFieldByCategory("AssignTPS");
        Assert.assertNotNull(rlist);
        Assert.assertTrue(rlist.equals(list));
    }

    @Test
    public void  testQueryAssignResourceFieldByCategoryThrowSQLException() throws SQLException {
        PowerMockito.when(assignResourceFieldDAO.queryAssignResourceFieldByCategory(connection,"AssignTPS")).thenThrow(new SQLException("connect time out"));
        List<AssignResourceField> rlist = assignResourceFieldServiceImp.queryAssignResourceFieldByCategory("AssignTPS");
        Assert.assertTrue(rlist.isEmpty());
    }

    @Test
    public void testQueryAssignResourceFieldValue() throws SQLException {
        List<AssignResourceField> list = Arrays.asList(new AssignResourceField());
        PowerMockito.when(assignResourceFieldDAO.queryAssignResourceFieldValue(connection,"AssignResourceFieldServiceImp")).thenReturn(list);
        List<AssignResourceField> rlist = assignResourceFieldServiceImp.queryAssignResourceFieldValue("AssignResourceFieldServiceImp");
        Assert.assertNotNull(rlist);
        Assert.assertTrue(rlist.equals(list));
    }

    @Test
    public void testQueryAssignResourceFieldValueException() throws SQLException {
        PowerMockito.when(assignResourceFieldDAO.queryAssignResourceFieldValue(connection,"AssignResourceFieldServiceImp")).thenThrow(new SQLException("connect time out"));
        List<AssignResourceField> rlist = assignResourceFieldServiceImp.queryAssignResourceFieldValue("AssignResourceFieldServiceImp");
        Assert.assertTrue(rlist.isEmpty());
    }
}
