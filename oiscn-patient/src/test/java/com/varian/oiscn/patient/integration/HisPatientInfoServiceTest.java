package com.varian.oiscn.patient.integration;

import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.patient.integration.config.HisServerStatusEnum;
import com.varian.oiscn.patient.integration.exception.HisServiceException;
import com.varian.oiscn.patient.integration.service.HisPatientHttpClient;
import com.varian.oiscn.patient.integration.service.HisPatientInfoService;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/25/2017
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HisPatientInfoService.class, HisPatientHttpClient.class, HttpClients.class,
        HisPatientInfoConfigService.class})
public class HisPatientInfoServiceTest {

    @InjectMocks
    private HisPatientInfoService hisPatientInfoService;

    protected HisPatientHttpClient hisPatientHttpClient;
    protected String configFile = "../config/integration/HisSystem.yaml";

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(HisPatientInfoConfigService.class);
    }

    @Test
    public void testIsOK() {
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(null);
        Assert.assertFalse(HisPatientInfoService.isOK());
    }

    @Test
    public void testCallHisInterfaceAndThrowEcxeption() throws Exception {
        hisPatientHttpClient = PowerMockito.mock(HisPatientHttpClient.class);
        PowerMockito.whenNew(HisPatientHttpClient.class).withAnyArguments().thenReturn(hisPatientHttpClient);

        PowerMockito.when(hisPatientHttpClient.sendMessage()).thenThrow(new HisServiceException(HisServerStatusEnum.BAD_CONFIGURATION));
        String params = "{\"patname\":\"dkjksdf\",\"patimg\":\"patimg\"}";
        RegistrationVO hisPatientVO = HisPatientInfoService.callHisWebservice(params);
        Assert.assertNull(hisPatientVO);
    }

    @Test
    public void testCallHisInterface() {
        try {
            hisPatientHttpClient = PowerMockito.mock(HisPatientHttpClient.class);
            PowerMockito.whenNew(HisPatientHttpClient.class).withAnyArguments().thenReturn(hisPatientHttpClient);

            String params = "{\"patname\":\"dkjksdf\",\"patimg\":\"patimg\"}";
            PowerMockito.when(hisPatientHttpClient.sendMessage()).thenReturn(params);
            HisPatientInfoService.callHisWebservice(params);
            Mockito.verify(hisPatientHttpClient).sendMessage();
            Mockito.verify(hisPatientHttpClient).close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
