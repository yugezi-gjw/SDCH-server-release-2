package com.varian.oiscn.core.hipaa;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.hipaa.queue.AuditLogQueue;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by gbt1220 on 2/27/2018.
 */
@Path("/auditlog")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HipaaEventResource extends AbstractResource {
    public HipaaEventResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
    }

    @POST
    @Path("/")
    public Response insert(@Auth UserContext userContext, HipaaLogMessageDto dto) {
        AuditLogQueue.getInstance().push(userContext, dto.getPatientSer(), dto.getEvent(), dto.getObjectType(), dto.getComment());
        return Response.status(Response.Status.OK).entity(true).build();
    }

    @POST
    @Path("/loginfailed")
    public Response insert(HipaaLogMessageDto dto){
        AuditLogQueue.getInstance().push(null, dto.getPatientSer(), dto.getEvent(), dto.getObjectType(), dto.getComment());
        return Response.status(Response.Status.OK).entity(true).build();
    }
}
