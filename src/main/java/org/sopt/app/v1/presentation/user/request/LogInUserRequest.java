package org.sopt.app.v1.presentation.user.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
