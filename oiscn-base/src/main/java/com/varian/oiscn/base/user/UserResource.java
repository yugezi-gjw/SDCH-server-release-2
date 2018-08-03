package com.varian.oiscn.base.user;

import com.varian.oiscn.base.user.profile.PropertyEntity;
import com.varian.oiscn.base.user.profile.PropertyVO;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/user")
public class UserResource extends AbstractResource {

	protected Configuration configuration;
	protected Environment environment;
	protected UserService userService;

	public UserResource(Configuration configuration, Environment environment) {
		super(configuration, environment);
		userService = new UserServiceImpl(configuration, environment);
	}

	/**
	 * Get User Profile with property name.<br>
	 * @param userContext User Context
	 * @param propertyName specified property
	 * @return User's profile
	 */
	@Path("/profile")
	@GET
	public Response getProfile(@Auth UserContext userContext, @QueryParam("property") String propertyName) {
		Map<String, String> result = new HashMap<>();
		final String userId = userContext.getLogin().getUsername();

		if (StringUtils.isBlank(propertyName)) {
			log.debug("getProfile - no property");
			// return all properties.
			result = userService.getAllProperties(userId);
		} else {
			String propertyValue = userService.getProperty(userId, propertyName);
			result.put(propertyName, propertyValue);
		}
		return Response.ok(result).build();
	}

	/**
	 * Update Profile.<br>
	 *
	 * @param userContext UserContext
	 * @param vo          PropertyVO
	 * @return update result
	 */
	@Path("/profile")
	@PUT
	public Response putProfile(@Auth UserContext userContext, PropertyVO vo) {
		Map<String, Object> result = new HashMap<>();
		PropertyEntity entity = new PropertyEntity(vo.getProperty(), vo.getValue());
		final String userId = userContext.getLogin().getUsername();

		boolean updateResult = userService.updateProperty(userId, entity);
		result.put("result", updateResult);
		return Response.accepted(result).build();
	}


	/**
	 *  Update user Preference
	 * @param userContext
	 * @param preferenceList
	 * @return
	 */
	@Path("/profiles")
	@PUT
	public Response putPreference(@Auth UserContext userContext, List<KeyValuePair> preferenceList) {
		Map<String, Object> result = new HashMap<>();

		if(preferenceList == null || preferenceList.isEmpty()){
			log.debug("putPreference - no preferenceList");
			result.put("result",preferenceList);
			return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
		}
		List<PropertyEntity> propertyEntities = new ArrayList<>();
		preferenceList.forEach(keyValuePair -> {
			propertyEntities.add(new PropertyEntity(keyValuePair.getKey(),keyValuePair.getValue()));
		});
		final String userId = userContext.getLogin().getUsername();
		boolean updateResult = false;
		for(PropertyEntity propertyEntity : propertyEntities){
			updateResult = userService.updateProperty(userId, propertyEntity);
			if(!updateResult){
				result.put("result",updateResult);
			}
		}
		if(result.isEmpty()){
			result.put("result",updateResult);
		}
		return Response.accepted(result).build();
	}

	/**
	 *  Update user Preference
	 * @param userContext
	 * @param propertyList
	 * @return
	 */
	@Path("/profiles")
	@POST
	public Response getPreference(@Auth UserContext userContext, List<KeyValuePair> propertyList) {
		Map<String, Object> result = new HashMap<>();
		if(propertyList == null || propertyList.isEmpty()){
			log.debug("getPreference - no propertyList");
			result.put("result",propertyList);
			return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
		}
		List<String> list = propertyList.stream().map(keyValuePair -> keyValuePair.getKey()).collect(Collectors.toList());
		List<KeyValuePair> r = userService.getLoginUserValuesByProperties(list,userContext.getLogin().getUsername());
		return Response.accepted(r).build();
	}

}
