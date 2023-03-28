package org.sopt.app.presentation.auth;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        val playgroundMember = playgroundAuthService.getPlaygroundMember(playgroundToken.getAccessToken());

        val userId = userService.loginWithUserPlaygroundId(playgroundMember, playgroundToken);

        val accessToken = jwtTokenService.encodeJwtToken(userId);
        val refreshToken = jwtTokenService.encodeJwtRefreshToken(userId);
        val response = authResponseMapper.of(accessToken, refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    /**
//     * 닉네임 변경
//     */
//    @PatchMapping(value = "/api/v1/auth/nickname")
//    public void changeNickname(
//            @RequestHeader(name = "userId") String userId,
//            @RequestBody ChangeNicknameRequestDto changeNicknameRequestDto
//    ) {
//        String nickname = changeNicknameRequestDto.getNickname();
//        authUseCase.changeNickname(userId, nickname);
//    }
//
//    /**
//     * 탈퇴하기
//     */
//    @DeleteMapping(value = "/api/v1/auth/withdraw")
//    public void withdraw(
//            @RequestHeader(name = "userId") String userId
//    ) {
//        authUseCase.deleteUser(userId);
//    }
}