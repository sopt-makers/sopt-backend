package org.sopt.app.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.*;
import lombok.AllArgsConstructor;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.*;
import org.springframework.http.*;
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
            ErrorCode errorMessage = e.getErrorCode();
            HttpStatus httpStatus = errorMessage.getHttpStatus();
            setResponse(httpServletResponse, httpStatus, errorMessage);
        }
    }

    private void setResponse(HttpServletResponse response, HttpStatus httpStatus, ErrorCode errorCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(httpStatus.value());
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(FailureResponse.of(errorCode)));
    }
}