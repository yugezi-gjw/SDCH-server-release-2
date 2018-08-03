package com.varian.oiscn.order.resource;

/**
 * Created by gbt1220 on 2/23/2017.
 */

import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.user.UserContext;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderResource.class)
public class OrderResourceTest {

    private Configuration configuration;

    private Environment environment;

    private OrderAntiCorruptionServiceImp antiCorruptionServiceImp;

    private OrderResource resource;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        antiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(antiCorruptionServiceImp);
        resource = new OrderResource(configuration, environment);
    }

    @Test
    public void givenAnOrderwhenCreatethenReturnCreatedResponse() {
        String orderId = "newOrderId";
        OrderDto dto = givenAnOrder();
        PowerMockito.when(antiCorruptionServiceImp.createOrder(dto)).thenReturn(orderId);
        Response response = resource.createOrder(new UserContext(), dto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.CREATED));
        assertThat(response.getEntity(), is(orderId));
    }

    @Test
    public void givenAnOrderwhenFhirThrowCreateExceptionthenReturnInternalServerErrorResponse() {
        OrderDto dto = givenAnOrder();
        PowerMockito.when(antiCorruptionServiceImp.createOrder(dto)).thenThrow(Exception.class);
        Response response = resource.createOrder(new UserContext(), dto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void givenAPatientIdAndPatientIdIsBlankwhenQueryOrderthenReturnResponseStatusBadRequest() {
        Response response = resource.search(new UserContext(), null);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenAPatientIdwhenQueryOrderthenReturnResponseStatusOk() {
        List<OrderDto> lstOrderDto = givenAnOrderDtoList();
        PowerMockito.when(antiCorruptionServiceImp.queryOrderListByPatientId("1111")).thenReturn(lstOrderDto);
        Response response = resource.search(new UserContext(), 1111L);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        assertThat(response.getEntity(), equalTo(lstOrderDto));
    }

    @Test
    public void givenAPatientIdwhenQueryOrderthenReturnResponseStatusNotFound() {
        List<OrderDto> lstOrderDto = new ArrayList<>();
        PowerMockito.when(antiCorruptionServiceImp.queryOrderListByPatientId("1111")).thenReturn(lstOrderDto);
        Response response = resource.search(new UserContext(), 1111L);
        assertThat(response.getEntity(), equalTo(new ArrayList<>()));
    }

    @Test
    public void givenAnOrderwhenUpdateOrderthenReturnResponseOk() {
        OrderDto dto = givenAnOrder();
        dto.setOrderId("testOrderId");
        PowerMockito.when(antiCorruptionServiceImp.updateOrder(dto)).thenReturn(dto.getOrderId());
        Response response = resource.updateOrder(new UserContext(), dto.getOrderId(), dto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        assertThat(response.getEntity(), equalTo(dto));
    }

    @Test
    public void givenAnOrderwhenFhirThrowUpdateExceptionthenReturnInternalServerErrorResponse() {
        OrderDto dto = givenAnOrder();
        PowerMockito.when(antiCorruptionServiceImp.updateOrder(dto)).thenThrow(Exception.class);
        Response response = resource.updateOrder(new UserContext(), dto.getOrderId(), dto);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
    }

    private OrderDto givenAnOrder() {
        OrderDto dto = new OrderDto();
        dto.setOrderType("immobilization");
        dto.setOrderGroup("group");
        dto.setOrderStatus("new");
        dto.setParticipants(new ArrayList<>());
        return dto;
    }

    private List<OrderDto> givenAnOrderDtoList() {
        List<OrderDto> lstOrderDto = new ArrayList<>();
        lstOrderDto.add(givenAnOrder());
        return lstOrderDto;
    }
}
