package com.varian.oiscn.base.user;

import com.varian.oiscn.base.user.profile.PropertyEntity;
import com.varian.oiscn.core.common.KeyValuePair;

import java.util.List;
import java.util.Map;

/**
 * User Service Interface.<br>
 */
public interface UserService {
	/** 
	 * Get specified property value.<br>
	 * @param userId User Id
	 * @param property Property Name
	 * @return Property Value
	 */
	String getProperty(String userId, String property);

	/** 
	 * Get all property values.<br>
	 * @param userId User Id
	 * @return All Property Values
	 */
	Map<String, String> getAllProperties(String userId);

	/**
	 * Get all property values.<br>
	 * @param propertyList
	 * @param userId  User Id
	 * @return All Property Values
	 */
	List<KeyValuePair> getLoginUserValuesByProperties(List<String> propertyList,String userId);

	/** 
	 * Update property value.<br>
	 * @param userId User Id
	 * @param entity Property Entity
	 * @return operation result
	 */
	boolean updateProperty(String userId, PropertyEntity entity);
}
