package com.varian.oiscn.base.util;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.anyString;

/**
 * Created by gbt1220 on 11/7/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemConfigPool.class, DeviceUtil.class})
public class DeviceUtilTest {

    @Test
    public void givenWhenActivityCodeIsNullThenReturnEmptyList() {
        Assert.assertEquals(0, DeviceUtil.getDevicesByActivityCode("1", null).size());
    }

    @Test
    public void givenActivityCodeWhenGetDevicesThenReturnList() throws Exception {
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(anyString())).thenReturn(new ArrayList<>());
        CarePathTemplate template = MockDtoUtil.givenCarePathTemplate();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(anyString())).thenReturn(template);
        Assert.assertEquals(0, DeviceUtil.getDevicesByActivityCode("template", template.getActivities().get(0).getActivityCode()).size());
    }
}
