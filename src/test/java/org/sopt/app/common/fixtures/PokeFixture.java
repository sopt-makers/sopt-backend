package org.sopt.app.common.fixtures;

import java.util.ArrayList;
import java.util.List;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.ActivityCardinalInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundActivity;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfileOfRecommendedFriend;
import org.sopt.app.application.user.UserInfo.UserProfile;

public class PokeFixture {

    public static final String MBTI = "ENFP";
    public static final String UNIVERSITY = "테스트대학교";
    public static final Long GENERATION = 33L;
    public static final String PART = "서버";

    public static List<PlaygroundProfile> createPlaygroundProfileList(
            List<Long> playgroundIds) {
        return playgroundIds.stream().map(playgroundId ->
                PlaygroundProfile.builder()
                        .memberId(playgroundId)
                        .activities(List.of(new ActivityCardinalInfo(GENERATION + "," + PART)))
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
                .activities(List.of(new ActivityCardinalInfo(GENERATION + "," + PART)))
                .build();
    }

    public static OwnPlaygroundProfile createMbtiNullPlaygroundProfile() {
        return OwnPlaygroundProfile.builder()
                .mbti(null)
                .university(UNIVERSITY)
                .activities(List.of(new ActivityCardinalInfo(GENERATION + "," + PART)))
                .build();
    }

    public static List<PlaygroundProfileOfRecommendedFriend> createSameMbtiPlaygroundProfileOfRecommendedFriend(List<Long> playgroundIds, String mbti, Integer generation) {
        return playgroundIds.stream().map(playgroundId ->
                PlaygroundProfileOfRecommendedFriend.builder()
                        .playgroundId(playgroundId)
                        .mbti(mbti)
                        .activities(List.of(new PlaygroundActivity(PART,generation)))
                        .build()).toList();
    }

    public static List<PlaygroundProfileOfRecommendedFriend> createSameUniversityPlaygroundProfileOfRecommendedFriend(List<Long> playgroundIds, String university, Integer generation) {
        return playgroundIds.stream().map(playgroundId ->
                PlaygroundProfileOfRecommendedFriend.builder()
                        .playgroundId(playgroundId)
                        .university(university)
                        .activities(List.of(new PlaygroundActivity(PART,generation)))
                        .build()).toList();
    }
}
