package com.varian.oiscn.base.dynamicform;

import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.common.KeyValuePair;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by gbt1220 on 6/28/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DynamicFormTemplateServiceImp.class, ConnectionPool.class, BasicDataSourceFactory.class})
public class DynamicFormTemplateServiceImpTest {

    private Connection con;

    private DynamicFormTemplateDAO dynamicFormTemplateDAO;

    private DynamicFormTemplateServiceImp dynamicFormTemplateServiceImp;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        dynamicFormTemplateDAO = PowerMockito.mock(DynamicFormTemplateDAO.class);
        PowerMockito.whenNew(DynamicFormTemplateDAO.class).withNoArguments().thenReturn(dynamicFormTemplateDAO);
        dynamicFormTemplateServiceImp = new DynamicFormTemplateServiceImp();
    }

    @Test
    public void givenTemplateIdWhenDaoThrowExceptionThenReturnEmptyString() throws SQLException, IOException {
        String notExistTemplateId = "templateId";
        PowerMockito.when(dynamicFormTemplateDAO.queryTemplateJsonByTemplateIds(con, Arrays.asList(notExistTemplateId))).thenThrow(SQLException.class);
        Map<String, String> jsonList = dynamicFormTemplateServiceImp.queryTemplateJsonByTemplateId(Arrays.asList(notExistTemplateId));
        Assert.assertTrue(jsonList.isEmpty());
    }

    @Test
    public void givenTemplateIdWhenQueryThenReturnTemplateJson() throws SQLException, IOException {
        String templateId = "templateId";
        String json = "jsonString";
        Map<String, String> jsonList = new HashMap<String, String>() {{
            put(templateId, json);
        }};
        PowerMockito.when(dynamicFormTemplateDAO.queryTemplateJsonByTemplateIds(con, Arrays.asList(templateId))).thenReturn(jsonList);
        Assert.assertEquals(jsonList, dynamicFormTemplateServiceImp.queryTemplateJsonByTemplateId(Arrays.asList(templateId)));
    }

    @Test
    public void givenTemplateListWhenDaoThrowExceptionThenReturnEmptyList() throws SQLException {
        List<String> notExistTemplateIds = Arrays.asList("templateId2", "templateId3");
        List<DynamicFormTemplate> dynamicFormTemplateList;
        PowerMockito.when(dynamicFormTemplateDAO.queryTemplateFormListByTemplateIds(con, notExistTemplateIds)).thenThrow(SQLException.class);
        dynamicFormTemplateList = dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(notExistTemplateIds);
        Assert.assertNotNull(dynamicFormTemplateList);
    }

    @Test
    public void givenTemplateListWhenQueryThenReturnTemplateList() throws SQLException {
        List<String> existTemplateIds = Arrays.asList("templateId4", "templateId5");
        List<DynamicFormTemplate> returnList = new ArrayList<>();
        returnList.add(new DynamicFormTemplate() {{
            setTemplateId(existTemplateIds.get(0));
            setTemplateName("templateName4");
        }});
        returnList.add(new DynamicFormTemplate() {{
            setTemplateId(existTemplateIds.get(1));
            setTemplateName("templateName5");
        }});

        PowerMockito.when(dynamicFormTemplateDAO.queryTemplateFormListByTemplateIds(con, existTemplateIds)).thenReturn(returnList);
        List<DynamicFormTemplate> dynamicFormTemplateList = dynamicFormTemplateServiceImp.queryTemplateListByTemplateIds(existTemplateIds);
        Assert.assertThat(returnList, CoreMatchers.equalTo(dynamicFormTemplateList));
    }

    @Test
    public void givenTemplateNamesWhenDaoThrowExceptionThenReturnEmptyList() throws SQLException {
        List<String> notExistTemplateIds = Arrays.asList("templateId6", "templateId7");
        List<KeyValuePair> dynamicFormTemplateNameList;
        PowerMockito.when(dynamicFormTemplateDAO.queryTemplateFormListByTemplateIds(con, notExistTemplateIds)).thenThrow(SQLException.class);
        dynamicFormTemplateNameList = dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(notExistTemplateIds);
        Assert.assertNotNull(dynamicFormTemplateNameList);
    }

    @Test
    public void givenTemplateNamesWhenQueryThenReturnList() throws SQLException {
        List<String> existTemplateIds = Arrays.asList("templateId8", "templateId9");
        List<DynamicFormTemplate> returnList = new ArrayList<>();
        String templateName8 = "templateName8";
        String templateName9 = "templateName9";

        returnList.add(new DynamicFormTemplate() {{
            setTemplateId(existTemplateIds.get(0));
            setTemplateName(templateName8);
        }});
        returnList.add(new DynamicFormTemplate() {{
            setTemplateId(existTemplateIds.get(1));
            setTemplateName(templateName9);
        }});

        List<KeyValuePair> shouldResult = new ArrayList<>();
        shouldResult.add(new KeyValuePair(existTemplateIds.get(0), templateName8));
        shouldResult.add(new KeyValuePair(existTemplateIds.get(1), templateName9));
        List<KeyValuePair> dynamicFormTemplateNameList;
        PowerMockito.when(dynamicFormTemplateDAO.queryTemplateFormListByTemplateIds(con, existTemplateIds)).thenReturn(returnList);
        dynamicFormTemplateNameList = dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(existTemplateIds);
        Assert.assertThat(shouldResult, CoreMatchers.equalTo(dynamicFormTemplateNameList));
    }

}
