package org.sopt.app.application.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.event.Event;
import org.sopt.app.common.event.EventPublisher;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class SoptLetterServiceTest {

    private static final Long USER_ID = 1L;

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
    private OperationConfigService operationConfigService;

    @Mock
    private Clock clock;

    @Mock
    private EventPublisher eventPublisher;

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
    @DisplayName("SUCCESS_솝레터 익명 신고 폼 주소를 조회한다")
    void SUCCESS_getReportForm() {
        // given
        final String reportFormUrl = "https://example.com/sopt-letter-report";
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "linkUrl"))
                .thenReturn(reportFormUrl);

        // when
        SoptLetterInfo.ReportFormResult result = soptLetterService.getReportForm();

        // then
        assertThat(result.getReportFormUrl()).isEqualTo(reportFormUrl);
        verify(operationConfigService, times(1))
                .getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "linkUrl");
    }

    @Test
    @DisplayName("SUCCESS_현재 노출 기간인 최신 솝레터 CTA를 조회한다")
    void SUCCESS_getCta() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 7, 25, 12, 0);
        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        when(topic.getId()).thenReturn(3L);
        when(topic.getCtaText()).thenReturn("38기 SOPT 활동 어떠셨나요? 👀");
        when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
        when(soptLetterTopicRepository.findActiveCtas(now)).thenReturn(List.of(topic));

        // when
        SoptLetterInfo.CtaResult result = soptLetterService.getCta();

        // then
        assertThat(result.isShowCta()).isTrue();
        assertThat(result.getTopicId()).isEqualTo(3L);
        assertThat(result.getCtaText()).isEqualTo("38기 SOPT 활동 어떠셨나요? 👀");
        verify(soptLetterTopicRepository, times(1)).findActiveCtas(now);
    }

    @Test
    @DisplayName("SUCCESS_현재 노출할 솝레터 CTA가 없으면 미노출 응답을 반환한다")
    void SUCCESS_getCta_hidden() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 8, 8, 0, 0);
        when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
        when(soptLetterTopicRepository.findActiveCtas(now)).thenReturn(List.of());

        // when
        SoptLetterInfo.CtaResult result = soptLetterService.getCta();

        // then
        assertThat(result.isShowCta()).isFalse();
        assertThat(result.getTopicId()).isNull();
        assertThat(result.getCtaText()).isNull();
    }

    @Nested
    @DisplayName("주제 목록 조회")
    class GetTopics {

        @Nested
        @DisplayName("주제 목록은 기본 주제를 맨 앞에 두고 나머지는 created_at 최신순을 지킨다")
        class Sort {

            @Test
            @DisplayName("SUCCESS_type 없이 조회하면 기본 주제 우선 정렬 쿼리로 전체 주제를 불러온다")
            void noTypeLoadsAllTopicsWithDefaultFirst() {
                // given 쿼리가 기본 주제를 먼저 주고 더 최신인 일반 주제를 뒤에 준다
                LocalDateTime firstCreatedAt = LocalDateTime.of(2026, 4, 20, 0, 0);
                LocalDateTime secondCreatedAt = LocalDateTime.of(2026, 4, 18, 0, 0);
                SoptLetterTopic firstTopic = mock(SoptLetterTopic.class);
                SoptLetterTopic secondTopic = mock(SoptLetterTopic.class);
                when(firstTopic.getId()).thenReturn(2L);
                when(firstTopic.getTitle()).thenReturn("36기 앱잼 회고");
                when(firstTopic.isDefault()).thenReturn(false);
                when(firstTopic.getCreatedAt()).thenReturn(firstCreatedAt);
                when(secondTopic.getId()).thenReturn(1L);
                when(secondTopic.getTitle()).thenReturn("36기 회고");
                when(secondTopic.isDefault()).thenReturn(true);
                when(secondTopic.getCreatedAt()).thenReturn(secondCreatedAt);
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc())
                        .thenReturn(List.of(secondTopic, firstTopic));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics(null);

                // then 기본 주제가 첫째로 오고 제목과 created_at을 그대로 담는다. 전체 주제 조회만 부르고 type별 조회는 부르지 않는다
                assertThat(result.getTopics()).hasSize(2);
                assertThat(result.getTopics().get(0).getTopicId()).isEqualTo(1L);
                assertThat(result.getTopics().get(0).getTitle()).isEqualTo("36기 회고");
                assertThat(result.getTopics().get(0).isDefault()).isTrue();
                assertThat(result.getTopics().get(0).getCreatedAt()).isEqualTo(secondCreatedAt);
                assertThat(result.getTopics().get(1).getTopicId()).isEqualTo(2L);
                assertThat(result.getTopics().get(1).isDefault()).isFalse();
                verify(soptLetterTopicRepository, times(1)).findAllDefaultFirstOrderByCreatedAtDesc();
                verify(soptLetterTopicRepository, never()).findAllDefaultTopicsOrderByCreatedAtDesc();
                verify(soptLetterTopicRepository, never()).findAllNormalTopicsOrderByCreatedAtDesc();
            }

            @Test
            @DisplayName("SUCCESS_쿼리가 준 순서를 다시 정렬하지 않는다")
            void keepsQueryOrder() {
                // given topicId 순서와 created_at 순서가 모두 어긋난 순서로 준다. id나 created_at으로 다시 정렬하는 구현이면 순서가 바뀐다
                SoptLetterTopic defaultTopic4 = mockTopic(4L, true, LocalDateTime.of(2026, 9, 20, 0, 0));
                SoptLetterTopic defaultTopic2 = mockTopic(2L, true, LocalDateTime.of(2026, 9, 1, 0, 0));
                SoptLetterTopic normalTopic1 = mockTopic(1L, false, LocalDateTime.of(2026, 10, 5, 0, 0));
                SoptLetterTopic normalTopic3 = mockTopic(3L, false, LocalDateTime.of(2026, 9, 10, 0, 0));
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc())
                        .thenReturn(List.of(defaultTopic4, defaultTopic2, normalTopic1, normalTopic3));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics(null);

                // then 쿼리가 준 순서 그대로 나온다
                assertThat(result.getTopics())
                        .extracting(SoptLetterInfo.TopicSummary::getTopicId)
                        .containsExactly(4L, 2L, 1L, 3L);
            }

            @Test
            @DisplayName("SUCCESS_주제가 하나도 없으면 빈 목록을 반환한다")
            void emptyWhenNoTopics() {
                // given 저장소에 주제가 하나도 없다
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc()).thenReturn(List.of());

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics(null);

                // then 예외 없이 빈 목록을 돌려준다
                assertThat(result.getTopics()).isEmpty();
            }
        }

        @Nested
        @DisplayName("주제마다 subtitle을 그대로 내려준다")
        class Subtitle {

            @Test
            @DisplayName("SUCCESS_subtitle이 있는 주제는 그 값을, 없는 주제는 null을 내려준다")
            void returnsSubtitleOrNullPerTopic() {
                // given 한 주제에만 subtitle을 두고 다른 주제는 비워 둔다. 값이 다른 주제로 옮겨 붙지 않는지 함께 본다
                SoptLetterTopic topicWithSubtitle = mockTopic(2L, false, LocalDateTime.of(2026, 10, 5, 0, 0));
                SoptLetterTopic topicWithoutSubtitle = mockTopic(1L, false, LocalDateTime.of(2026, 9, 1, 0, 0));
                when(topicWithSubtitle.getSubtitle()).thenReturn("앱잼 끝나고 하고 싶은 말");
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc())
                        .thenReturn(List.of(topicWithSubtitle, topicWithoutSubtitle));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics(null);

                // then 각 주제가 자기 subtitle을 그대로 받는다
                assertThat(result.getTopics().get(0).getSubtitle()).isEqualTo("앱잼 끝나고 하고 싶은 말");
                assertThat(result.getTopics().get(1).getSubtitle()).isNull();
            }
        }

        @Nested
        @DisplayName("HOT 뱃지는 최근 7일 솝레터 수 상위 2개 주제에 붙는다")
        class Hot {

            @Test
            @DisplayName("SUCCESS_집계 쿼리가 돌려준 주제만 HOT을 받는다")
            void onlyQueriedTopicsAreHot() {
                // given 쿼리가 고른 두 주제를 목록 순서와 다르게 돌려준다. 가장 최신 주제는 쿼리 결과에 없다
                SoptLetterTopic topic3 = mockTopic(3L, false, LocalDateTime.of(2026, 10, 5, 0, 0));
                SoptLetterTopic topic2 = mockTopic(2L, false, LocalDateTime.of(2026, 9, 20, 0, 0));
                SoptLetterTopic topic1 = mockTopic(1L, false, LocalDateTime.of(2026, 9, 1, 0, 0));
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc()).thenReturn(List.of(topic3, topic2, topic1));
                when(soptLetterRepository.findTopTopicIdsByLetterCountSince(any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(List.of(1L, 2L));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics(null);

                // then 쿼리가 돌려준 두 주제만 HOT이고 가장 최신 주제는 HOT이 아니다
                assertThat(result.getTopics())
                        .extracting(SoptLetterInfo.TopicSummary::getTopicId, SoptLetterInfo.TopicSummary::isHot)
                        .containsExactly(tuple(3L, false), tuple(2L, true), tuple(1L, true));
            }

            @Test
            @DisplayName("SUCCESS_집계 쿼리가 주제 하나만 돌려주면 그 주제만 HOT을 받는다")
            void onlyQueriedTopicIsHotWhenSingle() {
                // given 주제는 셋이지만 쿼리 결과에는 가운데 주제 하나만 있다. 두 자리를 채우려고 다른 주제까지 HOT을 주는 구현이면 틀린다
                SoptLetterTopic topic3 = mockTopic(3L, false, LocalDateTime.of(2026, 10, 5, 0, 0));
                SoptLetterTopic topic2 = mockTopic(2L, false, LocalDateTime.of(2026, 9, 20, 0, 0));
                SoptLetterTopic topic1 = mockTopic(1L, false, LocalDateTime.of(2026, 9, 1, 0, 0));
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc()).thenReturn(List.of(topic3, topic2, topic1));
                when(soptLetterRepository.findTopTopicIdsByLetterCountSince(any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(List.of(2L));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics(null);

                // then 쿼리가 돌려준 주제만 HOT이다
                assertThat(result.getTopics())
                        .extracting(SoptLetterInfo.TopicSummary::getTopicId, SoptLetterInfo.TopicSummary::isHot)
                        .containsExactly(tuple(3L, false), tuple(2L, true), tuple(1L, false));
            }

            @Test
            @DisplayName("SUCCESS_집계 쿼리 결과가 비어 있으면 어느 주제도 HOT을 받지 않는다")
            void noHotWhenQueryEmpty() {
                // given 주제는 있지만 쿼리 결과가 비어 있다
                SoptLetterTopic topic2 = mockTopic(2L, false, LocalDateTime.of(2026, 9, 20, 0, 0));
                SoptLetterTopic topic1 = mockTopic(1L, false, LocalDateTime.of(2026, 9, 1, 0, 0));
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc()).thenReturn(List.of(topic2, topic1));
                when(soptLetterRepository.findTopTopicIdsByLetterCountSince(any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(List.of());

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics(null);

                // then HOT을 받은 주제가 하나도 없다
                assertThat(result.getTopics())
                        .extracting(SoptLetterInfo.TopicSummary::isHot)
                        .containsExactly(false, false);
            }

            @Test
            @DisplayName("SUCCESS_type이 normal이어도 HOT은 기본 주제를 포함한 전체 주제 순위를 따른다")
            void normalTypeFollowsOverallRanking() {
                // given 목록에는 일반 주제 둘만 있고 쿼리는 목록에 없는 기본 주제를 1위로 돌려준다. 목록 안에서 빈 자리를 채우는 구현이면 두 주제 모두 HOT을 받는다
                SoptLetterTopic topic3 = mockTopic(3L, false, LocalDateTime.of(2026, 10, 5, 0, 0));
                SoptLetterTopic topic2 = mockTopic(2L, false, LocalDateTime.of(2026, 9, 20, 0, 0));
                when(soptLetterTopicRepository.findAllNormalTopicsOrderByCreatedAtDesc()).thenReturn(List.of(topic3, topic2));
                when(soptLetterRepository.findTopTopicIdsByLetterCountSince(any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(List.of(1L, 2L));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics("normal");

                // then 기본 주제가 1위 자리를 가져가서 일반 주제는 쿼리가 고른 하나만 HOT
                assertThat(result.getTopics())
                        .extracting(SoptLetterInfo.TopicSummary::getTopicId, SoptLetterInfo.TopicSummary::isHot)
                        .containsExactly(tuple(3L, false), tuple(2L, true));
            }

            @Test
            @DisplayName("SUCCESS_집계 쿼리에 현재 시각에서 7일 전 시각과 상위 2개 제한을 넘긴다")
            void queriesSinceSevenDaysAgoWithTopTwo() {
                // given 현재 시각을 정오로 고정한다. 날짜만 남기고 시각을 버리는 구현을 잡으려고 자정이 아닌 시각을 고른다
                LocalDateTime now = LocalDateTime.of(2026, 10, 10, 12, 0);
                SoptLetterTopic topic = mockTopic(1L, false, LocalDateTime.of(2026, 10, 1, 0, 0));
                when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc()).thenReturn(List.of(topic));

                // when
                soptLetterService.getTopics(null);

                // then 집계 쿼리를 한 번 부르고 7일 전 같은 시각과 첫 페이지 2개를 넘긴다
                ArgumentCaptor<LocalDateTime> sinceCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
                verify(soptLetterRepository, times(1))
                        .findTopTopicIdsByLetterCountSince(sinceCaptor.capture(), pageableCaptor.capture());
                assertThat(sinceCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 10, 3, 12, 0));
                assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(0, 2));
            }

            @Test
            @DisplayName("SUCCESS_NEW 주제도 HOT 조건을 채우면 두 뱃지를 함께 받는다")
            void newAndHotAtSameTime() {
                // given NEW로 판정되는 주제 하나가 최근 7일 집계에도 들어 있다. 두 뱃지가 서로를 덮어쓰지 않는지 본다
                LocalDateTime now = LocalDateTime.of(2026, 10, 10, 12, 0);
                SoptLetterTopic topic = mockTopic(1L, false, LocalDateTime.of(2026, 10, 9, 0, 0));
                when(topic.isNewAt(now)).thenReturn(true);
                when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
                when(soptLetterTopicRepository.findAllDefaultFirstOrderByCreatedAtDesc()).thenReturn(List.of(topic));
                when(soptLetterRepository.findTopTopicIdsByLetterCountSince(any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(List.of(1L));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics(null);

                // then NEW와 HOT을 함께 받는다
                assertThat(result.getTopics().get(0).isNew()).isTrue();
                assertThat(result.getTopics().get(0).isHot()).isTrue();
            }
        }

        @Nested
        @DisplayName("type으로 기본 주제나 일반 주제만 고를 수 있다")
        class TypeFilter {

            @Test
            @DisplayName("SUCCESS_type이 default면 기본 주제만 조회한다")
            void defaultTypeReturnsOnlyDefaultTopics() {
                // given type을 default로 넘기고 기본 주제 하나에 NEW와 HOT 조건을 모두 채운다. type별 조회 경로에서도 뱃지를 계산하는지 함께 본다
                LocalDateTime now = LocalDateTime.of(2026, 4, 20, 12, 0);
                LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 0, 0);
                SoptLetterTopic defaultTopic = mock(SoptLetterTopic.class);
                when(defaultTopic.getId()).thenReturn(1L);
                when(defaultTopic.getTitle()).thenReturn("36기 솝레터");
                when(defaultTopic.isDefault()).thenReturn(true);
                when(defaultTopic.getCreatedAt()).thenReturn(createdAt);
                when(defaultTopic.isNewAt(now)).thenReturn(true);
                when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
                when(soptLetterTopicRepository.findAllDefaultTopicsOrderByCreatedAtDesc()).thenReturn(List.of(defaultTopic));
                when(soptLetterRepository.findTopTopicIdsByLetterCountSince(any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(List.of(1L));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics("default");

                // then 기본 주제 조회만 부르고 주제 정보와 NEW, HOT 뱃지를 그대로 담는다
                assertThat(result.getTopics()).hasSize(1);
                assertThat(result.getTopics().get(0).getTopicId()).isEqualTo(1L);
                assertThat(result.getTopics().get(0).getTitle()).isEqualTo("36기 솝레터");
                assertThat(result.getTopics().get(0).isDefault()).isTrue();
                assertThat(result.getTopics().get(0).getCreatedAt()).isEqualTo(createdAt);
                assertThat(result.getTopics().get(0).isNew()).isTrue();
                assertThat(result.getTopics().get(0).isHot()).isTrue();
                verify(soptLetterTopicRepository, times(1)).findAllDefaultTopicsOrderByCreatedAtDesc();
                verify(soptLetterTopicRepository, never()).findAllDefaultFirstOrderByCreatedAtDesc();
                verify(soptLetterTopicRepository, never()).findAllNormalTopicsOrderByCreatedAtDesc();
            }

            @Test
            @DisplayName("SUCCESS_type이 normal이면 기본 주제를 뺀 일반 주제만 조회한다")
            void normalTypeReturnsOnlyNormalTopics() {
                // given type을 normal로 넘기고 일반 주제 하나에 HOT 조건만 채운다. 두 뱃지를 따로 계산하는지 보려고 NEW 조건은 비워 둔다
                LocalDateTime createdAt = LocalDateTime.of(2026, 7, 19, 0, 0);
                SoptLetterTopic normalTopic = mock(SoptLetterTopic.class);
                when(normalTopic.getId()).thenReturn(2L);
                when(normalTopic.getTitle()).thenReturn("38기 앱잼 회고");
                when(normalTopic.isDefault()).thenReturn(false);
                when(normalTopic.getCreatedAt()).thenReturn(createdAt);
                when(soptLetterTopicRepository.findAllNormalTopicsOrderByCreatedAtDesc()).thenReturn(List.of(normalTopic));
                when(soptLetterRepository.findTopTopicIdsByLetterCountSince(any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(List.of(2L));

                // when
                SoptLetterInfo.TopicListResult result = soptLetterService.getTopics("normal");

                // then 일반 주제 조회만 부르고 주제 정보를 담는다. NEW는 아니고 HOT이다
                assertThat(result.getTopics()).hasSize(1);
                assertThat(result.getTopics().get(0).getTopicId()).isEqualTo(2L);
                assertThat(result.getTopics().get(0).getTitle()).isEqualTo("38기 앱잼 회고");
                assertThat(result.getTopics().get(0).isDefault()).isFalse();
                assertThat(result.getTopics().get(0).getCreatedAt()).isEqualTo(createdAt);
                assertThat(result.getTopics().get(0).isNew()).isFalse();
                assertThat(result.getTopics().get(0).isHot()).isTrue();
                verify(soptLetterTopicRepository, times(1)).findAllNormalTopicsOrderByCreatedAtDesc();
                verify(soptLetterTopicRepository, never()).findAllDefaultTopicsOrderByCreatedAtDesc();
                verify(soptLetterTopicRepository, never()).findAllDefaultFirstOrderByCreatedAtDesc();
            }

            @Test
            @DisplayName("FAIL_지원하지 않는 type이면 BadRequestException이 발생하고 집계 쿼리를 호출하지 않는다")
            void rejectsUnsupportedType() {
                // given 지원 목록에 없는 type 문자열을 넘긴다

                // when & then 예외로 막고 주제 조회와 집계 쿼리를 하나도 부르지 않는다
                assertThatThrownBy(() -> soptLetterService.getTopics("invalid"))
                        .isInstanceOf(BadRequestException.class)
                        .satisfies(e -> {
                            BadRequestException exception = (BadRequestException) e;
                            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAMETER);
                        });
                verify(soptLetterTopicRepository, never()).findAllDefaultFirstOrderByCreatedAtDesc();
                verify(soptLetterTopicRepository, never()).findAllDefaultTopicsOrderByCreatedAtDesc();
                verify(soptLetterTopicRepository, never()).findAllNormalTopicsOrderByCreatedAtDesc();
                verify(soptLetterRepository, never()).findTopTopicIdsByLetterCountSince(any(), any());
            }
        }
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
        assertThat(profile.isHasWritten()).isTrue();
        assertThat(result.getColorCode()).isEqualTo(SoptLetterColor.BLUE_50.getHexCode());
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        assertThat(result.getLikedByMe()).isFalse();
        assertThat(result.getMine()).isTrue();
        verify(soptLetterRepository, times(1)).save(any(SoptLetter.class));
    }

    @Test
    @DisplayName("FAIL_메시지 저장에 실패하면 프로필의 작성 이력을 변경하지 않는다")
    void FAIL_createSoptLetter_saveFailure() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final String content = "테스트 편지 내용";

        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();
        SoptLetter letter = mock(SoptLetter.class);

        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterGenerator.generate(profile.getId(), topicId, content, null)).thenReturn(letter);
        when(soptLetterRepository.save(any(SoptLetter.class))).thenThrow(new IllegalStateException("저장 실패"));

        // when & then
        assertThatThrownBy(() -> soptLetterService.createSoptLetter(userId, topicId, content))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("저장 실패");
        assertThat(profile.isHasWritten()).isFalse();
        verify(soptLetterRepository, times(1)).save(letter);
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
        SoptLetterInfo.MessageResult result = soptLetterService.updateSoptLetter(userId, 3L, messageId, newContent);

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
        assertThatThrownBy(() -> soptLetterService.updateSoptLetter(userId, 3L, messageId, "수정 내용"))
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
                .topicId(3L)
                .authorProfileId(10L)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.updateSoptLetter(userId, 3L, messageId, "수정 내용"))
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
                .topicId(3L)
                .authorProfileId(99L) // 다른 유저의 프로필 ID
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L) // 요청 유저의 프로필 ID
                .userId(userId)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when & then
        assertThatThrownBy(() -> soptLetterService.updateSoptLetter(userId, 3L, messageId, "수정 내용"))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(e -> {
                    ForbiddenException exception = (ForbiddenException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
                });
    }

    @Test
    @DisplayName("FAIL_수정 시 요청한 토픽 ID와 메시지의 토픽 ID가 다르면 NotFoundException이 발생한다")
    void FAIL_updateSoptLetter_topicNotMatched() {
        // given
        final Long userId = 1L;
        final Long messageId = 125L;

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .authorProfileId(10L)
                .topicId(4L) // 다른 토픽 ID
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when & then
        assertThatThrownBy(() -> soptLetterService.updateSoptLetter(userId, 3L, messageId, "수정 내용"))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NOT_FOUND);
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
                .topicId(3L)
                .authorProfileId(10L)
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .hasWritten(true)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when
        soptLetterService.deleteSoptLetter(userId, 3L, messageId);

        // then
        verify(soptLetterLikeRepository, times(1)).deleteAllByLetterIdInQuery(messageId);
        verify(soptLetterRepository, times(1)).delete(letter);
        assertThat(profile.isHasWritten()).isTrue();
    }

    @Test
    @DisplayName("FAIL_삭제 시 솝레터 메시지가 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_deleteSoptLetter_letterNotFound() {
        // given
        final Long userId = 1L;
        final Long messageId = 999L;

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.deleteSoptLetter(userId, 3L, messageId))
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
                .topicId(3L)
                .authorProfileId(10L)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.deleteSoptLetter(userId, 3L, messageId))
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
                .topicId(3L)
                .authorProfileId(99L)
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when & then
        assertThatThrownBy(() -> soptLetterService.deleteSoptLetter(userId, 3L, messageId))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(e -> {
                    ForbiddenException exception = (ForbiddenException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
                });
    }

    @Test
    @DisplayName("FAIL_삭제 시 요청한 토픽 ID와 메시지의 토픽 ID가 다르면 NotFoundException이 발생한다")
    void FAIL_deleteSoptLetter_topicNotMatched() {
        // given
        final Long userId = 1L;
        final Long messageId = 125L;

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .authorProfileId(10L)
                .topicId(4L) // 다른 토픽 ID
                .build();

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .build();

        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // when & then
        assertThatThrownBy(() -> soptLetterService.deleteSoptLetter(userId, 3L, messageId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("SUCCESS_좋아요를 누르지 않은 메시지에 좋아요를 추가하면 좋아요 수가 증가한다")
    void SUCCESS_addLike() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;
        when(soptLetterRepository.existsByIdAndTopicId(messageId, topicId)).thenReturn(true);
        when(soptLetterLikeRepository.insertIgnore(userId, messageId)).thenReturn(1);

        // when
        soptLetterService.addLike(userId, topicId, messageId);

        // then
        verify(soptLetterLikeRepository, times(1)).insertIgnore(userId, messageId);
        verify(soptLetterRepository, times(1)).increaseLikeCount(messageId);
    }

    @Test
    @DisplayName("SUCCESS_이미 좋아요를 누른 메시지에 좋아요를 추가하면 현재 상태를 그대로 성공 처리한다")
    void SUCCESS_addLike_alreadyLiked() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;
        when(soptLetterRepository.existsByIdAndTopicId(messageId, topicId)).thenReturn(true);
        when(soptLetterLikeRepository.insertIgnore(userId, messageId)).thenReturn(0);

        // when
        soptLetterService.addLike(userId, topicId, messageId);

        // then
        verify(soptLetterLikeRepository, times(1)).insertIgnore(userId, messageId);
        verify(soptLetterRepository, never()).increaseLikeCount(messageId);
    }

    @Test
    @DisplayName("FAIL_존재하지 않는 메시지에 좋아요 추가 시 NotFoundException이 발생한다")
    void FAIL_addLike_letterNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 999L;
        when(soptLetterRepository.existsByIdAndTopicId(messageId, topicId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> soptLetterService.addLike(userId, topicId, messageId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NOT_FOUND);
                });
        verify(soptLetterLikeRepository, never()).insertIgnore(userId, messageId);
    }

    @Test
    @DisplayName("SUCCESS_좋아요를 누른 메시지의 좋아요를 삭제하면 좋아요 수가 감소한다")
    void SUCCESS_removeLike() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;
        when(soptLetterRepository.existsByIdAndTopicId(messageId, topicId)).thenReturn(true);
        when(soptLetterLikeRepository.deleteByLetterIdAndUserId(messageId, userId)).thenReturn(1);

        // when
        soptLetterService.removeLike(userId, topicId, messageId);

        // then
        verify(soptLetterLikeRepository, times(1)).deleteByLetterIdAndUserId(messageId, userId);
        verify(soptLetterRepository, times(1)).decreaseLikeCount(messageId);
    }

    @Test
    @DisplayName("SUCCESS_좋아요가 없는 메시지의 좋아요를 삭제하면 현재 상태를 그대로 성공 처리한다")
    void SUCCESS_removeLike_notLiked() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;
        when(soptLetterRepository.existsByIdAndTopicId(messageId, topicId)).thenReturn(true);
        when(soptLetterLikeRepository.deleteByLetterIdAndUserId(messageId, userId)).thenReturn(0);

        // when
        soptLetterService.removeLike(userId, topicId, messageId);

        // then
        verify(soptLetterLikeRepository, times(1)).deleteByLetterIdAndUserId(messageId, userId);
        verify(soptLetterRepository, never()).decreaseLikeCount(messageId);
    }

    @Test
    @DisplayName("FAIL_존재하지 않는 메시지에 좋아요 삭제 시 NotFoundException이 발생한다")
    void FAIL_removeLike_letterNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 999L;
        when(soptLetterRepository.existsByIdAndTopicId(messageId, topicId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> soptLetterService.removeLike(userId, topicId, messageId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NOT_FOUND);
                });
        verify(soptLetterLikeRepository, never()).deleteByLetterIdAndUserId(messageId, userId);
    }

    @Test
    @DisplayName("SUCCESS_내가 작성한 솝레터 메시지 상세 조회 성공")
    void SUCCESS_getMessageDetail_mine() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 124L;

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .topicId(topicId)
                .authorProfileId(10L)
                .message("테스트 메시지 내용")
                .color(SoptLetterColor.YELLOW_50)
                .shapeType(SoptLetterShapeType.CLOUD)
                .degree(0.0)
                .likeCount(0)
                .build();

        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterLikeRepository.existsByLetterIdAndUserId(messageId, userId)).thenReturn(false);

        // when
        SoptLetterInfo.MessageResult result = soptLetterService.getMessageDetail(userId, topicId, messageId);

        // then
        assertThat(result.getMessageId()).isEqualTo(messageId);
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        assertThat(result.getContent()).isEqualTo("테스트 메시지 내용");
        assertThat(result.getLikedByMe()).isFalse();
        assertThat(result.getMine()).isTrue();
    }

    @Test
    @DisplayName("SUCCESS_타인이 작성한 솝레터 메시지 상세 조회 성공")
    void SUCCESS_getMessageDetail_other() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 124L;

        SoptLetterProfile myProfile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("내 닉네임")
                .build();

        SoptLetterProfile authorProfile = SoptLetterProfile.builder()
                .id(20L)
                .userId(2L)
                .nickname("반짝이는 고래")
                .build();

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .topicId(topicId)
                .authorProfileId(20L)
                .message("테스트 메시지 내용")
                .color(SoptLetterColor.YELLOW_50)
                .shapeType(SoptLetterShapeType.CLOUD)
                .degree(0.0)
                .likeCount(0)
                .build();

        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(myProfile));
        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));
        when(soptLetterProfileRepository.findById(authorProfile.getId())).thenReturn(Optional.of(authorProfile));
        when(soptLetterLikeRepository.existsByLetterIdAndUserId(messageId, userId)).thenReturn(true);

        // when
        SoptLetterInfo.MessageResult result = soptLetterService.getMessageDetail(userId, topicId, messageId);

        // then
        assertThat(result.getMessageId()).isEqualTo(messageId);
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        assertThat(result.getContent()).isEqualTo("테스트 메시지 내용");
        assertThat(result.getLikedByMe()).isTrue();
        assertThat(result.getMine()).isFalse();
    }

    @Test
    @DisplayName("FAIL_상세 조회 시 사용자의 프로필이 없으면 NotFoundException이 발생한다")
    void FAIL_getMessageDetail_profileNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 124L;

        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.getMessageDetail(userId, topicId, messageId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_상세 조회 시 메시지가 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_getMessageDetail_letterNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 999L;

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .build();

        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.getMessageDetail(userId, topicId, messageId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_상세 조회 시 요청한 토픽 ID와 메시지의 토픽 ID가 다르면 NotFoundException이 발생한다")
    void FAIL_getMessageDetail_topicNotMatched() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 124L;

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .build();

        SoptLetter letter = SoptLetter.builder()
                .id(messageId)
                .topicId(4L) // 다른 토픽 ID
                .authorProfileId(10L)
                .build();

        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.findById(messageId)).thenReturn(Optional.of(letter));

        // when & then
        assertThatThrownBy(() -> soptLetterService.getMessageDetail(userId, topicId, messageId))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("SUCCESS_주제별 솝레터 메시지 목록을 최신순으로 조회한다")
    void SUCCESS_getTopicMessages() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final int size = 2;
        final String longContent = "1234567890".repeat(6);

        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        when(topic.getId()).thenReturn(topicId);
        when(topic.getTitle()).thenReturn("36기 회고");

        SoptLetterProfile requesterProfile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("내 닉네임")
                .build();
        SoptLetterProfile otherProfile = SoptLetterProfile.builder()
                .id(20L)
                .userId(2L)
                .nickname("반짝이는 고래")
                .build();

        SoptLetter firstLetter = SoptLetter.builder()
                .id(125L)
                .topicId(topicId)
                .authorProfileId(20L)
                .message(longContent)
                .color(SoptLetterColor.GREEN_50)
                .shapeType(SoptLetterShapeType.POINT)
                .degree(4.0)
                .likeCount(5)
                .build();
        SoptLetter secondLetter = SoptLetter.builder()
                .id(124L)
                .topicId(topicId)
                .authorProfileId(10L)
                .message("내가 쓴 메시지")
                .color(SoptLetterColor.YELLOW_50)
                .shapeType(SoptLetterShapeType.CLOUD)
                .degree(0.0)
                .likeCount(1)
                .build();
        SoptLetter extraLetter = SoptLetter.builder()
                .id(123L)
                .topicId(topicId)
                .authorProfileId(20L)
                .message("다음 페이지 메시지")
                .color(SoptLetterColor.RED_50)
                .shapeType(SoptLetterShapeType.POINT)
                .degree(-4.0)
                .likeCount(0)
                .build();

        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(requesterProfile));
        when(soptLetterRepository.findAllByTopicIdOrderByIdDesc(anyLong(), any()))
                .thenReturn(List.of(firstLetter, secondLetter, extraLetter));
        when(soptLetterLikeRepository.findLikedLetterIdsByUserIdAndLetterIdIn(anyLong(), any()))
                .thenReturn(Set.of(125L));
        when(soptLetterProfileRepository.findAllById(any())).thenReturn(List.of(requesterProfile, otherProfile));
        when(soptLetterRepository.countByTopicId(topicId)).thenReturn(3L);

        // when
        SoptLetterInfo.TopicMessageListResult result = soptLetterService.getTopicMessages(userId, topicId, null, size);

        // then
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getTitle()).isEqualTo("36기 회고");
        assertThat(result.getTotalCount()).isEqualTo(3);
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getHasNormalTopic()).isNull();
        assertThat(result.getNextCursor()).isEqualTo(124L);
        assertThat(result.getMessages()).hasSize(2);
        assertThat(result.getMessages().get(0).getMessageId()).isEqualTo(125L);
        assertThat(result.getMessages().get(0).getAuthorNickname()).isEqualTo("반짝이는 고래");
        assertThat(result.getMessages().get(0).getPreviewContent()).isEqualTo(longContent.substring(0, 50));
        assertThat(result.getMessages().get(0).getLikedByMe()).isTrue();
        assertThat(result.getMessages().get(0).getMine()).isFalse();
        assertThat(result.getMessages().get(1).getMessageId()).isEqualTo(124L);
        assertThat(result.getMessages().get(1).getAuthorNickname()).isEqualTo("내 닉네임");
        assertThat(result.getMessages().get(1).getLikedByMe()).isFalse();
        assertThat(result.getMessages().get(1).getMine()).isTrue();
    }

    @Test
    @DisplayName("SUCCESS_커서가 있으면 커서보다 작은 메시지만 조회한다")
    void SUCCESS_getTopicMessages_withCursor() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long cursor = 120L;
        final int size = 20;

        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        when(topic.getId()).thenReturn(topicId);
        when(topic.getTitle()).thenReturn("36기 회고");

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();
        SoptLetter letter = SoptLetter.builder()
                .id(119L)
                .topicId(topicId)
                .authorProfileId(10L)
                .message("커서 이후 메시지")
                .color(SoptLetterColor.BLUE_50)
                .shapeType(SoptLetterShapeType.CLOUD)
                .degree(0.0)
                .likeCount(0)
                .build();

        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.findAllByTopicIdAndIdLessThanOrderByIdDesc(topicId, cursor, org.springframework.data.domain.PageRequest.of(0, 21)))
                .thenReturn(List.of(letter));
        when(soptLetterLikeRepository.findLikedLetterIdsByUserIdAndLetterIdIn(anyLong(), any()))
                .thenReturn(Set.of());
        when(soptLetterProfileRepository.findAllById(any())).thenReturn(List.of(profile));
        when(soptLetterRepository.countByTopicId(topicId)).thenReturn(1L);

        // when
        SoptLetterInfo.TopicMessageListResult result = soptLetterService.getTopicMessages(userId, topicId, cursor, size);

        // then
        assertThat(result.getMessages()).hasSize(1);
        assertThat(result.getMessages().get(0).getMessageId()).isEqualTo(119L);
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getNextCursor()).isEqualTo(119L);
    }

    @Test
    @DisplayName("SUCCESS_주제별 솝레터 메시지가 없으면 빈 목록을 반환한다")
    void SUCCESS_getTopicMessages_empty() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;

        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        when(topic.getId()).thenReturn(topicId);
        when(topic.getTitle()).thenReturn("36기 회고");

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();

        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.findAllByTopicIdOrderByIdDesc(anyLong(), any())).thenReturn(List.of());
        when(soptLetterRepository.countByTopicId(topicId)).thenReturn(0L);

        // when
        SoptLetterInfo.TopicMessageListResult result = soptLetterService.getTopicMessages(userId, topicId, null, 20);

        // then
        assertThat(result.getMessages()).isEmpty();
        assertThat(result.getTotalCount()).isZero();
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getNextCursor()).isNull();
        verify(soptLetterLikeRepository, never()).findLikedLetterIdsByUserIdAndLetterIdIn(anyLong(), any());
        verify(soptLetterProfileRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("FAIL_주제별 솝레터 메시지 목록 조회 시 size가 0 이하이면 BadRequestException이 발생한다")
    void FAIL_getTopicMessages_zeroSize() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;

        // when & then
        assertThatThrownBy(() -> soptLetterService.getTopicMessages(userId, topicId, null, 0))
                .isInstanceOf(BadRequestException.class)
                .satisfies(e -> {
                    BadRequestException exception = (BadRequestException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAMETER);
                });
        verify(soptLetterTopicRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("FAIL_주제별 솝레터 메시지 목록 조회 시 토픽이 없으면 NotFoundException이 발생한다")
    void FAIL_getTopicMessages_topicNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 999L;

        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.getTopicMessages(userId, topicId, null, 20))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_주제별 솝레터 메시지 목록 조회 시 프로필이 없으면 NotFoundException이 발생한다")
    void FAIL_getTopicMessages_profileNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        SoptLetterTopic topic = mock(SoptLetterTopic.class);

        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptLetterService.getTopicMessages(userId, topicId, null, 20))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_주제별 솝레터 메시지 목록 조회 시 작성자 프로필이 없으면 NotFoundException이 발생한다")
    void FAIL_getTopicMessages_authorProfileNotFound() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;

        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        SoptLetterProfile requesterProfile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("내 닉네임")
                .build();
        SoptLetter letter = SoptLetter.builder()
                .id(125L)
                .topicId(topicId)
                .authorProfileId(20L)
                .message("작성자 프로필이 없는 메시지")
                .color(SoptLetterColor.GREEN_50)
                .shapeType(SoptLetterShapeType.POINT)
                .degree(4.0)
                .likeCount(0)
                .build();

        when(soptLetterTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(requesterProfile));
        when(soptLetterRepository.findAllByTopicIdOrderByIdDesc(anyLong(), any())).thenReturn(List.of(letter));
        when(soptLetterLikeRepository.findLikedLetterIdsByUserIdAndLetterIdIn(anyLong(), any()))
                .thenReturn(Set.of());
        when(soptLetterProfileRepository.findAllById(any())).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> soptLetterService.getTopicMessages(userId, topicId, null, 20))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("SUCCESS_기본 주제의 솝레터 메시지 목록을 최신순으로 조회한다")
    void SUCCESS_getDefaultTopicMessages() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final int size = 2;

        SoptLetterTopic defaultTopic = mock(SoptLetterTopic.class);
        when(defaultTopic.getId()).thenReturn(topicId);
        when(defaultTopic.getTitle()).thenReturn("36기 회고");

        SoptLetterProfile requesterProfile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("내 닉네임")
                .build();
        SoptLetterProfile otherProfile = SoptLetterProfile.builder()
                .id(20L)
                .userId(2L)
                .nickname("반짝이는 고래")
                .build();

        SoptLetter firstLetter = SoptLetter.builder()
                .id(125L)
                .topicId(topicId)
                .authorProfileId(20L)
                .message("첫 번째 메시지")
                .color(SoptLetterColor.GREEN_50)
                .shapeType(SoptLetterShapeType.POINT)
                .degree(4.0)
                .likeCount(5)
                .build();
        SoptLetter secondLetter = SoptLetter.builder()
                .id(124L)
                .topicId(topicId)
                .authorProfileId(10L)
                .message("내가 쓴 메시지")
                .color(SoptLetterColor.YELLOW_50)
                .shapeType(SoptLetterShapeType.CLOUD)
                .degree(0.0)
                .likeCount(1)
                .build();
        SoptLetter extraLetter = SoptLetter.builder()
                .id(123L)
                .topicId(topicId)
                .authorProfileId(20L)
                .message("다음 페이지 메시지")
                .color(SoptLetterColor.RED_50)
                .shapeType(SoptLetterShapeType.POINT)
                .degree(-4.0)
                .likeCount(0)
                .build();

        when(soptLetterTopicRepository.findAllDefaultTopicsOrderByCreatedAtDesc())
                .thenReturn(List.of(defaultTopic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(requesterProfile));
        when(soptLetterRepository.findAllByTopicIdOrderByIdDesc(anyLong(), any()))
                .thenReturn(List.of(firstLetter, secondLetter, extraLetter));
        when(soptLetterLikeRepository.findLikedLetterIdsByUserIdAndLetterIdIn(anyLong(), any()))
                .thenReturn(Set.of(125L));
        when(soptLetterProfileRepository.findAllById(any())).thenReturn(List.of(requesterProfile, otherProfile));
        when(soptLetterRepository.countByTopicId(topicId)).thenReturn(3L);
        when(soptLetterTopicRepository.existsNormalTopic()).thenReturn(true);

        // when
        SoptLetterInfo.TopicMessageListResult result = soptLetterService.getDefaultTopicMessages(userId, null, size);

        // then
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getTitle()).isEqualTo("36기 회고");
        assertThat(result.getTotalCount()).isEqualTo(3);
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getHasNormalTopic()).isTrue();
        assertThat(result.getNextCursor()).isEqualTo(124L);
        assertThat(result.getMessages()).hasSize(2);
        assertThat(result.getMessages().get(0).getMessageId()).isEqualTo(125L);
        assertThat(result.getMessages().get(0).getAuthorNickname()).isEqualTo("반짝이는 고래");
        assertThat(result.getMessages().get(0).getLikedByMe()).isTrue();
        assertThat(result.getMessages().get(0).getMine()).isFalse();
        assertThat(result.getMessages().get(1).getMessageId()).isEqualTo(124L);
        assertThat(result.getMessages().get(1).getMine()).isTrue();
        verify(soptLetterTopicRepository, times(1)).findAllDefaultTopicsOrderByCreatedAtDesc();
        verify(soptLetterTopicRepository, times(1)).existsNormalTopic();
        verify(soptLetterTopicRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("SUCCESS_기본 주제가 여러 개면 가장 최근 생성된 기본 주제의 메시지를 조회한다")
    void SUCCESS_getDefaultTopicMessages_picksMostRecent() {
        // given
        final Long userId = 1L;
        final Long recentTopicId = 5L;

        SoptLetterTopic recentTopic = mock(SoptLetterTopic.class);
        when(recentTopic.getId()).thenReturn(recentTopicId);
        when(recentTopic.getTitle()).thenReturn("최신 기본 주제");
        SoptLetterTopic oldTopic = mock(SoptLetterTopic.class);

        SoptLetterProfile profile = SoptLetterProfile.builder()
                .id(10L)
                .userId(userId)
                .nickname("반짝이는 고래")
                .build();

        // 레포지토리는 createdAt DESC 정렬 보장 → 목록 첫 번째가 최신
        when(soptLetterTopicRepository.findAllDefaultTopicsOrderByCreatedAtDesc())
                .thenReturn(List.of(recentTopic, oldTopic));
        when(soptLetterProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(soptLetterRepository.findAllByTopicIdOrderByIdDesc(anyLong(), any())).thenReturn(List.of());
        when(soptLetterRepository.countByTopicId(recentTopicId)).thenReturn(0L);

        // when
        SoptLetterInfo.TopicMessageListResult result = soptLetterService.getDefaultTopicMessages(userId, null, 20);

        // then
        assertThat(result.getTopicId()).isEqualTo(recentTopicId);
        assertThat(result.getTitle()).isEqualTo("최신 기본 주제");
        verify(soptLetterRepository, times(1)).countByTopicId(recentTopicId);
    }

    @Test
    @DisplayName("FAIL_기본 주제가 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_getDefaultTopicMessages_noDefaultTopic() {
        // given
        final Long userId = 1L;
        when(soptLetterTopicRepository.findAllDefaultTopicsOrderByCreatedAtDesc()).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> soptLetterService.getDefaultTopicMessages(userId, null, 20))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_TOPIC_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("FAIL_기본 주제 메시지 목록 조회 시 size가 0 이하이면 BadRequestException이 발생한다")
    void FAIL_getDefaultTopicMessages_zeroSize() {
        // given
        final Long userId = 1L;

        // when & then
        assertThatThrownBy(() -> soptLetterService.getDefaultTopicMessages(userId, null, 0))
                .isInstanceOf(BadRequestException.class)
                .satisfies(e -> {
                    BadRequestException exception = (BadRequestException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAMETER);
                });
        verify(soptLetterTopicRepository, never()).findAllDefaultTopicsOrderByCreatedAtDesc();
    }

    @Nested
    @DisplayName("읽음 기록")
    class GetTopicMessages {

        @Nested
        @DisplayName("첫 페이지를 조회하면 그 주제를 최신 메시지까지 읽음으로 기록한다")
        class ReadOnFirstPage {

            @Test
            @DisplayName("SUCCESS_첫 페이지를 조회하면 가장 큰 메시지 ID로 읽음 이벤트를 한 번 낸다")
            void raisesReadEventWithLatestLetterId() {
                // given 메시지 12, 11, 10이 있는 주제를 cursor 없이 조회한다. 유저, 주제, 메시지 ID를 모두 다른 값으로 둬서 자리를 바꿔 담는 구현을 잡는다
                SoptLetterTopic topic = mock(SoptLetterTopic.class);
                when(topic.getId()).thenReturn(3L);
                when(soptLetterTopicRepository.findById(3L)).thenReturn(Optional.of(topic));
                stubRequesterAndAuthors();
                when(soptLetterRepository.findAllByTopicIdOrderByIdDesc(anyLong(), any()))
                        .thenReturn(List.of(letter(12L, 3L), letter(11L, 3L), letter(10L, 3L)));

                // when
                soptLetterService.getTopicMessages(USER_ID, 3L, null, 20);

                // then 12번까지 읽었다는 이벤트를 한 번 낸다
                ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
                verify(eventPublisher, times(1)).raise(eventCaptor.capture());
                assertThat(eventCaptor.getValue())
                        .isInstanceOf(SoptLetterTopicReadEvent.class)
                        .extracting("userId", "topicId", "lastReadLetterId")
                        .containsExactly(USER_ID, 3L, 12L);
            }

            @Test
            @DisplayName("SUCCESS_cursor로 다음 페이지를 조회하면 읽음 이벤트를 내지 않는다")
            void noReadEventWithCursor() {
                // given cursor 10으로 그 아래 메시지 9, 8을 조회한다. 스크롤을 내린 것이라 새로 읽은 범위가 아니다
                SoptLetterTopic topic = mock(SoptLetterTopic.class);
                when(topic.getId()).thenReturn(3L);
                when(soptLetterTopicRepository.findById(3L)).thenReturn(Optional.of(topic));
                stubRequesterAndAuthors();
                when(soptLetterRepository.findAllByTopicIdAndIdLessThanOrderByIdDesc(3L, 10L, PageRequest.of(0, 21)))
                        .thenReturn(List.of(letter(9L, 3L), letter(8L, 3L)));

                // when
                soptLetterService.getTopicMessages(USER_ID, 3L, 10L, 20);

                // then 이벤트를 내지 않는다
                verify(eventPublisher, never()).raise(any());
            }

            @Test
            @DisplayName("SUCCESS_메시지가 없는 주제를 조회하면 읽음 이벤트를 내지 않는다")
            void noReadEventWhenNoLetters() {
                // given 아직 글이 하나도 없는 주제다
                SoptLetterTopic topic = mock(SoptLetterTopic.class);
                when(topic.getId()).thenReturn(3L);
                when(soptLetterTopicRepository.findById(3L)).thenReturn(Optional.of(topic));
                when(soptLetterProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(requesterProfile()));
                when(soptLetterRepository.findAllByTopicIdOrderByIdDesc(anyLong(), any())).thenReturn(List.of());

                // when
                soptLetterService.getTopicMessages(USER_ID, 3L, null, 20);

                // then 기록할 ID가 없어서 이벤트를 내지 않는다
                verify(eventPublisher, never()).raise(any());
            }

            @Test
            @DisplayName("FAIL_솝레터 프로필이 없으면 예외가 나고 읽음 이벤트를 내지 않는다")
            void noReadEventWhenProfileMissing() {
                // given 온보딩을 하지 않아 프로필이 없는 유저다
                SoptLetterTopic topic = mock(SoptLetterTopic.class);
                when(soptLetterTopicRepository.findById(3L)).thenReturn(Optional.of(topic));
                when(soptLetterProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

                // when & then SOPT_LETTER_PROFILE_NOT_FOUND 예외가 나고 이벤트는 없다
                assertThatThrownBy(() -> soptLetterService.getTopicMessages(USER_ID, 3L, null, 20))
                        .isInstanceOf(NotFoundException.class)
                        .satisfies(e -> {
                            NotFoundException exception = (NotFoundException) e;
                            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                        });
                verify(eventPublisher, never()).raise(any());
            }
        }

        @Nested
        @DisplayName("구버전 앱의 기본 주제 조회도 같은 규칙으로 읽음을 기록한다")
        class ReadOnDefaultTopicFirstPage {

            @Test
            @DisplayName("SUCCESS_구버전 기본 주제 첫 페이지를 조회해도 가장 큰 메시지 ID로 읽음 이벤트를 낸다")
            void raisesReadEventForDefaultTopic() {
                // given 기본 주제에 메시지 21, 20이 있고 cursor 없이 조회한다
                SoptLetterTopic defaultTopic = mock(SoptLetterTopic.class);
                when(defaultTopic.getId()).thenReturn(5L);
                when(soptLetterTopicRepository.findAllDefaultTopicsOrderByCreatedAtDesc()).thenReturn(List.of(defaultTopic));
                when(soptLetterTopicRepository.existsNormalTopic()).thenReturn(true);
                stubRequesterAndAuthors();
                when(soptLetterRepository.findAllByTopicIdOrderByIdDesc(anyLong(), any()))
                        .thenReturn(List.of(letter(21L, 5L), letter(20L, 5L)));

                // when
                soptLetterService.getDefaultTopicMessages(USER_ID, null, 20);

                // then 기본 주제를 21번까지 읽었다는 이벤트를 낸다
                ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
                verify(eventPublisher, times(1)).raise(eventCaptor.capture());
                assertThat(eventCaptor.getValue())
                        .isInstanceOf(SoptLetterTopicReadEvent.class)
                        .extracting("userId", "topicId", "lastReadLetterId")
                        .containsExactly(USER_ID, 5L, 21L);
            }

            @Test
            @DisplayName("SUCCESS_구버전 기본 주제를 cursor로 조회하면 읽음 이벤트를 내지 않는다")
            void noReadEventForDefaultTopicWithCursor() {
                // given cursor 20으로 메시지 19를 조회한다
                SoptLetterTopic defaultTopic = mock(SoptLetterTopic.class);
                when(defaultTopic.getId()).thenReturn(5L);
                when(soptLetterTopicRepository.findAllDefaultTopicsOrderByCreatedAtDesc()).thenReturn(List.of(defaultTopic));
                when(soptLetterTopicRepository.existsNormalTopic()).thenReturn(true);
                stubRequesterAndAuthors();
                when(soptLetterRepository.findAllByTopicIdAndIdLessThanOrderByIdDesc(5L, 20L, PageRequest.of(0, 21)))
                        .thenReturn(List.of(letter(19L, 5L)));

                // when
                soptLetterService.getDefaultTopicMessages(USER_ID, 20L, 20);

                // then 이벤트를 내지 않는다
                verify(eventPublisher, never()).raise(any());
            }
        }
    }

    private SoptLetterProfile requesterProfile() {
        return SoptLetterProfile.builder()
                .id(10L)
                .userId(USER_ID)
                .nickname("내 닉네임")
                .build();
    }

    private void stubRequesterAndAuthors() {
        SoptLetterProfile author = SoptLetterProfile.builder()
                .id(20L)
                .userId(2L)
                .nickname("반짝이는 고래")
                .build();
        when(soptLetterProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(requesterProfile()));
        when(soptLetterLikeRepository.findLikedLetterIdsByUserIdAndLetterIdIn(anyLong(), any())).thenReturn(Set.of());
        when(soptLetterProfileRepository.findAllById(any())).thenReturn(List.of(author));
    }

    private SoptLetter letter(Long id, Long topicId) {
        return SoptLetter.builder()
                .id(id)
                .topicId(topicId)
                .authorProfileId(20L)
                .message("메시지 " + id)
                .color(SoptLetterColor.GREEN_50)
                .shapeType(SoptLetterShapeType.POINT)
                .degree(0.0)
                .likeCount(0)
                .build();
    }

    private SoptLetterTopic mockTopic(Long id, boolean isDefault, LocalDateTime createdAt) {
        SoptLetterTopic topic = mock(SoptLetterTopic.class);
        when(topic.getId()).thenReturn(id);
        when(topic.isDefault()).thenReturn(isDefault);
        when(topic.getCreatedAt()).thenReturn(createdAt);
        return topic;
    }
}
