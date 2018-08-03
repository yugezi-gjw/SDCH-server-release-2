package com.varian.oiscn.carepath.task;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.common.CarePathInstanceHelper;
import com.varian.oiscn.base.tasklocking.TaskLockingDto;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderStatusEnum;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Update Eclipse Task Status Service.<br>
 */
@Slf4j
public class EclipseTaskService {
    protected EclipseTaskDAO dao;
    protected OrderAntiCorruptionServiceImp orderService;

    public EclipseTaskService(UserContext ctx) {
        dao = new EclipseTaskDAO(null);
        orderService = new OrderAntiCorruptionServiceImp();
    }

    public String createPendingTask(EclipseTask task, String userName) {
        Connection con = null;
        String newId = StringUtils.EMPTY;
        try {
            con = ConnectionPool.getConnection();
            newId = dao.create(con, task, userName);
        } catch (SQLException e) {
            log.error("createPendingTask SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return newId;
    }
    
    public synchronized List<EclipseTask> listPendingTask() {
        List<EclipseTask> list = new ArrayList<>();
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            list = dao.listPending(con);
        } catch (SQLException e) {
            log.error("listPendingTask SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return list;
    }
    
    protected synchronized int doneByQin(String orderId, String userName) {
        int done = 0;
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            done = dao.doneByQin(con, orderId, userName);
        } catch (SQLException e) {
            log.error("doneByQin SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return done;
    }
    
    protected synchronized int doneByEclipse(String orderId, String userName) {
        int done = 0;
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            done = dao.doneByEclipse(con, orderId, userName);
        } catch (SQLException e) {
            log.error("doneByEclipse SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return done;
    }

    public synchronized void doneTask(OrderDto orderDto, String patientSer, String userName) {
        CarePathAntiCorruptionServiceImp cpService = new CarePathAntiCorruptionServiceImp();
        CarePathInstance cp = cpService.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(patientSer, orderDto.getOrderId(), ActivityTypeEnum.TASK);
        CarePathInstanceHelper helper = new CarePathInstanceHelper(cp);
        List<ActivityInstance> nextActivities = helper.getNextActivitiesByInstanceId(orderDto.getOrderId(), ActivityTypeEnum.TASK.name());
        if ((nextActivities != null) && !nextActivities.isEmpty()) {
            for (ActivityInstance eachNextActivity : nextActivities) {
                // check if all pre activities of each next activity are done.
                if (!helper.isAllPreActivitiesDoneOfEachNextActivity(orderDto.getOrderId(), eachNextActivity)) {
                    break;
                }
                if (StringUtils.isEmpty(eachNextActivity.getInstanceID())) {
                    OrderDto newOrderDto = new OrderDto();
                    newOrderDto.setOrderType(eachNextActivity.getActivityCode());
                    newOrderDto.setOrderStatus(OrderStatusEnum.getDisplay(OrderStatusEnum.READY));
                    newOrderDto.setOrderGroup(eachNextActivity.getDefaultGroupID());
                    newOrderDto.setDueDate(DateUtil.addMillSecond(getDueDateOfTheActivity(eachNextActivity.getId(), helper), 1 * 1000));

                    ParticipantDto patient = new ParticipantDto(ParticipantTypeEnum.PATIENT, patientSer);
                    newOrderDto.setParticipants(Arrays.asList(patient));
                    log.debug("Schedule next order of id[{}]: activityCode[{}], status[{}], group[{}], dueDate[{}], patient[{}]",
                            eachNextActivity.getId(), newOrderDto.getOrderType(),
                            newOrderDto.getOrderStatus(), newOrderDto.getOrderGroup(),
                            newOrderDto.getDueDate(), patientSer);
                    String newId = cpService.scheduleNextTask(cp.getId(), eachNextActivity.getId(), newOrderDto);
                    if (StringUtils.isBlank(newId)) {
                        break;
                    }
                }
            }
        }

        unlockQinTask(orderDto.getOrderId(), userName);
        doneByEclipse(orderDto.getOrderId(), userName);
    }
    
    protected Date getDueDateOfTheActivity(String activityId, CarePathInstanceHelper helper) {
        Date theDueDateOfTheTask = new Date();
        List<ActivityInstance> prevActivities = helper.getPrevActivities(activityId);

        if ((prevActivities != null) && !prevActivities.isEmpty()) {
            for (ActivityInstance activityInstance : prevActivities) {
                theDueDateOfTheTask = calculateDueDate(theDueDateOfTheTask,
                        activityInstance);
            }
        }

        return theDueDateOfTheTask;
    }
    
    protected Date calculateDueDate(Date theDueDateOfActivity, ActivityInstance activityInstance) {
        Date tmpTheDueDateOfActivity = theDueDateOfActivity;
        OrderDto thePrevOrder = orderService.queryOrderById(activityInstance.getInstanceID());
        if (thePrevOrder.getDueDate().compareTo(theDueDateOfActivity) > 0) {
            tmpTheDueDateOfActivity = thePrevOrder.getDueDate();
        }
        return tmpTheDueDateOfActivity;
    }
    
    protected boolean unlockQinTask(String orderId, String userName) {
        TaskLockingServiceImpl taskLockingService = new TaskLockingServiceImpl(new UserContext());
        TaskLockingDto taskLockingDto = new TaskLockingDto(orderId, ActivityTypeEnum.TASK.toString(), userName, null, null, null);
        return taskLockingService.unLockTask(taskLockingDto);
    }

    public OrderDto queryOrderById(String orderId) {
        return orderService.queryOrderById(orderId);
    }
}
