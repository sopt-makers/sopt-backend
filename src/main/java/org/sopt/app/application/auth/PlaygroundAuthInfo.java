package org.sopt.app.application.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.sopt.app.domain.enums.UserStatus;

public class PlaygroundAuthInfo {

    @Getter
    @Builder
    @ToString
    public static class PlaygroundAccessToken {

        private String accessToken;
    }

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
    @Setter
    @ToString
    public static class PlaygroundProfile {

        private String name;
        private String profileImage;
        private List<PlaygroundActivity> activities;
    }

    @Getter
    @Setter
    // TODO 임시
    @Builder
    @ToString
    public static class PlaygroundProfileWithId extends PlaygroundProfile {
        private Long id;
    }

    @Getter
    @Setter
    @ToString
    public static class PlaygroundActivity {

        private String cardinalInfo;
        private List<PlaygroundCardinalActivity> cardinalActivities;
    }

    @Getter
    @Setter
    @ToString
    public static class PlaygroundCardinalActivity {

        private Long id;
        private Long generation;
        private String team;
        private String part;
        private Boolean isProject;
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
    @ToString
    public static class AccessToken {

        private String accessToken;
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
}
