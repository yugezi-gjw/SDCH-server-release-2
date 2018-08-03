package com.varian.oiscn.encounter;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.MockDatabaseConnection;
import com.varian.oiscn.util.MockPreparedStatement;
import com.varian.oiscn.util.MockResultSet;
import org.junit.Assert;
import org.junit.Before;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;

/**
 * Created by bhp9696 on 2017/11/9.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, EncounterCarePathDAO.class, MockPreparedStatement.class})
public class EncounterCarePathDAOTest {

    private EncounterCarePathDAO encounterCarePathDAO;

    private Connection connection;

    @Before
    public void setup(){
        UserContext userContext = PowerMockito.mock(UserContext.class);
        PowerMockito.when(userContext.getLogin()).thenReturn(new Login(){{
            setName("sysAdminName");
            setUsername("sysAdmin");
        }});
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        encounterCarePathDAO = new EncounterCarePathDAO(userContext);
    }

    @Test
    public void givenPatientSerListWhenSelectActivityIdListThenReturnList() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("patientSer")).thenReturn("1201");
        PowerMockito.when(rs.getLong("id")).thenReturn(12L);
        PowerMockito.when(rs.getLong("cpInstanceId")).thenReturn(5677L);
        PowerMockito.when(rs.getString("status")).thenReturn(StatusEnum.FINISHED.name());

        PowerMockito.when(rs.getString("patientSer")).thenReturn("1201");
        PowerMockito.when(rs.getLong("id")).thenReturn(13L);
        PowerMockito.when(rs.getLong("cpInstanceId")).thenReturn(6677L);
        PowerMockito.when(rs.getString("status")).thenReturn(StatusEnum.FINISHED.name());

        List<PatientEncounterCarePath> list = encounterCarePathDAO.selectCarePathInstanceIdList(connection, Arrays.asList("1201"));
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void testAddCarePathInstanceId() {
        Connection con = null;
        int expected = 1;
        int actual = 0;
        String encounterId = "1234";
        String carePathInstanceId = "5678";

        try {
            con = PowerMockito.mock(MockDatabaseConnection.class);
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            PowerMockito.when(ps.executeUpdate()).thenReturn(expected);
            EncounterCarePath encounterCarePath = new EncounterCarePath(){{
               setCpInstanceId(new Long(carePathInstanceId));
               setCategory(EncounterCarePathCategoryEnum.PRIMARY);
               setEncounterId(new Long(encounterId));
            }};
            actual = encounterCarePathDAO.addCarePathInstanceId(con, encounterCarePath);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCountCarePathByPatientSer() {
        Connection con;
        int expected = 2;
        Long patientSer = 1212L;
        try {
            con = PowerMockito.mock(MockDatabaseConnection.class);
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getInt(1)).thenReturn(expected);
            int actual = encounterCarePathDAO.countCarePathByPatientSer(con, patientSer);
            Assert.assertEquals(expected, actual);
        } catch (SQLException e) {
            Assert.fail();
        }
    }
}
