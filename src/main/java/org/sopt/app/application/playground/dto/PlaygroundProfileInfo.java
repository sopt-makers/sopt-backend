package org.sopt.app.application.playground.dto;

import static org.sopt.app.domain.enums.PlaygroundPart.findPlaygroundPartByPartName;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import lombok.*;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.enums.PlaygroundPart;
import org.sopt.app.domain.enums.UserStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaygroundProfileInfo {

    public record ActiveUserIds(
            @JsonProperty("memberIds")
            List<Long> userIds
    ) {
    }

    public record UserActiveInfo(
            Long currentGeneration,
            UserStatus status
    ) {}

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaygroundMain {
        private Long id;
        private String name;
        private Long latestGeneration;
        private String profileImage;
        private boolean hasProfile;
    }

    @Builder
    public record LoginInfo(
            @NotNull
            Long playgroundId,
            @NotNull
            String name,
            @NotNull
            Long generation,
            @NotNull
            String profileImage,
            boolean hasProfile,
            @NotNull
            String playgroundToken
    ){
        public static LoginInfo of(PlaygroundMain playgroundMain, String playgroundToken) {
            return LoginInfo.builder()
                    .playgroundId(playgroundMain.id)
                    .name(playgroundMain.name)
                    .generation(playgroundMain.latestGeneration)
                    .profileImage(playgroundMain.profileImage)
                    .hasProfile(playgroundMain.hasProfile)
                    .playgroundToken(playgroundToken)
                    .build();
        }

    }

    public record MainView(
            MainViewUser user
    ) {
    }

    @Getter
    @Builder
    public static class MainViewUser {

        private UserStatus status;
        private String name;
        private String profileImage;
        private List<Long> generationList;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PlaygroundProfile {

        private Long memberId;
        private String name;
        private String profileImage;
        private List<ActivityCardinalInfo> activities;

        public ActivityCardinalInfo getLatestActivity() {
            return activities.stream()
                    .sorted(Comparator.comparing(ActivityCardinalInfo::getGeneration, Comparator.reverseOrder()))
                    .toList().getFirst();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class ActivityCardinalInfo {

        private String cardinalInfo; // "{generation},{part}"
        // part = 기획, 디자인, 서버, 안드로이드, iOS, 웹 / 회장, 부회장, 총무, {team} 팀장, {part} 파트장,

        public Long getGeneration() {
            try {
                return Long.parseLong(cardinalInfo.split(",")[0]);
            } catch (NumberFormatException e) {
                throw new BadRequestException(ErrorCode.INVALID_PLAYGROUND_CARDINAL_INFO);
            }
        }

        public PlaygroundPart getPlaygroundPart() {
            return findPlaygroundPartByPartName(cardinalInfo.split(",")[1]);
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OwnPlaygroundProfile {

        private String mbti;
        private String university;
        private List<ActivityCardinalInfo> activities;

        public List<Long> getAllGenerations() {
            return activities.stream()
                    .map(ActivityCardinalInfo::getGeneration)
                    .toList();
        }
    }
}
