package org.sopt.app.common.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        val token = jwtTokenService.getToken(request);
        if (token != null) {
            if (jwtTokenService.validateToken(token)) {
                try {
                    val authentication = jwtTokenService.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (NotFoundException e) {
                    throw new UnauthorizedException(ErrorCode.INVALID_ACCESS_TOKEN);
                }
            } else {
                throw new UnauthorizedException(ErrorCode.INVALID_ACCESS_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }
}

