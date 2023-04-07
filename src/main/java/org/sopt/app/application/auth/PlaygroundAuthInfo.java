package org.sopt.app.application.auth;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    }

    @Getter
    @Setter
    @ToString
    public static class PlaygroundProfile {

        private String name;
        private String profileImage;
        private PlaygroundActivity activities;
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
}
