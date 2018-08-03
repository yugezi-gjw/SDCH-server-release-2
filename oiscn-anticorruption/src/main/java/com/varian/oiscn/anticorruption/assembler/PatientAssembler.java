package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Patient;
import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.converter.EnumerationHelper;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.util.I18nReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Patient.ContactComponent;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.varian.oiscn.anticorruption.converter.DataHelper.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by gbt1220 on 12/23/2016.
 */
@Slf4j
public class PatientAssembler {
    /**
     * Locale Text: Family
     */
    public static final String FAMILY = I18nReader.getLocaleValueByKey("PatientAssembler.family");
    /** Locale Text: InPatient */
    public static final String IN_PATIENT = I18nReader.getLocaleValueByKey("Patient.InPatient");
    /** Locale Text: OutPatient */
    public static final String OUT_PATIENT = I18nReader.getLocaleValueByKey("Patient.OutPatient");

    protected static final String RELATION_CODE_CONTACT = "C";
    protected static final String RELATION_CODE_EMPLOY = "E";

    private PatientAssembler() {

    }

    /**
     * Return Fhir Patient from DTO.<br>
     * @param patientDto DTO
     * @return Fhir Patient
     */
    public static Patient getPatient(PatientDto patientDto) {
        Patient patient = new Patient();
        if (patientDto != null) {
            setPatientName(patient, patientDto.getChineseName(), patientDto.getPinyin());
            patient.setBirthDate(patientDto.getBirthday());
            patient.setGender(isNotBlank(patientDto.getGender()) ? EnumerationHelper.getGender(patientDto.getGender()) : null);

            String patientId1;
            if (StringUtils.equals(PatientIdMapper.getPatientId1Mapper(), PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID)) {
                patientId1 = patientDto.getAriaId();
            } else {
                patientId1 = patientDto.getHisId();
            }
            String patientId2;
            if (StringUtils.equals(PatientIdMapper.getPatientId2Mapper(), PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID)) {
                patientId2 = patientDto.getAriaId();
            } else {
                patientId2 = patientDto.getHisId();
            }

            setPatientIdentifier(patient, "ARIA ID1", patientId1);
            setPatientIdentifier(patient, "ARIA ID2",  patientId2);
            setPatientIdentifier(patient, "SSN", patientDto.getNationalId());
            setPatientTelephone(patient, patientDto.getTelephone());
            setPatientAddress(patient, patientDto.getAddress());
            // setPatientContact(patient, patientDto.getContactPerson(), patientDto.getContactPhone());
            patient = updatePatientContact(patient, patientDto.getContactPerson(), patientDto.getContactPhone());
            setPatientPrimaryPhysician(patient, patientDto.getPhysicianId(),patientDto.getPhysicianName());
            setPatientLabel(patient, patientDto.getLabels());
            setPatientPhoto(patient, patientDto.getPhoto());
            setPatientCarePath(patient, patientDto.getCpTemplateId(), patientDto.getCpTemplateName());
            setIsInPatientToResource(patient, patientDto.getPatientSource());

            setPatientHistory(patient, patientDto.getPatientHistory());
        }
        return patient;
    }

    /**
     * Return DTO from Fhir Patient.<br>
     * @param patient Patient
     * @return DTO
     */
    public static PatientDto getPatientDto(Patient patient) {
        PatientDto patientDto = new PatientDto();
        if (patient != null) {
            patientDto.setPatientSer(patient.hasIdElement() ? patient.getIdElement().getIdPart() : null);
            setPatientDtoIdentifier(patient, patientDto);
            setPatientDtoName(patient, patientDto);
            patientDto.setGender(patient.hasGender() ? patient.getGender().toString() : null);
            patientDto.setBirthday(patient.hasBirthDate() ? patient.getBirthDate() : null);
            setPatientDtoAddress(patient, patientDto);
            setPatientDtoTelephone(patient, patientDto);
            // setPatientDtoContact(patient, patientDto);
            patientDto = updatePatientDtoContact(patient, patientDto);
            setPatientDtoPrimaryPhysician(patient, patientDto);
            setPatientDtoAlert(patient, patientDto);
            setPatientDtoPhoto(patient, patientDto);
            setPatientDtoCarePath(patient, patientDto);
            patientDto.setPatientSource(getIsInPatientFromResource(patient));

            setPatientDtoPatientHistory(patient, patientDto);
            setPatientPhysicianComment(patient, patientDto);
            patientDto.setCreatedDT(null != patient.getCreatedDate() ? patient.getCreatedDate().getValue() : null);
        }
        return patientDto;
    }

