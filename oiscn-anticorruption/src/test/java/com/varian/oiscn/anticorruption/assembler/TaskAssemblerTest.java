package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.datahelper.MockTaskUtil;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;

/**
 * Created by fmk9441 on 2017-02-17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TaskAssembler.class})
public class TaskAssemblerTest {
    @InjectMocks
    private TaskAssembler taskAssembler;

    @Test
    public void givenAnOrderDtoWhenConvertThenReturnTask() throws Exception {
        OrderDto orderDto = MockTaskUtil.givenAnOrderDto();
        Task task = TaskAssembler.getTask(orderDto);
        Assert.assertThat(task, is(notNullValue()));
    }

    @Test
    public void givenAnOrderDtoWhenConvertThenThrowFHIRException() throws Exception {
        OrderDto orderDto = MockTaskUtil.givenAnOrderDto();
        orderDto.setOrderStatus("OrderStatus");
        Task task = TaskAssembler.getTask(orderDto);
        Assert.assertNull(task.getStatus());
    }

    @Test
    public void givenATaskWhenConvertThenReturnOrderDto() throws Exception {
        Task task = MockTaskUtil.givenATask();
        OrderDto orderDto = TaskAssembler.getOrderDto(task);
        Assert.assertThat(orderDto, is(notNullValue()));
    }

    @Test
    public void doUpdateTaskWhenGivenATaskAndOrderDto() throws Exception {
        Task task = MockTaskUtil.givenATask();
        OrderDto orderDto = MockTaskUtil.givenAnOrderDto();
        orderDto.setOrderId("TaskId");
        orderDto.setOwnerId("PractitionerId");
        orderDto.setOrderStatus("completed");
        orderDto.setParticipants(Arrays.asList(new ParticipantDto(ParticipantTypeEnum.PATIENT, "PatientId1"), new ParticipantDto(ParticipantTypeEnum.PRACTITIONER, "PractitionerId")));
        TaskAssembler.updateTask(task, orderDto);
        Assert.assertEquals(task.getStatus(), Task.TaskStatus.COMPLETED);
        Assert.assertTrue(task.getRestriction().getRecipient().stream().map(r -> getReferenceValue(r)).collect(Collectors.toList()).contains("PatientId1"));
    }
}