package com.varian.oiscn.encounter.assign;

import com.varian.oiscn.core.assign.AssignResource;
import com.varian.oiscn.core.assign.AssignResourceVO;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockDtoUtil;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by bhp9696 on 2018/3/23.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AssignResourceDAO.class, MockPreparedStatement.class})
public class AssignResourceDAOTest {
    private AssignResourceDAO assignResourceDAO;
    private Connection conn;
    private PreparedStatement ps;

    @Before
    public void setup() throws SQLException {
        conn = PowerMockito.mock(MockDatabaseConnection.class);
        ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
    }

    @Test
    public void testGetTableName(){
        assignResourceDAO = new AssignResourceDAO(new UserContext());
        String tblName = assignResourceDAO.getTableName();
        Assert.assertTrue("AssignResource".equals(tblName));
    }

    @Test
    public void testGetJsonbColumnName(){
        assignResourceDAO = new AssignResourceDAO(new UserContext());
        String jsonbColumnName = assignResourceDAO.getJsonbColumnName();
        Assert.assertNull(jsonbColumnName);
    }

    @Test
    public void testBatchCreate() throws SQLException {
        Connection conn = PowerMockito.mock(MockDatabaseConnection.class);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
        List<AssignResource> assignResourceList = Arrays.asList(new AssignResource(){{
            setResourceId("resourceId");
            setPatientSer(1212L);
            setEncounterId(123L);
            setActivityCode("activityCode");
        }});
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{12});
        assignResourceDAO = new AssignResourceDAO(MockDtoUtil.givenUserContext());
        int[] r = assignResourceDAO.batchCreate(conn,assignResourceList);
        Assert.assertNotNull(r);
        Assert.assertEquals(12, r[0]);
    }

    @Test
    public void testCreate() throws SQLException {
        Connection conn = PowerMockito.mock(MockDatabaseConnection.class);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);

        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{12});
        assignResourceDAO = new AssignResourceDAO(MockDtoUtil.givenUserContext());
        int r = assignResourceDAO.createResource(conn,new AssignResource(){{
            setResourceId("resourceId");
            setPatientSer(1212L);
            setEncounterId(123L);
            setActivityCode("activityCode");
        }});
        Assert.assertNotNull(r);
        Assert.assertEquals(12, r);
    }

    @Test
    public void testDeleteFromAssignedDevice() throws SQLException{
        assignResourceDAO = new AssignResourceDAO(new UserContext());
        boolean r = assignResourceDAO.delete(conn, 1212L, null);
        Assert.assertTrue(r);
    }

    @Test
    public void testSearchAssignTPSSummary() throws SQLException {
        assignResourceDAO = new AssignResourceDAO(new UserContext());
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("name")).thenReturn("TPS");
        PowerMockito.when(rs.getString("value")).thenReturn("#FFFFFF");
        PowerMockito.when(rs.getInt("amount")).thenReturn(25);
        Assert.assertEquals(1, assignResourceDAO.listAssignResourceSummary(conn, "activity01, activity02").size());
    }

    @Test
    public void testDeleteFromAssignedTPS() throws SQLException{
        assignResourceDAO = new AssignResourceDAO(new UserContext());
        boolean r = assignResourceDAO.delete(conn, 1212L, null);
        Assert.assertTrue(r);
    }

    @Test
    public void testSelectAssignResourceStatistics() throws SQLException{
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("RESOURCE_ID")).thenReturn("1234");
        PowerMockito.when(rs.getInt("RESOURCE_CNT")).thenReturn(25);
        assignResourceDAO = new AssignResourceDAO(new UserContext());
        List<AssignResourceVO> list = assignResourceDAO.listAssignResourceSummary(conn, "activityCode");
        Assert.assertNotNull(list);
        Assert.assertTrue("1234".equals(list.get(0).getId()));
        Assert.assertTrue("1234".equals(list.get(0).getCode()));
        Assert.assertNull(list.get(0).getColor());
        Assert.assertNotNull(list.get(0).getName());
        Assert.assertEquals(list.get(0).getAmount(),25);
        Assert.assertNull(list.get(0).getPatientSerInstanceIdPairList());
    }

    @Test
    public void testGetAllDeviceConfig() throws SQLException {
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("resourceId")).thenReturn("Eclipse");
        PowerMockito.when(rs.getString("resourceCode")).thenReturn("Eclipse");
        PowerMockito.when(rs.getString("resourceName")).thenReturn("Eclipse");
        PowerMockito.when(rs.getString("color")).thenReturn("#FFEFDD");
        PowerMockito.when(rs.getString("activityCode")).thenReturn("AssignTPS");
        assignResourceDAO = new AssignResourceDAO(new UserContext());
        List<DeviceDto> deviceDtoList = assignResourceDAO.getAllResourceConfig(conn,"AssignTPS");
        Assert.assertNotNull(deviceDtoList);
        Assert.assertTrue(deviceDtoList.size()==1);
        Assert.assertTrue(deviceDtoList.get(0).getId().equals("Eclipse"));
    }

    @Test
    public void testGetAssignResourceByPatientSerList() throws SQLException {
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        List<Long> patientSerList = new ArrayList<>();
        patientSerList.add(1L);
        PowerMockito.when(rs.getLong(1)).thenReturn(1L);
        PowerMockito.when(rs.getString(2)).thenReturn("assign");
        assignResourceDAO = new AssignResourceDAO(new UserContext());
        Map<Long, String> resultMap = assignResourceDAO.getPatientSerResourceMapByPatientSerList(conn, patientSerList, "physicistGroupingActivityCode");
        Assert.assertEquals("assign", resultMap.get(1L));
    }
}
