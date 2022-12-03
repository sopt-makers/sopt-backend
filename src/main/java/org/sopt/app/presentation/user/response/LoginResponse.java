package org.sopt.app.presentation.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {
    private Long userId;

    @Builder
    public LoginResponse(Long userId) {
        this.userId = userId;
    }
}
