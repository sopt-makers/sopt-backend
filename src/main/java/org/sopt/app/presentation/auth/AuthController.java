package org.sopt.app.presentation.auth;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
public class AuthController {

    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final AuthResponseMapper authResponseMapper;

    @Operation(summary = "플그로 로그인/회원가입")
    @PostMapping(value = "/playground")
    public ResponseEntity<AuthResponse.Token> playgroundLogin(@RequestBody AuthRequest.CodeRequest codeRequest) {
        val playgroundToken = playgroundAuthService.getPlaygroundAccessToken(codeRequest);
        val playgroundMember = playgroundAuthService.getPlaygroundInfo(playgroundToken);
        val userId = userService.loginWithUserPlaygroundId(playgroundMember);

        val appToken = jwtTokenService.issueNewTokens(userId, playgroundMember);
        val response = authResponseMapper.of(appToken.getAccessToken(), appToken.getRefreshToken(),
                playgroundMember.getAccessToken(), playgroundMember.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "토큰 리프레시")
    @PatchMapping(value = "/refresh")
    public ResponseEntity<AuthResponse.Token> refreshToken(@RequestBody AuthRequest.RefreshRequest refreshRequest) {
        val userId = jwtTokenService.getUserIdFromJwtToken(refreshRequest.getRefreshToken());
        val playgroundToken = userService.getPlaygroundToken(userId);
        val playgroundMember = playgroundAuthService.getPlaygroundInfo(playgroundToken);

        val appToken = jwtTokenService.issueNewTokens(userId, playgroundMember);
        val response = authResponseMapper.of(appToken.getAccessToken(), appToken.getRefreshToken(),
                playgroundToken.getAccessToken(), playgroundMember.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}