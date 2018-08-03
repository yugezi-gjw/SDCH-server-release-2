package com.varian.oiscn.appointment.resource;

import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.appointment.calling.CallingConfig;
import com.varian.oiscn.resource.AbstractResource;
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
 * @Date: Created in 10/24/2017
 * @Modified By:
 */
@Path("/calling")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class CallingWaitingResource extends AbstractResource {

       private CallingConfig callingConfig;

       public CallingWaitingResource(Configuration configuration, Environment environment) {
           super(configuration, environment);
       }

       @Path("/getconfig")
       @GET
       public Response getConfig() {
           if (HisPatientInfoConfigService.getConfiguration() == null
                   || !HisPatientInfoConfigService.getConfiguration().isCallingSystemEnable()) {
               log.warn("Please check the HisSystem.yaml file, and ensure the calling system switcher is enabled.");
               return Response.ok("").build();
           }

           callingConfig = configuration.getCallingConfig();
           if(callingConfig != null) {
               return Response.status(Response.Status.OK).entity(callingConfig).build();
           }
           return Response.status(Response.Status.NOT_FOUND).build();
       }

}
