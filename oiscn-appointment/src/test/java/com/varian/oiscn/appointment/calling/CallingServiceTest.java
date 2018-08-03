package com.varian.oiscn.appointment.calling;

import com.varian.oiscn.config.Configuration;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.junit.Assert.fail;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CallingService.class, CallingHttpClient.class, HttpClients.class})
public class CallingServiceTest {

    protected CallingHttpClient client;
    protected Configuration configuration;
    
    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        String callingConfigFile = "../config/integration/CallingSystem.yaml";
        PowerMockito.when(configuration.getCallingConfigFile()).thenReturn(callingConfigFile);
        CallingService.init(configuration);
    }

    @Test
    public void testInit() {
        Assert.assertNotNull(CallingService.serverConfig);
        Assert.assertNotNull(CallingService.deviceList);
        Assert.assertTrue(CallingService.deviceList.size() > 0);
    }
    
    @Test
    public void testSendMsg() {
        Map<String, Object> msg = PowerMockito.mock(Map.class);
        try {
            client = PowerMockito.mock(CallingHttpClient.class);
            PowerMockito.whenNew(CallingHttpClient.class).withNoArguments().thenReturn(client);

            CallingService.sendMsg(msg);

            Mockito.verify(client).sendMsg(msg);
            Mockito.verify(client).close();
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetCallingDeviceIdByAriaDeviceId() {
        // deviceId: macaddress1008
        // deviceName: 制模1008
        // ariaDeviceId: 1008
        String ariaDeviceId = "1008";
        String actual = CallingService.getCallingDeviceIdByAriaDeviceId(ariaDeviceId);
        Assert.assertNotNull("macaddress1008", actual);
    }

    @Test
    public void testGetDeviceRoomByAriaDeviceId() {
        // deviceId: macaddress1008
        // deviceName: 制模1008
        // ariaDeviceId: 1008
        String ariaDeviceId = "1008";
        String actual = CallingService.getDeviceRoomByAriaDeviceId(ariaDeviceId);
        Assert.assertNotNull("制模1008", actual);
    }


    @Test
    public void testGetDeviceRoomByCallingDeviceId() {
        // deviceId: macaddress1008
        // deviceName: 制模1008
        // ariaDeviceId: 1008
        String deviceId = "macaddress1008";
        String actual = CallingService.getDeviceRoomByCallingDeviceId(deviceId);
        Assert.assertNotNull("制模1008", actual);
    }
}
