package com.varian.oiscn.application.filters;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import org.apache.commons.lang3.StringUtils;

import com.varian.oiscn.application.resources.ResourceRegistry;
import com.varian.oiscn.core.hipaa.queue.AuditLogQueue;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.hipaa.HipaaEvent;
import com.varian.oiscn.util.hipaa.HipaaObjectType;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by gbt1220 on 11/1/2017.
 */
@Slf4j
public class HttpRequestLogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("HttpRequestLog init.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if(httpRequest.getMethod().equals(HttpMethod.GET)){
            String bearerAndToken = httpRequest.getHeader("Authorization");
            String token = "";
            if(StringUtils.isNotEmpty(bearerAndToken)){
                token = bearerAndToken.substring(bearerAndToken.indexOf(' ') + 1);
            }
            UserContext userContext = null;
            if(StringUtils.isNotEmpty(token)){
                userContext = ResourceRegistry.getAuthenticationCache().get(token);
            }
            String patientId = "";
            String uri = httpRequest.getRequestURI();
            HipaaObjectType hipaaObjectType = null;
            String comment = "";
            if(uri.startsWith("/appointment") || uri.startsWith("/appointments")){
                hipaaObjectType = HipaaObjectType.Appointment;
                comment = "Searching appointments.";
            } else if(uri.startsWith("/encounter") || uri.startsWith("/patient")){
                hipaaObjectType = HipaaObjectType.Patient;
                comment = "Searching patients.";
            } else if(uri.startsWith("/order") || uri.startsWith("/orders")){
                hipaaObjectType = HipaaObjectType.Other;
                comment = "Searching tasks.";
            } else if(uri.startsWith("/treatmentsummary")){
                hipaaObjectType = HipaaObjectType.Plan;
                comment = "Searching plans.";
            } else if(uri.startsWith("/carepath")){
                hipaaObjectType = HipaaObjectType.Other;
                comment = "Searching carepaths.";
            } else if(uri.startsWith("/activity") || uri.startsWith("/activities")){
                hipaaObjectType = HipaaObjectType.Activities;
                comment = "Searching activities.";
            } else {
                hipaaObjectType = HipaaObjectType.Other;
            }

            Map<String, String[]> parameterMap = httpRequest.getParameterMap();
            if(parameterMap.containsKey("patientId")){
                String[] array = parameterMap.get("patientId");
                if(array.length > 0) {
                    patientId = array[0];
                }
            } else if(parameterMap.containsKey("hisId")){
                String[] array = parameterMap.get("hisId");
                if(array.length > 0) {
                    patientId = array[0];
                }
            }
            AuditLogQueue.getInstance().push(userContext, patientId, HipaaEvent.View, hipaaObjectType, comment);
        }
        chain.doFilter(request, response);

        log.debug("Source[{}] Method[{}] URI[{}] Status[{}]",
                httpRequest.getRemoteHost(),
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpResponse.getStatus());
    }

    @Override
    public void destroy() {
        log.debug("HttpRequestLog destroy.");
    }
}