    /**
     * Update Fhir Patient from DTO.<br>
     * @param patient Fhir Patient
     * @param patientDto DTO
     */
    public static void updatePatient(Patient patient, PatientDto patientDto) {
        if (patient != null && patientDto != null) {
            setPatientName(patient, patientDto.getChineseName(), patientDto.getPinyin());
            patient.setBirthDate(patientDto.getBirthday());
            patient.setGender(StringUtils.isNotEmpty(patientDto.getGender()) ? EnumerationHelper.getGender(patientDto.getGender()) : null);
            setPatientIdentifier(patient, "SSN", patientDto.getNationalId());

            if (StringUtils.equals(PatientIdMapper.getPatientId2Mapper(), PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID)) {
                setPatientIdentifier(patient, "ARIA ID2", patientDto.getAriaId());
                setPatientIdentifier(patient, "ARIA ID1", patientDto.getHisId());
            } else {
                setPatientIdentifier(patient, "ARIA ID1", patientDto.getAriaId());
                setPatientIdentifier(patient, "ARIA ID2", patientDto.getHisId());
            }

            //setPatientContact(patient, patientDto.getContactPerson(), patientDto.getContactPhone());
            patient = updatePatientContact(patient, patientDto.getContactPerson(), patientDto.getContactPhone());
            setPatientTelephone(patient, patientDto.getTelephone());
            setPatientAddress(patient, patientDto.getAddress());
            setPatientPrimaryPhysician(patient, patientDto.getPhysicianId(),patientDto.getPhysicianName());
            setPatientLabel(patient, patientDto.getLabels());
            setPatientPhoto(patient, patientDto.getPhoto());
            setIsInPatientToResource(patient, patientDto.getPatientSource());
            setPatientHistory(patient, patientDto.getPatientHistory());
            setPatientPhysicianComment(patient, "");
        }
    }

    /**
     * Set Fhir Patient Resource isInPatient by PatientSource.<br>
     *
     * @param patient       Fhir Patient Resource
     * @param patientSource Patient Source
     */
    protected static void setIsInPatientToResource(Patient patient, String patientSource) {
        if (IN_PATIENT.equals(patientSource)) {
            // In patient
            patient.setIsInPatient(new BooleanType(true));
//          FHIR强制要求住院病人必须设置roomNumber
            patient.getPatientLocation().setRoomNumber(new StringType("1"));
        } else {
            patient.setIsInPatient(new BooleanType(false));
        }
    }

    /**
     * Get isInPatient from Fhir Patient Resource.<br>
     *
     * @param patient Fhir Patient
     * @return isInPatient locale text
     */
    protected static String getIsInPatientFromResource(Patient patient) {
        // default is out patient.
        String patientSource = OUT_PATIENT;
        if (patient != null && patient.getIsInPatient() != null && patient.getIsInPatient().booleanValue()) {
            // InPatient
            patientSource = IN_PATIENT;
        }
        return patientSource;
    }

