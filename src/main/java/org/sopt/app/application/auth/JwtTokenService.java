// package org.sopt.app.application.auth;
//
// import io.jsonwebtoken.*;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import java.nio.charset.StandardCharsets;
// import java.security.Key;
// import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.util.Base64;
// import java.util.Date;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;
// import lombok.val;
//
// import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.AppToken;
// import org.sopt.app.common.exception.UnauthorizedException;
// import org.sopt.app.common.response.ErrorCode;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.stereotype.Component;
//
// @Component
// @RequiredArgsConstructor
// public class JwtTokenService {
//
//     private final CustomUserDetailService customUserDetailsService;
//     @Value("${jwt.secret}")
//     private String JWT_SECRET;
//
//     private String encodeKey(String secretKey) {
//         return Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
//     }
//
//     private Key getSigningKey(String keyString) {
//         val secretKey = this.encodeKey(keyString);
//         return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
//     }
//
//     public AppToken issueNewTokens(Long userId, Long playgroundId) {
//         val accessToken = this.encodeJwtToken(userId, playgroundId);
//         val refreshToken = this.encodeJwtRefreshToken(userId);
//         return AppToken.builder().accessToken(accessToken).refreshToken(refreshToken).build();
//     }
//
//     private String encodeJwtToken(Long userId, Long playgroundId) {
//         val now = LocalDateTime.now();
//         val nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
//         val nowDate1PlusDays = Date.from(now.plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
//         return Jwts.builder()
//                 .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
//                 .setIssuer("sopt-makers")
//                 .setIssuedAt(nowDate)
//                 .setSubject(userId.toString())
//                 .setExpiration(nowDate1PlusDays)
//                 .claim("id", userId)
//                 .claim("playgroundId", playgroundId)
//                 .claim("roles", "USER")
//                 .signWith(getSigningKey(JWT_SECRET), SignatureAlgorithm.HS256)
//                 .compact();
//     }
//
//     private String encodeJwtRefreshToken(Long userId) {
//         val now = LocalDateTime.now();
//         val nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
//         val nowDate30PlusDays = Date.from(now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
//         return Jwts.builder()
//                 .setIssuedAt(nowDate)
//                 .setSubject(userId.toString())
//                 .setExpiration(nowDate30PlusDays)
//                 .claim("id", userId)
//                 .claim("roles", "USER")
//                 .signWith(getSigningKey(JWT_SECRET), SignatureAlgorithm.HS256)
//                 .compact();
//     }
//
//     public Long getUserIdFromJwtToken(String token) {
//         try {
//             val claims = Jwts.parser()
//                 .setSigningKey(this.encodeKey(JWT_SECRET))
//                 .parseClaimsJws(token)
//                 .getBody();
//         return Long.parseLong(claims.getSubject());
//         } catch (ExpiredJwtException e) {
//             throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED);
//         } catch (Exception e) {
//             throw new UnauthorizedException(ErrorCode.INVALID_ACCESS_TOKEN);
//         }
//     }
//
//     public Authentication getAuthentication(String token) {
//         val userDetails = customUserDetailsService.loadUserByUsername(this.getUserIdFromJwtToken(token).toString());
//         return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//     }
//
//
//     public Boolean validateToken(String token) {
//         try {
//             val claims = Jwts.parser().setSigningKey(this.encodeKey(JWT_SECRET)).parseClaimsJws(token);
//
//             return !claims.getBody().getExpiration().before(new Date());
//         } catch (Exception e) {
//             return false;
//         }
//     }
//
//     public String getToken(HttpServletRequest request) {
//         return request.getHeader("Authorization");
//     }
// }
