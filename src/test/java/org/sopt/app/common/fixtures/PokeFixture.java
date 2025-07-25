// package org.sopt.app.common.fixtures;
//
// import java.util.ArrayList;
// import java.util.List;
// import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.*;
// import org.sopt.app.application.user.UserProfile;
//
// public class PokeFixture {
//
//     public static final String MBTI = "ENFP";
//     public static final String UNIVERSITY = "테스트대학교";
//     public static final Long GENERATION = 33L;
//     public static final String PART = "서버";
//
//     public static List<PlaygroundProfile> createPlaygroundProfileList(
//             List<Long> playgroundIds) {
//         return playgroundIds.stream().map(playgroundId ->
//                 PlaygroundProfile.builder()
//                         .memberId(playgroundId)
//                         .activities(List.of(new ActivityCardinalInfo(GENERATION + "," + PART)))
//                         .build()).toList();
//     }
//
//     public static List<UserProfile> createUserProfileList(List<Long> userIds, List<Long> playgroundIds) {
//         List<UserProfile> userProfiles = new ArrayList<>();
//         for (int i = 0; i < userIds.size(); i++) {
//             userProfiles.add(UserProfile.builder()
//                     .userId(userIds.get(i))
//                     .playgroundId(playgroundIds.get(i))
//                     .build());
//         }
//         return userProfiles;
//     }
//
//     public static OwnPlaygroundProfile createOwnPlaygroundProfile() {
//         return OwnPlaygroundProfile.builder()
//                 .mbti(MBTI)
//                 .university(UNIVERSITY)
//                 .activities(List.of(new ActivityCardinalInfo(GENERATION + "," + PART)))
//                 .build();
//     }
//
//     public static OwnPlaygroundProfile createMbtiNullPlaygroundProfile() {
//         return OwnPlaygroundProfile.builder()
//                 .mbti(null)
//                 .university(UNIVERSITY)
//                 .activities(List.of(new ActivityCardinalInfo(GENERATION + "," + PART)))
//                 .build();
//     }
// }
