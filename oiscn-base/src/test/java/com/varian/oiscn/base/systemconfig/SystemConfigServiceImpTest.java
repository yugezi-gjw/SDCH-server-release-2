package com.varian.oiscn.base.systemconfig;

import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.base.util.MockPreparedStatement;
import com.varian.oiscn.base.util.MockResultSet;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.user.ViewConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.Matchers.anyString;

/**
 * Created by cxq8822 on Sep. 20, 2017
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, MockPreparedStatement.class})
@SuppressStaticInitializationFor("com.varian.oiscn.connection.ConnectionPool")
public class SystemConfigServiceImpTest {

    private Connection con;
    private SystemConfigServiceImp systemConfigServiceImp;

    @Before
    public void setup() throws Exception {
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
    }

    @Test
    public void testQueryConfigValueByNameNoData() throws SQLException {

        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(false);

            systemConfigServiceImp = new SystemConfigServiceImp();
            List<String> actual = systemConfigServiceImp.queryConfigValueByName("abc");
            Assert.assertNotNull(actual);
            Assert.assertTrue(actual.isEmpty());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }

        PowerMockito.when(con.prepareStatement(anyString())).thenThrow(new SQLException());
        List<String> result = systemConfigServiceImp.queryConfigValueByName("abc");
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testQueryConfigValueByNameWithData() throws SQLException {

        try {
            final String value = "1234567";

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(1)).thenReturn(value);

            List<String> expected = new ArrayList<>();
            expected.add(value);

            systemConfigServiceImp = new SystemConfigServiceImp();
            Assert.assertEquals(expected, systemConfigServiceImp.queryConfigValueByName("abc"));
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void testQueryAllConfigValues() throws SQLException {
        String configKey = "name1";
        String configValue = "value1";
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString("name")).thenReturn(configKey);
            PowerMockito.when(rs.getString("value")).thenReturn(configValue);
            Map<String, List<String>> expected = new HashMap<>();
            expected.put(configKey, Arrays.asList(configValue));

            systemConfigServiceImp = new SystemConfigServiceImp();
            Assert.assertEquals(expected, systemConfigServiceImp.queryAllConfigValues());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void testQueryGroupDefaultView() throws SQLException {
        List<GroupDto> groupList = new ArrayList<>();
        groupList.add(new GroupDto("1", "Group1"));
        groupList.add(new GroupDto("2", "Group2"));
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString("category")).thenReturn("default_view").thenReturn("default_banner");
            PowerMockito.when(rs.getString("value")).thenReturn("1234").thenReturn("abcd");
            Map<String, String> expected = new HashMap<>();
//            expected.put(ViewConfig.CATEGORY_VIEW_ID, "1234");
//            expected.put(ViewConfig.CATEGORY_TAB_ID, "abcd");
            expected.put(ViewConfig.CATEGORY_PATIENT_BANNER_VIEW_ID, "1234");

            systemConfigServiceImp = new SystemConfigServiceImp();
            Assert.assertEquals(expected, systemConfigServiceImp.queryGroupDefaultView(groupList));
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void queryRecurringAppointmentTimeLimit() {
        String configValue = "55";
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString("value")).thenReturn(configValue);

            systemConfigServiceImp = new SystemConfigServiceImp();
            Assert.assertEquals(configValue, systemConfigServiceImp.queryRecurringAppointmentTimeLimit());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void queryRecurringAppointmentTimeLimitThrowException() {
        try {
            PowerMockito.when(con.prepareStatement(anyString())).thenThrow(new SQLException());
            systemConfigServiceImp = new SystemConfigServiceImp();
            Assert.assertEquals(SystemConfigConstant.RECURRING_APPOINTMENT_TIME_LIMIT_DEFAULT_VALUE, systemConfigServiceImp.queryRecurringAppointmentTimeLimit());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testQueryConfThrowException() {
        try {
            PowerMockito.when(con.prepareStatement(anyString())).thenThrow(new SQLException());
            systemConfigServiceImp = new SystemConfigServiceImp();
            Assert.assertTrue(systemConfigServiceImp.getDefaultConf().isEmpty());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testQueryConf() {
        try {
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);
            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(anyString())).thenReturn("value");

            systemConfigServiceImp = new SystemConfigServiceImp();
            Assert.assertEquals(1, systemConfigServiceImp.getDefaultConf().size());

            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(anyString())).thenReturn("value");
            Assert.assertEquals(1, systemConfigServiceImp.getFHIRServerConf().size());

            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(anyString())).thenReturn("value");
            Assert.assertEquals(1, systemConfigServiceImp.getHttpClientConf().size());

            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(anyString())).thenReturn("value");
            Assert.assertEquals(1, systemConfigServiceImp.getLocaleConf().size());

            PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
            PowerMockito.when(rs.getString(anyString())).thenReturn("value");
            Assert.assertEquals(1, systemConfigServiceImp.getAuditLog().size());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }
}
