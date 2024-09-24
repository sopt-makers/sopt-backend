package org.sopt.app.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse, FilterChain chain)
            throws ServletException, IOException {
        try {
            chain.doFilter(httpServletRequest, httpServletResponse);
        } catch (UnauthorizedException e) {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(httpServletResponse.getWriter(),
                    CommonResponse.onFailure(e.getStatusCode(), e.getMessage()));
        }
    }
}