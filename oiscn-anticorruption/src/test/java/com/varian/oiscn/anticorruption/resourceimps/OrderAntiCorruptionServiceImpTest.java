package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.assembler.TaskAssembler;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumTaskQuery;
import com.varian.oiscn.anticorruption.datahelper.MockTaskUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRTaskInterface;
import com.varian.oiscn.core.RankEnum;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderRankEnum;
import com.varian.oiscn.core.pagination.Pagination;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-02-21.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({OrderAntiCorruptionServiceImp.class, TaskAssembler.class})
public class OrderAntiCorruptionServiceImpTest {
    private FHIRTaskInterface fhirTaskInterface;
    private OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirTaskInterface = PowerMockito.mock(FHIRTaskInterface.class);
        PowerMockito.whenNew(FHIRTaskInterface.class).withNoArguments().thenReturn(fhirTaskInterface);
        orderAntiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
    }

    @Test
    public void givenAOrderDtoWhenCreateThenReturnOrderId() {
        final String taskId = "TaskId";
        Task task = PowerMockito.mock(Task.class);
        OrderDto orderDto = PowerMockito.mock(OrderDto.class);
        PowerMockito.mockStatic(TaskAssembler.class);
        PowerMockito.when(TaskAssembler.getTask(orderDto)).thenReturn(task);
        PowerMockito.when(fhirTaskInterface.create(task)).thenReturn(taskId);
        String createdTaskId = orderAntiCorruptionServiceImp.createOrder(orderDto);
        Assert.assertEquals(taskId, createdTaskId);
    }

    @Test
    public void givenAOrderDtoWhenUpdateThenReturnOrderId() {
        final String taskId = "TaskId";
        Task task = MockTaskUtil.givenATask();
        OrderDto orderDto = MockTaskUtil.givenAnOrderDto();
        PowerMockito.when(fhirTaskInterface.queryById(anyString(),any())).thenReturn(task);
        PowerMockito.when(fhirTaskInterface.update(task)).thenReturn(taskId);
        String updatedTaskId = orderAntiCorruptionServiceImp.updateOrder(orderDto);
        Assert.assertEquals(taskId, updatedTaskId);
    }

    @Test
    public void givenAOrderIdWhenQueryThenReturnOrderDto() {
        final String orderId = "OrderId";
        Task task = MockTaskUtil.givenATask();
        PowerMockito.when(fhirTaskInterface.queryById(anyString(),any())).thenReturn(task);
        OrderDto orderDto = orderAntiCorruptionServiceImp.queryOrderById(orderId);
        Assert.assertNotNull(orderDto);
        Assert.assertEquals(orderDto.getOrderStatus(), "ready");
    }

    @Test
    public void givenAPatientIdWhenQueryThenReturnOrderDtoList() {
        final String patientId = "PatientId";
        LinkedHashMap<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "ready"));
        List<Task> lstTask = MockTaskUtil.givenATaskList();
        PowerMockito.when(fhirTaskInterface.queryTaskList(taskQueryImmutablePairLinkedHashMap)).thenReturn(lstTask);
        List<OrderDto> lstOrderDto = orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientId);
        Assert.assertThat(1, is(lstOrderDto.size()));
    }

    @Test
    public void givenAPatientIdAndActivityCodeWhenQueryThenReturnOrderDtoList() {
        final String patientId = "PatientId";
        final String activityCode = "ActivityCode";
        LinkedHashMap<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "ready"));
        List<Task> lstTask = MockTaskUtil.givenATaskList();
        PowerMockito.when(fhirTaskInterface.queryTaskList(taskQueryImmutablePairLinkedHashMap)).thenReturn(lstTask);
        List<OrderDto> lstOrderDto = orderAntiCorruptionServiceImp.queryOrderListByPatientIdAndActivityCode(patientId, activityCode);
        Assert.assertThat(1, is(lstOrderDto.size()));
    }

    @Test
    public void givenAPatientIdAndActivityCodeAndPaginationWhenQueryThenReturnOrderDtoList() {
        final String patientId = "PatientId";
        final String activityCode = "ActivityCode";
        final Integer countPerPage = 5;
        final Integer pageNumber = 1;
        LinkedHashMap<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "ready"));
        List<Task> lstTask = MockTaskUtil.givenATaskList();
        Pagination<Task> taskPagination = new Pagination<>();
        taskPagination.setTotalCount(1);
        taskPagination.setLstObject(lstTask);
        PowerMockito.when(fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairLinkedHashMap, countPerPage, pageNumber,1)).thenReturn(taskPagination);
        Pagination<OrderDto> orderDtoPagination = orderAntiCorruptionServiceImp.queryOrderListByPatientIdAndActivityCodeAndGroupIdsAndPractitionerIdsWithPaging(patientId, activityCode,null,null, countPerPage, pageNumber,1, null);
        Assert.assertThat(1, is(orderDtoPagination.getTotalCount()));
        Assert.assertThat(1, is(orderDtoPagination.getLstObject().size()));
    }

    @Test
    public void givenAGroupIDAndActivityNameAndSortingWhenQueryThenReturnOrderDtoList() {
        final List<String> listGroupId = Arrays.asList("GroupID");
        final String activityName = "ActivityName";
        final List<ImmutablePair<OrderRankEnum, RankEnum>> listRank = new ArrayList<>();
        listRank.add(new ImmutablePair<>(OrderRankEnum.TASK_CREATION_DATE, RankEnum.DESC));
        LinkedHashMap<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.GROUP_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, listGroupId));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityName));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(listRank.get(0).getRight().name()), OrderRankEnum.getDisplay(listRank.get(0).getLeft())));
        List<Task> lstTask = MockTaskUtil.givenATaskList();
        Pagination<Task> taskPagination = new Pagination<>();
        taskPagination.setTotalCount(1);
        taskPagination.setLstObject(lstTask);
        PowerMockito.when(fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairLinkedHashMap, Integer.MAX_VALUE, 1, Integer.MAX_VALUE)).thenReturn(taskPagination);
        PowerMockito.when(fhirTaskInterface.queryTaskList(taskQueryImmutablePairLinkedHashMap)).thenReturn(lstTask);
        List<OrderDto> lstOrderDto = orderAntiCorruptionServiceImp.queryOrderListByGroupIDAndActivityName(listGroupId, activityName, listRank, null);
        Assert.assertThat(1, is(lstOrderDto.size()));
    }

    @Test
    public void givenAGroupIDAndActivityNameAndSortingAndPaginationWhenQueryThenReturnOrderDtoList() {
        final List<String> listGroupId = Arrays.asList("GroupID");
        final String activityName = "ActivityName";
        final List<ImmutablePair<OrderRankEnum, RankEnum>> listRank = new ArrayList<>();
        final Integer countPerPage = 5;
        final Integer pageNumber = 1;
        listRank.add(new ImmutablePair<>(OrderRankEnum.TASK_CREATION_DATE, RankEnum.DESC));
        LinkedHashMap<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.GROUP_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, listGroupId));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityName));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(listRank.get(0).getRight().name()), OrderRankEnum.getDisplay(listRank.get(0).getLeft())));
        List<Task> lstTask = MockTaskUtil.givenATaskList();
        Pagination<Task> taskPagination = new Pagination<>();
        taskPagination.setTotalCount(1);
        taskPagination.setLstObject(lstTask);
        PowerMockito.when(fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairLinkedHashMap, countPerPage, pageNumber,1)).thenReturn(taskPagination);
        Pagination<OrderDto> orderDtoPagination = orderAntiCorruptionServiceImp.queryOrderListByGroupIDAndActivityNameWithPaging(listGroupId, activityName, listRank, countPerPage, pageNumber,1, null);
        Assert.assertThat(1, is(orderDtoPagination.getTotalCount()));
        Assert.assertThat(1, is(orderDtoPagination.getLstObject().size()));
    }

    @Test
    public void givenAPractitionerIdListAndActivityNameAndSortingWhenQueryThenReturnOrderDtoList() {
        final List<String> listPractitionerIdList = Arrays.asList("PractitionerID");
        final String activityName = "ActivityName";
        final List<ImmutablePair<OrderRankEnum, RankEnum>> listRank = new ArrayList<>();
        listRank.add(new ImmutablePair<>(OrderRankEnum.TASK_CREATION_DATE, RankEnum.DESC));
        LinkedHashMap<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.RECIPIENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, listPractitionerIdList));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityName));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(listRank.get(0).getRight().name()), OrderRankEnum.getDisplay(listRank.get(0).getLeft())));
        List<Task> lstTask = MockTaskUtil.givenATaskList();
        PowerMockito.when(fhirTaskInterface.queryTaskList(taskQueryImmutablePairLinkedHashMap)).thenReturn(lstTask);
        List<OrderDto> lstOrderDto = orderAntiCorruptionServiceImp.queryOrderListByPractitionerIdAndActivityName(listPractitionerIdList, activityName, listRank, null);
        Assert.assertThat(1, is(lstOrderDto.size()));
    }

    @Test
    public void givenAPractitionerIdListAndActivityNameAndSortingAndPaginationWhenQueryThenReturnOrderDtoList() {
        final List<String> listPractitionerIdList = Arrays.asList("PractitionerID");
        final String activityName = "ActivityName";
        final List<ImmutablePair<OrderRankEnum, RankEnum>> listRank = new ArrayList<>();
        final Integer countPerPage = 5;
        final Integer pageNumber = 1;
        listRank.add(new ImmutablePair<>(OrderRankEnum.TASK_CREATION_DATE, RankEnum.DESC));
        LinkedHashMap<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairLinkedHashMap = new LinkedHashMap<>();
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.RECIPIENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, listPractitionerIdList));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityName));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        taskQueryImmutablePairLinkedHashMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(listRank.get(0).getRight().name()), OrderRankEnum.getDisplay(listRank.get(0).getLeft())));
        List<Task> lstTask = MockTaskUtil.givenATaskList();
        Pagination<Task> taskPagination = new Pagination<>();
        taskPagination.setTotalCount(1);
        taskPagination.setLstObject(lstTask);
        PowerMockito.when(fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairLinkedHashMap, countPerPage, pageNumber,1)).thenReturn(taskPagination);
        Pagination<OrderDto> orderDtoPagination = orderAntiCorruptionServiceImp.queryOrderListByPractitionerIdAndActivityNameWithPaging(listPractitionerIdList, activityName, listRank, countPerPage, pageNumber,1, null);
        Assert.assertThat(1, is(orderDtoPagination.getTotalCount()));
        Assert.assertThat(1, is(orderDtoPagination.getLstObject().size()));
    }
}