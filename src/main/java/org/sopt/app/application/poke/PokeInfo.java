package org.sopt.app.application.poke;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.sopt.app.domain.entity.PokeHistory;

public class PokeInfo {

    public static final String NEW_FRIEND_NO_MUTUAL = "새로운 친구";
    public static final String NEW_FRIEND_ONE_MUTUAL = "%s의 친구";
    public static final String NEW_FRIEND_MANY_MUTUAL = "%s 외 %d명과 친구";

    @Getter
    @Builder
    @ToString
    public static class PokeHistoryInfo {

        private Long id;
        private Long pokerId;
        private Long pokedId;
        private String message;
        private Boolean isReply;
        private Boolean isAnonymous;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static PokeHistoryInfo from(PokeHistory pokeHistory) {
            return new PokeHistoryInfo(
                    pokeHistory.getId(),
                    pokeHistory.getPokerId(),
                    pokeHistory.getPokedId(),
                    pokeHistory.getMessage(),
                    pokeHistory.getIsReply(),
                    pokeHistory.getIsAnonymous(),
                    pokeHistory.getCreatedAt(),
                    pokeHistory.getUpdatedAt()
            );
        }
    }

    @Getter
    @Builder
    @ToString
    public static class Relationship {

        private final int pokeNum;
        private final String relationName;
        private final String anonymousName;
    }

    @Getter
    @Builder
    @ToString
    public static class PokedUserInfo {

        private final Long userId;
        private final Long playgroundId;
        private final String name;
        private final String profileImage;
        private final Long generation;
        private final String part;
        private final Relationship relation;
        private final List<String> mutualFriendNames;

        public String getMutualRelationMessage() {
            int size = mutualFriendNames.size();

            if (size == 0) {
                return NEW_FRIEND_NO_MUTUAL;
            } else if (size == 1) {
                return String.format(NEW_FRIEND_ONE_MUTUAL, mutualFriendNames.get(0));
            } else {
                return String.format(NEW_FRIEND_MANY_MUTUAL, mutualFriendNames.get(0), size - 1);
            }
        }

        public Boolean isFirstMeet() {
            return relation.getPokeNum() < 2;
        }
    }

    @Getter
    @Builder
    @ToString
    public static class PokeDetail {

        private final Long id;
        private final Long pokerId;
        private final Long pokedId;
        private final String message;

    }

    @Getter
    @Builder
    @ToString
    public static class PokeMessageDetail {

        private final Long id;
        private final String content;

    }

}
