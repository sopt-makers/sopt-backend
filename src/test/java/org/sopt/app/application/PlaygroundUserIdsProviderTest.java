// package org.sopt.app.application;
//
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.BDDMockito.given;
//
// import java.util.*;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.*;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
// import org.sopt.app.application.playground.user_finder.*;
// import org.sopt.app.common.fixtures.PokeFixture;
// import org.sopt.app.domain.enums.FriendRecommendType;
//
// @ExtendWith(MockitoExtension.class)
// class PlaygroundUserIdsProviderTest {
//
//     @Spy
//     private Map<String, PlaygroundUserFindConditionCreator> creatorMap;
//     @Mock
//     private PlaygroundUserFindConditionByMbtiCreator mbtiConditionCreator;
//     @InjectMocks
//     private PlaygroundUserIdsProvider playgroundUserIdsProvider;
//
//     @Test
//     @DisplayName("SUCCESS_자신의 유형 값이 null이면 빈 Set 반환")
//     void SUCCESS_findPlaygroundIdsByType_requirement_1() {
//         // given
//         OwnPlaygroundProfile profile = PokeFixture.createMbtiNullPlaygroundProfile();
//         given(creatorMap.get(FriendRecommendType.MBTI.getConditionCreatorBeanName())).willReturn(mbtiConditionCreator);
//
//         // when
//         Set<Long> result = playgroundUserIdsProvider.findPlaygroundIdsByType(profile, FriendRecommendType.MBTI);
//
//         // then
//         assertTrue(result.isEmpty());
//     }
//
// }
