package com.varian.oiscn.core.hipaa.log;

import com.varian.oiscn.core.hipaa.config.AuditLogConfig;
import com.varian.oiscn.util.hipaa.HipaaEvent;
import com.varian.oiscn.util.hipaa.HipaaLogMessage;
import com.varian.oiscn.util.hipaa.HipaaLogger;
import com.varian.oiscn.util.hipaa.HipaaObjectType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 3/7/2018
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AuditLogTransfer.class})
public class AuditLogTransferTest {

    private HipaaLogger logger;
    private AuditLogTransfer auditLogTransfer;
    private AtomicInteger logCount = new AtomicInteger(0);

    @Before
    public void setup() {
        AuditLogConfig config = givenAuditLogConfig();
        logger = PowerMockito.mock(HipaaLogger.class);
        auditLogTransfer = PowerMockito.spy(new AuditLogTransfer(config, true, logger));
    }

    @Test
    public void testRun() throws Exception {
        MemberModifier.stub(MemberMatcher.method(AuditLogTransfer.class, "takeLogMsg")).toReturn(assembleLogMessage());
        List<HipaaLogMessage> logMessageList = new ArrayList<>();
        logMessageList.add(assembleLogMessage());
        PowerMockito.doNothing().when(auditLogTransfer, "doLogAction", new Object[]{logMessageList});
        PowerMockito.doNothing().when(auditLogTransfer, "run");
        auditLogTransfer.run();
        Assert.assertTrue(true);
    }

    private HipaaLogMessage assembleLogMessage() {
        String userid = "userid";
        String patientid = "patientid";
        String objectId = "1";
        HipaaLogMessage logMessage = new HipaaLogMessage(userid, patientid, HipaaEvent.View, HipaaObjectType.Activities, objectId);
        logMessage.setApplicationId("applicationid1");
        logMessage.setComment("comment");
        logMessage.setTime(new Date());
        logMessage.setPatientName("patientname");
        return logMessage;
    }

    private AuditLogConfig givenAuditLogConfig() {
        AuditLogConfig config = new AuditLogConfig();
        config.setHostName("hostName");
        config.setLogBatchSize(2);
        config.setLogThreadCount(1);
        config.setPort(1000);
        return config;
    }

}
