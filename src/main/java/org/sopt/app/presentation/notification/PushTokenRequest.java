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
    // Builder 로 하면 Jackson 라이브러리에서 자동 RequestBody Mapping을 못해줍니다.
    @Setter
    @ToString
    public static class EditRequest {

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }

    @Getter
    @Setter
    @ToString
    public static class DeleteRequest {

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }

    public interface ExternalRequest {}

    @Getter
    @Builder
    @ToString
    public static class ExternalMemberRequest implements ExternalRequest{
        //TODO : NonMember 정책 확정되면 비회원전용 Request 객체 필요

        @Schema(description = "유저 아이디", example = "['1']")
        @NotNull(message = "target user's Playground ID List used by register/delete request for Push Server may not be null")
        private List<String> userIds;

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String deviceToken;
    }
}
