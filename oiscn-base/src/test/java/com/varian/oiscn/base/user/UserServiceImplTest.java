/**
 * 
 */
package com.varian.oiscn.base.user;

import com.varian.oiscn.base.user.profile.ProfileDAO;
import com.varian.oiscn.base.user.profile.PropertyEntity;
import com.varian.oiscn.base.user.profile.PropertyEnum;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author bnp6208
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, UserServiceImpl.class, BasicDataSourceFactory.class})
public class UserServiceImplTest {

	private ProfileDAO dao;
    private UserServiceImpl service;
    private Connection con;
    
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(BasicDataSourceFactory.class);
        dao = PowerMockito.mock(ProfileDAO.class);
        PowerMockito.whenNew(ProfileDAO.class).withAnyArguments().thenReturn(dao);
        PowerMockito.mockStatic(ConnectionPool.class);
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        service = new UserServiceImpl(null, null);
	}

	/**
	 * Test method for {@link com.varian.oiscn.base.user.UserServiceImpl#getProperty(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetProperty() {
		String value = "abcd";
		String userId = "liguozhu";
		PropertyEnum property = PropertyEnum.SHOW_DONE_HINT;
		
        try {
            PowerMockito.when(dao.queryUserProperty(con, userId, property.getName())).thenReturn(value);
        } catch (SQLException e) {
            Assert.fail();
        }
        String actual = service.getProperty(userId, property.getName());
        Assert.assertEquals(value, actual);
	}

	/**
	 * Test method for {@link com.varian.oiscn.base.user.UserServiceImpl#getAllProperties(java.lang.String)}.
	 */
	@Test
	public void testGetAllProperties() {
		Map<String, String> map = new HashMap<String, String>();
		String userId = "liguozhu";
		try {
			PowerMockito.when(dao.queryUserAllProperties(con, userId)).thenReturn(map);
		} catch (SQLException e) {
			Assert.fail();
		}
		Map<String, String> actual = service.getAllProperties(userId);
		Assert.assertEquals(PropertyEnum.values().length, actual.size());
	}

	/**
	 * Test method for {@link com.varian.oiscn.base.user.UserServiceImpl#updateProperty(java.lang.String, com.varian.oiscn.base.user.profile.PropertyEntity)}.
	 */
	@Test
	public void testUpdateProperty() {
		String value = "abcd";
		String userId = "liguozhu";
		PropertyEntity property = new PropertyEntity(PropertyEnum.SHOW_DONE_HINT.getName(), value);

		try {
			PowerMockito.when(dao.updateUserProperty(con, userId, PropertyEnum.SHOW_DONE_HINT.getName(),value)).thenReturn(1);
		} catch (SQLException e) {
			Assert.fail();
		}
		boolean actual = service.updateProperty(userId, property);
		Assert.assertTrue(actual);
	}

}
