package com.varian.oiscn.appointment.resource;

import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfiguration;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.appointment.calling.*;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 10/25/2017
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CallingWaitingResource.class, HisPatientInfoConfigService.class})
public class CallingWaitingResourceTest {

    private Configuration configuration;

    private Environment environment;

    private CallingWaitingResource resource;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        resource = new CallingWaitingResource(configuration, environment);
        PowerMockito.mockStatic(HisPatientInfoConfigService.class);
    }

    @Test
    public void testCallingSystemDisable() {
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(null);
        Response response = resource.getConfig();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertSame("", response.getEntity());


        HisPatientInfoConfiguration configuration = PowerMockito.mock(HisPatientInfoConfiguration.class);
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(configuration);
        PowerMockito.when(configuration.isCallingSystemEnable()).thenReturn(false);
        response = resource.getConfig();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        Assert.assertSame("", response.getEntity());
    }

    @Test
    public void givenNoParameterWhenCallingConfigExistsThenReturnResponseOK() {
        HisPatientInfoConfiguration hisPatientInfoConfiguration = PowerMockito.mock(HisPatientInfoConfiguration.class);
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(hisPatientInfoConfiguration);
        PowerMockito.when(hisPatientInfoConfiguration.isCallingSystemEnable()).thenReturn(true);
        PowerMockito.when(configuration.getCallingConfig()).thenReturn(assembleCallingConfig());
        Response response = resource.getConfig();
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenNoParameterWhenCallingConfigNotExistsThenReturnResponseNotFound() {
        HisPatientInfoConfiguration hisPatientInfoConfiguration = PowerMockito.mock(HisPatientInfoConfiguration.class);
        PowerMockito.when(HisPatientInfoConfigService.getConfiguration()).thenReturn(hisPatientInfoConfiguration);
        PowerMockito.when(hisPatientInfoConfiguration.isCallingSystemEnable()).thenReturn(true);
        PowerMockito.when(configuration.getCallingConfig()).thenReturn(null);
        Response response = resource.getConfig();
        assertThat(response.getStatusInfo(), equalTo(Response.Status.NOT_FOUND));
    }

    private CallingConfig assembleCallingConfig() {
        CallingConfig callingConfig = new CallingConfig();
        callingConfig.setLogo("Vairan logo");
        callingConfig.setCompanyInfo("Varian");
        callingConfig.setSystemInfo(new SystemInfo());

        CheckInGuide checkInGuide = new CheckInGuide();
        checkInGuide.setSummaryGuide(assembleSummaryGuide());
        checkInGuide.setDevicesGuide(assembleDevicesGuide());

        CallingPatientGuide callingPatientGuide = new CallingPatientGuide();
        callingPatientGuide.setSummaryGuide(assembleSummaryGuide());
        callingPatientGuide.setDevicesGuide(assembleDevicesGuide());

        callingConfig.setCheckInGuide(checkInGuide);
        callingConfig.setCallingPatientGuide(callingPatientGuide);

        return callingConfig;
    }

    private SummaryGuide assembleSummaryGuide() {
        SummaryGuide summaryGuide = new SummaryGuide();
        List<String> texts = new ArrayList<>();
        texts.add("text1");
        texts.add("text2");
        texts.add("text3");
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("image url1");
        imageUrls.add("image url2");
        imageUrls.add("image url3");
        List<String> videoUrls = new ArrayList<>();
        videoUrls.add("video url1");
        videoUrls.add("videoo url2");
        videoUrls.add("video url3");
        summaryGuide.setTexts(texts);
        summaryGuide.setImageUrls(imageUrls);
        summaryGuide.setVideoUrls(videoUrls);
        return summaryGuide;
    }

    private DeviceGuide assembleDeviceGuide() {
        DeviceGuide deviceGuide = new DeviceGuide();
        List<String> texts = new ArrayList<>();
        texts.add("text1");
        texts.add("text2");
        texts.add("text3");
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("image url1");
        imageUrls.add("image url2");
        imageUrls.add("image url3");
        List<String> videoUrls = new ArrayList<>();
        videoUrls.add("video url1");
        videoUrls.add("videoo url2");
        videoUrls.add("video url3");
        deviceGuide.setTexts(texts);
        deviceGuide.setImageUrls(imageUrls);
        deviceGuide.setVideoUrls(videoUrls);
        return deviceGuide;
    }

    private Device assembleDevice() {
        Device device = new Device();
        device.setDeviceId("deviceId");
        device.setDeviceName("deviceName");
        device.setAriaDeviceId("ariaDeviceId");
        device.setDeviceRoom("deviceRoom");
        device.setDeviceGuide(assembleDeviceGuide());
        return device;
    }

    private DevicesGuide assembleDevicesGuide() {
        DevicesGuide devicesGuide = new DevicesGuide();
        List<Device> devices = new ArrayList<>();
        devices.add(assembleDevice());
        devicesGuide.setDevices(devices);
        return devicesGuide;
    }


}
