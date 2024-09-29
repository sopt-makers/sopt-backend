package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EditNicknameRequest {

        @Schema(description = "닉네임", example = "김앱짱")
        @NotNull(message = "nickname may not be null")
        private String nickname;
    }

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
    public static class EditIsOptInRequest {

        @Schema(description = "푸시 알림 수신 동의", example = "false")
        @NotNull(message = "opt-in value may not be null")
        private Boolean isOptIn;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MemberProfilesRequest {

        @Schema(description = "프로필 조회 대상 플레이그라운드 ID 리스트", example = "[115, 106]")
        @NotNull(message = "Member ID list may not be null")
        private List<Long> memberIds;
    }

}
