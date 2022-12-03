package org.sopt.app.presentation.user.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogInUserRequest {
    @NotBlank(message = "아이디나 비밀번호는 필수 입력 값입니다.")
    private String email;
    @NotBlank(message = "아이디나 비밀번호는 필수 입력 값입니다.")
    private String password;
}
