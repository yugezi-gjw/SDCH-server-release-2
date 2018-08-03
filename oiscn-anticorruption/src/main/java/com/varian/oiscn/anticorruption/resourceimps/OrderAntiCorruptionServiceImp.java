package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.assembler.TaskAssembler;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumTaskQuery;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRTaskInterface;
import com.varian.oiscn.core.RankEnum;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderRankEnum;
import com.varian.oiscn.core.order.OrderStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by fmk9441 on 2017-02-07.
 */
public class OrderAntiCorruptionServiceImp {
    private FHIRTaskInterface fhirTaskInterface;

    /**
     * Default Constructor.<br>
     */
    public OrderAntiCorruptionServiceImp() {
        fhirTaskInterface = new FHIRTaskInterface();
    }

    /**
     * Create Fhir Task from Order DTO.<br>
     *
     * @param orderDto Order DTO
     * @return new Task Id
     */
    public String createOrder(OrderDto orderDto) {
        Task task = TaskAssembler.getTask(orderDto);
        return fhirTaskInterface.create(task);
    }

    /**
     * Update Fhir Task from Order DTO.<br>
     * @param orderDto Order DTO
     * @return Task Id
     */
    public String updateOrder(OrderDto orderDto) {
        String updatedOrderId = StringUtils.EMPTY;
        Task task = fhirTaskInterface.queryById(orderDto.getOrderId(),Task.class);
        if (null != task) {
            TaskAssembler.updateTask(task, orderDto);
            updatedOrderId = fhirTaskInterface.update(task);
        }

        return updatedOrderId;
    }

    /**
     * Return Order DTO.<br>
     * @param orderId Order Id
     * @return Order DTO
     */
    public OrderDto queryOrderById(String orderId) {
        OrderDto orderDto = null;
        Task task = fhirTaskInterface.queryById(orderId,Task.class);
        if (null != task) {
            orderDto = TaskAssembler.getOrderDto(task);
        }

        return orderDto;
    }

    /**
     * Return Order Dto List.<br>
     * @param patientId Patient Id
     * @return Order DTO List
     */
    public List<OrderDto> queryOrderListByPatientId(String patientId){
        return queryOrderListByPatientIdAndActivityCode(patientId, null);
    }

