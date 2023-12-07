package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import org.sopt.app.application.poke.PokeInfo;

import java.util.List;

public class PokeResponse {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeMessages {

        @Schema(description = "찌르기 메시지 리스트", example = "['메시지A', '메시지B', '메시지C', '메시지D', '메시지E']")
        List<String> messages;

        public static PokeMessages of(List<String> messages) {
            return new PokeMessages(messages);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IsNew {
        @Schema(description = "새로운 유저 여부", example = "true")
        private Boolean isNew;

        public static IsNew of(Boolean isNew) {
            return new IsNew(isNew);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class SimplePokeProfile {

        @Schema(description = "유저 ID", example = "1")
        private Long userId;
        @Schema(description = "프로필 사진 URL", example = ".....")
        private String profileImage;
        @Schema(description = "유저 이름", example = "다혜다해")
        private String name;
        @Schema(description = "메시지", example = "메시지A")
        private String message;
        @Schema(description = "SOPT 최신 활동 기수 정보", example = "{'generation': 29, 'part': '안드로이드'}")
        private PokeInfo.Activity activity;
        @Schema(description = "현재까지 찌른 횟수", example = "3")
        private Long pickNum;
        @Schema(description = "함께 친구 관계인 친구들의 이름", example = "['제갈송현', '왕건모', '진동규', '차승호']")
        private List<String> mutual;
        @Schema(description = "이전에 찌른 이력이 있는지에 대한 여부", example = "false")
        private Boolean isFirstMeet;
        @Schema(description = "이미 오늘 찔렀는지에 대한 여부", example = "true")
        private Boolean isAlreadyPoke;

        public static SimplePokeProfile of(
            Long userId, String profileImage, String name, String message,
            PokeInfo.Activity activity, Long pickNum, List<String> mutual, Boolean isFirstMeet,
            Boolean isAlreadyPoke
        ) {
            return new SimplePokeProfile(
                userId, profileImage, name, message, activity, pickNum, mutual, isFirstMeet,
                isAlreadyPoke
            );
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class PokeAlarmStatusResponse {

        @Schema(description = "알림 서버 Response Status", example = "200")
        private Integer status;

        @Schema(description = "성공 여부", example = "true")
        private Boolean success;

        @Schema(description = "알림 서버 Response Message", example = "토큰 해지 성공")
        private String message;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PokeProfile {

        @Schema(description = "유저 ID", example = "1")
        private Long userId;
        @Schema(description = "프로필 사진 URL", example = ".....")
        private String profileImage;
        @Schema(description = "유저 이름", example = "다혜다해")
        private String name;
        @Schema(description = "기수", example = "29")
        private Long generation;
        @Schema(description = "파트", example = "안드로이드")
        private String part;

        public static PokeProfile of(
            Long userId, String profileImage, String name, Long generation, String part
        ) {
            return new PokeProfile(userId, profileImage, name, generation, part);
        }
    }
}
