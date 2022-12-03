package org.sopt.app.application.user.dto;


import lombok.Builder;

import javax.validation.constraints.NotNull;

public record LogInUserDto(@NotNull(message = "아이디나 비밀번호는 필수 입력 값입니다.") String email,
                           @NotNull(message = "아이디나 비밀번호는 필수 입력 값입니다.") String password) {
    @Builder
    public LogInUserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
