package com.varian.oiscn.base.util;

import com.varian.oiscn.util.DatabaseUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class DatabaseUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSafeCloseConnection() {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        try {
            PowerMockito.when(con.isClosed()).thenReturn(false);
            DatabaseUtil.safeCloseConnection(con);
            Mockito.verify(con).close();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseConnectionWithException() {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        try {
            SQLException se = PowerMockito.mock(SQLException.class);
            PowerMockito.when(con.isClosed()).thenThrow(se);
            DatabaseUtil.safeCloseConnection(con);
            Mockito.verify(con, Mockito.times(0)).close();
            Mockito.verify(se, Mockito.times(1)).getSQLState();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseStatement() {
        Statement mock = PowerMockito.mock(MockPreparedStatement.class);
        try {
            PowerMockito.when(mock.isClosed()).thenReturn(false);
            DatabaseUtil.safeCloseStatement(mock);
            Mockito.verify(mock).close();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseStatementWithException() {
        Statement mock = PowerMockito.mock(MockPreparedStatement.class);
        try {
            SQLException se = PowerMockito.mock(SQLException.class);
            PowerMockito.doThrow(se).when(mock).close();
            DatabaseUtil.safeCloseStatement(mock);
            Mockito.verify(se, Mockito.times(1)).getSQLState();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseResultSet() {
        ResultSet mock = PowerMockito.mock(MockResultSet.class);
        try {
//			PowerMockito.when(mock.isClosed()).thenReturn(false);
            DatabaseUtil.safeCloseResultSet(mock);
            Mockito.verify(mock).close();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseResultSetWithException() {
        ResultSet mock = PowerMockito.mock(MockResultSet.class);
        try {
            SQLException se = PowerMockito.mock(SQLException.class);
            PowerMockito.doThrow(se).when(mock).close();
            DatabaseUtil.safeCloseResultSet(mock);
            Mockito.verify(se).getSQLState();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseAll() {
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            Statement st = PowerMockito.mock(MockPreparedStatement.class);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            DatabaseUtil.safeCloseAll(con, st, rs);
            Mockito.verify(con).close();
            Mockito.verify(st).close();
            Mockito.verify(rs).close();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseAllWithConException() {
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            Statement st = PowerMockito.mock(MockPreparedStatement.class);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            SQLException se = PowerMockito.mock(SQLException.class);
            PowerMockito.doThrow(se).when(con).close();

            DatabaseUtil.safeCloseAll(con, st, rs);
            Mockito.verify(se).getSQLState();
            Mockito.verify(st).close();
            Mockito.verify(rs).close();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseAllWithStException() {
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            Statement st = PowerMockito.mock(MockPreparedStatement.class);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            SQLException se = PowerMockito.mock(SQLException.class);
            PowerMockito.doThrow(se).when(st).close();

            DatabaseUtil.safeCloseAll(con, st, rs);
            Mockito.verify(se).getSQLState();
            Mockito.verify(con).close();
            Mockito.verify(rs).close();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeCloseAllWithRsException() {
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            Statement st = PowerMockito.mock(MockPreparedStatement.class);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            SQLException se = PowerMockito.mock(SQLException.class);
            PowerMockito.doThrow(se).when(rs).close();

            DatabaseUtil.safeCloseAll(con, st, rs);
            Mockito.verify(se).getSQLState();
            Mockito.verify(con).close();
            Mockito.verify(st).close();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeSetAutoCommit() {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        try {
            DatabaseUtil.safeSetAutoCommit(con, true);
            Mockito.verify(con).setAutoCommit(true);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeSetAutoCommitWithException() {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        try {
            SQLException se = PowerMockito.mock(SQLException.class);
            PowerMockito.doThrow(se).when(con).setAutoCommit(true);
            DatabaseUtil.safeSetAutoCommit(con, true);
            Mockito.verify(se, Mockito.times(1)).getSQLState();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testSafeRollback() {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        try {
            DatabaseUtil.safeRollback(con);
            Mockito.verify(con).rollback();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSafeRollbackWithException() {
        Connection con = PowerMockito.mock(MockDatabaseConnection.class);
        try {
            SQLException se = PowerMockito.mock(SQLException.class);
            PowerMockito.doThrow(se).when(con).rollback();
            DatabaseUtil.safeRollback(con);
            Mockito.verify(se, Mockito.times(1)).getSQLState();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

}
