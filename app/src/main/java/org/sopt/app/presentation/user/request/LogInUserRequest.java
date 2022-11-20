package org.sopt.app.presentation.user.request;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LogInUserRequest {

    @NotNull
    private String email;
    @NotNull
    private String password;


    @Builder
    public LogInUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
