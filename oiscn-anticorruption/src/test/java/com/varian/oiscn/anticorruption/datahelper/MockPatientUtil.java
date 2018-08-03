package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Address;
import com.varian.fhir.resources.Patient;
import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.core.patient.PatientDto;
import org.hl7.fhir.dstu3.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-03-24.
 */
public class MockPatientUtil {
    private MockPatientUtil() {
    }

    public static Patient givenAPatient() {
        Patient patient = new Patient();
        patient.setId("PatientId");
        patient.getIdentifier().add(new Identifier().setType(new CodeableConcept().addCoding(new Coding().setCode("ARIA ID2"))).setValue("ARIAID"));
        patient.getIdentifier().add(new Identifier().setType(new CodeableConcept().addCoding(new Coding().setCode("SSN"))).setValue("SSN"));
        patient.getIdentifier().add(new Identifier().setType(new CodeableConcept().addCoding(new Coding().setCode("ARIA ID1"))).setValue("HISID"));
        patient.getName().add(new HumanName().setFamily("ChineseName").setUse(HumanName.NameUse.OFFICIAL));
        patient.setBirthDate(new Date());
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        patient.getTelecom().add(new ContactPoint().setUse(ContactPoint.ContactPointUse.HOME).setValue("Telephone"));
        patient.getAddress().add(new Address().setUse(Address.AddressUse.HOME).addLine("Address"));
        Patient.ContactComponent contactComponent = patient.addContact();
        contactComponent.addRelationship().getCodingFirstRep().setCode("C");
        contactComponent.addExtension().setUrl(Patient.EXTENSION_CONTACT_SUBRELATION).setValue(new StringType("Family"));
        contactComponent.getName().setUse(HumanName.NameUse.OFFICIAL);
        contactComponent.getName().setFamily("ContactName");
        contactComponent.addTelecom(new ContactPoint().setUse(ContactPoint.ContactPointUse.HOME).setValue("ContactPhone"));
        List<Patient.PatientDoctorRelationship> lstPhysician = new ArrayList<>();
        Patient.PatientDoctorRelationship patientDoctorRelationship = new Patient.PatientDoctorRelationship();
        patientDoctorRelationship.setPhysician(new Reference().setReference("PractitionerId").setDisplay("PractitionerName"));
        patientDoctorRelationship.setPrimary(new BooleanType(true));
        lstPhysician.add(patientDoctorRelationship);
        patient.setPhysicians(lstPhysician);
        Practitioner practitioner = MockPractitionerUtil.givenAPractitioner();
        patient.getContained().add(practitioner);
        Patient.Label lblAlert = new Patient.Label();
        lblAlert.setLabelId(new StringType("UserDefAttribute01"));
        lblAlert.setDisplay(new StringType("Alert"));
        lblAlert.setValue(new StringType("WarningText"));
        List<Patient.Label> lstLabel = new ArrayList<>();
        lstLabel.add(lblAlert);
        patient.setLabels(lstLabel);

        patient.setActive(true);
        patient.setCreatedDate(new DateTimeType(new Date()));
        Attachment attachment = new Attachment();
        attachment.setData(new byte[]{0, 0, -20});
        patient.setPhoto(Arrays.asList(attachment));
        patient.setPatientCarePath(new Reference().setReference("CarePathId").setDisplay("CarePathName"));

        return patient;
    }

    public static PatientDto givenAPatientDto() {
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer("123456");
        patientDto.setAriaId("ARIAID");
        patientDto.setHisId("HISID");
        patientDto.setChineseName("ChineseName");
        patientDto.setEnglishName("EnglishName");
        patientDto.setGender("Male");
        patientDto.setTelephone("Telephone");
        patientDto.setAddress("Address");
        patientDto.setNationalId("NationId");
        patientDto.setBirthday(new Date());
        patientDto.setContactPerson("ContactPerson");
        patientDto.setContactPhone("ContactPhone");
        patientDto.setPhysicianId("PractitionerId1");
        PatientDto.PatientLabel patientLabel = new PatientDto.PatientLabel();
        patientLabel.setLabelId("Id");
        patientLabel.setLabelTag("Tag");
        patientLabel.setLabelText("Text");
        patientDto.setLabels(Arrays.asList(patientLabel));
        patientDto.setCreatedDT(new Date());
        patientDto.setPhoto(new byte[]{0, 0, -20});
        patientDto.setCpTemplateId("CarePathId");
        patientDto.setCpTemplateName("CarePathName");
        return patientDto;
    }

    public static Bundle givenAPatientBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent1 = new Bundle.BundleEntryComponent();
        bundleEntryComponent1.setResource(givenAPatient());
        Bundle.BundleEntryComponent bundleEntryComponent2 = new Bundle.BundleEntryComponent();
        bundleEntryComponent2.setResource(givenAPatient());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent1);
        lstBundleEntryComponents.add(bundleEntryComponent2);
        bundle.setTotal(2);
        bundle.setEntry(lstBundleEntryComponents);
        return bundle;
    }

    public static List<Patient> givenAPatientList() {
        return Arrays.asList(givenAPatient());
    }
}