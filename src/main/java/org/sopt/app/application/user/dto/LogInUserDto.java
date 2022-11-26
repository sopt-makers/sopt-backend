package org.sopt.app.application.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LogInUserDto {
    private String email;
    private String password;


    @Builder
    public LogInUserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
