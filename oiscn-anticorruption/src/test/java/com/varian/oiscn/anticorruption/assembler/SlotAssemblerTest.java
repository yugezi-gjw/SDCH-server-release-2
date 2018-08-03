package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Slot;
import com.varian.oiscn.anticorruption.datahelper.MockSlotUtil;
import com.varian.oiscn.core.slot.SlotDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by fmk9441 on 2017-02-28.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SlotAssembler.class})
public class SlotAssemblerTest {
    @InjectMocks
    private SlotAssembler slotAssembler;

    @Test
    public void givenASlotWhenConvertThenReturnSlotDto() throws Exception {
        Slot slot = MockSlotUtil.givenASlot();
        SlotDto slotDto = SlotAssembler.getSlotDto(slot);
        Assert.assertNotNull(slotDto);
        Assert.assertEquals(slotDto.getStart(), "2017-03-01 08:15");
        Assert.assertEquals(slotDto.getEnd(), "2017-03-01 08:30");
        Assert.assertEquals(slotDto.getStatus(), "free");
    }
}