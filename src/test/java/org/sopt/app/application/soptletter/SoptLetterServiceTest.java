package org.sopt.app.application.soptletter;

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
import org.sopt.app.common.utils.AnonymousNameGenerator;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterProfileRepository;

@ExtendWith(MockitoExtension.class)
class SoptLetterServiceTest {

    @Mock
    private SoptLetterProfileRepository soptLetterProfileRepository;

    @Mock
    private AnonymousNameGenerator anonymousNameGenerator;

    @InjectMocks
    private SoptLetterService soptLetterService;

    @Test
    @DisplayName("SUCCESS_이미 온보딩된 유저이면 true를 반환한다")
    void SUCCESS_isOnboarded_true() {
        // given
        final Long userId = 1L;
        when(soptLetterProfileRepository.existsByUserId(userId)).thenReturn(true);

        // when
        boolean result = soptLetterService.isOnboarded(userId);

        // then
        assertThat(result).isTrue();
        verify(soptLetterProfileRepository, times(1)).existsByUserId(userId);
    }

    @Test
    @DisplayName("SUCCESS_온보딩되지 않은 유저이면 false를 반환한다")
    void SUCCESS_isOnboarded_false() {
        // given
        final Long userId = 1L;
        when(soptLetterProfileRepository.existsByUserId(userId)).thenReturn(false);

        // when
        boolean result = soptLetterService.isOnboarded(userId);

        // then
        assertThat(result).isFalse();
        verify(soptLetterProfileRepository, times(1)).existsByUserId(userId);
    }

    @Test
    @DisplayName("SUCCESS_닉네임을 정상적으로 생성한다")
    void SUCCESS_generateNickname() {
        // given
        final String expectedNickname = "익명의 솝트";
        when(anonymousNameGenerator.generate()).thenReturn(expectedNickname);

        // when
        String result = soptLetterService.generateNickname();

        // then
        assertThat(result).isEqualTo(expectedNickname);
        verify(anonymousNameGenerator, times(1)).generate();
    }
}
