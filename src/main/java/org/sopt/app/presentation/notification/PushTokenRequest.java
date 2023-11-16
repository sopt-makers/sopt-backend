package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

public class PushTokenRequest {

    @Getter
    // Builder 로 하면 Jackson 라이브러리에서 자동 RequestBody Mapping을 못해줍니다.
    @Setter
    @ToString
    public static class EditRequest {

        @Schema(description = "접속 기기 플랫폼", example = "Android")
        @NotNull(message = "platform value may not be null")
        private String platform;

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }

    @Getter
    @Setter
    @ToString
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
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PushTokenManageRequest {

        @Schema(description = "유저 아이디", example = "['1']")
        @NotNull(message = "target user's Playground ID List used by register/delete request for Push Server may not be null")
        private List<String> userIds;

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String deviceToken;

        public static PushTokenManageRequest of(List<String> userIds, String deviceToken) {
            return new PushTokenManageRequest(userIds, deviceToken);
        }
    }
}
