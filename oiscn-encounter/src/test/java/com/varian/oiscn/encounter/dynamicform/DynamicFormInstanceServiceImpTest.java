package com.varian.oiscn.encounter.dynamicform;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.DynamicFormRecordDAO;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.view.DynamicFormItemsAndTemplateInfo;
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
import java.util.*;

/**
 * Created by gbt1220 on 7/4/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DynamicFormInstanceServiceImp.class, ConnectionPool.class, BasicDataSourceFactory.class})
public class DynamicFormInstanceServiceImpTest {
    private Connection con;
    private DynamicFormInstanceDAO dynamicFormInstanceDAO;

    private DynamicFormRecordDAO dynamicFormRecordDAO;

    private EncounterDAO encounterDAO;

    private DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        dynamicFormInstanceDAO = PowerMockito.mock(DynamicFormInstanceDAO.class);
        PowerMockito.whenNew(DynamicFormInstanceDAO.class).withAnyArguments().thenReturn(dynamicFormInstanceDAO);
        dynamicFormRecordDAO = PowerMockito.mock(DynamicFormRecordDAO.class);
        PowerMockito.whenNew(DynamicFormRecordDAO.class).withAnyArguments().thenReturn(dynamicFormRecordDAO);
        encounterDAO = PowerMockito.mock(EncounterDAO.class);
        PowerMockito.whenNew(EncounterDAO.class).withAnyArguments().thenReturn(encounterDAO);
        PowerMockito.mockStatic(ConnectionPool.class);
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        dynamicFormInstanceServiceImp = new DynamicFormInstanceServiceImp(new UserContext());
    }

    @Test
    public void givenPatientIdWhenDaoThrowExceptionThenReturnNull() throws SQLException {
        Long patientId = 12345324L;
        PowerMockito.when(dynamicFormInstanceDAO.queryByPatientSer(con, patientId)).thenThrow(SQLException.class);
        Assert.assertNull(dynamicFormInstanceServiceImp.queryByPatientSer(patientId));
    }

    @Test
    public void givenPatientIdWhenQueryThenReturnDynamicFormInstance() throws SQLException {
        Long patientId = 12345324L;
        DynamicFormInstance instance = givenADynamicFormInstance();
        PowerMockito.when(dynamicFormInstanceDAO.queryByPatientSer(con, patientId)).thenReturn(instance);
        Assert.assertEquals(instance, dynamicFormInstanceServiceImp.queryByPatientSer(patientId));
    }

    @Test
    public void givenPatientIdAndItemsWhenSaveOrUpdateAndDaoFoundInstanceThenUpdate() throws Exception {
        Long patientId = 12345324L;
        DynamicFormInstance instance = givenADynamicFormInstance();
        Encounter encounter = new Encounter();
        encounter.setId("1121");
        PowerMockito.when(encounterDAO.queryByPatientSer(con, patientId)).thenReturn(encounter);
        PowerMockito.when(dynamicFormInstanceDAO.queryByPatientSer(con, patientId)).thenReturn(instance);
        List<KeyValuePair> items = givenItems();
        items.add(new KeyValuePair("item3", "value3"));
        DynamicFormItemsAndTemplateInfo dynamicFormItemsAndTemplateInfo = new DynamicFormItemsAndTemplateInfo();
        dynamicFormItemsAndTemplateInfo.setRecordInfo(items);
        dynamicFormItemsAndTemplateInfo.setTemplateInfo("templateInfo");
        PowerMockito.when(dynamicFormInstanceDAO.update(con, instance, patientId.toString())).thenReturn(true);
        DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
        dynamicFormRecord.setDynamicFormRecordInfo(items);
        dynamicFormRecord.setPatientSer(patientId);
        String templateId = "templateId";
        dynamicFormRecord.setTemplateId(templateId);
        String carePathInstanceId = "carePathInstanceId";
        dynamicFormRecord.setCarePathInstanceId(carePathInstanceId);
        PowerMockito.when(dynamicFormRecordDAO.create(con, dynamicFormRecord)).thenReturn("1");
        Assert.assertEquals(instance.getId(), dynamicFormInstanceServiceImp.saveOrUpdate(patientId, dynamicFormItemsAndTemplateInfo, templateId, carePathInstanceId, "existingDynamicFormRecordId"));
    }

    @Test
    public void givenPatientSerAndFieldNameWhenQueryFieldValueByHisIdAndFieldNameThenReturnString() throws SQLException {
        String patientSer = "12121";
        String activityCode = "PlaceImmobilizationAndCTOrder";
        String templateId = "PlaceImmobilizationAndCTOrder";
        PowerMockito.when(dynamicFormInstanceDAO.selectFieldValueByPatientSerAndName(con, patientSer, activityCode + ".templateId")).thenReturn(templateId);
        String returnTmpId = dynamicFormInstanceServiceImp.queryFieldValueByPatientSerListAndFieldName(patientSer, activityCode + ".templateId");
        Assert.assertNotNull(returnTmpId);
        Assert.assertTrue(templateId.equals(returnTmpId));
    }

    @Test
    public void testQueryFieldValueByPatientSerListAndFieldNameByHisIdListAndFieldNameThenReturnMap() throws SQLException {
        List<String> patientSerList = Arrays.asList("12121","12122");
        String fieldName = "select_planSystem";
        Map<String,String> map = new HashMap<>();
        map.put(patientSerList.get(0),"TOMO");
        map.put(patientSerList.get(1),"Eclipse");
        PowerMockito.when(dynamicFormInstanceDAO.selectFieldValueByPatientSerListAndName(con,patientSerList,fieldName)).thenReturn(map);
        Map<String,String> r = dynamicFormInstanceServiceImp.queryFieldValueByPatientSerListAndFieldName(patientSerList,fieldName);
        Assert.assertTrue(r.equals(map));
    }

    private DynamicFormInstance givenADynamicFormInstance() {
        DynamicFormInstance instance = new DynamicFormInstance();
        instance.setId("1");
        instance.setHisId("hisId");
        instance.setEncounterId("1");
        instance.setDynamicFormItems(givenItems());
        return instance;
    }

    private List<KeyValuePair> givenItems() {
        List<KeyValuePair> itemKeyList = new ArrayList<>();
        itemKeyList.add(new KeyValuePair("item1", "value1"));
        itemKeyList.add(new KeyValuePair("item2", "value2"));
        return itemKeyList;
    }
}
