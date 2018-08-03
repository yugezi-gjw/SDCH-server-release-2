package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Slot;
import com.varian.oiscn.core.slot.SlotDto;

import java.text.SimpleDateFormat;

/**
 * Created by fmk9441 on 2017-02-28.
 */
public class SlotAssembler {
    private SlotAssembler() {

    }

    /**
     * Return DTO from Fhir Slot.<br>
     *
     * @param slot Fhir Slot
     * @return DTO
     */
    public static SlotDto getSlotDto(Slot slot) {
        SlotDto slotDto = new SlotDto();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (slot != null) {
            if (slot.hasStart()) {
                slotDto.setStart(formatter.format(slot.getStart()));
            }
            if (slot.hasEnd()) {
                slotDto.setEnd(formatter.format(slot.getEnd()));
            }
            if (slot.hasStatus()) {
                slotDto.setStatus(slot.getStatus().toCode());
            }
        }
        return slotDto;
    }
}