    private static void setPatientName(Patient patient, String chineseName, String pinyin) {
        if (isEmpty(chineseName))
            return;
        boolean bExisted = false;
        if (patient.hasName()) {
            Optional<HumanName> humanNameOptional = patient.getName().stream().filter(n -> n.hasUse() && n.getUse().equals(HumanName.NameUse.OFFICIAL)).findAny();
            if (humanNameOptional.isPresent()) {
                bExisted = true;
                HumanName humanName = humanNameOptional.get();
                humanName.setFamily(chineseName).setGiven(Arrays.asList(new StringType(pinyin)));
            }
        }
        if (!bExisted) {
            patient.addName().setUse(HumanName.NameUse.OFFICIAL).setFamily(chineseName).setGiven(Arrays.asList(new StringType(pinyin)));
        }
    }

    private static void setPatientDtoName(Patient patient, PatientDto patientDto) {
        if (patient.hasName()) {
            Optional<HumanName> humanNameOptional = patient.getName().stream().filter(x -> x.hasUse() && x.getUse().equals(HumanName.NameUse.OFFICIAL)).findFirst();
            patientDto.setChineseName(humanNameOptional.isPresent() && humanNameOptional.get().hasFamily() ? humanNameOptional.get().getFamily() : null);
            patientDto.setPinyin(humanNameOptional.isPresent() && humanNameOptional.get().hasGiven() ? humanNameOptional.get().getGiven().get(0).toString() : null);
        }
    }

    private static void setPatientIdentifier(Patient patient, String tag, String value) {
        if (isEmpty(tag) && isEmpty(value))
            return;
        boolean bExisted = false;
        if (patient.hasIdentifier()) {
            Optional<Identifier> identifierOptional = patient.getIdentifier().stream().filter(i -> i.hasType() && i.getType().hasCoding() && tag.equalsIgnoreCase(i.getType().getCodingFirstRep().getCode())).findAny();
            if (identifierOptional.isPresent()) {
                bExisted = true;
                Identifier identifier = identifierOptional.get();
                identifier.setValue(value);
            }
        }
        if (!bExisted) {
            patient.addIdentifier(new Identifier().setType(new CodeableConcept().addCoding(new Coding().setCode(tag))).setValue(value));
        }
    }

    private static void setPatientDtoIdentifier(Patient patient, PatientDto patientDto) {
        if (patient.hasIdentifier()) {

            Optional<Identifier> identifier1 = patient.getIdentifier().stream().filter(x -> x.hasType() && x.getType().hasCoding() && "ARIA ID1".equalsIgnoreCase(x.getType().getCodingFirstRep().getCode())).findFirst();
            if (StringUtils.equals(PatientIdMapper.getPatientId1Mapper(), PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID)) {
                patientDto.setAriaId(identifier1.isPresent() ? identifier1.get().getValue() : null);
            } else {
                patientDto.setHisId(identifier1.isPresent() ? identifier1.get().getValue() : null);
            }

            Optional<Identifier> identifier2 = patient.getIdentifier().stream().filter(x -> x.hasType() && x.getType().hasCoding() && "ARIA ID2".equalsIgnoreCase(x.getType().getCodingFirstRep().getCode())).findFirst();
            if (StringUtils.equals(PatientIdMapper.getPatientId2Mapper(), PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID)) {
                patientDto.setAriaId(identifier2.isPresent() ? identifier2.get().getValue() : null);
            } else {
                patientDto.setHisId(identifier2.isPresent() ? identifier2.get().getValue() : null);
            }

            Optional<Identifier> identifier3 = patient.getIdentifier().stream().filter(x -> x.hasType() && x.getType().hasCoding() && "SSN".equalsIgnoreCase(x.getType().getCodingFirstRep().getCode())).findFirst();
            patientDto.setNationalId(identifier3.isPresent() ? identifier3.get().getValue() : null);
        }
    }

