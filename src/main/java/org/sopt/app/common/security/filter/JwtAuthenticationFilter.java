package org.sopt.app.common.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.JwtException;
import org.sopt.app.common.jwt.service.JwtAuthenticationService;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.security.authentication.MakersAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationService jwtAuthenticationService;
    private static final String ACCESS_TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationToken = getAuthorizationToken(request);
        if (authorizationToken != null) {
            MakersAuthentication authentication = jwtAuthenticationService.authenticate(authorizationToken);
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getAuthorizationToken(final HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(ACCESS_TOKEN_PREFIX)) {
            return header.substring(ACCESS_TOKEN_PREFIX.length());
        }
        return null;
    }
}
