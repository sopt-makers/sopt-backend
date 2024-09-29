package org.sopt.app.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;
import org.sopt.app.application.playground.dto.RecommendedFriendInfo.PlaygroundUserFindFilter;
import org.sopt.app.application.playground.user_finder.PlaygroundUserFindConditionCreatorFactory;
import org.sopt.app.application.playground.user_finder.PlaygroundUserFinder;
import org.sopt.app.application.playground.user_finder.PlaygroundUserIdsProvider;
import org.sopt.app.common.fixtures.PokeFixture;
import org.sopt.app.domain.enums.FriendRecommendType;

@ExtendWith(MockitoExtension.class)
class PlaygroundUserIdsProviderTest {

    @Spy
    private PlaygroundUserFindConditionCreatorFactory factory;
    @Mock
    private PlaygroundUserFinder finder;
    @InjectMocks
    private PlaygroundUserIdsProvider playgroundUserIdsProvider;

    @Test
    @DisplayName("SUCCESS_자신의 유형 값과 같은 플레이그라운드 아이디 찾기")
    void SUCCESS_findPlaygroundIdsByType() {
        // given
        OwnPlaygroundProfile profile = PokeFixture.createOwnPlaygroundProfile();

        // when
        ArgumentCaptor<PlaygroundUserFindCondition> ac = ArgumentCaptor.forClass(PlaygroundUserFindCondition.class);
        when(finder.findByCondition(ac.capture())).thenReturn(Set.of(1L, 2L, 3L));
        Set<Long> result = playgroundUserIdsProvider.findPlaygroundIdsByType(profile, FriendRecommendType.MBTI);
        PlaygroundUserFindFilter expectedFilter = PlaygroundUserFindFilter.builder()
                .key(String.valueOf(FriendRecommendType.MBTI))
                .value(profile.getMbti())
                .build();
        // then
        assertEquals(ac.getValue(), new PlaygroundUserFindCondition(profile.getAllGenerations(), List.of(expectedFilter)));
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("SUCCESS_자신의 유형 값이 null이면 빈 Set 반환")
    void SUCCESS_findPlaygroundIdsByType_requirement_1() {
        // given
        OwnPlaygroundProfile profile = PokeFixture.createMbtiNullPlaygroundProfile();

        // when
        Set<Long> result = playgroundUserIdsProvider.findPlaygroundIdsByType(profile, FriendRecommendType.MBTI);

        // then
        assertTrue(result.isEmpty());
    }

}