    private static void setPatientTelephone(Patient patient, String phone) {
        if (isEmpty(phone))
            return;
        boolean bExisted = false;
        if (patient.hasTelecom()) {
            Optional<ContactPoint> contactPointOptional = patient.getTelecom().stream().filter(t -> t.hasUse() && t.getUse().equals(ContactPoint.ContactPointUse.HOME)).findAny();
            if (contactPointOptional.isPresent()) {
                bExisted = true;
                ContactPoint contactPoint = contactPointOptional.get();
                contactPoint.setValue(phone);
            }
        }
        if (!bExisted) {
            patient.addTelecom().setUse(ContactPoint.ContactPointUse.HOME).setValue(phone);
        }
    }

    private static void setPatientDtoTelephone(Patient patient, PatientDto patientDto) {
        if (patient.hasTelecom()) {
            Optional<ContactPoint> homeContactPoint = patient.getTelecom().stream().filter(x -> x.hasUse() && x.getUse().equals(ContactPoint.ContactPointUse.HOME)).findFirst();
            patientDto.setTelephone(homeContactPoint.isPresent() && homeContactPoint.get().hasValue() ? homeContactPoint.get().getValue() : null);
        }
    }

    private static void setPatientAddress(Patient patient, String address) {
        if (isEmpty(address))
            return;
        boolean bExisted = false;
        if (patient.hasAddress()) {
            Optional<Address> addressOptional = patient.getAddress().stream().filter(a -> a.hasUse() && a.getUse().equals(Address.AddressUse.HOME)).findAny();
            if (addressOptional.isPresent()) {
                bExisted = true;
                Address homeAddress = addressOptional.get();
                homeAddress.setLine(Arrays.asList(new StringType(address)));
            }
        }
        if (!bExisted) {
            patient.addAddress().setUse(Address.AddressUse.HOME).setLine(Arrays.asList(new StringType(address)));
        }
    }

    private static void setPatientDtoAddress(Patient patient, PatientDto patientDto) {
        if (patient.hasAddress()) {
            Optional<Address> homeAddress = patient.getAddress().stream().filter(x -> x.hasUse() && x.getUse().equals(Address.AddressUse.HOME)).findFirst();
            patientDto.setAddress(homeAddress.isPresent() && homeAddress.get().hasLine() ? homeAddress.get().getLine().get(0).getValue() : null);
        }
    }

//    @Deprecated
//    private static void setPatientContact(Patient patient, String contactName, String contactPhone) {
//        if (isNotEmpty(contactName) || isNotEmpty(contactPhone)) {
//            Patient.ContactComponent contactComponent = getPatientContactComponent(patient);
//            contactComponent.getName().setFamily(contactName);
//            ContactPoint contactPoint = getPatientContactPoint(contactComponent);
//            contactPoint.setUse(ContactPoint.ContactPointUse.HOME).setValue(contactPhone);
//        }
//    }

    /**
     * Update patient contact information for primary contact name, and telecom.<br>
     *
     * @param patient      Patient
     * @param contactName  Primary Contact Name
     * @param contactPhone Primary Contact Phone
     * @return updated Patient
     */
    protected static Patient updatePatientContact(Patient patient, String contactName, String contactPhone) {
        if (isNotBlank(contactName) || isNotBlank(contactPhone)) {
            Patient.ContactComponent primaryContact = null;
            if (patient.hasContact()) {
                // if there is a primary contact, then use it.
                primaryContact = findPrimaryContact(patient.getContact());
            } else {
                // no contact yet. create new one
                primaryContact = new Patient.ContactComponent();
                primaryContact.addExtension(Patient.EXTENSION_CONTACT_IS_PRIMARY, new BooleanType(true));
                // add the new contact to patient
                patient.addContact(primaryContact);
            }

            // update primary name
            primaryContact.getName().setFamily(contactName);

            // update HOME telecom
            ContactPoint homePhone = null;
            if (primaryContact.hasTelecom()) {
                homePhone = findHomePhone(primaryContact.getTelecom());
                homePhone.setUse(ContactPoint.ContactPointUse.HOME).setValue(contactPhone);

            } else {
                // no telecom before, create new one.
                homePhone = new ContactPoint();
                homePhone.setUse(ContactPoint.ContactPointUse.HOME).setValue(contactPhone);
                primaryContact.addTelecom(homePhone);
            }

            // Contact in FHIR must have C relationship for Contact (E for employee)
            if (primaryContact.hasRelationship()) {
//            	CodeableConcept relationship = primaryContact.getRelationshipFirstRep();
//            	if (relationship.hasCoding()) {
//            		relationship.getCodingFirstRep().setCode(RELATION_CODE_CONTACT);
//            	} else {
//            		Coding code = new Coding();
//            		code.setCode(RELATION_CODE_CONTACT);
//					relationship.addCoding(code);
//            	}
            } else {
                CodeableConcept relationship = new CodeableConcept();
                Coding code = new Coding();
                code.setCode(RELATION_CODE_CONTACT);
                relationship.addCoding(code);
                primaryContact.addRelationship(relationship);
            }
        }
        return patient;
    }

