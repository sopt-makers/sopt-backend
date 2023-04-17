package org.sopt.app.v1.presentation.user.request;

import com.sun.istack.NotNull;
import lombok.Getter;
import org.sopt.app.domain.enums.OsType;

@Getter
public class CreateUserRequest {

    @NotNull
    private String nickname;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private OsType osType;
    private String clientToken;
}
