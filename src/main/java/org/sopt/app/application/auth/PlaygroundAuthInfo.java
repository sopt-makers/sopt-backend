package org.sopt.app.application.auth;

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
public class PlaygroundAuthInfo {

    @Getter
    @Builder
    @ToString
    public static class AppToken {

        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Setter
    @ToString
    public static class RefreshedToken {

        private String accessToken;
        private String errorCode;
    }

    @Getter
    @Setter
    @ToString
    public static class ActiveUserIds {

        @JsonProperty("memberIds")
        private List<Long> userIds;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    public static class UserActiveInfo {

        private Long currentGeneration;
        private UserStatus status;
    }

    @Getter
    @Setter
    @ToString
    public static class PlaygroundMain {

        private Long id;
        private String name;
        private Long generation;
        private String profileImage;
        private Boolean hasProfile;
        private String accessToken;
        private UserStatus status;
    }

    @Getter
    @Builder
    @ToString
    public static class MainView {

        private MainViewUser user;
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
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PlaygroundProfile {

        private Long memberId;
        private String name;
        private String profileImage;
        private List<ActivityCardinalInfo> activities;

        public ActivityCardinalInfo getLatestActivity() {
            return activities.stream()
                    .sorted(Comparator.comparing(PlaygroundAuthInfo.ActivityCardinalInfo::getGeneration,
                            Comparator.reverseOrder()))
                    .toList()
                    .get(0);
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
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
    @ToString
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class OwnPlaygroundProfile {

        private String mbti;
        private String university;
        private List<ActivityCardinalInfo> activities;
    }

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PlaygroundProfileOfRecommendedFriendList {

        private List<PlaygroundProfileOfRecommendedFriend> members;
    }

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @EqualsAndHashCode(of = {"playgroundId"})
    public static class PlaygroundProfileOfRecommendedFriend {

        @JsonProperty("id")
        private Long playgroundId;
        private String mbti;
        private String university;
        private String profileImage;
        private String name;
        private List<PlaygroundActivity> activities;

        public PlaygroundProfileOfRecommendedFriend getProfileOfSameGeneration(Integer generation) {
            this.activities = activities.stream()
                    .filter(activity -> activity.getGeneration().equals(generation))
                    .toList();
            return this;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PlaygroundActivity {

        private String part;
        private Integer generation;
    }

    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecommendFriendRequest {

        private List<Integer> generations;
        private List<RecommendFriendFilter> filters;

        public static RecommendFriendRequest getRecommendFriendRequestByGeneration(List<Integer> generations) {
            return RecommendFriendRequest.builder()
                    .generations(generations)
                    .filters(List.of())
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecommendFriendFilter {

        private String key;
        private String value;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PlaygroundUserIds {
        private List<Long> userIds;
    }

}
