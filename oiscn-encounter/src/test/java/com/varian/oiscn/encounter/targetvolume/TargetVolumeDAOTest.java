package com.varian.oiscn.encounter.targetvolume;

import com.varian.oiscn.core.targetvolume.TargetVolume;
import com.varian.oiscn.core.targetvolume.TargetVolumeItem;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockPreparedStatement;
import com.varian.oiscn.encounter.util.MockResultSet;
import lombok.Cleanup;
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
 * Created by BHP9696 on 2017/7/26.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TargetVolumeDAO.class, MockPreparedStatement.class})
public class TargetVolumeDAOTest {
    private TargetVolumeDAO targetVolumeDAO;
    private Connection conn;

    @Before
    public void setup() {
        try {
            conn = PowerMockito.mock(MockDatabaseConnection.class);
            UserContext userContext = PowerMockito.mock(UserContext.class);
            Login login = PowerMockito.mock(Login.class);
            PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
            PowerMockito.when(userContext.getLogin()).thenReturn(login);
            PowerMockito.when(userContext.getName()).thenReturn("sysadmin");
            targetVolumeDAO = new TargetVolumeDAO(userContext);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenExistPatientSerAndEncounterIdQueryTargetVolumeThenReturnTargetVolume() {
        try {
            Long patientSer = 1212L;
            Long encounterId = 121L;
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PreparedStatement psItem = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
            PowerMockito.when(conn.prepareStatement("SELECT targetVolumeId,fieldId,fieldValue,rNum,seq FROM TargetVolumeItem WHERE targetVolumeId = ? ORDER BY rNum,seq")).thenReturn(psItem);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getLong("id")).thenReturn(1213L);
            PowerMockito.when(rs.getLong("patientSer")).thenReturn(patientSer);
            PowerMockito.when(rs.getString("encounterId")).thenReturn("1234");
            PowerMockito.when(rs.getString("name")).thenReturn("VolumeName1");
            PowerMockito.when(rs.getString("memo")).thenReturn("remarks for VolumeName1");
            ResultSet rsItem = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(psItem.executeQuery()).thenReturn(rsItem);
            PowerMockito.when(rsItem.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rsItem.getString("fieldId")).thenReturn("volume");
            PowerMockito.when(rsItem.getString("fieldValue")).thenReturn("GTV");
            PowerMockito.when(rsItem.getInt("rNum")).thenReturn(1);
            PowerMockito.when(rsItem.getInt("seq")).thenReturn(1);

            List<TargetVolume> list = targetVolumeDAO.selectTargetVolumeByPatientSer(conn,patientSer,encounterId);
            Assert.assertNotNull(list);
            Assert.assertTrue(!list.isEmpty());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenNotExistPatientSerAndEncounterIdQueryTargetVolumeThenReturnNull() {
        try {
            Long patientSer = 1212L;
            Long encounterId = 121L;
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(false);
            List<TargetVolume> returnTargetVolume = targetVolumeDAO.selectTargetVolumeByPatientSer(conn, patientSer,encounterId);
            Assert.assertTrue(returnTargetVolume.isEmpty());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenTargetVolumeSaveSuccessThenReturnPrimaryKey() {
        try {
            List<TargetVolume> targetVolumeList = Arrays.asList(new TargetVolume(){{
                setPatientSer(121L);
                setTargetVolumeItemList(Arrays.asList(new TargetVolumeItem(){{
                    setRNum(1);
                    setSeq(1);
                }}));
            }});

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(conn.prepareStatement(Matchers.anyString(), Matchers.anyByte())).thenReturn(ps);
            PowerMockito.when(ps.executeUpdate()).thenReturn(1);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true);
            PowerMockito.when(rs.getString(1)).thenReturn("101");
            PreparedStatement ps2 = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps2);
            PowerMockito.when(ps2.executeBatch()).thenReturn(new int[]{1});
            boolean ok = targetVolumeDAO.create(conn, targetVolumeList);
            Assert.assertTrue(ok);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSelectTargetVolumeExceptItemByHisIdThenReturnList(){
        try {
            Long patientSer = 1212L;
            Long encounterId = 1234L;
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(conn.prepareStatement(Matchers.anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getLong("id")).thenReturn(1213L);
            PowerMockito.when(rs.getLong("patientSer")).thenReturn(patientSer);
            PowerMockito.when(rs.getString("encounterId")).thenReturn("1234");
            PowerMockito.when(rs.getString("name")).thenReturn("VolumeName1");
            PowerMockito.when(rs.getString("memo")).thenReturn("remarks for VolumeName1");
            List<TargetVolume> list = targetVolumeDAO.selectTargetVolumeExceptItemByPatientSer(conn,patientSer,encounterId);
            Assert.assertNotNull(list);
            Assert.assertTrue(!list.isEmpty());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void  testBatchDelete() throws SQLException {
        List<Long> idList = Arrays.asList(123L,234L);
        StringBuffer delSubItemsSql = new StringBuffer("DELETE FROM TargetVolumeItem WHERE EXISTS (SELECT 'X' FROM ")
                .append("TargetVolume").append(" tv WHERE tv.id=TargetVolumeItem.targetVolumeId AND (");
        StringBuffer delSql = new StringBuffer("DELETE FROM ").append("TargetVolume").append(" WHERE (");

        for(int i=0;i<idList.size();i++){
            if(i<idList.size()-1){
                delSubItemsSql.append("tv.id=? OR ");
                delSql.append("id=? OR ");
            }else{
                delSubItemsSql.append("tv.id=?").append(") )");
                delSql.append("id=?").append(")");
            }
        }
        @Cleanup
        PreparedStatement delSubItemPs = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(delSubItemsSql.toString())).thenReturn(delSubItemPs);
        PowerMockito.when(delSubItemPs.executeUpdate()).thenReturn(2);
        @Cleanup
        PreparedStatement delPs = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(conn.prepareStatement(delSql.toString())).thenReturn(delPs);
        PowerMockito.when(delPs.executeUpdate()).thenReturn(4);

        boolean ok = targetVolumeDAO.batchDelete(conn,idList);
        Assert.assertTrue(ok);
    }

    @Test
    public void testGetJsonbColumnName(){
        Assert.assertTrue("".equals(targetVolumeDAO.getJsonbColumnName()));
    }

}

