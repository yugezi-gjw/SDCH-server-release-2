package com.varian.oiscn.base.systemconfig;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;

import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;

/**
 * Created by cxq8822 on Sep 20, 2017
 *
 * This class query configuration data and return them to front end.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SystemConfigResource extends AbstractResource {

    private SystemConfigServiceImp systemConfigServiceImp;

    public SystemConfigResource(Configuration configuration, Environment environment){
        super(configuration, environment);
        systemConfigServiceImp = new SystemConfigServiceImp();
    }

    @GET
    @Path("/systemconfig/query")
    public Response querySystemConfigValueByName(@Auth UserContext userContext, @QueryParam("name") String name) {
        if(!StringUtils.isEmpty(name)){
            List<String> result = systemConfigServiceImp.queryConfigValueByName(name);
            if(!result.isEmpty()){
                return Response.status(Response.Status.OK).entity(result).build();
            }
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/systemconfig/recurring-appointment-time-limit")
    public Response queryRecurringAppointmentTimeLimit(@Auth UserContext userContext) {
        String limit = systemConfigServiceImp.queryRecurringAppointmentTimeLimit();
        return Response.status(Response.Status.OK).entity(limit).build();
    }
}
