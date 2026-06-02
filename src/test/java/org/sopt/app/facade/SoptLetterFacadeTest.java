package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.application.soptletter.SoptLetterService;

@ExtendWith(MockitoExtension.class)
class SoptLetterFacadeTest {

    @Mock
    private SoptLetterService soptLetterService;

    @InjectMocks
    private SoptLetterFacade soptLetterFacade;

    @Test
    @DisplayName("SUCCESS_온보딩 프로필 정보를 정상적으로 조회하거나 생성한다")
    void SUCCESS_getOrCreateOnboardingProfile() {
        // given
        final Long userId = 1L;
        final String generatedNickname = "익명의 솝트";
        Profile expected = Profile.of(generatedNickname, false);
        when(soptLetterService.getOrCreateProfile(userId)).thenReturn(expected);

        // when
        Profile result = soptLetterFacade.getOrCreateOnboardingProfile(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(generatedNickname);
        assertThat(result.isOnboarded()).isFalse();
        verify(soptLetterService, times(1)).getOrCreateProfile(userId);
    }

    @Test
    @DisplayName("SUCCESS_온보딩 완료 처리 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_completeOnboardingProfile() {
        // given
        final Long userId = 1L;
        final String nickname = "익명의 솝트";
        Profile expected = Profile.of(nickname, true);
        when(soptLetterService.completeOnboarding(userId)).thenReturn(expected);

        // when
        Profile result = soptLetterFacade.completeOnboardingProfile(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.isOnboarded()).isTrue();
        verify(soptLetterService, times(1)).completeOnboarding(userId);
    }
}
