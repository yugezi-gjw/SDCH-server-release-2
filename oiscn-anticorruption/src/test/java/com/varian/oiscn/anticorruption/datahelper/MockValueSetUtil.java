package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.ValueSet;
import org.hl7.fhir.dstu3.model.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fmk9441 on 2017-06-19.
 */
public class MockValueSetUtil {
    private MockValueSetUtil() {
    }

    public static ValueSet givenAValueSet() {
        ValueSet valueSet = new ValueSet();
        valueSet.setId("Id");
        ValueSet.ValueSetComposeComponent valueSetComposeComponent = new ValueSet.ValueSetComposeComponent();
        ValueSet.ConceptSetComponent conceptSetComponent = new ValueSet.ConceptSetComponent();
        ValueSet.ConceptReferenceComponent conceptReferenceComponent = new ValueSet.ConceptReferenceComponent();
        conceptReferenceComponent.setCode("Code");
        conceptReferenceComponent.setDisplay("Desc");
        conceptSetComponent.setConcept(Arrays.asList(conceptReferenceComponent));
        valueSetComposeComponent.setInclude(Arrays.asList(conceptSetComponent));
        valueSet.setCompose(valueSetComposeComponent);
        return valueSet;
    }

    public static List<ValueSet> givenAValueSetList() {
        return Arrays.asList(givenAValueSet());
    }

    public static Bundle givenAValueSetBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenAValueSet());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponents);

        return bundle;
    }
}