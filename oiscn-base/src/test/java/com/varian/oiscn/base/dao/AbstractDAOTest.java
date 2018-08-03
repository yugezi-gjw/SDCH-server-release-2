package com.varian.oiscn.base.dao;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.base.util.MockPreparedStatement;
import com.varian.oiscn.base.util.MockResultSet;
import com.varian.oiscn.core.user.UserContext;

@RunWith(PowerMockRunner.class)
public class AbstractDAOTest {
	
    private Connection con;

    @Before
    public void setup(){
        con = PowerMockito.mock(MockDatabaseConnection.class);
    }

	@Test
	public void testCreate() throws SQLException {
		AbstractDAO<String> dao = new MockAbstractDAO(MockDtoUtil.givenUserContext());
		PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);;
		
        PowerMockito.when(con.prepareStatement(Mockito.anyString(), Mockito.anyInt())).thenReturn(ps);
        
        PowerMockito.doNothing().when(ps).setString(Mockito.anyInt(), Mockito.any());
        PowerMockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any());
        
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        
        String result = "xxxx";
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
		PowerMockito.when(rs.getString(1)).thenReturn(result);
        
        Assert.assertEquals(result, dao.create(con, "{abcd:xyz}"));
	}

	@Test
	public void testUpdate() throws SQLException {
		AbstractDAO<String> dao = new MockAbstractDAO(MockDtoUtil.givenUserContext());
		PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);;
		
        PowerMockito.when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
        
        PowerMockito.doNothing().when(ps).setString(Mockito.anyInt(), Mockito.any());
        PowerMockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any());
        PowerMockito.doNothing().when(ps).setLong(Mockito.anyInt(), Mockito.anyLong());
        
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(rs);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        
        String result = "xxxx";
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
		PowerMockito.when(rs.getString(1)).thenReturn(result);
        
        Assert.assertEquals(true, dao.update(con, "{abcd:xyz}", "1234"));
	}

	class MockAbstractDAO extends AbstractDAO<String> {

		public MockAbstractDAO(UserContext userContext) {
			super(userContext);
		}

		@Override
		protected String getTableName() {
			return "tableName";
		}

		@Override
		protected String getJsonbColumnName() {
			return "ColumnName";
		}
		
	}
}
