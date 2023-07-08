package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class UserRequest {

    @Getter
    @Setter
    @ToString
    public static class EditNicknameRequest {

        @Schema(description = "닉네임", example = "김앱짱")
        @NotNull(message = "nickname may not be null")
        private String nickname;
    }

    @Getter
    @Setter
    @ToString
    public static class EditProfileMessageRequest {

        @Schema(description = "한마디", example = "1등이 되고 말거야!")
        @NotNull(message = "profileMessage may not be null")
        private String profileMessage;
    }

    @Getter
    @Setter
    @ToString
    public static class EditPushTokenRequest {

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        @NotNull(message = "push token may not be null")
        private String pushToken;
    }

    @Getter
    @Setter
    @ToString
    public static class EditOptInRequest {

        @Schema(description = "전체 알림 수신 동의", example = "false")
        private Boolean allOptIn;

        @Schema(description = "파트별 알림 수신 동의", example = "true")
        private Boolean partOptIn;

        @Schema(description = "소식 알림 수신 동의", example = "false")
        private Boolean newsOptIn;
    }

}
