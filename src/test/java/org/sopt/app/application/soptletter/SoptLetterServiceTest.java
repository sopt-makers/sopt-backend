package org.sopt.app.application.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.ConflictException;
import org.sopt.app.common.exception.ForbiddenException;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.utils.AnonymousNameGenerator;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.sopt.app.domain.entity.soptletter.SoptLetterProfile;
import org.sopt.app.domain.entity.soptletter.SoptLetterTopic;
import org.sopt.app.domain.enums.SoptLetterColor;
import org.sopt.app.domain.enums.SoptLetterShapeType;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterLikeRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterProfileRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterTopicRepository;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class SoptLetterServiceTest {

    @Mock
    private SoptLetterProfileRepository soptLetterProfileRepository;

    @Mock
    private AnonymousNameGenerator anonymousNameGenerator;

    @Mock
    private SoptLetterRepository soptLetterRepository;

    @Mock
    private SoptLetterTopicRepository soptLetterTopicRepository;

    @Mock
    private SoptLetterLikeRepository soptLetterLikeRepository;

    @Mock
    private SoptLetterGenerator soptLetterGenerator;

    @Mock
    private Clock clock;

    @InjectMocks
    private SoptLetterService soptLetterService;

    @org.junit.jupiter.api.BeforeEach
    void setUpClock() {
        lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        lenient().when(clock.instant()).thenAnswer(invocation -> java.time.Instant.now());
    }

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
        verify(soptLetterProfileRepository, never()).saveAndFlush(any(SoptLetterProfile.class));
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
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NICKNAME_IS_FULL);
                });
    }

    @Test
    @DisplayName("FAIL_저장 시점에 DB 유니크 제약조건 충돌이 일어나면 로그를 찍고 CONFLIECT 예외를 던진다")
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
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONFLICT);
                });
    }

    @Test
    @DisplayName("SUCCESS_온보딩 완료 처리 시 profile의 isOnboarded 상태를 true로 변경하고 반환한다")
    void SUCCESS_completeOnboarding() {
        // given
        final Long userId = 1L;
        final String nickname = "익명의 솝트";
        SoptLetterProfile profile = SoptLetterProfile.builder()
                .userId(userId)
                .nickname(nickname)
                .isOnboarded(false)
                .build();
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when
        Profile result = soptLetterService.completeOnboarding(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.isOnboarded()).isTrue();
        assertThat(profile.isOnboarded()).isTrue();
    }

    @Test
    @DisplayName("FAIL_온보딩 완료 처리 시 솝레터 프로필이 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_completeOnboarding_profileNotFound() {
        // given
        final Long userId = 1L;
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.completeOnboarding(userId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("SUCCESS_솝레터 주제 목록을 최신순으로 조회한다")
    void SUCCESS_getTopics() {
        // given
        LocalDateTime firstCreatedAt = LocalDateTime.of(2026, 4, 20, 0, 0);
        LocalDateTime secondCreatedAt = LocalDateTime.of(2026, 4, 18, 0, 0);
        SoptLetterTopic firstTopic = mock(SoptLetterTopic.class);
        SoptLetterTopic secondTopic = mock(SoptLetterTopic.class);
        when(firstTopic.getId()).thenReturn(2L);
        when(firstTopic.getTitle()).thenReturn("36기 앱잼 회고");
        when(firstTopic.getCreatedAt()).thenReturn(firstCreatedAt);
        when(secondTopic.getId()).thenReturn(1L);
        when(secondTopic.getTitle()).thenReturn("36기 회고");
        when(secondTopic.getCreatedAt()).thenReturn(secondCreatedAt);
        when(soptLetterTopicRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(firstTopic, secondTopic));

        // when
        SoptLetterInfo.TopicListResult result = soptLetterService.getTopics();

        // then
        assertThat(result.getTopics()).hasSize(2);
        assertThat(result.getTopics().get(0).getTopicId()).isEqualTo(2L);
        assertThat(result.getTopics().get(0).getTitle()).isEqualTo("36기 앱잼 회고");
        assertThat(result.getTopics().get(0).getCreatedAt()).isEqualTo(firstCreatedAt);
        assertThat(result.getTopics().get(1).getTopicId()).isEqualTo(1L);
        verify(soptLetterTopicRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("SUCCESS_솝레터 주제를 단일 조회한다")
    void SUCCESS_getTopic() {
        // given
        final Long topicId = 3L;
        LocalDateTime now = LocalDateTime.of(2026, 4, 20, 12, 0);
        LocalDateTime startedAt = LocalDateTime.of(2026, 4, 18, 0, 0);
        LocalDateTime endedAt = LocalDateTime.of(2026, 4, 28, 23, 59, 59);
        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 0, 0);
        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        when(topic.getId()).thenReturn(topicId);
        when(topic.getTitle()).thenReturn("36기 회고");
        when(topic.getStartedAt()).thenReturn(startedAt);
        when(topic.getEndedAt()).thenReturn(endedAt);
        when(topic.getCreatedAt()).thenReturn(createdAt);
        when(topic.isActiveAt(now)).thenReturn(true);
        when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        // when
        SoptLetterInfo.TopicDetail result = soptLetterService.getTopic(topicId);

        // then
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getTitle()).isEqualTo("36기 회고");
        assertThat(result.getActive()).isTrue();
        assertThat(result.getStartedAt()).isEqualTo(startedAt);
        assertThat(result.getEndedAt()).isEqualTo(endedAt);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        verify(soptLetterTopicRepository, times(1)).findById(topicId);
    }

    @Test
    @DisplayName("FAIL_솝레터 주제 단일 조회 시 주제가 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_getTopic_notFound() {
        // given
        final Long topicId = 999L;
        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.getTopic(topicId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_TOPIC_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("SUCCESS_첫 번째 메시지 작성 시 기본 색상인 BLUE_50으로 편지를 성공적으로 작성한다")
    void SUCCESS_createSoptLetter() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final String content = "첫 편지 내용입니다.";
        final LocalDateTime now = LocalDateTime.now();

        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();

        when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.countByAuthorProfileIdAndCreatedAtGreaterThanEqual(anyLong(), any(LocalDateTime.class))).thenReturn(0L);
        when(soptLetterRepository.findFirstByTopicIdOrderByIdDesc(topicId)).thenReturn(Optional.empty());
        when(soptLetterGenerator.generate(anyLong(), anyLong(), any(String.class), any())).thenAnswer(invocation -> {
            Long authorProfileId = invocation.getArgument(0);
            Long tId = invocation.getArgument(1);
            String msg = invocation.getArgument(2);
            return SoptLetter.builder()
                    .authorProfileId(authorProfileId)
                    .topicId(tId)
                    .degree(0.0)
                    .message(msg)
                    .color(SoptLetterColor.BLUE_50)
                    .shapeType(SoptLetterShapeType.POINT)
                    .likeCount(0)
                    .build();
        });
        when(soptLetterRepository.save(any(SoptLetter.class))).thenAnswer(invocation -> {
            SoptLetter letter = invocation.getArgument(0);
            return SoptLetter.builder()
                    .id(125L)
                    .authorProfileId(letter.getAuthorProfileId())
                    .topicId(letter.getTopicId())
                    .degree(letter.getDegree())
                    .message(letter.getMessage())
                    .color(letter.getColor())
                    .shapeType(letter.getShapeType())
                    .likeCount(0)
                    .build();
        });

        // when
        SoptLetterInfo.MessageResult result = soptLetterService.createSoptLetter(userId, topicId, content);

        // then
        assertThat(result.getMessageId()).isEqualTo(125L);
        assertThat(result.getColorCode()).isEqualTo(SoptLetterColor.BLUE_50.getHexCode());
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        assertThat(result.getLikedByMe()).isFalse();
        assertThat(result.getMine()).isTrue();
        verify(soptLetterRepository, times(1)).save(any(SoptLetter.class));
    }

    @Test
    @DisplayName("SUCCESS_이전 메시지가 BLUE_50인 경우 다음 색상인 GREEN_50으로 순환하여 작성한다")
    void SUCCESS_createSoptLetter_cycleColors() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final String content = "두 번째 편지 내용입니다.";
        final LocalDateTime now = LocalDateTime.now();

        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();
        SoptLetter latestLetter = SoptLetter.builder()
                .color(SoptLetterColor.BLUE_50)
                .build();

        when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.countByAuthorProfileIdAndCreatedAtGreaterThanEqual(anyLong(), any(LocalDateTime.class))).thenReturn(0L);
        when(soptLetterRepository.findFirstByTopicIdOrderByIdDesc(topicId)).thenReturn(Optional.of(latestLetter));
        when(soptLetterGenerator.generate(anyLong(), anyLong(), any(String.class), any())).thenAnswer(invocation -> {
            Long authorProfileId = invocation.getArgument(0);
            Long tId = invocation.getArgument(1);
            String msg = invocation.getArgument(2);
            SoptLetterColor prevColor = invocation.getArgument(3);
            return SoptLetter.builder()
                    .authorProfileId(authorProfileId)
                    .topicId(tId)
                    .degree(0.0)
                    .message(msg)
                    .color(prevColor == SoptLetterColor.BLUE_50 ? SoptLetterColor.GREEN_50 : SoptLetterColor.BLUE_50)
                    .shapeType(SoptLetterShapeType.POINT)
                    .likeCount(0)
                    .build();
        });
        when(soptLetterRepository.save(any(SoptLetter.class))).thenAnswer(invocation -> {
            SoptLetter letter = invocation.getArgument(0);
            return SoptLetter.builder()
                    .id(126L)
                    .authorProfileId(letter.getAuthorProfileId())
                    .topicId(letter.getTopicId())
                    .degree(letter.getDegree())
                    .message(letter.getMessage())
                    .color(letter.getColor())
                    .shapeType(letter.getShapeType())
                    .likeCount(0)
                    .build();
        });

        // when
        SoptLetterInfo.MessageResult result = soptLetterService.createSoptLetter(userId, topicId, content);

        // then
        assertThat(result.getMessageId()).isEqualTo(126L);
        assertThat(result.getColorCode()).isEqualTo(SoptLetterColor.GREEN_50.getHexCode());
        verify(soptLetterRepository, times(1)).save(any(SoptLetter.class));
    }

    @Test
    @DisplayName("FAIL_존재하지 않는 토픽 ID로 작성 시도 시 NotFoundException이 발생한다")
    void FAIL_createSoptLetter_topicNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 999L;
        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.createSoptLetter(userId, topicId, "테스트"))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_온보딩하지 않은 유저가 작성 시도 시 NotFoundException이 발생한다")
    void FAIL_createSoptLetter_profileNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        SoptLetterTopic topic = mock(SoptLetterTopic.class);

        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.createSoptLetter(userId, topicId, "테스트"))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_일일 솝레터 작성 제한 초과 시 BadRequestException이 발생한다")
    void FAIL_createSoptLetter_dailyLimitExceeded() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final LocalDateTime now = LocalDateTime.now();

        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();

        when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.countByAuthorProfileIdAndCreatedAtGreaterThanEqual(anyLong(), any(LocalDateTime.class))).thenReturn(10L);

        // when & then
        assertThatThrownBy(() -> soptLetterService.createSoptLetter(userId, topicId, "테스트"))
                .isInstanceOf(BadRequestException.class)
                .satisfies(e -> {
                    BadRequestException exception = (BadRequestException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_DAILY_LIMIT_EXCEEDED);
                });
    }

    @Test
    @DisplayName("SUCCESS_메시지를 정상적으로 수정하고 결과를 반환한다")
    void SUCCESS_updateSoptLetter() {
        // given
        final Long userId = 1L;
        final Long messageId = 125L;
        final String newContent = "수정된 편지 내용입니다.";

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .authorProfileId(10L)
                .topicId(3L)
                .degree(0.0)
                .message("이전 편지 내용")
                .color(SoptLetterColor.BLUE_50)
                .shapeType(SoptLetterShapeType.POINT)
                .likeCount(0)
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterLikeRepository.existsByLetterIdAndUserId(messageId, userId)).thenReturn(false);

        // when
        SoptLetterInfo.MessageResult result = soptLetterService.updateSoptLetter(userId, messageId, newContent);

        // then
        assertThat(result.getMessageId()).isEqualTo(messageId);
        assertThat(result.getContent()).isEqualTo(newContent);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        assertThat(result.getMine()).isTrue();
        assertThat(letter.getMessage()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("FAIL_수정 시 솝레터 메시지가 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_updateSoptLetter_letterNotFound() {
        // given
        final Long userId = 1L;
        final Long messageId = 999L;
        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.updateSoptLetter(userId, messageId, "수정 내용"))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_수정 시 솝레터 프로필이 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_updateSoptLetter_profileNotFound() {
        // given
        final Long userId = 1L;
        final Long messageId = 125L;

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .authorProfileId(10L)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.updateSoptLetter(userId, messageId, "수정 내용"))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_수정 시 본인 글이 아니면 ForbiddenException이 발생한다")
    void FAIL_updateSoptLetter_forbidden() {
        // given
        final Long userId = 1L;
        final Long messageId = 125L;

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .authorProfileId(99L) // 다른 유저의 프로필 ID
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L) // 요청 유저의 프로필 ID
                .userId(userId)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when & then
        assertThatThrownBy(() -> soptLetterService.updateSoptLetter(userId, messageId, "수정 내용"))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(e -> {
                    ForbiddenException exception = (ForbiddenException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
                });
    }

    @Test
    @DisplayName("SUCCESS_솝레터 메시지를 성공적으로 삭제한다")
    void SUCCESS_deleteSoptLetter() {
        // given
        final Long userId = 1L;
        final Long messageId = 125L;

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .authorProfileId(10L)
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when
        soptLetterService.deleteSoptLetter(userId, messageId);

        // then
        verify(soptLetterLikeRepository, times(1)).deleteAllByLetterIdInQuery(messageId);
        verify(soptLetterRepository, times(1)).delete(letter);
    }

    @Test
    @DisplayName("FAIL_삭제 시 솝레터 메시지가 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_deleteSoptLetter_letterNotFound() {
        // given
        final Long userId = 1L;
        final Long messageId = 999L;

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.deleteSoptLetter(userId, messageId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_삭제 시 솝레터 프로필이 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_deleteSoptLetter_profileNotFound() {
        // given
        final Long userId = 1L;
        final Long messageId = 125L;

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .authorProfileId(10L)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.deleteSoptLetter(userId, messageId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_삭제 시 본인 글이 아니면 ForbiddenException이 발생한다")
    void FAIL_deleteSoptLetter_forbidden() {
        // given
        final Long userId = 1L;
        final Long messageId = 125L;

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .authorProfileId(99L)
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when & then
        assertThatThrownBy(() -> soptLetterService.deleteSoptLetter(userId, messageId))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(e -> {
                    ForbiddenException exception = (ForbiddenException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
                });
    }
}
