/**
 * 
 */
package com.varian.oiscn.base.user;

import com.varian.oiscn.base.user.profile.ProfileDAO;
import com.varian.oiscn.base.user.profile.PropertyEntity;
import com.varian.oiscn.base.user.profile.PropertyEnum;
import com.varian.oiscn.base.user.profile.UserRoleEnum;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.util.DatabaseUtil;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User Service Implementation.<br>
 *
 */
@Slf4j
public class UserServiceImpl implements UserService {

	protected ProfileDAO profile = new ProfileDAO();
	
	public UserServiceImpl(Configuration configuration, Environment environment) {
	}

	@Override
	public String getProperty(String userId, String property) {
		String value = null;
        Connection con = null;
        try {
        	con = ConnectionPool.getConnection();
        	value = profile.queryUserProperty(con, userId, property);
        } catch (SQLException e) {
            log.error("getProperty - SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        // when no setting in database, default value is set.
        if (StringUtils.isBlank(value)) {
        	value = "true";
        }
        return value;
	}

	@Override
	public Map<String, String> getAllProperties(String userId) {
		Map<String, String> map = null;
        Connection con = null;
        try {
        	con = ConnectionPool.getConnection();
        	map = profile.queryUserAllProperties(con, userId);
        } catch (SQLException e) {
            log.error("getAllProperties - SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        
        // when no setting in database, default value is set.
        for (PropertyEnum p: PropertyEnum.values()) {
        	if (!map.containsKey(p.getName())) {
        		map.put(p.getName(), p.getDefaultValue());
        	}
        }
        return map;
	}

    @Override
    public List<KeyValuePair> getLoginUserValuesByProperties(List<String> propertyList,String userId) {
        List<KeyValuePair> result = new ArrayList<>();
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            Map<String,String> map = profile.queryValuesByProperties(con,propertyList,userId,UserRoleEnum.LOGIN_USER);
            propertyList.forEach(prop->{
                String val = map.get(prop);
                result.add(new KeyValuePair(prop,StringUtils.trimToEmpty(val)));
            });
        } catch (SQLException e) {
            log.error("getLoginUserValuesByProperties - SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return result;
    }

    @Override
	public boolean updateProperty(String userId, PropertyEntity property) {
		int affectedRow = 0;
        Connection con = null;
        try {
        	con = ConnectionPool.getConnection();
        	affectedRow = profile.updateUserProperty(con, userId, property.getProperty(),property.getValue());
        } catch (SQLException e) {
            log.error("updateProperty - SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return affectedRow > 0;
	}
}
