package com.varian.oiscn.base.coverage;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 11/1/2017
 * @Modified By:
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class PayorInfoResource extends AbstractResource {

    public PayorInfoResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
    }

    @Path("/insurance-type/list")
    @GET
    public Response getAllPayorInfo(@Auth UserContext userContext) {
        List<KeyValuePair> rlist = new ArrayList<>();
        Map<String, String> payorInfoMap = PayorInfoPool.getCachedPayorInfo();
        payorInfoMap.forEach((key, value) -> {
            rlist.add(new KeyValuePair(key, value));
        });
        Collections.sort(rlist, Comparator.comparing(KeyValuePair::getKey));
        return Response.ok(rlist).build();
    }
}
