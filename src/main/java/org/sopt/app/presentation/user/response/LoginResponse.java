package org.sopt.app.presentation.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {
    private Long userId;

    private String profileMessage;

    @Builder
    public LoginResponse(Long userId, String profileMessage) {
        this.userId = userId;
        this.profileMessage = profileMessage;
    }
}
