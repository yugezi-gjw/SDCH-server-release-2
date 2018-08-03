package com.varian.oiscn.base.dynamicform;

import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.base.util.MockPreparedStatement;
import com.varian.oiscn.base.util.MockResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by BHP9696 on 2017/7/28.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DynamicFormTemplateDAO.class, Paths.class, Files.class, MockPreparedStatement.class})
public class DynamicFormTemplateDAOTest {
    private Connection connection;
    private DynamicFormTemplateDAO dynamicFormTemplateDAO;

    @Before
    public void setup() {
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        dynamicFormTemplateDAO = new DynamicFormTemplateDAO();
    }

    @Test
    public void givenTemplateIdsWhenQueryTemplateFormListByTemplateIdsThenReturnObjecs() throws SQLException {
        List<String> templateIds = Arrays.asList("templateId1", "templateId2");
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Date date = new Date(new java.util.Date().getTime());
        PowerMockito.when(rs.getString("id")).thenReturn("1").thenReturn("2");
        PowerMockito.when(rs.getString("templateid")).thenReturn("templateId1").thenReturn("templateId2");
        PowerMockito.when(rs.getString("templatename")).thenReturn("templatename1").thenReturn("templatename2");
        PowerMockito.when(rs.getString("templatepath")).thenReturn("config\\template\\templateId1.json").thenReturn("config\\template\\templateId2.json");
        PowerMockito.when(rs.getDate("createddate")).thenReturn(date).thenReturn(date);
        PowerMockito.when(rs.getString("lastupdateduser")).thenReturn("lastupdateduser").thenReturn("templateId2");
        PowerMockito.when(rs.getDate("lastupdateddate")).thenReturn(date).thenReturn(date);
        List<DynamicFormTemplate> dynamicFormTemplateList = dynamicFormTemplateDAO.queryTemplateFormListByTemplateIds(connection, templateIds);

        Assert.assertTrue(dynamicFormTemplateList.size() == 2);
        Assert.assertTrue(dynamicFormTemplateList.get(0).getTemplateId().equals(templateIds.get(0)));
        Assert.assertTrue(dynamicFormTemplateList.get(1).getTemplateId().equals(templateIds.get(1)));
    }

    @Test
    public void givenTemplateIdWhenQueryTemplateJsonByTemplateIdThenReturnJsonStr() throws SQLException, IOException {
        String file = "config\\template\\templateId1.json";
        List<String> templateIds = Arrays.asList("templateId1");
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        Date date = new Date(new java.util.Date().getTime());
        PowerMockito.when(rs.getString("id")).thenReturn("1");
        PowerMockito.when(rs.getString("templateid")).thenReturn("templateId1");
        PowerMockito.when(rs.getString("templatename")).thenReturn("templatename1");
        PowerMockito.when(rs.getString("templatepath")).thenReturn(file);
        PowerMockito.when(rs.getDate("createddate")).thenReturn(date).thenReturn(date);
        PowerMockito.when(rs.getString("lastupdateduser")).thenReturn("lastupdateduser");
        PowerMockito.when(rs.getDate("lastupdateddate")).thenReturn(date);

        PowerMockito.mockStatic(Paths.class);
        Path path = Paths.get(file);
        PowerMockito.when(Paths.get(file)).thenReturn(path);
        PowerMockito.mockStatic(Files.class);
        PowerMockito.when(Files.readAllBytes(path)).thenReturn(new byte[]{123, 34, 112, 114, 111, 112, 101, 114, 116, 105, 101, 115, 34, 58, 123, 34, 119, 105, 100, 116, 104, 34, 58, 34, 56, 48, 48, 112, 120, 34, 125, 125});
        Map<String, String> jsonList = dynamicFormTemplateDAO.queryTemplateJsonByTemplateIds(connection, templateIds);
        Assert.assertNotNull(jsonList);
        Assert.assertTrue(jsonList.size() > 0);
    }


}
