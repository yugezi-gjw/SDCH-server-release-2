package com.varian.oiscn.encounter.confirmpayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockPreparedStatement;
import com.varian.oiscn.encounter.util.MockResultSet;
import com.varian.oiscn.util.I18nReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by BHP9696 on 2017/8/1.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfirmPaymentDAO.class, MockPreparedStatement.class})
public class ConfirmPaymentDAOTest {
    private Connection connection;
    private ConfirmPaymentDAO confirmPaymentDAO;

    @Before
    public void setup() {
        Locale.setDefault(Locale.CHINA);
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        PowerMockito.when(userContext.getName()).thenReturn("admin");
        PowerMockito.when(userContext.getLogin()).thenReturn(new Login(){{
            setUsername("admin");
        }});
        confirmPaymentDAO = new ConfirmPaymentDAO(userContext);
    }

    @Test
    public void givenHisIdWhenSelectConfirmPaymentByPatientSerThenReturnObject() throws SQLException {
        Long patientSer = 9527L;
        String encounterId = "8341";
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("id")).thenReturn("1212121");
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSer.toString());
        PowerMockito.when(rs.getString("encounterId")).thenReturn(encounterId);
        PowerMockito.when(rs.getString("ConfirmPaymentInfo")).thenReturn("{\"id\":null,\"patientSer\":\"9527\",\"encounterId\":\"8341\",\"confirmStatusList\":[{\"activityCode\":\"DoImmobilization\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoImmobilization") + "\",\"status\":0},{\"activityCode\":\"DoCTSim\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoCTSim") + "\",\"status\":0},{\"activityCode\":\"DoRepositioning\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoRepositioning") + "\",\"status\":0}],\"treatmentConfirmStatus\":{\"activityCode\":\"DoFirstTreatment\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoFirstTreatment") + "\",\"totalPaymentCount\":30,\"confirmPaymentCount\":0}}");
        ConfirmPayment confirmPayment = confirmPaymentDAO.selectConfirmPaymentByPatientSer(connection, patientSer);
        Assert.assertNotNull(confirmPayment);
        Assert.assertTrue(patientSer.equals(confirmPayment.getPatientSer()));
        Assert.assertTrue(encounterId.equals(confirmPayment.getEncounterId()));
    }

    @Test
    public void givenNotExistsPatientSerAndEncounterIdWhenSelectConfirmPaymentByHisIdThenReturnNull() throws SQLException {
        Long patientSer = 121212L;
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(false);
        ConfirmPayment confirmPayment = confirmPaymentDAO.selectConfirmPaymentByPatientSer(connection, patientSer);
        Assert.assertNull(confirmPayment);
    }

    @Test
    public void givenPatientSerListWhenSelectConfirmPaymentByPatientSerListThenReturnObjects() throws SQLException {
        List<String> patientSerList = Arrays.asList("201707140002", "201707140003");
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);

        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("id")).thenReturn(patientSerList.get(0)).thenReturn(patientSerList.get(1));
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSerList.get(0));
        PowerMockito.when(rs.getString("ConfirmPaymentInfo")).thenReturn("{\"id\":null,\"patientSer\":\"" + patientSerList.get(0) + "\",\"encounterId\":\"8341\",\"confirmStatusList\":[{\"activityCode\":\"DoImmobilization\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoImmobilization") + "\",\"status\":0},{\"activityCode\":\"DoCTSim\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoCTSim") + "\",\"status\":0},{\"activityCode\":\"DoRepositioning\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoRepositioning") + "\",\"status\":0}],\"treatmentConfirmStatus\":{\"activityCode\":\"DoFirstTreatment\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoFirstTreatment") + "\",\"totalPaymentCount\":30,\"confirmPaymentCount\":0}}")
                .thenReturn("{\"id\":null,\"patientSer\":\"" + patientSerList.get(1) + "\",\"encounterId\":\"8342\",\"confirmStatusList\":[{\"activityCode\":\"DoImmobilization\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoImmobilization") + "\",\"status\":0},{\"activityCode\":\"DoCTSim\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoCTSim") + "\",\"status\":0},{\"activityCode\":\"DoRepositioning\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoRepositioning") + "\",\"status\":0}],\"treatmentConfirmStatus\":{\"activityCode\":\"DoFirstTreatment\",\"activityContent\":\"" + I18nReader.getLocaleValueByKey("ConfirmPaymentDAOTests.DoFirstTreatment") + "\",\"totalPaymentCount\":30,\"confirmPaymentCount\":5}}");
        PreparedStatement ps2 = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement("select c.id,c.hisId,c.encounterId,cs.activityCode,cs.activityContent,cs.totalPaymentCount,cs.confirmPaymentCount from ConfirmPayment as c,TreatmentConfirmStatus as cs,Encounter as e where (c.hisId = ? or c.hisId = ?) and c.encounterId = e.id  and e.status = ? and c.id = cs.confirmPaymentId order by c.hisId")).thenReturn(ps2);
        ResultSet rs2 = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps2.executeQuery()).thenReturn(rs2);
        PowerMockito.when(rs2.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs2.getString("id")).thenReturn("121");
        PowerMockito.when(rs.getInt("totalPaymentCount")).thenReturn(10);
        PowerMockito.when(rs.getInt("confirmPaymentCount")).thenReturn(2);
		List<ConfirmPayment> confirmPaymentList = confirmPaymentDAO.selectConfirmPaymentByPatientSerList(connection, patientSerList);
        Assert.assertNotNull(confirmPaymentList);
        Assert.assertFalse(confirmPaymentList.isEmpty());
        Assert.assertTrue(confirmPaymentList.get(0).getPatientSer().equals(new Long(patientSerList.get(0))));
    }

    @Test
    public void givenPatientSerListWhenQueryConfirmStatusByHisIdThenReturnList() throws SQLException {
        List<String> patientSerList = Arrays.asList("201707140002", "201707140003");
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSerList.get(0));
        PowerMockito.when(rs.getString("encounterId")).thenReturn("123");
        PowerMockito.when(rs.getInt("status")).thenReturn(1);
        PowerMockito.when(rs.getString("activityCode")).thenReturn("DoCT");
        List<ConfirmPayment> confirmPaymentList = confirmPaymentDAO.queryConfirmStatusByPatientSerList(connection,patientSerList,"DoCT");
        Assert.assertNotNull(confirmPaymentList);
        Assert.assertTrue(confirmPaymentList.size() == 1);
    }

    @Test
    public void givenPatientSerListWhenQueryTreatmentConfirmStatusByHisIdThenReturnList() throws SQLException {
        List<String> patientSerList = Arrays.asList("201707140002", "201707140003");
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSerList.get(0));
        PowerMockito.when(rs.getString("encounterId")).thenReturn("123");
        PowerMockito.when(rs.getInt("confirmPaymentCount")).thenReturn(2);
        PowerMockito.when(rs.getInt("totalPaymentCount")).thenReturn(30);
        PowerMockito.when(rs.getString("activityCode")).thenReturn("DoTreatment");
        List<ConfirmPayment> confirmPaymentList = confirmPaymentDAO.queryTreatmentConfirmStatusByPatientSerList(connection,patientSerList,"DoTreatment");
        Assert.assertNotNull(confirmPaymentList);
        Assert.assertTrue(confirmPaymentList.size() == 1);
    }

    @Test
    public void givenPatientSerListAndDeviceIdWhenQueryTreatmentNumForHIsIdsThenReturnMap() throws SQLException, ParseException {
        List<String> patientSerList = Arrays.asList("201707140002", "201707140003");
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSerList.get(0));
        PowerMockito.when(rs.getInt("treatmentNum")).thenReturn(2);

        java.util.Date startDate = DateUtil.parse(DateUtil.getCurrentDate());

        java.util.Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        java.util.Date tomorrowDate = calendar.getTime();

        Map<String,Integer> map = confirmPaymentDAO.queryTreatmentNumForPatientSerList(connection,patientSerList,"1213",startDate,tomorrowDate);
        Assert.assertNotNull(map);
        Assert.assertTrue(2 == map.get(patientSerList.get(0)));
    }

    @Test
    public void testCreateConfirmPayment() throws SQLException {
        ConfirmPayment confirmPayment  = new ConfirmPayment(){{
           setEncounterId("1212");
           setPatientSer(121212L);
           setConfirmStatusList(Arrays.asList(new ConfirmStatus(){{
               setStatus(1);
               setCarePathInstanceId(2L);
           }}));
           setTreatmentConfirmStatus(new TreatmentConfirmStatus(){{
               setTotalPaymentCount(10);
               setConfirmPaymentCount(5);
           }});
        }};
        String createPkId = "12";
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString(),Matchers.anyInt())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString(1)).thenReturn(createPkId);

        PreparedStatement ps2 = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps2);
        PowerMockito.when(ps2.executeBatch()).thenReturn(new int[]{1});
        PowerMockito.when(ps2.executeUpdate()).thenReturn(1);

        Assert.assertTrue(createPkId.equals(confirmPaymentDAO.create(connection,confirmPayment)));
    }


    @Test
    public void testUpdateConfirmPayment() throws SQLException {
        ConfirmPayment confirmPayment  = new ConfirmPayment(){{
            setEncounterId("1212");
            setConfirmStatusList(Arrays.asList(new ConfirmStatus(){{
                setStatus(1);
                setCarePathInstanceId(2L);
            }},new ConfirmStatus(){{
                setStatus(1);
                setCarePathInstanceId(2L);
            }}));
            setTreatmentConfirmStatus(new TreatmentConfirmStatus(){{
                setTotalPaymentCount(10);
                setConfirmPaymentCount(5);
            }});
        }};
        String createPkId = "12";
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);

        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{1});

        Assert.assertTrue(confirmPaymentDAO.update(connection,confirmPayment,createPkId));
    }

    @Test
    public void testGetTableName(){
        Assert.assertTrue("ConfirmPayment".equals(confirmPaymentDAO.getTableName()));
    }

    @Test
    public void testGetJsonbColumnName(){
        Assert.assertTrue("".equals(confirmPaymentDAO.getJsonbColumnName()));
    }
}
