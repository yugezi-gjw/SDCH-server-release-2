
package com.varian.oiscn.anticorruption.osp.authorization.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.varian.oiscn.anticorruption.osp.authorization.wsdl package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ServiceFault_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", "ServiceFault");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _ArrayOfboolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfboolean");
    private final static QName _ArrayOfstring_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfstring");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _ReasonCode_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/ReasonCode/2011/4/", "ReasonCode");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _UserInfo_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", "UserInfo");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _ServiceFaultHeader_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", "Header");
    private final static QName _ServiceFaultMessage_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", "Message");
    private final static QName _UserInfoLanguageID_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", "LanguageID");
    private final static QName _UserInfoUserCUID_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", "UserCUID");
    private final static QName _UserInfoUserGroupID_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", "UserGroupID");
    private final static QName _UserInfoUserID_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", "UserID");
    private final static QName _UserInfoUserName_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", "UserName");
    private final static QName _UserInfoSecToken_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", "SecToken");
    private final static QName _ValidateTokenSecToken_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", "secToken");
    private final static QName _HasRightsResponseHasRightsResult_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", "HasRightsResult");
    private final static QName _ValidateTokenResponseValidateTokenResult_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", "ValidateTokenResult");
    private final static QName _HasRightsStrPrivilegeid_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", "strPrivilegeid");
    private final static QName _SynchronizeSUploadPrivileges_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", "sUploadPrivileges");
    private final static QName _SynchronizeSAppPrivilegeSyncXML_QNAME = new QName("http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", "sAppPrivilegeSyncXML");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.varian.oiscn.anticorruption.osp.authorization.wsdl
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HasRights }
     */
    public HasRights createHasRights() {
        return new HasRights();
    }

    /**
     * Create an instance of {@link ArrayOfstring }
     */
    public ArrayOfstring createArrayOfstring() {
        return new ArrayOfstring();
    }

    /**
     * Create an instance of {@link ValidateTokenResponse }
     */
    public ValidateTokenResponse createValidateTokenResponse() {
        return new ValidateTokenResponse();
    }

    /**
     * Create an instance of {@link UserInfo }
     */
    public UserInfo createUserInfo() {
        return new UserInfo();
    }

    /**
     * Create an instance of {@link HasRightsResponse }
     */
    public HasRightsResponse createHasRightsResponse() {
        return new HasRightsResponse();
    }

    /**
     * Create an instance of {@link ArrayOfboolean }
     */
    public ArrayOfboolean createArrayOfboolean() {
        return new ArrayOfboolean();
    }

    /**
     * Create an instance of {@link ValidateToken }
     */
    public ValidateToken createValidateToken() {
        return new ValidateToken();
    }

    /**
     * Create an instance of {@link ServiceFault }
     */
    public ServiceFault createServiceFault() {
        return new ServiceFault();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceFault }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", name = "ServiceFault")
    public JAXBElement<ServiceFault> createServiceFault(ServiceFault value) {
        return new JAXBElement<ServiceFault>(_ServiceFault_QNAME, ServiceFault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfboolean }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", name = "ArrayOfboolean")
    public JAXBElement<ArrayOfboolean> createArrayOfboolean(ArrayOfboolean value) {
        return new JAXBElement<ArrayOfboolean>(_ArrayOfboolean_QNAME, ArrayOfboolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", name = "ArrayOfstring")
    public JAXBElement<ArrayOfstring> createArrayOfstring(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_ArrayOfstring_QNAME, ArrayOfstring.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReasonCode }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/ReasonCode/2011/4/", name = "ReasonCode")
    public JAXBElement<ReasonCode> createReasonCode(ReasonCode value) {
        return new JAXBElement<ReasonCode>(_ReasonCode_QNAME, ReasonCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserInfo }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", name = "UserInfo")
    public JAXBElement<UserInfo> createUserInfo(UserInfo value) {
        return new JAXBElement<UserInfo>(_UserInfo_QNAME, UserInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", name = "Header", scope = ServiceFault.class)
    public JAXBElement<String> createServiceFaultHeader(String value) {
        return new JAXBElement<String>(_ServiceFaultHeader_QNAME, String.class, ServiceFault.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/", name = "Message", scope = ServiceFault.class)
    public JAXBElement<String> createServiceFaultMessage(String value) {
        return new JAXBElement<String>(_ServiceFaultMessage_QNAME, String.class, ServiceFault.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", name = "LanguageID", scope = UserInfo.class)
    public JAXBElement<String> createUserInfoLanguageID(String value) {
        return new JAXBElement<String>(_UserInfoLanguageID_QNAME, String.class, UserInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", name = "UserCUID", scope = UserInfo.class)
    public JAXBElement<String> createUserInfoUserCUID(String value) {
        return new JAXBElement<String>(_UserInfoUserCUID_QNAME, String.class, UserInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", name = "UserGroupID", scope = UserInfo.class)
    public JAXBElement<String> createUserInfoUserGroupID(String value) {
        return new JAXBElement<String>(_UserInfoUserGroupID_QNAME, String.class, UserInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", name = "UserID", scope = UserInfo.class)
    public JAXBElement<String> createUserInfoUserID(String value) {
        return new JAXBElement<String>(_UserInfoUserID_QNAME, String.class, UserInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", name = "UserName", scope = UserInfo.class)
    public JAXBElement<String> createUserInfoUserName(String value) {
        return new JAXBElement<String>(_UserInfoUserName_QNAME, String.class, UserInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Shared/UserInfo/2011/4/", name = "SecToken", scope = UserInfo.class)
    public JAXBElement<String> createUserInfoSecToken(String value) {
        return new JAXBElement<String>(_UserInfoSecToken_QNAME, String.class, UserInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", name = "secToken", scope = ValidateToken.class)
    public JAXBElement<String> createValidateTokenSecToken(String value) {
        return new JAXBElement<String>(_ValidateTokenSecToken_QNAME, String.class, ValidateToken.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfboolean }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", name = "HasRightsResult", scope = HasRightsResponse.class)
    public JAXBElement<ArrayOfboolean> createHasRightsResponseHasRightsResult(ArrayOfboolean value) {
        return new JAXBElement<ArrayOfboolean>(_HasRightsResponseHasRightsResult_QNAME, ArrayOfboolean.class, HasRightsResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserInfo }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", name = "ValidateTokenResult", scope = ValidateTokenResponse.class)
    public JAXBElement<UserInfo> createValidateTokenResponseValidateTokenResult(UserInfo value) {
        return new JAXBElement<UserInfo>(_ValidateTokenResponseValidateTokenResult_QNAME, UserInfo.class, ValidateTokenResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", name = "strPrivilegeid", scope = HasRights.class)
    public JAXBElement<ArrayOfstring> createHasRightsStrPrivilegeid(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_HasRightsStrPrivilegeid_QNAME, ArrayOfstring.class, HasRights.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", name = "secToken", scope = HasRights.class)
    public JAXBElement<String> createHasRightsSecToken(String value) {
        return new JAXBElement<String>(_ValidateTokenSecToken_QNAME, String.class, HasRights.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", name = "sUploadPrivileges", scope = Synchronize.class)
    public JAXBElement<String> createSynchronizeSUploadPrivileges(String value) {
        return new JAXBElement<String>(_SynchronizeSUploadPrivileges_QNAME, String.class, Synchronize.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", name = "sAppPrivilegeSyncXML", scope = Synchronize.class)
    public JAXBElement<String> createSynchronizeSAppPrivilegeSyncXML(String value) {
        return new JAXBElement<String>(_SynchronizeSAppPrivilegeSyncXML_QNAME, String.class, Synchronize.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.varian.com/Foundation/Platform/Public/Authorization/2011/4/", name = "secToken", scope = Synchronize.class)
    public JAXBElement<String> createSynchronizeSecToken(String value) {
        return new JAXBElement<String>(_ValidateTokenSecToken_QNAME, String.class, Synchronize.class, value);
    }

}
