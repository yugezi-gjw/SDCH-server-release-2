package com.varian.oiscn.core.patient;

import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.identifier.Identifier;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 3/28/2017.
 */
@Data
@Slf4j
public class Patient {
    private static final String GBK = "GBK";

    private String id;
    private String hisId;
    private String radiationId;
    private Long patientSer;
    private String nationalId;
    private String chineseName;
    private String englishName;
    private String pinyin;
    private GenderEnum gender;
    private Date birthDate;
    private PatientStatusEnum patientStatus;
    private MaritalStatusEnum maritalStatus;
    private VIPEnum vip;
    private CodeSystem citizenship;
    private CodeSystem ethnicGroup;
    private String photo;   //base64 string
    private String workPhone;
    private String homePhone;
    private String mobilePhone;
    private String address;
    private List<Identifier> identifiers;
    private List<HumanName> humanNames;
    private List<Contact> contacts;
    private String patientHistory;

    public void addIdentifier(Identifier identifier) {
        if (identifiers == null) {
            identifiers = new ArrayList<>();
        }
        identifiers.add(identifier);
    }

    public void addHumanName(HumanName humanName) {
        if (humanNames == null) {
            humanNames = new ArrayList<>();
        }
        humanNames.add(humanName);
    }

    public void addContact(Contact contact) {
        if (contacts == null) {
            contacts = new ArrayList<>();
        }
        contacts.add(contact);
    }

    /**
     * Verify chinese name and his id
     * @return verify result
     */
    public boolean verifyMandatoryDataAndLength() {
        if (isBlank(this.chineseName) || isBlank(this.hisId)) {
            return false;
        }
        try {
            if (verifyRegistrationHisIdAndNameLength() || verifyRegistrationOthersLength()) {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException: {}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * if registration's hisID's length >25 or registration's chineseName's length >64,
     * return true;
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean verifyRegistrationHisIdAndNameLength() throws UnsupportedEncodingException {
        return strLengthGreatThan(this.hisId, 25, GBK) || strLengthGreatThan(this.chineseName, 64, GBK);
    }

    /**
     * verify NationalId,ContactPerson,ContactPhone for registration
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean verifyRegistrationOthersLength() throws UnsupportedEncodingException {
        return strLengthGreatThan(this.nationalId, 25, GBK)
                || strLengthGreatThan(this.mobilePhone, 64, GBK);
    }

    /**
     * verify str's bytes length weather grete than  greateThan
     *
     * @param str
     * @param greateThan
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean strLengthGreatThan(String str, int greateThan, String encoding) throws UnsupportedEncodingException {
        return isNotEmpty(str) && str.getBytes(encoding).length > greateThan;
    }
}
