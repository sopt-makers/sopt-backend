package org.sopt.app.presentation.auth;


import lombok.RequiredArgsConstructor;
import org.sopt.app.application.auth.AuthUseCaseImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCaseImpl authUseCase;

    @GetMapping(value = "/api/v1/auth")
    public void check(@RequestParam(value = "nickname", required = false) String nickname,
                      @RequestParam(value = "email", required = false) String email) {
        authUseCase.validate(nickname, email);
    }
}