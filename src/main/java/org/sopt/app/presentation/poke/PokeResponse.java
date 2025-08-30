package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.*;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.poke.PokeInfo.*;
import org.sopt.app.application.user.UserProfile;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.common.utils.AnonymousImageGenerator;

public class PokeResponse {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class RandomInfo {

        @Schema(description = "추천 타입 ENUM", example = "MBTI")
        private String randomType;
        @Schema(description = "추천 타입 제목", example = "나와 MBTI가 같은 사람")
        private String randomTitle;
        @Schema(description = "추천 유저 리스트", example = "[]")
        private List<SimplePokeProfile> userInfoList;
    }


    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class IsNew {

        @Schema(description = "새로운 유저 여부", example = "true")
        private Boolean isNew;
    }

    interface FriendList {
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PokeToMeHistoryList {

        private List<SimplePokeProfile> history;
        private int totalPageSize;
        private Integer pageSize;
        private Integer pageNum;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PokeMessageList {

        private String header;
        private List<PokeMessage> messages;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class PokeMessage {

        private Long messageId;
        private String content;
    }


    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class SimplePokeProfile {

        @Schema(description = "유저 ID", example = "1")
        private Long userId;
        // @Schema(description = "playgroundId", example = "1")
        // private Long playgroundId;
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
                    // pokedUserInfo.getPlaygroundId(),
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
                // Long playgroundId,
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
                    // playgroundId,
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
                PlaygroundProfile playgroundProfile, UserProfile userProfile
        ) {
            return new SimplePokeProfile(
                    userProfile.getUserId(),
                    // userProfile.getPlaygroundId(),
                    playgroundProfile.getProfileImage() == null ? "" : playgroundProfile.getProfileImage(),
                    userProfile.getName(),
                    "",
                    playgroundProfile.getLatestActivity().getGeneration(),
                    playgroundProfile.getLatestActivity().getPlaygroundPart().getPartName(),
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
        // @Schema(description = "playgroundId", example = "1")
        // private Long playgroundId;
        @Schema(description = "친구 이름", example = "제갈송현")
        private String friendName;
        @Schema(description = "친구 프로필 사진 URL", example = ".....")
        private String friendProfileImage;
        @Schema(description = "친구 프로필 리스트", example = "[{'userId': 1, 'profileImage': '...', 'name': '제갈송현', 'generation': 29, 'part': '안드로이드'}]")
        private List<SimplePokeProfile> friendList;

        public static Friend of(Long friendId, String friendName, String friendProfileImage,
                List<SimplePokeProfile> friendList) {
            return new Friend(friendId, friendName, friendProfileImage == null ? "" : friendProfileImage, friendList);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class RecommendedFriendsRequest {

        private List<RecommendedFriendsByType> randomInfoList;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecommendedFriendsByType {

        @Schema(description = "친구 추천 타입 ENUM", example = "MBTI")
        private FriendRecommendType randomType;
        @Schema(description = "친구 추천 타입 별 제목", example = "나와 MBTI가 같은 사람이에요")
        private String randomTitle;
        @Schema(description = "추천 친구 정보 리스트", example = "[]")
        private List<SimplePokeProfile> userInfoList;
    }
}
