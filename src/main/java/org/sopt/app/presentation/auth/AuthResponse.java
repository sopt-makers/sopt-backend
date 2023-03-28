package org.sopt.app.presentation.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AuthResponse {

    @Getter
    @Setter
    @ToString
    public static class PlaygroundMemberResponse {

        private Long id;
        private String name;
        private Long generation;
        private String profileImage;
        private Boolean hasProfile;
    }

    @Getter
    @Setter
    @ToString
    public static class LoginResponse {

        private Long userId;
        private String profileMessage;
    }

}
