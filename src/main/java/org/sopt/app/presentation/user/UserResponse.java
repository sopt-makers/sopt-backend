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
        @Schema(description = "알림 전체 읽음 여부", example = "false")
        private Boolean exists;

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
//        @Schema(description = "유저 푸시 토큰", example = "null")
//        private String pushToken;
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
    public static class IsOptIn {

        @Schema(description = "유저 알림 수신 동의 여부", example = "false")
        private Boolean isOptIn;
    }



    @Getter
    @Builder
    @ToString
    public static class Generation {
        @Schema(description = "현재 솝트 기수", example = "33")
        private Long currentGeneration;
        @Schema(description = "활동/비활동/비회원 분기 처리", example = "ACTIVE")
        private String status;
    }
}
