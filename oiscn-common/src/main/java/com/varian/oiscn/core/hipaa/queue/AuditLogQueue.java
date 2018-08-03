package com.varian.oiscn.core.hipaa.queue;

import com.varian.oiscn.core.hipaa.log.AuditLogService;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.hipaa.HipaaEvent;
import com.varian.oiscn.util.hipaa.HipaaLogMessage;
import com.varian.oiscn.util.hipaa.HipaaObjectType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 3/5/2018
 * @Modified By:
 */
@Slf4j
public class AuditLogQueue {

    private static final int QUEUE_SIZE = 300000;

    private static final int PUT_QUEUE_RETRY_COUNT = 3;

    private static final int PUT_QUEUE_TIMEOUT = 10;

    private static volatile AuditLogQueue instance = null;

    private BlockingQueue<HipaaLogMessage> blockingQueue = null;

    private AuditLogQueue() {
        blockingQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    }

    public static AuditLogQueue getInstance() {
        if(instance == null) {
            synchronized (AuditLogQueue.class) {
                if(instance == null) {
                    instance = new AuditLogQueue();
                }
            }
        }
        return instance;
    }

    /**
     * Push log to queue
     * @param logMessage
     * @return
     */
    public void push(HipaaLogMessage logMessage) {
        for(int i = 0; i < PUT_QUEUE_RETRY_COUNT; i++) {
            try {
                if(blockingQueue.offer(logMessage, PUT_QUEUE_TIMEOUT, TimeUnit.MILLISECONDS))
                    return;
            } catch (InterruptedException e) {
                //Retry it
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void push(UserContext user, String patientId, HipaaEvent hipaaEvent, HipaaObjectType hipaaObjectType, String comment){
        String userId = "";
        if(user != null && user.getLogin() != null){
            userId = user.getLogin().getName();
        }
        if(StringUtils.isEmpty(userId)){
            userId = AuditLogService.NO_USER_ID;
        }
        String patientIdToHipaa = "";
        if(StringUtils.isEmpty(patientId)){
            patientIdToHipaa = AuditLogService.NO_PATIENT_ID;
        }
        HipaaLogMessage hipaaLogMessage = new HipaaLogMessage(userId, patientIdToHipaa, hipaaEvent, hipaaObjectType, AuditLogService.DEFAULT_OBJECT_ID);
        hipaaLogMessage.setComment(comment);
        push(hipaaLogMessage);
    }

    /**
     * Take log from queue
     * @return
     */
    public HipaaLogMessage poll() {
        HipaaLogMessage ele = null;
        try {
            ele = this.blockingQueue.take();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return ele;
    }

    /**
     * Get queue size
     * @return
     */
    public int size() {
        return this.blockingQueue.size();
    }


}