    /**
     * Find the home phone of Contact List.<br>
     *
     * @param contactList List
     * @return
     */
    protected static ContactPoint findHomePhone(List<ContactPoint> contactList) {
        ContactPoint homePhone = null;
        for (ContactPoint contactPoint : contactList) {
            if (contactPoint.hasUse() && ContactPoint.ContactPointUse.HOME.equals(contactPoint.getUse())) {
                homePhone = contactPoint;
                break;
            }
        }

        if (homePhone == null) {
            homePhone = contactList.get(0);
        }
        return homePhone;
    }

    /**
     * Find the primary contact, if none, return the first contact.<br>
     *
     * @param contactList Contact List
     * @return primary contact
     */
    protected static Patient.ContactComponent findPrimaryContact(List<ContactComponent> contactList) {
        Patient.ContactComponent primaryContact = null;
        for (ContactComponent contact : contactList) {
            if (contact.hasExtension(Patient.EXTENSION_CONTACT_IS_PRIMARY)) {
                // IS_PRIMARY extension should only one element.
                List<Extension> extIsPrimary = contact.getExtensionsByUrl(Patient.EXTENSION_CONTACT_IS_PRIMARY);
                if (extIsPrimary != null && extIsPrimary.size() > 0) {
                    Type value = extIsPrimary.get(0).getValue();
                    if (value instanceof BooleanType) {
                        BooleanType isPrimary = (BooleanType) value;
                        if (isPrimary.getValue()) {
                            primaryContact = contact;
                            break;
                        }
                    }
                }
            }
        }
        // if there is no primary contact, use the first contact.
        if (primaryContact == null) {
            int minId = Integer.MAX_VALUE;
            int minIdIndex = 0;
            int size = contactList.size();
            for (int index = 0; index < size; index++) {
                // to get the min id when no primary contact
                String idString = contactList.get(index).getId();
                if (NumberUtils.isNumber(idString)) {
                    int id = Integer.parseInt(idString);
                    if (id < minId) {
                        minId = id;
                        minIdIndex = index;
                    }
                }
            }
            primaryContact = contactList.get(minIdIndex);
        }
        if (primaryContact.hasExtension(Patient.EXTENSION_CONTACT_IS_PRIMARY)) {
            List<Extension> extIsPrimary = primaryContact.getExtensionsByUrl(Patient.EXTENSION_CONTACT_IS_PRIMARY);
            extIsPrimary.get(0).setValue(new BooleanType(true));
        } else {
            primaryContact.addExtension(Patient.EXTENSION_CONTACT_IS_PRIMARY, new BooleanType(true));
        }
        return primaryContact;
    }

//    @Deprecated
//    private static void setPatientDtoContact(Patient patient, PatientDto patientDto) {
//        if (patient.hasContact()) {
//            Optional<Patient.ContactComponent> contactComponent = patient.getContact().stream().filter(x -> x.hasRelationship() && StringUtils.equalsIgnoreCase(x.getRelationshipFirstRep().getCodingFirstRep().getCode(), RELATION_CODE_CONTACT)).findFirst();
//            if (contactComponent.isPresent() && contactComponent.get().hasExtension()) {
//                patientDto.setContactPerson(contactComponent.get().getName() != null ? contactComponent.get().getName().getFamily() : "");
//            }
//            if (contactComponent.isPresent() && contactComponent.get().hasTelecom() && contactComponent.get().getTelecomFirstRep().getUse().equals(ContactPoint.ContactPointUse.HOME)) {
//                patientDto.setContactPhone(contactComponent.get().getTelecomFirstRep().getValue());
//            }
//        }
//    }

