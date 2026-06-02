package org.sopt.app.application.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.common.exception.ConflictException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.utils.AnonymousNameGenerator;
import org.sopt.app.domain.entity.soptletter.SoptLetterProfile;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterProfileRepository;
import org.springframework.dao.DataIntegrityViolationException;

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
    @DisplayName("SUCCESS_기존 프로필이 존재하면 해당 프로필 정보를 반환한다")
    void SUCCESS_getOrCreateProfile_exists() {
        // given
        final Long userId = 1L;
        final String nickname = "익명의 맛있는 족발";
        SoptLetterProfile existingProfile = SoptLetterProfile.builder()
                .userId(userId)
                .nickname(nickname)
                .isOnboarded(true)
                .build();
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existingProfile));

        // when
        Profile result = soptLetterService.getOrCreateProfile(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.isOnboarded()).isTrue();
        verify(soptLetterProfileRepository, never()).save(any(SoptLetterProfile.class));
    }

    @Test
    @DisplayName("SUCCESS_프로필이 없고 닉네임 중복이 없으면 새로 생성해서 저장한다")
    void SUCCESS_getOrCreateProfile_createNew() {
        // given
        final Long userId = 1L;
        final List<String> candidates = List.of("익명1", "익명2", "익명3");
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(anonymousNameGenerator.generateMultiple(3)).thenReturn(candidates);
        when(soptLetterProfileRepository.findExistingNicknames(candidates)).thenReturn(Set.of());

        // when
        Profile result = soptLetterService.getOrCreateProfile(userId);

        // then
        assertThat(result.getNickname()).isEqualTo("익명1");
        assertThat(result.isOnboarded()).isFalse();

        ArgumentCaptor<SoptLetterProfile> profileCaptor = ArgumentCaptor.forClass(SoptLetterProfile.class);
        verify(soptLetterProfileRepository, times(1)).saveAndFlush(profileCaptor.capture());
        assertThat(profileCaptor.getValue().getNickname()).isEqualTo("익명1");
        assertThat(profileCaptor.getValue().isOnboarded()).isFalse();
    }

    @Test
    @DisplayName("SUCCESS_첫 후보가 중복이면 사용 가능한 다음 후보로 생성하여 저장한다")
    void SUCCESS_getOrCreateProfile_skipUsedNickname() {
        // given
        final Long userId = 1L;
        final List<String> candidates = List.of("익명1", "익명2", "익명3");
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(anonymousNameGenerator.generateMultiple(3)).thenReturn(candidates);
        when(soptLetterProfileRepository.findExistingNicknames(candidates)).thenReturn(Set.of("익명1"));

        // when
        Profile result = soptLetterService.getOrCreateProfile(userId);

        // then
        assertThat(result.getNickname()).isEqualTo("익명2");
        assertThat(result.isOnboarded()).isFalse();
    }

    @Test
    @DisplayName("FAIL_모든 후보가 3회 연속 중복이면 ConflictException이 발생한다")
    void FAIL_getOrCreateProfile_allCandidatesUsed() {
        // given
        final Long userId = 1L;
        final List<String> candidates = List.of("익명1", "익명2", "익명3");
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(anonymousNameGenerator.generateMultiple(3)).thenReturn(candidates);
        when(soptLetterProfileRepository.findExistingNicknames(candidates)).thenReturn(Set.copyOf(candidates));

        // when & then
        assertThatThrownBy(() -> soptLetterService.getOrCreateProfile(userId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> {
                    ConflictException exception = (ConflictException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_IS_FULL);
                });
    }

    @Test
    @DisplayName("FAIL_저장 시점에 DB 유니크 제약조건 충돌이 일어나면 ALREADY_ONBOARDED_SOPT_LETTER 예외를 던진다")
    void FAIL_getOrCreateProfile_dataIntegrityViolation() {
        // given
        final Long userId = 1L;
        final List<String> candidates = List.of("익명1", "익명2", "익명3");
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(anonymousNameGenerator.generateMultiple(3)).thenReturn(candidates);
        when(soptLetterProfileRepository.findExistingNicknames(candidates)).thenReturn(Set.of());
        doThrow(DataIntegrityViolationException.class).when(soptLetterProfileRepository).saveAndFlush(any(SoptLetterProfile.class));

        // when & then
        assertThatThrownBy(() -> soptLetterService.getOrCreateProfile(userId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> {
                    ConflictException exception = (ConflictException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_ONBOARDED_SOPT_LETTER);
                });
    }
}