    /**
     * Return Order Dto List.<br>
     * @param patientId Patient Id
     * @param activityCode Activity Code
     * @return Order DTO List
     */
    public List<OrderDto> queryOrderListByPatientIdAndActivityCode(String patientId, String activityCode){
        List<OrderDto> lstOrderDto = new ArrayList<>();
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        if (isNotBlank(activityCode)) {
            taskQueryImmutablePairMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
        }
        taskQueryImmutablePairMap.put(EnumTaskQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "ready"));
        List<Task> lstTask = fhirTaskInterface.queryTaskList(taskQueryImmutablePairMap);
        if (!lstTask.isEmpty()) {
            lstOrderDto = getOrderDtos(lstTask);
        }
        return lstOrderDto;
    }

    /**
     * Return Order Dto List.<br>
     * @param patientId Patient Id
     * @param activityCode Activity Code
     * @param countPerPage Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo Page Number To
     * @return Order DTO List
     */
    public Pagination<OrderDto> queryOrderListByPatientIdAndActivityCodeAndGroupIdsAndPractitionerIdsWithPaging(String patientId, String activityCode, List<String> lstGroupID, List<String> practitionerIdList, int countPerPage, int pageNumberFrom, int pageNumberTo, String urgentCode) {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        if (isNotBlank(activityCode)) {
            taskQueryImmutablePairMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
        }
        if (isNotEmpty(urgentCode)){
            taskQueryImmutablePairMap.put(EnumTaskQuery.URGENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }
        if(lstGroupID != null && !lstGroupID.isEmpty()){
            taskQueryImmutablePairMap.put(EnumTaskQuery.GROUP_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstGroupID));
        }
        if(practitionerIdList != null && !practitionerIdList.isEmpty()){
            taskQueryImmutablePairMap.put(EnumTaskQuery.RECIPIENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, practitionerIdList));
        }
        taskQueryImmutablePairMap.put(EnumTaskQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, OrderStatusEnum.getDisplay(OrderStatusEnum.READY)));
        Pagination<Task> taskPagination = fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairMap, countPerPage, pageNumberFrom,pageNumberTo);
        return getOrderDtoPagination(taskPagination);
    }

    /**
     * Return Order Dto List.<br>
     * @param lstGroupID Group Id List
     * @param activityName Activity Name
     * @param lstRank Rank List
     * @return Order DTO List
     */
    public List<OrderDto> queryOrderListByGroupIDAndActivityName(List<String> lstGroupID, String activityName, List<ImmutablePair<OrderRankEnum, RankEnum>> lstRank, String urgentCode) {
        List<OrderDto> lstOrderDto = new ArrayList<>();
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.GROUP_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstGroupID));
        taskQueryImmutablePairMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityName));
        taskQueryImmutablePairMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        if(null != lstRank){
            for(ImmutablePair<OrderRankEnum, RankEnum> immutablePair : lstRank){
                taskQueryImmutablePairMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(immutablePair.getRight().name()), OrderRankEnum.getDisplay(immutablePair.getLeft())));
            }
        }
        if (isNotEmpty(urgentCode)){
            taskQueryImmutablePairMap.put(EnumTaskQuery.URGENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }

        Pagination<Task> taskPagination = fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairMap, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        List<Task> lstTask = taskPagination.getLstObject();
        if (!lstTask.isEmpty()) {
            lstOrderDto = getOrderDtos(lstTask);
        }
        return lstOrderDto;
    }

    /**
     * Return Order Dto List.<br>
     * @param lstGroupID Group Id List
     * @param activityName Activity Name
     * @param lstRank Rank List
     * @param countPerPage Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo Page Number To
     * @return Order DTO List
     */
    public Pagination<OrderDto> queryOrderListByGroupIDAndActivityNameWithPaging(List<String> lstGroupID, String activityName, List<ImmutablePair<OrderRankEnum, RankEnum>> lstRank, int countPerPage, int pageNumberFrom,int pageNumberTo, String urgentCode) {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.GROUP_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstGroupID));
        taskQueryImmutablePairMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityName));
        taskQueryImmutablePairMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        if (null != lstRank) {
            for (ImmutablePair<OrderRankEnum, RankEnum> immutablePair : lstRank) {
                taskQueryImmutablePairMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(immutablePair.getRight().name()), OrderRankEnum.getDisplay(immutablePair.getLeft())));
            }
        }
        if (isNotEmpty(urgentCode)){
            taskQueryImmutablePairMap.put(EnumTaskQuery.URGENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }

        Pagination<Task> taskPagination = fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairMap, countPerPage, pageNumberFrom,pageNumberTo);
        return getOrderDtoPagination(taskPagination);
    }

    /**
     * Return Order Dto List.<br>
     * @param practitionerIdList
     * @param activityName Activity Name
     * @param lstRank Rank List
     * @return Order DTO List
     */
    public List<OrderDto> queryOrderListByPractitionerIdAndActivityName(List<String> practitionerIdList, String activityName, List<ImmutablePair<OrderRankEnum, RankEnum>> lstRank, String urgentCode) {
        List<OrderDto> lstOrderDto = new ArrayList<>();
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.RECIPIENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, practitionerIdList));
        taskQueryImmutablePairMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityName));
        taskQueryImmutablePairMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        if(null != lstRank){
            for(ImmutablePair<OrderRankEnum, RankEnum> immutablePair : lstRank){
                taskQueryImmutablePairMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(immutablePair.getRight().name()), OrderRankEnum.getDisplay(immutablePair.getLeft())));
            }
        }
        if (isNotEmpty(urgentCode)){
            taskQueryImmutablePairMap.put(EnumTaskQuery.URGENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }

        List<Task> lstTask = fhirTaskInterface.queryTaskList(taskQueryImmutablePairMap);
        if (!lstTask.isEmpty()) {
            lstOrderDto = getOrderDtos(lstTask);
        }
        return lstOrderDto;
    }

    /**
     * Return Order Dto List.<br>
     * @param practitionerIdList
     * @param activityName Activity Name
     * @param lstRank Rank List
     * @param countPerPage Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo Page Number To
     * @return Order DTO List
     */
    public Pagination<OrderDto> queryOrderListByPractitionerIdAndActivityNameWithPaging(List<String> practitionerIdList, String activityName, List<ImmutablePair<OrderRankEnum, RankEnum>> lstRank, int countPerPage, int pageNumberFrom,int pageNumberTo, String urgentCode){
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.RECIPIENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, practitionerIdList));
        taskQueryImmutablePairMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityName));
        taskQueryImmutablePairMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        if (null != lstRank) {
            for (ImmutablePair<OrderRankEnum, RankEnum> immutablePair : lstRank) {
                taskQueryImmutablePairMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(immutablePair.getRight().name()), OrderRankEnum.getDisplay(immutablePair.getLeft())));
            }
        }
        if (isNotEmpty(urgentCode)){
            taskQueryImmutablePairMap.put(EnumTaskQuery.URGENT, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }

        Pagination<Task> taskPagination = fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairMap, countPerPage, pageNumberFrom,pageNumberTo);
        return getOrderDtoPagination(taskPagination);
    }

    private List<OrderDto> getOrderDtos(List<Task> lstTask) {
        List<OrderDto> lstOrderDto = new ArrayList<>();
        if (!lstTask.isEmpty()) {
            lstTask.forEach(task -> lstOrderDto.add(TaskAssembler.getOrderDto(task)));
        }
        return lstOrderDto;
    }

    private Pagination<OrderDto> getOrderDtoPagination(Pagination<Task> taskPagination) {
        Pagination<OrderDto> orderDtoPagination = new Pagination<>();
        if (taskPagination != null) {
            orderDtoPagination.setTotalCount(taskPagination.getTotalCount());
            orderDtoPagination.setLstObject(getOrderDtos(taskPagination.getLstObject()));
        }
        return orderDtoPagination;
    }
}