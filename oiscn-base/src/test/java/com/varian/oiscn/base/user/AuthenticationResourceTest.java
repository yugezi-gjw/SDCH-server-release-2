package com.varian.oiscn.base.user;

import com.varian.oiscn.anticorruption.resourceimps.GroupAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PractitionerAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.UserAntiCorruptionServiceImp;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigServiceImp;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import com.varian.oiscn.core.user.*;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthenticationResource.class, UserAntiCorruptionServiceImp.class,
        SystemConfigServiceImp.class, TaskLockingServiceImpl.class,
        GroupPractitionerHelper.class,PermissionService.class})
public class AuthenticationResourceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private AuthenticationResource resource;
    private AuthenticationCache cache;
    private Configuration configuration;
    private Environment environment;
    private UserAntiCorruptionServiceImp userAntiCorruptionServiceImp;
    private GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp;
    private PractitionerAntiCorruptionServiceImp practitionerAntiCorruptionServiceImp;
    private String fhirUrl = "testFhirUrl";
    private String wsdlUrl = "testWsdlUrl";
    private String authorizationUrl = "testUrl";

    @Before
    public void setup() throws Exception {
        cache = new AuthenticationCache(10);
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        userAntiCorruptionServiceImp = PowerMockito.mock(UserAntiCorruptionServiceImp.class);
        PowerMockito.when(configuration.getFhirServerBaseUri()).thenReturn(fhirUrl);
        PowerMockito.when(configuration.getOspAuthenticationWsdlUrl()).thenReturn(wsdlUrl);
        PowerMockito.when(configuration.getOspAuthorizationWsdlUrl()).thenReturn(authorizationUrl);
        PowerMockito.whenNew(UserAntiCorruptionServiceImp.class).withArguments(fhirUrl, wsdlUrl, authorizationUrl).thenReturn(userAntiCorruptionServiceImp);
        groupAntiCorruptionServiceImp = PowerMockito.mock(GroupAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(GroupAntiCorruptionServiceImp.class).withNoArguments().thenReturn(groupAntiCorruptionServiceImp);
        practitionerAntiCorruptionServiceImp = PowerMockito.mock(PractitionerAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PractitionerAntiCorruptionServiceImp.class).withNoArguments().thenReturn(practitionerAntiCorruptionServiceImp);
        resource = new AuthenticationResource(configuration, environment, cache);
        PowerMockito.mockStatic(PermissionService.class);
        PowerMockito.mockStatic(GroupPractitionerHelper.class);
    }

    @Test
    public void givenAnUserWhenAuthenticateSuccessfullyThenReturnLoginInfo() {
        Login login = MockDtoUtil.givenALogin();
        login.setToken("newToken");
        OspLogin ospLogin = MockDtoUtil.givenAnOspLogin();
        PowerMockito.when(userAntiCorruptionServiceImp.login(Matchers.any(User.class))).thenReturn(login);
        PowerMockito.when(userAntiCorruptionServiceImp.ospLogin(Matchers.any(User.class))).thenReturn(ospLogin);
        List<GroupDto> groupDtos = givenAGroupList();
        PowerMockito.when(groupAntiCorruptionServiceImp.queryGroupListByResourceID(login.getResourceSer().toString())).thenReturn(groupDtos);
        PractitionerDto practitionerDto = givenAPractitioner();
        PowerMockito.when(practitionerAntiCorruptionServiceImp.queryPractitionerById(login.getResourceSer().toString())).thenReturn(practitionerDto);
        User u = new User("username", "password", "");
        u.setUsername("username");
        u.setPassword("password");
        SystemConfigServiceImp service = PowerMockito.mock(SystemConfigServiceImp.class);
        try {
            PowerMockito.whenNew(SystemConfigServiceImp.class).withNoArguments().thenReturn(service);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        PowerMockito.when(PermissionService.getOperationListByGroup(Matchers.anyString())).thenReturn(Arrays.asList("Oncologist"));
        GroupTreeNode groupTreeNode = new GroupTreeNode("111","Oncologist","Oncologist");
        PowerMockito.when(GroupPractitionerHelper.searchGroupById(Matchers.anyString())).thenReturn(groupTreeNode);

        PowerMockito.when( GroupPractitionerHelper.parallelTreeNode(Matchers.any())).thenReturn(Arrays.asList(groupTreeNode));

        Object entity = resource.login(u).getEntity();
        assertThat(entity, is(login));
    }

    @Test
    public void givenAnUserWhenFailToAuthenticateThenThrowWebApplicationException() {
        PowerMockito.when(userAntiCorruptionServiceImp.login(Matchers.any(User.class))).thenReturn(new Login());

        User user = new User();
        user.setUsername("username");
        Response res = resource.login(user);
        Assert.assertEquals(Response.Status.UNAUTHORIZED, res.getStatusInfo());
        Object entity = res.getEntity();
        if (entity instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> resMap = (Map<String, String>) entity;
            Assert.assertEquals("login-error-01", resMap.get("message"));
        }
    }

    @Test
    public void givenCacheWhenLogoutThenRemoveToken() {
        cache.put("token", givenUserContext());
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);

        TaskLockingServiceImpl service = PowerMockito.mock(TaskLockingServiceImpl.class);
        try {
            PowerMockito.whenNew(TaskLockingServiceImpl.class).withAnyArguments().thenReturn(service);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        resource.logout(request, "Bearer " + givenUserContext().getLogin().getToken());
        Assert.assertEquals(0, cache.getTokenCache().size());
        Mockito.verify(service).unLockTask(Mockito.any());
    }

    private List<GroupDto> givenAGroupList() {
        return Collections.singletonList(new GroupDto("1", "name"));
    }

    private PractitionerDto givenAPractitioner() {
        PractitionerDto practitionerDto = new PractitionerDto();
        practitionerDto.setId("1");
        practitionerDto.setName("name");
        return practitionerDto;
    }

    private UserContext givenUserContext() {
        Login login = MockDtoUtil.givenALogin();
        OspLogin ospLogin = MockDtoUtil.givenAnOspLogin();
        return new UserContext(login, ospLogin);
    }
}
