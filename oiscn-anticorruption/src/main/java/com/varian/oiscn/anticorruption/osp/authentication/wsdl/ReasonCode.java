
package com.varian.oiscn.anticorruption.osp.authentication.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReasonCode.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReasonCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="ERROR_TS_CONFLICT"/>
 *     &lt;enumeration value="ERROR_INV_SETTINGS"/>
 *     &lt;enumeration value="ERROR_INV_CMD"/>
 *     &lt;enumeration value="ERROR_FILE_NOT_FOUND"/>
 *     &lt;enumeration value="ERROR_NO_PRIVILEGE_EXISTS"/>
 *     &lt;enumeration value="ERROR_FILE_EXISTS_ALREADY"/>
 *     &lt;enumeration value="ERROR_OBSOLETE"/>
 *     &lt;enumeration value="ERROR_LOGAUDIT_FAILED"/>
 *     &lt;enumeration value="ERROR_FILE_CORRUPT"/>
 *     &lt;enumeration value="ERROR_ACCOUNT_EXCEPTION"/>
 *     &lt;enumeration value="ERROR_USER_ALREADY_EXISTS"/>
 *     &lt;enumeration value="ERROR_INVALID_SECTOKEN"/>
 *     &lt;enumeration value="ERROR_EXPIRED_SECTOKEN"/>
 *     &lt;enumeration value="ACCT_MISSING"/>
 *     &lt;enumeration value="PW_FORCECHANGE"/>
 *     &lt;enumeration value="PW_CANNOTCHANGE"/>
 *     &lt;enumeration value="PW_EXPIRED"/>
 *     &lt;enumeration value="PW_DISABLED"/>
 *     &lt;enumeration value="PW_SAMEOLD"/>
 *     &lt;enumeration value="PW_FORMAT_MISMATCH"/>
 *     &lt;enumeration value="AD_PW_EXPIRED"/>
 *     &lt;enumeration value="AD_ACCT_DISABLE"/>
 *     &lt;enumeration value="AD_ACCT_LOCKED"/>
 *     &lt;enumeration value="AD_INVALID_CRED"/>
 *     &lt;enumeration value="ERROR_INTERNAL"/>
 *     &lt;enumeration value="ERROR_RIA_CONNECTION"/>
 *     &lt;enumeration value="AD_NO_SERVER"/>
 *     &lt;enumeration value="AD_REASON_UNKNOWN"/>
 *     &lt;enumeration value="AD_ACCT_EXPIRED"/>
 *     &lt;enumeration value="NDS_INVALID_CRED"/>
 *     &lt;enumeration value="NDS_ACC_ERR"/>
 *     &lt;enumeration value="WOX_FILE_CORRUPT"/>
 *     &lt;enumeration value="ACCT_OUTSIDE_ACCESS_TIME_WITH_EMERG_ACCESS"/>
 *     &lt;enumeration value="ACCT_OUTSIDE_ACCESS_TIME_WITHOUT_EMERG_ACCESS"/>
 *     &lt;enumeration value="AD_NO_STORE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "ReasonCode", namespace = "http://schemas.varian.com/Foundation/Platform/Shared/ReasonCode/2011/4/")
@XmlEnum
public enum ReasonCode {

    OK,
    ERROR_TS_CONFLICT,
    ERROR_INV_SETTINGS,
    ERROR_INV_CMD,
    ERROR_FILE_NOT_FOUND,
    ERROR_NO_PRIVILEGE_EXISTS,
    ERROR_FILE_EXISTS_ALREADY,
    ERROR_OBSOLETE,
    ERROR_LOGAUDIT_FAILED,
    ERROR_FILE_CORRUPT,
    ERROR_ACCOUNT_EXCEPTION,
    ERROR_USER_ALREADY_EXISTS,
    ERROR_INVALID_SECTOKEN,
    ERROR_EXPIRED_SECTOKEN,
    ACCT_MISSING,
    PW_FORCECHANGE,
    PW_CANNOTCHANGE,
    PW_EXPIRED,
    PW_DISABLED,
    PW_SAMEOLD,
    PW_FORMAT_MISMATCH,
    AD_PW_EXPIRED,
    AD_ACCT_DISABLE,
    AD_ACCT_LOCKED,
    AD_INVALID_CRED,
    ERROR_INTERNAL,
    ERROR_RIA_CONNECTION,
    AD_NO_SERVER,
    AD_REASON_UNKNOWN,
    AD_ACCT_EXPIRED,
    NDS_INVALID_CRED,
    NDS_ACC_ERR,
    WOX_FILE_CORRUPT,
    ACCT_OUTSIDE_ACCESS_TIME_WITH_EMERG_ACCESS,
    ACCT_OUTSIDE_ACCESS_TIME_WITHOUT_EMERG_ACCESS,
    AD_NO_STORE;

    public static ReasonCode fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}
