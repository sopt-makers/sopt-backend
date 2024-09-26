package org.sopt.app.common.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ServletRequestWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public String getRequestURI() {
                String requestURI = super.getRequestURI();
                if (StringUtils.endsWith(requestURI, "/")) {
                    return StringUtils.removeEnd(requestURI, "/");
                }
                return requestURI;
            }
        };
        chain.doFilter(requestWrapper, response);
    }
}