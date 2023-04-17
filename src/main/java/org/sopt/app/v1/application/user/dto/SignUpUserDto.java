package org.sopt.app.v1.application.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.sopt.app.domain.enums.OsType;

@Getter
public class SignUpUserDto {

    private final String nickname;
    private final String email;
    private final String password;
    private final OsType osType;
    private final String clientToken;

    @Builder
    public SignUpUserDto(String nickname, String email, String password, OsType osType, String clientToken) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.osType = osType;
        this.clientToken = clientToken;
    }
}
