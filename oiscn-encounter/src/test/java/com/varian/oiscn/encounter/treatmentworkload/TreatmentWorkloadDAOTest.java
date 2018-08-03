package com.varian.oiscn.encounter.treatmentworkload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.treatmentworkload.WorkloadSignature.SignatureTypeEnum;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockPreparedStatement;
import com.varian.oiscn.encounter.util.MockResultSet;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by BHP9696 on 2017/8/22.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, MockPreparedStatement.class, BasicDataSourceFactory.class})
public class TreatmentWorkloadDAOTest {
   private TreatmentWorkloadDAO treatmentWorkloadDAO;


    @Before
    public void setup() {
        try {
            Locale.setDefault(Locale.CHINA);
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
            UserContext userContext = PowerMockito.mock(UserContext.class);
            PowerMockito.whenNew(UserContext.class).withAnyArguments().thenReturn(userContext);
            PowerMockito.when(userContext.getName()).thenReturn("sysadmin");
            Login login = new Login();
            login.setUsername("sysadmin");
            PowerMockito.when(userContext.getLogin()).thenReturn(login);
            treatmentWorkloadDAO = new TreatmentWorkloadDAO(userContext);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenSelectTreatmentWorkloadByHisIdThenReturnObject() throws SQLException {
        String id = "1";
        Long patientSer = 1212L;
        Long encounterId = 121L;
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);

        PowerMockito.when(rs.getString("workloadId")).thenReturn(id);
        PowerMockito.when(rs.getString("planId")).thenReturn("planId");
        PowerMockito.when(rs.getString("comment")).thenReturn("comment");
        PowerMockito.when(rs.getInt("deliveredFractions")).thenReturn(10);

        List<WorkloadPlan> list = treatmentWorkloadDAO.selectLatestWorkloadPlanByPatientSer(connection, patientSer,encounterId);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 1);
    }


    @Test
    public void givenNotExistsHisIdWhenSelectTreatmentWorkloadByHisIdThenReturnNull() throws SQLException {
        Long patientSer = 20170818004L;
        Long encounterId = 121L;
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(false);
        List<WorkloadPlan> list = treatmentWorkloadDAO.selectLatestWorkloadPlanByPatientSer(connection, patientSer,encounterId);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void givenPatientSerWhenQueryTreatmentWorkloadListByPatientSerThenReturnList() throws SQLException {
        Long patientSer = 1212L;
        Long encounterId = 121L;
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("id")).thenReturn("12");
        PowerMockito.when(rs.getString("hisId")).thenReturn("");
        PowerMockito.when(rs.getLong("patientSer")).thenReturn(patientSer);
        PowerMockito.when(rs.getString("encounterId")).thenReturn(encounterId.toString());
        PowerMockito.when(rs.getTimestamp("treatmentDate")).thenReturn(timestamp);


        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getLong("workloadId")).thenReturn(12L);
        PowerMockito.when(rs.getString("planId")).thenReturn("planId1");
        PowerMockito.when(rs.getString("comment")).thenReturn("comments");
        PowerMockito.when(rs.getInt("deliveredFractions")).thenReturn(1);

        PowerMockito.when(rs.getLong("workloadId")).thenReturn(12L);
        PowerMockito.when(rs.getString("userName")).thenReturn("");
        PowerMockito.when(rs.getString("resourceName")).thenReturn("Guozhu.Li");
        PowerMockito.when(rs.getLong("resourceSer")).thenReturn(0L);
        PowerMockito.when(rs.getTimestamp("signDate")).thenReturn(timestamp);
        PowerMockito.when(rs.getString("signType")).thenReturn(WorkloadSignature.SignatureTypeEnum.PHYSICIAN.name());

        PowerMockito.when(rs.getLong("workloadId")).thenReturn(12L);
        PowerMockito.when(rs.getString("workerName")).thenReturn("worker1");
        PowerMockito.when(rs.getInt("orderNum")).thenReturn(1);

        List<TreatmentWorkload> treatmentWorkloadList = treatmentWorkloadDAO.queryTreatmentWorkloadListByPatientSer(connection,patientSer,encounterId);
        Assert.assertNotNull(treatmentWorkloadList);
        Assert.assertTrue(treatmentWorkloadList.size() == 1);
    }

    @Test
    public void givenTreatmentWorkloadWhenCreteThenReturnPrimaryKey() throws SQLException {
        TreatmentWorkload treatmentWorkload = new TreatmentWorkload(){{
            setPatientSer(1212L);
            setEncounterId("12");
            setTreatmentDate(new Date());
            setWorkloadPlans(Arrays.asList(new WorkloadPlan(){{
                setPlanId("plan1");
                setDeliveredFractions(2);
                setSelected((byte)1);
                setComment("comment");
            }}));
            setWorkloadSignatures(Arrays.asList(new WorkloadSignature(){{
                setUserName("kevin");
                setResourceName("kevin");
                setResourceSer(121L);
                setSignDate(new Date());
                setSignType(SignatureTypeEnum.OPERATORA);
            }}));
            setWorkloadWorkers(Arrays.asList(new WorkloadWorker(){{
                setWorkerName("Evan");
                setOrderNum(1);
            }}));
        }};
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        Connection connection = ConnectionPool.getConnection();
        PowerMockito.when(connection.prepareStatement(Matchers.anyString(),Matchers.anyInt())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.getString(1)).thenReturn("1");

        PreparedStatement ps2 = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps2);
        PowerMockito.when(ps2.executeBatch()).thenReturn(new int[]{1});

        String r = treatmentWorkloadDAO.create(connection,treatmentWorkload);
        Assert.assertTrue("1".equals(r));
    }

    @Test
    public void givenPatientSerListWhenQueryTotalTreatmentCountThenReturnMap() throws SQLException {
        List<String> patientSerList = Arrays.asList("1212","12123");
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        Connection connection = ConnectionPool.getConnection();
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSerList.get(0));
        PowerMockito.when(rs.getInt("treatmentedCount")).thenReturn(2);
        Map<String,Integer> map = treatmentWorkloadDAO.queryTotalTreatmentCount(connection,patientSerList);
        Assert.assertNotNull(map);
        Assert.assertTrue(2 == map.get(patientSerList.get(0)));
    }

}
