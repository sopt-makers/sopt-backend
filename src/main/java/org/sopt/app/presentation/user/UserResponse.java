package org.sopt.app.presentation.user;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class UserResponse {

    @Getter
    @Builder
    @ToString
    public static class Nickname {

        private String nickname;
    }
}
