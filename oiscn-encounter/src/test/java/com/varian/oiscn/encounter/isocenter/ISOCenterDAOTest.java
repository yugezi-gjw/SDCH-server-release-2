package com.varian.oiscn.encounter.isocenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

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

/**
 * Created by BHP9696 on 2017/7/26.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ISOCenterDAOTest.class, MockPreparedStatement.class})
public class ISOCenterDAOTest {
    private ISOCenterDAO isoCenterDAO;
    private Connection conn;

    @Before
    public void setup() {
        conn = PowerMockito.mock(MockDatabaseConnection.class);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        Login login = PowerMockito.mock(Login.class);
        PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
        PowerMockito.when(userContext.getLogin()).thenReturn(login);
        PowerMockito.when(userContext.getName()).thenReturn("sysadmin");
        isoCenterDAO = new ISOCenterDAO(userContext);
    }

    @Test
    public void givnExistsPatientSerThenReturnISOCenter() {
        try {
            Long patientSer = 1212L;
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true);
            PowerMockito.when(rs.getString("isoCenterInfo")).thenReturn("{\"planList\":[{\"siteList\":[{\"lat\":34.6,\"lng\":90,\"isoName\":\"ISO1\",\"vrt\":100},{\"lat\":11.9,\"lng\":48.2,\"isoName\":\"ISO2\",\"vrt\":92}],\"planId\":\"Lung RA\"},{\"siteList\":[{\"lat\":23.6,\"lng\":55.4,\"isoName\":\"ISO1\",\"vrt\":90},{\"lat\":9.9,\"lng\":38.2,\"isoName\":\"ISO2\",\"vrt\":88}],\"planId\":\"Node RA\"}],\"patientSer\":\"" + patientSer + "\",\"encounterId\":\"533\",\"id\":null}");
            PowerMockito.when(rs.getString("id")).thenReturn("101");
            ISOCenter isoCenter = isoCenterDAO.selectISOCenterByPatientSer(conn, patientSer,111L);
            Assert.assertNotNull(isoCenter);
            Assert.assertTrue(patientSer.equals(isoCenter.getPatientSer()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenNotExistsPatientSerThenReturnNull() {
        try {
            Long patientSer = 1212L;
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(false);
            ISOCenter isoCenter = isoCenterDAO.selectISOCenterByPatientSer(conn, patientSer,111L);
            Assert.assertNull(isoCenter);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreate() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString(),Matchers.anyInt())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString(1)).thenReturn("12");
        PreparedStatement ps2 = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps2);
        PowerMockito.when(ps2.executeBatch()).thenReturn(new int[]{1,1});
        ISOCenter isoCenter =getISOCenterEntiry();
        String pk = isoCenterDAO.create(conn,isoCenter);
        Assert.assertNotNull(pk);
    }

    @Test
    public void testUpdate() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        PreparedStatement ps2 = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps2);
        PowerMockito.when(ps2.executeBatch()).thenReturn(new int[]{1,1});
        ISOCenter isoCenter = getISOCenterEntiry();
        boolean ok = isoCenterDAO.update(conn,isoCenter,"1212");
        Assert.assertTrue(ok);
    }

    private ISOCenter getISOCenterEntiry(){
        return  new ISOCenter(){{
            setId("1212");
            setHisId("hisId");
            setEncounterId("1111");
            setPatientSer(1234L);
            setPlanList(Arrays.asList(new ISOPlanTretment(){{
                setPlanId("plan1");
                setSiteList(Arrays.asList(new ISOCenterVO(){{
                    setIsoName("isoName");
                    setLat(1d);
                    setLng(2d);
                    setVrt(3d);
                }}));
            }}));
        }};
    }
}
