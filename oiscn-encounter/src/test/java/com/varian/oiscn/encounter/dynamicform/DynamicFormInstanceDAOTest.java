package com.varian.oiscn.encounter.dynamicform;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.core.common.KeyValuePair;
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
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class DynamicFormInstanceDAOTest {

    UserContext userContext;

    DynamicFormInstanceDAO dynamicFormInstanceDAO;

    @Before
    public void setup(){
        userContext = MockDtoUtil.givenUserContext();
        dynamicFormInstanceDAO = new DynamicFormInstanceDAO(userContext);
    }

    @Test
    public void testCreate() throws SQLException{
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        DynamicFormInstance dynamicFormInstance = new DynamicFormInstance();
        dynamicFormInstance.setEncounterId("1");
        dynamicFormInstance.setPatientSer("30000");
        dynamicFormInstance.setDynamicFormItems(Arrays.asList(new KeyValuePair("Key", "Value")));
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString(), Matchers.anyInt())).thenReturn(ps);
        PreparedStatement ps1 = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps1);
        PowerMockito.when(ps1.executeBatch()).thenReturn(new int[]{1});
        String createdId = "createdId";
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getString(1)).thenReturn(createdId);
        Assert.assertEquals(createdId, dynamicFormInstanceDAO.create(connection, dynamicFormInstance));
    }

    @Test
    public void testUpdate() throws SQLException{
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{1});
        DynamicFormInstance dynamicFormInstance = new DynamicFormInstance();
        dynamicFormInstance.setEncounterId("1");
        dynamicFormInstance.setPatientSer("30000");
        dynamicFormInstance.setDynamicFormItems(Arrays.asList(new KeyValuePair("Key", "Value")));
        Assert.assertTrue(dynamicFormInstanceDAO.update(connection, dynamicFormInstance, "10000"));
    }

    @Test
    public void testQueryByPatientSer() throws Exception{
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true);
        PowerMockito.when(resultSet.getString(1)).thenReturn("1");
        PowerMockito.when(resultSet.getString(2)).thenReturn("{\"id\":\"2\",\"hisId\":null,\"encounterId\":\"2\",\"patientSer\":\"1737\",\"dynamicFormItems\":[{\"key\":\"bingrentiwei\",\"value\":\"俯卧\"},{\"key\":\"teshutiwei\",\"value\":\"\"},{\"key\":\"fuyadian_immbDeviceGroup\",\"value\":\"true\"},{\"key\":\"paomodian_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"gaofenzidingweidian_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"gudingqi1_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"zhenxing_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"gudingqi2_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"qita_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"toumo_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"toujingjianmo_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"rumo_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"fumo_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"yinshui_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"yitimo_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"daitou_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"penqiang_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"bieniao_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"jingxiongmo_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"jingxiongmotice_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"jingxiongmoeshang_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"jingxiongmohuxidongdu_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"xiongmo_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"xiongmotice_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"xiongmoeshang_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"xiongmohuxidongdu_immbDeviceGroup\",\"value\":\"\"},{\"key\":\"teshushuoming\",\"value\":\"\"},{\"key\":\"zhongliubuwei\",\"value\":\"\"},{\"key\":\"saomiaobuwei\",\"value\":\"\"},{\"key\":\"congtoudingzhisuogugutou_scanSiteGroup\",\"value\":\"true\"},{\"key\":\"congtoudingzhiqiguanfencha_scanSiteGroup\",\"value\":\"\"},{\"key\":\"jingxiongbu_scanSiteGroup\",\"value\":\"\"},{\"key\":\"xiongbu_scanSiteGroup\",\"value\":\"\"},{\"key\":\"shangfu_scanSiteGroup\",\"value\":\"\"},{\"key\":\"xiafu_scanSiteGroup\",\"value\":\"\"},{\"key\":\"fuke_scanSiteGroup\",\"value\":\"\"},{\"key\":\"changgui_scanSiteGroup\",\"value\":\"\"},{\"key\":\"fukegongjing_scanSiteGroup\",\"value\":\"\"},{\"key\":\"ruxian_scanSiteGroup\",\"value\":\"\"},{\"key\":\"sizhi_scanSiteGroup\",\"value\":\"\"},{\"key\":\"zengqiang_ctScanGroup\",\"value\":\"\"},{\"key\":\"fourDCT_ctScanGroup\",\"value\":\"\"},{\"key\":\"fangxiang\",\"value\":\"\"},{\"key\":\"ctsaomiaocengjv\",\"value\":\"\"},{\"key\":\"ctqitacengjv\",\"value\":\"\"},{\"key\":\"ctsaomiaoteshuyaoqiu\",\"value\":\"\"},{\"key\":\"MRICheckbox\",\"value\":\"true\"},{\"key\":\"zengqiang_mriScanGroup\",\"value\":\"true\"},{\"key\":\"t1_mriScanGroup\",\"value\":\"\"},{\"key\":\"t2_mriScanGroup\",\"value\":\"\"},{\"key\":\"mrisaomiaocengjv\",\"value\":\"5mm\"},{\"key\":\"mriqitacengjv\",\"value\":\"\"},{\"key\":\"mrisaomiaoteshuyaoqiu\",\"value\":\"\"}]}");
        Assert.assertEquals("1", dynamicFormInstanceDAO.queryByPatientSer(connection, 30000L).getId());
    }

    @Test
    public void testSelectFieldValueByPatientSerAndName() throws SQLException{
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        String patientSer = "30000";
        String itemValue = "ItemValue";
        PowerMockito.when(resultSet.getString("patientSer")).thenReturn(patientSer);
        PowerMockito.when(resultSet.getString("itemValue")).thenReturn(itemValue);
        Assert.assertEquals(itemValue, dynamicFormInstanceDAO.selectFieldValueByPatientSerAndName(connection, patientSer, "fieldName"));
    }

    @Test
    public void testSelectFieldNameValuePairsByPatientSerList() throws SQLException{
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        String patientSer = "30000";
        String itemKey = "ItemKey";
        String itemValue = "ItemValue";
        PowerMockito.when(resultSet.getString("patientSer")).thenReturn(patientSer);
        PowerMockito.when(resultSet.getString("itemKey")).thenReturn(itemKey);
        PowerMockito.when(resultSet.getString("itemValue")).thenReturn(itemValue);
        Map<String, Map<String, String>> map = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put(itemKey, itemValue);
        map.put(patientSer, innerMap);
        Assert.assertEquals(map, dynamicFormInstanceDAO.selectFieldNameValuePairsByPatientSerList(connection, Arrays.asList(patientSer), Arrays.asList("fieldName1")));
    }
}
