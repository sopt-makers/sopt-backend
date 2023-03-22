package org.sopt.app.presentation.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AuthRequest {

    @Getter
    @Setter
    @ToString
    public static class CodeRequest {

        private String code;
    }

    @Getter
    @Setter
    @ToString
    public static class AccessTokenRequest {

        private String accessToken;
    }
}
