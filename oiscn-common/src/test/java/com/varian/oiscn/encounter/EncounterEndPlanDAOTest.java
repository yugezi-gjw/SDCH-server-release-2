package com.varian.oiscn.encounter;

import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.util.MockDatabaseConnection;
import com.varian.oiscn.util.MockPreparedStatement;
import com.varian.oiscn.util.MockResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;

import java.sql.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EncounterEndPlanDAOTest {

    EncounterEndPlanDAO encounterEndPlanDAO;

    @Before
    public void setup(){
        encounterEndPlanDAO = new EncounterEndPlanDAO();
    }
    @Test
    public void testBatchCreate() throws SQLException{
        EncounterEndPlan encounterEndPlan = new EncounterEndPlan();
        encounterEndPlan.setEncounterId(1000L);
        encounterEndPlan.setPlanCreatedDt(new Date());
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{1});
        Assert.assertTrue(encounterEndPlanDAO.batchCreate(connection, Arrays.asList(encounterEndPlan)));
    }

    @Test
    public void testSelectEncounterEndPlanListByPatientSer() throws SQLException {
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getLong("encounterId")).thenReturn(1L);
        PowerMockito.when(resultSet.getTimestamp("planCreatedDt")).thenReturn(new Timestamp(1000000000));
        List<EncounterEndPlan> encounterEndPlanList = encounterEndPlanDAO.selectEncounterEndPlanListByPatientSer(connection, 15L);
        Assert.assertEquals(new Long(1L), encounterEndPlanList.get(0).getEncounterId());
    }
}
