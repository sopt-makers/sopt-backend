package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


import java.util.List;

public class PokeResponse {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IsNew {
        @Schema(description = "새로운 유저 여부", example = "true")
        private Boolean isNew;

        public static IsNew of(Boolean isNew) {
            return new IsNew(isNew);
        }
    }

    // TODO 사용되지 않는 interface 책임 지우기
    interface FriendList {
    }
    interface  HistoryList{
    }
    interface MessageList {
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class AllRelationFriendList implements FriendList{

        @Schema(description = "대표 친한 친구 2명")
        private List<SimplePokeProfile> newFriend;
        @Schema(description = "친한 친구 수", example = "1")
        private Integer newFriendSize;
        @Schema(description = "대표 단짝 친구 2명")
        private List<SimplePokeProfile> bestFriend;
        @Schema(description = "단짝 친구 수", example = "1")
        private Integer bestFriendSize;
        @Schema(description = "대표 소울메이트 친구 2명")
        private List<SimplePokeProfile> soulmate;
        @Schema(description = "소울메이트 친구 수", example = "1")
        private Integer soulmateSize;

        public static AllRelationFriendList of(
            List<SimplePokeProfile> newFriend, Integer newFriendSize,
            List<SimplePokeProfile> bestFriend, Integer bestFriendSize,
            List<SimplePokeProfile> soulmate, Integer soulmateSize
        ) {
            return new AllRelationFriendList(
                    newFriend, newFriendSize,
                    bestFriend, bestFriendSize,
                    soulmate, soulmateSize
            );
        }
    }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class EachRelationFriendList implements FriendList{

        private List<SimplePokeProfile> friendList;
        private Integer pageSize;
        private Integer pageNum;

        public static EachRelationFriendList of(
                List<SimplePokeProfile> friendList, Integer pageSize, Integer pageNum
        ) {
            return new EachRelationFriendList(
                    friendList, pageSize, pageNum
            );
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeToMeHistoryList implements HistoryList{

        private List<SimplePokeProfile> history;
        private Integer pageSize;
        private Integer pageNum;

        public static PokeToMeHistoryList of(
                List<SimplePokeProfile> history, Integer pageSize, Integer pageNum
        ) {
            return new PokeToMeHistoryList(
                    history, pageSize, pageNum
            );
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeMessageList implements MessageList {
        private List<PokeMessage> messages;

        public static PokeMessageList of(
            List<PokeMessage> messages
        ) {
            return new PokeMessageList(messages);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeMessage {
        private Long messageId;
        private String content;

        public static PokeMessage of(
            Long id, String content
        ) {
            return new PokeMessage(id, content);
        }
    }



    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    @EqualsAndHashCode
    public static class SimplePokeProfile {

        @Schema(description = "유저 ID", example = "1")
        private Long userId;
        @Schema(description = "playgroundId", example = "1")
        private Long playgroundId;
        @Schema(description = "프로필 사진 URL", example = ".....")
        private String profileImage;
        @Schema(description = "유저 이름", example = "다혜다해")
        private String name;
        @Schema(description = "메시지", example = "메시지A")
        private String message;
        @Schema(description = "SOPT 최신 활동 기수 정보", example = "{'generation': 29, 'part': '안드로이드'}")
        private Integer generation;
        @Schema(description = "SOPT 최신 활동 기수 정보", example = "{'generation': 29, 'part': '안드로이드'}")
        private String part;
        @Schema(description = "현재까지 찌른 횟수", example = "3")
        private Integer pokeNum;
        @Schema(description = "관계 이름", example = "천생연분")
        private String relationName;
        @Schema(description = "함께 친구 관계인 친구들의 이름", example = "['제갈송현', '왕건모', '진동규', '차승호']")
        private List<String> mutual;
        @Schema(description = "이전에 찌른 이력이 있는지에 대한 여부", example = "false")
        private Boolean isFirstMeet;
        @Schema(description = "이미 오늘 찔렀는지에 대한 여부", example = "true")
        private Boolean isAlreadyPoke;

        public static SimplePokeProfile of(
            Long userId, Long playgroundId, String profileImage, String name, String message,
            Integer generation, String part, Integer pickNum, String relationName, List<String> mutual, Boolean isFirstMeet,
            Boolean isAlreadyPoke
        ) {
            return new SimplePokeProfile(
                userId, playgroundId, profileImage, name, message, generation, part, pickNum, relationName, mutual, isFirstMeet,
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
    public static class Friend {
        @Schema(description = "친구 ID", example = "1")
        private Long friendId;
        @Schema(description = "playgroundId", example = "1")
        private Long playgroundId;
        @Schema(description = "친구 이름", example = "제갈송현")
        private String friendName;
        @Schema(description = "친구 프로필 사진 URL", example = ".....")
        private String friendProfileImage;
        @Schema(description = "친구 프로필 리스트", example = "[{'userId': 1, 'profileImage': '...', 'name': '제갈송현', 'generation': 29, 'part': '안드로이드'}]")
        private List<SimplePokeProfile> friendList;

        public static Friend of(Long friendId, Long playgroundId, String friendName, String friendProfileImage, List<SimplePokeProfile> friendList) {
            return new Friend(friendId, playgroundId, friendName, friendProfileImage, friendList);
        }
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
        @Schema(description = "이미 오늘 찔렀는지에 대한 여부", example = "true")
        private Boolean isAlreadyPoked;

        public static PokeProfile of(
            Long userId, String profileImage, String name, Long generation, String part, Boolean isAlreadyPoked
        ) {
            return new PokeProfile(userId, profileImage, name, generation, part, isAlreadyPoked);
        }
    }
}
