package com.varian.oiscn.encounter;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, EncounterCarePathServiceImpl.class})
public class EncounterCarePathServiceImplTest {
    EncounterCarePathServiceImpl encounterCarePathService;

    EncounterCarePathDAO encounterCarePathDAO;

    @Before
    public void setup() throws Exception{
        encounterCarePathDAO = PowerMockito.mock(EncounterCarePathDAO.class);
        PowerMockito.whenNew(EncounterCarePathDAO.class).withAnyArguments().thenReturn(encounterCarePathDAO);
        encounterCarePathService = new EncounterCarePathServiceImpl(givenUserContext());
    }

    @Test
    public void testAddEncounterCarePath() throws SQLException{
        Connection connection = PowerMockito.mock(Connection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
        EncounterCarePath encounterCarePath = new EncounterCarePath();
        PowerMockito.when(encounterCarePathDAO.addCarePathInstanceId(connection, encounterCarePath)).thenReturn(1);
        Assert.assertTrue(encounterCarePathService.addEncounterCarePath(encounterCarePath));
    }

    @Test
    public void testQueryEncounterCarePathByPatientSer() throws SQLException{
        Connection connection = PowerMockito.mock(Connection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
        String patientSer = "30000";
        PowerMockito.when(encounterCarePathDAO.selectCarePathInstanceIdList(connection, Arrays.asList(patientSer))).thenReturn(new ArrayList<>());
        Assert.assertEquals(null, encounterCarePathService.queryEncounterCarePathByPatientSer(patientSer));
    }

    @Test
    public void testCountCarePathByPatientSer() throws SQLException{
        Connection connection = PowerMockito.mock(Connection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
        Long patientSer = 30000L;
        PowerMockito.when(encounterCarePathDAO.countCarePathByPatientSer(connection, patientSer)).thenReturn(1);
        Assert.assertEquals(1, encounterCarePathService.countCarePathByPatientSer(patientSer));
    }

    private static UserContext givenUserContext() {
        return new UserContext(givenALogin(), givenAnOspLogin());
    }

    private static Login givenALogin() {
        Login login = new Login();
        login.setGroup("group");
        login.setName("name");
        login.setResourceSer(1L);
        login.setToken("token");
        login.setUsername("username");
        return login;
    }

    private static OspLogin givenAnOspLogin() {
        OspLogin ospLogin = new OspLogin();
        ospLogin.setName("name");
        ospLogin.setUsername("username");
        ospLogin.setDisplayName("displayName");
        ospLogin.setUserCUID("cuid");
        ospLogin.setToken("token");
        ospLogin.setLastModifiedDt(new Date());
        return ospLogin;
    }
}
