package org.sopt.app.v1.application.user.dto;


import lombok.Builder;

public record LogInUserDto(String email, String password) {
    @Builder
    public LogInUserDto {
    }
}
