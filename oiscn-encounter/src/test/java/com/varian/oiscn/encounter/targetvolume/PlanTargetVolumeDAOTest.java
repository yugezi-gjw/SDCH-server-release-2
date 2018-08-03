package com.varian.oiscn.encounter.targetvolume;

import com.varian.oiscn.core.targetvolume.PlanTargetVolume;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockPreparedStatement;
import com.varian.oiscn.encounter.util.MockResultSet;
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

/**
 * Created by bhp9696 on 2018/3/1.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PlanTargetVolumeDAO.class,MockPreparedStatement.class})
public class PlanTargetVolumeDAOTest {
    private PlanTargetVolumeDAO planTargetVolumeDAO;
    private Connection conn;

    @Before
    public void setup(){
        try {
            conn = PowerMockito.mock(MockDatabaseConnection.class);
            UserContext userContext = PowerMockito.mock(UserContext.class);
            Login login = PowerMockito.mock(Login.class);
            PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
            PowerMockito.when(userContext.getLogin()).thenReturn(login);
            PowerMockito.when(userContext.getName()).thenReturn("sysadmin");
            planTargetVolumeDAO = new PlanTargetVolumeDAO(userContext);
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreatePlanTargetVolumeThenReturnTrue() throws SQLException {
        List<PlanTargetVolume> list = Arrays.asList(new PlanTargetVolume("hisId","encounterId",1212L,"planId","baqu1"),
                new PlanTargetVolume("hisId2","encounterId2",1212L,"planId2","baqu2"));
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{1,1});
        boolean ok = planTargetVolumeDAO.create(conn,list);
        Assert.assertTrue(ok);
    }

    @Test
    public void testQueryPlanTargetVolumeListByHisIdThenReturnList() throws SQLException{
        Long encounterId = new Long(121);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("hisId")).thenReturn("hisId");
        PowerMockito.when(rs.getLong("patientSer")).thenReturn(233L);
        PowerMockito.when(rs.getString("encounterId")).thenReturn(encounterId.toString());
        PowerMockito.when(rs.getString("planId")).thenReturn("planId");
        PowerMockito.when(rs.getString("targetVolumeName")).thenReturn("baqu1");

        List<PlanTargetVolume> list = planTargetVolumeDAO.queryPlanTargetVolumeListByPatientSer(conn,233L,encounterId);

        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void testDelete() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        int i = planTargetVolumeDAO.delete(conn,12121L,111L);
        Assert.assertEquals(i,1);
    }
}
