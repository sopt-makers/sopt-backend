package org.sopt.app.application.auth;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.joda.time.LocalDateTime;
import org.sopt.app.application.auth.PlaygroundAuthInfo.AppToken;
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

    private String encodeKey(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Key getSigningKey(String keyString) {
        val secretKey = this.encodeKey(keyString);
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public PlaygroundAuthInfo.AppToken issueNewTokens(UserInfo.Id userId) {
        val accessToken = this.encodeJwtToken(userId);
        val refreshToken = this.encodeJwtRefreshToken(userId);
        return AppToken.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    private String encodeJwtToken(UserInfo.Id userId) {
        val now = LocalDateTime.now();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("sopt-makers")
                .setIssuedAt(now.toDate())
                .setSubject(userId.getId().toString())
                .setExpiration(now.plusDays(1).toDate())
                .claim("id", userId.getId())
                .claim("roles", "USER")
                .signWith(getSigningKey(JWT_SECRET), SignatureAlgorithm.HS256)
                .compact();
    }

    private String encodeJwtRefreshToken(UserInfo.Id userId) {
        val now = LocalDateTime.now();
        return Jwts.builder()
                .setIssuedAt(now.toDate())
                .setSubject(userId.getId().toString())
                .setExpiration(now.plusDays(14).toDate())
                .claim("id", userId.getId())
                .claim("roles", "USER")
                .signWith(getSigningKey(JWT_SECRET), SignatureAlgorithm.HS256)
                .compact();
    }

    public UserInfo.Id getUserIdFromJwtToken(String token) {
        val claims = Jwts.parser()
                .setSigningKey(this.encodeKey(JWT_SECRET))
                .parseClaimsJws(token)
                .getBody();
        return UserInfo.Id.builder().id(Long.parseLong(claims.getSubject())).build();
    }

    public Authentication getAuthentication(String token) {
        val userDetails = customUserDetailsService.loadUserByUsername(
                this.getUserIdFromJwtToken(token).getId().toString());
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }


    public Boolean validateToken(String token) {
        try {
            val claims = Jwts.parser()
                    .setSigningKey(this.encodeKey(JWT_SECRET)).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
