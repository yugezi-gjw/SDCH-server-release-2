package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Slot;
import com.varian.oiscn.anticorruption.datahelper.MockSlotUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRSlotInterface;
import com.varian.oiscn.core.slot.SlotDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Created by fmk9441 on 2017-02-21.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SlotAntiCorruptionServiceImp.class})
public class SlotAntiCorruptionServiceImpTest {
    private FHIRSlotInterface fhirSlotInterface;
    private SlotAntiCorruptionServiceImp slotAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirSlotInterface = PowerMockito.mock(FHIRSlotInterface.class);
        PowerMockito.whenNew(FHIRSlotInterface.class).withNoArguments().thenReturn(fhirSlotInterface);
        slotAntiCorruptionServiceImp = new SlotAntiCorruptionServiceImp();
    }

    @Test
    public void givenADeviceIdAndDateWhenQueryThenReturnAllSlots() throws ParseException {
        final String deviceId = "DeviceId";
        final Date date = new Date();
        List<Slot> lstSlot = MockSlotUtil.givenASlotList();
        PowerMockito.when(fhirSlotInterface.querySlotListByDeviceIdAndDate(deviceId, date)).thenReturn(lstSlot);
        List<SlotDto> lstSlotDto = slotAntiCorruptionServiceImp.querySlotsByDeviceIdAndDate(deviceId, date);
        Assert.assertThat(1, is(lstSlotDto.size()));
    }
}
