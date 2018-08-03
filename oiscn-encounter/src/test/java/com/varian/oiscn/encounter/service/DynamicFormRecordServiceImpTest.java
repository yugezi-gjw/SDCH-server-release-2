package com.varian.oiscn.encounter.service;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.DynamicFormRecordDAO;
import com.varian.oiscn.encounter.dynamicform.DynamicFormRecord;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, DynamicFormRecordServiceImp.class})
public class DynamicFormRecordServiceImpTest {
    private DynamicFormRecordDAO dynamicFormRecordDAO;
    private Connection connection;
    private DynamicFormRecordServiceImp serviceImp;

    @Before
    public void setup() throws Exception{
        dynamicFormRecordDAO = PowerMockito.mock(DynamicFormRecordDAO.class);
        PowerMockito.whenNew(DynamicFormRecordDAO.class).withAnyArguments().thenReturn(dynamicFormRecordDAO);
        serviceImp = new DynamicFormRecordServiceImp(new UserContext());
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
    }

    @Test
    public void givenARecordThenReturnAutoGeneratedId() throws SQLException{
        DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
        PowerMockito.when(dynamicFormRecordDAO.create(connection, dynamicFormRecord)).thenReturn("id");
        String id = serviceImp.create(dynamicFormRecord);
        Assert.assertEquals("id", id);
    }

    @Test
    public void givenHisIdAndEncounterIdThenReturnDynamicFormRecordList() throws SQLException{
        List<DynamicFormRecord> dynamicFormRecordList = new ArrayList<>();
        Long patientSer = 1234L;
        Long encounterId =121L ;
        PowerMockito.when(dynamicFormRecordDAO.queryDynamicFormRecordInfoByEncounterId(connection, patientSer, encounterId)).thenReturn(dynamicFormRecordList);
        Assert.assertEquals(dynamicFormRecordList, serviceImp.queryDynamicFormRecordInfoByEncounterId(patientSer, encounterId));
    }

    @Test
    public void givenIdThenReturnDynamicFormRecordInfo() throws SQLException{
        String id = "id";
        String dynamicFormRecordInfo = "dynamicFormRecordInfo";
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("recordInfo", dynamicFormRecordInfo);
        PowerMockito.when(dynamicFormRecordDAO.queryDynamicFormRecordInfoById(connection, id)).thenReturn(resultMap);
        Assert.assertEquals(dynamicFormRecordInfo, serviceImp.queryDynamicFormRecordInfoById(id).get("recordInfo"));
    }
}