    /**
     * Return the updated Patient DTO from Patient information.<br>
     *
     * @param patient    Patient
     * @param dto DTO
     */
    protected static PatientDto updatePatientDtoContact(Patient patient, PatientDto dto) {
        if (patient.hasContact()) {
            ContactComponent primaryContact = findPrimaryContact(patient.getContact());
            dto.setContactPerson(primaryContact.getName().getFamily());

            if (primaryContact.hasTelecom()) {
                ContactPoint homePhone = findHomePhone(primaryContact.getTelecom());
                dto.setContactPhone(homePhone.getValue());
            }
        }
        return dto;
    }

    private static void setPatientLabel(Patient patient, List<PatientDto.PatientLabel> lstPatientLabels) {
        if (null == lstPatientLabels || lstPatientLabels.isEmpty()) {
            // clear patient labels when there is no label setting in dto.
            patient.setLabels(new ArrayList<>());
            return;
        }
        if (null != patient.getLabels()) {
            for (PatientDto.PatientLabel patientLabel : lstPatientLabels) {
                Optional<Patient.Label> labelOptional = patient.getLabels().stream().filter(l -> l.getDisplay().getValue().equals(patientLabel.getLabelTag())).findAny();
                if (labelOptional.isPresent()) {
                    Patient.Label label = labelOptional.get();
                    label.setValue(new StringType(patientLabel.getLabelText()));
                } else {
                    addPatientLabelList(patient.getLabels(), patientLabel);
                }

                // FIXME: it's a workaround for multiple Labels with same labelId, and different display. Need change all the label text for same label Id.
                labelOptional = patient.getLabels().stream().filter(
                        l -> !(l.getDisplay().getValue().equals(patientLabel.getLabelTag())) && l.getLabelId().getValue().equals(patientLabel.getLabelId())).findAny();
                if (labelOptional.isPresent()) {
                    Patient.Label label = labelOptional.get();
                    label.setValue(new StringType(patientLabel.getLabelText()));
                }
            }
        } else {
            List<Patient.Label> lstPatientLabel = new ArrayList<>();
            lstPatientLabels.forEach(label -> addPatientLabelList(lstPatientLabel, label));
            patient.setLabels(lstPatientLabel);
        }
    }

    private static void addPatientLabelList(List<Patient.Label> lstPatientLabel, PatientDto.PatientLabel patientLabel) {
        Patient.Label newLabel = new Patient.Label();
        newLabel.setLabelId(new StringType(patientLabel.getLabelId()));
        newLabel.setDisplay(new StringType(patientLabel.getLabelTag()));
        newLabel.setValue(new StringType(patientLabel.getLabelText()));
        lstPatientLabel.add(newLabel);

    }

    private static void setPatientDtoAlert(Patient patient, PatientDto patientDto) {
        if (null != patient.getLabels()) {
            List<PatientDto.PatientLabel> lstPatientLabel = new ArrayList<>();
            patient.getLabels().forEach(label -> {
                PatientDto.PatientLabel patientLabel = new PatientDto.PatientLabel();
                patientLabel.setLabelId(label.getLabelId().getValue());
                patientLabel.setLabelTag(label.getDisplay().getValue());
                patientLabel.setLabelText(label.getValue().getValue());
                lstPatientLabel.add(patientLabel);
            });
            patientDto.setLabels(lstPatientLabel);
        }
    }

