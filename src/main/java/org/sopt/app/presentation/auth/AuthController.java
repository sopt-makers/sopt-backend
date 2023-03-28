package org.sopt.app.presentation.auth;


import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.AuthService;
import org.sopt.app.application.auth.AuthUseCaseImpl;
import org.sopt.app.presentation.auth.dto.ChangeNicknameRequestDto;
import org.sopt.app.presentation.auth.dto.ChangePasswordRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCaseImpl authUseCase;
    private final AuthService authService;
    private final AuthResponseMapper authResponseMapper;

    @PostMapping(value = "/api/v1/auth/login")
    public ResponseEntity<?> playgroundLogin(@RequestBody AuthRequest.CodeRequest codeRequest) {
        val accessToken = authService.getPlaygroundAccessToken(codeRequest);
        val result = authService.getPlaygroundMember(accessToken.getAccessToken());
        val response = result;
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 닉네임, 이메일 검증 API
     */
    @GetMapping(value = "/api/v1/auth")
    public void check(@RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "email", required = false) String email) {
        authUseCase.validate(nickname, email);
    }

    /**
     * 비밀번호 변경
     */

    @PatchMapping(value = "/api/v1/auth/password")
    public void changePassword(
            @RequestHeader(name = "userId") String userId,
            @RequestBody ChangePasswordRequestDto changePasswordRequestDto
    ) {
        authUseCase.changePassword(userId, changePasswordRequestDto.getPassword());
    }

    /**
     * 닉네임 변경
     */
    @PatchMapping(value = "/api/v1/auth/nickname")
    public void changeNickname(
            @RequestHeader(name = "userId") String userId,
            @RequestBody ChangeNicknameRequestDto changeNicknameRequestDto
    ) {
        String nickname = changeNicknameRequestDto.getNickname();
        authUseCase.changeNickname(userId, nickname);
    }

    /**
     * 탈퇴하기
     */
    @DeleteMapping(value = "/api/v1/auth/withdraw")
    public void withdraw(
            @RequestHeader(name = "userId") String userId
    ) {
        authUseCase.deleteUser(userId);
    }
}