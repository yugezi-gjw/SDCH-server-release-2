package com.varian.oiscn.core.hipaa.log;


import com.varian.oiscn.core.hipaa.config.AuditLogConfig;
import com.varian.oiscn.util.hipaa.HipaaLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by gbt1220 on 3/7/2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AuditLogService.class})
public class AuditLogServiceTest {

    private AuditLogConfig config;
    private HipaaLogger logger;

    @Before
    public void setup() throws Exception {
        config = new AuditLogConfig();
        config.setHostName("hostName");
        config.setLogBatchSize(100);
        config.setLogThreadCount(1);
        config.setPort(1000);
        config.setTimeoutInMs(300);
    }

    @Test
    public void testStartThread() throws Exception {
        try {
            AuditLogService service = new AuditLogService(config);
            service.startLogThread();
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
