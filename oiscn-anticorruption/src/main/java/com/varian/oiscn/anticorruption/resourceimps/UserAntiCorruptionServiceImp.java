package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.oiscn.anticorruption.fhircontext.HttpClientContextFactory;
import com.varian.oiscn.anticorruption.osp.authentication.wsdl.Authentication;
import com.varian.oiscn.anticorruption.osp.authentication.wsdl.IAuthentication;
import com.varian.oiscn.anticorruption.osp.authentication.wsdl.UserInfo;
import com.varian.oiscn.anticorruption.osp.authorization.wsdl.Authorization;
import com.varian.oiscn.anticorruption.osp.authorization.wsdl.IAuthorizationValidateTokenServiceFaultFaultFaultMessage;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 12/29/2016.
 */
@Slf4j
@AllArgsConstructor
public class UserAntiCorruptionServiceImp {
    private String fhirServerBaseUri;

    private String ospAuthenticationWsdlUrl;

    private String ospAuthorizationWsdlUrl;

    /**
     * Login.<br>
     *
     * @param user User
     * @return Login
     */
    public Login login(User user) {
        try {
            long time1 = System.currentTimeMillis();
            URL loginUrl = new URL(new URL(fhirServerBaseUri), "/login");
            WebTarget target = HttpClientContextFactory.getInstance().getHttpClient().target(loginUrl.toURI());
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.post(Entity.entity(user, MediaType.APPLICATION_JSON));
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - Login : {}", (time2 - time1) / 1000.0);
            return response.readEntity(Login.class);
        } catch (MalformedURLException e) {
            log.error("MalformedURLException: {}", e.getMessage());
        } catch (URISyntaxException e) {
            log.error("URISyntaxException: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return new Login();
    }

    /**
     * OSP Login.<br>
     * @param user User
     * @return OSP Login
     */
    public OspLogin ospLogin(User user) {
        try {
            if (isNotBlank(user.getUsername()) && isNotBlank(user.getPassword())) {
                long time1 = System.currentTimeMillis();
                URL url = new URL(ospAuthenticationWsdlUrl);
                Authentication authentication = new Authentication(url);

                IAuthentication basicHttpBindingIAuthentication = authentication.getBasicHttpBindingIAuthentication();
                BindingProvider bindingProvider = (BindingProvider) basicHttpBindingIAuthentication;
                List<Handler> handlerChain = bindingProvider.getBinding().getHandlerChain();
                handlerChain.add(new DAModeHeaderHandler());
                bindingProvider.getBinding().setHandlerChain(handlerChain);

                UserInfo userInfo = basicHttpBindingIAuthentication.authenticate(user.getUsername(), user.getPassword());
                long time2 = System.currentTimeMillis();
                log.debug("FHIR - OspLogin : {}", (time2 - time1) / 1000.0);
                if (userInfo != null) {
                    OspLogin ospLogin = new OspLogin();
                    ospLogin.setName(userInfo.getUserName().getValue());
                    ospLogin.setUsername(userInfo.getUserName().getValue());
                    ospLogin.setDisplayName(userInfo.getUserName().getValue());
                    ospLogin.setToken(userInfo.getSecToken().getValue());
                    ospLogin.setUserCUID(userInfo.getUserCUID().getValue());
                    ospLogin.setLastModifiedDt(new Date());
                    return ospLogin;
                }
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return new OspLogin();
    }

    /**
     * OSP Validate Token.<br>
     * @param ospToken OSP Token
     */
    public void validateToken(String ospToken) {
        try {
            if (isNotEmpty(ospToken)) {
                URL url = new URL(ospAuthorizationWsdlUrl);
                Authorization authorization = new Authorization(url);
                com.varian.oiscn.anticorruption.osp.authorization.wsdl.UserInfo userInfo =
                        authorization.getBasicHttpBindingIAuthorization().validateToken(ospToken);
                if (log.isInfoEnabled() && userInfo != null && userInfo.getUserID() != null) {
                    log.info("validateToken: userId=[{}]", userInfo.getUserID().getValue());
                }
            }
        } catch (MalformedURLException e) {
            log.error("MalformedURLException: {}", e.getMessage());
        } catch (IAuthorizationValidateTokenServiceFaultFaultFaultMessage e) {
            log.error("IAuthorizationValidateTokenServiceFaultFaultFaultMessage: {}", e.getMessage());
        }
    }

    /**
     * DA Mode Header Handler.<br>
     */
    public class DAModeHeaderHandler  implements SOAPHandler<SOAPMessageContext> {

        @Override
        public Set<QName> getHeaders() {
            return null;
        }

        @Override
        public boolean handleMessage(SOAPMessageContext context) {
            try {
                SOAPMessage soapMsg = context.getMessage();
                SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
                SOAPHeader soapHeader = soapEnv.getHeader();

                if (soapHeader == null) {
                    soapHeader = soapEnv.addHeader();
                }
                SOAPHeaderElement daMode = soapHeader.addHeaderElement(new QName("http://schemas.varian.com/Foundation/Platform/Public/Authentication/2011/4/", "DAMode"));
                daMode.setTextContent("true");
                soapMsg.saveChanges();
            } catch(Exception e) {
                return false;
            }

            return true;
        }

        @Override
        public boolean handleFault(SOAPMessageContext context) {
            return true;
        }

        @Override
        public void close(MessageContext context) {

        }
    }
}