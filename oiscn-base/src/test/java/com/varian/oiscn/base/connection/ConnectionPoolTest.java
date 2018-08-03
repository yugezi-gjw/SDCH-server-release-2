package com.varian.oiscn.base.connection;

/**
 * Created by gbt1220 on 3/27/2017.
 */

import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.connection.ConnectionParam;
import com.varian.oiscn.connection.ConnectionPool;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, DriverManager.class, BasicDataSourceFactory.class})
public class ConnectionPoolTest {


    private static String nullOrEmpty2DefaultValue(String value, String defaultValue) {
        String tmp = StringUtils.trimToEmpty(value);
        if (StringUtils.isEmpty(tmp)) {
            return defaultValue;
        }
        return tmp;
    }

    @Before
    public void setup() {
        initConnectionPool();
    }

    @Test
    public void givenValidParamsWhenThenReturnValidConnection() throws SQLException {
        Connection connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
        Assert.assertEquals(connection, ConnectionPool.getConnection());
    }

    @Test
    public void givenWhenThrowSQLExceptionThenReturnNull() throws SQLException {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        DataSource dataSource = PowerMockito.mock(BasicDataSource.class);
        PowerMockito.when(dataSource.getConnection()).thenThrow(SQLException.class);
        Assert.assertNull(ConnectionPool.getConnection());
    }

    private void initConnectionPool() {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        BasicDataSource dataSource = PowerMockito.mock(BasicDataSource.class);
        try {
            Properties properties = new Properties();
            properties.setProperty("driverClassName", nullOrEmpty2DefaultValue(ConnectionParam.getDRIVER(), "org.postgresql.Driver"));
            properties.setProperty("username", nullOrEmpty2DefaultValue(ConnectionParam.getUSER(), "varian"));
            properties.setProperty("password", nullOrEmpty2DefaultValue(ConnectionParam.getPASSWORD(), "V@rian01"));
            properties.setProperty("url", nullOrEmpty2DefaultValue(ConnectionParam.getURL(), "jdbc:postgresql://localhost:5432/Qin"));
            properties.setProperty("maxTotal", nullOrEmpty2DefaultValue(ConnectionParam.getMAXTOTAL(), "8"));
            properties.setProperty("initialSize", nullOrEmpty2DefaultValue(ConnectionParam.getINITIALSIZE(), "8"));
            properties.setProperty("maxIdle", nullOrEmpty2DefaultValue(ConnectionParam.getMAXIDLE(), "8"));
            properties.setProperty("maxWaitMillis", nullOrEmpty2DefaultValue(ConnectionParam.getMAXWAITMILLIS(), "5000"));
            PowerMockito.when(BasicDataSourceFactory.createDataSource(properties)).thenReturn(dataSource);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        PowerMockito.mockStatic(ConnectionPool.class);
    }
}
