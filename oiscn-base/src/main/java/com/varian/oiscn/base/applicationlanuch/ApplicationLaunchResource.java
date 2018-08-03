package com.varian.oiscn.base.applicationlanuch;

import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.security.util.EncryptionUtil;
import com.varian.oiscn.security.util.SHACoder;
import io.dropwizard.auth.Auth;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.http.util.TextUtils.isEmpty;

/**
 * Created by gbt1220 on 7/20/2017.
 */
@Slf4j
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationLaunchResource extends AbstractResource {

    private static String KEY = "DE7A452ADE454E41A5B71DC615A5CEF6";

    /**
     * Constructor.<br>
     *
     * @param configuration Configuration
     * @param environment
     */
    public ApplicationLaunchResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
    }

    @Path("/applicationlaunch/getGuid")
    @GET
    public Response getGuid(@Context HttpServletRequest request, @Auth UserContext userContext,
                            @QueryParam("moduleId") String moduleId,
                            @QueryParam("taskId") String taskId,
                            @QueryParam("activityId") String activityId,
                            @QueryParam("activityCode") String activityCode,
                            @QueryParam("patientName") String patientName,
                            @QueryParam("patientSer") Long patientSer) {
        if (log.isInfoEnabled()) {
            log.info("[getGuid]:client-ip=[{}],", request.getRemoteAddr());
            String token = request.getHeader("Authorization");
            if (token != null && token.length() > 7) {
                log.info("[getGuid]:client-token(hashed)=[{}]", EncryptionUtil.hashMD5(token.substring(7)));
            }
            if (userContext.getOspLogin() != null) {
                String ospToken = userContext.getOspLogin().getToken();
                log.info("[getGuid]:client-osp-token(hashed)=[{}]", EncryptionUtil.hashMD5(ospToken));
            }
        }
        ApplicationLaunchVO applicationLaunchVO = new ApplicationLaunchVO();
        if (isEmpty(moduleId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(applicationLaunchVO).build();
        }
        if (userContext.getOspLogin() == null || isEmpty(userContext.getOspLogin().getToken())) {
            log.error("The use osp token is invalid. Please relogin.");
            return Response.status(Response.Status.UNAUTHORIZED).entity(applicationLaunchVO).build();
        }

        String guid = UUID.randomUUID().toString();
        String serverIp = configuration.getServerAddressOfCCIP();
        DefaultServerFactory serverFactory = (DefaultServerFactory) configuration.getServerFactory();
        HttpConnectorFactory httpConnectionFactory = (HttpConnectorFactory) serverFactory.getApplicationConnectors().get(0);
        int port = httpConnectionFactory.getPort();
        StringBuffer url = new StringBuffer("http://").append(serverIp).append(":").append(port).append("/applicationlaunch/").append(guid);
        String saltUrl = url.toString() + KEY;
        String sha256Url = SHACoder.encodeSHA256(saltUrl.getBytes());

        StringBuilder link = new StringBuilder("appframe:&-url=").append(url).append("&-signature=").append(sha256Url);

        applicationLaunchVO.setGuid(guid);
        applicationLaunchVO.setSignature(sha256Url);
        applicationLaunchVO.setLink(link.toString());

        ApplicationLaunchParam param;
        try {
            param = getApplicationLaunchParam(userContext, moduleId, activityId, patientSer, activityCode, patientName);
        } catch (UnsupportedEncodingException e) {
            String errMsg = "Patient Name is in Unsupported Encoding!";
            log.warn(errMsg);
            return Response.status(Response.Status.BAD_REQUEST).entity(errMsg).build();
        }
        String launchContext = ApplicationLaunchContentBuilder.getLaunchContent(param);
        ApplicationLaunchContentPool.put(guid, launchContext);

        updateTaskOwner(userContext, taskId);

        return Response.status(Response.Status.OK).entity(applicationLaunchVO).build();
    }

    @Path("/applicationlaunch/{guid}")
    @GET
    public Response launch(@Context HttpServletRequest request, @PathParam("guid") String guid) {
        if (log.isInfoEnabled()) {
            log.info("[launch]:client-ip=[{}],", request.getRemoteAddr());
            String token = request.getHeader("Authorization");
            if (token != null && token.length() > 7) {
                log.info("[getGuid]:client-token(hashed)=[{}]", EncryptionUtil.hashMD5(token.substring(7)));
            }
        }
        if (isEmpty(guid)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(StringUtils.EMPTY).build();
        }
        String content = StringUtils.defaultIfEmpty(ApplicationLaunchContentPool.get(guid), StringUtils.EMPTY);
        if (isNotEmpty(content)) {
            ApplicationLaunchContentPool.remove(guid);
        }
        return Response.status(Response.Status.OK).entity(content).build();
    }

    private void updateTaskOwner(UserContext userContext, String taskId) {
        OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
        OrderDto task = new OrderDto();
        task.setOrderId(taskId);
        String ownerId = userContext.getLogin().getResourceSer().toString();
        task.setOwnerId(ownerId);
        task.setParticipants(Arrays.asList(new ParticipantDto(ParticipantTypeEnum.PRACTITIONER, ownerId)));
        orderAntiCorruptionServiceImp.updateOrder(task);
    }

    private ApplicationLaunchParam getApplicationLaunchParam(UserContext userContext, String moduleId, String taskId, Long patientSer, String activityCode, String patientName) throws UnsupportedEncodingException {
        ApplicationLaunchParam param = new ApplicationLaunchParam();
        param.setModuleId(moduleId);
        param.setOspToken(userContext.getOspLogin().getToken());
        param.setTaskId(taskId);
        param.setTaskName(activityCode);
        param.setPatientSer(patientSer);
        if (isNotBlank(patientName)) {
            param.setPatientName(URLDecoder.decode(patientName, StandardCharsets.UTF_8.toString()));
        }
        param.setResourceName(userContext.getLogin().getResourceName());
        param.setOspCUID(userContext.getOspLogin().getUserCUID());
        return param;
    }
}
