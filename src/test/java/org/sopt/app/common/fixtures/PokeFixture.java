package org.sopt.app.common.fixtures;

import java.util.ArrayList;
import java.util.List;
import org.sopt.app.application.auth.PlaygroundAuthInfo.ActivityCardinalInfo;
import org.sopt.app.application.auth.PlaygroundAuthInfo.OwnPlaygroundProfile;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundActivity;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundProfileOfRecommendedFriend;
import org.sopt.app.application.user.UserInfo.UserProfile;

public class PokeFixture {

    public static final String MBTI = "ENFP";
    public static final String UNIVERSITY = "테스트대학교";
    public static final Integer GENERATION = 33;
    public static final String PART = "서버";

    public static List<PlaygroundProfileOfRecommendedFriend> createPlaygroundProfileOfRecommendedFriendSameGenerationList(
            List<Long> playgroundIds) {
        return playgroundIds.stream().map(playgroundId ->
                PlaygroundProfileOfRecommendedFriend.builder()
                        .playgroundId(playgroundId)
                        .activities(List.of(new PlaygroundActivity(PART,GENERATION)))
                        .build()).toList();
    }

    public static List<PlaygroundProfileOfRecommendedFriend> createPlaygroundProfileOfRecommendedFriendSameMbtiList(
            List<Long> playgroundIds) {
        return playgroundIds.stream().map(playgroundId ->
                PlaygroundProfileOfRecommendedFriend.builder()
                        .playgroundId(playgroundId)
                        .mbti(MBTI)
                        .activities(List.of(new PlaygroundActivity(PART,GENERATION)))
                        .build()).toList();
    }

    public static List<PlaygroundProfileOfRecommendedFriend> createPlaygroundProfileOfRecommendedFriendSameUniversityList(
            List<Long> playgroundIds) {
        return playgroundIds.stream().map(playgroundId ->
                PlaygroundProfileOfRecommendedFriend.builder()
                        .playgroundId(playgroundId)
                        .university(UNIVERSITY)
                        .activities(List.of(new PlaygroundActivity(PART,GENERATION)))
                        .build()).toList();
    }

    public static List<UserProfile> createUserProfileList(List<Long> userIds, List<Long> playgroundIds) {
        List<UserProfile> userProfiles = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i++) {
            userProfiles.add(UserProfile.builder()
                    .userId(userIds.get(i))
                    .playgroundId(playgroundIds.get(i))
                    .build());
        }
        return userProfiles;
    }

    public static OwnPlaygroundProfile createOwnPlaygroundProfile() {
        return OwnPlaygroundProfile.builder()
                .mbti(MBTI)
                .university(UNIVERSITY)
                .activities(List.of(ActivityCardinalInfo.builder().cardinalInfo(GENERATION + "," + PART).build()))
                .build();
    }
}
