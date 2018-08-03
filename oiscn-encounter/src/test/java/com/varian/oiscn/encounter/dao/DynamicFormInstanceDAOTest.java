package com.varian.oiscn.encounter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstance;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceDAO;
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
 * Created by BHP9696 on 2017/7/28.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DynamicFormInstanceDAO.class, MockPreparedStatement.class})
public class DynamicFormInstanceDAOTest {
    private DynamicFormInstanceDAO dynamicFormInstanceDAO;
    private Connection connection;

    @Before
    public void setup() {
        Locale.setDefault(Locale.CHINA);
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        UserContext userContext = PowerMockito.mock(UserContext.class);
        PowerMockito.when(userContext.getName()).thenReturn("admin");
        PowerMockito.when(userContext.getLogin()).thenReturn(new Login(){{
            setUsername("admin");
        }});
        dynamicFormInstanceDAO = new DynamicFormInstanceDAO(userContext);
    }

    @Test
    public void givenPatientSerWhenQueryByHisIdThenReturnObject() throws SQLException {
        Long patientSer = 20170710001L;
        String instanceId = "2";
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString(1)).thenReturn(instanceId);
        PowerMockito.when(rs.getString(2)).thenReturn("{\"id\": 2, \"patientSer\": \"20170710001\", \"encounterId\": \"489\", \"dynamicFormItems\": [{\"key\": \"patientName\", \"value\": \"" + I18nReader.getLocaleValueByKey("DynamicFormInstanceDAOTests.patientName") + "\"}, {\"key\": \"age\", \"value\": \"1\"}, {\"key\": \"reoccurence\", \"value\": \"1\"}, {\"key\": \"birthday\", \"value\": \"2017-06-26\"}, {\"key\": \"gender\", \"value\": \"2\"}, {\"key\": \"part_lung\", \"value\": \"true\"}, {\"key\": \"part_brain\", \"value\": \"\"}, {\"key\": \"part_liver\", \"value\": \"true\"}]}");
        DynamicFormInstance dynamicFormInstance = dynamicFormInstanceDAO.queryByPatientSer(connection, patientSer);
        Assert.assertNotNull(dynamicFormInstance);
        Assert.assertTrue(patientSer.toString().equals(dynamicFormInstance.getPatientSer()));
        Assert.assertTrue(instanceId.equals(dynamicFormInstance.getId()));
    }

    @Test
    public void givenPatientSerWhenQueryByHisIdThenReturnNull() throws SQLException {
        Long patientSer = 12345324L;
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(false);
        DynamicFormInstance dynamicFormInstance = dynamicFormInstanceDAO.queryByPatientSer(connection, patientSer);
        Assert.assertNull(dynamicFormInstance);
    }

    @Test
    public void givenPatientSerAndFieldNameWhenFieldValueByHisIdAndNameThenReturnString() throws SQLException {
        String patientSer = "20170710002";
        String activityCode = "PlaceImmobilizationAndCTOrder";
        String templateId = "CTAndImmobilizationOrderTemplateSwitch";
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSer);
        PowerMockito.when(rs.getString("itemValue")).thenReturn(templateId);
        String tmpId = dynamicFormInstanceDAO.selectFieldValueByPatientSerAndName(connection, patientSer, activityCode + ".templateId");
        Assert.assertNotNull(tmpId);
        Assert.assertTrue(templateId.equals(tmpId));

    }

    @Test
    public void testSelectFieldValueByPatientSerListAndNameThenReturnMap() throws SQLException {
        List<String> patientSerList = Arrays.asList("12121","12122");
        String fieldName = "select_planSystem";
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSerList.get(0));
        PowerMockito.when(rs.getString("itemValue")).thenReturn("TOMO");
        Map<String,String> rmap = dynamicFormInstanceDAO.selectFieldValueByPatientSerListAndName(connection,patientSerList,fieldName);
        Assert.assertNotNull(rmap);
        Assert.assertTrue(rmap.get(patientSerList.get(0)).equals("TOMO"));
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("patientSer")).thenReturn(patientSerList.get(1));
        PowerMockito.when(rs.getString("itemValue")).thenReturn("Eclipse");
        rmap = dynamicFormInstanceDAO.selectFieldValueByPatientSerListAndName(connection,patientSerList,fieldName);
        Assert.assertNotNull(rmap);
        Assert.assertTrue(rmap.get(patientSerList.get(1)).equals("Eclipse"));
    }

    @Test
    public void testCreateDynamicFormInstance() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString(),Matchers.anyInt())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getString(1)).thenReturn("121");


        PreparedStatement ps2 = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps2);
        PowerMockito.when(ps2.executeBatch()).thenReturn(new int[]{1,1});

        DynamicFormInstance dynamicFormInstance = new DynamicFormInstance(){{
            setHisId("HisId");
            setEncounterId("1212");
            setPatientSer("111");
            setDynamicFormItems(Arrays.asList(new KeyValuePair("field1","121212"),new KeyValuePair("field2","121212d")));
        }};
        String pk = dynamicFormInstanceDAO.create(connection,dynamicFormInstance);
        Assert.assertNotNull(pk);
    }

    @Test
    public void testUpdateDynamicFormInstance() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(2);
        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{1,1});
        DynamicFormInstance dynamicFormInstance = new DynamicFormInstance(){{
            setHisId("HisId");
            setEncounterId("1212");
            setPatientSer("111");
            setDynamicFormItems(Arrays.asList(new KeyValuePair("field1","121212"),new KeyValuePair("field2","121212d")));
        }};
        boolean ok = dynamicFormInstanceDAO.update(connection,dynamicFormInstance,"1111");
        Assert.assertTrue(ok);
    }

}
