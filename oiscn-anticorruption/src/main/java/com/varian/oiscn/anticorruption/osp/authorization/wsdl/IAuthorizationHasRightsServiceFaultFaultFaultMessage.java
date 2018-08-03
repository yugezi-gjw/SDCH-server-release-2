
package com.varian.oiscn.anticorruption.osp.authorization.wsdl;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 */
@WebFault(name = "ServiceFault", targetNamespace = "http://schemas.varian.com/Foundation/Platform/Shared/ServiceFault/2011/4/")
public class IAuthorizationHasRightsServiceFaultFaultFaultMessage
        extends Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     */
    private ServiceFault faultInfo;

    /**
     * @param faultInfo
     * @param message
     */
    public IAuthorizationHasRightsServiceFaultFaultFaultMessage(String message, ServiceFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * @param faultInfo
     * @param cause
     * @param message
     */
    public IAuthorizationHasRightsServiceFaultFaultFaultMessage(String message, ServiceFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * @return returns fault bean: com.varian.oiscn.anticorruption.osp.authorization.wsdl.ServiceFault
     */
    public ServiceFault getFaultInfo() {
        return faultInfo;
    }

}
