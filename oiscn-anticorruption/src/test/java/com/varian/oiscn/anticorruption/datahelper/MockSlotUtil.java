package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Slot;
import org.hl7.fhir.dstu3.model.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fmk9441 on 2017-03-24.
 */
public final class MockSlotUtil {
    private MockSlotUtil() {
    }

    public static Slot givenASlot() throws ParseException {
        Slot slot = new Slot();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        slot.setId("2017-03-01 0815 - 0830");
        slot.setStart(formatter.parse("2017-03-01 08:15:00"));
        slot.setEnd(formatter.parse("2017-03-01 08:30:00"));
        slot.setStatus(org.hl7.fhir.dstu3.model.Slot.SlotStatus.FREE);

        return slot;
    }

    public static List<Slot> givenASlotList() throws ParseException {
        return Arrays.asList(givenASlot());
    }

    public static Bundle givenASlotBundle() throws ParseException {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenASlot());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponents);

        return bundle;
    }
}