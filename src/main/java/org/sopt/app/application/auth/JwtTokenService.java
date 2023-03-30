package org.sopt.app.application.auth;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.joda.time.LocalDateTime;
import org.sopt.app.application.user.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenService {

    private final CustomUserDetailService customUserDetailsService;
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public String encodeJwtToken(UserInfo.Id userId) {
        val now = LocalDateTime.now();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("sopt-makers")
                .setIssuedAt(now.toDate())
                .setSubject(userId.getId().toString())
                .setExpiration(now.plusDays(1).toDate())
                .claim("id", userId.getId())
                .claim("roles", "USER")
                .signWith(SignatureAlgorithm.HS256,
                        Base64.getEncoder().encodeToString((JWT_SECRET).getBytes(
                                StandardCharsets.UTF_8)))
                .compact();
    }

    public String encodeJwtRefreshToken(UserInfo.Id userId) {
        val now = LocalDateTime.now();
        return Jwts.builder()
                .setIssuedAt(now.toDate())
                .setSubject(userId.getId().toString())
                .setExpiration(now.plusDays(14).toDate())
                .claim("id", userId.getId())
                .claim("roles", "USER")
                .signWith(SignatureAlgorithm.HS256,
                        Base64.getEncoder().encodeToString((JWT_SECRET).getBytes(
                                StandardCharsets.UTF_8)))
                .compact();
    }

    public Long getUserIdFromJwtToken(String token) {
        val claims = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString((JWT_SECRET).getBytes(
                        StandardCharsets.UTF_8)))
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public Authentication getAuthentication(String token) {
        val userDetails = customUserDetailsService.loadUserByUsername(
                this.getUserIdFromJwtToken(token).toString());
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }


    public Boolean validateToken(String token) {
        try {
            val claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString((JWT_SECRET).getBytes(
                            StandardCharsets.UTF_8))).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