    private static void setPatientPhoto(Patient patient, byte[] photo) {
        if (photo != null && photo.length > 0) {
            Attachment attachment = new Attachment();
            attachment.setData(photo);
            patient.setPhoto(Arrays.asList(attachment));
        }
    }

    private static void setPatientHistory(Patient patient, String patientHistory) {
//        TODO mock set fhir patient history method
        log.debug("mock  fhir setPatientHistory");
    }

    private static void setPatientPhysicianComment(Patient patient, String physicianComment) {
//        TODO mock set fhir patient history method
        log.debug("mock  fhir setPatientPhysicianComment");
    }
    private static void setPatientDtoPhoto(Patient patient, PatientDto patientDto) {
        if (patient.hasPhoto() && !patient.getPhoto().isEmpty()) {
            patientDto.setPhoto(patient.getPhoto().get(0).getData());
        }
    }

    private static void setPatientCarePath(Patient patient, String carePathId, String carePathName) {
        if (isEmpty(carePathId) || isEmpty(carePathName))
            return;
        patient.setPatientCarePath(getReference(carePathId, carePathName, "CarePath", false));
    }

    private static void setPatientDtoCarePath(Patient patient, PatientDto patientDto) {
        if (patient.getPatientCarePath() != null) {
            patientDto.setCpTemplateId(getReferenceValue(patient.getPatientCarePath()));
            patientDto.setCpTemplateName(getReferenceType(patient.getPatientCarePath()));
        }
    }

    private static void setPatientDtoPatientHistory(Patient patient, PatientDto patientDto) {
//        TODO mock patient history from fhir
        log.debug("mock  fhir setPatientDtoPatientHistory");
    }

    private static void setPatientPhysicianComment(Patient patient, PatientDto patientDto) {
//        TODO mock patient PhysicianComment from fhir
        log.debug("mock  fhir setPatientPhysicianComment");
    }


    private static void setPatientPrimaryPhysician(Patient patient, String physicianId,String name) {
        if (isEmpty(physicianId))
            return;

        boolean primaryPhysicianExisted = false;
        if (null != patient.getPhysicians()) {
            Optional<Patient.PatientDoctorRelationship> patientDoctorRelationshipOptional = patient.getPhysicians().stream().filter(p -> p.getPrimary().getValue()).findAny();
            if (patientDoctorRelationshipOptional.isPresent()) {
                primaryPhysicianExisted = true;
                Patient.PatientDoctorRelationship patientDoctorRelationship = patientDoctorRelationshipOptional.get();
                patientDoctorRelationship.setPhysician(new Reference().setReference(String.format("Practitioner/%1$s", physicianId)).setDisplay("Practitioner"));
                List<Resource> lstResource = patient.getContained();
                Optional<Resource> resource = lstResource.stream().filter(x -> x.getResourceType().equals(ResourceType.Practitioner)).findFirst();
                if (resource.isPresent()) {
                    Practitioner practitioner = (Practitioner) resource.get();
                    practitioner.setId("#"+physicianId);
                    practitioner.setDisplayName(new StringType(name));
                }
            }
        }
        if (!primaryPhysicianExisted) {
            List<Patient.PatientDoctorRelationship> lstPhysician = ((null != patient.getPhysicians()) ? patient.getPhysicians() : new ArrayList<>());
            Patient.PatientDoctorRelationship patientDoctorRelationship = new Patient.PatientDoctorRelationship();
            patientDoctorRelationship.setPhysician(new Reference().setReference(String.format("Practitioner/%1$s", physicianId)).setDisplay("PRACTITIONER"));
            patientDoctorRelationship.setPrimary(new BooleanType(true));
            lstPhysician.add(patientDoctorRelationship);
            patient.setPhysicians(lstPhysician);
        }
    }

