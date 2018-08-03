package com.varian.oiscn.base.user;

import com.codahale.metrics.annotation.Timed;
import com.varian.oiscn.anticorruption.resourceimps.GroupAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PractitionerAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.UserAntiCorruptionServiceImp;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.systemconfig.SystemConfigServiceImp;
import com.varian.oiscn.base.tasklocking.TaskLockingDto;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.hipaa.log.AuditLogService;
import com.varian.oiscn.core.hipaa.queue.AuditLogQueue;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import com.varian.oiscn.core.user.*;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.util.hipaa.HipaaEvent;
import com.varian.oiscn.util.hipaa.HipaaLogMessage;
import com.varian.oiscn.util.hipaa.HipaaObjectType;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class AuthenticationResource extends AbstractResource {

    private UserAntiCorruptionServiceImp userAntiCorruptionServiceImp;
    private GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp;
    private PractitionerAntiCorruptionServiceImp practitionerAntiCorruptionServiceImp;
    private AuthenticationCache cache;

    public AuthenticationResource(Configuration configuration, Environment environment, AuthenticationCache cache) {
        super(configuration, environment);
        this.userAntiCorruptionServiceImp = new UserAntiCorruptionServiceImp(
                this.configuration.getFhirServerBaseUri(),
                this.configuration.getOspAuthenticationWsdlUrl(),
                this.configuration.getOspAuthorizationWsdlUrl());
        this.groupAntiCorruptionServiceImp = new GroupAntiCorruptionServiceImp();
        this.practitionerAntiCorruptionServiceImp = new PractitionerAntiCorruptionServiceImp();
        this.cache = cache;
    }

    @POST
    @Path("/login")
    @Timed(name = "Authentication-Login")
    public Response login(User user) {
        Login login = userAntiCorruptionServiceImp.login(user);
        if (login == null || StringUtils.isBlank(login.getToken())) {
            // no token, unauthorized
            Map<String, String> errResponse = new HashMap<>(1);
            errResponse.put("message", "login-error-01");
            HipaaLogMessage hipaaLogMessage = new HipaaLogMessage(user.getUsername(), AuditLogService.NO_PATIENT_ID, HipaaEvent.FailedLogin, HipaaObjectType.Other, AuditLogService.DEFAULT_OBJECT_ID);
            hipaaLogMessage.setComment("Login failed.");
            AuditLogQueue.getInstance().push(hipaaLogMessage);
            return Response.status(Response.Status.UNAUTHORIZED).entity(errResponse).build();
        }

        // token exists
        HipaaLogMessage hipaaLogMessage = new HipaaLogMessage(user.getUsername(), AuditLogService.NO_PATIENT_ID, HipaaEvent.AuthorizedLogin, HipaaObjectType.Other, AuditLogService.DEFAULT_OBJECT_ID);
        hipaaLogMessage.setComment("Login.");
        AuditLogQueue.getInstance().push(hipaaLogMessage);
        OspLogin ospLogin = userAntiCorruptionServiceImp.ospLogin(user);
        cache.put(login.getToken(), new UserContext(login, ospLogin));
        if (login.getResourceSer() != null) {
            List<GroupDto> groupDtoList = groupAntiCorruptionServiceImp.queryGroupListByResourceID(login.getResourceSer().toString());
            List<String> groupIDList = new ArrayList<>();
            List<String> permissionList = new ArrayList<>();
            groupDtoList.forEach(groupDto -> {
                groupIDList.add(groupDto.getGroupId());
//              获取groupId指定的树
                GroupTreeNode node = GroupPractitionerHelper.searchGroupById(groupDto.getGroupId());
                if(node != null){
//              平铺树
                    List<GroupTreeNode> listNode = GroupPractitionerHelper.parallelTreeNode(node);
                    listNode.forEach(groupTreeNode -> {
                        List<String> optList = PermissionService.getOperationListByGroup(groupTreeNode.getOriginalName());
                        if(optList != null) {
                            optList.forEach(opt -> {
                                if (!permissionList.contains(opt)) {
                                    permissionList.add(opt);
                                }
                            });
                        }
                    });
                }
            });
            login.setPermissionList(permissionList);
            login.setStaffGroups(groupIDList);
            PractitionerDto practitionerDto = practitionerAntiCorruptionServiceImp.queryPractitionerById(login.getResourceSer().toString());
            if (practitionerDto != null) {
                login.setResourceName(practitionerDto.getName());
            }

            // query group default view
            SystemConfigServiceImp service = new SystemConfigServiceImp();
            Map<String, String> defaultView = service.queryGroupDefaultView(groupDtoList);
            login.setView(defaultView);
        }
        return Response.ok(login).build();
    }

    /**
     * Logout - remove the token from cache without any authorization.<br>
     *
     * @param authorization token
     * @return response with message
     */
    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request, @HeaderParam("Authorization") String authorization) {
        if (log.isInfoEnabled()) {
            log.info("[logout]:client-ip=[{}],", request.getRemoteAddr());
        }
        Map<String, String> resp = new HashMap<>();
        if (authorization != null && authorization.startsWith("Bearer ")) {
            final String token = authorization.substring(7);
            UserContext userContext = cache.get(token);
            AuditLogQueue.getInstance().push(userContext, null, HipaaEvent.LogOut, HipaaObjectType.Other, "Logout");
            if (userContext != null) {
                TaskLockingServiceImpl taskLockingService = new TaskLockingServiceImpl(userContext);
                TaskLockingDto taskLockingDto = new TaskLockingDto();
                taskLockingDto.setLockUserName(userContext.getName());
                taskLockingService.unLockTask(taskLockingDto);
            }
            cache.remove(token);
            resp.put("msg", "Logout Normally!");
        } else {
            resp.put("msg", "No Authorization Token !");
        }

        return Response.ok(resp).build();
    }
}
