package com.varian.oiscn.core.hipaa.log;

import com.varian.oiscn.core.hipaa.config.AuditLogConfig;
import com.varian.oiscn.core.hipaa.queue.AuditLogQueue;
import com.varian.oiscn.util.hipaa.HipaaException;
import com.varian.oiscn.util.hipaa.HipaaLogMessage;
import com.varian.oiscn.util.hipaa.HipaaLogger;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 3/2/2018
 * @Modified By:
 */
@Slf4j
public class AuditLogTransfer implements Runnable {

    private AuditLogConfig config;
    private volatile boolean isRunning;
    private List<HipaaLogMessage> logMsgList;
    private HipaaLogger logger;
    private Thread thread;
    private Object sync = new Object();

    public AuditLogTransfer(AuditLogConfig config, boolean isRunning, HipaaLogger logger) {
        this.config = config;
        this.isRunning = isRunning;
        this.logger = logger;
        logMsgList = Collections.synchronizedList(new ArrayList<>());
    }

    public void run() {
        thread = Thread.currentThread();
        log.info("AuditLogTransfer thread [{}] start running.", thread.getName());

        int batchSize = config.getLogBatchSize();

        while(isRunning) {
            try {
                if(logMsgList.size() < batchSize) {
                    HipaaLogMessage logMsg = takeLogMsg();
                    logMsgList.add(logMsg);
                } else {
                    doLogAction(logMsgList);
                    synchronized (sync) {
                        logMsgList.clear();
                    }
                }
            } catch (Exception e) {
                log.warn("Exception: [{}]", e.getMessage());
                isRunning = false;
                break;
            }

            if(thread.isInterrupted()) {
                isRunning = false;
            }
        }
        log.info("AuditLogTransfer thread [{}] stop running.", thread.getName());
    }

    /**
     * Take log message from queue
     * @return
     * @throws InterruptedException
     */
    private HipaaLogMessage takeLogMsg() throws InterruptedException {
        return AuditLogQueue.getInstance().poll();
    }

    /**
     * Write logs to ARIA
     * @param logMsgList
     */
    private void doLogAction(List<HipaaLogMessage> logMsgList) {
        logMsgList.forEach(logMsg -> {
            try {
                logger.log(logMsg);
            } catch (HipaaException e) {
                log.warn("Exception: [{}]", e.getMessage());
            }
        });
    }
}
