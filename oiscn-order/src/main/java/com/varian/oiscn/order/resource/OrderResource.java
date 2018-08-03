package com.varian.oiscn.order.resource;

import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 2/23/2017.
 */
@Path("/")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource extends AbstractResource {

    private OrderAntiCorruptionServiceImp antiCorruptionServiceImp;

    public OrderResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        antiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
    }

    @POST
    @Path("/order")
    public Response createOrder(@Auth UserContext userContext, OrderDto orderDto) {
        try {
            String orderId = antiCorruptionServiceImp.createOrder(orderDto);
            return Response.status(Response.Status.CREATED).entity(orderId).build();
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").build();
        }
    }

    @PUT
    @Path("/order/{id}")
    public Response updateOrder(@Auth UserContext userContext, @PathParam("id") String orderId, OrderDto orderDto) {
        try {
            orderDto.setOrderId(orderId);
            antiCorruptionServiceImp.updateOrder(orderDto);
            return Response.status(Response.Status.OK).entity(orderDto).build();
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").build();
        }
    }

    @GET
    @Path("/orders/search")
    public Response search(@Auth UserContext userContext,
                           @QueryParam("patientId") Long patientId){
        if(patientId == null){
            return Response.status(Response.Status.BAD_REQUEST).entity(new ArrayList<>()).build();
        }

        List<OrderDto> lstOrderDto = antiCorruptionServiceImp.queryOrderListByPatientId(String.valueOf(patientId));
        return Response.status(Response.Status.OK).entity(lstOrderDto).build();
    }
}