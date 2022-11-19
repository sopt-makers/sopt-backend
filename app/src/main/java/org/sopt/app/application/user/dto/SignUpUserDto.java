package org.sopt.app.application.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.sopt.app.domain.enums.OsType;

@Getter
public class SignUpUserDto {
    private String nickname;
    private String email;
    private String password;
    private OsType osType;
    private String clientToken;

    @Builder
    public SignUpUserDto(String nickname, String email, String password, OsType osType, String clientToken) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.osType = osType;
        this.clientToken = clientToken;
    }
}
