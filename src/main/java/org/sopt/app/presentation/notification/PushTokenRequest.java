package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PushTokenRequest {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EditRequest {

        @Schema(description = "접속 기기 플랫폼", example = "Android")
        @NotNull(message = "platform value may not be null")
        private String platform;

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DeleteRequest {

        @Schema(description = "접속 기기 플랫폼", example = "Android")
        @NotNull(message = "platform value may not be null")
        private String platform;

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PushTokenManageRequest {

        @Schema(description = "유저 아이디", example = "['1']")
        @NotNull(message = "target user's Playground ID List used by register/delete request for Push Server may not be null")
        private List<String> userIds;

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String deviceToken;
    }
}
