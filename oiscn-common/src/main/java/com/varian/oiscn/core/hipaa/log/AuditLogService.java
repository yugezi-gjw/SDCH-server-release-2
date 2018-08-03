package com.varian.oiscn.core.hipaa.log;

import com.varian.oiscn.core.hipaa.config.AuditLogConfig;
import com.varian.oiscn.core.hipaa.utils.AuditLogBaseThreadFactory;
import com.varian.oiscn.util.hipaa.HipaaLogger;
import com.varian.oiscn.util.hipaa.HipaaLoggerConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 3/2/2018
 * @Modified By:
 */
@Slf4j
public class AuditLogService {

    private static final String DEFAULT_THREADPOOL_NAME = "AuditLogPool";
    public static final String NO_PATIENT_ID = "[None]";
    public static final String DEFAULT_OBJECT_ID = "[None]";
    public static final String NO_USER_ID = "[None]";

    private AuditLogConfig config;

    private HipaaLoggerConfiguration loggerConfiguration;
    private HipaaLogger logger;

    private AuditLogBaseThreadFactory threadFactory;
    private AuditLogTransfer logThread;

    public AuditLogService(AuditLogConfig config) {
        this.config = config;
        if (config != null) {
            log.debug("config: [{}]", config);
            initializeLogService(config);
            logThread = new AuditLogTransfer(config, true, logger);
        }
    }

    /**
     * Initialize Hipaa log service
     * @param config
     */
    private void initializeLogService(AuditLogConfig config) {
        loggerConfiguration = new HipaaLoggerConfiguration();
        loggerConfiguration.setHostname(config.getHostName());
        loggerConfiguration.setPort(config.getPort());
        loggerConfiguration.setTimeoutInMs(config.getTimeoutInMs());
        logger = new HipaaLogger(loggerConfiguration);
    }

    /**
     * Start the audit log thread
     */
    public void startLogThread() {
        threadFactory = new AuditLogBaseThreadFactory(DEFAULT_THREADPOOL_NAME);
        for(int i = 0; i < config.getLogThreadCount(); i++) {
            Thread executor = threadFactory.newThread(logThread);
            executor.start();
        }
    }
}
