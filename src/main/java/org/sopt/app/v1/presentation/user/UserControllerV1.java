package org.sopt.app.v1.presentation.user;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.User;
import org.sopt.app.v1.application.user.UserUseCase;
import org.sopt.app.v1.application.user.dto.LogInUserDto;
import org.sopt.app.v1.application.user.dto.SignUpUserDto;
import org.sopt.app.v1.presentation.user.request.LogInUserRequest;
import org.sopt.app.v1.presentation.user.response.LoginResponse;
import org.sopt.app.v1.presentation.user.response.SignUpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerV1 {

    private final UserUseCase userUseCase;

    /**
     * 이메일 회원가입
     */
    @PostMapping(value = "/api/v1/user/signup")
    public SignUpResponse signUp2(@RequestBody SignUpUserDto request) {
        Long userId = userUseCase.signUp(SignUpUserDto.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(request.getPassword())
                .osType(request.getOsType())
                .clientToken(request.getClientToken())
                .build());

        return SignUpResponse.builder()
                .userId(userId)
                .build();
    }


    /**
     * 로그인
     */
    @PostMapping(value = "/api/v1/user/login")
    public LoginResponse signIn2(@Valid @RequestBody LogInUserRequest request) {

        User user = userUseCase.logIn(LogInUserDto.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build());
        return LoginResponse.builder()
                .userId(user.getId())
                .profileMessage(user.getProfileMessage())
                .build();
    }
}
