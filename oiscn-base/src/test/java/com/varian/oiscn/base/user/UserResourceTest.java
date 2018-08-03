package com.varian.oiscn.base.user;

import com.varian.oiscn.base.user.profile.PropertyEntity;
import com.varian.oiscn.base.user.profile.PropertyEnum;
import com.varian.oiscn.base.user.profile.PropertyVO;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
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

import javax.ws.rs.core.Response;
import java.util.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserServiceImpl.class})
public class UserResourceTest {

	protected UserServiceImpl userService;
	protected UserResource resource;
	protected UserContext context;
	
	
	@Before
	public void setUp() throws Exception {
		userService = PowerMockito.mock(UserServiceImpl.class);

		Login login = new Login();
		login.setUsername("liguozhu");
		OspLogin ospLogin = new OspLogin();
		ospLogin.setName("liguozhu");
		
		context = new UserContext(login, ospLogin);
		resource = new UserResource(null, null);
	}

	@Test
	public void testGetProfile() throws Exception {
		
		Map<String, String> mockPropertyMap = new HashMap<>();
		for (PropertyEnum pe: PropertyEnum.values()) {
			mockPropertyMap.put(pe.getName(), pe.getDefaultValue());
		}
		String result = "sfasdfadf";
		resource.userService = userService;
		
        PowerMockito.when(userService.getProperty("liguozhu", PropertyEnum.SHOW_DONE_HINT.getName())).thenReturn(result);
        
        Response response = resource.getProfile(context, "ShowDoneHint");
        
        Object data = response.getEntity();
        if (data instanceof Map) {
            Map<String, String> actualData = (Map<String, String>) data;
            Assert.assertEquals(actualData.get("ShowDoneHint"), result);
        }
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
	}


	
	@Test
	public void testPutProfile() {
		
		PropertyVO vo = new PropertyVO(PropertyEnum.SHOW_DONE_HINT.getName(), "DEF");
		resource.userService = userService;
		
		boolean result = true;
		PowerMockito.when(userService.updateProperty(Mockito.anyString(), Mockito.any(PropertyEntity.class))).thenReturn(result);
        
        Response response = resource.putProfile(context, vo);
        
        Object resEntity = response.getEntity();
        if (resEntity instanceof Map) {
            Map actualData = (Map) resEntity;
            Assert.assertNotNull(actualData);
            Assert.assertNotNull(actualData.get("result"));
        }
		Assert.assertEquals(Response.Status.ACCEPTED, response.getStatusInfo());
	}

	@Test
	public void testPutPreference() {
		resource.userService = userService;

		boolean result = true;
		PowerMockito.when(userService.updateProperty(Mockito.anyString(), Mockito.any(PropertyEntity.class))).thenReturn(result);

		Response response = resource.putPreference(context, Arrays.asList(new KeyValuePair(PropertyEnum.SHOW_DONE_HINT.getName(),"aaa")));

		Object resEntity = response.getEntity();
		if (resEntity instanceof Map) {
			Map actualData = (Map) resEntity;
			Assert.assertNotNull(actualData);
			Assert.assertNotNull(actualData.get("result"));
		}
		Assert.assertEquals(Response.Status.ACCEPTED, response.getStatusInfo());
	}


	@Test
	public void testGetLoginUserValuesByProperties(){
		List<KeyValuePair> list = new ArrayList<>();
        list.add(new KeyValuePair("p1", "v1"));
        list.add(new KeyValuePair("p2", "v3"));
		
		PowerMockito.when(userService.getLoginUserValuesByProperties(Matchers.anyList(),Matchers.anyString())).thenReturn(list);
		resource.userService = userService;
		Response response = resource.getPreference(context,Arrays.asList(new KeyValuePair("p1",""),new KeyValuePair("p2","")));
		Assert.assertNotNull(response);
		Assert.assertTrue(((List<KeyValuePair>)response.getEntity()).size()==2);

	}
}
