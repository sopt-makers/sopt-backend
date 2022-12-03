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

    @GetMapping(value = "/auth")
    public void check(@RequestParam String nickname) {
        authUseCase.validateNickname(nickname);
    }
}
