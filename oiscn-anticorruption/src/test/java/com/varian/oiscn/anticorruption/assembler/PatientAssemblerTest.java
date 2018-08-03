package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Patient;
import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.datahelper.MockPatientUtil;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.util.I18nReader;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Patient.ContactComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;

/**
 * Created by fmk9441 on 2017-01-16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientAssembler.class, PatientIdMapper.class})
public class PatientAssemblerTest {
    @InjectMocks
    private PatientAssembler patientAssembler;

    @Before
    public void setup(){
        PowerMockito.mockStatic(PatientIdMapper.class);
        PowerMockito.when(PatientIdMapper.getPatientId1Mapper()).thenReturn(PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID);
        PowerMockito.when(PatientIdMapper.getPatientId2Mapper()).thenReturn(PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID);
    }

    @Test
    public void givenAPatientDtoWhenConvertThenReturnPatient() throws Exception {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        Patient patient = PatientAssembler.getPatient(patientDto);
        Assert.assertNotNull(patient);
    }

    @Test
    public void doUpdatePatient() throws Exception {
        Patient patient = MockPatientUtil.givenAPatient();
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        PatientAssembler.updatePatient(patient, patientDto);
        Assert.assertTrue("Male".equalsIgnoreCase(patient.getGender().toCode()));
        Assert.assertEquals("PractitionerId1", getReferenceValue(patient.getPhysicians().stream().filter(p -> p.getPrimary().getValue()).findAny().get().getPhysician()));
        patientDto.setPhysicianId("PractitionerId");
        PatientAssembler.updatePatient(patient, patientDto);
        Assert.assertEquals("PractitionerId", getReferenceValue(patient.getPhysicians().stream().filter(p -> p.getPrimary().getValue()).findAny().get().getPhysician()));
    }

    @Test
    public void givenAPatientDtoWhenConvertAndEnglishNameIsBlankThenReturnPatient() throws Exception {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        patientDto.setEnglishName("");
        Patient patient = PatientAssembler.getPatient(patientDto);
        Assert.assertNotNull(patient);
    }

    @Test
    public void testGetPatient() throws Exception {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        String positiveSign = PowerMockito.mock(String.class);
        String insuranceType = PowerMockito.mock(String.class);
        String ecogScore = PowerMockito.mock(String.class);
        String ecogDesc = PowerMockito.mock(String.class);
        patientDto.setPositiveSign(positiveSign);
        patientDto.setInsuranceType(insuranceType);
        patientDto.setEcogScore(ecogScore);
        patientDto.setEcogDesc(ecogDesc);
        Patient patient = PatientAssembler.getPatient(patientDto);
        PatientDto actualDto = PatientAssembler.getPatientDto(patient);

        Assert.assertNotNull(actualDto);
        // Positive Sign would be saved in Local DB.
        // Assert.assertEquals(positiveSign, actualDto.getPositiveSign());
        // Assert.assertEquals(insuranceType, actualDto.getInsuranceType());
        // Ecog would be saved in Local DB.
        // Assert.assertEquals(ecogScore, actualDto.getEcogScore());
        // Assert.assertEquals(ecogDesc, actualDto.getEcogDesc());
    }

    @Test
    public void testGetPatientInPatient() throws Exception {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        String patientSource = I18nReader.getLocaleValueByKey("Patient.InPatient");
        patientDto.setPatientSource(patientSource);
        Patient patient = PatientAssembler.getPatient(patientDto);
        PatientDto actualDto = PatientAssembler.getPatientDto(patient);

        Assert.assertNotNull(actualDto);
        Assert.assertEquals(patientSource, actualDto.getPatientSource());
    }

    @Test
    public void testGetPatientOutPatient() throws Exception {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        String patientSource = I18nReader.getLocaleValueByKey("Patient.OutPatient");
        patientDto.setPatientSource(patientSource);
        Patient patient = PatientAssembler.getPatient(patientDto);
        PatientDto actualDto = PatientAssembler.getPatientDto(patient);

        Assert.assertNotNull(actualDto);
        Assert.assertEquals(patientSource, actualDto.getPatientSource());
    }

    @Test
    public void findPrimaryContactNormal() throws Exception {
        List<org.hl7.fhir.dstu3.model.Patient.ContactComponent> contactList = new ArrayList<>();
        ContactComponent contactComponent1 = new ContactComponent();
        ContactComponent contactComponent2 = new ContactComponent();
        ContactPoint t1 = new ContactPoint();
        t1.setUse(ContactPoint.ContactPointUse.HOME).setValue("mockPhone");
        ContactPoint t2 = new ContactPoint();
        t2.setUse(ContactPoint.ContactPointUse.HOME).setValue("mockPhone");
        Extension ex1 = new Extension(Patient.EXTENSION_CONTACT_IS_PRIMARY, new BooleanType(false));
        Extension ex2 = new Extension(Patient.EXTENSION_CONTACT_IS_PRIMARY, new BooleanType(true));
        contactComponent1.addExtension(ex1);
        contactComponent2.addExtension(ex2);

        contactComponent1.setId("111");
        contactComponent1.addTelecom(t1);
        contactComponent2.setId("22");
        contactComponent2.addTelecom(t2);
        contactList.add(contactComponent1);
        contactList.add(contactComponent2);
        Patient.ContactComponent primaryContact = PatientAssembler.findPrimaryContact(contactList);
        Assert.assertNotNull(primaryContact);
    }

    @Test
    public void findPrimaryContactNoPrimary() throws Exception {
        List<org.hl7.fhir.dstu3.model.Patient.ContactComponent> contactList = new ArrayList<>();
        ContactComponent contactComponent1 = new ContactComponent();
        ContactComponent contactComponent2 = new ContactComponent();
        ContactPoint t1 = new ContactPoint();
        ContactPoint t2 = new ContactPoint();
        Extension ex1 = new Extension(Patient.EXTENSION_CONTACT_IS_PRIMARY, new BooleanType(false));
        Extension ex2 = new Extension(Patient.EXTENSION_CONTACT_IS_PRIMARY, new BooleanType(false));
        contactComponent1.addExtension(ex1);
        contactComponent2.addExtension(ex2);

        contactComponent1.setId("111");
        contactComponent1.addTelecom(t1);
        contactComponent2.setId("22");
        contactComponent2.addTelecom(t2);
        contactList.add(contactComponent1);
        contactList.add(contactComponent2);
        Patient.ContactComponent primaryContact = PatientAssembler.findPrimaryContact(contactList);
        Assert.assertNotNull(primaryContact);
    }

    @Test
    public void givenAPatientWhenConvertThenReturnPatientDto() throws Exception {
        Patient patient = MockPatientUtil.givenAPatient();
        PowerMockito.mockStatic(PatientIdMapper.class);
        PowerMockito.when(PatientIdMapper.getPatientId1Mapper()).thenReturn("hisId");
        PowerMockito.when(PatientIdMapper.getPatientId2Mapper()).thenReturn("ariaId");
        PatientDto patientDto = PatientAssembler.getPatientDto(patient);
        Assert.assertNotNull(patientDto);
        Assert.assertEquals(patientDto.getHisId(), "HISID");
        Assert.assertEquals(patientDto.getAriaId(), "ARIAID");
        Assert.assertEquals(patientDto.getNationalId(), "SSN");
        Assert.assertEquals(patientDto.getChineseName(), "ChineseName");
    }
}