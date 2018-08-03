/**
 * 
 */
package com.varian.oiscn.base.user.profile;

import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.base.util.MockPreparedStatement;
import com.varian.oiscn.base.util.MockResultSet;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;

/**
 * @author bnp6208
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConnectionPool.class, ProfileDAO.class, MockDatabaseConnection.class, MockPreparedStatement.class, MockResultSet.class })
public class ProfileDAOTest {

	private Connection connection;
    private UserContext userContext;
    private Login login;
    
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		connection = PowerMockito.mock(MockDatabaseConnection.class);
        userContext = PowerMockito.mock(UserContext.class);
        login = PowerMockito.mock(Login.class);
        PowerMockito.when(login.getUsername()).thenReturn("sysadmin");
        PowerMockito.when(userContext.getLogin()).thenReturn(login);
        PowerMockito.when(userContext.getName()).thenReturn("sysadmin");
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#queryUserProperty(java.sql.Connection, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testQueryUserProperty() {
		connection = PowerMockito.mock(MockDatabaseConnection.class);
		String userId = PowerMockito.mock(String.class);
		String property = "ShowDoneHint";
		try {
			ProfileDAO dao = PowerMockito.mock(ProfileDAO.class);
			PowerMockito.when(dao.queryUserProperty(connection, userId, property)).thenCallRealMethod();
			dao.queryUserProperty(connection, userId, property);
			Mockito.verify(dao).queryProperty(connection, userId, UserRoleEnum.LOGIN_USER, property);
		} catch (SQLException e) {
			Assert.fail("SQLException Test");
		}
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#queryUserAllProperties(java.sql.Connection, java.lang.String)}
	 * .
	 */
	@Test
	public void testQueryUserAllProperties() {
		connection = PowerMockito.mock(MockDatabaseConnection.class);
		String userId = PowerMockito.mock(String.class);
		try {
			ProfileDAO dao = PowerMockito.mock(ProfileDAO.class);
			PowerMockito.when(dao.queryUserAllProperties(connection, userId)).thenCallRealMethod();
			dao.queryUserAllProperties(connection, userId);
			Mockito.verify(dao).queryAllProperties(connection, userId, UserRoleEnum.LOGIN_USER);
		} catch (SQLException e) {
			Assert.fail("SQLException Test");
		}
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#updateUserProperty(java.sql.Connection, java.lang.String, java.lang.String,java.lang.String)}
	 * .
	 */
	@Test
	public void testUpdateUserProperty() {
		connection = PowerMockito.mock(MockDatabaseConnection.class);
		String userId = PowerMockito.mock(String.class);
		String property = "ShowDoneHint";
		try {
			ProfileDAO dao = PowerMockito.mock(ProfileDAO.class);
			PowerMockito.when(dao.updateUserProperty(connection, userId, property,"true")).thenCallRealMethod();
			dao.updateUserProperty(connection, userId, property,"true");
			Mockito.verify(dao).updateProperty(connection, userId, UserRoleEnum.LOGIN_USER, property,"true");
		} catch (SQLException e) {
			Assert.fail("SQLException Test");
		}
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#updatePatientProperty(java.sql.Connection, java.lang.String, java.lang.String,java.lang.String)}
	 * .
	 */
	@Test
	public void testUpdatePatientProperty() {
		connection = PowerMockito.mock(MockDatabaseConnection.class);
		String userId = PowerMockito.mock(String.class);
		String property = "ShowDoneHint";
		String propertyValue = "true";
		try {
			ProfileDAO dao = PowerMockito.mock(ProfileDAO.class);
			PowerMockito.when(dao.updatePatientProperty(connection, userId, property,propertyValue)).thenCallRealMethod();
			dao.updatePatientProperty(connection, userId, property,propertyValue);
			Mockito.verify(dao).updateProperty(connection, userId, UserRoleEnum.PATIENT, property,propertyValue);
		} catch (SQLException e) {
			Assert.fail("SQLException Test");
		}
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#queryPatientProperty(java.sql.Connection, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testQueryPatientProperty() {
		connection = PowerMockito.mock(MockDatabaseConnection.class);
		String userId = PowerMockito.mock(String.class);
		String property = "ShowDoneHint";
		try {
			ProfileDAO dao = PowerMockito.mock(ProfileDAO.class);
			PowerMockito.when(dao.queryPatientProperty(connection, userId, property)).thenCallRealMethod();
			dao.queryPatientProperty(connection, userId, property);
			Mockito.verify(dao).queryProperty(connection, userId, UserRoleEnum.PATIENT, property);
		} catch (SQLException e) {
			Assert.fail("SQLException Test");
		}
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#queryPatientAllProperties(java.sql.Connection, java.lang.String)}
	 * .
	 */
	@Test
	public void testQueryPatientAllProperties() {
		connection = PowerMockito.mock(MockDatabaseConnection.class);
		String userId = PowerMockito.mock(String.class);
		try {
			ProfileDAO dao = PowerMockito.mock(ProfileDAO.class);
			PowerMockito.when(dao.queryPatientAllProperties(connection, userId)).thenCallRealMethod();
			dao.queryPatientAllProperties(connection, userId);
			Mockito.verify(dao).queryAllProperties(connection, userId, UserRoleEnum.PATIENT);
		} catch (SQLException e) {
			Assert.fail("SQLException Test");
		}
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#queryProperty(java.sql.Connection, java.lang.String, com.varian.oiscn.base.user.profile.UserRoleEnum, java.lang.String}
	 * .
	 */
		@Test
		public void testQueryProperty() {
		String value = "abcd";
		String userId = "liguozhu";

        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);

            // no data
            PowerMockito.when(rs.next()).thenReturn(true, false);
            PowerMockito.when(rs.getString("propertyValue")).thenReturn(value);

            ProfileDAO dao = new ProfileDAO();
            String actual = dao.queryProperty(con, userId, UserRoleEnum.LOGIN_USER, "ShowDoneHint");
           Assert.assertEquals(value, actual);
        } catch (SQLException e) {
            Assert.fail();
        }
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#queryAllProperties(java.sql.Connection, java.lang.String, com.varian.oiscn.base.user.profile.UserRoleEnum)}
	 * .
	 */
	@Test
	public void testQueryAllProperties() {
		String userId = "liguozhu";
        Map<String, String> propertyList = new HashMap<>();
        propertyList.put("aaa", "111");
        propertyList.put("bbb", "222");
        propertyList.put("ccc", "333");

        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);

            ResultSet rs = PowerMockito.mock(MockResultSet.class);
            PowerMockito.when(ps.executeQuery()).thenReturn(rs);

            // no data
            PowerMockito.when(rs.next()).thenReturn(true, true, true, false);
            PowerMockito.when(rs.getString("propertyName")).thenReturn("aaa", "bbb", "ccc");
            PowerMockito.when(rs.getString("propertyValue")).thenReturn("111", "222", "333");


            ProfileDAO dao = new ProfileDAO();
            Map<String, String> actualMap = dao.queryAllProperties(con, userId, UserRoleEnum.LOGIN_USER);
            for (Map.Entry<String, String> entry : propertyList.entrySet()) {
                String key = entry.getKey();
                String actual = actualMap.get(key);
                Assert.assertEquals(entry.getValue(), actual);
            }
        } catch (SQLException e) {
            Assert.fail();
        }
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#updateProperty(java.sql.Connection, java.lang.String, com.varian.oiscn.base.user.profile.UserRoleEnum, java.lang.String,java.lang.String)}
	 * .
	 */
	@Test
	public void testUpdateProperty() {
		String value = "abcd";
		String userId = "liguozhu";
		String property = "ShowDoneHint";
		String propertyValue = "true";
        try {
            Connection con = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);

            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(con.prepareStatement(anyString())).thenReturn(ps);
            PowerMockito.doNothing().when(ps).setString(anyInt(), anyString());
            PowerMockito.doNothing().when(ps).setInt(anyInt(), anyInt());
            PowerMockito.doNothing().when(ps).setTimestamp(anyInt(), anyObject());
            PowerMockito.when(ps.executeUpdate()).thenReturn(1);
            PowerMockito.when(ps.execute()).thenReturn(true);
            
            ProfileDAO dao = new ProfileDAO();
            int actual = dao.updateProperty(con, userId, UserRoleEnum.LOGIN_USER, property,propertyValue);
            Assert.assertEquals(1, actual);
        } catch (SQLException e) {
            Assert.fail();
        }
	}

	/**
	 * Test method for
	 * {@link com.varian.oiscn.base.user.profile.ProfileDAO#queryValuesByProperties(Connection, List, String, UserRoleEnum)}
	 * .
	 */
	@Test
	public void testQueryValuesByProperties(){
		try {
			Connection con = PowerMockito.mock(MockDatabaseConnection.class);
			PowerMockito.mockStatic(ConnectionPool.class);
			PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
			PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
			PowerMockito.when(con.prepareStatement(Matchers.anyString())).thenReturn(ps);
			ResultSet rs = PowerMockito.mock(MockResultSet.class);
			PowerMockito.when(ps.executeQuery()).thenReturn(rs);
			PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
			PowerMockito.when(rs.getString("propertyName")).thenReturn("p1");
			PowerMockito.when(rs.getString("propertyValue")).thenReturn("p1Value");
			ProfileDAO dao = new ProfileDAO();
			Map<String,String>  map = dao.queryValuesByProperties(con, Arrays.asList("p1","p2"),"admin",UserRoleEnum.LOGIN_USER);
			Assert.assertNotNull(map);
			Assert.assertTrue(!map.isEmpty());
		} catch (SQLException e) {
			Assert.fail();
		}

	}
}
