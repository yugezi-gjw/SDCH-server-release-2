package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Group;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReference;

/**
 * Created by fmk9441 on 2017-03-24.
 */
public final class MockGroupUtil {
    private MockGroupUtil() {
    }

    public static Group givenAGroup() {
        Group group = new Group();
        group.setId("GroupId");
        group.setName("GroupName");
        Group.GroupMemberComponent groupMemberComponent = new org.hl7.fhir.dstu3.model.Group.GroupMemberComponent();
        groupMemberComponent.setEntity(getReference("PractitionerId", "PractitionerName", "Practitioner", false));
        group.addMember(groupMemberComponent);
        return group;
    }

    public static List<Reference> givenAGroupMemberRefList() {
        return Arrays.asList(getReference("PractitionerId", "PractitionerName", "Practitioner", false));
    }

    public static Bundle givenAGroupBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenAGroup());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponents);
        bundle.setTotal(1);
        return bundle;
    }

    public static List<Group> givenAGroupList() {
        return Arrays.asList(givenAGroup());
    }
}