package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class UserResponse {

    @Getter
    @Builder
    @ToString
    public static class MainView {

        @Schema(description = "유저 인증 정보", example = "")
        private Playground user;
        @Schema(description = "유저 운영 서비스 정보", example = "")
        private Operation operation;

    }

    @Getter
    @Builder
    @ToString
    public static class Playground {

        @Schema(description = "활동/비활동/비회원 분기 처리", example = "ACTIVE")
        private String status;
        @Schema(description = "유저 이름", example = "김앱짱")
        private String name;
        @Schema(description = "유저 프로필 메세지", example = "1등이 되고 말거야!")
        private String profileImage;
        @Schema(description = "유저 활동 기수 정보", example = "[32,30,29]")
        private List<Long> generationList;

    }

    @Getter
    @Builder
    @ToString
    public static class Operation {

        @Schema(description = "유저 솝트 출석 정보", example = "2.0")
        private Double attendanceScore;
        @Schema(description = "솝트 공지", example = "공지다!")
        private String announcement;

    }


    @Getter
    @Builder
    @ToString
    public static class AppUser {

        @Schema(description = "유저 닉네임", example = "김앱짱")
        private String username;
        @Schema(description = "유저 푸시 토큰", example = "null")
        private String pushToken;
    }

    @Getter
    @Builder
    @ToString
    public static class Soptamp {

        @Schema(description = "유저 닉네임", example = "김앱짱")
        private String nickname;
        @Schema(description = "유저 랭킹 점수", example = "15")
        private Long points;
        @Schema(description = "유저 프로필 메세지", example = "1등이 되고 말거야!")
        private String profileMessage;
    }

    @Getter
    @Builder
    @ToString
    public static class Nickname {

        @Schema(description = "유저 닉네임", example = "김앱짱")
        private String nickname;
    }

    @Getter
    @Builder
    @ToString
    public static class ProfileMessage {

        @Schema(description = "유저 프로필 메세지", example = "1등이 되고 말거야!")
        private String profileMessage;
    }

    @Getter
    @Builder
    @ToString
    public static class PushToken {

        @Schema(description = "푸시 토큰", example = "asdfasdf")
        private String pushToken;
    }

    @Getter
    @Builder
    @ToString
    public static class OptIn {

        @Schema(description = "전체 알림 수신 동의", example = "false")
        private Boolean allOptIn;

        @Schema(description = "파트별 알림 수신 동의", example = "true")
        private Boolean partOptIn;

        @Schema(description = "소식 알림 수신 동의", example = "false")
        private Boolean newsOptIn;
    }

}
