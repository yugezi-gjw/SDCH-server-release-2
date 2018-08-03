package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Flag;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fmk9441 on 2017-06-23.
 */
public class MockFlagUtil {
    private MockFlagUtil() {
    }

    public static Flag givenAFlag() {
        Flag flag = new Flag();
        flag.setSubject(new Reference().setReference("PatientID"));
        flag.setCode(new CodeableConcept().addCoding(new Coding().setCode("FlagCode")));
        flag.setStatus(org.hl7.fhir.dstu3.model.Flag.FlagStatus.ACTIVE);
        return flag;
    }

    public static Bundle givenAFlagBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenAFlag());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponents);
        bundle.setTotal(5);
        return bundle;
    }

    public static List<Flag> givenAFlagList() {
        return Arrays.asList(givenAFlag());
    }
}
