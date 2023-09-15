package org.sopt.app.presentation.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class PlaygroundAuthRequest {
    /**
     * Playground SSO Auth  DTO
     */
    @Getter
    @Setter
    @ToString
    public static class PlaygroundAccessTokenRequest {
        private String code;

    }

}