    private static void setPatientDtoPrimaryPhysician(Patient patient, PatientDto patientDto) {
        if (patient.getPhysicians() == null)
            return;
        Optional<Patient.PatientDoctorRelationship> patientDoctorRelationshipOptional = patient.getPhysicians().stream().filter(x -> x.getPrimary().booleanValue()).findFirst();
        if (patient.hasContained() && patientDoctorRelationshipOptional.isPresent()) {
            Patient.PatientDoctorRelationship patientDoctorRelationship = patientDoctorRelationshipOptional.get();
            String physicianID = getReferenceValue(patientDoctorRelationship.getPhysician());
            patientDto.setPhysicianId(physicianID);
            List<Resource> lstResource = patient.getContained();
            Optional<Resource> resource = lstResource.stream().filter(x -> x.getResourceType().equals(ResourceType.Practitioner) && getReferenceValue(x.getIdElement().getIdPart()).equals(physicianID)).findFirst();
            if (resource.isPresent()) {
                Practitioner practitioner = (Practitioner) resource.get();
                getPhysicianName(patientDto, practitioner);
                getPhysicianPhone(patientDto, practitioner);
            }
        }
    }

    private static void getPhysicianName(PatientDto patientDto, Practitioner practitioner) {
        if (null != practitioner.getDisplayName()) {
            patientDto.setPhysicianName(practitioner.getDisplayName().getValue());
        }
    }

    private static void getPhysicianPhone(PatientDto patientDto, Practitioner practitioner) {
        if (practitioner.hasAddress()) {
            try {
                patientDto.setPhysicianPhone(practitioner.getAddress().get(0).getExtensionString(com.varian.fhir.resources.Address.EXTENSION_TELEPHONE1));
            } catch (FHIRException e) {
                log.error("FHIRException: {}", e.getMessage());
            }
        }
    }

//    @Deprecated
//    private static Patient.ContactComponent getPatientContactComponent(Patient patient) {
//        boolean bExisted = false;
//        Patient.ContactComponent contactComponent = new Patient.ContactComponent();
//        contactComponent.addRelationship().getCodingFirstRep().setCode("C");
//        contactComponent.addExtension().setUrl(Patient.EXTENSION_CONTACT_SUBRELATION).setValue(new StringType(FAMILY));
//        if (patient.hasContact()) {
//            Optional<Patient.ContactComponent> contactComponentOptional = patient.getContact().stream().filter(c -> c.hasRelationship()
//                    && "C".equals(c.getRelationshipFirstRep().getCodingFirstRep().getCode())
//                    && c.hasExtension(Patient.EXTENSION_CONTACT_SUBRELATION)
//                    && FAMILY.equals(c.getExtensionsByUrl(Patient.EXTENSION_CONTACT_SUBRELATION).get(0).getValue().toString())).findAny();
//            if (contactComponentOptional.isPresent()) {
//                bExisted = true;
//                contactComponent = contactComponentOptional.get();
//            }
//        }
//        if (!bExisted) {
//            patient.getContact().add(contactComponent);
//        }
//        return contactComponent;
//    }

//    @Deprecated
//    private static ContactPoint getPatientContactPoint(Patient.ContactComponent contactComponent) {
//        boolean bExisted = false;
//        ContactPoint contactPoint = new ContactPoint();
//        if (contactComponent.hasTelecom()) {
//            Optional<ContactPoint> contactPointOptional = contactComponent.getTelecom().stream().filter(t -> t.hasUse() && t.getUse().equals(ContactPoint.ContactPointUse.HOME)).findAny();
//            if (contactPointOptional.isPresent()) {
//                bExisted = true;
//                contactPoint = contactPointOptional.get();
//            }
//        }
//        if (!bExisted) {
//            contactComponent.addTelecom(contactPoint);
//        }
//        return contactPoint;
//    }

}