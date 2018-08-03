package com.varian.oiscn.util;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathDAO;
import com.varian.oiscn.encounter.EncounterCarePathServiceImpl;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
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
 * Created by bhp9696 on 2017/11/9.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, EncounterCarePath.class, BasicDataSourceFactory.class,EncounterCarePathServiceImpl.class})
public class EncounterCarePathServiceImplTest {

    private Connection connection;
    private EncounterCarePathDAO encounterCarePathDAO;
    private EncounterCarePathServiceImpl encounterCarePathService;

    @Before
    public void setup() {
        try {
            connection = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
            encounterCarePathDAO = PowerMockito.mock(EncounterCarePathDAO.class);
            PowerMockito.whenNew(EncounterCarePathDAO.class).withAnyArguments().thenReturn(encounterCarePathDAO);
            UserContext userContext = PowerMockito.mock(UserContext.class);
            PowerMockito.when(userContext.getLogin()).thenReturn(new Login(){{
                setUsername("sysAdmin");
            }});
            encounterCarePathService = new EncounterCarePathServiceImpl(userContext);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenQueryEncounterHeaderActivityByPatientSerThenReturnObject() throws SQLException {
        PatientEncounterCarePath patientEncounterCarePath = new PatientEncounterCarePath();
        List<PatientEncounterCarePath> rList = Arrays.asList(patientEncounterCarePath);
        PowerMockito.when(encounterCarePathDAO.selectCarePathInstanceIdList(connection, Arrays.asList("1234"))).thenReturn(rList);
        PatientEncounterCarePath en = encounterCarePathService.queryEncounterCarePathByPatientSer("1234");
        Assert.assertNotNull(en);
    }

    @Test
    public void testCountCarePathByHisId() throws SQLException {
        Long patientSer = 1212L;
        int expected = 2;
        PowerMockito.when(encounterCarePathDAO.countCarePathByPatientSer(connection, patientSer)).thenReturn(expected);
        int actual = encounterCarePathService.countCarePathByPatientSer(patientSer);
        Assert.assertEquals(expected, actual);
    }
}
