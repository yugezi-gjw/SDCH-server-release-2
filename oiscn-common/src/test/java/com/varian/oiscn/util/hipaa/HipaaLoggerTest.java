package com.varian.oiscn.util.hipaa;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;

/**
 * Created by bhp9696 on 2018/4/25.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HipaaLogger.class})
public class HipaaLoggerTest {
    private HipaaLogger hipaaLogger;
    private Socket socket = null;
    private OutputStreamWriter socketWriter = null;

    @Before
    public void setup() {
        try {
            HipaaLoggerConfiguration configuration = new HipaaLoggerConfiguration();
            configuration.setHostname("localhost");
            configuration.setPort(121212);
            configuration.setTimeoutInMs(10000);

            hipaaLogger = new HipaaLogger(configuration);
            socket = PowerMockito.mock(Socket.class);

            PowerMockito.whenNew(Socket.class).withArguments(configuration.getHostname(), configuration.getPort()).thenReturn(socket);
            PowerMockito.when(socket.isClosed()).thenReturn(true);

            PowerMockito.doNothing().when(socket,"setKeepAlive",true);
            PowerMockito.doNothing().when(socket,"setTcpNoDelay",true);
            PowerMockito.doNothing().when(socket,"setSoLinger",true,1);
            PowerMockito.doNothing().when(socket,"setSoTimeout",configuration.getTimeoutInMs());

            socketWriter = PowerMockito.mock(OutputStreamWriter.class);
            OutputStream outputStream = PowerMockito.mock(OutputStream.class);
            PowerMockito.when(socket.getOutputStream()).thenReturn(outputStream);
            PowerMockito.whenNew(OutputStreamWriter.class).withArguments(socket.getOutputStream()).thenReturn(socketWriter);
            HipaaLogMessage msg = getHippaLogMessage();
            PowerMockito.doNothing().when(socketWriter,"write",msg.toString());
            PowerMockito.doNothing().when(socketWriter,"flush");

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testLog() throws Exception {
        HipaaLogMessage msg = getHippaLogMessage();

        Assert.assertNotNull(msg.getObjectId());
        Assert.assertNotNull(msg.getObjectType());
        Assert.assertNotNull(msg.getApplicationId());
        Assert.assertNotNull(msg.getComment());
        Assert.assertNotNull(msg.getEvent());
        Assert.assertNotNull(msg.getPatientId());
        Assert.assertNotNull(msg.getPatientName());
        Assert.assertNotNull(msg.getTime());
        Assert.assertNotNull(msg.getUserId());

        hipaaLogger.log(msg);
        hipaaLogger.log(msg);
        PowerMockito.doThrow(new IOException("test")).when(socketWriter,"write", Matchers.anyString());
        try {
            hipaaLogger.log(msg);
        }catch (Exception e){
            if(e instanceof HipaaException){
                HipaaException hipaaException = (HipaaException) e;

                Assert.assertTrue(hipaaException.toString().indexOf("test")>0);
            }
        }
    }


    private HipaaLogMessage getHippaLogMessage(){
        HipaaLogMessage msg = new HipaaLogMessage();
        msg.setObjectType(HipaaObjectType.Activities);
        msg.setEvent(HipaaEvent.AuthorizedLogin);
        msg.setApplicationId("Qin");
        msg.setComment("comment");
        msg.setObjectId("objectId");
        msg.setPatientId("patientId");
        msg.setPatientName("kevin");
        try {
            msg.setTime(DateUtils.parseDate("2018-04-25 20:51:00", "yyyy-MM-dd HH:mm:ss"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        msg.setUserId("SysAdmin");
        return msg;
    }
}
