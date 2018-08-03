package com.varian.oiscn.core.hipaa.queue;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.hipaa.HipaaEvent;
import com.varian.oiscn.util.hipaa.HipaaLogMessage;
import com.varian.oiscn.util.hipaa.HipaaObjectType;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 3/6/2018
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AuditLogQueue.class})
public class AuditLogQueueTest {

    private AuditLogQueue auditLogQueue;

    @Before
    public void setup() {
        auditLogQueue = PowerMockito.mock(AuditLogQueue.class);
        PowerMockito.mockStatic(AuditLogQueue.class);
    }

    @Test
    public void testGetInstanceWithNoArguments() {
        PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(auditLogQueue);
        auditLogQueue = AuditLogQueue.getInstance();
        Assert.assertNotNull(auditLogQueue);
    }

    @Test
    public void testGivenLogMessageToPushNoException() throws Exception {
        PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(auditLogQueue);
        BlockingQueue<HipaaLogMessage> blockingQueue = PowerMockito.mock(LinkedBlockingQueue.class);
        MemberModifier.field(AuditLogQueue.class, "blockingQueue").set(auditLogQueue, blockingQueue);
        MemberModifier.field(AuditLogQueue.class, "PUT_QUEUE_RETRY_COUNT").set(AuditLogQueue.class, 3);
        HipaaLogMessage logMessage = assembleLogMessage();
        PowerMockito.when(blockingQueue.offer(logMessage, 10, TimeUnit.MILLISECONDS)).thenReturn(true);
        AuditLogQueue.getInstance().push(logMessage);
        PowerMockito.when(blockingQueue.size()).thenReturn(1);
        Assert.assertEquals(1, blockingQueue.size());
    }

    @Test
    public void testGivenLogMessageToPushWithException() throws Exception {
        PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(auditLogQueue);
        BlockingQueue<HipaaLogMessage> blockingQueue = PowerMockito.mock(LinkedBlockingQueue.class);
        MemberModifier.field(AuditLogQueue.class, "blockingQueue").set(auditLogQueue, blockingQueue);
        MemberModifier.field(AuditLogQueue.class, "PUT_QUEUE_RETRY_COUNT").set(AuditLogQueue.class, 3);
        HipaaLogMessage logMessage = assembleLogMessage();
        PowerMockito.when(blockingQueue.offer(logMessage, 10, TimeUnit.MILLISECONDS)).thenThrow(InterruptedException.class);
        AuditLogQueue.getInstance().push(logMessage);
        PowerMockito.when(blockingQueue.size()).thenReturn(0);
        Assert.assertEquals(0, blockingQueue.size());
    }

    @Test
    public void testTakeLogMessageFromQueue() throws Exception {
        PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(auditLogQueue);
        BlockingQueue<HipaaLogMessage> blockingQueue = PowerMockito.mock(LinkedBlockingQueue.class);
        MemberModifier.field(AuditLogQueue.class, "blockingQueue").set(auditLogQueue, blockingQueue);
        HipaaLogMessage logMessage = assembleLogMessage();
        PowerMockito.when(auditLogQueue.poll()).thenReturn(logMessage);
        PowerMockito.when(blockingQueue.take()).thenReturn(logMessage);
        Assert.assertEquals(logMessage, AuditLogQueue.getInstance().poll());
    }

    @Test
    public void givenParametersThenNoExceptions(){
        PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(auditLogQueue);
        AuditLogQueue.getInstance().push(new UserContext(), "patientId", HipaaEvent.Other, HipaaObjectType.Other, "comment");
        Assert.assertTrue(true);
    }

    private HipaaLogMessage assembleLogMessage() {
        String userid = "userid";
        String patientid = "patientid";
        String objectId = "1";
        HipaaLogMessage logMessage = new HipaaLogMessage(userid, patientid, HipaaEvent.View, HipaaObjectType.Activities, objectId);
        logMessage.setApplicationId("applicationid");
        logMessage.setComment("comment");
        logMessage.setTime(new Date());
        logMessage.setPatientName("patientname");
        return logMessage;
    }

}
