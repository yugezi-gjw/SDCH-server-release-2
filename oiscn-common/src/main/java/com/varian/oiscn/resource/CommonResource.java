package com.varian.oiscn.resource;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.UserContext;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 11/20/2017
 * @Modified By:
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class CommonResource extends AbstractResource {

    public CommonResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
    }


    /**
     *
     * @param userContext
     * @return
     */
    @Path("/common/server/datetime")
    @GET
    public Response getServerTime(@Auth UserContext userContext){
        KeyValuePair keyValuePair = new KeyValuePair("datetime",new java.util.Date().getTime()+"");
        return Response.ok(keyValuePair).build();
    }
}
