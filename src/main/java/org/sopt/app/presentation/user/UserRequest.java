package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EditProfileMessageRequest {

        @Schema(description = "한마디", example = "1등이 되고 말거야!")
        @NotNull(message = "profileMessage may not be null")
        private String profileMessage;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateUserRequest{
        @Schema(description = "생성된 UserId", example = "101")
        @NotNull(message = "userId may not be null")
        private Long userId;
    }
}
