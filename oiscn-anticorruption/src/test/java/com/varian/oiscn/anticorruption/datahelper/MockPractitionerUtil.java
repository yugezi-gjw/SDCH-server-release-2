package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Group;
import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import org.hl7.fhir.dstu3.model.*;

import java.util.*;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReference;

/**
 * Created by fmk9441 on 2017-03-24.
 */
public final class MockPractitionerUtil {
    private MockPractitionerUtil() {
    }

    public static Practitioner givenAPractitioner() {
        Practitioner practitioner = new Practitioner();
        practitioner.setId("PractitionerId");
        practitioner.setDisplayName(new StringType("PractitionerName"));
        Address address = new Address();
        address.setUse(Address.AddressUse.WORK);
        address.setExtension(Arrays.asList(new Extension(com.varian.fhir.resources.Address.EXTENSION_TELEPHONE1).setValue(new StringType("PractitionerPhone"))));
        practitioner.setAddress(Arrays.asList(address));

        return practitioner;
    }

    public static PractitionerDto givenAPractitionerDto() {
        PractitionerDto practitionerDto = new PractitionerDto();
        practitionerDto.setId("PractitionerId");
        practitionerDto.setName("PractitionerName");
        return practitionerDto;
    }

    public static List<Practitioner> givenAPractitionerList() {
        return Arrays.asList(givenAPractitioner());
    }

    public static List<PractitionerDto> givenAPractitionerDtoList() {
        return Arrays.asList(givenAPractitionerDto());
    }

    public static Bundle givenAPractitionerBundle() {
        Bundle bundle = new Bundle();

        Bundle.BundleEntryComponent bundleEntryComponent1 = new Bundle.BundleEntryComponent();
        Practitioner practitioner1 = new Practitioner();
        practitioner1.setId("PractitionerId1");
        practitioner1.addName(new HumanName().setFamily("PractitionerName1").setUse(HumanName.NameUse.OFFICIAL));
        practitioner1.setOncologist(new BooleanType(true));
        bundleEntryComponent1.setResource(practitioner1);

        Bundle.BundleEntryComponent bundleEntryComponent2 = new Bundle.BundleEntryComponent();
        Practitioner practitioner2 = new Practitioner();
        practitioner2.setId("PractitionerId2");
        practitioner2.addName(new HumanName().setFamily("PractitionerName2").setUse(HumanName.NameUse.OFFICIAL));
        practitioner2.setOncologist(new BooleanType(true));
        bundleEntryComponent2.setResource(practitioner2);

        Bundle.BundleEntryComponent bundleEntryComponent3 = new Bundle.BundleEntryComponent();
        Practitioner practitioner3 = new Practitioner();
        practitioner3.setId("PractitionerId3");
        practitioner3.addName(new HumanName().setFamily("PractitionerName3").setUse(HumanName.NameUse.OFFICIAL));
        practitioner3.setOncologist(new BooleanType(true));
        bundleEntryComponent3.setResource(practitioner3);

        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent1);
        lstBundleEntryComponents.add(bundleEntryComponent2);
        lstBundleEntryComponents.add(bundleEntryComponent3);
        bundle.setEntry(lstBundleEntryComponents);

        return bundle;
    }

    public static Map<Group, List<Reference>> givenAMapofGroupWithMemberRefList() {
        Map<Group, List<Reference>> hmGroupWithMemberRefList = new HashMap<>();
        Group group = MockGroupUtil.givenAGroup();
        List<Reference> lstReference = Arrays.asList(getReference("PractitionerId", "PractitionerName", "Practitioner", false));
        hmGroupWithMemberRefList.put(group, lstReference);
        return hmGroupWithMemberRefList;
    }
}
