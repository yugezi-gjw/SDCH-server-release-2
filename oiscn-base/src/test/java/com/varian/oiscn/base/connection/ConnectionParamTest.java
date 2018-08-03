package com.varian.oiscn.base.connection;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.connection.ConnectionParam;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.security.util.EncryptionUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.DriverManager;

/**
 * Created by gbt1220 on 3/27/2017.
 * Modified by bhp9696 on 5/7/2017.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*"})
@PrepareForTest({ConnectionPool.class, DriverManager.class, BasicDataSourceFactory.class, BasicDataSource.class})
public class ConnectionParamTest {
    public static final String USER = "varian";
    public static final String PSD = "S3rv1c3";
    public static final String DATABASE_SERVER = "localhost";
    public static final String QIN = "Qin";
    public static final String DATABASE = QIN;
    public static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String PORT = "5432";
    private Configuration configuration;

    @Before
    public void setup() {
        configuration = new Configuration();
        configuration.getDatabase().setDatabase(DATABASE);
        configuration.getDatabase().setDatabaseServer(DATABASE_SERVER);
        configuration.getDatabase().setDriver(DRIVER);
        try {
            configuration.getDatabase().setUsername(EncryptionUtil.encrypt(USER));
            configuration.getDatabase().setPassword(EncryptionUtil.encrypt(PSD));
        } catch (Exception e) {
            Assert.fail();
        }
        configuration.getDatabase().setPort(PORT);

    }

    @Test
    public void givenConfigurationWhenInitParamThenInitSuccess() {

        BasicDataSource basicDataSource = PowerMockito.mock(BasicDataSource.class);
        try {
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            PowerMockito.when(BasicDataSourceFactory.createDataSource(Mockito.any())).thenReturn(basicDataSource);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        ConnectionParam.initParam(configuration);

        try {
            Assert.assertEquals(EncryptionUtil.encrypt(USER), ConnectionParam.getUSER());
            Assert.assertEquals(EncryptionUtil.encrypt(PSD), ConnectionParam.getPASSWORD());
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertNotNull(ConnectionParam.getURL());
    }
}
