package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptletter.SoptLetterInfo;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.application.soptletter.SoptLetterService;
import org.sopt.app.application.soptletter.SoptLetterTopicReadEvent;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class SoptLetterFacadeTest {

    private static final Long USER_ID = 1L;

    @Mock
    private SoptLetterService soptLetterService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SoptLetterFacade soptLetterFacade;

    @Test
    @DisplayName("SUCCESS_온보딩 프로필 정보를 정상적으로 조회하거나 생성한다")
    void SUCCESS_getOrCreateOnboardingProfile() {
        // given
        final Long userId = 1L;
        final String generatedNickname = "익명의 솝트";
        Profile expected = Profile.builder()
                .nickname(generatedNickname)
                .isOnboarded(false)
                .build();
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
        Profile expected = Profile.builder()
                .nickname(nickname)
                .isOnboarded(true)
                .build();
        when(soptLetterService.completeOnboarding(userId)).thenReturn(expected);

        // when
        Profile result = soptLetterFacade.completeOnboardingProfile(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.isOnboarded()).isTrue();
        verify(soptLetterService, times(1)).completeOnboarding(userId);
    }

    @Test
    @DisplayName("SUCCESS_주제별 메시지 목록 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getTopicMessages() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long cursor = 120L;
        final Integer size = 20;

        SoptLetterInfo.TopicMessageSummary message = SoptLetterInfo.TopicMessageSummary.builder()
                .messageId(119L)
                .authorNickname("반짝이는 고래")
                .previewContent("커서 이후 메시지")
                .colorCode("#CCFFEC")
                .rotationDegree(4.0)
                .shapeType("POINT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0)
                .likedByMe(false)
                .mine(false)
                .build();
        SoptLetterInfo.TopicMessageListResult expected = SoptLetterInfo.TopicMessageListResult.builder()
                .topicId(topicId)
                .title("36기 회고")
                .totalCount(1)
                .nextCursor(119L)
                .hasNext(false)
                .messages(List.of(message))
                .build();

        when(soptLetterService.getTopicMessages(eq(userId), eq(topicId), eq(cursor), eq(size))).thenReturn(expected);

        // when
        SoptLetterInfo.TopicMessageListResult result = soptLetterFacade.getTopicMessages(userId, topicId, cursor, size);

        // then
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getMessages()).hasSize(1);
        assertThat(result.getNextCursor()).isEqualTo(119L);
        verify(soptLetterService, times(1)).getTopicMessages(eq(userId), eq(topicId), eq(cursor), eq(size));
    }

    @Test
    @DisplayName("SUCCESS_익명 신고 폼 주소 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getReportForm() {
        // given
        final String reportFormUrl = "https://example.com/sopt-letter-report";
        SoptLetterInfo.ReportFormResult expected = SoptLetterInfo.ReportFormResult.builder()
                .reportFormUrl(reportFormUrl)
                .build();
        when(soptLetterService.getReportForm()).thenReturn(expected);

        // when
        SoptLetterInfo.ReportFormResult result = soptLetterFacade.getReportForm();

        // then
        assertThat(result.getReportFormUrl()).isEqualTo(reportFormUrl);
        verify(soptLetterService, times(1)).getReportForm();
    }

    @Test
    @DisplayName("SUCCESS_솝레터 주제 목록 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getTopics() {
        // given
        final Long userId = 1L;
        SoptLetterInfo.TopicSummary topic = SoptLetterInfo.TopicSummary.builder()
                .topicId(3L)
                .title("36기 회고")
                .isDefault(true)
                .createdAt(LocalDateTime.of(2026, 4, 18, 0, 0))
                .build();
        SoptLetterInfo.TopicListResult expected = SoptLetterInfo.TopicListResult.builder()
                .topics(List.of(topic))
                .build();
        when(soptLetterService.getTopics(userId, null)).thenReturn(expected);

        // when
        SoptLetterInfo.TopicListResult result = soptLetterFacade.getTopics(userId, null);

        // then
        assertThat(result.getTopics()).hasSize(1);
        assertThat(result.getTopics().get(0).getTopicId()).isEqualTo(3L);
        assertThat(result.getTopics().get(0).getTitle()).isEqualTo("36기 회고");
        assertThat(result.getTopics().get(0).isDefault()).isTrue();
        verify(soptLetterService, times(1)).getTopics(userId, null);
    }

    @Test
    @DisplayName("SUCCESS_솝레터 CTA 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getCta() {
        // given
        SoptLetterInfo.CtaResult expected = SoptLetterInfo.CtaResult.builder()
            .showCta(true)
            .topicId(3L)
            .ctaText("이번 앱잼 회고하러 가볼까요?")
            .build();
        when(soptLetterService.getCta()).thenReturn(expected);

        // when
        SoptLetterInfo.CtaResult result = soptLetterFacade.getCta();

        // then
        assertThat(result.isShowCta()).isTrue();
        assertThat(result.getTopicId()).isEqualTo(3L);
        assertThat(result.getCtaText()).isEqualTo("이번 앱잼 회고하러 가볼까요?");
        verify(soptLetterService, times(1)).getCta();
    }

    @Test
    @DisplayName("SUCCESS_솝레터 주제 단일 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getTopic() {
        // given
        final Long topicId = 3L;
        SoptLetterInfo.TopicDetail expected = SoptLetterInfo.TopicDetail.builder()
                .topicId(topicId)
                .title("36기 회고")
                .active(true)
                .startedAt(LocalDateTime.of(2026, 4, 18, 0, 0))
                .endedAt(LocalDateTime.of(2026, 4, 28, 23, 59, 59))
                .createdAt(LocalDateTime.of(2026, 4, 18, 0, 0))
                .build();
        when(soptLetterService.getTopic(eq(topicId))).thenReturn(expected);

        // when
        SoptLetterInfo.TopicDetail result = soptLetterFacade.getTopic(topicId);

        // then
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getTitle()).isEqualTo("36기 회고");
        assertThat(result.getActive()).isTrue();
        verify(soptLetterService, times(1)).getTopic(eq(topicId));
    }

    @Test
    @DisplayName("SUCCESS_메시지 작성 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_createSoptLetter() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final String content = "편지 내용";

        SoptLetterInfo.MessageResult expected = SoptLetterInfo.MessageResult.builder()
                .messageId(125L)
                .topicId(topicId)
                .authorNickname("반짝이는 고래")
                .content(content)
                .colorCode("#CCFFEC")
                .rotationDegree(4.0)
                .shapeType("POINT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0)
                .likedByMe(false)
                .mine(true)
                .build();

        when(soptLetterService.createSoptLetter(eq(userId), eq(topicId), eq(content))).thenReturn(expected);

        // when
        SoptLetterInfo.MessageResult result = soptLetterFacade.createSoptLetter(userId, topicId, content);

        // then
        assertThat(result.getMessageId()).isEqualTo(125L);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        verify(soptLetterService, times(1)).createSoptLetter(eq(userId), eq(topicId), eq(content));
    }

    @Test
    @DisplayName("SUCCESS_메시지 수정 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_updateSoptLetter() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;
        final String content = "수정된 편지 내용";

        SoptLetterInfo.MessageResult expected = SoptLetterInfo.MessageResult.builder()
                .messageId(messageId)
                .topicId(topicId)
                .authorNickname("반짝이는 고래")
                .content(content)
                .colorCode("#CCFFEC")
                .rotationDegree(4.0)
                .shapeType("POINT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0)
                .likedByMe(false)
                .mine(true)
                .build();

        when(soptLetterService.updateSoptLetter(eq(userId), eq(topicId), eq(messageId), eq(content))).thenReturn(expected);

        // when
        SoptLetterInfo.MessageResult result = soptLetterFacade.updateSoptLetter(userId, topicId, messageId, content);

        // then
        assertThat(result.getMessageId()).isEqualTo(messageId);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        verify(soptLetterService, times(1)).updateSoptLetter(eq(userId), eq(topicId), eq(messageId), eq(content));
    }

    @Test
    @DisplayName("SUCCESS_메시지 삭제 파사드가 서비스 메서드를 정상 호출한다")
    void SUCCESS_deleteSoptLetter() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;

        // when
        soptLetterFacade.deleteSoptLetter(userId, topicId, messageId);

        // then
        verify(soptLetterService, times(1)).deleteSoptLetter(eq(userId), eq(topicId), eq(messageId));
    }

    @Test
    @DisplayName("SUCCESS_좋아요 추가 파사드가 서비스 메서드를 정상 호출한다")
    void SUCCESS_addLike() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;

        // when
        soptLetterFacade.addLike(userId, topicId, messageId);

        // then
        verify(soptLetterService, times(1)).addLike(eq(userId), eq(topicId), eq(messageId));
    }

    @Test
    @DisplayName("SUCCESS_좋아요 삭제 파사드가 서비스 메서드를 정상 호출한다")
    void SUCCESS_removeLike() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;

        // when
        soptLetterFacade.removeLike(userId, topicId, messageId);

        // then
        verify(soptLetterService, times(1)).removeLike(eq(userId), eq(topicId), eq(messageId));
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
                when(soptLetterService.getTopicMessages(USER_ID, 3L, null, 20)).thenReturn(messageList(3L, 12L, 11L, 10L));

                // when
                soptLetterFacade.getTopicMessages(USER_ID, 3L, null, 20);

                // then 12번까지 읽었다는 이벤트를 한 번 낸다
                ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
                verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
                assertThat(eventCaptor.getValue())
                        .isInstanceOf(SoptLetterTopicReadEvent.class)
                        .extracting("userId", "topicId", "lastReadLetterId")
                        .containsExactly(USER_ID, 3L, 12L);
            }

            @Test
            @DisplayName("SUCCESS_cursor로 다음 페이지를 조회하면 읽음 이벤트를 내지 않는다")
            void noReadEventWithCursor() {
                // given cursor 10으로 그 아래 메시지 9, 8을 조회한다. 스크롤을 내린 것이라 새로 읽은 범위가 아니다
                when(soptLetterService.getTopicMessages(USER_ID, 3L, 10L, 20)).thenReturn(messageList(3L, 9L, 8L));

                // when
                soptLetterFacade.getTopicMessages(USER_ID, 3L, 10L, 20);

                // then 이벤트를 내지 않는다
                verify(eventPublisher, never()).publishEvent(any(SoptLetterTopicReadEvent.class));
            }

            @Test
            @DisplayName("SUCCESS_메시지가 없는 주제를 조회하면 읽음 이벤트를 내지 않는다")
            void noReadEventWhenNoLetters() {
                // given 아직 글이 하나도 없는 주제다
                when(soptLetterService.getTopicMessages(USER_ID, 3L, null, 20)).thenReturn(messageList(3L));

                // when
                soptLetterFacade.getTopicMessages(USER_ID, 3L, null, 20);

                // then 기록할 ID가 없어서 이벤트를 내지 않는다
                verify(eventPublisher, never()).publishEvent(any(SoptLetterTopicReadEvent.class));
            }

            @Test
            @DisplayName("FAIL_솝레터 프로필이 없으면 예외가 나고 읽음 이벤트를 내지 않는다")
            void noReadEventWhenProfileMissing() {
                // given 온보딩을 하지 않아 프로필이 없는 유저다
                when(soptLetterService.getTopicMessages(USER_ID, 3L, null, 20))
                        .thenThrow(new NotFoundException(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND));

                // when & then SOPT_LETTER_PROFILE_NOT_FOUND 예외가 나고 이벤트는 없다
                assertThatThrownBy(() -> soptLetterFacade.getTopicMessages(USER_ID, 3L, null, 20))
                        .isInstanceOf(NotFoundException.class)
                        .satisfies(e -> {
                            NotFoundException exception = (NotFoundException) e;
                            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
                        });
                verify(eventPublisher, never()).publishEvent(any(SoptLetterTopicReadEvent.class));
            }
        }

        @Nested
        @DisplayName("구버전 앱의 기본 주제 조회도 같은 규칙으로 읽음을 기록한다")
        class ReadOnDefaultTopicFirstPage {

            @Test
            @DisplayName("SUCCESS_구버전 기본 주제 첫 페이지를 조회해도 가장 큰 메시지 ID로 읽음 이벤트를 낸다")
            void raisesReadEventForDefaultTopic() {
                // given 기본 주제에 메시지 21, 20이 있고 cursor 없이 조회한다
                when(soptLetterService.getDefaultTopicMessages(USER_ID, null, 20)).thenReturn(messageList(5L, 21L, 20L));

                // when
                soptLetterFacade.getDefaultTopicMessages(USER_ID, null, 20);

                // then 기본 주제를 21번까지 읽었다는 이벤트를 낸다
                ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
                verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
                assertThat(eventCaptor.getValue())
                        .isInstanceOf(SoptLetterTopicReadEvent.class)
                        .extracting("userId", "topicId", "lastReadLetterId")
                        .containsExactly(USER_ID, 5L, 21L);
            }

            @Test
            @DisplayName("SUCCESS_구버전 기본 주제를 cursor로 조회하면 읽음 이벤트를 내지 않는다")
            void noReadEventForDefaultTopicWithCursor() {
                // given cursor 20으로 메시지 19를 조회한다
                when(soptLetterService.getDefaultTopicMessages(USER_ID, 20L, 20)).thenReturn(messageList(5L, 19L));

                // when
                soptLetterFacade.getDefaultTopicMessages(USER_ID, 20L, 20);

                // then 이벤트를 내지 않는다
                verify(eventPublisher, never()).publishEvent(any(SoptLetterTopicReadEvent.class));
            }
        }
    }

    private SoptLetterInfo.TopicMessageListResult messageList(Long topicId, Long... messageIds) {
        List<SoptLetterInfo.TopicMessageSummary> messages = Arrays.stream(messageIds)
                .map(id -> SoptLetterInfo.TopicMessageSummary.builder().messageId(id).build())
                .toList();
        return SoptLetterInfo.TopicMessageListResult.builder()
                .topicId(topicId)
                .messages(messages)
                .build();
    }
}
