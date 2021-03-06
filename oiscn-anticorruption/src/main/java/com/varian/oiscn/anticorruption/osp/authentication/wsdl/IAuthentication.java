
package com.varian.oiscn.anticorruption.osp.authentication.wsdl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 */
@WebService(name = "IAuthentication", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/")
@XmlSeeAlso({
        ObjectFactory.class
})
public interface IAuthentication {


    /**
     * @param password
     * @param userID
     * @return returns com.varian.oiscn.anticorruption.osp.authentication.wsdl.UserInfo
     * @throws IAuthenticationAuthenticateServiceFaultFaultFaultMessage
     */
    @WebMethod(operationName = "Authenticate", action = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/IAuthentication/Authenticate")
    @WebResult(name = "AuthenticateResult", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/")
    @RequestWrapper(localName = "Authenticate", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/", className = "com.varian.oiscn.anticorruption.osp.authentication.wsdl.Authenticate")
    @ResponseWrapper(localName = "AuthenticateResponse", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/", className = "com.varian.oiscn.anticorruption.osp.authentication.wsdl.AuthenticateResponse")
    UserInfo authenticate(
            @WebParam(name = "userID", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/")
                    String userID,
            @WebParam(name = "password", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/")
                    String password)
            throws IAuthenticationAuthenticateServiceFaultFaultFaultMessage
    ;

    /**
     * @param password
     * @param userID
     * @return returns com.varian.oiscn.anticorruption.osp.authentication.wsdl.UserInfo
     * @throws IAuthenticationAuthenticateWithoutSecTokenServiceFaultFaultFaultMessage
     */
    @WebMethod(operationName = "AuthenticateWithoutSecToken", action = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/IAuthentication/AuthenticateWithoutSecToken")
    @WebResult(name = "AuthenticateWithoutSecTokenResult", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/")
    @RequestWrapper(localName = "AuthenticateWithoutSecToken", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/", className = "com.varian.oiscn.anticorruption.osp.authentication.wsdl.AuthenticateWithoutSecToken")
    @ResponseWrapper(localName = "AuthenticateWithoutSecTokenResponse", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/", className = "com.varian.oiscn.anticorruption.osp.authentication.wsdl.AuthenticateWithoutSecTokenResponse")
    UserInfo authenticateWithoutSecToken(
            @WebParam(name = "userID", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/")
                    String userID,
            @WebParam(name = "password", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/")
                    String password)
            throws IAuthenticationAuthenticateWithoutSecTokenServiceFaultFaultFaultMessage
    ;

}
