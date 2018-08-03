package com.varian.oiscn.application.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.varian.oiscn.application.resources.ResourceRegistry;
import com.varian.oiscn.base.user.AuthenticationCache;
import com.varian.oiscn.core.hipaa.queue.AuditLogQueue;
import com.varian.oiscn.core.user.UserContext;

/**
 * Created by gbt1220 on 11/1/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ResourceRegistry.class, AuditLogQueue.class})
public class HttpRequestLogFilterTest {
	private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private HttpRequestLogFilter filter;

    @Before
    public void setup() {
        request = PowerMockito.mock(HttpServletRequest.class);
        response = PowerMockito.mock(HttpServletResponse.class);
        filterChain = PowerMockito.mock(FilterChain.class);
        filter = new HttpRequestLogFilter();
    }

    @Test
    public void doFilterTestPOST() throws IOException, ServletException {
    	FilterConfig filterConfig = PowerMockito.mock(FilterConfig.class);;
    	filter.init(filterConfig);
    	Mockito.verifyZeroInteractions(filterConfig);
    	
    	PowerMockito.when(request.getMethod()).thenReturn(HttpMethod.POST);
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
        filter.destroy();
    }
    
    @Test
    public void doFilterTestGetAppointment() throws IOException, ServletException {
    	PowerMockito.when(request.getMethod()).thenReturn(HttpMethod.GET);
    	PowerMockito.when(request.getHeader("Authorization")).thenReturn("Bearer 12345667");
    	AuthenticationCache authCache = PowerMockito.mock(AuthenticationCache.class);
    	
    	PowerMockito.mockStatic(ResourceRegistry.class);
    	PowerMockito.when(ResourceRegistry.getAuthenticationCache()).thenReturn(authCache);
    	
    	
    	UserContext ctx = PowerMockito.mock(UserContext.class);
		PowerMockito.when(authCache.get("12345667")).thenReturn(ctx);
		PowerMockito.when(request.getRequestURI()).thenReturn("/appointment");
		
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put("patientId", new String[]{"1235w34"});
		PowerMockito.when(request.getParameterMap()).thenReturn(parameterMap);
		
		PowerMockito.mockStatic(AuditLogQueue.class);
		AuditLogQueue q =  PowerMockito.mock(AuditLogQueue.class);
		PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(q);
		PowerMockito.doNothing().when(q).push(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
		
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }
 
    
    @Test
    public void doFilterTestGetEncounter() throws IOException, ServletException {
    	PowerMockito.when(request.getMethod()).thenReturn(HttpMethod.GET);
    	PowerMockito.when(request.getHeader("Authorization")).thenReturn("Bearer 12345667");
    	AuthenticationCache authCache = PowerMockito.mock(AuthenticationCache.class);
    	
    	PowerMockito.mockStatic(ResourceRegistry.class);
    	PowerMockito.when(ResourceRegistry.getAuthenticationCache()).thenReturn(authCache);
    	
    	
    	UserContext ctx = PowerMockito.mock(UserContext.class);
		PowerMockito.when(authCache.get("12345667")).thenReturn(ctx);
		PowerMockito.when(request.getRequestURI()).thenReturn("/encounter");
		
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put("patientId", new String[]{"1235w34"});
		PowerMockito.when(request.getParameterMap()).thenReturn(parameterMap);
		
		PowerMockito.mockStatic(AuditLogQueue.class);
		AuditLogQueue q =  PowerMockito.mock(AuditLogQueue.class);
		PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(q);
		PowerMockito.doNothing().when(q).push(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
		
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }
    
    
    @Test
    public void doFilterTestGetOrder() throws IOException, ServletException {
    	PowerMockito.when(request.getMethod()).thenReturn(HttpMethod.GET);
    	PowerMockito.when(request.getHeader("Authorization")).thenReturn("Bearer 12345667");
    	AuthenticationCache authCache = PowerMockito.mock(AuthenticationCache.class);
    	
    	PowerMockito.mockStatic(ResourceRegistry.class);
    	PowerMockito.when(ResourceRegistry.getAuthenticationCache()).thenReturn(authCache);
    	
    	
    	UserContext ctx = PowerMockito.mock(UserContext.class);
		PowerMockito.when(authCache.get("12345667")).thenReturn(ctx);
		PowerMockito.when(request.getRequestURI()).thenReturn("/order");
		
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put("hisId", new String[]{"1235w34"});
		PowerMockito.when(request.getParameterMap()).thenReturn(parameterMap);
		
		PowerMockito.mockStatic(AuditLogQueue.class);
		AuditLogQueue q =  PowerMockito.mock(AuditLogQueue.class);
		PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(q);
		PowerMockito.doNothing().when(q).push(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
		
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }
    
    
    @Test
    public void doFilterTestGettreatmentsummary() throws IOException, ServletException {
    	PowerMockito.when(request.getMethod()).thenReturn(HttpMethod.GET);
    	PowerMockito.when(request.getHeader("Authorization")).thenReturn("Bearer 12345667");
    	AuthenticationCache authCache = PowerMockito.mock(AuthenticationCache.class);
    	
    	PowerMockito.mockStatic(ResourceRegistry.class);
    	PowerMockito.when(ResourceRegistry.getAuthenticationCache()).thenReturn(authCache);
    	
    	
    	UserContext ctx = PowerMockito.mock(UserContext.class);
		PowerMockito.when(authCache.get("12345667")).thenReturn(ctx);
		PowerMockito.when(request.getRequestURI()).thenReturn("/treatmentsummary");
		
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put("patientId", new String[]{"1235w34"});
		PowerMockito.when(request.getParameterMap()).thenReturn(parameterMap);
		
		PowerMockito.mockStatic(AuditLogQueue.class);
		AuditLogQueue q =  PowerMockito.mock(AuditLogQueue.class);
		PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(q);
		PowerMockito.doNothing().when(q).push(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
		
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }
    
    
    @Test
    public void doFilterTestGetcarepath() throws IOException, ServletException {
    	PowerMockito.when(request.getMethod()).thenReturn(HttpMethod.GET);
    	PowerMockito.when(request.getHeader("Authorization")).thenReturn("Bearer 12345667");
    	AuthenticationCache authCache = PowerMockito.mock(AuthenticationCache.class);
    	
    	PowerMockito.mockStatic(ResourceRegistry.class);
    	PowerMockito.when(ResourceRegistry.getAuthenticationCache()).thenReturn(authCache);
    	
    	
    	UserContext ctx = PowerMockito.mock(UserContext.class);
		PowerMockito.when(authCache.get("12345667")).thenReturn(ctx);
		PowerMockito.when(request.getRequestURI()).thenReturn("/carepath");
		
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put("patientId", new String[]{"1235w34"});
		PowerMockito.when(request.getParameterMap()).thenReturn(parameterMap);
		
		PowerMockito.mockStatic(AuditLogQueue.class);
		AuditLogQueue q =  PowerMockito.mock(AuditLogQueue.class);
		PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(q);
		PowerMockito.doNothing().when(q).push(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
		
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }
    
    
    @Test
    public void doFilterTestGetactivity() throws IOException, ServletException {
    	PowerMockito.when(request.getMethod()).thenReturn(HttpMethod.GET);
    	PowerMockito.when(request.getHeader("Authorization")).thenReturn("Bearer 12345667");
    	AuthenticationCache authCache = PowerMockito.mock(AuthenticationCache.class);
    	
    	PowerMockito.mockStatic(ResourceRegistry.class);
    	PowerMockito.when(ResourceRegistry.getAuthenticationCache()).thenReturn(authCache);
    	
    	
    	UserContext ctx = PowerMockito.mock(UserContext.class);
		PowerMockito.when(authCache.get("12345667")).thenReturn(ctx);
		PowerMockito.when(request.getRequestURI()).thenReturn("/activity");
		
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put("patientId", new String[]{"1235w34"});
		PowerMockito.when(request.getParameterMap()).thenReturn(parameterMap);
		
		PowerMockito.mockStatic(AuditLogQueue.class);
		AuditLogQueue q =  PowerMockito.mock(AuditLogQueue.class);
		PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(q);
		PowerMockito.doNothing().when(q).push(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
		
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }
    
    @Test
    public void doFilterTestGetXXXX() throws IOException, ServletException {
    	PowerMockito.when(request.getMethod()).thenReturn(HttpMethod.GET);
    	PowerMockito.when(request.getHeader("Authorization")).thenReturn("Bearer 12345667");
    	AuthenticationCache authCache = PowerMockito.mock(AuthenticationCache.class);
    	
    	PowerMockito.mockStatic(ResourceRegistry.class);
    	PowerMockito.when(ResourceRegistry.getAuthenticationCache()).thenReturn(authCache);
    	
    	
    	UserContext ctx = PowerMockito.mock(UserContext.class);
		PowerMockito.when(authCache.get("12345667")).thenReturn(ctx);
		PowerMockito.when(request.getRequestURI()).thenReturn("/xxxxxxxxx");
		
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put("patientId", new String[]{"1235w34"});
		PowerMockito.when(request.getParameterMap()).thenReturn(parameterMap);
		
		PowerMockito.mockStatic(AuditLogQueue.class);
		AuditLogQueue q =  PowerMockito.mock(AuditLogQueue.class);
		PowerMockito.when(AuditLogQueue.getInstance()).thenReturn(q);
		PowerMockito.doNothing().when(q).push(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
		
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }
}
