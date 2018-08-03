package com.varian.oiscn.base.vid;

import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.connection.ConnectionPool;
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

/**
 * Created by gbt1220 on 10/16/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({VIDGeneratorServiceImp.class, ConnectionPool.class,
        BasicDataSourceFactory.class, SystemConfigPool.class})
public class VIDGeneratorServiceImpTest {

    private Connection connection;

    private VIDGeneratorDAO dao;

    private VIDGeneratorServiceImp serviceImp;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
        PowerMockito.mockStatic(SystemConfigPool.class);
        dao = PowerMockito.mock(VIDGeneratorDAO.class);
        PowerMockito.whenNew(VIDGeneratorDAO.class).withNoArguments().thenReturn(dao);
        serviceImp = new VIDGeneratorServiceImp();
    }

    @Test
    public void givenWhenNoVIDInDBThenQueryFromPool() throws SQLException {
        String vid = "1";
        String vidPrefix = "V";
        PowerMockito.when(dao.getVID(connection)).thenReturn(null);
        PowerMockito.when(SystemConfigPool.queryStartVIDNumber()).thenReturn(vid);
        PowerMockito.when(dao.addVID(connection, 1)).thenReturn(true);
        PowerMockito.when(SystemConfigPool.queryVIDPrefix()).thenReturn(vidPrefix);
        Assert.assertEquals("V00001", serviceImp.generateVID());
    }

    @Test
    public void givenWhenHasVIDInDBThenQueryFromDB() throws SQLException {
        String vid = "1";
        String vidPrefix = "V";
        PowerMockito.when(dao.getVID(connection)).thenReturn(vid);
        PowerMockito.when(dao.updateVID(connection, 2)).thenReturn(true);
        PowerMockito.when(SystemConfigPool.queryVIDPrefix()).thenReturn(vidPrefix);
        Assert.assertEquals("V00001", serviceImp.generateVID());
    }
}
