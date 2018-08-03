package com.varian.oiscn.application.resources;

import com.varian.oiscn.carepath.task.EclipseTask;
import com.varian.oiscn.carepath.task.EclipseTaskService;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Refresh the task status which done by Eclipse Thread.<br>
 */
@Slf4j
public class TaskUpdateStatusThread implements Runnable {

    public static final String THREAD_NAME = "QinTaskUpdateStatusThread";
    protected EclipseTaskService qinService;

    /**
     * Constructor.<br>
     *
     * @param authenticationCache cache
     */
    public TaskUpdateStatusThread() {
        this.qinService = new EclipseTaskService(null);
    }

    @Override
    public void run() {
        // get removed user name list from token cache.
        List<EclipseTask> pendingTaskList = qinService.listPendingTask();
        if (pendingTaskList != null && pendingTaskList.size() > 0) {
            log.debug("pendingTaskList: {}", pendingTaskList.toString());
            pendingTaskList.forEach(task -> {
                final String orderId = task.getOrderId();
                OrderDto dto = qinService.queryOrderById(orderId);
                if (dto != null && dto.getOrderStatus().equalsIgnoreCase(OrderStatusEnum.COMPLETED.toString())) {
                    log.info("{} has done the task [{}] !", THREAD_NAME, dto.getOrderId());
                    qinService.doneTask(dto, task.getPatientSer(), THREAD_NAME);
                }
            });
        }
    }
}
