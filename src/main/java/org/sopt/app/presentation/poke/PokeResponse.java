package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.application.poke.PokeInfo.PokeDetail;
import org.sopt.app.application.poke.PokeInfo.PokedUserInfo;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.common.utils.AnonymousImageGenerator;

public class PokeResponse {

    // TODO 사용되지 않는 interface 책임 지우기
    interface FriendList {

    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RandomInfoList {

        private List<RandomInfo> randomInfoList;

        public static RandomInfoList of(
                List<RandomInfo> randomInfoList
        ) {
            return new RandomInfoList(randomInfoList);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RandomInfo {

        @Schema(description = "추천 타입 ENUM", example = "MBTI")
        private String randomType;
        @Schema(description = "추천 타입 제목", example = "나와 MBTI가 같은 사람")
        private String randomTitle;
        @Schema(description = "추천 유저 리스트", example = "[]")
        private List<SimplePokeProfile> userInfoList;

        public static RandomInfo of(
                String randomType, String randomTitle, List<SimplePokeProfile> userInfoList
        ) {
            return new RandomInfo(randomType, randomTitle, userInfoList);
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
    public static class AllRelationFriendList implements FriendList {

        @Schema(description = "대표 친한 친구 2명")
        private List<SimplePokeProfile> newFriend;
        @Schema(description = "친한 친구 수", example = "1")
        private int newFriendSize;
        @Schema(description = "대표 단짝 친구 2명")
        private List<SimplePokeProfile> bestFriend;
        @Schema(description = "단짝 친구 수", example = "1")
        private int bestFriendSize;
        @Schema(description = "대표 소울메이트 친구 2명")
        private List<SimplePokeProfile> soulmate;
        @Schema(description = "소울메이트 친구 수", example = "1")
        private int soulmateSize;
        @Schema(description = "전체 친구 수", example = "1")
        private int totalSize;

        public static AllRelationFriendList of(
                List<SimplePokeProfile> newFriend, int newFriendSize,
                List<SimplePokeProfile> bestFriend, int bestFriendSize,
                List<SimplePokeProfile> soulmate, int soulmateSize,
                int totalSize
        ) {
            return new AllRelationFriendList(
                    newFriend, newFriendSize,
                    bestFriend, bestFriendSize,
                    soulmate, soulmateSize,
                    totalSize
            );
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class EachRelationFriendList implements FriendList {

        @Schema(description = "친구 리스트")
        private List<SimplePokeProfile> friendList;
        @Schema(description = "친구 수", example = "1")
        private int totalSize;
        @Schema(description = "전체 페이지 사이즈", example = "1")
        private int totalPageSize;
        @Schema(description = "페이지 사이즈", example = "1")
        private int pageSize;
        @Schema(description = "페이지 번호", example = "1")
        private int pageNum;


        public static EachRelationFriendList of(
                List<SimplePokeProfile> friendList, int totalSize, int totalPageSize, int pageSize, int pageNum
        ) {
            return new EachRelationFriendList(
                    friendList, totalSize, totalPageSize, pageSize, pageNum
            );
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeToMeHistoryList {

        private List<SimplePokeProfile> history;
        private int totalPageSize;
        private Integer pageSize;
        private Integer pageNum;

        public static PokeToMeHistoryList of(
                List<SimplePokeProfile> history, int totalPageSize, Integer pageSize, Integer pageNum
        ) {
            return new PokeToMeHistoryList(
                    history, totalPageSize, pageSize, pageNum
            );
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeMessageList {

        private String header;
        private List<PokeMessage> messages;

        public static PokeMessageList of(
                String header,
                List<PokeMessage> messages
        ) {
            return new PokeMessageList(header, messages);
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
        private Long generation;
        @Schema(description = "SOPT 최신 활동 기수 정보", example = "{'generation': 29, 'part': '안드로이드'}")
        private String part;
        @Schema(description = "현재까지 찌른 횟수", example = "3")
        private Integer pokeNum;
        @Schema(description = "관계 이름", example = "천생연분")
        private String relationName;
        @Schema(description = "함께 아는 친구관계 문구", example = "제갈송현 외 1명과 친구")
        private String mutualRelationMessage;
        @Schema(description = "이전에 찌른 이력이 있는지에 대한 여부", example = "false")
        private Boolean isFirstMeet;
        @Schema(description = "이미 오늘 찔렀는지에 대한 여부", example = "true")
        private Boolean isAlreadyPoke;
        @Schema(description = "익명 여부", example = "true")
        private Boolean isAnonymous;
        @Schema(description = "익명 이름", example = "익명의 그윽한 떡볶이")
        private String anonymousName;
        @Schema(description = "익명 사진", example = "~.png")
        private String anonymousImage;

        public static SimplePokeProfile from(
                PokedUserInfo pokedUserInfo,
                PokeDetail pokeDetail,
                Boolean isAlreadyPoke,
                Boolean isAnonymous
        ) {
            return new SimplePokeProfile(
                    pokedUserInfo.getUserId(),
                    pokedUserInfo.getPlaygroundId(),
                    pokedUserInfo.getProfileImage() == null ? "" : pokedUserInfo.getProfileImage(),
                    pokedUserInfo.getName(),
                    pokeDetail.getMessage() == null ? "" : pokeDetail.getMessage(),
                    pokedUserInfo.getGeneration(),
                    pokedUserInfo.getPart(),
                    pokedUserInfo.getRelation().getPokeNum(),
                    pokedUserInfo.getRelation().getRelationName(),
                    pokedUserInfo.getMutualRelationMessage(),
                    pokedUserInfo.isFirstMeet(),
                    isAlreadyPoke,
                    isAnonymous,
                    isAnonymous ? pokedUserInfo.getRelation().getAnonymousName() : "",
                    AnonymousImageGenerator.getImageUrl(isAnonymous)
            );
        }

        public static SimplePokeProfile of(
                Long userId,
                Long playgroundId,
                String profileImage,
                String name,
                String message,
                Long generation,
                String part,
                Integer pickNum,
                String relationName,
                String mutualRelationMessage,
                Boolean isFirstMeet,
                Boolean isAlreadyPoke,
                Boolean isAnonymous,
                String anonymousName
        ) {
            return new SimplePokeProfile(
                    userId,
                    playgroundId,
                    profileImage == null ? "" : profileImage,
                    name,
                    message == null ? "" : message,
                    generation,
                    part,
                    pickNum,
                    relationName,
                    mutualRelationMessage,
                    isFirstMeet,
                    isAlreadyPoke,
                    isAnonymous,
                    isAnonymous ? anonymousName : "",
                    AnonymousImageGenerator.getImageUrl(isAnonymous)
            );
        }

        public static SimplePokeProfile createNonFriendPokeProfile(
                Long userId,
                Long playgroundId,
                String profileImage,
                String name,
                Long generation,
                String part
        ) {
            return new SimplePokeProfile(
                    userId,
                    playgroundId,
                    profileImage == null ? "" : profileImage,
                    name,
                    "",
                    generation,
                    part,
                    0,
                    "",
                    "",
                    true,
                    false,
                    false,
                    "",
                    ""
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

        public static Friend of(Long friendId, Long playgroundId, String friendName, String friendProfileImage,
                List<SimplePokeProfile> friendList) {
            return new Friend(friendId, playgroundId, friendName, friendProfileImage == null ? "" : friendProfileImage, friendList);
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
            return new PokeProfile(userId, profileImage == null ? "" : profileImage, name, generation, part, isAlreadyPoked);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecommendedFriendsRequest {

        private List<RecommendedFriendsByType> randomInfoList;

        public static RecommendedFriendsRequest of(
                List<RecommendedFriendsByType> randomInfoList
        ) {
            return new RecommendedFriendsRequest(randomInfoList);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecommendedFriendsByType {

        @Schema(description = "친구 추천 타입 ENUM", example = "MBTI")
        private FriendRecommendType randomType;
        @Schema(description = "친구 추천 타입 별 제목", example = "나와 MBTI가 같은 사람이에요")
        private String randomTitle;
        @Schema(description = "추천 친구 정보 리스트", example = "[]")
        private List<SimplePokeProfile> userInfoList;

        public static RecommendedFriendsByType of(
                FriendRecommendType randomType, String randomTitle, List<SimplePokeProfile> userInfoList
        ) {
            return new RecommendedFriendsByType(randomType, randomTitle, userInfoList);
        }
    }
}
