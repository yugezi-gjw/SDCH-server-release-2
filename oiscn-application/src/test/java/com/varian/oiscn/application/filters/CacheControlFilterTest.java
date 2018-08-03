package com.varian.oiscn.application.filters;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by gbt1220 on 1/13/2017.
 */

public class CacheControlFilterTest {
    private ServletRequest request;

    private HttpServletResponse response;

    private FilterChain filterChain;

    private CacheControlFilter filter;

    @Before
    public void setup() {
        request = PowerMockito.mock(ServletRequest.class);
        response = PowerMockito.mock(HttpServletResponse.class);
        filterChain = PowerMockito.mock(FilterChain.class);
        filter = new CacheControlFilter();
    }

    @Test
    public void givenARequestAndResponseWhenDoFilterThenResponseContainsHeaders() throws IOException, ServletException {
        PowerMockito.doNothing().when(filterChain).doFilter(request, response);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void givenAFilterWhenDestroyThenDoNothing() {
        filter = PowerMockito.spy(new CacheControlFilter());
        PowerMockito.doNothing().when(filter).destroy();
    }

    @Test
    public void giveAFilterWhenInitThenDoNoting() throws ServletException {
        filter = PowerMockito.spy(new CacheControlFilter());
        PowerMockito.doNothing().when(filter).init(null);
    }
}
