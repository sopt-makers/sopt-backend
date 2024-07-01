package org.sopt.app.application.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
    ) {
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaygroundMain {

        private Long id;
        private String name;
        private Long generation;
        private String profileImage;
        private Boolean hasProfile;
        @Setter
        private String accessToken;
        @Setter
        private UserStatus status;
    }

    public record MainView(
            MainViewUser user
    ) {
    }

    @Getter
    @Builder
    @ToString
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
                    .sorted(Comparator.comparing(activity -> Integer.parseInt(activity.getGeneration()),
                            Comparator.reverseOrder()))
                    .toList()
                    .get(0);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class ActivityCardinalInfo {

        private String cardinalInfo;

        public String getGeneration() {
            return cardinalInfo.split(",")[0];
        }

        public String getPart() {
            return cardinalInfo.split(",")[1];
        }

        public boolean isActualGeneration() {
            return !cardinalInfo.endsWith(",");
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OwnPlaygroundProfile {

        private String mbti;
        private String university;
        private List<ActivityCardinalInfo> activities;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaygroundProfileOfRecommendedFriendList {

        private List<PlaygroundProfileOfRecommendedFriend> members;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of = {"playgroundId"})
    public static class PlaygroundProfileOfRecommendedFriend {

        @JsonProperty("id")
        private Long playgroundId;
        private String mbti;
        private String university;
        private String profileImage;
        private String name;
        private List<PlaygroundActivity> activities;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PlaygroundActivity {

        private String part;
        private Integer generation;
    }

}
