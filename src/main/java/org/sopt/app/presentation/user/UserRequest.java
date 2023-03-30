package org.sopt.app.presentation.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class UserRequest {

    @Getter
    @Setter
    @ToString
    public static class EditNicknameRequest {

        private String nickname;
    }
}
