package org.sopt.app.application.poke;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public class PokeInfo {

    @Getter
    @Builder
    @ToString
    public static class Activity {
        private final int generation;
        private final String part;
    }

    @Getter
    @Builder
    @ToString
    public static class Relationship {
        private final int pokeCount;
        private final String relationName;
    }

    @Getter
    @Builder
    @ToString
    public static class PokedUserInfo {
        private final Long userId;
        private final String name;
        private final String profileImage;
        private final Activity activity;
        private final Relationship relation;
        private final List<String> mutualFriendNames;

    }

    @Getter
    @Builder
    @ToString
    public static class PokeDetail {
        private final Long id;
        private final String message;
        private final Boolean isReply;

    }

}
