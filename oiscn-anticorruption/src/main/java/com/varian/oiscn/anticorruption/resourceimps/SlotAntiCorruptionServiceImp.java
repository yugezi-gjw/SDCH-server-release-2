package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Slot;
import com.varian.oiscn.anticorruption.assembler.SlotAssembler;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRSlotInterface;
import com.varian.oiscn.core.slot.SlotDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-02-21.
 */
public class SlotAntiCorruptionServiceImp {
    private FHIRSlotInterface fhirSlotInterface;

    /**
     * Default Constructor.<br>
     */
    public SlotAntiCorruptionServiceImp() {
        fhirSlotInterface = new FHIRSlotInterface();
    }

    /**
     * Return Slot List.<br>
     *
     * @param deviceId Device Id
     * @param date     Date
     * @return Slot List
     */
    public List<SlotDto> querySlotsByDeviceIdAndDate(String deviceId, Date date) {
        List<SlotDto> lstSlotDto = new ArrayList<>();
        List<Slot> lstSlot = fhirSlotInterface.querySlotListByDeviceIdAndDate(deviceId, date);
        if (!lstSlot.isEmpty()) {
            lstSlot.forEach(slot -> lstSlotDto.add(SlotAssembler.getSlotDto(slot)));
        }

        return lstSlotDto;
    }
}
