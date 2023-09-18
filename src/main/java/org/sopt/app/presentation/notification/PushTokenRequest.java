package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

public class PushTokenRequest {

    @Getter
    @Builder
    @ToString
    public static class RegisterRequest {
        @Schema(description = "유저 아이디", example = "['1']")
        @NotNull(message = "register target user ID may not be null")
        private List<String> userIds;

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }

    @Getter
    @Setter
    @ToString
    public static class EditRequest {

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }

    @Getter
    @Builder
    @ToString
    public static class DeleteRequest {

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }
}
