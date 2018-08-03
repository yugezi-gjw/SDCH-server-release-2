package com.varian.oiscn.base.vid;

import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.base.util.MockPreparedStatement;
import com.varian.oiscn.base.util.MockResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Matchers.anyString;

/**
 * Created by gbt1220 on 10/16/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({VIDGeneratorDAO.class, MockPreparedStatement.class})
public class VIDGeneratorDAOTest {

    private Connection connection;

    private PreparedStatement ps;

    private ResultSet rs;

    private VIDGeneratorDAO dao;

    @Before
    public void setup() throws SQLException {
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(anyString())).thenReturn(ps);
        rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        dao = new VIDGeneratorDAO();
    }

    @Test
    public void givenWhenHasVIDThenReturnVID() throws SQLException {
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getInt(1)).thenReturn(1);
        Assert.assertEquals("1", dao.getVID(connection));
    }

    @Test
    public void givenWhenNoVIDThenReturnNull() throws SQLException {
        PowerMockito.when(rs.next()).thenReturn(false);
        Assert.assertNull(dao.getVID(connection));
    }

    @Test
    public void givenWhenAddVIDThenReturnTrue() throws SQLException {
        PowerMockito.doNothing().when(ps).setInt(1, 1);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        Assert.assertTrue(dao.addVID(connection, 1));
    }

    @Test
    public void givenWhenUpdateVIDThenReturnTrue() throws SQLException {
        PowerMockito.doNothing().when(ps).setInt(1, 2);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        Assert.assertTrue(dao.updateVID(connection, 2));
    }
}
